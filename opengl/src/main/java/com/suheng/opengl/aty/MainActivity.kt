package com.suheng.opengl.aty

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.suheng.opengl.R

//https://blog.piasy.com/2016/06/07/Open-gl-es-android-2-part-1/index.html
//http://zhangtielei.com/posts/blog-opengl-transformations-1.html
//https://learnopengl.com/Getting-started
//http://www.learnopengles.com/tag/opengl-es-2-for-android-a-quick-start-guide/
//https://open.gl/introduction
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
        findViewById<View>(R.id.tv_cube_1).setOnClickListener {
            CubeActivity.openActivity(this, CubeActivity.ENTER_FLAG_DATA_1)
        }
        findViewById<View>(R.id.tv_cube_2).setOnClickListener {
            CubeActivity.openActivity(this, CubeActivity.ENTER_FLAG_DATA_0)
        }
    }

}