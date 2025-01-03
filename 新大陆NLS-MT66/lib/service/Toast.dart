// ignore_for_file: file_names
import 'package:flutter/material.dart';

class Toast {
  static Future show(BuildContext context, String content) async {
    final snackBar = SnackBar(
      content: Text(content),
      action: SnackBarAction(
        label: '知道了',
        onPressed: () {
          // Some code to undo the change.
        },
      ),
    );

    // Find the ScaffoldMessenger in the widget tree
    // and use it to show a SnackBar.
    ScaffoldMessenger.of(context).showSnackBar(snackBar);
  }
}
