package com.example.demoadmin.Model

class CategoryModel {
    var categoryName: String? = null
    var categoryImage: String? = null
    var key: String? = null
    var setRegistration = 0

    constructor(categoryName: String?, categoryImage: String?, key: String?, setRegistration: Int) {
        this.categoryName = categoryName
        this.categoryImage = categoryImage
        this.key = key
        this.setRegistration = setRegistration
    }


    constructor()
}