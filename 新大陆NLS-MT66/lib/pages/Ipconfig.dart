// ----------------- ip地址修改页 -----------------
// ignore_for_file: file_names, use_build_context_synchronously, must_be_immutable
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../service/main.dart';
import '../appConfig.dart';

class Ipconfig extends StatefulWidget {
  const Ipconfig({Key? key}) : super(key: key);

  @override
  State<Ipconfig> createState() => _IpconfigState();
}

enum HttpType { http, https }

class _IpconfigState extends State<Ipconfig> {
  HttpType _httpType = HttpType.http;

  TextEditingController urlController = TextEditingController();
  TextEditingController portController = TextEditingController();

  String _result = "";

  @override
  void initState() {
    super.initState();
    _loadLocalRecord();
  }

  @override
  void dispose() {
    super.dispose();
  }

  // 查询本地缓存，解析并填入
  void _loadLocalRecord() async {
    String? url = await LocalStorage.getValue("serverUrl");
    // 如果无值
    if (url == null) {
      _execHttp(AppConfig.h5url);
      // 解析值并填入静态初始值
      _parseUrlStr(AppConfig.h5url);
    }
    // 如果有值
    else {
      _execHttp(url);
      // 解析值并填入
      _parseUrlStr(url);
    }
  }

  // 失焦收起键盘并展示计算结果
  void _blur() {
    FocusScope.of(context).requestFocus(FocusNode());
    setState(() {
      _result = _getUrlResult();
    });
  }

  // 计算输入结果
  String _getUrlResult() {
    String result = _httpType == HttpType.http ? "http://" : "https://";

    if (urlController.text != "") {
      result += urlController.text;
    }

    if (portController.text != "") {
      result += ":";
      result += portController.text;
    }

    return result;
  }

  // 复位
  void _resetUrl() {
    // 清空缓存
    LocalStorage.clearValue("serverUrl");

    _execHttp(AppConfig.h5url);
    // 重新加载
    _loadLocalRecord();
  }

  // 恢复至 http 或者 https
  void _execHttp(String url) {
    bool isHttps = url.contains("https://");
    setState(() {
      _httpType = isHttps ? HttpType.https : HttpType.http;
    });
  }

  // 解析值并填入
  void _parseUrlStr(String str) {
    // 分离前部 http:// 或者 https://
    String url = str.replaceFirst("http://", "").replaceFirst("https://", "");
    // 分离端口号
    List<String> urlArr = url.split(":");

    setState(() {
      urlController.text = urlArr[0];
      if (urlArr.length > 1) {
        portController.text = urlArr[1];
      } else {
        portController.text = "";
      }
    });
    // 默认给结果
    _result = str;
  }

  _setNewUrl() {
    // 获取新址
    String url = _getUrlResult();
    LocalStorage.setValue("serverUrl", url);
    Navigator.of(context).pop(url);
  }

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: () => _blur(),
      child: Scaffold(
        resizeToAvoidBottomInset: false,
        appBar: AppBar(title: const Text("配置服务器地址")),
        body: SingleChildScrollView(
          child: Column(
            children: [
              ListTile(
                title: const Text('HTTP'),
                leading: Radio<HttpType>(
                  value: HttpType.http,
                  groupValue: _httpType,
                  onChanged: (HttpType? value) {
                    setState(() {
                      _httpType = value!;
                    });
                    _blur();
                  },
                ),
              ),
              ListTile(
                title: const Text('HTTPS'),
                leading: Radio<HttpType>(
                  value: HttpType.https,
                  groupValue: _httpType,
                  onChanged: (HttpType? value) {
                    setState(() {
                      _httpType = value!;
                    });
                    _blur();
                  },
                ),
              ),
              const Spliter(),
              Inputer(label: "地址", controller: urlController),
              Inputer(
                  label: "端口号(可不填)",
                  controller: portController,
                  isNumber: true),
              Text('您即将访问：$_result'),
              const Spliter(),
              Row(
                mainAxisAlignment: MainAxisAlignment.center,
                crossAxisAlignment: CrossAxisAlignment.center,
                children: [
                  GradientBtn(label: "Go!", fn: _setNewUrl),
                  GradientBtn(label: "复位", fn: _resetUrl),
                ],
              )
            ],
          ),
        ),
      ),
    );
  }
}

class Spliter extends StatelessWidget {
  const Spliter({super.key});

  @override
  Widget build(BuildContext context) {
    return Container(
      width: MediaQuery.of(context).size.width - 40,
      height: 1,
      margin: const EdgeInsets.symmetric(vertical: 40),
      color: const Color.fromRGBO(0, 0, 0, 0.1),
    );
  }
}

class Inputer extends StatelessWidget {
  final String label;
  final TextEditingController controller;
  final bool? isNumber;
  const Inputer(
      {super.key,
      required this.label,
      required this.controller,
      this.isNumber});

  @override
  Widget build(BuildContext context) {
    bool isnumber = (isNumber != null && isNumber!);

    return Container(
      margin: const EdgeInsets.only(left: 40, right: 40, bottom: 40),
      child: TextField(
        controller: controller,
        keyboardType: isnumber ? TextInputType.number : TextInputType.text,
        inputFormatters: isnumber
            ? [
                FilteringTextInputFormatter.allow(RegExp("[0-9]")),
              ]
            : null,
        // obscureText: true,
        decoration: InputDecoration(
          border: const OutlineInputBorder(),
          labelText: label,
        ),
      ),
    );
  }
}

class GradientBtn extends StatelessWidget {
  String label;
  Function fn;
  GradientBtn({super.key, required this.label, required this.fn});

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(bottom: 18, left: 10, right: 10),
      child: ClipRRect(
        borderRadius: BorderRadius.circular(4),
        child: Stack(
          children: <Widget>[
            Positioned.fill(
              child: Container(
                decoration: const BoxDecoration(
                  gradient: LinearGradient(
                    colors: <Color>[
                      Color(0xFF0D47A1),
                      Color(0xFF1976D2),
                      Color(0xFF42A5F5),
                    ],
                  ),
                ),
              ),
            ),
            SizedBox(
              width: 120,
              child: TextButton(
                style: TextButton.styleFrom(
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.all(16.0),
                  textStyle: const TextStyle(fontSize: 20),
                ),
                onPressed: () => fn(),
                child: Text(label),
              ),
            )
          ],
        ),
      ),
    );
  }
}
