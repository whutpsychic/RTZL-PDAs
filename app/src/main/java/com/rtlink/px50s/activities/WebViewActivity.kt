package com.rtlink.px50s.activities

import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.device.ScanManager
import android.device.scanner.configuration.PropertyID
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.rtlink.px50s.GlobalConfig
import com.rtlink.px50s.GlobalConfig.Companion.OFFLINE_MODE
import com.rtlink.px50s.GlobalConfig.Companion.RAM_NAME
import com.rtlink.px50s.GlobalConfig.Companion.WEB_URL
import com.rtlink.px50s.R
import com.rtlink.px50s.utils.LocalStorage
import com.rtlink.px50s.utils.RequirePermission
import com.rtlink.px50s.utils.makeToast
import com.rtlink.px50s.webIO.CallbackKeys.Companion.SCAN
import com.rtlink.px50s.webIO.CallbackKeys.Companion.TAKE_PHOTO
import com.rtlink.px50s.webIO.Index
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

// *********************** 关于<input type="file" /> ***********************
// - 仅抬起相机ok
// - 相机或选择文件ok
// - 相机或选择文件（多选）ok
// - 解决在多选文件时进行了快捷选择（单选）后闪退的问题ok
// - 解决在多选文件模式下拍照后无法正常传输的问题ok
// - 所有类型的文件可上传ok
// - 解决打开文件选择器但不做选择退出后闪退的问题ok
// - 解决打开文件选择器/抬起相机但不做操作，退出后不能再次选择文件或拍照的问题ok
// ************************************************************************
// ************************** 加载本地html(离线运行) *************************
// - createWebHashHistory + base: './'
// ************************************************************************
class WebViewActivity : ComponentActivity() {

    companion object {
        // 单文件选择模式码
        const val SINGLE_FILE_CHOOSER_CODE = 0

        // 多文件选择模式码
        const val MULTI_FILE_CHOOSER_CODE = 1

        // 通知 ChannelId
        const val CHANNEL_ID = "rtlink_nc"
        const val CHANNEL_NAME = "rtlink_notification_channel"
    }

    // ipconfig启动器
    lateinit var ipConfigLauncher: ActivityResultLauncher<Intent>

    // 扫码启动器
    lateinit var scanResultLauncher: ActivityResultLauncher<Intent>

    // 选择文件启动器
    private lateinit var fileChooseLauncher: ActivityResultLauncher<Intent>

    // 拍照启动器(选择文件用)
    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>

    // 拍照启动器(选择文件用)
    private lateinit var photoLauncher: ActivityResultLauncher<Intent>

    // webView 实例
    private var webView: WebView? = null
    private var tvLoading: TextView? = null;

    // finish函数触发次数优化（使之仅触发一次）
    private var finishedAlready: Boolean = false

    // 临时存放文件选取回调函数
    private var fileUploadCallback: ValueCallback<Array<Uri>>? = null

    // 文件上传模式（默认单选）
    // 0 = 单选 1 = 多选
    private var currentFileChooserMode: Int = SINGLE_FILE_CHOOSER_CODE

    // 选择的上传文件（单选或相机拍摄）
    private lateinit var currentPhotoUri: Uri

    private lateinit var strs: Array<String>
    public var mAction: String = ScanManager.ACTION_DECODE //"android.provider.sdlMessage";
    public var mKey: String = ScanManager.BARCODE_STRING_TAG
    public val mScanManager: ScanManager = ScanManager()

    @OptIn(ExperimentalEncodingApi::class)
    @SuppressLint("SetJavaScriptEnabled", "InlinedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 沉浸式渲染
        enableEdgeToEdge()
        // 显示注册的页面
        setContentView(R.layout.activity_webview)

        // 创建通知通道
        createNotificationChannel()

        mScanManager.switchOutputMode(0) // 0：开启广播模式   1：键盘模式

        // 向用户索要写入权限
        RequirePermission(this, permission.WRITE_EXTERNAL_STORAGE)

        // 绑定webView实例
        webView = findViewById<WebView>(R.id.webView)
        tvLoading = findViewById(R.id.tv_loading);

        // 注意要启用JS，默认是不启用的，否则将导致某些页面无法显示
        webView?.settings?.javaScriptEnabled = true
        // 缓存模式
        webView?.settings?.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK;
        // 启用DOM存储
        webView?.settings?.domStorageEnabled = true
        // 启用数据库存储
        webView?.settings?.databaseEnabled = true

        // ******** 允许访问本地文件系统（加载本地.html/.css/.js等文件） ********
        webView?.settings?.allowFileAccess = true
        webView?.settings?.allowFileAccessFromFileURLs = true
        // **************************************************************
        // 启用此行代码可显示原生web端的一些功能比如：显示alert()
        webView?.webChromeClient = object : WebChromeClient() {

            // 补丁 - <input type="file" />
            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                // 备份回调函数
                fileUploadCallback = filePathCallback
                // 如果是选择图片
                if (fileChooserParams?.acceptTypes?.contains("image/*") == true) {
                    // 如果是仅使用相机
                    if (fileChooserParams.isCaptureEnabled) {
                        prepareCamera()
                    }
                    // 否则询问是相机还是选择文件
                    else {
                        // 询问使用相机还是图片文件选择器
                        val dialog =
                            AlertDialog.Builder(this@WebViewActivity)
                                .setCancelable(false)
                                .setItems(R.array.webView_fileChooser) { _, index ->
                                    // 选择动作
                                    val items =
                                        resources.getStringArray(R.array.webView_fileChooser)
                                    // 使用相机
                                    if (index == 0 && items[index] == "相机") {
                                        // 选择了相机就该将mode改为单文件模式
                                        currentFileChooserMode = SINGLE_FILE_CHOOSER_CODE
                                        prepareCamera()
                                    }
                                    // 使用图片文件选择器
                                    else {
                                        // 清空之前照的照片
                                        currentPhotoUri = Uri.EMPTY;
                                        prepareFileChooser(fileChooserParams.mode, "image/*")
                                    }
                                }
                                .create()
                        dialog.show()
                    }
                }

                // 如果是任意文件
                if (fileChooserParams?.acceptTypes?.contains("*/*") == true) {
                    // 则直接打开文件选择器
                    prepareFileChooser(fileChooserParams.mode, "*/*")
                }
                return true
            }

        }

