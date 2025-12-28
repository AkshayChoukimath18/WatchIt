package com.example.watchit.ui.libraryDetails

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.watchit.R
import com.example.watchit.domain.model.Movie

class LibraryDetailAdapter(
    private val onClick: (Movie) -> Unit
): ListAdapter<Movie, LibraryDetailAdapter.ViewHolder>(Diff) {

    inner class ViewHolder(view: View, onClick: (Movie) -> Unit): RecyclerView.ViewHolder(view){
        private val name = view.findViewById<TextView>(R.id.textLibraryName)
        private val description = view.findViewById<TextView>(R.id.textLibraryDescription)
        private var currentMovie: Movie? = null
        init {
            view.setOnClickListener {
                currentMovie?.let { onClick }
            }
        }

        fun bind(movie: Movie){
            Log.d("Adapter", movie.title)
            name.text = movie.title
            description.text = movie.sourceUrl
            currentMovie = movie
            onClick(movie)
        }
    }

    object Diff: DiffUtil.ItemCallback<Movie>(){
        override fun areItemsTheSame(
            oldItem: Movie,
            newItem: Movie
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Movie,
            newItem: Movie
        ): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): LibraryDetailAdapter.ViewHolder = ViewHolder(LayoutInflater.from(parent.context)
        .inflate(R.layout.item_library, parent, false), onClick
    )

    override fun onBindViewHolder(holder: LibraryDetailAdapter.ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}