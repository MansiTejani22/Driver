package com.example.demoadmin.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.demoadmin.Model.Request
import com.example.demoadmin.R

class RequestAdapter(
    private val context: Context,
    private val requestList: List<Request>,
    private val listener: RequestActionListener
) :
    RecyclerView.Adapter<RequestAdapter.RequestViewHolder>() {

    interface RequestActionListener {
        fun onApprovalClicked(request: Request)
        fun onRejectClicked(request: Request)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.user_data_list, parent, false)
        return RequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val request = requestList[position]
        holder.TxPhoneNo.text = "PhoneNo: ${request.currentUserPhone}" // Set driver's phone number
        holder.TxAddress.text = "Address: ${request.currentLocation}" // Set current location
        holder.TxStatus.text = "Status: ${request.status}" // Set status

        holder.btnApproval.setOnClickListener {
            listener.onApprovalClicked(request)
        }

        holder.btnReject.setOnClickListener {
            listener.onRejectClicked(request)
        }
    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    class RequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val TxPhoneNo: TextView = itemView.findViewById(R.id.TxPhoneNo)
        val TxAddress: TextView = itemView.findViewById(R.id.TxAddress)
        val TxStatus: TextView = itemView.findViewById(R.id.TxStatus)
        val btnApproval: Button = itemView.findViewById(R.id.btnApproval)
        val btnReject: Button = itemView.findViewById(R.id.btnReject)
    }
}
