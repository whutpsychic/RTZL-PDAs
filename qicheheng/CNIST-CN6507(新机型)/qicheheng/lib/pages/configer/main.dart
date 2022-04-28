import 'package:flutter/material.dart';
import '../../UI/TopBar/main.dart';
import "../../UI/Button/main.dart";
import "./Input.dart";
part "./style.dart";

/// This is the stateful widget that the main application instantiates.
class Configer extends StatefulWidget {
  const Configer({Key? key}) : super(key: key);

  @override
  _ConfigState createState() => _ConfigState();
}

/// This is the private State class that goes with MyStatefulWidget.
class _ConfigState extends State<Configer> {
  TextEditingController controller = TextEditingController();

  @override
  void initState() {
    super.initState();
  }

  Widget build(BuildContext context) {
    return Scaffold(
        resizeToAvoidBottomInset: true,
        appBar: TopBar(title: '设置size'),
        body: FutureBuilder<bool>(
            builder: (BuildContext context, AsyncSnapshot<bool> snapshot) {
          return GestureDetector(
              behavior: HitTestBehavior.translucent,
              onTap: () {
                FocusScope.of(context).requestFocus(FocusNode());
              },
              child: SingleChildScrollView(
                // reverse: true,
                child: Column(
                  children: <Widget>[
                    Input(
                      placeholder:
                          "BitmapUtils.encode2dAsBitmap (text, size, size, 2);",
                      controller: controller,
                      marginHorizontal: 20,
                      marginBottom: 20,
                      paddingHorizontal: 10,
                    ),
                    Button(
                      text: "Go!",
                      // onTap: () => print(
                      // "${_urlController.text}:${_portController.text}\n${_uriController.text}")
                      onClick: () {
                        Navigator.of(context)
                            .pushNamed('/main', arguments: controller.text);
                      },
                    )
                  ],
                ),
              ));
        }));
  }
}
