package br.com.rosait.marvelcharacters.viewmodel

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import br.com.rosait.marvelcharacters.common.entity.ItemEntity
import br.com.rosait.marvelcharacters.common.model.ItemCharacters
import br.com.rosait.marvelcharacters.common.model.ResponseData
import br.com.rosait.marvelcharacters.common.model.ResponseResult
import br.com.rosait.marvelcharacters.common.model.ThumbnailItem
import br.com.rosait.marvelcharacters.service.ItemService
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
        val viewModelSpy = spy(mViewModel)
        val itemResponse = Single.just(
            ResponseResult(
                ResponseData(
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

        `when`(mItemServiceMock.getItems()).thenReturn(itemResponse)
        doNothing().`when`(viewModelSpy).setItemList(response)
        doNothing().`when`(viewModelSpy).setLoading(ArgumentMatchers.anyBoolean())

        //Act
        viewModelSpy.getItems()

        //Assert
        verify(viewModelSpy, times(1)).getItems()
        verify(viewModelSpy, never()).logError(ArgumentMatchers.anyString())
        verify(mItemServiceMock, times(1)).getItems()
        verify(viewModelSpy, times(1)).setLoading(true)
        verify(viewModelSpy, times(1)).setLoading(false)
        verify(viewModelSpy, times(1)).setItemList(response)
        verifyNoMoreInteractions(viewModelSpy)
    }

    @Test
    fun test_getItems_WhenError() {
        //Given
        val errorMessage = "Error Message"
        val throwable = Throwable(errorMessage)
        val viewModelSpy = spy(mViewModel)
        val responseResult = mock(ResponseResult::class.java)

        `when`(mItemServiceMock.getItems()).thenReturn(Single.error(throwable))
        doNothing().`when`(viewModelSpy).setLoading(ArgumentMatchers.anyBoolean())
        doNothing().`when`(viewModelSpy).logError(errorMessage)

        //Act
        viewModelSpy.getItems()

        //Assert
        verify(viewModelSpy, times(1)).getItems()
        verify(viewModelSpy, never()).setItemList(responseResult)
        verify(mItemServiceMock, times(1)).getItems()
        verify(viewModelSpy, times(1)).setLoading(true)
        verify(viewModelSpy, times(1)).setLoading(false)
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
    fun test_setItemList_WhenItemsIsNotEmpty() {
        //Given
        val itemList = listOf(mItemMock)
        val responseResult = ResponseResult(ResponseData(itemList))

        //Act
        mViewModel.setItemList(responseResult)

        //Assert
        assertEquals(itemList, mViewModel.mItemList.value)
    }

    @Test
    fun test_setItemList_WhenItemsIsEmpty() {
        //Given
        val itemList = listOf<ItemCharacters>()
        val responseResult = ResponseResult(ResponseData(itemList))

        //Act
        mViewModel.setItemList(responseResult)

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