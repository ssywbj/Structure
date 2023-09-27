package com.suheng.structure.view.paging

import com.google.gson.annotations.SerializedName

class RepoResponse(
    @SerializedName("items") val items: List<Repo> = emptyList()
)
