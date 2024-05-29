package com.suheng.opengl.aty

import android.content.Context
import android.content.Intent
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.suheng.opengl.R
import com.suheng.opengl.Utils
import com.suheng.opengl.renderer.BaseRenderer
import com.suheng.opengl.renderer.CubeRenderer
import com.suheng.opengl.renderer.CubeRenderer1
import com.suheng.opengl.renderer.DemoRenderer
import com.suheng.opengl.renderer.MyRenderer
import com.suheng.opengl.renderer.MyRenderer2
import com.suheng.opengl.renderer.MyRenderer3
import com.suheng.opengl.renderer.MyRenderer4
import com.suheng.opengl.renderer.MySurfaceRenderer

class CubeActivity : AppCompatActivity() {

    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var renderer: GLSurfaceView.Renderer
    private lateinit var ctx: Context

    companion object {
        private const val ENTER_FLAG_EXTRA = "enter_flag_extra"
        const val ENTER_FLAG_DATA_0 = 0
        const val ENTER_FLAG_DATA_1 = 1
        const val ENTER_FLAG_DATA_2 = 2
        const val ENTER_FLAG_DATA_3 = 3
        const val ENTER_FLAG_DATA_4 = 4
        const val ENTER_FLAG_DATA_5 = 5
        const val ENTER_FLAG_DATA_6 = 6
        const val ENTER_FLAG_DATA_7 = 7

        fun openActivity(ctx: Context, enterFlag: Int, options: Bundle? = null) {
            ContextCompat.startActivity(ctx, Intent(ctx, CubeActivity::class.java).apply {
                putExtra(ENTER_FLAG_EXTRA, enterFlag)
            }, options)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ctx = this
        setContentView(R.layout.activity_cube)

        if (!Utils.supportGlEs20(this)) {
            Toast.makeText(this, "GLES 2.0 not supported!", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        glSurfaceView = findViewById<GLSurfaceView>(R.id.surface).apply {
            setEGLContextClientVersion(2)
            setEGLConfigChooser(8, 8, 8, 8, 16, 0)

            renderer = when (intent.getIntExtra(ENTER_FLAG_EXTRA, ENTER_FLAG_DATA_0)) {
                ENTER_FLAG_DATA_2 -> MyRenderer()
                ENTER_FLAG_DATA_3 -> MyRenderer2()
                ENTER_FLAG_DATA_4 -> MyRenderer3()
                ENTER_FLAG_DATA_5 -> MyRenderer4(ctx)
                ENTER_FLAG_DATA_7 -> MySurfaceRenderer()
                ENTER_FLAG_DATA_6 -> DemoRenderer(ctx)

                ENTER_FLAG_DATA_1 -> CubeRenderer1(ctx)
                else -> CubeRenderer(ctx)
            }
            setRenderer(renderer)

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
            if (this::renderer.isInitialized) {
                (renderer as? BaseRenderer)?.destroy()
            }
        }
        glSurfaceView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (this::renderer.isInitialized) {
            (renderer as? MyRenderer4)?.destroy()
        }
    }
}