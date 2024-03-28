package com.example.demoadmin.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.demoadmin.Model.Rating
import com.example.demoadmin.Model.RatingAdapterListener
import com.example.demoadmin.R

class RatingAdapter(private val context: Context, private val ratingList: List<Rating>, private val listener: RatingAdapterListener) : RecyclerView.Adapter<RatingAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ratingTextView: TextView = itemView.findViewById(R.id.TxFeedBAck)
        val feedbackImageView: ImageView = itemView.findViewById(R.id.ImgFeedBack)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_rating, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rating = ratingList[position]
        //holder.ratingTextView.text = rating.rating.toString()

        if (listener.isFeedbackActivity()) {
            holder.feedbackImageView.setImageResource(R.drawable.chat)
            holder.ratingTextView.text = rating.feedback.toString()
        } else {
            holder.feedbackImageView.setImageResource(R.drawable.good_feedback)
            holder.ratingTextView.text = rating.rating.toString()
        }
    }

    override fun getItemCount(): Int {
        return ratingList.size
    }
}
