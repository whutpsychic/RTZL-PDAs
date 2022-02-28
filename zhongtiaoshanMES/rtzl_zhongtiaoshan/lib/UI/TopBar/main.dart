import 'package:flutter/material.dart';
import "../../config/Sizes.dart";

final double _topBarHeight = Sizes.topBarHeight;

class TopBar extends StatelessWidget with PreferredSizeWidget {
  final title;

  TopBar({@required this.title});

  @override
  Widget build(BuildContext context) {
    return AppBar(
      // leading: Container(),
      actions: [],
      title: Text('${this.title}'),
    );
  }

  @override
  // 高度调整
  Size get preferredSize => Size.fromHeight(_topBarHeight);
}
