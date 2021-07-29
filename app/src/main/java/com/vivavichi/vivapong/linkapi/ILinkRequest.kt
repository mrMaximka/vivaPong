package com.vivavichi.vivapong.linkapi

import com.vivavichi.vivapong.model.LinkModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ILinkRequest {
    @GET("api/v1/applications/{id}")
    fun loadData(@Path("id") uuid: String, @Query("application") applicationID: String): Call<LinkModel>
}