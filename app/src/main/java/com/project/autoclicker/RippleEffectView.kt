package com.project.autoclicker

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.RippleDrawable
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat

// Класс для отображения риппл эффекта
@SuppressLint("ViewConstructor")
class RippleEffectView(context: Context, sizeDp: Int) : View(context) {
    private var rippleDrawable: RippleDrawable? = null

    init {
        val sizePx = (sizeDp * resources.displayMetrics.density).toInt()
        layoutParams = WindowManager.LayoutParams(sizePx, sizePx)
        background = ContextCompat.getDrawable(context, R.drawable.ripple_effect)
        rippleDrawable = background as RippleDrawable
    }

    fun showRippleEffectAtPosition(x: Float, y: Float) {
        this.x = x
        this.y = y
        rippleDrawable?.setHotspot(x, y)
        rippleDrawable?.state = intArrayOf(android.R.attr.state_pressed, android.R.attr.state_enabled)
        postDelayed({
            rippleDrawable?.state = intArrayOf()
        }, 300)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.translate(x - width / 2, y - height / 2)
        background?.draw(canvas)
    }
}