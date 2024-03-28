package com.example.demoadmin

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demoadmin.Adapter.RatingAdapter
import com.example.demoadmin.Model.Rating
import com.example.demoadmin.Model.RatingAdapterListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RetriveredUserRating : AppCompatActivity(), RatingAdapterListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var ratingAdapter: RatingAdapter
    private val ratingList: MutableList<Rating> = mutableListOf()

    private lateinit var ratingRef: DatabaseReference
    private lateinit var capBookRef: DatabaseReference
    private lateinit var driverFeedbackRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrivered_user_rating)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Pass this activity as the listener
        ratingAdapter = RatingAdapter(this, ratingList, this)
        recyclerView.adapter = ratingAdapter

        ratingRef = FirebaseDatabase.getInstance().getReference("DriverFeedback")
        capBookRef = FirebaseDatabase.getInstance().getReference("CapBook")
        driverFeedbackRef = FirebaseDatabase.getInstance().getReference("DriverFeedback")

        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val phoneNumber = currentUser.phoneNumber

            capBookRef.orderByChild("phoneNo").equalTo(phoneNumber).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (userSnapshot in dataSnapshot.children) {
                        val driverId = userSnapshot.child("driverId").getValue(String::class.java)

                        if (driverId != null) {
                            driverFeedbackRef.child(driverId).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(feedbackSnapshot: DataSnapshot) {
                                    ratingList.clear()
                                    for (snapshot in feedbackSnapshot.children) {
                                        val rating = snapshot.child("rating").getValue(Double::class.java)

                                        if (rating != null) {
                                            val ratingInt = (rating * 2).toInt()
                                            ratingList.add(Rating(null, "", ratingInt))
                                        }
                                    }
                                    ratingAdapter.notifyDataSetChanged()
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    Toast.makeText(
                                        this@RetriveredUserRating,
                                        "Error: " + databaseError.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                        } else {
                            // Handle the case where the driver ID doesn't exist for the phone number
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(
                        this@RetriveredUserRating,
                        "Error: " + databaseError.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    override fun isFeedbackActivity(): Boolean {
        return false
    }
}