package com.rtlink.px50s.ui.components

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.rtlink.px50s.R

class TopBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    Toolbar(context, attrs, defStyleAttr) {

    private val mToolbar: Toolbar
    private val mTextView: TextView

    init {
        inflate(context, R.layout._topbar, this)
        mToolbar = findViewById(R.id.toolbar)
        mTextView = findViewById(R.id.title)
        attrs?.let { setAttributes(context, it) }
    }

    private fun setAttributes(context: Context, attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.TopBar)
        val titleText = typedArray.getString(R.styleable.TopBar_title)
        val canPop: Boolean = typedArray.getBoolean(R.styleable.TopBar_canPop, false)
        typedArray.recycle()

        // ------------------------------------- 初始化顶部条 -------------------------------------
        // 标题文字
        if (titleText != null) {
            mTextView.text = titleText
        }
        // 是否可以后退
        if (canPop) {
            // 设置后退按钮
            mToolbar.setNavigationIcon(R.drawable.backup)
            // 监听后退按钮点击事件
            mToolbar.setNavigationOnClickListener() {
                (context as Activity).finish()
            }
        }
        // -------------------------------------------------------------------------------------
    }

}
