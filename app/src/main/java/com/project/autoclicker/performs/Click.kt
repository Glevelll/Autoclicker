package com.project.autoclicker.performs

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityService.GestureResultCallback
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.util.Log

fun AccessibilityService.performClick(x: Int, y: Int) {
    val clickPath = Path()
    clickPath.moveTo(x.toFloat(), y.toFloat())

    // Длительность нажатия
    val clickStroke = GestureDescription.StrokeDescription(clickPath, 0, 200)

    val gestureBuilder = GestureDescription.Builder()
    gestureBuilder.addStroke(clickStroke)

    val gesture = gestureBuilder.build()

    // Добавление задержки
    Handler(Looper.getMainLooper()).postDelayed({
        dispatchGesture(gesture, object : GestureResultCallback() {
            override fun onCompleted(gestureDescription: GestureDescription?) {
                super.onCompleted(gestureDescription)
                Log.d("FloatingButtonService", "Click gesture completed at ($x, $y)")
            }

            override fun onCancelled(gestureDescription: GestureDescription?) {
                super.onCancelled(gestureDescription)
                Log.d("FloatingButtonService", "Click gesture cancelled")
            }
        }, null)
    }, 100)

    Log.d("FloatingButtonService", "Gesture dispatched for ($x, $y)")
}



