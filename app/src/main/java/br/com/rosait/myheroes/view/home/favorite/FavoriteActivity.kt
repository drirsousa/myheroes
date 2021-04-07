package br.com.rosait.myheroes.view.home.favorite

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import br.com.rosait.myheroes.R
import br.com.rosait.myheroes.common.base.BaseActivity
import br.com.rosait.myheroes.common.di.App
import br.com.rosait.myheroes.common.di.AppModule
import br.com.rosait.myheroes.common.di.DaggerAppComponent
import br.com.rosait.myheroes.common.model.ItemCharacters
import br.com.rosait.myheroes.databinding.ActivityFavoriteBinding
import br.com.rosait.myheroes.view.home.ItemAdapter
import br.com.rosait.myheroes.viewmodel.ItemViewModel
import javax.inject.Inject

class FavoriteActivity : BaseActivity() {

    private val component by lazy { DaggerAppComponent.builder().appModule(
        AppModule(
            App(),
            application
        )
    ).build() }

    private lateinit var mBinding: ActivityFavoriteBinding

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
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_favorite)
        mBinding.lifecycleOwner = this
        mBinding.executePendingBindings()

        component.inject(this)

        setupToolbar()
        initRecyclerView()
        setupFavorites()
        setupLoading()
        setupErrorMessage()

        mViewModel.getFavorites()
    }

    private fun setupToolbar() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.title = getString(R.string.lbl_item_favorite_list_toolbar)
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

    private fun initRecyclerView() {
        mBinding.rvFavoriteItems.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        mBinding.rvFavoriteItems.adapter = mAdapter
    }

    private fun setItems(items: ArrayList<Any>) {
        mAdapter.updateList(items)
    }

    private fun setupLoading() {
        mViewModel.mLoading.observe(this, Observer { loading ->
            mBinding.pbFavoriteItem.visibility = if(loading) View.VISIBLE else View.GONE
        })
    }

    private fun setupFavorites() {
        mViewModel.getFavorites().observe(this, Observer { favorites ->
            setItems(ArrayList(favorites))
            mViewModel.setLoading(false)
        })
    }

    private fun setupErrorMessage() {
        mViewModel.mErrorMessage.observe(this, Observer { message ->
            showMessage(message)
        })
    }
}