package dam_a51392.helloworld

import android.R.attr.typeface

import android.os.Bundle
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat

class MainActivity : AppCompatActivity() {
    //var tasks = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets





        }
        var listOfTask = mutableListOf<String>()
        val button = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        if (button != null) {



            val taskContainer = findViewById<LinearLayout>(R.id.taskContainer)
            val remainigTasksText = findViewById<TextView>(R.id.textView5)
            var taskToDo = findViewById<TextInputEditText>(R.id.textInputEditText)

            button.setOnClickListener {
                //tasks++

                listOfTask.add(taskToDo.text.toString())
                val taskView = TextView(this)
                taskView.text = taskToDo.text.toString()

                taskView.textSize = 20f
                val typeface = ResourcesCompat.getFont(this, R.font.alfa_slab_one)
                taskView.setTypeface(typeface, Typeface.BOLD)
                taskView.setTextColor(resources.getColor(android.R.color.black, theme))

                taskView.setPadding(40, 8, 16, 8)


                taskContainer.addView(taskView)
                remainigTasksText.text = "Your remainig tasks: ${listOfTask.size}"
               // println("Elemetentos da lista $listOfTask")
                taskToDo.text?.clear()
            }
        }

        println(this@MainActivity.localClassName + getString(R.string.activity_oncreate_msg))






    }


}