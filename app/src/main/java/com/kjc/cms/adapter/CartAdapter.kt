package com.kjc.cms.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kjc.cms.R
import com.kjc.cms.model.CartComponent

class CartAdapter() : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val differCallBack = object: DiffUtil.ItemCallback<CartComponent>() {
        override fun areItemsTheSame(oldItem: CartComponent, newItem: CartComponent): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CartComponent, newItem: CartComponent): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this,differCallBack)

    fun saveData( dataResponse: List<CartComponent>){
        differ.submitList(dataResponse)
    }

    class CartViewHolder(itemVew: View): RecyclerView.ViewHolder(itemVew){
        val componentName: TextView = itemVew.findViewById(R.id.cart_item_name)
        val componentQuantity: TextView = itemVew.findViewById(R.id.cart_item_quantity)
        val componentImage: ImageView = itemVew.findViewById(R.id.cart_item_image)
        val increment: ImageView = itemVew.findViewById(R.id.cart_increment)
        val decrement: ImageView = itemVew.findViewById(R.id.cart_decrement)
        val delete: ImageView = itemVew.findViewById(R.id.deleteItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartAdapter.CartViewHolder {
        return CartAdapter.CartViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.cart_item, parent, false))
    }

    override fun onBindViewHolder(holder: CartAdapter.CartViewHolder, position: Int) {
        val cartItem = differ.currentList[position]
        holder.componentQuantity.text = cartItem.quantity.toString()
        holder.componentName.text = cartItem.name
        Glide.with(holder.itemView.context).load(cartItem.image).into(holder.componentImage)
        holder.delete.setOnClickListener{
            onDeleteClick?.invoke(cartItem)
        }
        holder.decrement.setOnClickListener{
//            holder.componentQuantity.text = (--cartItem.quantity).toString()
            onDecrementClick?.invoke(cartItem)
        }
        holder.increment.setOnClickListener{
//            holder.componentQuantity.text = (++cartItem.quantity).toString()
            onIncrementClick?.invoke(cartItem)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    var onDeleteClick :((CartComponent) -> Unit)? = null

    var onDecrementClick :((CartComponent) -> Unit)? = null

    var onIncrementClick :((CartComponent) -> Unit)? = null
}