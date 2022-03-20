package com.platzi.android.firestore.network

import com.google.firebase.firestore.FirebaseFirestore
import com.platzi.android.firestore.model.Crypto
import com.platzi.android.firestore.model.User

const val CRYPTO_COLLECTION_NAME = "cryptos"
const val USERS_COLLECTION_NAME = "users"

class FirestoreService(val firebaseFirestore: FirebaseFirestore) {
    fun setDocument(data: Any, collectionName: String, id: String, callback: Callback<Void>) {
        firebaseFirestore.collection(collectionName).document(id).set(data)
            .addOnSuccessListener { callback.onSuccess(null) }
            .addOnFailureListener { callback.onFailed(it) }
    }

    fun updateUser(user: User, callback: Callback<User>) {
        firebaseFirestore.collection(USERS_COLLECTION_NAME).document(user.username)
            .update("cryptoList", user.cryptoList)
            .addOnSuccessListener { callback.onSuccess(user) }
            .addOnFailureListener { callback.onFailed(it) }
    }

    fun updateCrypto(crypto: Crypto) {
        firebaseFirestore.collection(CRYPTO_COLLECTION_NAME).document(crypto.getDocumentID())
            .update("available", crypto.available)
    }

    fun getCryptos(callback: Callback<List<Crypto>>) {
        firebaseFirestore.collection(CRYPTO_COLLECTION_NAME)
            .get()
            .addOnSuccessListener {
                    callback.onSuccess(it.toObjects(Crypto::class.java))
            }
            .addOnFailureListener { callback.onFailed(it) }
    }

    fun findUserByID(id: String, callback: Callback<User>) {
        firebaseFirestore.collection(USERS_COLLECTION_NAME).document(id)
            .get()
            .addOnSuccessListener {
                if (it.data != null) {
                    callback.onSuccess(it.toObject(User::class.java))
                }else{
                    callback.onSuccess(null)
                }
            }
            .addOnFailureListener { callback.onFailed(it) }
    }
}