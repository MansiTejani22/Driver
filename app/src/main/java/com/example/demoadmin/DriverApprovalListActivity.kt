package com.example.demoadmin

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demoadmin.Adapter.RequestAdapter
import com.example.demoadmin.Model.Request
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class DriverApprovalListActivity : AppCompatActivity(), RequestAdapter.RequestActionListener {

    private lateinit var requestsRecyclerView: RecyclerView
    private lateinit var requestAdapter: RequestAdapter
    private var requestList: MutableList<Request> = mutableListOf()
    private lateinit var databaseReferenceRequests: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_driver_approval_list)

        requestsRecyclerView = findViewById(R.id.requestsRecyclerView)
        requestsRecyclerView.layoutManager = LinearLayoutManager(this)
        requestAdapter = RequestAdapter(this, requestList, this)
        requestsRecyclerView.adapter = requestAdapter

        databaseReferenceRequests = FirebaseDatabase.getInstance().getReference("Requests")

        fetchDriverApprovalRequests()
    }

    private fun fetchDriverApprovalRequests() {
        val currentUserPhone = FirebaseAuth.getInstance().currentUser?.phoneNumber

        databaseReferenceRequests.orderByChild("driverPhone").equalTo(currentUserPhone)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    requestList.clear()
                    for (snapshot in dataSnapshot.children) {
                        val currentLocation = snapshot.child("currentLocation").getValue(String::class.java)
                        val status = snapshot.child("status").getValue(String::class.java)
                        val driverApproval = snapshot.child("driverApproval").getValue(Boolean::class.java)
                        val key = snapshot.key // Get the key of the request
                        if (status == "approval" && driverApproval == true) {
                            currentUserPhone?.let {
                                requestList.add(Request(key, currentLocation, it, status))
                            }
                        }
                    }
                    requestAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle onCancelled event
                    Log.e(TAG, "Database read cancelled: ${databaseError.message}")
                }
            })
    }

    override fun onApprovalClicked(request: Request) {
        request.key?.let { key ->
            val requestRef = FirebaseDatabase.getInstance().getReference("Requests").child(key)

            Log.d(TAG, "Updating driver approval for request with key: $key")

            // Update the driverApproval field to true
            requestRef.child("driverApproval").setValue(true)
                .addOnSuccessListener {
                    Log.d(TAG, "Driver approval updated successfully")
                    // You can perform any additional actions here if needed
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Error updating driver approval: ${e.message}")
                    // Handle the error accordingly
                }
        }
    }



    override fun onRejectClicked(request: Request) {
        // Implement reject functionality if needed
    }

    companion object {
        private const val TAG = "DriverApprovalList"
    }
}
