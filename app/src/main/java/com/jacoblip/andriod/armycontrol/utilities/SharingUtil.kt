package com.jacoblip.andriod.armycontrol.utilities

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity


object SharingUtil {
    fun shareApp(context: Context) {
        val appPackageName = context.packageName
        val toShare = "הורד אפליקציית פיקוד ושליטה \nhttps://play.google.com/store/apps/details?id=$appPackageName"
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        shareIntent.putExtra(Intent.EXTRA_TEXT, toShare)
        context.startActivity(Intent.createChooser(shareIntent, "Share using ..."))
    }

    fun openAppInPlaystore(context: Context) {
        val appPackageName = context.packageName
        try {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName"))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName"))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }

    }
}