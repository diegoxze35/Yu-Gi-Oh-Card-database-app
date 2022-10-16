package com.android.yugioh.instances

import android.widget.ImageView
import com.squareup.picasso.Picasso

object Picasso {
	
	operator fun invoke(): Picasso = Picasso.get()
	
	fun Picasso.setImageFromUrlInImageView(url: String, img: ImageView) = load(url).into(img)
	
}