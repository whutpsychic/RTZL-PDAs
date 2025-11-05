package com.rtlink.px50s.webIO

class CallbackKeys {

    companion object {

        private const val COMMON_KEY: String = "Callback"

        // modalTips()
        const val MODAL_TIPS: String = "modalTips$COMMON_KEY"

        // modalConfirm()
        const val MODAL_CONFIRM: String = "modalConfirm$COMMON_KEY"

        // modalLoading()
        const val MODAL_LOADING: String = "modalLoading$COMMON_KEY"

        // readLocal()
        const val READ_LOCAL: String = "readLocal$COMMON_KEY"

        // scan()
        const val SCAN: String = "scan$COMMON_KEY"

        // networkType
        const val NETWORK_TYPE: String = "checkNetworkType$COMMON_KEY"

        // takePhoto
        const val TAKE_PHOTO: String = "takePhoto$COMMON_KEY"

        // getSafeHeights
        const val GET_SAFE_HEIGHTS: String = "getSafeHeights$COMMON_KEY"

        // getDeviceInfo
        const val GET_DEVICE_INFO: String = "getDeviceInfo$COMMON_KEY"

    }
}