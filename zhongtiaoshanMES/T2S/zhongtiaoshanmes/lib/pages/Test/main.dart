import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:image_picker/image_picker.dart';

class Tester extends StatefulWidget {
  const Tester({Key? key}) : super(key: key);
  @override
  State<StatefulWidget> createState() => TestPageState();
}

class TestPageState extends State<Tester> {
  final ImagePicker _picker = ImagePicker();
  dynamic _pickImageError;
  XFile? _imageFile = null;
  Uint8List? imgSrc;

  @override
  void initState() {
    super.initState();
  }

  @override
  void dispose() {
    //Dispose broadcast
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        Center(
          child: imgSrc != null ? Image.memory(imgSrc!) : Container(),
        ),
        GestureDetector(
          onTap: _go,
          child: Container(
            margin: const EdgeInsets.only(bottom: 20),
            color: Colors.orange,
            width: 100,
            height: 40,
          ),
        ),
        GestureDetector(
          onTap: _openScope,
          child: Container(
            margin: const EdgeInsets.only(top: 20),
            color: Colors.red,
            width: 100,
            height: 40,
          ),
        )
      ],
    );
  }

  void _picking() async {
    try {
      final XFile? pickedFile = await _picker.pickImage(
        source: ImageSource.gallery,
        maxWidth: 800,
        maxHeight: 2000,
      );
      print("---------after_picking----------");
      if (pickedFile != null) {
        setState(() {
          _imageFile = pickedFile;
        });
        print(pickedFile);
        print(pickedFile.path);
        // _callIO("operate", pickedFile.path);

//

      }
    } catch (e) {
      setState(() {
        _pickImageError = e;
      });
    }
  }

  _go() {
    // 打开相册选择照片并获取其路径
    _picking();
  }

  _openScope() {
    // 打开摄像头
    Navigator.of(context).pushNamed("/cam");
  }
}
