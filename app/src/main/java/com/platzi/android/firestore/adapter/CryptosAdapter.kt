package com.platzi.android.firestore.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.platzi.android.firestore.R
import com.platzi.android.firestore.model.Crypto
import com.squareup.picasso.Picasso

class CryptosAdapter(val cryptosAdapterListener: CryptosAdapterListener) :
    RecyclerView.Adapter<CryptosAdapter.ViewHolder>() {

    val cryptoList: List<Crypto> = ArrayList()

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val image = view.findViewById<ImageView>(R.id.image)
        val name = view.findViewById<TextView>(R.id.nameTextView)
        val available = view.findViewById<TextView>(R.id.availableTextView)
        val buyButton = view.findViewById<TextView>(R.id.buyButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder = ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.crypto_row, parent, false)
    )


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val crypto = cryptoList[position]
        Picasso.get().load(crypto.imageUrl).into(holder.image)
        holder.name.text = crypto.name
        holder.available.text = holder.itemView.context.getString(R.string.available_message, crypto.available.toString())
        holder.buyButton.setOnClickListener {
            cryptosAdapterListener.onBuyCryptoClicked(crypto)
        }
    }

    override fun getItemCount(): Int = cryptoList.size
}