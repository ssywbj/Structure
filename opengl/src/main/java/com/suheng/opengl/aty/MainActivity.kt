package com.suheng.opengl.aty

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.suheng.opengl.R
import com.suheng.opengl.Utils
import com.suheng.opengl.renderer.CubeRenderer
import com.suheng.opengl.renderer.MyRenderer4

//https://blog.piasy.com/2016/06/07/Open-gl-es-android-2-part-1/index.html
//http://zhangtielei.com/posts/blog-opengl-transformations-1.html
//https://learnopengl.com/Getting-started
//http://www.learnopengles.com/tag/opengl-es-2-for-android-a-quick-start-guide/
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
            //setRenderer(CubeRenderer(this@MainActivity).also { renderer = it })
            renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        }
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }

    override fun onPause() {
        super.onPause()
        glSurfaceView.queueEvent {
            /*
             * Generally, GL Context will be destroyed after pause.
             * So we destroy GL-related resources before pause.
             */
            (renderer as? CubeRenderer)?.destroy()
        }
        glSurfaceView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        (renderer as? MyRenderer4)?.destroy()
    }
}