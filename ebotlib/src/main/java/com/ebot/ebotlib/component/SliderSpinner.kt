package com.ebot.ebotlib.component

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.annotation.FontRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.ebot.ebotlib.R
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin
import androidx.core.graphics.createBitmap


@SuppressLint("ResourceType")
internal class SliderSpinner @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): BaseSliderView(context, attrs, defStyleAttr) {

    private val dashesLines: Path = Path()
    private val middleTextContainer: RectF = RectF()
    private var track: RectF = RectF()

    private val mSpinnerPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = ContextCompat.getColor(context, R.color.neutral)
    }
    private val tempValuePaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.textAlign = Paint.Align.CENTER
    }
    private val trackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.style = Paint.Style.STROKE
        this.strokeCap = Paint.Cap.ROUND
    }

    private var cacheBitmap: Bitmap? = null
    private var cacheCanvas: Canvas? = null

    private var centerX: Float = 0f
    private var centerY: Float = 0f
    private val dashesPositions: ArrayList<Pair<PointF, PointF>> = arrayListOf()

    private var modeIcon: Drawable? = null
    private var iconSize: Float = DEFAULT_ICON_SIZE

    private var trackColor: Int = R.color.neutral_02
    private var trackProgressColor: Int = R.color.palette_01
    private var iconColor: Int = trackProgressColor
    private var dashesLinesWidth: Float = 3f
    private var thumbColor: Int = R.color.neutral

    private var fontFamily: Typeface? = null
    private var progressLineGradient: Shader? = null

    private var sppinerRadius: Float = 0f
    private var trackWidth: Float = 18f
    private var tempValueSize: Float = 120f
    private var tempValue = ""
    private var tempUnit = ""
