package com.suheng.structure.view.wheel

import android.graphics.Camera
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.Transformation

class CameraRotateAnimation @JvmOverloads constructor(
    private val view: View,
    private val fromDegrees: Float,
    private val toDegrees: Float,
    private val direction: Int = 0,
) : Animation() {

    private lateinit var camera: Camera
    private var scale = 1.0f
    private var centerX: Float = 0f
    private var centerY: Float = 0f

    override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
        super.initialize(width, height, parentWidth, parentHeight)
        camera = Camera()
        scale = view.resources.displayMetrics.density
        centerX = view.width / 2f
        centerY = view.height / 2f
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        val degrees = fromDegrees + (toDegrees - fromDegrees) * interpolatedTime
        val matrix = t.matrix
        camera.save()
        Log.d(
            "CameraAnimation",
            "degrees: $degrees, interpolatedTime:$interpolatedTime, matrix: ${matrix.toShortString()}"
        )
        when (direction) {
            0 -> camera.rotateY(degrees)
            1 -> camera.rotateX(degrees)
            else -> camera.rotateZ(degrees)
        }
        camera.getMatrix(matrix)
        camera.restore()

        //修正失真，主要修改MPERSP_0和MPERSP_1
        FloatArray(9).also {
            matrix.getValues(it) //获取数值
            it[6] = it[6] / scale //数值修正
            it[7] = it[7] / scale //数值修正
            matrix.setValues(it)
        }

        matrix.preTranslate(-centerX, -centerY)
        matrix.postTranslate(centerX, centerY)
    }

    fun startAnimation() {
        view.startAnimation(this)
    }

}