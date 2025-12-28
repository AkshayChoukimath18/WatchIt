package com.example.watchit.ui.homepage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.watchit.R
import com.example.watchit.domain.model.Movie
import com.google.android.material.imageview.ShapeableImageView

class HorizontalRowAdapter(
    private val onClick:(Movie) -> Unit
): RecyclerView.Adapter<HorizontalRowAdapter.ViewHolder>() {

    private val items = mutableListOf<Movie>()

    fun submitList(list: List<Movie>){
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    /*
    * val dislplayMetrics = parent.context.resources.displayMetrics
        val itemWidth = (dislplayMetrics.widthPixels / 3f).toInt()
        view.layoutParams = view.layoutParams.apply {
            width = itemWidth
        }
    * */

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HorizontalRowAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row_poster_small, parent, false)
        val dislplayMetrics = parent.context.resources.displayMetrics
        val itemWidth = (dislplayMetrics.widthPixels / 3f).toInt()
        view.layoutParams = view.layoutParams.apply {
            width = itemWidth
        }
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: HorizontalRowAdapter.ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val imagePoster = view.findViewById<ShapeableImageView>(R.id.imagePoster)
        private val title = view.findViewById<TextView>(R.id.textTitle)

        fun bind(item: Movie){
            imagePoster.load(item.posterUrl){
                crossfade(true)
            }
            title.text = item.title

        }
    }
}