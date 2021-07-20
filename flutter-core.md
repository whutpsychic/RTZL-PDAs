### flutter-core.js 是一个多项目通用模块，本文档重点讲解使用方法。
----------


 * 1.监听扫码事件：
```
componentDidMount(){
	// barcode is String
	fc.register("onScan",(barcode){...});
}
```

 * 2.打印条码事件：
```
fc.printBarcode(code);
```


 * 3.打印二维码事件：
```
fc.printQRcode(code);
```
