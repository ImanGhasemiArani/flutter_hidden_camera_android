import 'dart:io';

import 'package:flutter/material.dart';

import 'package:flutter_hidden_camera_android/flutter_hidden_camera_android.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String? path;

  @override
  void initState() {
    super.initState();
    FlutterHiddenCameraAndroid.events.listen((event) {
      if (event is ImageEvent) {
        setState(() {
          path = event.file.path;
        });
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: [
              if (path != null)
                Image.file(File(path!), width: 100, height: 100),
              ElevatedButton(
                onPressed: () async {
                  FlutterHiddenCameraAndroid.takePicture(
                    resolution: Resolution.low,
                    imageFormat: ImageFormat.jpeg,
                    delay: 0,
                  ).then((value) => print(value));
                },
                child: const Text('Take Picture'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
