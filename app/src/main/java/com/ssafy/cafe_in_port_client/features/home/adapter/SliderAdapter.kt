package com.ssafy.cafe_in_port_client.features.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.ssafy.cafe_in_port_client.R

class SliderAdapter(
    private val images: List<Int>,
    private val clickListener: ((Int) -> Unit)? = null
) : RecyclerView.Adapter<SliderAdapter.SliderViewHolder>() {

    inner class SliderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.sliderImage)

        fun bind(imageResId: Int) {
            imageView.setImageResource(imageResId)
            clickListener?.let { listener ->
                imageView.setOnClickListener {
                    listener(imageResId)
                }
            } ?: run {
                imageView.setOnClickListener(null)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.slider_item, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        holder.bind(images[position])
    }

    override fun getItemCount(): Int = images.size
}