package com.documentscanner

import android.app.Activity
import android.content.Intent
import android.net.Uri
import com.facebook.react.bridge.*
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object DocumentScannerActivityHandler : ActivityEventListener {

  private const val REQ_CODE = 9911

  private var pendingPromise: Promise? = null
  private var ctx: ReactApplicationContext? = null

  // Stable, long-lived scope tied to the singleton.
  // SupervisorJob ensures that a failure inside one file-copy coroutine does
  // not cancel the scope itself, so subsequent scans are never affected.
  // Dispatchers.IO is the default dispatcher: an elastic thread pool sized
  // for blocking I/O (up to 64 threads or the number of CPU cores, whichever
  // is larger).
  private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

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

    // Cancellation path: no file I/O, resolve immediately on the main thread.
    if (resultCode != Activity.RESULT_OK || data == null) {
      val out = Arguments.createMap()
      out.putBoolean("canceled", true)
      out.putArray("images", Arguments.createArray())
      promise.resolve(out)
      return
    }

    // Parse the ML Kit result object — lightweight Intent-extra read, no I/O.
    val result = try {
      GmsDocumentScanningResult.fromActivityResultIntent(data)
    } catch (e: Exception) {
      promise.reject("SCAN_RESULT_ERROR", e.message, e)
      return
    }

    if (result == null) {
      val out = Arguments.createMap()
      out.putBoolean("canceled", true)
      out.putArray("images", Arguments.createArray())
      promise.resolve(out)
      return
    }

    // Dispatch all file-copy work to the stable IO scope.
    // After the blocking work finishes, withContext(Dispatchers.Main) returns
    // to the main thread before touching the Promise — consistent with how
    // Android expects UI-adjacent state to be resolved.
    ioScope.launch {
      try {
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

        withContext(Dispatchers.Main) {
          promise.resolve(out)
        }
      } catch (e: Exception) {
        withContext(Dispatchers.Main) {
          promise.reject("SCAN_RESULT_ERROR", e.message, e)
        }
      }
    }
  }

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
