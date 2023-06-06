package com.suheng.structure.view.activity

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.suheng.structure.view.PathKtView
import com.suheng.structure.view.PathKtView2
import com.suheng.structure.view.R
import com.suheng.structure.view.utils.CountViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SVGPathActivity : AppCompatActivity() {

    object Singleton {
        const val TAG = "SVGPath"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        //ViewTreeViewModelStoreOwner.set(window.decorView,this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_svg_path)

        this.initSeekBar()
        this.initTimeView();
    }

    private fun initSeekBar() {
        val width = resources.getDimensionPixelOffset(R.dimen.path_delete_icon)
        val maxWidth = resources.getDimensionPixelOffset(R.dimen.path_delete_icon_max)

        val imageView: ImageView = findViewById(R.id.kt_path_iv)
        imageView.setImageDrawable(
            Drawable.createFromXml(
                resources, resources.getXml(R.xml.vector_delete)
            )
        )

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

                val layoutParams = pathKtView.layoutParams
                layoutParams.width = layoutParams2.width
                layoutParams.height = layoutParams.width
                pathKtView.layoutParams = layoutParams

                val lpIV = imageView.layoutParams
                lpIV.width = layoutParams2.width
                lpIV.height = lpIV.width
                imageView.layoutParams = lpIV
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                Log.d(Singleton.TAG, "onStartTrackingTouch: progress = ${seekBar.progress}")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                Log.d(Singleton.TAG, "onStopTrackingTouch: progress = ${seekBar.progress}")
            }
        })
    }

    private val mHandler by lazy {
        Handler(Looper.getMainLooper())
    }

    private val mRunnable = object : Runnable {
        override fun run() {
            //mTextTime.text = "${mCountLive.value}, $mCount"
            mCount++
            mCountLive.value = mCountLive.value?.plus(1)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (mHandler.hasCallbacks(this)) {
                    mHandler.removeCallbacks(this)
                }
            } else {
                mHandler.removeCallbacks(this)
            }
            mHandler.postDelayed(this, 1000)
        }
    }

    /*private val mRunnable: Runnable = run {
        Runnable {
            mTextTime.text = mCount.toString()
            mCount++
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (mHandler.hasCallbacks(mRunnable)) {
                    mHandler.removeCallbacks(mRunnable)
                }
            } else {
                mHandler.removeCallbacks(mRunnable)
            }
            mHandler.postDelayed(mRunnable, 1000)
        }
    }*/

    private lateinit var mTextTime: TextView
    private var mCount: Int = 0
    private val mViewModel by lazy {
        ViewModelProvider(this)[CountViewModel::class.java]
    }

    private val mCountLive: MutableLiveData<Int> by lazy {
        MutableLiveData<Int>(0)
    }

    private fun initTimeView() {
        mTextTime = findViewById(R.id.text_time)
        /*mTextTime.text = "${mCountLive.value}"
        mCount++
        mCountLive.value = mCountLive.value?.plus(1)

        mHandler.postDelayed(mRunnable, 1000)

        val countObserver = Observer<Int> { t -> mTextTime.text = "$t" }
        mCountLive.observe(this, countObserver)*/

        mViewModel.mCountLive.observe(this) { value ->
            mTextTime.text = "$value"
        }
        mViewModel.startObserver()

        lifecycleScope.launch {
            Log.d("Wbj_", "++lifecycleScope launch launch, ${Thread.currentThread().name}")
            withContext(Dispatchers.IO) {
                Log.d("Wbj_", "IO thread, ${Thread.currentThread().name}")
            }
        }

        lifecycleScope.launch {
            Log.d("Wbj_", "--lifecycleScope launch thread, ${Thread.currentThread().name}")
            withContext(Dispatchers.Main) {
                Log.d("Wbj_", "main thread, ${Thread.currentThread().name}")
            }
        }

        /*lifecycleScope.launch {
            val async = async(Dispatchers.IO) {
                Log.d("Wbj_", "async thread: ${Thread.currentThread().name}")
                return@async FileUtil.loadPets(resources)
            }
            val recAdapter = MainRecAdapter(this@MainActivity, async.await())
            recyclerView.adapter = recAdapter
        }*/

        /*lifecycleScope.launch(Dispatchers.IO) {
            val petList = suspendCoroutine {
                Log.d("Wbj_", "suspendCoroutine thread: ${Thread.currentThread().name}")
                it.resume(FileUtil.loadPets(resources))
            }

            withContext(Dispatchers.Main) {
                val recAdapter = MainRecAdapter(this@MainActivity, petList)
                recyclerView.adapter = recAdapter
            }
        }*/

    }

}