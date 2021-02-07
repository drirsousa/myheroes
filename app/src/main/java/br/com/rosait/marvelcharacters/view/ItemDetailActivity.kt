package br.com.rosait.marvelcharacters.view

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import br.com.rosait.marvelcharacters.R
import br.com.rosait.marvelcharacters.common.base.BaseActivity
import br.com.rosait.marvelcharacters.common.di.App
import br.com.rosait.marvelcharacters.common.di.AppModule
import br.com.rosait.marvelcharacters.common.di.DaggerAppComponent
import br.com.rosait.marvelcharacters.common.entity.ItemEntity
import br.com.rosait.marvelcharacters.common.model.ItemCharacters
import br.com.rosait.marvelcharacters.databinding.ActivityItemDetailBinding
import br.com.rosait.marvelcharacters.viewmodel.ItemViewModel
import com.squareup.picasso.Picasso
import java.io.Serializable
import javax.inject.Inject

class ItemDetailActivity : BaseActivity() {

    companion object {
        const val ITEM = "item"
    }

    private val component by lazy { DaggerAppComponent.builder().appModule(
        AppModule(
            App(),
            application
        )
    ).build() }

    private lateinit var mBinding: ActivityItemDetailBinding
    private var mItemFavoriteList = listOf<Long>()

    @Inject
    lateinit var mViewModel: ItemViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_item_detail)
        mBinding.lifecycleOwner = this
        mBinding.executePendingBindings()

        component.inject(this)

        setupToolbar()
        setupFavorites()
        setupDetailItem()
        setupDetailFavoriteItem()
        setupLoading()
        setupErrorMessage()

        mViewModel.getFavorites()

        val item = intent?.extras?.getSerializable(ITEM)

        getItemDetail(item)
    }

    private fun getItemDetail(item: Serializable?) {
        when (item) {
            is ItemCharacters -> {
                item.let {
                    mViewModel.getDetailItem(it.id)
                }
            }
            is ItemEntity -> {
                item.let {
                    mViewModel.getDetailFavorite(it.id!!)
                }
            }
        }
    }

    private fun setupToolbar() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = getString(R.string.lbl_item_detail_toolbar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupDetailItem() {
        mViewModel.mItem.observe(this, Observer { item ->
            setItemViews(item.id,"${item.thumbnail.path}.${item.thumbnail.extension}", item.description, false)
        })
    }

    private fun setupDetailFavoriteItem() {
        mViewModel.mItemFavorite.observe(this, Observer { itemFavorite ->
            setItemViews(itemFavorite.id, itemFavorite.thumbnail, itemFavorite.description, true)
            mViewModel.setLoading(false)
        })
    }

    private fun setItemViews(id: Long, imagePath: String, description: String?, isItemEntity: Boolean) {
        imagePath.let {
            Picasso.get()
                .load(it)
                .placeholder(R.drawable.ic_placeholder)
                .into(mBinding.imvPicture)
        }

        description?.let {
            mBinding.txtDescription.text = it
        }

        if(isItemEntity)
            mBinding.imvFavorite.setImageDrawable(resources.getDrawable(R.drawable.ic_favorite))
        else
            setFavoriteIcon(id)
    }

    private fun setupLoading() {
        mViewModel.mLoading.observe(this, Observer { loading ->
            mBinding.pbItemDetail.visibility = if(loading) View.VISIBLE else View.GONE
        })
    }

    private fun setupFavorites() {
        mViewModel.getFavorites().observe(this, Observer { favorites ->
            setFavoriteItems(favorites.map { it.id })
        })
    }

    private fun setFavoriteItems(favoriteItems: List<Long>) {
        mItemFavoriteList = favoriteItems
    }

    private fun setFavoriteIcon(id: Long) {
        if(mItemFavoriteList.contains(id))
            mBinding.imvFavorite.setImageDrawable(resources.getDrawable(R.drawable.ic_favorite))
        else
            mBinding.imvFavorite.setImageDrawable(resources.getDrawable(R.drawable.ic_favorite_border))
    }

    private fun setupErrorMessage() {
        mViewModel.mErrorMessage.observe(this, Observer { message ->
            showMessage(message)
        })
    }
}