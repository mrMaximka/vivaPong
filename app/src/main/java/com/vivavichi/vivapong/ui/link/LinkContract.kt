package com.vivavichi.vivapong.ui.link

import android.content.Context

interface LinkContract {
    interface View {
        fun loadViva()
        fun loadLink(link: String)

    }
    interface Presenter {
        fun checkTheInternet(requireContext: Context): Boolean
        fun tryToLoadData(requireContext: Context)

    }
}