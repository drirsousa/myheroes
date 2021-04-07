package br.com.rosait.myheroes.view.home.main

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.rosait.myheroes.R
import br.com.rosait.myheroes.common.base.BaseActivity
import br.com.rosait.myheroes.common.di.App
import br.com.rosait.myheroes.common.di.App.Companion.mLimit
import br.com.rosait.myheroes.common.di.AppModule
import br.com.rosait.myheroes.common.di.DaggerAppComponent
import br.com.rosait.myheroes.common.model.ItemCharacters
import br.com.rosait.myheroes.common.utils.InfiniteScrollListener
import br.com.rosait.myheroes.common.utils.ProgressUtil
import br.com.rosait.myheroes.databinding.ActivityMainBinding
import br.com.rosait.myheroes.view.home.ItemAdapter
import br.com.rosait.myheroes.view.home.favorite.FavoriteActivity
import br.com.rosait.myheroes.viewmodel.ItemViewModel
import javax.inject.Inject

class MainActivity : BaseActivity() {

    companion object {
        const val OFFSET_START = 0
    }

    private val component by lazy { DaggerAppComponent.builder().appModule(
        AppModule(
            App(),
            application
        )
    ).build() }

    private lateinit var mBinding: ActivityMainBinding
    private var isLoading = false
    private var isLastPage = false
    private var totalPages = 0
    private var currentOffSet =
        OFFSET_START

    @Inject
    lateinit var mViewModel: ItemViewModel

    private val itemList by lazy { listOf<ItemCharacters>() }
    private val mAdapter by lazy {
        ItemAdapter(ArrayList(itemList)) { item, save ->
            if (save)
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
        val layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)

        setupToolbar()
        initRecyclerView(layoutManager)
        setupFavorites()
        setupItems()
        setTotalPages()
        //setupLoading()
        setupErrorMessage()
        setupFavoriteButton()
        setListener()

        mViewModel.getFavorites()

        mBinding.rvItems.addOnScrollListener(object : InfiniteScrollListener(layoutManager) {
            override fun loadMoreItems() {
                isLoading = true
                currentOffSet += mLimit
                loadNextItems()
            }

            override fun getTotalPageCount(): Int {
                return totalPages
            }

            override fun isLastPage(): Boolean {
                return isLastPage
            }

            override fun isLoading(): Boolean {
                return isLoading
            }
        })

        loadFirstTime()
    }

    private fun setupToolbar() {
        val actionBar = supportActionBar
        actionBar?.title = getString(R.string.lbl_item_list_toolbar)
    }

    private fun initRecyclerView(layoutManager: LinearLayoutManager) {
        mBinding.rvItems.layoutManager = layoutManager
        mBinding.rvItems.itemAnimator = DefaultItemAnimator()
        mBinding.rvItems.adapter = mAdapter
    }

    private fun loadFirstTime() {
        ProgressUtil.showProgressDialog(this)
        mViewModel.getItems(OFFSET_START)
    }

    private fun loadNextItems() {
        mViewModel.getItems(currentOffSet)
    }

    private fun setupItems() {
        mViewModel.mItemList.observe(this, Observer { items ->
            if(currentOffSet.equals(OFFSET_START)) {
                ProgressUtil.hideProgressDialog()
                setItems(items)
                if (currentOffSet <= totalPages) mAdapter.addLoadingFooter() else isLastPage = true
            } else {
                mAdapter.removeLoadingFooter()
                isLoading = false
                setItems(items)
                if(currentOffSet != totalPages) mAdapter.addLoadingFooter() else isLastPage = true
            }
        })
    }

    private fun setTotalPages() {
        mViewModel.mTotal.observe(this, Observer { total ->
            totalPages = total / mLimit
        })
    }

    private fun setItems(items: List<ItemCharacters>) {
        mAdapter.addAll(ArrayList(items))
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