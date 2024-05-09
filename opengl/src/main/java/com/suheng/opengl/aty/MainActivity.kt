package com.suheng.opengl.aty

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.suheng.opengl.MyRenderer4
import com.suheng.opengl.R
import com.suheng.opengl.Utils

class MainActivity : AppCompatActivity() {

    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var renderer: GLSurfaceView.Renderer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!Utils.supportGlEs20(this)) {
            Toast.makeText(this, "GLES 2.0 not supported!", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        glSurfaceView = findViewById<GLSurfaceView>(R.id.surface).apply {
            setEGLContextClientVersion(2)
            setEGLConfigChooser(8, 8, 8, 8, 16, 0)
            //setRenderer(DemoRenderer(this@MainActivity));
            //setRenderer(MyRenderer())
            //setRenderer(MyRenderer2())
            //setRenderer(MyRenderer3())
            setRenderer(MyRenderer4(this@MainActivity).also { renderer = it })
            renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        }
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        (renderer as? MyRenderer4)?.destroy()
    }
}