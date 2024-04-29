package com.example.alexey_kovalev_auii_drill_i

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.HorizontalScrollView
import androidx.appcompat.app.AppCompatActivity
import android.os.Handler

class MainActivity : AppCompatActivity() {

    private lateinit var horizontalScrollView: HorizontalScrollView
    private val handler = Handler()
    private val scrollSpeed: Long = 20
    private var scrollDirection = 1
    private val scrollAmount = 2

    private val scroller = object : Runnable {
        override fun run() {
            val maxScroll =
                horizontalScrollView.getChildAt(0).measuredWidth - horizontalScrollView.measuredWidth
            val currentScroll = horizontalScrollView.scrollX
            if (currentScroll >= maxScroll || currentScroll <= 0) {
                scrollDirection = -scrollDirection
            }
            horizontalScrollView.smoothScrollBy(scrollAmount * scrollDirection, 0)
            handler.postDelayed(this, scrollSpeed)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val decorView = window.decorView
        val uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
        decorView.systemUiVisibility = uiOptions
        setContentView(R.layout.activity_movie_lion)

        horizontalScrollView = findViewById(R.id.horizontalScrollView)

        // Start images auto-scrolling
        handler.postDelayed(scroller, scrollSpeed)

        val buttonGetTickets: Button = findViewById(R.id.buttonGetTickets)
        buttonGetTickets.setOnClickListener {
            val intent = Intent(this, TicketsPick::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
            onPause()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(scroller)
    }
}
