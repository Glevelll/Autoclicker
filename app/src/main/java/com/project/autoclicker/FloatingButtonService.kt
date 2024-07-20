package com.project.autoclicker

import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.project.autoclicker.performs.performClick
import com.project.autoclicker.performs.performSwipe

class FloatingButtonService : AccessibilityService() {

    private lateinit var windowManager: WindowManager
    private lateinit var floatingButtonView: View
    private lateinit var params: WindowManager.LayoutParams
    private lateinit var floatingButton: Button
    private var isExpanded = false
    private lateinit var textView: TextView

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("ClickableViewAccessibility", "WrongConstant", "ServiceCast")
    override fun onCreate() {
        super.onCreate()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        floatingButtonView = LayoutInflater.from(this).inflate(R.layout.floating_button, null)

        textView = floatingButtonView.findViewById(R.id.textView)

        // Настройка сервиса поверх приложений
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.TOP or Gravity.START
        params.x = 0
        params.y = 100

        windowManager.addView(floatingButtonView, params)

        floatingButton = floatingButtonView.findViewById(R.id.floatingButton)

        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f

        // Нажатие на главную кнопку
        floatingButton.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                }

                MotionEvent.ACTION_MOVE -> {
                    // Обновляем координаты, куда направлен палец
                    val currentTouchX = event.rawX
                    val currentTouchY = event.rawY
                    updateTextView(currentTouchX.toInt(), currentTouchY.toInt())

                    params.x = initialX + (currentTouchX - initialTouchX).toInt()
                    params.y = initialY + (currentTouchY - initialTouchY).toInt()
                    windowManager.updateViewLayout(floatingButtonView, params)
                }

