package com.notes.notesproxmlviews

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.card.MaterialCardView

class NoteAdapter(options: FirestoreRecyclerOptions<Note>, var context: Context) :
    FirestoreRecyclerAdapter<Note, NoteAdapter.NoteViewHolder>(options) {

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int, note: Note) {
        holder.titleTextView.text = note.title
        holder.contentTextView.text = note.content
        holder.timestampTextView.text = Utility.timestampToString(note.timestamp)

        // 1. Dynamic Pastel Color Styling
        val colorHex = note.colorHex ?: "#FFFFFF"
        try {
            holder.noteCardView.setCardBackgroundColor(Color.parseColor(colorHex))
        } catch (e: Exception) {
            holder.noteCardView.setCardBackgroundColor(Color.parseColor("#FFFFFF"))
        }

        // 2. Decode and display base64 image (if any)
        if (!note.imageUrl.isNullOrEmpty()) {
            try {
                val decodedString = Base64.decode(note.imageUrl, Base64.DEFAULT)
                val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                if (decodedByte != null) {
                    holder.noteImageView.setImageBitmap(decodedByte)
                    holder.noteImageView.visibility = View.VISIBLE
                } else {
                    holder.noteImageView.visibility = View.GONE
                }
            } catch (e: Exception) {
                e.printStackTrace()
                holder.noteImageView.visibility = View.GONE
            }
        } else {
            holder.noteImageView.visibility = View.GONE
        }

        // 3. Display Chip tag (if any)
        if (!note.tag.isNullOrEmpty()) {
            holder.noteTagView.text = note.tag
            holder.noteTagView.visibility = View.VISIBLE
        } else {
            holder.noteTagView.visibility = View.GONE
        }

        // 4. Intent with premium fields to Details Editor
        holder.itemView.setOnClickListener {
            val currentPos = holder.bindingAdapterPosition
            if (currentPos != RecyclerView.NO_POSITION) {
                val intent = Intent(context, NoteDetailsActivity::class.java)
                intent.putExtra("title", note.title)
                intent.putExtra("content", note.content)
                intent.putExtra("imageUrl", note.imageUrl)
                intent.putExtra("colorHex", note.colorHex)
                intent.putExtra("tag", note.tag)
                val docId = snapshots.getSnapshot(currentPos).id
                intent.putExtra("docId", docId)
                context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_note_item, parent, false)
        return NoteViewHolder(view)
    }

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val noteCardView: MaterialCardView = itemView.findViewById(R.id.note_card_view)
        val noteImageView: ImageView = itemView.findViewById(R.id.note_image_view)
        val noteTagView: TextView = itemView.findViewById(R.id.note_tag_view)
        val titleTextView: TextView = itemView.findViewById(R.id.note_title_text_view)
        val contentTextView: TextView = itemView.findViewById(R.id.note_content_text_view)
        val timestampTextView: TextView = itemView.findViewById(R.id.note_timestamp_text_view)
    }
}