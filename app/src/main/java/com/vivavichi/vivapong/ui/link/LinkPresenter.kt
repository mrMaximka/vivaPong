package com.vivavichi.vivapong.ui.link

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.telephony.TelephonyManager
import com.vivavichi.vivapong.linkapi.LinkRequest
import com.vivavichi.vivapong.model.LinkModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LinkPresenter (private val fragment: LinkContract.View) : LinkContract.Presenter {


    override fun checkTheInternet(requireContext: Context): Boolean {
        val result: Boolean
        val connectivityManager =
        requireContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw =
            connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
        return result
    }

    override fun tryToLoadData(requireContext: Context) {
        val tm: TelephonyManager =
            requireContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        if (tm.simState != TelephonyManager.SIM_STATE_ABSENT) {
//            getLink()
            fragment.loadViva()
        } else {
            fragment.loadViva()
        }
    }

    private fun getLink() {
        val call: Call<LinkModel> = LinkRequest.getClient.loadData("0776c757-0d9c-40a3-afe7-a69b368a0368", "com.vivavichi.vivapong")
        call.enqueue(object : Callback<LinkModel> {
            override fun onResponse(
                call: Call<LinkModel>,
                response: Response<LinkModel>
            ) {
                if (response.body() != null && response.isSuccessful){
                    val model: LinkModel = response.body()!!
                    if (model.link != null && model.link.isNotEmpty() && isSource(model.link)) {
                        fragment.loadLink(model.link)
                    } else {
                        fragment.loadViva()
                    }
                }else{
                    fragment.loadViva()
                }
            }

            override fun onFailure(
                call: Call<LinkModel>,
                t: Throwable
            ) {
                fragment.loadViva()
            }
        })
    }

    private fun isSource(link: String): Boolean {
        return link.contains("source=true")
    }
}