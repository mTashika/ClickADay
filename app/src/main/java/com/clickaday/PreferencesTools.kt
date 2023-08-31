package com.clickaday

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class PreferencesTools {
    companion object {
        private const val PREF_NAME = "MyPrefs"
        private const val PREF_NAME_CRYPT = "MyCryptPrefs"
        const val PREF_PERMISSIONS = "permissionsResult"
        const val PREF_BLUR_IMG = "isBlurEnable"
        const val PREF_PASSWORD_VALUE_CRYPT = "isPasswordEnable"
        const val PREF_PASSWORD = "isPasswordEnable"
        const val PREF_DESCRIPTION_IMG = "isEnableImgDescription"
        const val PREF_FOLDER_PICT = "folderPicture"

        fun savePrefStrCrypt(ctx: Context, value: String, key: String) {
            val masterKey = MasterKey.Builder(ctx)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val sharedPreferences = EncryptedSharedPreferences.create(
                ctx,
                PREF_NAME_CRYPT,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            val editor = sharedPreferences.edit()
            editor.putString(key, value)
            editor.apply()
        }
        fun getPrefStrCrypt(ctx: Context, key: String): String? {
            val masterKey = MasterKey.Builder(ctx)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            val sharedPreferences = EncryptedSharedPreferences.create(
                ctx,
                PREF_NAME_CRYPT,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            return sharedPreferences.getString(key, null)
        }

        fun savePrefBool(ctx: Context, value: Boolean, key: String) {
            val sharedPreferences = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean(key, value)
            editor.apply()
        }

        fun savePrefStr(ctx: Context, value: String, key: String) {
            val sharedPreferences = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(key, value)
            editor.apply()
        }

        fun getPrefBool(ctx: Context, key: String): Boolean {
            val sharedPreferences = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean(key, false)

        }

        fun getPrefStr(ctx: Context, key: String): String? {
            val sharedPreferences = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getString(key, null)
        }
    }
}
