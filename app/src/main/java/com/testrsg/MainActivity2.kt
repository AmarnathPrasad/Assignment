package com.testrsg

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.testrsg.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class MainActivity2 : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: RandomStringAdapter

    private val contentProviderUri: Uri =
        Uri.parse("content://com.iav.contestdataprovider/text")
    private val dataColumnName = "data"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = RandomStringAdapter(mutableListOf()) { position ->
            adapter.removeAt(position)
        }
        binding.rvRandomStrings.layoutManager = LinearLayoutManager(this)
        binding.rvRandomStrings.adapter = adapter

        binding.btnGenerate.setOnClickListener {
            val lengthString = binding.etMaxLength.text.toString()
            if (lengthString.isBlank()) {
                Toast.makeText(this, "Please enter a valid length", Toast.LENGTH_SHORT).show()
            } else {
                val maxLength = lengthString.toIntOrNull()
                if (maxLength == null || maxLength <= 0) {
                    Toast.makeText(this, "Length must be a positive number", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    fetchRandomString(maxLength)
                }
            }
        }

        binding.btnClearAll.setOnClickListener {
            adapter.clearAll()
        }
    }

    private fun fetchRandomString(maxLength: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            try {

                val queryArgs = Bundle().apply {
                    putInt(ContentResolver.QUERY_ARG_LIMIT, maxLength)
                }
                val cursor: Cursor? = contentResolver.query(
                    contentProviderUri,  // URI
                    null,                // projection; we assume the provider returns all columns
                    null,                // selection
                    null,                // selectionArgs
                    null,                // sort order
                  //  queryArgs
                    null
                )
                // Cursor may be null or the provider might work slowly / be unreliable
                if (cursor != null && cursor.moveToFirst()) {
                    val jsonData = cursor.getString(cursor.getColumnIndexOrThrow(dataColumnName))
                    cursor.close()

                    // Parse the JSON data
                    val randomStringData = parseJson(randomStringData = jsonData)
                    withContext(Dispatchers.Main) {
                        if (randomStringData != null) {
                            adapter.addItem(randomStringData)
                        } else {
                            Toast.makeText(
                                this@MainActivity2,
                                "Error: Unable to parse response",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@MainActivity2,
                            "Error: No response from content provider",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Error querying content provider", e)
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity2,
                        "Error: ${e.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun parseJson(randomStringData: String): RandomStringData? {
        return try {
            val jsonObject = JSONObject(randomStringData)
            val randomText = jsonObject.getJSONObject("randomText")
            val value = randomText.getString("value")
            val length = randomText.getInt("length")
            val created = randomText.getString("created")
            RandomStringData(value = value, length = length, created = created)
        } catch (e: Exception) {
            Log.e("ParseJSON", "Error parsing JSON", e)
            null
        }
    }
}