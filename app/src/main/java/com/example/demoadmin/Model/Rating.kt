package com.example.demoadmin.Model


/*
data class Rating(val id: String?, val rating: Int)*/



data class Rating(val id: String?, val feedback: String, val rating: Int)


// Rating.kt
/*data class Rating(
    val feedback: String? = null,
    val rating: Int? = null
)

{
    constructor() : this("", 0)
}*/
/*data class RatingItem(val rating: Int, val imageResourceId: Int)*/
