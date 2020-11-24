package syr.project.homework_7


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.movie_list_item_left.view.*


//var query = FirebaseDatabase.getInstance()
//        .reference
//        .child("movies")
//        .limitToLast(50)

class MyFirebaseRecyclerAdapter(var modelClass: Class<MovieData>,var query: Query):
        FirebaseRecyclerAdapter<MovieData, MyFirebaseRecyclerAdapter.MovieViewHolder>(
                FirebaseRecyclerOptions.Builder<MovieData>()
                        .setQuery(query,modelClass)
                        .build()
        ){
    var myListener: MyItemClickListener? = null
    interface MyItemClickListener {
        fun onItemClickedFromAdapter(position: Int)
        fun onItemLongClickedFromAdapter(position: Int)
    }
    fun setMyItemClickListener(listener: MyItemClickListener) {
        this.myListener = listener
    }
    inner class MovieViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        val rVMovieTitle= view?.rVTitle
        val rVOverview= view?.rVOverview

        val rVposterid= view?.rVPosterid
        val rVRating= view?.rVRating
        val rVCheckBox= view?.rVCheckBox
        val overflow = view?.overflow
        init{
            overflow?.setOnClickListener {
                if(myListener != null){
                    if(adapterPosition != androidx.recyclerview.widget.RecyclerView.NO_POSITION){
//                        myListener!!.onOverflowMenuClickedFromAdapter(it, adapterPosition)
                    }
                }
            }



            view?.setOnLongClickListener {
                if(myListener!=null){
                    if(adapterPosition!= RecyclerView.NO_POSITION){

                        myListener!!.onItemLongClickedFromAdapter(adapterPosition)
                    }
                }
                true
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFirebaseRecyclerAdapter.MovieViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view:View
        view=when(viewType){
            1 -> {
                layoutInflater.inflate(R.layout.movie_list_item_right,parent,false)
            }
            2 -> {
                layoutInflater.inflate(R.layout.movie_list_item_left,parent,false)
            }
            else->{
                layoutInflater.inflate(R.layout.movie_list_item_right,parent,false)
            }
        }

        return MovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyFirebaseRecyclerAdapter.MovieViewHolder, position: Int, movie: MovieData) {
//        val movie=movieList[position]
        holder.rVMovieTitle!!.text =movie.title
        holder.rVOverview!!.text=movie.overview
//        holder.rVposterid!!.setImageResource(posterTable[movie.title]!!)

        val url = "https://image.tmdb.org/t/p/w185/" + movie.poster_path!!

        val picasso = Picasso.Builder(holder.itemView.context).listener { _, _, e -> e.printStackTrace() }.build()
        picasso.load(url).into(holder.rVposterid)
        Picasso.get().load(url).error(R.mipmap.ic_launcher).into(holder.rVposterid)
        holder.rVRating!!.text= movie.vote_average.toString()
        holder.rVCheckBox!!.isChecked= movie.checked!!
    }
    private val mDatabase: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val mRef = mDatabase.child("movies")
//    fun duplicateMovie(position: Int){
//
//
//
//        var movie=movieList[position].copy()
//
//        movieList.add(position+1,movie)
//        notifyItemInserted(position+1)
//        mRef.child("position").setValue(movie).addOnSuccessListener {
//            Log.d(TAG, "Insert a new Movie: ${movie.title}")
//        }
//
////        notifyDataSetChanged()
//
//    }

}





