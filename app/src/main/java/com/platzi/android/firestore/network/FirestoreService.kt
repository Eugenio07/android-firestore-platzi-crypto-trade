package com.platzi.android.firestore.network

import com.google.firebase.firestore.FirebaseFirestore
import com.platzi.android.firestore.model.Crypto
import com.platzi.android.firestore.model.User

const val CYRPTO_COLLECTION_NAME ="cryptos"
const val USERS_COLLECTION_NAME ="users"

class FirestoreService(val firebaseFirestore: FirebaseFirestore) {
    fun setDocument(data: Any, collectionName: String, id: String, callback: Callback<Unit>){
        firebaseFirestore.collection(collectionName).document(id).set(data)
            .addOnSuccessListener { callback.onSuccess(null) }
            .addOnFailureListener { callback.onFailed(it) }
    }

    fun updateUser(user: User, callback: Callback<User>){
        firebaseFirestore.collection(USERS_COLLECTION_NAME).document(user.username)
            .update("cryptoList", user.cryptoList)
            .addOnSuccessListener { callback.onSuccess(user) }
            .addOnFailureListener { callback.onFailed(it) }
    }

    fun updateCrypto(crypto: Crypto){
        firebaseFirestore.collection(CYRPTO_COLLECTION_NAME).document(crypto.getDocumentID())
            .update("available", crypto.available)
    }
}