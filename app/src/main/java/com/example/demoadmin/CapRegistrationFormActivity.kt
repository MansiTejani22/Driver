package com.example.demoadmin

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.demoadmin.Model.DriverCapBookModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class CapRegistrationFormActivity : AppCompatActivity() {
    private lateinit var etName: TextInputEditText
    private lateinit var etPhoneNo: TextInputEditText
    private lateinit var etDrivingLicenseNo: TextInputEditText
    private lateinit var etVehicleRc: TextInputEditText
    private lateinit var etAadhaarCardNo: TextInputEditText
    private lateinit var etPanCard: TextInputEditText
    private lateinit var etBankAccountNo: TextInputEditText
    private lateinit var etIFSCcode: TextInputEditText
    private lateinit var etNameOfBank: TextInputEditText
    private lateinit var btnSubmit: Button
    private lateinit var getLocationSwitch: SwitchMaterial
    private lateinit var locationTextView: TextView
    private lateinit var dbRef: DatabaseReference
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cap_registration_form)
        auth = FirebaseAuth.getInstance()

        init()
        dbRef = FirebaseDatabase.getInstance().getReference("DriverRegistration")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        btnSubmit.setOnClickListener {
            saveDriverCapBookData()
        }

        getLocationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                requestLocation()
            } else {
                locationTextView.text = ""
            }
        }
    }

    private fun init() {
        etName = findViewById(R.id.EtName)
        etPhoneNo = findViewById(R.id.EtPhoneNo)
        etDrivingLicenseNo = findViewById(R.id.EtDrivingLicenseNo)
        etVehicleRc = findViewById(R.id.ETVehicleRc)
        etAadhaarCardNo = findViewById(R.id.EtAadhaarCardNo)
        etPanCard = findViewById(R.id.EtPanCard)
        etBankAccountNo = findViewById(R.id.EtBankAccountNo)
        etIFSCcode = findViewById(R.id.EtIFSCcode)
        etNameOfBank = findViewById(R.id.EtNameOfBank)
        btnSubmit = findViewById(R.id.BtnSubmit)
        getLocationSwitch = findViewById(R.id.GetLocation)
        locationTextView = findViewById(R.id.TxLocation)
    }

    @SuppressLint("MissingPermission")
    private fun requestLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val address = getAddressFromLocation(location.latitude, location.longitude)
                locationTextView.text = address
            } else {
                Toast.makeText(this, "Unable to fetch location", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            Toast.makeText(this, "Error getting location: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getAddressFromLocation(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)!!
        val address = addresses[0]
        val area = address.subLocality ?: ""
        val city = address.locality ?: ""
        val country = address.countryName ?: ""
        return "$area, $city, $country"
    }

    private fun saveDriverCapBookData() {
        val name = etName.text.toString()
        val phoneNo = etPhoneNo.text.toString()
        val drivingLicenseNo = etDrivingLicenseNo.text.toString()
        val vehicleRc = etVehicleRc.text.toString()
        val aadhaarCardNo = etAadhaarCardNo.text.toString()
        val panCard = etPanCard.text.toString()
        val bankAccountNo = etBankAccountNo.text.toString()
        val ifscCode = etIFSCcode.text.toString()
        val nameOfBank = etNameOfBank.text.toString()
        val location = locationTextView.text.toString()

        if (name.isBlank() || phoneNo.isBlank() || drivingLicenseNo.isBlank() || vehicleRc.isBlank() ||
            aadhaarCardNo.isBlank() || panCard.isBlank() || bankAccountNo.isBlank() || ifscCode.isBlank() ||
            nameOfBank.isBlank() || location.isBlank()
        ) {
            Toast.makeText(this, "Fields can't be blank", Toast.LENGTH_SHORT).show()
            return
        }

        val currentUser = auth.currentUser
        val currentUserPhoneNumber = currentUser?.phoneNumber
        if (currentUserPhoneNumber != phoneNo) {
            Toast.makeText(this, "Phone numbers don't match", Toast.LENGTH_SHORT).show()
            return
        }

        val driverId = dbRef.push().key!!
        val ticketId = generateTicket()
        val capbookModel = DriverCapBookModel(
            driverId, name, phoneNo, drivingLicenseNo, vehicleRc, aadhaarCardNo,
            panCard, bankAccountNo, ifscCode, nameOfBank, location, null, ticketId
        )

        val locationParts = location.split(", ")
        val area = if (locationParts.size > 0) locationParts[0] else ""
        val city = if (locationParts.size > 1) locationParts[1] else ""
        val country = if (locationParts.size > 2) locationParts[2] else ""

        val locationMap = mapOf(
            "area" to area,
            "city" to city,
            "country" to country
        )

        dbRef.child(driverId).setValue(capbookModel)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    dbRef.child(driverId).child("location").setValue(locationMap)
                        .addOnCompleteListener {
                            Toast.makeText(this, "Data Inserted Successfully", Toast.LENGTH_SHORT).show()
                            clearEditTextFields()
                            startActivity(Intent(this, HomeActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener { err ->
                            Toast.makeText(this, "Error: ${err.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(this, "Data Insertion Failed", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { err ->
                Toast.makeText(this, "Error: ${err.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun clearEditTextFields() {
        etName.text?.clear()
        etPhoneNo.text?.clear()
        etDrivingLicenseNo.text?.clear()
        etVehicleRc.text?.clear()
        etAadhaarCardNo.text?.clear()
        etPanCard.text?.clear()
        etBankAccountNo.text?.clear()
        etIFSCcode.text?.clear()
        etNameOfBank.text?.clear()
        locationTextView.text = ""
    }

    private fun generateTicket(): String {
        val random = Random()
        return "TICKET-${random.nextInt(10000)}"
    }
}
