// ignore_for_file: file_names
import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';
import './Toast.dart';

class PhoneCall {
  static Future<void> dial(BuildContext context, String number) async {
    final Uri url = Uri.parse("tel:$number");
    if (!await launchUrl(url)) {
      Toast.show(context, "拨号失败!");
    }
  }
}
