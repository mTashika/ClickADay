package com.clickaday

import android.content.Context

class PreferencesTools {
    companion object {
        private const val PREF_NAME = "MyPrefs"

        /**Sauvegarder la valeur de permissionsResult dans les SharedPreferences
         *
         */
        fun savePermissionsResult(ctx: Context, permissionsResult: Boolean) {
            val sharedPreferences = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("permissionsResult", permissionsResult)
            editor.apply()
        }

        /** Fonction pour récupérer la valeur de permissionsResult depuis les SharedPreferences
         *
         */
        fun getPermissionsResult(ctx: Context): Boolean {
            val sharedPreferences = ctx.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return sharedPreferences.getBoolean("permissionsResult", false)
        }
    }
}