//    private var tempUnit = "°C"
    private var temperatureValue = "$tempValue${tempUnit}"

    private val startAngle = START_ANGLE
    private val endAngle = END_ANGLE
    var minValue: Float = 0f
    var maxValue: Float = 1f
    private var progress: Float = 30f
    private var isOn: Boolean = true
    private var isOperatigStateActive: Boolean = true

    companion object {
        const val START_ANGLE: Float = 135f
        const val END_ANGLE: Float = 270f
        const val DEFAULT_VIEW_SIZE: Int = 900
        const val DEFAULT_PADDING: Float = 0.013f
        const val DEFAULT_ICON_SIZE: Float = 72f
    }

    init {
        attrs?.let {
            val attr = context.obtainStyledAttributes(it, R.styleable.SliderSpinner)
            try {
                isOn = attr.getBoolean(R.styleable.SliderSpinner_isOn, true)
                dashesLinesWidth = attr.getFloat(R.styleable.SliderSpinner_dashLinesWidth, dashesLinesWidth)
                tempValue = attr.getString(R.styleable.SliderSpinner_value) ?: ""
                tempValueSize = attr.getDimension(R.styleable.SliderSpinner_valueSize, tempValueSize)
                trackWidth = attr.getDimension(R.styleable.SliderSpinner_trackWidth, trackWidth)
                progress = attr.getFloat(R.styleable.SliderSpinner_trackProgress, progress)
                trackProgressColor = attr.getResourceId(R.styleable.SliderSpinner_trackProgressColor, trackProgressColor)
                iconColor = attr.getResourceId(R.styleable.SliderSpinner_iconColor, trackProgressColor)
                modeIcon = attr.getResourceId(R.styleable.SliderSpinner_icon, 0)
                    .takeIf { it != 0 }
                    ?.let { iconId ->
                    ContextCompat.getDrawable(context, iconId)
                }
                iconSize = attr.getDimension(R.styleable.SliderSpinner_iconSize, DEFAULT_ICON_SIZE)
                fontFamily = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    attr.getFont(R.styleable.SliderSpinner_font)
                } else {
                    attr.getResourceId(R.styleable.SliderSpinner_font, 0)
                        .takeIf { it != 0 }
                        ?.let { font ->
                            ResourcesCompat.getFont(context, font)
                        }
                }
            } finally {
                attr.recycle()
            }
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val mWidth = resolveSizeAndState(DEFAULT_VIEW_SIZE, widthMeasureSpec, 0)
        val mHeight = resolveSizeAndState(DEFAULT_VIEW_SIZE, heightMeasureSpec, 0)
        val viewSize =  min(mWidth, mHeight).plus(trackWidth.toInt())
        this.setMeasuredDimension(viewSize, viewSize)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculatePositions(w, h, oldw, oldh)
        cacheInvalidated = true
    }

    private fun calculatePositions(w: Int, h: Int, oldw: Int, oldh: Int) {
        centerX = w.times(0.5f)
        centerY = h.times(0.5f)
        track = RectF(
            trackWidth,
            trackWidth,
            w - trackWidth,
            h - trackWidth
        )
        sppinerRadius = track.width().times(.5f) - trackWidth.times(.5f) - track.width().times(
            DEFAULT_PADDING
        )
        calculateTextMaxBounds()
        calculateDashesPositions()
    }

    /**
     *  Method to handle over-draw
     *  this method cache the drawing of icon, track, dashes, spinnerCircle
     */
    private fun createCache() {
        if (cacheBitmap == null || cacheInvalidated) {
            cacheBitmap = createBitmap(width, height)
            cacheBitmap?.let { cacheBitmap ->
                cacheCanvas = Canvas(cacheBitmap)
                cacheCanvas?.let { cacheCanvas ->
                    drawTrack(cacheCanvas)
                    drawSpinnerCirlce(cacheCanvas)
                    drawModeIcon(cacheCanvas)
                    onDrawDashes(cacheCanvas)
                }
                cacheInvalidated = false
            }
        }
    }

    private fun calculateDashesPositions() {
        val side = 45
        val startPoint = Math.toRadians(90.0)
        val radius = sppinerRadius + track.width().times(DEFAULT_PADDING) - sppinerRadius.times(.12f)
        val radius2 = sppinerRadius + track.width().times(DEFAULT_PADDING) - sppinerRadius.times(.25f)
        for (i in startPoint.toInt() until side) {
            dashesPositions.add(
                Pair(
                    PointF(
                        centerX  + (radius * cos(startPoint + i).toFloat()),
                        centerY  + (radius * sin(startPoint + i).toFloat()),
                    ),
                    PointF(
                        centerX  + (radius2 * cos(startPoint + i).toFloat()),
                        centerY + (radius2 * sin(startPoint + i).toFloat()),
                    )
                )
            )
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        createCache()

        cacheBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }
        drawTrackProgress(canvas, progress.coerceIn(0f, endAngle))
        drawTemperture(canvas)
    }

    private fun drawSpinnerCirlce(canvas: Canvas) {
        canvas.drawCircle(
            centerX,
            centerY,
            sppinerRadius - track.width().times(DEFAULT_PADDING),
            mSpinnerPaint
        )
    }

    private fun drawTrackProgress(canvas: Canvas, progress: Float) {
        val pro =  if (isOn) progress else 0f
        val progressAngle = (pro / 100f) * endAngle
        canvas.drawArc(
            track,
            startAngle,
            progressAngle,
            false,
            trackPaint.apply {
                this.color = getIsOnColor(trackProgressColor)
                this.strokeWidth = trackWidth
                this.shader = progressLineGradient
            }
        )
        val thumbPos = getThumbPosition(progressAngle)
        canvas.drawCircle(
            thumbPos.x,
            thumbPos.y,
            trackWidth.times(1.2f),
            mSpinnerPaint.apply {
                this.color = ContextCompat.getColor(context, thumbColor)
                this.setShadowLayer(
                    10f,
                    0f,
                    0f,
                    ContextCompat.getColor(
                        context,
                        R.color.neutral_02
                    )
                )
            }
        )
    }

    private fun drawTrack(canvas: Canvas) {
        canvas.drawArc(
            track,
            startAngle,
            endAngle,
            false,
            trackPaint.apply {
                this.color = ContextCompat.getColor(context, trackColor)
                this.strokeWidth = trackWidth
            }
        )

    }

    private fun getThumbPosition(progressAngle: Float): PointF {
        val radius = (width - trackWidth.times(2)).times(.5f)
        val angleInRadians = Math.toRadians((startAngle + progressAngle).toDouble())
        val x = (centerX+ radius * cos(angleInRadians)).toFloat()
        val y = (centerY+ radius * sin(angleInRadians)).toFloat()
        return PointF(x, y)
    }

    private fun drawModeIcon(canvas: Canvas) {
        val iconWidth = iconSize.times(.5f).toInt()
        val leftPoint = centerX.toInt()
        val radius2 = sppinerRadius + track.width().times(DEFAULT_PADDING) - sppinerRadius.times(.483f)
        val topPoint = leftPoint - (radius2 * sin(90.0)).toInt()

        modeIcon?.let {
            it.setBounds(
                leftPoint - iconWidth,
                topPoint - iconWidth,
                leftPoint + iconWidth,
                topPoint + iconWidth
            )
            DrawableCompat.wrap(it)
            DrawableCompat.setTint(it,  getIsOnColor(iconColor))
            it.draw(canvas)
        }
    }

    private fun onDrawDashes(canvas: Canvas) {
        dashesLines.apply {
            dashesPositions.forEach {
                this.moveTo(
                    it.first.x,
                    it.first.y,
                )
                this.lineTo(
                    it.second.x,
                    it.second.y,
                )
                this.close()
            }
        }
        canvas.drawPath(
            dashesLines,
            trackPaint.apply {
                this.color = ContextCompat.getColor(context, trackColor)
                this.strokeWidth = dashesLinesWidth
            }
        )

    }

    private fun calculateTempValueMaxSize(tempValue: String, maxSize: Float): Float {
        val bounds = Rect()
        for (i in 1 .. maxSize.toInt()) {
            tempValuePaint.textSize = i.toFloat()
            tempValuePaint.getTextBounds(tempValue, 0, tempValue.length, bounds)
            if (bounds.width() > middleTextContainer.width()) {
                return i.toFloat() - 1
            }
        }
        return maxSize
    }

    private fun drawTemperture(canvas: Canvas) {
        val bounds = Rect()
        val dashLineRadius2 = sppinerRadius - sppinerRadius.times(.25f)
        val textSize = calculateTempValueMaxSize(temperatureValue, tempValueSize)

        tempValuePaint.apply {
            this.color = if (isOn) { ContextCompat.getColor(context, R.color.neutral_04)
            } else ContextCompat.getColor(context, R.color.neutral_01)
            this.textSize = textSize
            this.getTextBounds(tempValue, 0, tempValue.length, bounds)
            this.setTypeface(fontFamily)
        }
        val tempValuesHeight = centerY - (tempValuePaint.descent()
                + tempValuePaint.ascent().div(2)
                -  centerY.times(.05f)
                ) + (dashLineRadius2 * sin(0f))

        canvas.drawText(
            tempValue,
            centerX,
            tempValuesHeight,
            tempValuePaint
        )
        canvas.drawText(
            tempUnit,
            centerX + bounds.right.div(2f) + 20f,
            tempValuesHeight + bounds.top.times(.7f),
            tempValuePaint.apply {
                this.color = ContextCompat.getColor(context, R.color.neutral_03)
                this.getTextBounds(temperatureValue, 0, temperatureValue.length, bounds)
                this.textSize = textSize.times(.3f)
            }
        )
    }

    private fun calculateTextMaxBounds() {
        val radius = sppinerRadius + track.width().times(DEFAULT_PADDING) - sppinerRadius.times(.22f)
        val startPoint = Math.toRadians(40.0)

        val leftPoint = centerX - (radius * cos(startPoint.toFloat()))
        val topPoint = centerY + (radius * sin(startPoint.toFloat()))
        val rightPoint = centerX + (radius * cos(startPoint.toFloat()))
        val bottomPoint = centerY - (radius * sin(startPoint.toFloat()))
        val textContainerSize = bottomPoint - topPoint / 4f

        middleTextContainer.set(
            leftPoint,
            topPoint - textContainerSize,
            rightPoint,
            bottomPoint + textContainerSize
        )
    }

    private fun getIsOnColor(colorId: Int): Int {
        return if (isOn) {
            ContextCompat.getColor(context, colorId)
        } else ContextCompat.getColor(context, R.color.neutral_02)
    }

