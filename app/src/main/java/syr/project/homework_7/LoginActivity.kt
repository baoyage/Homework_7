package syr.project.homework_7

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_main.*

class LoginActivity : AppCompatActivity(),LoginFragment.OnFragmentInteractionListener,
SignupFragment.OnFragmentInteractionListener{
    var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        setSupportActionBar(myToolbar)
        val appBar = supportActionBar
        appBar!!.title = "Homework_7"
        appBar.setDisplayShowHomeEnabled(true)

        //        auth = FirebaseAuth.getInstance()
        //        if(auth!!.currentUser != null){
        //            Toast.makeText(this, "You are already logged in", Toast.LENGTH_SHORT).show()
        //            // launch the Main activity
        //            val intent = Intent(this, MainActivity::class.java)
        //            startActivity(intent)
        //        }

        supportFragmentManager.beginTransaction().add(R.id.login_container, LoginFragment()).commit()

    }
    override fun onSignUpRoutine(email: String, passwd: String) {
        supportFragmentManager.beginTransaction().replace(R.id.login_container,
        SignupFragment.newInstance(email, passwd)).commit()
    }
    override fun onSignInRoutine() {
        supportFragmentManager.beginTransaction().replace(R.id.login_container,
        LoginFragment()).commit()
    }
}