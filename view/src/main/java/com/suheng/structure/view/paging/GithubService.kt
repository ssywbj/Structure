package com.suheng.structure.view.paging

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface GithubService {

    //https://api.github.com/search/repositories?sort=stars&q=Android&per_page=5&page=1
    @GET("search/repositories?sort=stars&q=Android")
    suspend fun searchRepos(@Query("page") page: Int, @Query("per_page") perPage: Int): RepoResponse

    companion object {
        private const val BASE_URL = "https://api.github.com/"

        fun create(): GithubService {
            return Retrofit.Builder().baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).build()
                .create(GithubService::class.java)
        }
    }

}