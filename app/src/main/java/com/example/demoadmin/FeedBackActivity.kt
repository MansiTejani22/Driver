package com.example.demoadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.cardview.widget.CardView

class FeedBackActivity : AppCompatActivity()
{
    private lateinit var cardFeedBack : CardView
    private lateinit var cardRating:CardView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed_back)

        cardFeedBack = findViewById(R.id.CardFeedBack);
        cardRating = findViewById(R.id.CardRating)

        cardRating.setOnClickListener {
            // Handle click action for cardRating here
            // For example, you can start a new activity
            val intent = Intent(this, RetriveredUserRating::class.java)
            startActivity(intent)
        }

        cardFeedBack.setOnClickListener{
            val intent = Intent(this,RetriveredUserFeedBack::class.java)
            startActivity(intent)
        }


    }
}