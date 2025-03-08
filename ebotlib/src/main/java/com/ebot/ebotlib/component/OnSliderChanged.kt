package com.ebot.ebotlib.component


interface OnSliderChanged {

    fun onChangeValueStart()
    fun onChangedValue(value: Float)
    fun onChangingValue(value: Float)
//    fun onIncreaseClicked()
//    fun onDicreaseClicked()

}