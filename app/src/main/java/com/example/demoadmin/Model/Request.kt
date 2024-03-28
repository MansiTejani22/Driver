package com.example.demoadmin.Model

/*data class Request(
    val key: String? = null, // Add the key property
    val currentLocation: String? = null,
    val currentUserPhone: String? = null,
    val status: String? = null
)*/

data class Request(
    val key: String? = null,
    val currentLocation: String? = null,
    val currentUserPhone: String? = null,
    val status: String? = null,
    val driverApproval: Boolean? = null // Add the driverApproval property
)


