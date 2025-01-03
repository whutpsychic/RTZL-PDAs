import 'package:flutter/material.dart';
import './App.dart';

GlobalKey<AppState> appPageKey = GlobalKey();

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: App(key: appPageKey),
    );
  }
}
