package com.project.autoclicker.performs

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityService.GestureResultCallback
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.util.Log

// Свайп по координатам
fun AccessibilityService.performSwipe(startX: Int, startY: Int, endX: Int, endY: Int) {
    val swipePath = Path()
    swipePath.moveTo(startX.toFloat(), startY.toFloat())
    swipePath.lineTo(endX.toFloat(), endY.toFloat())

    // Длительность свайпа
    val swipeStroke = GestureDescription.StrokeDescription(swipePath, 0, 500)

    val gestureBuilder = GestureDescription.Builder()
    gestureBuilder.addStroke(swipeStroke)

    val gesture = gestureBuilder.build()

    // Задержка перед жестом
    Handler(Looper.getMainLooper()).postDelayed({
        dispatchGesture(gesture, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
                Log.d("FloatingButtonService", "Swipe gesture completed from ($startX, $startY) to ($endX, $endY)")
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                super.onCancelled(gestureDescription)
                Log.d("FloatingButtonService", "Swipe gesture cancelled")
            }
        }, null)
    }, 100)

    Log.d("FloatingButtonService", "Swipe gesture dispatched from ($startX, $startY) to ($endX, $endY)")
}