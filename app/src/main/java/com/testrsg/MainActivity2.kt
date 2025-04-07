package com.testrsg

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.testrsg.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class MainActivity2 : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private lateinit var inputLength: EditText
    private lateinit var fetchButton: Button
    private lateinit var resultText: TextView

    private lateinit var repository: RandomStringRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // binding = ActivityMainBinding.inflate(layoutInflater)
       // setContentView(binding.root)
       // setSupportActionBar(binding.toolbar)

        repository = RandomStringRepository(this)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(40, 80, 40, 80)
        }

        inputLength = EditText(this).apply {
            hint = "Max length"
            inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }

        fetchButton = Button(this).apply {
            text = "Fetch Random String"
        }

        resultText = TextView(this).apply {
            textSize = 16f
        }

        layout.apply {
            addView(inputLength)
            addView(fetchButton)
            addView(resultText)
        }

        setContentView(layout)

        fetchButton.setOnClickListener {
            val maxLength = inputLength.text.toString().toIntOrNull()
            if (maxLength != null && maxLength > 0) {
                lifecycleScope.launch {
                    resultText.text = "Fetching..."
                    val result = repository.getRandomString(maxLength)
                    result.fold(
                        onSuccess = {
                            resultText.text = "Value: ${it.value}\nLength: ${it.length}\nCreated: ${it.created}"
                        },
                        onFailure = {
                            resultText.text = "Error: ${it.message}"
                        }
                    )
                }
            } else {
                Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_SHORT).show()
            }
        }
    }
}