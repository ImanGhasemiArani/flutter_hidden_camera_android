package ghasemiarani.iman.flutter_hidden_camera_android

import android.content.Intent
import android.os.Handler
import android.os.IBinder
import ghasemiarani.iman.flutter_hidden_camera_android.androidhiddencamera.CameraConfig
import ghasemiarani.iman.flutter_hidden_camera_android.androidhiddencamera.CameraError
import ghasemiarani.iman.flutter_hidden_camera_android.androidhiddencamera.HiddenCameraService
import ghasemiarani.iman.flutter_hidden_camera_android.androidhiddencamera.HiddenCameraUtils
import ghasemiarani.iman.flutter_hidden_camera_android.androidhiddencamera.config.CameraFacing
import ghasemiarani.iman.flutter_hidden_camera_android.androidhiddencamera.config.CameraFocus
import ghasemiarani.iman.flutter_hidden_camera_android.androidhiddencamera.config.CameraImageFormat
import ghasemiarani.iman.flutter_hidden_camera_android.androidhiddencamera.config.CameraResolution
import ghasemiarani.iman.flutter_hidden_camera_android.androidhiddencamera.config.CameraRotation
import io.flutter.Log
import java.io.File

class CameraService : HiddenCameraService() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val data = intent?.extras
        val resolution = data?.getString("resolution") ?: "LOW_RESOLUTION"
        val format = data?.getString("format") ?: "FORMAT_JPEG"
        val delay = data?.getInt("delay") ?: 2000

        Log.d("CameraService", "onStartCommand: $resolution $format $delay")

        try {
//            if (HiddenCameraUtils.canOverDrawOtherApps(this)) {
            val cameraConfig =
                CameraConfig().getBuilder(this).setCameraFacing(CameraFacing.REAR_FACING_CAMERA)
                    .setImageRotation(CameraRotation.ROTATION_90)
                    .setCameraFocus(CameraFocus.AUTO).setCameraResolution(
                        when (resolution) {
                            "LOW_RESOLUTION" -> CameraResolution.LOW_RESOLUTION
                            "MEDIUM_RESOLUTION" -> CameraResolution.MEDIUM_RESOLUTION
                            "HIGH_RESOLUTION" -> CameraResolution.HIGH_RESOLUTION
                            else -> CameraResolution.LOW_RESOLUTION
                        }
                    ).setImageFormat(
                        when (format) {
                            "FORMAT_JPEG" -> CameraImageFormat.FORMAT_JPEG
                            "FORMAT_PNG" -> CameraImageFormat.FORMAT_PNG
                            "FORMAT_WEBP" -> CameraImageFormat.FORMAT_WEBP
                            else -> CameraImageFormat.FORMAT_JPEG
                        }
                    ).build()

            startCamera(cameraConfig)

            Handler(this.mainLooper).postDelayed(
                {
                    FlutterHiddenCameraAndroidPlugin.onEvent("Capturing image.")
                    takePicture()
                }, delay.toLong()
            )
//            } else {
//                HiddenCameraUtils.openDrawOverPermissionSetting(this)
//                FlutterHiddenCameraAndroidPlugin.onError(
//                    "Draw over other app permission not available. Please grant the permission first"
//                )
//            }
        } catch (e: Exception) {
            FlutterHiddenCameraAndroidPlugin.onError(e.message ?: "Unknown error")
        }
        return START_NOT_STICKY
    }

    override fun onImageCapture(imageFile: File) {
        FlutterHiddenCameraAndroidPlugin.onImageCapture(imageFile)
        stopSelf()
    }

    override fun onCameraError(errorCode: Int) {
        when (errorCode) {
            CameraError.ERROR_CAMERA_OPEN_FAILED -> FlutterHiddenCameraAndroidPlugin.onError(
                "ERROR_CAMERA_OPEN_FAILED: $errorCode\nCamera open failed. Probably because another application is using the camera"
            )

            CameraError.ERROR_IMAGE_WRITE_FAILED -> FlutterHiddenCameraAndroidPlugin.onError(
                "ERROR_IMAGE_WRITE_FAILED: $errorCode\nImage write failed. Please check if you have provided WRITE_EXTERNAL_STORAGE permission"
            )

            CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE -> FlutterHiddenCameraAndroidPlugin.onError(
                "ERROR_CAMERA_PERMISSION_NOT_AVAILABLE: $errorCode\nCamera permission not available. Please grant camera permission first"
            )

            CameraError.ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION -> FlutterHiddenCameraAndroidPlugin.onError(
                "ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION: $errorCode\nDraw over other app permission not available. Please grant the permission first"
            )
//            HiddenCameraUtils.openDrawOverPermissionSetting(this)

            CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA -> FlutterHiddenCameraAndroidPlugin.onError(
                "ERROR_DOES_NOT_HAVE_FRONT_CAMERA: $errorCode\nFront camera not available"
            )
        }

        stopSelf()
    }
}
