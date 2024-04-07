package com.example.wavelength

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.*
import android.view.MotionEvent
import android.util.Log
import kotlin.random.Random

class MainGame : AppCompatActivity() {
    private var randomNumber = -1 // Initialize randomNumber

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.main_game)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnBack: Button = findViewById(R.id.btnBack)
        val semiCircleSegmentsView: SemiCircleSegmentsView = findViewById(R.id.SemiCircleSegmentsView)
        val lineView: LineView = findViewById(R.id.lineView)
        val semiCircleHider: CircleHider = findViewById(R.id.circleHider)
        val promptLeft: TextView = findViewById(R.id.promptLeft)
        val promptRight: TextView = findViewById(R.id.promptRight)

        generateRandomNumber()
        promptLeft.text = PromptLeftObject.promptLeftList[randomNumber]
        promptRight.text = PromptRightObject.promptRightList[randomNumber]

        btnBack.setOnClickListener {
            // Open a new activity when the button is clicked
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val btnReload: Button = findViewById(R.id.randomizeButton)
        btnReload.setOnClickListener {
            semiCircleSegmentsView.reloadSegmentColors()
        }

        val btnChangePrompt: Button = findViewById(R.id.changePrompt)
        btnChangePrompt.setOnClickListener {
            generateRandomNumber()
            promptLeft.text = PromptLeftObject.promptLeftList[randomNumber]
            promptRight.text = PromptRightObject.promptRightList[randomNumber]
        }

        val btnKnob: CircleView = findViewById(R.id.knob)
        btnKnob.setOnClickListener {
            if (semiCircleHider.visibility == View.VISIBLE) {
                semiCircleHider.visibility = View.INVISIBLE
            }
            else {
                semiCircleSegmentsView.reloadSegmentColors()
                lineView.setLineAngle(90.0)
                semiCircleHider.visibility = View.VISIBLE

                generateRandomNumber()
                promptLeft.text = PromptLeftObject.promptLeftList[randomNumber]
                promptRight.text = PromptRightObject.promptRightList[randomNumber]
            }
        }
        btnKnob.setOnLongClickListener {
            if (semiCircleHider.visibility == View.VISIBLE) {
                semiCircleHider.visibility = View.INVISIBLE
            }
            else {
                semiCircleHider.visibility = View.VISIBLE
            }
            true
        }
    }

    private fun generateRandomNumber() {
        val promptListSize = PromptLeftObject.promptLeftList.size
        val range = 0 .. promptListSize
        randomNumber = Random.nextInt(range.last)
    }
}

class SemiCircleSegmentsView : View {

    private val paint: Paint = Paint()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        paint.isAntiAlias = true
    }

    fun reloadSegmentColors() {
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        val targetColors = listOf(Color.parseColor("#D8832B"), Color.parseColor("#C94D4D"), Color.parseColor("#407480"), Color.parseColor("#C94D4D"), Color.parseColor("#D8832B"))

        val segmentColors = mutableListOf<Int>()
        val otherColors = mutableListOf<Int>()

        val segments = 19

        for (i in 0 until segments) {
            otherColors.add(Color.parseColor("#F6EEE3"))
        }

        val startIndex = (0 until segments - targetColors.size).random()

        for (i in startIndex until startIndex + targetColors.size) {
            segmentColors.add(i % segments)
            otherColors.remove(i % segments)
        }

        for (i in 0 until segments) {
            paint.color = if (segmentColors.contains(i)) {
                targetColors[segmentColors.indexOf(i)]
            } else {
                otherColors[i]
            }
            canvas.drawArc(
                0f,
                0f,
                width,
                height * 2,
                180f + (i * (180f / segments)),
                (180f / segments),
                true,
                paint
            )
        }
    }
}

class CircleHider : View {

    private val paint: Paint = Paint()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        paint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        // Set the paint color to blue
        paint.color = Color.parseColor("#FF8672")

        // Draw a single semi-circle
        canvas.drawArc(
            0f,
            0f,
            width,
            height * 2,
            180f,
            180f,
            true,
            paint
        )
    }

}

class LineView : View {

    private var lineLength = 725f
    private var lineAngle = 90.0

    private val linePaint = Paint().apply {
        color = Color.parseColor("#141414")
        strokeWidth = 50f
        strokeCap = Paint.Cap.ROUND
    }

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    fun setLineAngle(angle: Double) {
        lineAngle = angle
        Log.d("ANGLE: ", angle.toString())
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val width = width.toFloat()
        val height = height.toFloat()

        val startX = width / 2f
        val endX = startX + lineLength * cos(Math.toRadians(lineAngle)).toFloat()
        val endY = height - lineLength * sin(Math.toRadians(lineAngle)).toFloat()

        canvas.drawLine(startX, height, endX, endY, linePaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                if (isTouchNearLine(event.x, event.y)) {
                    lastTouchAngle = calculateAngle(event.x, event.y) // Initialize lastTouchAngle
                    return true
                }
            }
            MotionEvent.ACTION_UP -> {
                performClick()
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val newTouchAngle = calculateAngle(event.x, event.y).toInt()
                val interval = 174 / 19 // Divide the semicircle into 19 equal parts
                val snappedAngle = (newTouchAngle.toDouble() / interval).roundToInt() * interval

                var updatedAngle = lineAngle + (lastTouchAngle - snappedAngle)

                if (updatedAngle < 4) {
                    updatedAngle = 3.0
                }
                if (updatedAngle > 176) {
                    updatedAngle = 177.0
                }

                setLineAngle(updatedAngle)
                lastTouchAngle = snappedAngle.toFloat()
                return true
            }
        }
        return super.onTouchEvent(event)
    }


    private fun isTouchNearLine(x: Float, y: Float): Boolean {
        val startX = width / 2f
        val endX = startX + lineLength * cos(Math.toRadians(lineAngle)).toFloat()
        val endY = height - lineLength * sin(Math.toRadians(lineAngle)).toFloat()

        val touchRadius = 50f

        val distance = abs(
            ((endX - startX) * (height - y) - (startX - x) * (endY - height)) /
                    sqrt(
                        ((endX - startX) * (endX - startX) + (endY - height) * (endY - height))
                    )
        )

        return distance <= touchRadius
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private var lastTouchAngle = 0f

    private fun calculateAngle(x: Float, y: Float): Float {
        val centerX = width / 2f
        val centerY = height
        val deltaX = x - centerX
        val deltaY = y - centerY

        return Math.toDegrees(atan2(deltaY.toDouble(), deltaX.toDouble())).toFloat()
    }
}

class CircleView : View {

    private val paint: Paint = Paint()

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        // Set up paint properties
        paint.color = Color.parseColor("#407480")
        paint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f
        val radius = min(centerX, centerY)

        // Draw the circle
        canvas.drawCircle(centerX, centerY, radius, paint)
    }
}