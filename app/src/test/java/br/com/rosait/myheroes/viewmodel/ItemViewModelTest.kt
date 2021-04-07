package br.com.rosait.myheroes.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.rosait.myheroes.common.entity.ItemEntity
import br.com.rosait.myheroes.common.model.ItemCharacters
import br.com.rosait.myheroes.common.model.ResponseData
import br.com.rosait.myheroes.common.model.ResponseResult
import br.com.rosait.myheroes.common.model.ThumbnailItem
import br.com.rosait.myheroes.service.ItemService
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.*
import org.junit.Assert.*
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations

@RunWith(JUnit4::class)
class ItemViewModelTest {

    @Rule
    @JvmField
    val rule : TestRule = InstantTaskExecutorRule()

    @Mock
    lateinit var mItemServiceMock: ItemService

    @Mock
    lateinit var mListResponseResultMock: List<ResponseResult>

    @Mock
    lateinit var mItemMock: ItemCharacters

    @Mock
    lateinit var mContext: Context

    lateinit var mViewModel: ItemViewModel

    @Before
    fun setUp() {
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline()}
        MockitoAnnotations.initMocks(this)

        mViewModel = ItemViewModel(mContext)
        mViewModel.mItemService = mItemServiceMock
    }

    @After
    fun tearDown() {
        verifyNoMoreInteractions(mItemServiceMock, mListResponseResultMock, mItemMock, mContext)
    }

    @Test
    fun test_getItems_WhenSuccess() {
        //Given
        val offset = 0
        val viewModelSpy = spy(mViewModel)
        val itemResponse = Single.just(
            ResponseResult(
                0, "",
                ResponseData(
                    0,
                    listOf(
                        ItemCharacters(
                            1, "first item", "first item description"
                            , ThumbnailItem("path", "jpg")
                        )
                    )
                )
            )
        )
        var response = mock(ResponseResult::class.java)
        itemResponse.subscribe { result -> response = result }

        `when`(mItemServiceMock.getItems(offset)).thenReturn(itemResponse)
        doNothing().`when`(viewModelSpy).setData(response)
        doNothing().`when`(viewModelSpy).setLoading(ArgumentMatchers.anyBoolean())

        //Act
        viewModelSpy.getItems(offset)

        //Assert
        verify(viewModelSpy, times(1)).getItems(offset)
        verify(viewModelSpy, never()).logError(ArgumentMatchers.anyString())
        verify(mItemServiceMock, times(1)).getItems(offset)
        verify(viewModelSpy, times(1)).setData(response)
        verifyNoMoreInteractions(viewModelSpy)
    }

    @Test
    fun test_getItems_WhenError() {
        //Given
        val offset = 0
        val errorMessage = "Error Message"
        val throwable = Throwable(errorMessage)
        val viewModelSpy = spy(mViewModel)
        val responseResult = mock(ResponseResult::class.java)

        `when`(mItemServiceMock.getItems(offset)).thenReturn(Single.error(throwable))
        doNothing().`when`(viewModelSpy).setLoading(ArgumentMatchers.anyBoolean())
        doNothing().`when`(viewModelSpy).logError(errorMessage)

        //Act
        viewModelSpy.getItems(offset)

        //Assert
        verify(viewModelSpy, times(1)).getItems(offset)
        verify(viewModelSpy, never()).setData(responseResult)
        verify(mItemServiceMock, times(1)).getItems(offset)
        verify(viewModelSpy, times(1)).logError(errorMessage)
        verifyNoMoreInteractions(viewModelSpy)
    }

    @Test
    fun test_getFavorites() {
        //Given
        val viewModelSpy = spy(mViewModel)
        val itemFavoriteList: LiveData<List<ItemEntity>> = MutableLiveData()
        `when`(mItemServiceMock.getFavorites()).thenReturn(itemFavoriteList)
        doNothing().`when`(viewModelSpy).setLoading(true)

        //Act
        val itemFavoriteListResult = viewModelSpy.getFavorites()

        //Assert
        assertEquals(itemFavoriteList, itemFavoriteListResult)
        verify(viewModelSpy, times(1)).getFavorites()
        verify(viewModelSpy, times(1)).setLoading(true)
        verify(mItemServiceMock, times(1)).getFavorites()
        verifyNoMoreInteractions(viewModelSpy)
    }

    @Test
    fun test_getDetailFavorite() {
        //Given
        val id = 10L
        val itemEntity = ItemEntity(id, "Item Entity", "Item Entity Description", "thumbnail")
        val viewModelSpy = spy(mViewModel)
        `when`(mItemServiceMock.getDetailFavorite(id)).thenReturn(itemEntity)
        doNothing().`when`(viewModelSpy).setItemFavorite(itemEntity)

        //Act
        viewModelSpy.getDetailFavorite(id)

        //Assert
        verify(viewModelSpy, times(1)).getDetailFavorite(id)
        verify(mItemServiceMock, times(1)).getDetailFavorite(id)
        verify(viewModelSpy, times(1)).setItemFavorite(itemEntity)
        verifyNoMoreInteractions(viewModelSpy)
    }

    @Test
    fun test_setData() {
        //Given
        val viewModelSpy = spy(mViewModel)
        val total = 10
        val itemList = listOf(mItemMock)
        val responseResult = ResponseResult(0, "", ResponseData(total, itemList))

        //Act
        viewModelSpy.setData(responseResult)

        //Assert
        verify(viewModelSpy, times(1)).setData(responseResult)
        verify(viewModelSpy, times(1)).setTotalItems(total)
        verify(viewModelSpy, times(1)).setItemList(itemList)
        assertEquals(total, responseResult.data.total)
        assertEquals(itemList, responseResult.data.results)
    }

    @Test
    fun test_setItemList_WhenItemsIsNotEmpty() {
        //Given
        val itemList = listOf(mItemMock)

        //Act
        mViewModel.setItemList(itemList)

        //Assert
        assertEquals(itemList, mViewModel.mItemList.value)
    }

    @Test
    fun test_setItemList_WhenItemsIsEmpty() {
        //Given
        val itemList = listOf<ItemCharacters>()

        //Act
        mViewModel.setItemList(itemList)

        //Assert
        assertNotEquals(itemList, mViewModel.mItemList.value)
        assertNull(mViewModel.mItemList.value)
    }

    @Test
    fun test_setLoading() {
        //Given

        //Act
        mViewModel.setLoading(true)

        //Assert
        assertTrue(mViewModel.mLoading.value ?: false)
    }
}