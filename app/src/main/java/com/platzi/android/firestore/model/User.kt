package com.platzi.android.firestore.model

data class User(
    var username: String = "",
    var cryptoList: List<Crypto>? = null
)
