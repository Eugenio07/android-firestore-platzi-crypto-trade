package com.platzi.android.firestore.ui.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.platzi.android.firestore.R
import com.platzi.android.firestore.adapter.CryptosAdapter
import com.platzi.android.firestore.adapter.CryptosAdapterListener
import com.platzi.android.firestore.model.Crypto
import com.platzi.android.firestore.model.User
import com.platzi.android.firestore.network.Callback
import com.platzi.android.firestore.network.FirestoreService
import com.platzi.android.firestore.network.RealtimeDataListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_trader.*
import java.lang.Exception


/**
 * @author Santiago Carrillo
 * 2/14/19.
 */
class TraderActivity : AppCompatActivity(), CryptosAdapterListener {

    lateinit var firestoreService: FirestoreService
    private val cryptosAdapter = CryptosAdapter(this)

    lateinit var username: String

     var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trader)
        firestoreService = FirestoreService(FirebaseFirestore.getInstance())

        username = intent.extras!![USERNAME_KEY].toString()

        usernameTextView.text = username

        configureRecyclerView()
        loadCryptos()

        fab.setOnClickListener { view ->
            Snackbar.make(view, getString(R.string.generating_new_cryptos), Snackbar.LENGTH_SHORT)
                .setAction("Info", null).show()
            generateRandomCryptos()
        }

    }

    private fun generateRandomCryptos() {
        for (crypto in cryptosAdapter.cryptoList){
            crypto.available += (1..10).random()
            firestoreService.updateCrypto(crypto)
        }
    }

    private fun configureRecyclerView() {
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = cryptosAdapter
    }

    fun showGeneralServerErrorMessage() {
        Snackbar.make(
            fab,
            getString(R.string.error_while_connecting_to_the_server),
            Snackbar.LENGTH_LONG
        )
            .setAction("Info", null).show()
    }

    override fun onBuyCryptoClicked(crypto: Crypto) {
        if(crypto.available > 0){
            for(userCrypto in user!!.cryptoList!!){
                if(userCrypto.name == crypto.name){
                    userCrypto.available++
                    break
                }
            }
            crypto.available--
            firestoreService.updateUser(user!!, null)
            firestoreService.updateCrypto(crypto)
        }

    }

    override fun loadCryptos() {
        firestoreService.getCryptos(object : Callback<List<Crypto>> {
            override fun onSuccess(cryptoList: List<Crypto>?) {
                firestoreService.findUserByID(username, object : Callback<User> {
                    override fun onSuccess(result: User?) {
                        user = result!!
                        if (user!!.cryptoList == null) {
                            val userCryptoList = mutableListOf<Crypto>()
                            for (crypto in cryptoList!!) {
                                val cryptoUser = Crypto()
                                cryptoUser.name = crypto.name
                                cryptoUser.imageUrl = crypto.imageUrl
                                cryptoUser.available = crypto.available
                                userCryptoList.add(cryptoUser)
                            }
                            user!!.cryptoList = userCryptoList
                            firestoreService.updateUser(user!!, null)
                        }
                        loadUserCryptos()
                        addRealtimeDatabaseListener(user!!, cryptoList!!)
                    }

                    override fun onFailed(exception: Exception) {
                        showGeneralServerErrorMessage()
                    }

                })
                this@TraderActivity.runOnUiThread {
                    cryptosAdapter.cryptoList = cryptoList!!
                    cryptosAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailed(exception: Exception) {
                showGeneralServerErrorMessage()
            }

        })


    }

    private fun addRealtimeDatabaseListener(user: User, cryptoList: List<Crypto>) {
        firestoreService.listenForUpdates(user, object : RealtimeDataListener<User>{
            override fun onDataChanged(updatedData: User) {
                this@TraderActivity.user = updatedData
                loadUserCryptos()
            }
            override fun onError(exception: Exception) {
                showGeneralServerErrorMessage()
            }

        })

        firestoreService.listenForUpdates(cryptoList, object : RealtimeDataListener<Crypto>{
            override fun onDataChanged(updatedData: Crypto) {
                var pos = 0
                for (crypto in cryptosAdapter.cryptoList){
                    if(crypto.name == updatedData.name){
                        crypto.available = updatedData.available
                        cryptosAdapter.notifyItemChanged(pos)
                    }
                    pos++
                }
            }
            override fun onError(exception: Exception) {
                showGeneralServerErrorMessage()
            }

        })

    }

    private fun loadUserCryptos() {
        runOnUiThread {
            if (!user!!.cryptoList.isNullOrEmpty()) {
                infoPanel.removeAllViews()
                for (crypto in user!!.cryptoList!!) {
                    addUserCryptoInfoRow(crypto)
                }

            }
        }
    }

    private fun addUserCryptoInfoRow(crypto: Crypto) {
        val view = LayoutInflater.from(this).inflate(R.layout.coin_info, infoPanel, false)
        view.findViewById<TextView>(R.id.coinLabel).text = getString(R.string.coin_info, crypto.name, crypto.available.toString())
        Picasso.get().load(crypto.imageUrl).into(view.findViewById<ImageView>(R.id.coinIcon))
        infoPanel.addView(view)
    }
}