package com.suheng.structure.view.activity

import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.suheng.structure.view.PathKtView
import com.suheng.structure.view.PathKtView2
import com.suheng.structure.view.R

class SVGPathActivity : AppCompatActivity() {

    object Singleton {
        const val TAG = "SVGPath"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_svg_path)

        this.initSeekBar()
    }

    private fun initSeekBar() {
        val width = resources.getDimensionPixelOffset(R.dimen.path_delete_icon)
        val maxWidth = resources.getDimensionPixelOffset(R.dimen.path_delete_icon_max)

        val pathKtView2: PathKtView2 = findViewById(R.id.kt_path_view2)
        pathKtView2.setOnClickListener {
            Toast.makeText(this@SVGPathActivity, "PathKtView2", Toast.LENGTH_SHORT).show()
        }

        val pathKtView: PathKtView = findViewById(R.id.kt_path_view)

        val seekBar = findViewById<SeekBar>(R.id.kt_seek_bar)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                val layoutParams2 = pathKtView2.layoutParams
                layoutParams2.width = (width + progress / 100f * maxWidth).toInt()
                layoutParams2.height = layoutParams2.width
                pathKtView2.layoutParams = layoutParams2

                //val radio = 1f * layoutParams2.width / width
                //pathKtView.animate().scaleX(radio).scaleY(radio).setDuration(50).start()
                val layoutParams = pathKtView.layoutParams
                layoutParams.width = layoutParams2.width
                layoutParams.height = layoutParams.width
                pathKtView.layoutParams = layoutParams
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                Log.d(Singleton.TAG, "onStartTrackingTouch: progress = ${seekBar.progress}")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                Log.d(Singleton.TAG, "onStopTrackingTouch: progress = ${seekBar.progress}")
            }
        })
    }

}