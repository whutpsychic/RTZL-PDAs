import "package:flutter/material.dart";

class Input extends StatefulWidget {
  final bool enabled;
  final String placeholder;
  final TextEditingController controller;

  final double paddingTop;
  final double paddingRight;
  final double paddingBottom;
  final double paddingLeft;

  final double paddingHorizontal;
  final double paddingVertical;

  final double marginTop;
  final double marginRight;
  final double marginBottom;
  final double marginLeft;

  final double marginHorizontal;
  final double marginVertical;

  Input({
    this.enabled,
    this.placeholder,
    this.controller,
    this.paddingTop,
    this.paddingRight,
    this.paddingBottom,
    this.paddingLeft,
    this.paddingHorizontal,
    this.paddingVertical,
    this.marginTop,
    this.marginRight,
    this.marginBottom,
    this.marginLeft,
    this.marginHorizontal,
    this.marginVertical,
  });

  @override
  _InputState createState() {
    return _InputState();
  }
}

class _InputState extends State<Input> {
  void initState() {
    super.initState();
    widget.controller.addListener(() {
      final text = widget.controller.text.toLowerCase();
      widget.controller.value = widget.controller.value.copyWith(text: text);
    });
  }

  @override
  build(BuildContext context) {
    return Padding(
        padding: EdgeInsets.only(
          top: widget.marginTop ?? widget.marginVertical ?? 0,
          right: widget.marginRight ?? widget.marginHorizontal ?? 0,
          bottom: widget.marginBottom ?? widget.marginVertical ?? 0,
          left: widget.marginLeft ?? widget.marginHorizontal ?? 0,
        ),
        child: TextField(
          enabled: widget.enabled,
          controller: widget.controller,
          decoration: InputDecoration(
            contentPadding: EdgeInsets.only(
              top: widget.paddingTop ?? widget.paddingVertical ?? 0,
              right: widget.paddingRight ?? widget.paddingHorizontal ?? 0,
              bottom: widget.paddingBottom ?? widget.paddingVertical ?? 0,
              left: widget.paddingLeft ?? widget.paddingHorizontal ?? 0,
            ),
            hintText: widget.placeholder,
            // errorText: 'Error Text',
            border: OutlineInputBorder(),
          ),
        ));
  }
}
