package kogvet.eye

import android.app.Activity
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.content.Intent

class SplashscreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // continue with your regular implementation

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()

    }

}