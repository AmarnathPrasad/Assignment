package com.testrsg

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class RandomStringRepository(private val context: Context) {

    // Replace with the actual URI provided by IAV
    private val baseUri = "content://com.iav.randomprovider/randomstring"

    suspend fun getRandomString(maxLength: Int): Result<RandomTextResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val uri = Uri.parse("$baseUri?maIAV-1Length=$maxLength")
                val cursor = context.contentResolver.query(uri, null, null, null, null)

                cursor?.use {
                    if (it.moveToFirst()) {
                        val jsonString = it.getString(it.getColumnIndexOrThrow("data"))

                        val json = JSONObject(jsonString).getJSONObject("randomText")
                        val value = json.getString("value")
                        val length = json.getInt("length")
                        val created = json.getString("created")

                        Result.success(RandomTextResponse(value, length, created))
                    } else {
                        Result.failure(Exception("Empty response"))
                    }
                } ?: Result.failure(Exception("Cursor was null"))
            } catch (e: Exception) {
                Log.e("RandomStringRepo", "Failed to query provider", e)
                Result.failure(e)
            }
        }
    }
}