//    fun updateSpinner(
//        mode: ThermostatMode,
//        setPoint: String = "--",
//        isOn: Boolean = true,
//        unit: String = "",
//    ) {
//        this.mode = mode
//        this.isOn = isOn
//        this.tempValue = setPoint
//        this.tempUnit = unit
//        invalidate()
//    }

    fun setProgress(value: Float) {
        progress = convertSetPointValueToPercent(value.coerceIn(minValue, maxValue))
        tempValue = calcualteSetpoint(progress).toString()
        invalidate()
    }

    fun setFonFamily(@FontRes fonId: Int) {
        fontFamily = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            resources.getFont(fonId)
        } else {
            ResourcesCompat.getFont(context, fonId)
        }
        invalidate()
    }

    fun setIcon(iconId: Int) {
        modeIcon = ContextCompat.getDrawable(context, iconId)
        invalidate()
    }

    private fun updateProgressValue(value: Float) {
        tempValue = calcualteSetpoint(value).toString()
        invalidate()
    }

    private fun convertSetPointValueToPercent(value: Float): Float {
        return ((value - minValue) / (maxValue - minValue)) * 100f
    }

    /**
     * Round value to one decimal
     */
    private fun calcualteSetpoint(values: Float): Float {
        val v = minValue + ((values / 100f) * (maxValue - minValue))
        return (v * 10f).roundToInt() / 10f
    }

    private fun updateProgressBasedOnTouch(x: Float, y: Float) {
        val angle = Math.toDegrees(atan2(y - centerY, x - centerX).toDouble()).toFloat()
        val adjustedAngle = angle.plus(360f)
        val isInRange = adjustedAngle !in startAngle..startAngle.plus(endAngle)

        val reAdjustedAngle = if (isInRange) adjustedAngle - 360f
        else adjustedAngle

        if (reAdjustedAngle in startAngle..startAngle.plus(endAngle)) {
            progress = (((reAdjustedAngle - startAngle) / endAngle) * 100f)
            updateProgressValue(progress)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return if (isOn) {
            super.onTouchEvent(event)
            return when(event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    val setPointValue = calcualteSetpoint(progress)
                    onSliderChanged?.onChangeValueStart(setPointValue)
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    updateProgressBasedOnTouch(event.x, event.y)
                    val setPointValue = calcualteSetpoint(progress)
                    onSliderChanged?.onChangedValue(setPointValue)
                    return true
                }
                MotionEvent.ACTION_UP -> {
                    val setPointValue = calcualteSetpoint(progress)
                    onSliderChanged?.onChangedValue(setPointValue)
                    true
                }
                else -> false
            }
        } else false
    }

}

enum class Units(val symbol: String, val longName: String) {
    C("ºC", "celsius"),
    F("ºF", "fahrenheit"),
    PERCENTAGE("%", "percentage")
}
