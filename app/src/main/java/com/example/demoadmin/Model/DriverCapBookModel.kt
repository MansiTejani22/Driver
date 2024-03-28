package com.example.demoadmin.Model

class DriverCapBookModel {
    var driverId: String? = null
    var name: String? = null // Changed Name to name (lowercase n)
    var phoneNo: String? = null // Changed PhoneNo to phoneNo (lowercase p)
    var drivingLicenseNo: String? = null // Changed DrivingLicenseNo to drivingLicenseNo (lowercase d and l)
    var vehicleRc: String? = null // Changed VehicleRc to vehicleRc (lowercase v and r)
    var aadhaarCardNo: String? = null
    var panCard: String? = null // Changed PanCard to panCard (lowercase p)
    var bankAccountNo: String? = null // Changed BankAccountNo to bankAccountNo (lowercase b and a)
    var IFSCcode: String? = null // Changed IFSCcode to IFSCcode (uppercase I)
    var nameOfBank: String? = null // Changed NameOfBank to nameOfBank (lowercase n)
    var location: String? = null
    var fcmToken: String? = null // Add FCM token field
    var ticketId: String? = null // Add ticketId field

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
        location: String?,
        fcmToken: String?,
        ticketId: String? // Add ticketId parameter
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
        this.ticketId = ticketId // Assign ticketId parameter to class field
    }
}
