package com.suheng.opengl.aty

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.suheng.opengl.R

//https://blog.piasy.com/2016/06/07/Open-gl-es-android-2-part-1/index.html
//http://zhangtielei.com/posts/blog-opengl-transformations-1.html
//http://light3moon.com/2019/12/30/OpenGLES%20%E5%85%A5%E9%97%A8%E5%AD%A6%E4%B9%A0/
//https://cloud.tencent.com/developer/article/1472455
//https://learnopengl.com/Getting-started
//https://learnopengl-cn.github.io/
//https://learnopengl-cn.readthedocs.io/zh/latest/
//https://open.gl/introduction
//https://github.com/JYangkai/MediaDemo
//https://juejin.cn/post/6975806731473387528
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.tv_base_1).setOnClickListener {
            CubeActivity.openActivity(this, CubeActivity.ENTER_FLAG_DATA_2)
        }
        findViewById<View>(R.id.tv_base_2).setOnClickListener {
            CubeActivity.openActivity(this, CubeActivity.ENTER_FLAG_DATA_3)
        }
        findViewById<View>(R.id.tv_base_3).setOnClickListener {
            CubeActivity.openActivity(this, CubeActivity.ENTER_FLAG_DATA_4)
        }
        findViewById<View>(R.id.tv_base_4).setOnClickListener {
            CubeActivity.openActivity(this, CubeActivity.ENTER_FLAG_DATA_5)
        }
        findViewById<View>(R.id.tv_base_5).setOnClickListener {
            CubeActivity.openActivity(this, CubeActivity.ENTER_FLAG_DATA_6)
        }
        findViewById<View>(R.id.tv_base_6).setOnClickListener {
            CubeActivity.openActivity(this, CubeActivity.ENTER_FLAG_DATA_7)
        }
        findViewById<View>(R.id.tv_cube_1).setOnClickListener {
            CubeActivity.openActivity(this, CubeActivity.ENTER_FLAG_DATA_1)
        }
        findViewById<View>(R.id.tv_cube_2).setOnClickListener {
            CubeActivity.openActivity(this, CubeActivity.ENTER_FLAG_DATA_0)
        }
    }

}