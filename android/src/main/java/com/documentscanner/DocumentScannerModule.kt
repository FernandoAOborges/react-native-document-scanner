package com.documentscanner

import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReadableMap
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning

class DocumentScannerModule(reactContext: ReactApplicationContext) :
  NativeDocumentScannerSpec(reactContext) {

  override fun scan(options: ReadableMap?, promise: Promise) {
    val activity = reactApplicationContext.currentActivity
    if (activity == null) {
      promise.reject("NO_ACTIVITY", "Current activity is null")
      return
    }

    val allowGallery = options?.getBoolean("allowGallery") ?: true
    val pageLimit =
      if (options != null && options.hasKey("pageLimit")) options.getInt("pageLimit") else 10
    val returnPdf = options?.getBoolean("returnPdf") ?: true
    val returnJpeg = options?.getBoolean("returnJpeg") ?: true

    val builder = GmsDocumentScannerOptions.Builder()
      .setGalleryImportAllowed(allowGallery)
      .setPageLimit(pageLimit)
      .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)

    // ✅ Sem spread operator, compatível com sua API
    when {
      returnJpeg && returnPdf -> builder.setResultFormats(
        GmsDocumentScannerOptions.RESULT_FORMAT_JPEG,
        GmsDocumentScannerOptions.RESULT_FORMAT_PDF
      )
      returnPdf -> builder.setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_PDF)
      else -> builder.setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_JPEG)
    }

    val scannerOptions = builder.build()
    val scanner = GmsDocumentScanning.getClient(scannerOptions)

    scanner.getStartScanIntent(activity)
      .addOnSuccessListener { intentSender ->
        DocumentScannerActivityHandler.start(reactApplicationContext, intentSender, promise)
      }
      .addOnFailureListener { e ->
        promise.reject("SCAN_INTENT_ERROR", e.message, e)
      }
  }

  companion object {
    const val NAME = NativeDocumentScannerSpec.NAME
  }
}
