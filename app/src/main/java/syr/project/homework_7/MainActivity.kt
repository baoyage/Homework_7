package syr.project.homework_7


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.navi_header.view.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {


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
}