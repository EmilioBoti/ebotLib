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
import kotlin.math.min


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
    private val trackProgressPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = ContextCompat.getColor(context, R.color.neutral_02)
        this.style = Paint.Style.STROKE
        this.strokeCap = Paint.Cap.ROUND
    }

    private val thumbPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        this.color = ContextCompat.getColor(context, R.color.neutral)
        this.style = Paint.Style.FILL_AND_STROKE
        this.strokeCap = Paint.Cap.ROUND
    }

    private var cacheBitmap: Bitmap? = null
    private var cacheCanva: Canvas? = null


    private var centerX: Float = 0f
    private var centerY: Float = 0f
    private var trackWidth: Float = 24f
    private var progress: Float = 0.0f
    private var minValue: Float = 0f
    private var maxValue: Float = 100f
    private var progressPos: FloatArray = floatArrayOf(0f, 0f)

    companion object {
        const val DEFAULT_VIEW_WIDTH: Int = 800
        const val DEFAULT_VIEW_HEIGHT: Int = 400
        const val DEFAULT_PADDING: Float = 0.013f

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val mWidth = resolveSizeAndState(DEFAULT_VIEW_WIDTH, widthMeasureSpec, 0)
        val mHeight = resolveSizeAndState(DEFAULT_VIEW_HEIGHT, heightMeasureSpec, 0)
        val viewSize =  min(mWidth, mHeight).plus(trackWidth.toInt())
        this.setMeasuredDimension(mWidth, mHeight)
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
                trackWidth,
                h - trackWidth.times(2f)
            )
            this.cubicTo(
                trackWidth, h - trackWidth.times(2f),
                centerX, trackWidth,
                width.toFloat() - trackWidth,h- trackWidth.times(2f)
            )
        }

        trackProgressPath.apply {
            this.reset()
            this.moveTo(
                trackWidth,
                h - trackWidth.times(2f)
            )
            this.cubicTo(
                trackWidth, h - trackWidth.times(2f),
                centerX, trackWidth,
                width.toFloat() - trackWidth,h- trackWidth.times(2f)
            )
        }
        progressPos = floatArrayOf(trackWidth, h - trackWidth.times(2f))
        trackPathMeasure.setPath(trackPath, false)
        trackPathLength = trackPathMeasure.length
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
        canvas.drawPath(
            trackProgressPath,
            trackProgressPaint.apply {
                this.color = ContextCompat.getColor(context, R.color.palette_01)
                this.strokeWidth = trackWidth
            }
        )
        canvas.drawCircle(
            progressPos[0],
            progressPos[1],
            trackWidth,
            thumbPaint
        )
        updateProgress()
    }

    private fun onDrawTrack(canvas: Canvas) {
        canvas.drawPath(
            trackPath,
            trackPaint.apply {
                this.strokeWidth = trackWidth
            }
        )
    }

    private fun updateProgress() {
        trackPathMeasure.getPosTan(
            trackPathLength * progress,
            progressPos,
            null
        )
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return event?.let {
            super.onTouchEvent(event)
            when(it.action) {
                MotionEvent.ACTION_DOWN -> {
                    this.onSliderChanged?.onChangeValueStart()
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    progress = (event.x / width).coerceIn(minValue, maxValue)
                    if (progress in 0f..1f) {
                        val value = progress * maxValue
                        this.onSliderChanged?.onChangingValue(value)
                        updateProgress()
                        invalidate()
                    }
                    true
                }
                else -> false
            }
        } ?: false

    }

}