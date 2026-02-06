package com.documentscanner

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.facebook.react.bridge.*
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult;
import java.io.File
import java.io.FileOutputStream

object DocumentScannerActivityHandler : ActivityEventListener {

  private const val REQ_CODE = 9911

  private var pendingPromise: Promise? = null
  private var ctx: ReactApplicationContext? = null

  fun start(
    context: ReactApplicationContext,
    intentSender: android.content.IntentSender,
    promise: Promise
  ) {
    val activity = context.currentActivity
    if (activity == null) {
      promise.reject("NO_ACTIVITY", "Current activity is null")
      return
    }

    if (ctx == null) {
      ctx = context
      context.addActivityEventListener(this)
    }

    if (pendingPromise != null) {
      promise.reject("SCAN_IN_PROGRESS", "Another scan is already in progress")
      return
    }

    pendingPromise = promise

    try {
      activity.startIntentSenderForResult(intentSender, REQ_CODE, null, 0, 0, 0)
    } catch (e: Exception) {
      pendingPromise = null
      promise.reject("START_SCAN_ERROR", e.message, e)
    }
  }

  // ✅ RN 0.80 signature (activity não-null)
  override fun onActivityResult(
    activity: Activity,
    requestCode: Int,
    resultCode: Int,
    data: Intent?
  ) {
    if (requestCode != REQ_CODE) return

    val promise = pendingPromise
    pendingPromise = null
    if (promise == null) return

    if (resultCode != Activity.RESULT_OK || data == null) {
      val out = Arguments.createMap()
      out.putBoolean("canceled", true)
      out.putArray("images", Arguments.createArray())
      promise.resolve(out)
      return
    }

    try {
      val result = GmsDocumentScanningResult.fromActivityResultIntent(data)

      if (result == null) {
        val out = Arguments.createMap()
        out.putBoolean("canceled", true)
        out.putArray("images", Arguments.createArray())
        promise.resolve(out)
        return
      }

      val out = Arguments.createMap()
      out.putBoolean("canceled", false)

      val images = Arguments.createArray()
      result.pages?.forEach { page ->
        val local = copyToCache(page.imageUri)
        images.pushString(local.toString())
      }
      out.putArray("images", images)

      result.pdf?.let { pdf ->
        val localPdf = copyToCache(pdf.uri)
        out.putString("pdf", localPdf.toString())
      }

      promise.resolve(out)
    } catch (e: Exception) {
      promise.reject("SCAN_RESULT_ERROR", e.message, e)
    }
  }

  // ✅ RN 0.80 signature (intent não-null)
  override fun onNewIntent(intent: Intent) = Unit

  private fun copyToCache(uri: Uri): Uri {
    val context = ctx ?: throw IllegalStateException("React context is null")
    val resolver = context.contentResolver

    val ext = guessExt(uri)
    val file = File(context.cacheDir, "rn_docscan_${System.currentTimeMillis()}.$ext")

    resolver.openInputStream(uri).use { input ->
      if (input == null) throw IllegalStateException("Cannot open input stream for $uri")
      FileOutputStream(file).use { output ->
        input.copyTo(output)
      }
    }

    return Uri.fromFile(file)
  }

  private fun guessExt(uri: Uri): String {
    val s = uri.toString().lowercase()
    return if (s.contains(".pdf")) "pdf" else "jpg"
  }
}
