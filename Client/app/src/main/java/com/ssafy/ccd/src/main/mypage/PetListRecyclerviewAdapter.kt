package com.ssafy.ccd.src.main.mypage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.ccd.R
import com.ssafy.ccd.databinding.ItemPetListBinding
import com.ssafy.ccd.src.dto.Pet

class PetListRecyclerviewAdapter(private val petList: List<Pet>) : RecyclerView.Adapter<PetListRecyclerviewAdapter.PetListViewHolder>() {


    inner class PetListViewHolder(private val binding: ItemPetListBinding) : RecyclerView.ViewHolder(binding.root) {
        val petImage = binding.itemCvPetImage

        fun bind(pet: Pet, position: Int) {
            binding.pet = pet
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PetListViewHolder {
        return PetListViewHolder(DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.item_pet_list, parent, false))
    }

    override fun onBindViewHolder(holder: PetListViewHolder, position: Int) {
        val pet = petList[position]
        holder.apply {
            bind(pet, position)
            petImage.setOnClickListener {
                itemClickListener.onClick(it, position, pet)
            }
        }
    }

    override fun getItemCount(): Int {
        return petList.size
    }

    interface ItemClickListener{
        fun onClick(view: View, position: Int, pet: Pet)
    }

    private lateinit var itemClickListener : ItemClickListener

    fun setItemClickListener(itemClickListener: ItemClickListener){
        this.itemClickListener = itemClickListener
    }
}