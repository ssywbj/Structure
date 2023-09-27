package com.suheng.structure.view.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState

class RepoPagingSource(private val githubService: GithubService) : PagingSource<Int, Repo>() {

    override fun getRefreshKey(state: PagingState<Int, Repo>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Repo> {
        return try {
            val page = params.key ?: 1
            val perPage = params.loadSize
            val items = githubService.searchRepos(page, perPage).items
            val prevKey = if (page > 1) page - 1 else null
            val nextKey = if (items.isNotEmpty()) page + 1 else null
            LoadResult.Page(items, prevKey, nextKey)
        } catch (e: Exception) {
            Log.e("Wbj", "LoadResult Error: $e")
            LoadResult.Error(e)
        }
    }

}

