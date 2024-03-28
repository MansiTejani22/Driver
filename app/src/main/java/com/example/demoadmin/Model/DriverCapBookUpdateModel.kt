package com.example.demoadmin.Model

class Location(
    val area: String?,
    val city: String?,
    val country: String?
) {
    // No-argument constructor required for Firebase
    constructor() : this(null, null, null)
}

class DriverCapBookUpdateModel {
    var driverId: String? = null
    var name: String? = null
    var phoneNo: String? = null
    var drivingLicenseNo: String? = null
    var vehicleRc: String? = null
    var aadhaarCardNo: String? = null
    var panCard: String? = null
    var bankAccountNo: String? = null
    var IFSCcode: String? = null
    var nameOfBank: String? = null
    var location: Location? = null
    var fcmToken: String? = null
    var ticketId: String? = null

    // No-argument constructor
    constructor()

    constructor(
        driverId: String?,
        name: String?,
        phoneNo: String?,
        drivingLicenseNo: String?,
        vehicleRc: String?,
        aadhaarCardNo: String?,
        panCard: String?,
        bankAccountNo: String?,
        IFSCcode: String?,
        nameOfBank: String?,
        location: Location?,
        fcmToken: String?,
        ticketId: String?
    ) {
        this.driverId = driverId
        this.name = name
        this.phoneNo = phoneNo
        this.drivingLicenseNo = drivingLicenseNo
        this.vehicleRc = vehicleRc
        this.aadhaarCardNo = aadhaarCardNo
        this.panCard = panCard
        this.bankAccountNo = bankAccountNo
        this.IFSCcode = IFSCcode
        this.nameOfBank = nameOfBank
        this.location = location
        this.fcmToken = fcmToken
        this.ticketId = ticketId
    }
}
