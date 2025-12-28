package com.example.watchit.ui.homepage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.watchit.R
import com.example.watchit.domain.model.LibraryWithMoviesDomain
import com.example.watchit.domain.model.Movie

class HomeSelectionAdapter(
    private val viewPool: RecyclerView.RecycledViewPool = RecyclerView.RecycledViewPool(),
    private val onMovieClick: (Movie) -> Unit
): RecyclerView.Adapter<HomeSelectionAdapter.ViewHolder>() {

    private val sections = mutableListOf<LibrarySection>()

    fun submitList(newSection: List<LibrarySection>){
        sections.clear()
        sections.addAll(newSection)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomeSelectionAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home_section, parent, false)
        return ViewHolder(view, viewPool, onMovieClick)
    }

    override fun onBindViewHolder(holder: HomeSelectionAdapter.ViewHolder, position: Int) {
        holder.bind(sections[position])
    }

    override fun getItemCount(): Int {
        return sections.size
    }

    inner class ViewHolder(itemView: View,
        private val sharedPool: RecyclerView.RecycledViewPool,
        private val onClick: (Movie) -> Unit): RecyclerView.ViewHolder(itemView){
            private val title = itemView.findViewById<TextView>(R.id.textSectionTitle)
            private val recycler = itemView.findViewById<RecyclerView>(R.id.recyclerSectionMovies)
            private val horizontalRowAdapter = HorizontalRowAdapter(onClick)

            init{
                recycler.apply {
                    layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
                    adapter = horizontalRowAdapter
                    setRecycledViewPool(sharedPool)
                }
            }
        fun bind(section: LibrarySection){
            title.text = section.libraryTitle
            horizontalRowAdapter.submitList(section.movies)
        }
        }

}