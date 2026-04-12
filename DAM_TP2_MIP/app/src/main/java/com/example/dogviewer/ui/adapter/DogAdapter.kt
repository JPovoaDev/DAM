package com.example.dogviewer.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dogviewer.R
import com.example.dogviewer.model.ImageItem

/**
 * Adapter responsible for binding Dog API images to the RecyclerView grid/list.
 */
class DogAdapter(private var images: List<ImageItem> = emptyList()) :
    RecyclerView.Adapter<DogAdapter.DogViewHolder>() {

    class DogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dogImageView: ImageView = itemView.findViewById(R.id.dogImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_dog_image, parent, false)
        return DogViewHolder(view)
    }

    override fun onBindViewHolder(holder: DogViewHolder, position: Int) {
        val item = images[position]
        
        // Asynchronously load the URL string into the ImageView via Glide with a smooth cross-fade transition
        Glide.with(holder.itemView.context)
            .load(item.url)
            .transition(com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade())
            .placeholder(R.color.surfaceVariant)
            .error(android.R.drawable.stat_notify_error)
            .into(holder.dogImageView)

        // Setup click listener to navigate to ImageDetailsActivity seamlessly
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = android.content.Intent(context, com.example.dogviewer.ImageDetailsActivity::class.java).apply {
                // Pass the dog API parsed URL logic exactly how ImageDetailsActivity requires it
                putExtra("EXTRA_IMAGE_URL", item.url)
                putExtra("EXTRA_IMAGE_TITLE", item.title)
                putExtra("EXTRA_IMAGE_BREED", item.breed)
                putExtra("EXTRA_IMAGE_SUBBREED", item.subBreed)
                putExtra("EXTRA_IMAGE_ID", item.id)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = images.size

    /**
     * Replaces the old data set and recalculates the list.
     */
    fun updateData(newImages: List<ImageItem>) {
        images = newImages
        notifyDataSetChanged()
    }
}
