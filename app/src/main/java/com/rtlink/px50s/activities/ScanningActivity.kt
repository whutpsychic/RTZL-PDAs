package com.rtlink.px50s.activities

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.rtlink.px50s.R

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.rtlink.px50s.utils.RequirePermission
import com.rtlink.px50s.utils.makeToast
import java.util.concurrent.Executors

class ScanningActivity : ComponentActivity() {

    private lateinit var cameraProvider: ProcessCameraProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 沉浸式渲染
        enableEdgeToEdge()
        // 显示注册的页面
        setContentView(R.layout.activity_scanning)
        // 相机权限确认和后续操作
        RequirePermission(this@ScanningActivity, Manifest.permission.CAMERA, ::bindCameraUseCases)
    }

    // 权限申请结果监听
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == RequirePermission.CAMERA_PERMISSION_REQUEST_CODE
            && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            // user granted permissions - we can set up our scanner
            bindCameraUseCases()
        } else {
            // user did not grant permissions - we can't use the camera
            makeToast(this, "Camera permission required")
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    // 相机事件注册
    private fun bindCameraUseCases() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            cameraProvider = cameraProviderFuture.get()

            // setting up the preview use case
            val previewUseCase = Preview.Builder()
                .build()
                .also {
                    val pv: PreviewView = findViewById(R.id.cameraView)
                    it.surfaceProvider = pv.surfaceProvider
                }

            // configure our MLKit BarcodeScanning client

            /* passing in our desired barcode formats - MLKit supports additional formats outside of the
            ones listed here, and you may not need to offer support for all of these. You should only
            specify the ones you need */
            val options = BarcodeScannerOptions.Builder().setBarcodeFormats(
                Barcode.FORMAT_CODE_128,
                Barcode.FORMAT_CODE_39,
                Barcode.FORMAT_CODE_93,
                Barcode.FORMAT_EAN_8,
                Barcode.FORMAT_EAN_13,
                Barcode.FORMAT_QR_CODE,
                Barcode.FORMAT_UPC_A,
                Barcode.FORMAT_UPC_E,
                Barcode.FORMAT_PDF417
            ).build()

            // getClient() creates a new instance of the MLKit barcode scanner with the specified options
            val scanner = BarcodeScanning.getClient(options)

            // setting up the analysis use case
            val analysisUseCase = ImageAnalysis.Builder()
                .build()

            // define the actual functionality of our analysis use case
            analysisUseCase.setAnalyzer(Executors.newSingleThreadExecutor()) { imageProxy ->
                processImageProxy(scanner, imageProxy)
            }

            // configure to use the back camera
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    previewUseCase,
                    analysisUseCase
                )
            } catch (illegalStateException: IllegalStateException) {
                // If the use case has already been bound to another lifecycle or method is not called on main thread.
                Log.e(TAG, illegalStateException.message.orEmpty())
            } catch (illegalArgumentException: IllegalArgumentException) {
                // If the provided camera selector is unable to resolve a camera to be used for the given use cases.
                Log.e(TAG, illegalArgumentException.message.orEmpty())
            }
        }, ContextCompat.getMainExecutor(this))
    }

    // 监听扫码事件函数
    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(barcodeScanner: BarcodeScanner, imageProxy: ImageProxy) {
        imageProxy.image?.let { image ->
            val inputImage =
                InputImage.fromMediaImage(
                    image,
                    imageProxy.imageInfo.rotationDegrees
                )

            barcodeScanner.process(inputImage)
                // 扫码成功事件
                .addOnSuccessListener { barcodeList ->
                    val barcode = barcodeList.getOrNull(0)

                    // `rawValue` is the decoded value of the barcode
                    barcode?.rawValue?.let { value ->
                        // 得到扫码数据后暂停/关闭相机，并带着结果返回
                        cameraProvider.unbindAll()

                        val intent = Intent().putExtra("code", value)
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    }
                }
                .addOnFailureListener {
                    // This failure will happen if the barcode scanning model
                    // fails to download from Google Play Services

                    Log.e(TAG, it.message.orEmpty())
                }.addOnCompleteListener {
                    // When the image is from CameraX analysis use case, must
                    // call image.close() on received images when finished
                    // using them. Otherwise, new images may not be received
                    // or the camera may stall.

                    imageProxy.image?.close()
                    imageProxy.close()
                }
        }
    }

    companion object {
        // 查看报错信息用
        val TAG: String = ScanningActivity::class.java.simpleName
    }

}
