package com.android.yugioh.ui.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

class NetworkConnectivity @Inject constructor(@ApplicationContext context: Context) :
	LiveData<Boolean>(false) {
	
	private val connectivityManager: ConnectivityManager
	
	companion object {
		private const val URL_GOOGLE = "https://www.google.com"
	}
	
	init {
		connectivityManager =
			context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
	}
	
	private val actualNetworks: MutableSet<Network> = mutableSetOf()
	private val networkCallback = object : ConnectivityManager.NetworkCallback() {
		
		override fun onAvailable(network: Network) {
			super.onAvailable(network)
			if (network.hasInternet())
				actualNetworks.add(network)
			postValue(actualNetworks.isNotEmpty())
		}
		
		override fun onLost(network: Network) {
			super.onLost(network)
			actualNetworks.remove(network)
			postValue(actualNetworks.isNotEmpty())
		}
	}
	
	private fun Network.hasInternet(): Boolean {
		val httpConnection by lazy { openConnection(URL(URL_GOOGLE)) as HttpURLConnection }
		if (connectivityManager.getNetworkCapabilities(this)
				?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
		) {
			val ok = try {
				httpConnection.connect()
				(httpConnection.responseCode == HttpURLConnection.HTTP_OK)
			} catch (e: Exception) {
				false
			} finally {
				httpConnection.disconnect()
			}
			return ok
		}
		return false
	}
	
	override fun onActive() {
		connectivityManager.registerNetworkCallback(NetworkRequest.Builder().run {
			addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
			build()
		}, networkCallback)
		super.onActive()
	}
	
	override fun onInactive() {
		connectivityManager.unregisterNetworkCallback(networkCallback)
		super.onInactive()
	}
}