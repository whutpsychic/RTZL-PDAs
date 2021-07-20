//========================================
// 本模块为 flutter 与 h5 相互通信的对接模块
// 开发者：zbc
// 创建日期：2021-06-07
// 上次修改日期：2021-07-20
// ========================================

const fc = {};

// flutter主动向js发送信息，js须负责以此初始化监听注册
// 其原理是flutter会向window续写方法，所有调用皆来自window对象
fc.register = (name, callback) => {
	if (typeof name != "string")
		throw new Error("fc.register函数传入的name不是一个字符串");
	if (typeof callback != "function")
		throw new Error("fc.register函数传入的callback不是一个函数方法");

	window[name] = callback;
};

// 建议在componentUnmonut当中将之前注册的函数注销掉，以免一起不必要的bug
fc.unregister = (name) => {
	if (typeof name != "string")
		throw new Error("fc.unregister函数传入的name不是一个字符串");
	window[name] = undefined;
};

// 预留的向 flutter 端发送一般信息的默认函数
fc.traceback = (info) => {
	if (typeof info != "string")
		throw new Error("您传给flutter端的信息不是一个字符串，将无法识别！");
	if (!window.traceback) {
		alert(
			"您所在的位置于flutter端没有注册traceback方法，请先行注册方可使用，或者联系zbc！"
		);
		console.warn(
			"如果您在非flutter端调试，那么traceback方法将不会产生任何作用，且不会影响您继续调试其他功能。"
		);
		return;
	}

	window.traceback.postMessage(info);
};

fc.printBarcode = (str) => {
	if (typeof str != "string")
		throw new Error("您向打印条码函数传送的参数不是一个字符串!");
	if (!window.PDAPrintBarcode) {
		alert(
			"您所在的位置于flutter端没有注册打印条码的方法，请先注册再使用，或者联系zbc！"
		);
		return;
	}

	window.PDAPrintBarcode.postMessage(info);
};

fc.printQRcode = (str) => {
	if (typeof str != "string")
		throw new Error("您向打印二维函数传送的参数不是一个字符串!");
	if (!window.PDAPrintBarcode) {
		alert(
			"您所在的位置于flutter端没有注册打印条码的方法，请先注册再使用，或者联系zbc！"
		);
		return;
	}

	window.PDAPrintQRcode.postMessage(info);
};

export default fc;
