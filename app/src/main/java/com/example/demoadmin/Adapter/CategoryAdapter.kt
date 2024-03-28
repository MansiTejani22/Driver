package com.example.demoadmin.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.demoadmin.CapRegistrationFormActivity
import com.example.demoadmin.Model.CategoryModel
import com.example.demoadmin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView

class CategoryAdapter(private val context: Context, private val list: ArrayList<CategoryModel>) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val categoryModel = list[position]
        holder.txCategoryName.text = categoryModel.categoryName
        Picasso.get()
            .load(categoryModel.categoryImage)
            .placeholder(R.drawable.car)
            .into(holder.imgCategory)


       /* holder.itemView.setOnClickListener {
            // Here, you can open the registration form or perform any other action
            // For example, you can start a new activity:
            val intent = Intent(context, CapRegistrationFormActivity::class.java)
            context.startActivity(intent)
        }*/

        holder.itemView.setOnClickListener {
            val currentUser = FirebaseAuth.getInstance().currentUser
            val dbRef = FirebaseDatabase.getInstance().getReference("DriverRegistration")
            currentUser?.let { user ->
                dbRef.orderByChild("phoneNo").equalTo(user.phoneNumber).addListenerForSingleValueEvent(object :
                    ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            // User is already registered, show a message or perform any other action
                            Toast.makeText(context, "You are already registered", Toast.LENGTH_SHORT).show()
                        } else {
                            // User is not registered, open the registration form activity
                            val intent = Intent(context, CapRegistrationFormActivity::class.java)
                            context.startActivity(intent)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle database error
                        Toast.makeText(context, "Database Error: ${error.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imgCategory: CircleImageView = itemView.findViewById(R.id.ImgCategory)
        var txCategoryName: TextView = itemView.findViewById(R.id.TxCategoryName)
    }
}