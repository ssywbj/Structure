package com.suheng.structure.view.activity

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.suheng.structure.view.R
import com.suheng.structure.view.wheel.CameraRotateAnimation
import com.suheng.structure.view.wheel.CurvedImage
import com.suheng.structure.view.wheel.Rotate3DImage

class GraphicsCameraActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graphic_camera)
        findViewById<View>(R.id.iView).setOnClickListener { v: View ->
            val status = (v.tag as? Int) ?: 0.also { v.tag = 0 }
            val degrees = 180
            val fromDegrees = (if (status == 0) 0 else degrees).toFloat()
            val toDegrees = (if (status == 0) degrees else 0).toFloat()
            CameraRotateAnimation(v, fromDegrees, toDegrees, 1).apply {
                duration = 1000
                fillAfter = true
                interpolator = LinearInterpolator()
                setAnimationListener(object : Animation.AnimationListener {
                    override fun onAnimationStart(animation: Animation) {}
                    override fun onAnimationEnd(animation: Animation) {
                        (v.tag as? Int)?.let {
                            v.tag = if (it == 0) 1 else 0
                        }
                    }

                    override fun onAnimationRepeat(animation: Animation) {}
                })
                startAnimation()
            }
        }

        findViewById<View>(R.id.rotate3DImage).setOnClickListener { v: View -> (v as? Rotate3DImage)?.startAnimation() }
        findViewById<View>(R.id.ciView).setOnClickListener { v: View -> (v as? CurvedImage)?.startAnimation() }
    }
}