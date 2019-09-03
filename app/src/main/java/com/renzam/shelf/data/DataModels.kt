package com.renzam.shelf.data

import com.google.firebase.firestore.FieldValue

data class DataModels(
    var userId: String,
    var shopCatogorey: String,
    var businessName: String,
    var ownerName: String,
    var ownerPhoneNum: String,
    var businessPlace: String,
    var url: String,
    var latitude: Double,
    var longitude: Double,
    var Date: FieldValue
)