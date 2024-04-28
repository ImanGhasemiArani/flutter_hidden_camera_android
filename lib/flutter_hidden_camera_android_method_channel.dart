import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'flutter_hidden_camera_android_platform_interface.dart';

/// An implementation of [FlutterHiddenCameraAndroidPlatform] that uses method channels.
class MethodChannelFlutterHiddenCameraAndroid
    extends FlutterHiddenCameraAndroidPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('flutter_hidden_camera_android');
  final eventChannel =
      const EventChannel('flutter_hidden_camera_android_event');

  @override
  Stream<dynamic> get events => eventChannel.receiveBroadcastStream();

  @override
  Future<bool> takePicture({
    String? resolution,
    String? imageFormat,
    int? delay,
  }) async {
    final bool result =
        await methodChannel.invokeMethod('takePicture', <String, dynamic>{
      'resolution': resolution,
      'imageFormat': imageFormat,
      'delay': delay,
    });
    return result;
  }
}
