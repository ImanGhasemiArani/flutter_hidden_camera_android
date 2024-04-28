import 'dart:io';

import 'flutter_hidden_camera_android_platform_interface.dart';

class FlutterHiddenCameraAndroid {
  FlutterHiddenCameraAndroid._();

  static final events =
      FlutterHiddenCameraAndroidPlatform.instance.events.map(_parseEvent);

  static Future<bool> takePicture({
    Resolution resolution = Resolution.low,
    ImageFormat imageFormat = ImageFormat.jpeg,
    int delay = 0,
  }) {
    return FlutterHiddenCameraAndroidPlatform.instance.takePicture(
      resolution: resolution.value,
      imageFormat: imageFormat.value,
      delay: delay,
    );
  }
}

enum Resolution {
  low('LOW_RESOLUTION'),
  medium('MEDIUM_RESOLUTION'),
  high('HIGH_RESOLUTION');

  final String value;

  const Resolution(this.value);
}

enum ImageFormat {
  jpeg('FORMAT_JPEG'),
  png('FORMAT_PNG');

  final String value;

  const ImageFormat(this.value);
}

sealed class Event {
  final String message;

  Event(this.message);
}

class ImageEvent extends Event {
  final File file;

  ImageEvent(super.message, this.file);
}

class ErrorEvent extends Event {
  ErrorEvent(super.message);
}

class CustomEvent extends Event {
  final dynamic data;

  CustomEvent(super.message, this.data);
}

Event _parseEvent(dynamic event) {
  if (event is Map) {
    final map = event.map((key, value) {
      if (key is String) {
        return MapEntry(key, value);
      }
      return MapEntry(key.toString(), value);
    });

    if (map['event'] == 'onImageCaptured') {
      return ImageEvent(map['event'], File(map['path']));
    } else if (map.containsKey('error')) {
      return ErrorEvent(map['error']);
    }
  }
  return CustomEvent('Unknown event', event);
}
