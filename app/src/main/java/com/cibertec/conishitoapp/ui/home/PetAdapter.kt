package com.cibertec.conishitoapp.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cibertec.conishitoapp.R
import com.cibertec.conishitoapp.data.Pet
import java.text.Normalizer

private val DIACRITIC_REGEX = "\\p{Mn}+".toRegex()
private val NON_ALPHANUMERIC_REGEX = "[^a-z0-9]".toRegex()

class PetAdapter(
    private val onClick: (Pet) -> Unit,
    private val onToggleFavorite: (Pet) -> Unit
) : ListAdapter<Pet, PetAdapter.VH>(DIFF) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.row_pet, parent, false)
        return VH(v, onClick, onToggleFavorite)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

    class VH(
        itemView: View,
        private val onClick: (Pet) -> Unit,
        private val onToggleFavorite: (Pet) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val tvName = itemView.findViewById<TextView>(R.id.tvName)
        private val tvInfo = itemView.findViewById<TextView>(R.id.tvInfo)
        private val tvCity = itemView.findViewById<TextView>(R.id.tvCity)
        private val btnFavorite = itemView.findViewById<ImageButton>(R.id.btnFavorite)
        private val ivPhoto = itemView.findViewById<ImageView>(R.id.ivPhoto)
        private var current: Pet? = null

        init {
            itemView.setOnClickListener { current?.let(onClick) }
            btnFavorite.setOnClickListener { current?.let(onToggleFavorite) }
        }

        fun bind(item: Pet) {
            current = item
            tvName.text = item.name
            tvInfo.text = "${item.type} • ${item.age}"
            tvCity.text = item.city
            val imageRes = resolvePetImageResource(itemView, item)
            ivPhoto.setImageResource(imageRes)
            ivPhoto.contentDescription =
                itemView.context.getString(R.string.pet_photo_description, item.name)
            if (item.isFavorite) {
                btnFavorite.setImageResource(R.drawable.ic_favorite_24)
                btnFavorite.contentDescription =
                    itemView.context.getString(R.string.remove_favorite)
            } else {
                btnFavorite.setImageResource(R.drawable.ic_favorite_border_24)
                btnFavorite.contentDescription =
                    itemView.context.getString(R.string.favorite)
            }
        }
    }

    private object DIFF : DiffUtil.ItemCallback<Pet>() {
        override fun areItemsTheSame(old: Pet, new: Pet) = old.id == new.id
        override fun areContentsTheSame(old: Pet, new: Pet) = old == new
    }
}

private fun resolvePetImageResource(view: View, pet: Pet): Int {
    val context = view.context
    val resources = context.resources
    val packageName = context.packageName

    val candidates = mutableListOf<String>()
    pet.photoUrl?.takeIf { it.isNotBlank() }?.let { candidates.add(it) }
    candidates.add(pet.name)

    for (candidate in candidates) {
        val normalized = normalizeDrawableName(candidate)
        if (normalized.isNotEmpty()) {
            val resId = resources.getIdentifier(normalized, "drawable", packageName)
            if (resId != 0) return resId
        }
    }

    return R.drawable.ic_paw
}

private fun normalizeDrawableName(value: String): String {
    val base = Normalizer.normalize(value, Normalizer.Form.NFD)
    val withoutDiacritics = DIACRITIC_REGEX.replace(base, "")
    val sanitized = withoutDiacritics.lowercase()
        .substringAfterLast('/')
        .substringBeforeLast('.')
    return NON_ALPHANUMERIC_REGEX.replace(sanitized, "")
}
