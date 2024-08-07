package com.kjc.cms.ui.fragments.home

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import com.kjc.cms.R
import com.kjc.cms.adapter.HomeAdapter
import com.kjc.cms.databinding.FragmentHomeBinding
import com.kjc.cms.model.CartComponent
import com.kjc.cms.model.Component
import com.kjc.cms.ui.SecondaryActivity

class HomeFragment(private var homeItemList: ArrayList<Component>, private var cartItems: ArrayList<CartComponent>) : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeAdapter: HomeAdapter
    private var homeRecycler : RecyclerView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeRecycler?.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
        homeAdapter = HomeAdapter()
        binding.homeGridRecycler.adapter = homeAdapter
        homeAdapter.onItemClick = { component ->
            var isPresent = false
            cartItems.forEach { item ->
                if (item.id == component.Id){
                    isPresent = true
                }
            }
            if (isPresent) {
                openDialogBox("Item already added to cart")
            } else if (component.AvailableQuantity.toInt() > 0) {
                //opens component details if it is available
                val intent = Intent(context, SecondaryActivity::class.java)
                intent.putExtra("Data", Gson().toJson(component))
                intent.putExtra("fragName", "EachComponent")
                startActivity(intent)
            } else {
                openDialogBox()
            }
        }
        fetchItems()
    }

    private fun openDialogBox(text:String? = null) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.item_not_available_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val okBtn: TextView = dialog.findViewById(R.id.ok_btn_item_not_available)
        if (text!=null){
            val dialogText: TextView = dialog.findViewById(R.id.dialogText)
            dialogText.text = text
        }
        okBtn.setOnClickListener{
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun fetchItems(){
        FirebaseFirestore.getInstance().collection("Components").get().addOnSuccessListener { result ->
            for (doc in result) {
                val component = Component(
                    Id = doc.id,
                    AvailableQuantity = doc.data["AvailableQuantity"].toString(),
                    Image = doc.data["Image"].toString(),
                    LastUpdated = doc.data["LastUpdated"].toString(),
                    Model = doc.data["Model"].toString(),
                    Name = doc.data["Name"].toString(),
                    Price = doc.data["Price"].toString(),
                    Quantity = doc.data["Quantity"].toString()
                )
                homeItemList.add(component)
            }
            homeAdapter.saveData(homeItemList)
        }.addOnFailureListener { exception ->
            Log.d("FireStore Error", exception.toString())
        }
    }
}