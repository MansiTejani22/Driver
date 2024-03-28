package com.example.demoadmin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.demoadmin.Model.DriverCapBookUpdateModel
import com.example.demoadmin.Model.Location
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var etName: TextInputEditText
    private lateinit var etPhoneNo: TextInputEditText
    private lateinit var etDrivingLicenseNo: TextInputEditText
    private lateinit var etVehicleRc: TextInputEditText
    private lateinit var etAadhaarCardNo: TextInputEditText
    private lateinit var etPanCard: TextInputEditText
    private lateinit var etBankAccountNo: TextInputEditText
    private lateinit var etIFSCcode: TextInputEditText
    private lateinit var etNameOfBank: TextInputEditText
    private lateinit var etAddress: TextInputEditText
    private lateinit var btnUpdate: Button

    private lateinit var currentUserPhoneNo: String
    private lateinit var databaseReference: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        etName = findViewById(R.id.EtName)
        etPhoneNo = findViewById(R.id.EtPhoneNo)
        etAadhaarCardNo = findViewById(R.id.EtAadhaarCardNo)
        etDrivingLicenseNo = findViewById(R.id.EtDrivingLicenseNo)
        etPanCard = findViewById(R.id.EtPanCard)
        etBankAccountNo = findViewById(R.id.EtBankAccountNo)
        etIFSCcode = findViewById(R.id.EtIFSCcode)
        etAddress = findViewById(R.id.ETAddress)
        etNameOfBank = findViewById(R.id.EtNameOfBank)
        etVehicleRc = findViewById(R.id.ETVehicleRc)
        btnUpdate = findViewById(R.id.BtnUpdate)

        // Get current user's phone number
        currentUserPhoneNo = FirebaseAuth.getInstance().currentUser?.phoneNumber ?: ""

        // Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("DriverRegistration")

        // Load user data from database
        loadUserData()

        // Update button click listener
        btnUpdate.setOnClickListener {
            updateProfile()
        }
    }

    private fun loadUserData() {
        // Query to get the current user's data
        val query = databaseReference.orderByChild("phoneNo").equalTo(currentUserPhoneNo)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (data in snapshot.children) {
                        val user = data.getValue(DriverCapBookUpdateModel::class.java)
                        user?.let {
                            etName.setText(it.name)
                            etPhoneNo.setText(it.phoneNo)
                            etAadhaarCardNo.setText(it.aadhaarCardNo)
                            etDrivingLicenseNo.setText(it.drivingLicenseNo)
                            etPanCard.setText(it.panCard)
                            etBankAccountNo.setText(it.bankAccountNo)
                            etIFSCcode.setText(it.IFSCcode)
                            etAddress.setText("${it.location?.area}, ${it.location?.city}, ${it.location?.country}")
                            etNameOfBank.setText(it.nameOfBank)
                            etVehicleRc.setText(it.vehicleRc)
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle database error
            }
        })
    }

    private fun updateProfile() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        userId?.let { uid ->
            val newAadhaarCardNo = etAadhaarCardNo.text.toString()
            val newDrivingLicenseNo = etDrivingLicenseNo.text.toString()
            val newPanCard = etPanCard.text.toString()
            val newBankAccountNo = etBankAccountNo.text.toString()
            val newIFSCcode = etIFSCcode.text.toString()
            val newAddress = etAddress.text.toString()
            val newNameOfBank = etNameOfBank.text.toString()
            val newVehicleRc = etVehicleRc.text.toString()

            // Create a map to hold the updates
            val updates = HashMap<String, Any>()
            updates["aadhaarCardNo"] = newAadhaarCardNo
            updates["drivingLicenseNo"] = newDrivingLicenseNo
            updates["panCard"] = newPanCard
            updates["bankAccountNo"] = newBankAccountNo
            updates["IFSCcode"] = newIFSCcode
            updates["nameOfBank"] = newNameOfBank
            updates["location/area"] = newAddress.split(", ")[0] // Update only area
            updates["location/city"] = newAddress.split(", ")[1] // Update only city
            updates["location/country"] = newAddress.split(", ")[2] // Update only country
            updates["vehicleRc"] = newVehicleRc

            // Get the reference to the current user's node
            val currentUserRef = databaseReference.child(uid)

            // Update the data in the database
            currentUserRef.updateChildren(updates)
                .addOnSuccessListener {
                    // Handle successful update
                    Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    // Handle update failure
                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
        }
    }



}
