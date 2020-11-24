package syr.project.homework_7

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.database.*
import jp.wasabeef.recyclerview.adapters.AlphaInAnimationAdapter
import jp.wasabeef.recyclerview.adapters.ScaleInAnimationAdapter
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator


import kotlinx.android.synthetic.main.fragment_recycler_view.*

var query = FirebaseDatabase.getInstance()
    .reference
    .child("movies")
    .limitToLast(50)

class RecyclerViewFragment() : Fragment(),
    MyFirebaseRecyclerAdapter.MyItemClickListener{
    var idx: Int = 0
//    private var listener: OnRecyclerInteractionListener? = null
    lateinit var myAdapter:MyFirebaseRecyclerAdapter
//    var mL=ArrayList(MovieList().movieList)


    override fun onItemClickedFromAdapter(position: Int) {
        idx = position

    }


//    override fun onItemClickedFromAdapter(movie: MovieData, posterid: Int?) {
//        onItemClickedFromRecyclerViewFragment(movie,posterid)
//    }

    override fun onItemLongClickedFromAdapter(position: Int) {
//        myAdapter.duplicateMovie(position)
        activity!!.startActionMode(ActionBarCallBack(position))


    }

    inner class ActionBarCallBack( val position: Int) : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            mode!!.menuInflater.inflate(R.menu.menu_popup, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            val movie = myAdapter.getItem(position) as MovieData

            mode!!.title = movie.title
            return false
        }
        var dmovie= MovieData()

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            when(item!!.itemId){
                R.id.action_dup -> {

                    myAdapter.getRef(position).addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            dmovie.stars = snapshot.child("stars").value.toString()
                            dmovie.backdrop_path = snapshot.child("backdrop_path").value.toString()
                            dmovie.id = (snapshot.child("id").value as Long).toInt()
                            dmovie.original_language =
                                snapshot.child("original_language").value.toString()
                            dmovie.original_title =
                                snapshot.child("original_title").value.toString()
                            dmovie.overview = snapshot.child("overview").value.toString()
                            dmovie.popularity = snapshot.child("popularity").value as Double
                            dmovie.poster_path = snapshot.child("poster_path").value.toString()
                            dmovie.release_date = snapshot.child("release_date").value.toString()
                            dmovie.title = snapshot.child("title").value.toString()
                            dmovie.vote_average = (snapshot.child("vote_average").value as Double)
                            dmovie.vote_count = (snapshot.child("vote_count").value as Long).toInt()



                            val mDatabase: DatabaseReference =
                                FirebaseDatabase.getInstance().reference


                            var d = myAdapter.getRef(position).key + " _new"


                            val ref = FirebaseDatabase.getInstance().getReference("/movies/$d")
                            ref.setValue(dmovie)

                        }

                        override fun onCancelled(error: DatabaseError) {

                        }


                    }

                    )


                    mode!!.finish()
//                    val movietitle=myAdapter.getRef(position).child("title").toString()
//                    var dmovietitle="Duplicate of " + movietitle
//                    mRef.child(dmovietitle).setValue(myAdapter.getRef(position))

//                    myAdapter.getRef(position+1).push()

                }
                R.id.action_rem -> {
                    myAdapter.getRef(position).removeValue()
                    mode!!.finish()
                }
            }
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {

        }

    }


//    interface OnRecyclerInteractionListener {
//        fun onItemClicked(movie: MovieData,posterid: Int?)
//
//    }
//    fun onItemClickedFromRecyclerViewFragment(movie: MovieData,posterid: Int?) {
//        listener?.onItemClicked(movie,posterid)
//    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance=true
        setHasOptionsMenu(true)



    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recycler_view, container, false)
//        toolBarTitle!!.text="Movie List"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        myAdapter= MyFirebaseRecyclerAdapter(MovieData::class.java,query)
        recyclerView.layoutManager= GridLayoutManager(context,1)
//        val myAdapter= MyMovieListAdapter(movieList ,posterTable)
        myAdapter.setMyItemClickListener(this)
        recyclerView.adapter=myAdapter
        val alphaAdapter = AlphaInAnimationAdapter(myAdapter)
        recyclerView.adapter = ScaleInAnimationAdapter(alphaAdapter).apply {
            // Change the durations.
            setDuration(1000)
            // Change the interpolator.
            setInterpolator(OvershootInterpolator())
            // Disable the first scroll mode.
            setFirstOnly(false)


        }
        recyclerView.itemAnimator = SlideInLeftAnimator(OvershootInterpolator()).apply {

            addDuration = 1000
            removeDuration = 100
            moveDuration = 1000
            changeDuration = 100

        }


//        myAdapter.sortItemsByTitle()

    }
//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        if (context is OnRecyclerInteractionListener) {
//            listener = context
//        } else {
//            throw RuntimeException(context.toString() + " must implement OnRecyclerInteractionListener")
//        }
////        toolBarTitle!!.text="Movie List"
//    }

    override fun onStart() {
        super.onStart()
        myAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        myAdapter.stopListening()
    }




//    @SuppressLint("RestrictedApi")
//    override fun onOverflowMenuClickedFromAdapter(view: View?, position: Int) {
//        val popup = PopupMenu(context!!, view!!)
//        val menuInflater = popup.menuInflater
//        menuInflater.inflate(R.menu.menu_popup, popup.menu)
//        popup.setOnMenuItemClickListener {
//            when(it.itemId){
//                R.id.action_dup -> {
//
//                    myAdapter.duplicateMovie(position)
//                    return@setOnMenuItemClickListener true
//                }
//                R.id.action_rem -> {
//                    myAdapter.deleteMovies(position)
//                    return@setOnMenuItemClickListener true
//                }
//                else ->{
//                    return@setOnMenuItemClickListener false
//                }
//            }
//        }
//        // show icon on the popup menu!!
//        val menuHelper = MenuPopupHelper(this.context!!, popup.menu as MenuBuilder, view)
//        menuHelper.setForceShowIcon(true)
//        menuHelper.gravity = Gravity.END
//        menuHelper.show()
//    }


//    companion object {
//
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            RecyclerViewFragment(movieList, posterTable).apply {
//                arguments = Bundle().apply {
//
//                }
//            }
//    }
}




