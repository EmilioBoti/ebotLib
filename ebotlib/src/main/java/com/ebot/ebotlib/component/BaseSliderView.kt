package com.ebot.ebotlib.component

import android.content.Context
import android.util.AttributeSet


open class BaseSliderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): BaseView(context, attrs, defStyleAttr) {

    protected var onSliderChanged: OnSliderChanged? = null


    fun setOnSliderValueChanged(onSliderChanged: OnSliderChanged) {
        this.onSliderChanged = onSliderChanged
    }

}