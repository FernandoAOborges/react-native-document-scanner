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

    val allowGallery = if (options != null && options.hasKey("allowGallery")) options.getBoolean("allowGallery") else true
    // null when omitted — ML Kit is not given a page limit, so scanning is unlimited.
    val pageLimit: Int? =
      if (options != null && options.hasKey("pageLimit")) options.getInt("pageLimit") else null
    val returnJpeg = if (options != null && options.hasKey("returnJpeg")) options.getBoolean("returnJpeg") else true
    val returnPdf  = if (options != null && options.hasKey("returnPdf"))  options.getBoolean("returnPdf")  else false

    val builder = GmsDocumentScannerOptions.Builder()
      .setGalleryImportAllowed(allowGallery)
      .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)

    // Only constrain page count when the caller explicitly requested a limit.
    if (pageLimit != null) {
      builder.setPageLimit(pageLimit)
    }

    when {
      returnJpeg && returnPdf -> builder.setResultFormats(
        GmsDocumentScannerOptions.RESULT_FORMAT_JPEG,
        GmsDocumentScannerOptions.RESULT_FORMAT_PDF
      )
      returnPdf -> builder.setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_PDF)
      else      -> builder.setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_JPEG)
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
