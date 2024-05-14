package com.suheng.structure.view.activity

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.suheng.structure.view.R

class ConstraintLayoutActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_constraint_layout)
        findViewById<ViewGroup>(R.id.root_layout).run {
            addView(createTitleLayout(this@ConstraintLayoutActivity, "Title, Title, Title"))
            addView(
                createTitleLayout(
                    this@ConstraintLayoutActivity,
                    "Title, Title, Title",
                    isAttachTitleArrow = true
                )
            )
            addView(
                createTitleLayout(
                    this@ConstraintLayoutActivity,
                    "Title, Title, Title, Title, Title, Title, Title, Title, Title",
                    isAttachTitleArrow = true
                )
            )
            addView(
                createTitleLayout(
                    this@ConstraintLayoutActivity,
                    "Title, Title, Title, Title, Title, Title, Title, Title, Title",
                    "Subtitle, Subtitle, Subtitle",
                    true
                )
            )

            addView(
                createTitleLayout(
                    this@ConstraintLayoutActivity,
                    "Title, Title, Title, Title, Title, Title, Title, Title, Title",
                    "Subtitle, Subtitle, Subtitle, Subtitle, Subtitle, Subtitle, Subtitle, Subtitle, Subtitle, Subtitle, Subtitle, Subtitle, Subtitle, Subtitle",
                    true
                )
            )

            addView(
                createTitleLayout(
                    this@ConstraintLayoutActivity,
                    "Title, Title, Title, Title",
                    "Subtitle, Subtitle, Subtitle",
                )
            )
        }
    }

    private fun createTitleLayout(
        context: Context,
        title: CharSequence,
        subtitle: CharSequence? = "",
        isAttachTitleArrow: Boolean = false,
        arrow: Drawable? = null
    ): ViewGroup {
        val displayMetrics = resources.displayMetrics
        return ConstraintLayout(context).apply {
            val metrics = resources.displayMetrics
            val height = ViewGroup.LayoutParams.WRAP_CONTENT
            /*val height =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80f, metrics).toInt()*/
            val marginTop =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6f, metrics).toInt()
            layoutParams = ViewGroup.MarginLayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, height
            ).apply { topMargin = marginTop }
            setBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_orange_dark))
            val padding =
                TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, displayMetrics)
                    .toInt()
            setPadding(padding, padding, padding, padding)
        }.also {
            it.takeUnless { TextUtils.isEmpty(title) }?.let { _ ->
                val subtitleIsEmpty = TextUtils.isEmpty(subtitle)

                it.addView( //Title
                    TextView(context).apply {
                        id = R.id.title_layout_tv_title
                        maxLines = 1
                        ellipsize = TextUtils.TruncateAt.END
                        setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                        text = title
                    }, ConstraintLayout.LayoutParams(
                        if (isAttachTitleArrow) ViewGroup.LayoutParams.WRAP_CONTENT else 0,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                        if (isAttachTitleArrow) {
                            horizontalChainStyle = ConstraintLayout.LayoutParams.CHAIN_PACKED
                            constrainedWidth = true
                            horizontalBias = 0f
                            endToStart = R.id.title_layout_iv_arrow_down
                        } else {
                            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                        }
                        topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                        if (subtitleIsEmpty) {
                            bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                        } else {
                            verticalChainStyle = ConstraintLayout.LayoutParams.CHAIN_PACKED
                            bottomToTop = R.id.title_layout_tv_subtitle
                        }

                        /*marginStart = TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_DIP,
                            6f,
                            resources.displayMetrics
                        ).toInt()*/
                    })

                (if (isAttachTitleArrow) {
                    arrow ?: ContextCompat.getDrawable(context, android.R.drawable.ic_media_pause)
                } else null)?.let { drawable ->
                    it.addView( // up or down arrow
                        ImageView(context).apply {
                            id = R.id.title_layout_iv_arrow_down
                            setImageDrawable(drawable)
                        }, ConstraintLayout.LayoutParams(
                            ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                        ).apply {
                            startToEnd = R.id.title_layout_tv_title
                            endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                            topToTop = R.id.title_layout_tv_title
                            bottomToBottom = R.id.title_layout_tv_title
                        })
                }

                it.takeUnless { subtitleIsEmpty }?.addView( //Subtitle
                    TextView(context).apply {
                        id = R.id.title_layout_tv_subtitle
                        maxLines = 2
                        ellipsize = TextUtils.TruncateAt.END
                        text = subtitle
                    }, ConstraintLayout.LayoutParams(
                        0, ViewGroup.LayoutParams.WRAP_CONTENT
                    ).apply {
                        startToStart = R.id.title_layout_tv_title
                        endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
                        topToBottom = R.id.title_layout_tv_title
                        bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                    }
                )
            }
        }
    }

}