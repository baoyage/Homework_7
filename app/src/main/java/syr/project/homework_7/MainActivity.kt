package syr.project.homework_7


import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_signup.*
import kotlinx.android.synthetic.main.navi_header.view.*
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    var selectedPhotoUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        isLoggedIn()
        setSupportActionBar(myToolbar)
        val appBar = supportActionBar
        appBar!!.title = "Homework_7"
        appBar.setDisplayShowHomeEnabled(true)
//        appBar.setBackgroundDrawable(ColorDrawable(Color.parseColor("#1B82D2")))
        val toggle = ActionBarDrawerToggle(this, mainAct, myToolbar, 0, 0)
        mainAct.addDrawerListener(toggle)
        toggle.syncState()
        navView.setNavigationItemSelectedListener(this)



        supportFragmentManager.beginTransaction().replace(R.id.meContainer,RecyclerViewFragment()).commit()




    }


    override fun onStart() {
        super.onStart()
        val uid = FirebaseAuth.getInstance().uid
        if(uid != null){
            val firebaseUser = FirebaseAuth.getInstance().currentUser
            if (firebaseUser != null) {
                if(firebaseUser.email == null)
                    Log.i(firebaseUser!!.email, "onStart: dsafasddsaf")
            }
            Log.i(firebaseUser!!.displayName, "onStart: qwetrtyuyuii")
            val headerView = navView.getHeaderView(0)
            val profileEmail = headerView.profileEmail
            val profileUserName = headerView.profileUserName
            val profileImage= headerView.profileImage
//            var currentUid= FirebaseAuth.getInstance().currentUser!!.uid
//            val ref = FirebaseDatabase.getInstance().reference.child("users")
//            val currentUserRef = ref!!.child(currentUid)
//            currentUserRef.child("useremail").setValue(firebaseUser!!.email)
//            profileEmail.text= firebaseUser!!.email
//        profileUserName.text=firebaseUser!!.displayName
//            FirebaseAuth.getInstance().currentUser?.photoUrl




            val profileRef = FirebaseDatabase.getInstance().reference.child("users").child(firebaseUser!!.uid)

            profileRef.addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if(dataSnapshot != null){
                        profileEmail.text = dataSnapshot.child("useremail").value.toString()
                        Log.i(dataSnapshot.child("useremail").value.toString(), "onStart: qwertytyu")
//                        if(profileEmail.text=="null"){
//                            profileEmail.text="Email"
//                        }
                        profileUserName.text=dataSnapshot.child("username").value.toString()
//                        if(profileUserName.text=="null"){
//                            profileUserName.text=firebaseUser!!.displayName
//                        }
                        Picasso.get().load(dataSnapshot.child("profileImageUrl").value.toString()).fit().into(profileImage)
                        if(dataSnapshot.child("profileImageUrl").value.toString()=="null"){
//                            profileImage.setImageResource(R.drawable._8)
                            Picasso.get().load(FirebaseAuth.getInstance().currentUser?.photoUrl.toString()).fit().into(profileImage)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {


                }

            }
            )
        }

    }

    private fun isLoggedIn(){
        val uid = FirebaseAuth.getInstance().uid // check current uid of authentication!
        if(uid == null){
        // launch the Login activity
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        }

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.signout -> {
                signOut()
            }
            R.id.changeImage->{
                changeImage()
            }
        }


        return true
    }

    override fun onBackPressed() {
        if (mainAct.isDrawerOpen(GravityCompat.START)) {
            mainAct.closeDrawer(GravityCompat.START)
        } else
            super.onBackPressed()
    }




    private fun signOut(){
        FirebaseAuth.getInstance().signOut()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
    private fun changeImage(){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
        uploadImageToFirebaseStorage()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null) {
            Log.d("SignUp", "Photo was selected")
            selectedPhotoUri = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(this!!.contentResolver, selectedPhotoUri)
            selectphoto_imageview.setImageBitmap(bitmap)
            selectphoto_button.alpha = 0f // hide button for selected photo imageview

        }

    }
    private fun uploadImageToFirebaseStorage() {
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        ref.putFile(selectedPhotoUri!!)
            .addOnSuccessListener {
                Log.d("SignUp", "Successfully uploaded image: ${it.metadata?.path}")
                ref.downloadUrl.addOnSuccessListener {
                    Log.d("SignUp", "File Location: $it")
                    saveUserToFirebaseDatabase(it.toString())
                }
            }
            .addOnFailureListener {
                Log.d("SignUp", "Failed to upload image to storage: ${it.message}")
            }


    }
    private fun saveUserToFirebaseDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/users/$uid")
//        var uid= FirebaseAuth.getInstance().currentUser!!.uid
//        val ref = FirebaseDatabase.getInstance().reference.child("users")
        val currentUserRef = ref!!.child(uid)
        val headerView = navView.getHeaderView(0)
        val profileEmail = headerView.profileEmail
        val profileUserName = headerView.profileUserName
        val profileImage= headerView.profileImage
        val user = User(uid, profileUserName.text.toString(), profileEmail.text.toString(), profileImageUrl)
//        currentUserRef.setValue(user)
//        currentUserRef.child("username").setValue("cccccc")
        ref.setValue(user)
            .addOnSuccessListener{
                Log.d("SignUp", "saved the user to Firebase Database")
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener{
                Log.d("SignUp", "Failed to set value to database: ${it.message}")
            }
    }



}