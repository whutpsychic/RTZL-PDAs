// ----------------- ip地址修改页 -----------------
// ignore_for_file: file_names, use_build_context_synchronously, must_be_immutable
import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';

class TakingPhoto extends StatefulWidget {
  const TakingPhoto({Key? key}) : super(key: key);

  @override
  State<TakingPhoto> createState() => _TakingPhotoState();
}

class _TakingPhotoState extends State<TakingPhoto> {
  final ImagePicker _picker = ImagePicker();

  void _logic() async {
    try {
      double w = MediaQuery.of(context).size.width;
      double h = MediaQuery.of(context).size.height;
      final XFile? pickedFile = await _picker.pickImage(
        source: ImageSource.camera,
        maxWidth: w,
        maxHeight: h,
        imageQuality: 100,
      );
      String type = _getTypeByName(pickedFile?.name);

      Uint8List? imageBytes = await pickedFile?.readAsBytes();
      if (imageBytes != null) {
        String base64Image = base64Encode(imageBytes);
        Navigator.of(context).pop("data:image/$type;base64,$base64Image");
      } else {
        Navigator.of(context).pop();
      }
    } catch (err) {
      print(err);
    }
  }

  String _getTypeByName(String? name) {
    if (name == null) {
      // default
      return "jpg";
    } else {
      List<String> result = name.split(".");
      final int L = result.length;
      return result[L - 1];
    }
  }

  @override
  void initState() {
    super.initState();
    Future.delayed(const Duration(seconds: 1), () => {_logic()});
  }

  @override
  Widget build(BuildContext context) {
    return Container();
  }

  @override
  void dispose() {
    super.dispose();
  }
}
