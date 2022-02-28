import "package:flutter/material.dart";

class Dimensions {
  static double getScreenWidth(context) {
    return MediaQuery.of(context).size.width;
  }

  static double getScreenHeight(context) {
    return MediaQuery.of(context).size.height;
  }
}
