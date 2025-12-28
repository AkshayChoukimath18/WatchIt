package com.example.watchit.ui.homepage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.watchit.R
import com.example.watchit.domain.model.Movie
import com.google.android.material.imageview.ShapeableImageView

class CarouselAdapter(
    private val onClick: (Movie) -> Unit
) : RecyclerView.Adapter<CarouselAdapter.ViewHolder>() {
    private val items = mutableListOf<Movie>()

    fun submitList(list: List<Movie>){
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CarouselAdapter.ViewHolder {
        val view  = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carousel_poster, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarouselAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val imagePoster = view.findViewById<ShapeableImageView>(R.id.imagePoster)
        private val textTitle = view.findViewById<TextView>(R.id.textTitle)

        fun bind(item: Movie){
            textTitle.text = item.title
            imagePoster.load(item.posterUrl){
                crossfade(true)
            }
            itemView.setOnClickListener { onClick(item) }
        }
    }
}