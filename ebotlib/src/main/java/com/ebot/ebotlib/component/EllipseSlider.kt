package com.ebot.ebotlib.component

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PathMeasure
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import androidx.core.graphics.createBitmap
import com.ebot.ebotlib.R


class EllipseSlider @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): BaseSliderView(context, attrs, defStyleAttr) {

    private var trackPath: Path = Path()
    private var trackProgressPath: Path = Path()
    private var trackPathMeasure: PathMeasure = PathMeasure()
    private var trackPathLength: Float = 0f

    private val trackPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = ContextCompat.getColor(context, R.color.neutral_02)
        this.style = Paint.Style.STROKE
        this.strokeCap = Paint.Cap.ROUND
    }

    private val thumbPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = ContextCompat.getColor(context, R.color.neutral)
        this.style = Paint.Style.FILL_AND_STROKE
        this.strokeCap = Paint.Cap.ROUND
    }

    var progressTint: Int = ContextCompat.getColor(context, R.color.palette_01)
        set(value) {
            field = ContextCompat.getColor(context, value)
        }

    var thumTint: Int = ContextCompat.getColor(context, R.color.neutral)
        set(value) {
            field = ContextCompat.getColor(context, value)
        }

    private var cacheBitmap: Bitmap? = null
    private var cacheCanva: Canvas? = null

    private var centerX: Float = 0f
    private var centerY: Float = 0f
    private var trackWidth: Float = 22f
    private var thumbRadius: Float = trackWidth
    private var progress: Float = 0.0f
    var value: Float = 0f
        set(value) {
            field = value.coerceIn(minValue, maxValue)
            progress = this.value.div(maxValue)
            updateProgress(progress)
            invalidate()
        }
    var minValue: Float = 0f
    var maxValue: Float = 100f
    private var progressPos: FloatArray = floatArrayOf(0f, 0f)
    private var isDragging: Boolean = false


    companion object {

        const val DEFAULT_VIEW_WIDTH: Int = 800
        const val DEFAULT_VIEW_HEIGHT: Int = 400
        const val DEFAULT_PADDING: Float = 0.013f

    }


    init {
        attrs?.let {
            val attr = context.obtainStyledAttributes(it, R.styleable.EllipseSlider)
            try {
                this.trackWidth = attr.getDimension(R.styleable.EllipseSlider_ellipseWidth, this.trackWidth)
                this.thumbRadius = attr.getDimension(R.styleable.EllipseSlider_thumbRadius, this.thumbRadius)
                this.minValue = attr.getFloat(R.styleable.EllipseSlider_minValue, minValue)
                this.maxValue = attr.getFloat(R.styleable.EllipseSlider_maxValue, maxValue)
                this.value = calculateProgressValue(
                    attr.getFloat(
                        R.styleable.EllipseSlider_progressValue,
                        this.value
                    ).coerceIn(minValue, maxValue)
                        .div(maxValue)
                )
                this.progressTint = attr.getResourceId(
                    R.styleable.EllipseSlider_progressTint,
                    R.color.palette_01
                )

                this.thumTint = attr.getResourceId(
                    R.styleable.EllipseSlider_thumbTint,
                    R.color.neutral
                )

            } finally {
                attr.recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val mWidth = resolveSizeAndState(DEFAULT_VIEW_WIDTH, widthMeasureSpec, 0)
        val mHeight = resolveSizeAndState(DEFAULT_VIEW_HEIGHT, heightMeasureSpec, 0)
        this.setMeasuredDimension(
            mWidth.plus(thumbRadius.toInt().times(2)).plus(trackWidth).toInt(),
            mHeight.plus(thumbRadius.toInt()).plus(trackWidth).toInt()
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        calculatePositions(w, h, oldw, oldh)
        cacheInvalidated = true
    }

    private fun calculatePositions(w: Int, h: Int, oldw: Int, oldh: Int) {
        centerX = w.times(.5f)
        centerY = h.times(.5f)

        trackPath.apply {
            this.reset()
            this.moveTo(
                thumbRadius.plus(trackWidth),
                h - thumbRadius.times(2f).plus(trackWidth)
            )
            this.cubicTo(
                thumbRadius, h - thumbRadius.times(2f),
                centerX, thumbRadius,
                width.toFloat() - thumbRadius.plus(trackWidth),h- thumbRadius.times(2f).plus(trackWidth)
            )
        }

        trackProgressPath.apply {
            this.reset()
            this.moveTo(
                thumbRadius.plus(trackWidth),
                h - thumbRadius.times(2f).plus(trackWidth)
            )
            this.cubicTo(
                thumbRadius, h - thumbRadius.times(2f),
                centerX, thumbRadius,
                width.toFloat() - thumbRadius.plus(trackWidth),h- thumbRadius.times(2f).plus(trackWidth)
            )
        }
        trackPathMeasure.setPath(trackPath, false)
        trackPathLength = trackPathMeasure.length
        progressPos = floatArrayOf(trackWidth + this.value, h - trackWidth.times(2f))
    }

    private fun cacheStaticView() {
        if (cacheBitmap == null || cacheInvalidated) {
            cacheBitmap = createBitmap(width, height)
            cacheBitmap?.let { cacheBitmap ->
                cacheCanva = Canvas(cacheBitmap)
                cacheCanva?.let { cacheCanva ->
                    onDrawTrack(cacheCanva)
                }
                cacheInvalidated = false
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        cacheStaticView()

        cacheBitmap?.let {
            canvas.drawBitmap(it, 0f, 0f, null)
        }

        trackProgressPath.reset()
        trackPathMeasure.getSegment(
            0f, trackPathLength * progress,
            trackProgressPath,
            true
        )
        updateProgress(progress)
        canvas.drawPath(
            trackProgressPath,
            trackPaint.apply {
                this.color = progressTint
                this.strokeWidth = trackWidth
            }
        )
        canvas.drawCircle(
            progressPos[0],
            progressPos[1],
            thumbRadius,
            thumbPaint.apply {
                this.color = thumTint
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

    private fun onDrawTrack(canvas: Canvas) {
        canvas.drawPath(
            trackPath,
            trackPaint.apply {
                this.strokeWidth = trackWidth
            }
        )
    }

    private fun updateProgress(progress: Float) {
        trackPathMeasure.getPosTan(
            trackPathLength * progress,
            progressPos,
            null
        )
    }

    fun refreshView() {
        invalidate()
    }

    private fun calculateProgressValue(progress: Float): Float {
        return minValue + ((progress.times(maxValue) / maxValue) * (maxValue - minValue))
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return event?.let {
            super.onTouchEvent(event)
            when(it.action) {
                MotionEvent.ACTION_DOWN -> {
                    this.onSliderChanged?.onChangeValueStart(this.value)
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    isDragging = true
                    progress = (event.x / width)
                    if (progress in 0f..1f) {
                        this.value = calculateProgressValue(progress)
                        this.onSliderChanged?.onChangedValue(this.value)
                    }
                    true
                }
                MotionEvent.ACTION_UP -> {
                    isDragging = false
                    this.onSliderChanged?.onChangeValueStop(this.value)
                    true
                }
                else -> false
            }
        } ?: false

    }

}