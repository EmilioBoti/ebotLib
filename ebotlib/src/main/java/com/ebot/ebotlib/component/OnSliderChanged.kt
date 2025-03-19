package com.ebot.ebotlib.component


interface OnSliderChanged {

    fun onChangedValue(value: Float)
    fun onChangeValueStart(value: Float)
    fun onChangeValueStop(value: Float)

}