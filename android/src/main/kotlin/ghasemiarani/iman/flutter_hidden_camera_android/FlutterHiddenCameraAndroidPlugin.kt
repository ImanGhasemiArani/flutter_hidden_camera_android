package ghasemiarani.iman.flutter_hidden_camera_android

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import java.io.File
import java.lang.ref.WeakReference

/** FlutterHiddenCameraAndroidPlugin */
class FlutterHiddenCameraAndroidPlugin : FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    companion object {

        @SuppressLint("StaticFieldLeak")
        private lateinit var instance: FlutterHiddenCameraAndroidPlugin

        private val methodChannels = mutableMapOf<BinaryMessenger, MethodChannel>()
        private val eventChannels = mutableMapOf<BinaryMessenger, EventChannel>()
        private val eventHandlers = mutableListOf<WeakReference<EventCallbackHandler>>()

        private fun sendEvent(event: Map<String, Any>) {
            eventHandlers.forEach {
                it.get()?.send(event)
            }
        }

        fun sharePluginWithRegister(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) =
            initSharedInstance(
                flutterPluginBinding.applicationContext, flutterPluginBinding.binaryMessenger
            )

        private fun initSharedInstance(context: Context, binaryMessenger: BinaryMessenger) {
            if (!::instance.isInitialized) {
                instance = FlutterHiddenCameraAndroidPlugin()
                instance.context = context
            }

            val channel = MethodChannel(binaryMessenger, "flutter_hidden_camera_android")
            methodChannels[binaryMessenger] = channel
            channel.setMethodCallHandler(instance)

            val events = EventChannel(binaryMessenger, "flutter_hidden_camera_android_event")
            eventChannels[binaryMessenger] = events
            val handler = EventCallbackHandler()
            eventHandlers.add(WeakReference(handler))
            events.setStreamHandler(handler)
        }

        fun onImageCapture(imageFile: File) {
            sendEvent(
                mapOf(
                    "event" to "onImageCaptured",
                    "size" to imageFile.length(),
                    "path" to imageFile.path,
                    "absolutePath" to imageFile.absolutePath,
                )
            )
        }

        fun onEvent(event: String) {
            sendEvent(
                mapOf(
                    "event" to event
                )
            )
        }

        fun onError(error: String) {
            sendEvent(
                mapOf(
                    "error" to error
                )
            )
        }

    }

    private lateinit var context: Context

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) =
        sharePluginWithRegister(flutterPluginBinding)

    override fun onMethodCall(call: MethodCall, result: Result) = when {
        call.method == "takePicture" -> {
            val data = call.arguments as Map<*, *>?
            try {
                context.startService(Intent(context, CameraService::class.java).apply {
                    putExtra("resolution", data?.get("resolution") as String?)
                    putExtra("format", data?.get("imageFormat") as String?)
                    putExtra("delay", data?.get("delay") as Int?)
                })
                result.success(true)
            } catch (e: Exception) {
                result.success(false)
            }
        }

        else -> result.notImplemented()
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        methodChannels.remove(binding.binaryMessenger)?.setMethodCallHandler(null)
        eventChannels.remove(binding.binaryMessenger)?.setStreamHandler(null)
    }

    class EventCallbackHandler : EventChannel.StreamHandler {

        private var eventSink: EventChannel.EventSink? = null

        override fun onListen(arguments: Any?, sink: EventChannel.EventSink) {
            eventSink = sink
        }

        override fun onCancel(arguments: Any?) {
            eventSink = null
        }

        fun send(event: Map<String, Any>) {
            Handler(Looper.getMainLooper()).post { eventSink?.success(event) }
        }
    }

}

