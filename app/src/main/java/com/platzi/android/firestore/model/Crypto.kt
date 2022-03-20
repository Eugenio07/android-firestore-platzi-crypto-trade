package com.platzi.android.firestore.model

data class Crypto(
    var name: String = "",
    var imageUrl: String = "",
    var available: Int = 0
) {
    fun getDocumentID(): String = name.lowercase()
}
