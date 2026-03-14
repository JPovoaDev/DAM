package dam_a51392.helloworldoptional

import android.os.Build
import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets


        }
        val Deviceinfo=findViewById<EditText>(R.id.editTextTextMultiLine)
        val info = buildString {
            appendLine("Manufacturer: ${Build.MANUFACTURER}")
            appendLine("Model: ${Build.MODEL}")
            appendLine("Brand: ${Build.BRAND}")
            appendLine("Type: ${Build.TYPE}")
            appendLine("User: ${Build.USER}")
            appendLine("Base: ${Build.VERSION_CODES.BASE}")
            appendLine("Incremental: ${Build.VERSION.INCREMENTAL}")
            appendLine("SDK: ${Build.VERSION.SDK_INT}")
            appendLine("Version Code: ${Build.VERSION.SDK_INT}")
            appendLine("Display: ${Build.DISPLAY}")

        }

        Deviceinfo.setText(info)




    }
}