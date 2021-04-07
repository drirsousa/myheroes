package br.com.rosait.myheroes.common.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class InfiniteScrollListener(layoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {
    private var mLayoutManager = layoutManager

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val visibleItemCount = mLayoutManager.childCount
        val itemTotalCount = mLayoutManager.itemCount
        val firstVisibleItemPosition = mLayoutManager.findFirstVisibleItemPosition()

        if(!isLoading() && !isLastPage()) {
            if ((visibleItemCount + firstVisibleItemPosition) >= itemTotalCount && firstVisibleItemPosition >= 0) {
                loadMoreItems()
            }
        }
    }

    protected abstract fun loadMoreItems()
    abstract fun getTotalPageCount() : Int
    abstract fun isLastPage() : Boolean
    abstract fun isLoading() : Boolean
}