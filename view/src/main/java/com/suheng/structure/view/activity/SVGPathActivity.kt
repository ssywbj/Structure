package com.suheng.structure.view.activity

import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.suheng.structure.view.PathKtView2
import com.suheng.structure.view.R

class SVGPathActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_svg_path)

        this.initSeekBar()
    }

    private fun initSeekBar() {
        val minWith = 150
        val offsetWith = 350

        val pathKtView: PathKtView2 = findViewById(R.id.kt_path_view)
        val layoutParams = pathKtView.layoutParams
        layoutParams.width = minWith
        layoutParams.height = layoutParams.width
        pathKtView.layoutParams = layoutParams
        pathKtView.setOnClickListener {
            Toast.makeText(this@SVGPathActivity, "PathKtView", Toast.LENGTH_SHORT).show()
        }

        val seekBar = findViewById<SeekBar>(R.id.kt_seek_bar)
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                layoutParams.width = (minWith + progress / 100f * offsetWith).toInt()
                layoutParams.height = layoutParams.width
                pathKtView.layoutParams = layoutParams
                /*Log.v("Wbj", "onProgressChanged: progress = $progress, fromUser = $fromUser" +
                        ", layoutParams.width = ${layoutParams.width}")*/
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                Log.d("Wbj", "onStartTrackingTouch: progress = ${seekBar.progress}")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                Toast.makeText(
                    this@SVGPathActivity,
                    "stop progress is: " + seekBar.progress,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

}