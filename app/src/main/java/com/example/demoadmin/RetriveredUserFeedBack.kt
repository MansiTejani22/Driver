package com.example.demoadmin

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demoadmin.Adapter.RatingAdapter
import com.example.demoadmin.Model.Rating
import com.example.demoadmin.Model.RatingAdapterListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RetriveredUserFeedBack : AppCompatActivity(), RatingAdapterListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var ratingAdapter: RatingAdapter
    private val ratingList: MutableList<Rating> = mutableListOf()

    private lateinit var driverFeedbackRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrivered_user_feed_back)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Pass this activity as the listener
        ratingAdapter = RatingAdapter(this, ratingList, this)
        recyclerView.adapter = ratingAdapter

        driverFeedbackRef = FirebaseDatabase.getInstance().getReference("DriverFeedback")

        // Get the current Firebase user
        val currentUser = FirebaseAuth.getInstance().currentUser

        // Check if the current user is authenticated
        currentUser?.let { user ->
            // Retrieve the phone number of the current user
            val phoneNumber = user.phoneNumber

            // Retrieve the driver ID based on the phone number
            val capBookRef = FirebaseDatabase.getInstance().getReference("DriverRegistration")
            capBookRef.orderByChild("phoneNo").equalTo(phoneNumber).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (userSnapshot in dataSnapshot.children) {
                        val driverId = userSnapshot.child("driverId").getValue(String::class.java)

                        // Retrieve feedback data based on the driver ID
                        driverId?.let { id ->
                            driverFeedbackRef.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(feedbackSnapshot: DataSnapshot) {
                                    ratingList.clear()
                                    // Iterate through each child node under the driver's feedback
                                    for (snapshot in feedbackSnapshot.children) {
                                        val feedback = snapshot.child("feedback").getValue(String::class.java)

                                        // Add feedback to the list if it's not null
                                        feedback?.let {
                                            ratingList.add(Rating(null, it, 0))
                                        }
                                    }
                                    ratingAdapter.notifyDataSetChanged() // Notify adapter of data change
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    // Handle errors
                                    Log.e("Firebase", "Failed to read value.", databaseError.toException())
                                }
                            })
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle errors
                    Log.e("Firebase", "Failed to read value.", databaseError.toException())
                }
            })
        }
    }

    override fun isFeedbackActivity(): Boolean {
        return true
    }
}