        // 一般生命周期监测
        webView?.webViewClient = object : WebViewClient() {
            // press ctrl+o (cmd+o) to override more methods
            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                // error, can't connect to the page
                tvLoading?.text = "加载失败，请重试"; // 错误时修改提示文字
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                // page loading started
                tvLoading?.visibility = View.VISIBLE; // 显示加载提示
                tvLoading?.text = "加载中，请稍候..."; // 错误时修改提示文字
            }

            // 加载完成后
            override fun onPageFinished(view: WebView, url: String) {
                tvLoading?.visibility = View.GONE; // 隐藏加载提示
                if (finishedAlready) {
                    return
                }
                // page loading finished
                finishedAlready = true

//                Handler().postDelayed(Runnable {
//                    // ******************* 向 webView 发出函数执行指令 *******************
//                    webView?.evaluateJavascript(
//                        "alert('这是一条由native端发起，向web端传送的指令')",
//                        null
//                    )
//                }, 3000)
            }
        }

        // 清除缓存
//        webView?.clearCache(true)

        if (OFFLINE_MODE) {
            // 加载本地html
            webView?.loadUrl("file:///android_asset/index.html")
        } else {
            // 加载指定地址
            webView?.loadUrl(getCurrWebUrl())
//            val brand: String? = Build.BRAND
//            println(" ------------------------------------------------------- brand: $brand ")
//            tvLoading?.visibility = View.VISIBLE; // 显示
//            tvLoading?.text = brand
        }

        // 给webJS端安装功能函数
        webView?.addJavascriptInterface(Index(this@WebViewActivity, webView), GlobalConfig.IO_NAME)

        // 注册扫码结果启动器
        scanResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // There are no request codes
                    val data: Intent? = result.data
                    val code = data?.extras?.getString("code")
                    webView?.evaluateJavascript("$RAM_NAME.callback.$SCAN('$code')", null)
                }
            }

        // 注册拍照结果启动器(文件选择用)
        // 如果是文件选择触发的拍照动作默认就是单选文件
        cameraLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val results: Array<Uri> = when {
                        result.data?.data != null -> arrayOf(result.data!!.data!!)
                        else -> arrayOf(currentPhotoUri)
                    }
                    // web端获取结果
                    fileUploadCallback?.onReceiveValue(results)
                } else {
                    // 必须调用回调函数并予以空值
                    fileUploadCallback?.onReceiveValue(arrayOf(currentPhotoUri))
                }
            }

        // 注册选择文件启动器
        fileChooseLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // 如果web文件选择回调函数为空，抛出错误 "fileUploadCallback is null"
                    if (fileUploadCallback == null) {
                        webView?.evaluateJavascript("alert('fileUploadCallback is null')", null)
                    }

                    // 单选文件
                    if (currentFileChooserMode == SINGLE_FILE_CHOOSER_CODE) {
                        val results: Array<Uri> = when {
                            result.data?.data != null -> arrayOf(result.data!!.data!!)
                            else -> arrayOf(currentPhotoUri)
                        }
                        // web端获取结果
                        fileUploadCallback?.onReceiveValue(results)
                    }
                    // 多选文件
                    else {
                        // 快捷操作补丁（单选一个文件）
                        if (result.data?.data != null) {
                            fileUploadCallback?.onReceiveValue(arrayOf(result.data!!.data!!))
                        } else if (result.data?.clipData != null) {
                            var uriArr = arrayOf<Uri>()
                            for (i in 0 until result.data!!.clipData!!.itemCount) {
                                val uri = result.data!!.clipData!!.getItemAt(i).uri
                                uriArr = uriArr.plus(uri)
                            }
                            fileUploadCallback?.onReceiveValue(uriArr)
                        }
                    }
                } else {
                    // 必须调用回调函数并予以空值
                    fileUploadCallback?.onReceiveValue(arrayOf(currentPhotoUri))
                }
            }

        // 相机拍照启动器(结果以base64字符串形式返回)
        photoLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // 将拍照所得变为base64字符串
                    val targetImageUri: Uri = currentPhotoUri

                    val imageStream: InputStream? = contentResolver.openInputStream(targetImageUri)
                    val bmp: Bitmap = BitmapFactory.decodeStream(imageStream)

                    val byteArrOutputStream = ByteArrayOutputStream()
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, byteArrOutputStream)
                    val byteArr = byteArrOutputStream.toByteArray()
                    val encImage: String = Base64.encode(byteArr, 0)

                    webView?.evaluateJavascript(
                        "$RAM_NAME.callback.$TAKE_PHOTO('data:image/jpg;base64,$encImage')",
                        null
                    )
                }
            }

        // 前往ipconfig启动器
        ipConfigLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val data: Intent? = result.data
                    val url = data?.extras?.getString("url")
                    if (url != null) {
                        val localStorage = LocalStorage(this@WebViewActivity)
                        localStorage.write("weburl", url)
                        webView?.loadUrl(url)
                    }
                }
            }
    }

    // 监测请求权限后的回调函数
    // 由 web 触发
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // 监听相机请求
        if (requestCode == RequirePermission.CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 获得允许，启用相机
                launchCamera()
            } else {
                makeToast(this, "您没有获取相机权限")
            }
        }
    }

    fun prepareTakePhoto() {
        RequirePermission(
            this@WebViewActivity,
            android.Manifest.permission.CAMERA,
            ::launchCameraToTakePhoto
        )
    }

    private fun getCurrWebUrl(): String {
        val localStorage = LocalStorage(this@WebViewActivity)
        val currUrl: String = localStorage.read("weburl") ?: WEB_URL
        return currUrl
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        val descriptionText = "Rtlink notification channel."
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
            description = descriptionText
        }
        channel.setShowBadge(true)
        // Register the channel with the system.
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun launchCameraToTakePhoto() {
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        currentPhotoUri = createImageFileUri()
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
        photoLauncher.launch(captureIntent)
    }

    // 准备启用相机
    // 判断一下是否已经具备了访问相机的权限
    private fun prepareCamera() {
        RequirePermission(this@WebViewActivity, android.Manifest.permission.CAMERA, ::launchCamera)
    }

    // 启用相机
    private fun launchCamera() {
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        currentPhotoUri = createImageFileUri()
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
        cameraLauncher.launch(captureIntent)
    }

    // 为即将拍下的照片创建存储Uri???
    private fun createImageFileUri(): Uri {
        val fileName: String =
            SimpleDateFormat("YYYYMMDD_HHmmss", Locale.getDefault()).format(Date()) + ".jpg"
        val contentValues: ContentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
            }
        }
        val resolver: ContentResolver = contentResolver
        val imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        return imageUri ?: throw RuntimeException("ImageUri is null")
    }

    // 准备使用文件选择器
    private fun prepareFileChooser(modeCode: Int, type: String) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = type
        // 默认单文件选择器
        // 如果是多文件选择器
        if (modeCode == MULTI_FILE_CHOOSER_CODE) {
            // 文件多选
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        // 记录一下当前模式（是否multiple模式）
        currentFileChooserMode = modeCode
        val chooserIntent = Intent.createChooser(intent, "选择文件")
        fileChooseLauncher.launch(chooserIntent)
    }

    // 劫持 webView 后退事件
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (webView != null) {
            if (webView!!.canGoBack()) webView!!.goBack()
        }
    }

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action == mAction) {
                val barcodeStr = intent.getStringExtra(mKey) //barcode value  获取扫描的条码数据
                System.out.println(" ------------------------------------------- barcodeStr ")
                System.out.println(barcodeStr)
                // TODO 将数据发回给 h5
//                textView!!.text = barcodeStr
            }
        }
    }

    override fun onPause() {
        super.onPause()
        registerReceiver(false)
    }

    override fun onResume() {
        super.onResume()
        realTimeAction
        registerReceiver(true)
    }

    private val realTimeAction: Unit
        /**
         * 实时获取扫描头 ACTION 以及 key 参数
         */
        get() {
            strs = ScanManager().getParameterString(
                intArrayOf(
                    PropertyID.WEDGE_INTENT_ACTION_NAME,
                    PropertyID.WEDGE_INTENT_DATA_STRING_TAG
                )
            )
            mAction = strs[0] //实时获取的action
            mKey = strs[1] //实时获取的key
        }

    /**
     * @param register , ture register , false unregister
     */
    private fun registerReceiver(register: Boolean) {
        if (register) {
            val filter = IntentFilter()
            filter.addAction(mAction)
            registerReceiver(mReceiver, filter)
        } else {
            unregisterReceiver(mReceiver)
        }
    }

}
