package com.android.yugioh.ui.viewmodel

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NetworkConnectivity @Inject constructor(@ApplicationContext context: Context) :
	LiveData<Boolean>(false) {
	
	private val connectivityManager: ConnectivityManager
	
	init {
		connectivityManager =
			context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
	}
	
	private val actualNetworks: MutableSet<Network> = mutableSetOf()
	private val networkCallback = object : ConnectivityManager.NetworkCallback() {
		
		override fun onAvailable(network: Network) {
			super.onAvailable(network)
			if (connectivityManager.getNetworkCapabilities(network)
					?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
			)
				actualNetworks.add(network)
			postValue(actualNetworks.isNotEmpty())
		}
		
		override fun onLost(network: Network) {
			super.onLost(network)
			actualNetworks.remove(network)
			postValue(actualNetworks.isNotEmpty())
		}
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