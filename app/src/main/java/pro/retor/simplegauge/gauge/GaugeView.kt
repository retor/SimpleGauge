package pro.retor.simplegauge.gauge

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View


/**
 * Created by retor on 22.02.17.
 */
class GaugeView : View {

    companion object {
        val FLOAT_FORMAT = -100
        val INTEGER_FORMAT = -150
    }

    var format = FLOAT_FORMAT

    private val arcColor = "#009688"
    private val cursorColor = "#263238"


    private var left = 10f
    private var top = 10f
    private var width = 200f
    private var height = 120f
    private var strokeWidth = 1f
    private var paddingToInner = width / 5
    private var textSize = 16f
    private var maxValue = 100453f
    private var minValue = 0f
    private var currentValue = 15432f

    private var centerX: Float = height / 2
    private var centerY: Float = width / 2

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        readAttributes(attrs)
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        readAttributes(attrs)
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        readAttributes(attrs)
        init()
    }

    private var arrowRoundRadius = 10f

    private val oval: RectF by lazy {
        RectF(centerX - arrowRoundRadius, centerY - arrowRoundRadius, centerX + arrowRoundRadius, centerY + arrowRoundRadius)
    }

    private val arrowPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var arrowHeight = 100f

    private val arrow by lazy {
        val path = Path()
        path.reset()
        path.fillType = Path.FillType.WINDING
        path.moveTo(getXArrow(true), centerY - (arrowRoundRadius / 2))
        path.lineTo(centerX, (centerY + arrowRoundRadius) - arrowHeight)
        path.lineTo(getXArrow(false), centerY - (arrowRoundRadius / 2))
        path.close()
        path.addOval(oval, Path.Direction.CW)
        path
    }

    private fun getXArrow(start: Boolean): Float = if (start) centerX - (arrowRoundRadius / 2) else centerX + (arrowRoundRadius / 2)

    private var backRadius = 90f
    private var backAngel = 200f
    private var backSweep = 140f

    private val back by lazy {
        val rectF = RectF(centerX - backRadius, (centerY - backRadius), centerX + backRadius, (centerY + backRadius))
        rectF
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var bottomPadding = 50f

    private val arrowText = Paint(Paint.ANTI_ALIAS_FLAG)

    private fun init() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        left *= resources.displayMetrics.density
        top *= resources.displayMetrics.density
        width *= resources.displayMetrics.density
        height *= resources.displayMetrics.density
        strokeWidth *= resources.displayMetrics.density
        paddingToInner *= resources.displayMetrics.density
        arrowRoundRadius *= resources.displayMetrics.density
        arrowHeight *= resources.displayMetrics.density
        backRadius *= resources.displayMetrics.density
        textSize *= resources.displayMetrics.density

        centerX = width / 2
        centerY = height - bottomPadding

        arrowPaint.color = Color.parseColor(cursorColor)
        arrowPaint.style = Paint.Style.FILL
        arrowPaint.textSize = textSize

        paint.color = Color.parseColor(arcColor)
        paint.style = Paint.Style.FILL

        arrowText.textSize = textSize
        arrowText.color = Color.WHITE
    }

    private fun readAttributes(attrs: AttributeSet?) {
        attrs?.let {
            /*val attrsArray = intArrayOf(
                    android.R.attr.layout_width, // 0
                    android.R.attr.layout_height // 1
            )
            val ta = context.obtainStyledAttributes(it, attrsArray)
            val w = ta.getDimensionPixelSize(0, ViewGroup.LayoutParams.MATCH_PARENT)
            val h = ta.getDimensionPixelSize(1, ViewGroup.LayoutParams.MATCH_PARENT)


            if (w>0 && h>0) {
                width = w.toFloat()
                height = h.toFloat()
            }
            ta.recycle()*/
        }
    }

    fun setMinValue(value: Float) {
        minValue = value
        invalidate()
    }

    fun setMaxValue(value: Float) {
        maxValue = value
        invalidate()
    }

    fun setValue(value: Float) {
        currentValue = value
        if (maxValue < value)
            maxValue = value
        invalidate()
    }

    private fun calculateDegree(): Float {
        if (currentValue == (maxValue / 2))
            return 0f
        val step = 70 / 5
        val s = (maxValue / 2) / 5
        val v = (currentValue / 2) / s
        if (currentValue > (maxValue / 2)) {
            return (v * step) / 2
        } else {
            val fl = 70f - ((v * step) / 2)
            return -fl
        }
    }

    private fun rotateArrow() {
        val mMatrix = Matrix()
        mMatrix.postRotate(calculateDegree(), centerX, centerY)
        arrow.transform(mMatrix)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.save()

        canvas?.drawArc(back, backAngel, backSweep, true, paint)

        var miV: Number = minValue
        var maV: Number = maxValue
        var cur: Number = currentValue

        if (format == FLOAT_FORMAT) {
            miV = minValue
            maV = maxValue
            cur = currentValue
        }
        if (format == INTEGER_FORMAT) {
            miV = minValue.toInt()
            maV = maxValue.toInt()
            cur = currentValue.toInt()
        }

        canvas?.drawText("$miV", 15f, centerY, arrowPaint)
        canvas?.drawText("$maV", width - arrowPaint.measureText("$maV"), centerY, arrowPaint)

        rotateArrow()
        canvas?.drawPath(arrow, arrowPaint)

//        if (currentValue >= maxValue / 2)
//            canvas?.drawTextOnPath("$cur", arrow, (arrowHeight / 2) - (arrowPaint.measureText("$cur") / 2), 0f, arrowText)
//        else {
            canvas?.save()
            canvas?.rotate(calculateDegree(), centerX, centerY)
            canvas?.drawText("$cur", centerX - (arrowPaint.measureText("$cur") / 2),
                    (centerY + arrowRoundRadius) - arrowHeight, arrowPaint)
            canvas?.restore()
//        }
        canvas?.restore()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = View.MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = View.MeasureSpec.getSize(heightMeasureSpec)

        val w: Int
        val h: Int

        //Measure Width
        if (widthMode == View.MeasureSpec.EXACTLY) {
            //Must be this size
            w = widthSize
        } else if (widthMode == View.MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            w = Math.min(width.toInt(), widthSize)
        } else {
            //Be whatever you want
            w = width.toInt()
        }

        //Measure Height
        if (heightMode == View.MeasureSpec.EXACTLY) {
            //Must be this size
            h = heightSize
        } else if (heightMode == View.MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            h = Math.min(height.toInt(), heightSize)
        } else {
            //Be whatever you want
            h = height.toInt()
        }

        //MUST CALL THIS
        setMeasuredDimension(w, h)
    }

}