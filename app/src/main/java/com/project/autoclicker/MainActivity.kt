package com.project.autoclicker

import android.content.ComponentName
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.view.accessibility.AccessibilityManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private val OVERLAY_PERMISSION_REQUEST_CODE = 1000
    private val ACCESSIBILITY_PERMISSION_REQUEST_CODE = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!Settings.canDrawOverlays(this)) {
            val intent =
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
        }

        val startServiceButton: Button = findViewById(R.id.showFloatingButton)

        startServiceButton.setOnClickListener {
            if (isAccessibilityServiceEnabled()) {
                Toast.makeText(this, "Есть доступ к сервису", Toast.LENGTH_SHORT).show()
                startService(Intent(this, FloatingButtonService::class.java))
            } else {
                Toast.makeText(this, "Нет доступа к сервису", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
                startActivityForResult(intent, ACCESSIBILITY_PERMISSION_REQUEST_CODE)
            }
        }
    }

    private fun isAccessibilityServiceEnabled(): Boolean {
        val accessibilityService = ComponentName(this, FloatingButtonService::class.java)
        val enabledServices = Settings.Secure.getString(contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)

        if (enabledServices != null && enabledServices.isNotEmpty()) {
            val colonSplitter = TextUtils.SimpleStringSplitter(':')
            colonSplitter.setString(enabledServices)
            while (colonSplitter.hasNext()) {
                val componentName = colonSplitter.next()
                if (componentName.equals(accessibilityService.flattenToString(), ignoreCase = true)) {
                    return true
                }
            }
        }
        return false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVERLAY_PERMISSION_REQUEST_CODE) {
            // Проверяем результат запроса разрешения на отображение поверх других приложений
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "Разрешение не было предоставлено", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Разрешение предоставлено", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == ACCESSIBILITY_PERMISSION_REQUEST_CODE) {
            // Проверяем результат запроса разрешения на использование Accessibility Service
            if (isAccessibilityServiceEnabled()) {
                Toast.makeText(this, "Разрешение на использование Accessibility Service предоставлено", Toast.LENGTH_SHORT).show()
                startService(Intent(this, FloatingButtonService::class.java))
            } else {
                Toast.makeText(this, "Разрешение на использование Accessibility Service не было предоставлено", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
