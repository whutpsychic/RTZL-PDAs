import 'package:flutter/material.dart';

class Button extends StatelessWidget {
  final String text;
  final Function onClick;
  final Size? btnSize;

  // =============== style ===============
  final double? fontSize;
  final Size? size;
  final Color? color;
  // =============== style ===============

  Button(
      {required this.text,
      required this.onClick,
      this.btnSize,
      this.fontSize,
      this.size,
      this.color});

  Color _getColor(Set<MaterialState> states) {
    const Set<MaterialState> interactiveStates = <MaterialState>{
      MaterialState.pressed, // 按下
      MaterialState.hovered, // 放上
      MaterialState.focused, // 聚焦
    };
    if (states.any(interactiveStates.contains)) {
      return Color(0xFF389edc);
    }
    return Colors.blue;
  }

  @override
  Widget build(BuildContext context) {
    return TextButton(
        child: Text(
          text,
          style: TextStyle(
            fontSize: fontSize ?? 18.0, // 文字大小
            color: color ?? Colors.white, // 文字颜色
          ),
        ),
        onPressed: _onPressed,
        style: ButtonStyle(
            //设置按钮的大小
            minimumSize: MaterialStateProperty.all(size ?? Size(100, 50)),
            backgroundColor: MaterialStateProperty.resolveWith(_getColor)));
  }

  _onPressed() {
    if (onClick is Function) onClick();
  }
}
