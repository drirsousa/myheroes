package br.com.rosait.marvelcharacters.view

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.rosait.marvelcharacters.R
import br.com.rosait.marvelcharacters.common.base.BaseActivity
import br.com.rosait.marvelcharacters.common.di.App
import br.com.rosait.marvelcharacters.common.di.AppModule
import br.com.rosait.marvelcharacters.common.di.DaggerAppComponent
import br.com.rosait.marvelcharacters.common.model.ItemCharacters
import br.com.rosait.marvelcharacters.databinding.ActivityMainBinding
import br.com.rosait.marvelcharacters.viewmodel.ItemViewModel
import javax.inject.Inject

class MainActivity : BaseActivity() {

    private val component by lazy { DaggerAppComponent.builder().appModule(
        AppModule(
            App(),
            application
        )
    ).build() }

    private lateinit var mBinding: ActivityMainBinding

    @Inject
    lateinit var mViewModel: ItemViewModel

    private val itemList by lazy { listOf<ItemCharacters>() }
    private val mAdapter by lazy {
        ItemAdapter(itemList) { item, save ->
            if(save)
                mViewModel.saveFavorite(item)
            else
                mViewModel.deleteFavorite(item)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mBinding.lifecycleOwner = this
        mBinding.executePendingBindings()

        component.inject(this)

        setupToolbar()
        initRecyclerView()
        setupFavorites()
        setupItems()
        setupLoading()
        setupErrorMessage()
        setupFavoriteButton()
        setListener()

        mViewModel.getFavorites()

        mViewModel.getItems()
    }

    private fun setupToolbar() {
        val actionBar = supportActionBar
        actionBar?.title = getString(R.string.lbl_item_list_toolbar)
    }

    private fun initRecyclerView() {
        mBinding.rvItems.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        mBinding.rvItems.adapter = mAdapter
    }

    private fun setupItems() {
        mViewModel.mItemList.observe(this, Observer { items ->
            setItems(items)
        })
    }

    private fun setItems(items: List<ItemCharacters>) {
        mAdapter.updateList(items)
    }

    private fun setFavoriteItems(favoriteItems: List<Long>) {
        mAdapter.updateItemFavoriteList(favoriteItems)
    }

    private fun setupFavorites() {
        mViewModel.getFavorites().observe(this, Observer { favorites ->
            setFavoriteItems(favorites.map { it.id })
        })
    }

    private fun setupLoading() {
        mViewModel.mLoading.observe(this, Observer { loading ->
            mBinding.pbItem.visibility = if(loading) View.VISIBLE else View.GONE
        })
    }

    private fun setupFavoriteButton() {
        mBinding.fabFavorite.setOnClickListener {
           it.context.startActivity(Intent(it.context, FavoriteActivity::class.java))
        }
    }

    private fun setupErrorMessage() {
        mViewModel.mErrorMessage.observe(this, Observer { message ->
            showMessage(message)
        })
    }

    private fun setListener() {
        mBinding.edtSearch.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                mAdapter.filterItems(mBinding.edtSearch.text.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }
}