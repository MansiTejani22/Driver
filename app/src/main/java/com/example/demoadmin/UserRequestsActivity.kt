package com.example.demoadmin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.demoadmin.Adapter.RequestAdapter
import com.example.demoadmin.Model.Request
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class UserRequestsActivity : AppCompatActivity(), RequestAdapter.RequestActionListener {

    private lateinit var requestsRecyclerView: RecyclerView
    private lateinit var requestAdapter: RequestAdapter
    private lateinit var requestList: MutableList<Request>
    private lateinit var databaseReferenceRequests: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_requests)

        requestsRecyclerView = findViewById(R.id.requestsRecyclerView)
        requestsRecyclerView.layoutManager = LinearLayoutManager(this)
        requestList = mutableListOf()
        requestAdapter = RequestAdapter(this, requestList, this)
        requestsRecyclerView.adapter = requestAdapter

        databaseReferenceRequests = FirebaseDatabase.getInstance().getReference("Requests")

        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.let {
            val currentUserPhone = it.phoneNumber!!
            fetchRequests(currentUserPhone)
        }
    }

    private fun fetchRequests(currentUserPhone: String) {
        databaseReferenceRequests.orderByChild("driverPhone").equalTo(currentUserPhone)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    requestList.clear()
                    for (snapshot in dataSnapshot.children) {
                        val currentLocation = snapshot.child("currentLocation").getValue(String::class.java)
                        val status = snapshot.child("status").getValue(String::class.java)
                        val phone = snapshot.child("currentUserPhone").getValue(String::class.java)
                        val key = snapshot.key // Get the key of the request
                        requestList.add(Request(key, currentLocation, phone, status,))
                    }
                    requestAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle onCancelled event
                }
            })
    }

    override fun onApprovalClicked(request: Request) {
        val requestKey = request.key
        requestKey?.let {
            val updateData = mapOf<String, Any>("status" to "approval")
            databaseReferenceRequests.child(requestKey).updateChildren(updateData)
                .addOnSuccessListener {
                    // Status updated successfully
                    fetchRequests(FirebaseAuth.getInstance().currentUser?.phoneNumber!!)
                }
                .addOnFailureListener { exception ->
                    // Handle failure
                }
        }
    }

    override fun onRejectClicked(request: Request) {
        val requestKey = request.key
        requestKey?.let {
            val updateData = mapOf<String, Any>("status" to "rejected")
            databaseReferenceRequests.child(requestKey).updateChildren(updateData)
                .addOnSuccessListener {
                    // Status updated successfully
                    fetchRequests(FirebaseAuth.getInstance().currentUser?.phoneNumber!!)
                }
                .addOnFailureListener { exception ->
                    // Handle failure
                }
        }
    }
}
