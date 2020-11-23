package syr.project.homework_7

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.twitter.sdk.android.core.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_signup.*
import java.util.*
import kotlin.Result


class LoginFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    private lateinit var googleSignInClient: GoogleSignInClient
    var selectedPhotoUri: Uri? = null
//    private lateinit var googleSignInClient: GoogleSignInClient
    lateinit var providers:List<AuthUI.IdpConfig>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        login_button.setOnClickListener {
            performLogin()
        }
        // switch to sign up or reset password!
        back_to_register.setOnClickListener{
            val email = email_login.text.toString()
            val password = password_login.text.toString()
            // LoginActivity implements onSignUpRoutine()
            listener!!.onSignUpRoutine(email, password)
        }


        startSignInFlow()
        google_login_button.setOnClickListener{
            signIn()
        }






    }
    fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, 1000)
    }



    private fun startSignInFlow() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()

        googleSignInClient = GoogleSignIn.getClient(context!!, gso)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val task=GoogleSignIn.getSignedInAccountFromIntent(data)
        val exception = task.exception
        if (task.isSuccessful){
            try {
                // Google Sign In was successful, authenticate with Firebase

                val account = task.getResult(ApiException::class.java)!!
                Log.d("SignInActivity", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("SignInActivity", "Google sign in failed", e)
            }
        }



    }
    private fun firebaseAuthWithGoogle(idToken: String){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val mAuth = FirebaseAuth.getInstance()
        FirebaseAuth.getInstance().signInWithCredential(credential)

                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("SignInActivity", "signInWithCredential:success")
//                        val user = Firebase.auth.currentUser
//                        updateUI(user)
                        saveUserToFirebaseDatabase()

                    }
                    else {
                        // If sign in fails, display a message to the user.
                        Log.d("SignInActivity", "signInWithCredential:failure")
                    }
                }
    }

    private fun saveUserToFirebaseDatabase() {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val currentUserRef = ref!!.child(uid)
        val username= firebaseUser!!.displayName.toString()
        val email= firebaseUser!!.email.toString()
//        val httpsReference = storage.getReferenceFromUrl()
        val user = User(uid, username, email, null.toString())

        ref.setValue(user)
                .addOnSuccessListener{
                    Log.d("SignUp", "saved the user to Firebase Database")
                    val intent = Intent(context, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .addOnFailureListener{
                    Log.d("SignUp", "Failed to set value to database: ${it.message}")
                }
    }







    private fun performLogin() {
        val email = email_login.text.toString()
        val password = password_login.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "Please fill out email/pw.", Toast.LENGTH_SHORT).show()
            return
        }
        // Firebase Authentication using email and password!
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
        .addOnCompleteListener {
        if (it.isSuccessful) {
            Log.d("Login", "Successfully logged in: ${it.result!!.user!!.uid}")
        // launch the Main activity, clear back stack!

        // not going back to login activity when back button pressed
            val intent = Intent(context, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            }
        }
            .addOnFailureListener {
        Toast.makeText(context, "Failed to log in: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString()
            + " must implement OnFragmentInteractionListener")
        }
    }
    override fun onDetach() {
        super.onDetach()
        listener = null
    }
    interface OnFragmentInteractionListener {
        fun onSignUpRoutine(email: String, passwd: String)
    }

        companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}