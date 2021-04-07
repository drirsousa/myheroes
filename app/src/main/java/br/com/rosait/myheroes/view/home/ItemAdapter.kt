package br.com.rosait.myheroes.view.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import br.com.rosait.myheroes.R
import br.com.rosait.myheroes.common.model.ItemCharacters
import br.com.rosait.myheroes.databinding.ItemLayoutBinding
import br.com.rosait.myheroes.common.entity.ItemEntity
import br.com.rosait.myheroes.view.detail.ItemDetailActivity
import br.com.rosait.myheroes.view.detail.ItemDetailActivity.Companion.ITEM
import com.squareup.picasso.Picasso
import java.util.*
import kotlin.collections.ArrayList

class ItemAdapter(var items: ArrayList<Any>, val callback:(item: Any, save: Boolean) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var originalList = items
    private var itemList = items
    private val ITEM_LIST = 0
    private val LOADING = 1
    var isLoadingAdded = false

    private var itemFavoriteList = listOf<Long>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View

        return when (viewType) {
            ITEM_LIST -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
                ViewHolder(view)
            }
            else -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_progress, parent, false)
                LoadingVH(view)
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if(position == itemList.size - 1 && isLoadingAdded) LOADING else ITEM_LIST
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(getItemViewType(position) == ITEM_LIST) {
            val item = itemList[position]
            (holder as ViewHolder).bind(item, itemFavoriteList, callback)
            holder.itemView.setOnClickListener {
                val intent = Intent(it.context, ItemDetailActivity::class.java)

                when (item) {
                    is ItemCharacters -> intent.putExtra(ITEM, item)
                    is ItemEntity -> intent.putExtra(ITEM, item)
                }

                it.context.startActivity(intent)
            }
        }
    }

    fun add(item: Any) {
        itemList.add(item)
        if (!originalList.contains(item)) originalList.add(item)
        notifyItemInserted(itemList.size - 1)
    }

    fun addAll(announceList: List<Any>) {
        announceList.forEach {
            add(it)
        }
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        add(ItemCharacters())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        val position = itemList.size - 1
        if(position >= 0) {
            val item = getItem(position)
            if(item != null) {
                itemList.removeAt(position)
                notifyItemRemoved(position)
            }
        }
    }

    private fun getItem(position: Int) : ItemCharacters? {
        return itemList.get(position) as ItemCharacters
    }

    fun updateList(newList: ArrayList<Any>) {
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
            setFilteredList(ArrayList(originalList.filterIsInstance<ItemCharacters>().filter {
                it.name.toLowerCase(Locale.ROOT).contains(filter.toLowerCase(Locale.ROOT))
            }))
        } else {
            setFilteredList(originalList)
        }
    }

    private fun setFilteredList(filteredList: ArrayList<Any>) {
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

    class LoadingVH(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        override fun onClick(view: View?) {}
    }
}