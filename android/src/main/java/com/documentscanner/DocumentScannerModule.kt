package com.documentscanner

import com.facebook.react.bridge.ReactApplicationContext

class DocumentScannerModule(reactContext: ReactApplicationContext) :
  NativeDocumentScannerSpec(reactContext) {

  override fun multiply(a: Double, b: Double): Double {
    return a * b
  }

  companion object {
    const val NAME = NativeDocumentScannerSpec.NAME
  }
}
