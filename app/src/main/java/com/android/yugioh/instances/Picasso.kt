package com.android.yugioh.instances

import android.widget.ImageView
import com.squareup.picasso.Picasso

object Picasso {
	
	operator fun invoke(): Picasso = Picasso.get()
	
	fun setImageFromUrlInImageView(picasso: Picasso, url: String, img: ImageView) =
		picasso.load(url).into(img)
	
}