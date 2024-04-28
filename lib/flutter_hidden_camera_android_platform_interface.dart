import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'flutter_hidden_camera_android_method_channel.dart';

abstract class FlutterHiddenCameraAndroidPlatform extends PlatformInterface {
  /// Constructs a FlutterHiddenCameraAndroidPlatform.
  FlutterHiddenCameraAndroidPlatform() : super(token: _token);

  static final Object _token = Object();

  static FlutterHiddenCameraAndroidPlatform _instance =
      MethodChannelFlutterHiddenCameraAndroid();

  /// The default instance of [FlutterHiddenCameraAndroidPlatform] to use.
  ///
  /// Defaults to [MethodChannelFlutterHiddenCameraAndroid].
  static FlutterHiddenCameraAndroidPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [FlutterHiddenCameraAndroidPlatform] when
  /// they register themselves.
  static set instance(FlutterHiddenCameraAndroidPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  late final Stream<dynamic> events;

  Future<bool> takePicture({
    String? resolution,
    String? imageFormat,
    int? delay,
  }) {
    throw UnimplementedError('takePicture() has not been implemented.');
  }
}
