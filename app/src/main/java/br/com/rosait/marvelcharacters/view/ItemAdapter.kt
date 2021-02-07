package br.com.rosait.marvelcharacters.view

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import br.com.rosait.marvelcharacters.R
import br.com.rosait.marvelcharacters.common.model.ItemCharacters
import br.com.rosait.marvelcharacters.databinding.ItemLayoutBinding
import br.com.rosait.marvelcharacters.common.entity.ItemEntity
import br.com.rosait.marvelcharacters.view.ItemDetailActivity.Companion.ITEM
import com.squareup.picasso.Picasso
import java.util.*

class ItemAdapter(var items: List<Any>, val callback:(item: Any, save: Boolean) -> Unit) : RecyclerView.Adapter<ItemAdapter.ViewHolder>() {

    private var originalList = items
    private var itemList = items

    private var itemFavoriteList = listOf<Long>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item, itemFavoriteList, callback)
        holder.itemView.setOnClickListener {
            val intent = Intent(it.context, ItemDetailActivity::class.java)

            when (item) {
                is ItemCharacters -> intent.putExtra(ITEM, item)
                is ItemEntity -> intent.putExtra(ITEM, item)
            }

            it.context.startActivity(intent)
        }
    }

    fun updateList(newList: List<Any>) {
        this.itemList = newList
        this.originalList = newList
        notifyDataSetChanged()
    }

    fun updateItemFavoriteList(favoriteList: List<Long>) {
        itemFavoriteList = favoriteList
        notifyDataSetChanged()
    }

    fun filterItems(filter: String) {
        if(filter.isNotEmpty()) {
            setFilteredList(originalList.filterIsInstance<ItemCharacters>().filter {
                it.name.toLowerCase(Locale.ROOT).contains(filter.toLowerCase(Locale.ROOT))
            })
        } else {
            setFilteredList(originalList)
        }
    }

    private fun setFilteredList(filteredList: List<Any>) {
        itemList = filteredList
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding: ItemLayoutBinding? = DataBindingUtil.bind(itemView)

        fun bind(item: Any, favoriteList: List<Long>, callback:(item: Any, save: Boolean) -> Unit) {

            when (item) {

                is ItemCharacters -> {
                    setName(item.name)
                    if(favoriteList.contains(item.id))
                        setFavoriteIconDrawable(R.drawable.ic_favorite)
                    else
                        setFavoriteIconDrawable(R.drawable.ic_favorite_border)

                    item.thumbnail?.let {
                        if(it.extension.isNotEmpty() && it.path.isNotEmpty()) {
                            setImage("${it.path}.${it.extension}")
                        }
                    }
                }

                is ItemEntity -> {
                    setName(item.name)
                    setFavoriteIconDrawable(R.drawable.ic_favorite)

                    if(item.thumbnail.isNotEmpty()) setImage(item.thumbnail)
                }
            }

            setFavoriteIconClick(item, callback)
        }

        private fun setName(name: String) {
            name.let { binding?.txtName?.text = it }
        }

        private fun setImage(imagePath: String) {
            imagePath.let {
                if(it.isNotEmpty()) {
                    Picasso.get()
                        .load(it)
                        .placeholder(R.drawable.ic_placeholder)
                        .into(binding?.imvPicture)
                }
            }
        }

        private fun setFavoriteIconDrawable(drawableId: Int) {
            binding?.imvFavorite?.tag = drawableId
            binding?.imvFavorite?.setImageDrawable(itemView.context.resources.getDrawable(drawableId))
        }

        private fun setFavoriteIconClick(item: Any, callback:(item: Any, save: Boolean) -> Unit) {
            binding?.imvFavorite?.setOnClickListener {
                if(binding.imvFavorite.tag == R.drawable.ic_favorite_border) {
                    binding.imvFavorite.setImageDrawable(it.context.resources.getDrawable(R.drawable.ic_favorite))
                    binding.imvFavorite.tag = R.drawable.ic_favorite
                    callback(item, true)
                } else {
                    binding.imvFavorite.setImageDrawable(it.context.resources.getDrawable(R.drawable.ic_favorite_border))
                    binding.imvFavorite.tag = R.drawable.ic_favorite_border
                    callback(item, false)
                }
            }
        }
    }
}