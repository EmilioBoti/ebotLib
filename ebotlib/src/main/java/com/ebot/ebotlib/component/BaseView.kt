package com.ebot.ebotlib.component

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import androidx.annotation.RequiresApi
import kotlin.math.sqrt


abstract class BaseView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): View(context, attrs, defStyleAttr) {


    protected var cacheInvalidated = true


    @RequiresApi(Build.VERSION_CODES.O_MR1)
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let { touch ->
            parent?.requestDisallowInterceptTouchEvent(true)
            when(touch.action) {
                MotionEvent.ACTION_DOWN -> {
                    this.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                }
                MotionEvent.ACTION_UP -> {
                    this.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE)
                }
            }
            return true
        }
        return false
    }

}