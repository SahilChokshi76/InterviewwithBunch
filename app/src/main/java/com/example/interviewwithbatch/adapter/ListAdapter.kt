package com.example.interviewwithbatch.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.interviewwithbatch.R
import com.example.interviewwithbatch.databinding.CustomRowBinding
import com.example.interviewwithbatch.model.User

class ListAdapter: RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    private var userList = emptyList<User>()

    class MyViewHolder(val binding: CustomRowBinding): RecyclerView.ViewHolder(binding.root) {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val inflater = LayoutInflater.from(parent.context)

        val binding = CustomRowBinding.inflate(inflater, parent, false)

        return MyViewHolder(binding)

    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = userList[position]

        holder.binding.empIdTV.text = currentItem.id.toString()
        holder.binding.empNameTV.text = currentItem.firstName
    }

    fun setData(user: List<User>) {
        this.userList = user
        notifyDataSetChanged()
    }
}