                MotionEvent.ACTION_UP -> {
                    if (initialTouchX == event.rawX && initialTouchY == event.rawY) {
                        toggleButtonsVisibility()
                    }
                }
            }
            true
        }


        floatingButton.setOnClickListener(null)

        // Кнопка 1
        val button1 = floatingButtonView.findViewById<Button>(R.id.button1)
        button1.setOnClickListener {
            openStatusBar()
        }

        // Кнопка 2
        val button2 = floatingButtonView.findViewById<Button>(R.id.button2)
        button2.setOnClickListener {
            performClickWithDelay(600, 300, 0) // Сделать сразу
            performClickWithDelay(600, 600, 2000) // Сделать через 2 секунды
        }

        // Кнопка 3
        val button3 = floatingButtonView.findViewById<Button>(R.id.button3)
        button3.setOnClickListener {
            // Риппл эффект
            performSwipeWithDelay(100, 500, 600, 500, 0)
            performSwipeWithDelay(100, 700, 600, 700, 2000)
        }

        // Кнопка 4
        val button4 = floatingButtonView.findViewById<Button>(R.id.button4)
        button4.setOnClickListener {
            Toast.makeText(this, "Кнопка 4 нажата", Toast.LENGTH_SHORT).show()
        }

        // Кнопка 5
        val button5 = floatingButtonView.findViewById<Button>(R.id.button5)
        button5.setOnClickListener {
            Toast.makeText(this, "Кнопка 5 нажата", Toast.LENGTH_SHORT).show()
        }

        // Кнопка 6
        val button6 = floatingButtonView.findViewById<Button>(R.id.button6)
        button6.setOnClickListener {
            Toast.makeText(this, "Кнопка 6 нажата", Toast.LENGTH_SHORT).show()
        }
    }


    private fun toggleButtonsVisibility() {
        if (!isExpanded) {
            showButtons()
        } else {
            hideButtons()
        }
        isExpanded = !isExpanded
    }

    // Открытие кнопок
    private fun showButtons() {
        floatingButtonView.findViewById<Button>(R.id.button1).visibility = View.VISIBLE
        floatingButtonView.findViewById<Button>(R.id.button2).visibility = View.VISIBLE
        floatingButtonView.findViewById<Button>(R.id.button3).visibility = View.VISIBLE
        floatingButtonView.findViewById<Button>(R.id.button4).visibility = View.VISIBLE
        floatingButtonView.findViewById<Button>(R.id.button5).visibility = View.VISIBLE
        floatingButtonView.findViewById<Button>(R.id.button6).visibility = View.VISIBLE
    }

    // Скрытие кнопок
    private fun hideButtons() {
        floatingButtonView.findViewById<Button>(R.id.button1).visibility = View.GONE
        floatingButtonView.findViewById<Button>(R.id.button2).visibility = View.GONE
        floatingButtonView.findViewById<Button>(R.id.button3).visibility = View.GONE
        floatingButtonView.findViewById<Button>(R.id.button4).visibility = View.GONE
        floatingButtonView.findViewById<Button>(R.id.button5).visibility = View.GONE
        floatingButtonView.findViewById<Button>(R.id.button6).visibility = View.GONE
    }

    // Открытие статус бар (шторки)
    private fun openStatusBar() {
        val statusBarService = getSystemService("statusbar")
        val statusBarManager = Class.forName("android.app.StatusBarManager")
        val expand = statusBarManager.getMethod("expandNotificationsPanel")
        expand.invoke(statusBarService)
    }

    // КЛИК
    // Риппл эффект для клика
    private fun rippleClick(x: Int, y: Int) {
        val rippleSizeDp = 50
        val rippleEffectView = RippleEffectView(this, rippleSizeDp)
        val sizePx = (rippleSizeDp * resources.displayMetrics.density).toInt()
        val rippleParams = WindowManager.LayoutParams(
            sizePx,
            sizePx,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        rippleParams.gravity = Gravity.TOP or Gravity.START
        rippleParams.x = x - sizePx / 2
        rippleParams.y = y - sizePx / 2

        windowManager.addView(rippleEffectView, rippleParams)
        rippleEffectView.showRippleEffectAtPosition(sizePx / 2f, sizePx / 2f)

        rippleEffectView.postDelayed({
            windowManager.removeView(rippleEffectView)
        }, 350)
    }

    // Задержка для клика
    private fun performClickWithDelay(x: Int, y: Int, delayMillis: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            rippleClick(x, y)
            performClick(x, y)
        }, delayMillis)
    }


    // СВАЙП
    // Риппл эффект свайпа
    private fun rippleSwipe(startX: Int, startY: Int, endX: Int, endY: Int) {
        val rippleSizeDp = 50
        val rippleEffectViewStart = RippleEffectView(this, rippleSizeDp)
        val sizePx = (rippleSizeDp * resources.displayMetrics.density).toInt()
        val rippleParamsStart = WindowManager.LayoutParams(
            sizePx,
            sizePx,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        rippleParamsStart.gravity = Gravity.TOP or Gravity.START
        rippleParamsStart.x = startX - sizePx / 2
        rippleParamsStart.y = startY - sizePx / 2

        windowManager.addView(rippleEffectViewStart, rippleParamsStart)
        rippleEffectViewStart.showRippleEffectAtPosition(sizePx / 2f, sizePx / 2f)

        rippleEffectViewStart.postDelayed({
            windowManager.removeView(rippleEffectViewStart)

            val rippleEffectViewEnd = RippleEffectView(this, rippleSizeDp)
            val rippleParamsEnd = WindowManager.LayoutParams(
                sizePx,
                sizePx,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
            )
            rippleParamsEnd.gravity = Gravity.TOP or Gravity.START
            rippleParamsEnd.x = endX - sizePx / 2
            rippleParamsEnd.y = endY - sizePx / 2

            windowManager.addView(rippleEffectViewEnd, rippleParamsEnd)
            rippleEffectViewEnd.showRippleEffectAtPosition(sizePx / 2f, sizePx / 2f)

            rippleEffectViewEnd.postDelayed({
                windowManager.removeView(rippleEffectViewEnd)
            }, 350)
        }, 350)
    }

    // Задержка для свайпа
    private fun performSwipeWithDelay(startX: Int, startY: Int, endX: Int, endY: Int, delayMillis: Long) {
        Handler(Looper.getMainLooper()).postDelayed({
            rippleSwipe(startX, startY, endX, endY)
            performSwipe(startX, startY, endX, endY)
        }, delayMillis)
    }

    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(floatingButtonView)
    }

    private fun updateTextView(x: Int, y: Int) {
        textView.text = "X: ${x}, Y: ${y}"
    }



    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {
        TODO("Not yet implemented")
    }

    override fun onInterrupt() {
        TODO("Not yet implemented")
    }
}

