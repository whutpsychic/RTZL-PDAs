import 'package:flutter/material.dart';
import 'package:shared_preferences/shared_preferences.dart';
import '../../UI/TopBar/main.dart';
import "../../UI/Button/main.dart";
import "./Input.dart";
import "../../config/Arguments.dart";
part "./style.dart";

enum HttpCharacter { http, https }
enum SingingCharacter { url_port, uri }

/// This is the stateful widget that the main application instantiates.
class IpConfig extends StatefulWidget {
  IpConfig({Key? key}) : super(key: key);

  @override
  _IpConfigState createState() => _IpConfigState();
}

/// This is the private State class that goes with MyStatefulWidget.
class _IpConfigState extends State<IpConfig> {
  Future<SharedPreferences> _prefs = SharedPreferences.getInstance();

  HttpCharacter? _httpcharacter;
  SingingCharacter? _character;

  TextEditingController _urlController = TextEditingController();
  TextEditingController _portController = TextEditingController();
  TextEditingController _uriController = TextEditingController();

  Future<void> _setUrlPort(String urlString, String portString) async {
    final SharedPreferences prefs = await _prefs;
    setState(() {
      prefs.setString("url", urlString);
      prefs.setString("port", portString);
      prefs.setString("request_type", _character.toString());
    });
  }

  Future<void> _setUri(String uriString) async {
    final SharedPreferences prefs = await _prefs;
    setState(() {
      prefs.setString("uri", uriString);
      prefs.setString("request_type", _character.toString());
    });
  }

  Future<void> _setHttpType() async {
    final SharedPreferences prefs = await _prefs;
    setState(() {
      prefs.setString("http_type", _httpcharacter.toString().split(".")[1]);
    });
  }

  @override
  void initState() {
    super.initState();

// 检测如果本地缓存已有数据就跳转至主页那边去

    _prefs.then((SharedPreferences prefs) {
      String? urlResult = prefs.getString('url');
      String? portResult = prefs.getString('port');
      String? uriResult = prefs.getString('uri');

      // if (urlResult != null || portResult != null || uriResult != null) {
      //   Navigator.of(context).pushNamed("/main");
      // }

      urlResult = (urlResult ?? Arguments.defaultProxyUrl);
      portResult = (portResult ?? Arguments.defaultProxyPort);
      uriResult = (uriResult ?? "");

      _urlController.text = urlResult;
      _portController.text = portResult;
      _uriController.text = uriResult;

      String wt = (prefs.getString('request_type') ?? "");
      String wht = (prefs.getString('http_type') ?? "");

      _httpcharacter = (wht == HttpCharacter.https.toString())
          ? HttpCharacter.https
          : HttpCharacter.http;

      if (wht == "") {
        _httpcharacter = (Arguments.defaultHttpType == "http")
            ? HttpCharacter.http
            : HttpCharacter.https;
      }

      _character = (wt == SingingCharacter.uri.toString())
          ? SingingCharacter.uri
          : SingingCharacter.url_port;
    });
  }

  Widget build(BuildContext context) {
    final Future<bool> str = _prefs.then((SharedPreferences prefs) {
      return true;
    });

    return Scaffold(
        resizeToAvoidBottomInset: true,
        appBar: TopBar(title: '设置服务地址'),
        body: FutureBuilder<bool>(
            future: str,
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
                        ListTile(
                          title: const Text('http'),
                          leading: Radio(
                            value: HttpCharacter.http,
                            groupValue: _httpcharacter,
                            onChanged: (HttpCharacter? value) {
                              setState(() {
                                _httpcharacter = value;
                              });
                            },
                          ),
                        ),
                        ListTile(
                          title: const Text('https'),
                          leading: Radio(
                            value: HttpCharacter.https,
                            groupValue: _httpcharacter,
                            onChanged: (HttpCharacter? value) {
                              setState(() {
                                _httpcharacter = value;
                              });
                            },
                          ),
                        ),
                        ListTile(
                          title: const Text('url & port'),
                          leading: Radio(
                            value: SingingCharacter.url_port,
                            groupValue: _character,
                            onChanged: (SingingCharacter? value) {
                              setState(() {
                                _character = value;
                              });
                            },
                          ),
                        ),
                        Input(
                          enabled: _character == SingingCharacter.url_port,
                          placeholder: "请输入服务器地址",
                          controller: _urlController,
                          marginHorizontal: 20,
                          marginBottom: 20,
                          paddingHorizontal: 10,
                        ),
                        Input(
                          enabled: _character == SingingCharacter.url_port,
                          placeholder: "请输入服务器端口",
                          controller: _portController,
                          marginHorizontal: 20,
                          marginBottom: 20,
                          paddingHorizontal: 10,
                        ),
                        ListTile(
                          title: const Text('uri'),
                          leading: Radio(
                            value: SingingCharacter.uri,
                            groupValue: _character,
                            onChanged: (SingingCharacter? value) {
                              setState(() {
                                _character = value;
                              });
                            },
                          ),
                        ),
                        Input(
                          enabled: _character == SingingCharacter.uri,
                          placeholder: "请输入网址",
                          controller: _uriController,
                          marginHorizontal: 20,
                          marginBottom: 20,
                          paddingHorizontal: 10,
                        ),
                        Button(
                          text: "Go!",
                          // onTap: () => print(
                          // "${_urlController.text}:${_portController.text}\n${_uriController.text}")
                          onClick: () {
                            final httpType =
                                _httpcharacter == HttpCharacter.http
                                    ? "http"
                                    : "https";
                            _setHttpType();
                            if (_character == SingingCharacter.url_port) {
                              Navigator.of(context).pushNamed("/main",
                                  arguments: {
                                    "url":
                                        "$httpType://${_urlController.text}:${_portController.text}"
                                  });
                              // 保存数据到本地
                              _setUrlPort(
                                  _urlController.text, _portController.text);
                            } else if (_character == SingingCharacter.uri) {
                              Navigator.of(context)
                                  .pushNamed("/main", arguments: {
                                "url": "$httpType://${_uriController.text}",
                              });
                              // 保存数据到本地
                              _setUri(_uriController.text);
                            }
                          },
                        )
                      ],
                    ),
                  ));
            }));
  }
}
