package com.testrsg

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.testrsg.databinding.ItemRandomStringBinding

class RandomStringAdapter(
    private val items: MutableList<RandomStringData>,
    private val onDeleteClick: (position: Int) -> Unit
) : RecyclerView.Adapter<RandomStringAdapter.RandomStringViewHolder>() {

    inner class RandomStringViewHolder(val binding: ItemRandomStringBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.btnDeleteItem.setOnClickListener {
                onDeleteClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RandomStringViewHolder {
        val binding = ItemRandomStringBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return RandomStringViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RandomStringViewHolder, position: Int) {
        val item = items[position]
        holder.binding.apply {
            tvRandomString.text = item.value
            tvLength.text = "Length: ${item.length}"
            tvCreated.text = "Created: ${item.created}"
        }
    }

    override fun getItemCount(): Int = items.size

    fun removeAt(position: Int) {
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun clearAll() {
        items.clear()
        notifyDataSetChanged()
    }

    fun addItem(data: RandomStringData) {
        items.add(0, data) // Add to the top so the newest is shown first
        notifyItemInserted(0)
    }
}