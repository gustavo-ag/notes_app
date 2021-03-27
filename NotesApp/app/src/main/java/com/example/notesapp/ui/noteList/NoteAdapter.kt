package com.example.notesapp.ui.noteList

import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.notesapp.R
import com.example.notesapp.data.entity.NoteEntity
import com.example.notesapp.databinding.NoteAdapterBinding

class NoteAdapter : ListAdapter<NoteEntity, NoteAdapter.NoteViewHolder>(DIFF_CALLBACK) {

    var gotItemClickListener: ((NoteEntity) -> Unit)? = null
    var gotItemLongClickListener: ((NoteEntity) -> Boolean)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        return NoteViewHolder.create(parent, gotItemClickListener, gotItemLongClickListener)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class NoteViewHolder(
            private val itemBinding: NoteAdapterBinding,
            private val gotItemClickListener: ((NoteEntity) -> Unit)?,
            private val gotItemLongClickListener: ((NoteEntity) -> Boolean)?
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(note: NoteEntity) {
            itemBinding.run {
                textTitle.text = note.title
                textSubtitle.text = note.subtitle
                textDateTime.text = note.dateTime
                val gradientDrawable = layoutNote.background as GradientDrawable
                if (note.color.isNotEmpty()){
                    gradientDrawable.setColor(Color.parseColor(note.color))
                } else {
                    gradientDrawable.setColor(Color.parseColor("#333333"))
                }

                if (note.imagePath.isNotEmpty()){
                    imageNote.setImageBitmap(BitmapFactory.decodeFile(note.imagePath))
                    imageNote.visibility = View.VISIBLE
                } else {
                    imageNote.visibility = View.GONE
                }

                layoutNote.setOnClickListener {
                    gotItemClickListener?.invoke(note)
                }

                layoutNote.setOnLongClickListener {
                    gotItemLongClickListener?.invoke(note)
                    val result = gotItemLongClickListener?.invoke(note) ?: false
                    result
                }
            }
        }

        companion object {
            fun create(parent: ViewGroup, gotItemClickListener: ((NoteEntity) -> Unit)?, gotItemLongClickListener: ((NoteEntity) -> Boolean)?): NoteViewHolder {
                val itemBinding = NoteAdapterBinding
                        .inflate(LayoutInflater.from(parent.context), parent, false)

                return NoteViewHolder(itemBinding, gotItemClickListener, gotItemLongClickListener)
            }
        }
    }

    companion object {

        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<NoteEntity>() {
            override fun areContentsTheSame(oldItem: NoteEntity, newItem: NoteEntity): Boolean {
                return oldItem == newItem
            }

            override fun areItemsTheSame(oldItem: NoteEntity, newItem: NoteEntity): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}