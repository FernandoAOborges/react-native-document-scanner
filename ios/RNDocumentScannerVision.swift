import Foundation
import UIKit
import VisionKit
import React

@objc(RNDocumentScannerVision)
final class RNDocumentScannerVision: NSObject, VNDocumentCameraViewControllerDelegate {

  @objc static let shared = RNDocumentScannerVision()

  private var resolve: RCTPromiseResolveBlock?
  private var reject: RCTPromiseRejectBlock?
  private var options: [String: Any] = [:]
  private var isPresenting = false

  @objc
  func scan(_ options: NSDictionary?,
            resolve: @escaping RCTPromiseResolveBlock,
            reject: @escaping RCTPromiseRejectBlock) {

    DispatchQueue.main.async {
      if self.isPresenting {
        reject("E_BUSY", "A scan is already in progress.", nil)
        return
      }

      guard VNDocumentCameraViewController.isSupported else {
        reject("E_NOT_SUPPORTED", "VNDocumentCameraViewController is not supported on this device.", nil)
        return
      }

      self.isPresenting = true
      self.resolve = resolve
      self.reject = reject
      self.options = options as? [String: Any] ?? [:]

      let vc = VNDocumentCameraViewController()
      vc.delegate = self

      guard let top = RCTPresentedViewController() else {
        self.finishReject("E_NO_VIEW_CONTROLLER", "Could not find a view controller to present the scanner.", nil)
        return
      }

      top.present(vc, animated: true)
    }
  }

  // MARK: - VNDocumentCameraViewControllerDelegate

  func documentCameraViewControllerDidCancel(_ controller: VNDocumentCameraViewController) {
    controller.dismiss(animated: true) {
      self.finishResolve(canceled: true, images: [], pdf: nil)
    }
  }

  func documentCameraViewController(_ controller: VNDocumentCameraViewController, didFailWithError error: Error) {
    controller.dismiss(animated: true) {
      self.finishReject("E_SCAN_FAILED", error.localizedDescription, error)
    }
  }

  func documentCameraViewController(_ controller: VNDocumentCameraViewController, didFinishWith scan: VNDocumentCameraScan) {
    controller.dismiss(animated: true) {

      let pageLimit = (self.options["pageLimit"] as? NSNumber)?.intValue
      let returnPdf = (self.options["returnPdf"] as? Bool) ?? false
      let returnJpeg = (self.options["returnJpeg"] as? Bool) ?? true

      let total = scan.pageCount
      let limit = pageLimit != nil ? max(1, min(pageLimit!, total)) : total

      var images: [String] = []
      if returnJpeg {
        for i in 0..<limit {
          let img = scan.imageOfPage(at: i)
          if let path = self.writeJpeg(img, index: i) {
            images.append(path) // ✅ path puro
          }
        }
      }

      let pdfPath: String? = returnPdf ? self.writePdf(scan: scan, limit: limit) : nil
      self.finishResolve(canceled: false, images: images, pdf: pdfPath)
    }
  }

  // MARK: - Finish

  private func finishResolve(canceled: Bool, images: [String], pdf: String?) {
    let payload: [String: Any] = [
      "canceled": canceled,
      "images": images,
      "pdf": pdf as Any
    ]
    let res = self.resolve
    self.cleanup()
    res?(payload)
  }

  private func finishReject(_ code: String, _ message: String, _ error: Error?) {
    let rej = self.reject
    self.cleanup()
    rej?(code, message, error)
  }

  private func cleanup() {
    self.isPresenting = false
    self.resolve = nil
    self.reject = nil
    self.options = [:]
  }

  // MARK: - Files

  private func cacheDir() -> URL {
    let base = FileManager.default.urls(for: .cachesDirectory, in: .userDomainMask).first!
    let dir = base.appendingPathComponent("document-scanner", isDirectory: true)
    if !FileManager.default.fileExists(atPath: dir.path) {
      try? FileManager.default.createDirectory(at: dir, withIntermediateDirectories: true)
    }
    return dir
  }

  private func writeJpeg(_ image: UIImage, index: Int) -> String? {
    guard let data = image.jpegData(compressionQuality: 0.9) else { return nil }
    let url = cacheDir().appendingPathComponent("scan-\(UUID().uuidString)-\(index).jpg")
    do {
      try data.write(to: url, options: .atomic)
      return url.absoluteString
    } catch {
      return nil
    }
  }

  private func writePdf(scan: VNDocumentCameraScan, limit: Int) -> String? {
    let bounds = CGRect(x: 0, y: 0, width: 612, height: 792)
    let url = cacheDir().appendingPathComponent("scan-\(UUID().uuidString).pdf")

    UIGraphicsBeginPDFContextToFile(url.path, bounds, nil)
    defer { UIGraphicsEndPDFContext() }

    for i in 0..<limit {
      let img = scan.imageOfPage(at: i)
      UIGraphicsBeginPDFPageWithInfo(bounds, nil)

      let size = img.size
      let scale = min(bounds.width / size.width, bounds.height / size.height)
      let w = size.width * scale
      let h = size.height * scale
      let x = (bounds.width - w) / 2
      let y = (bounds.height - h) / 2

      img.draw(in: CGRect(x: x, y: y, width: w, height: h))
    }

    return url.absoluteString
  }
}
