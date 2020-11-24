package syr.project.homework_7

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.google.gson.annotations.SerializedName
import java.io.Serializable

@JsonIgnoreProperties("selection")
class MovieData: Serializable{


    var stars: String=""
    var backdrop_path: String=""
    var id: Int=-1
    var original_language: String=""
    var original_title: String=""
    var overview: String=""
    var popularity: Double=0.0
    var poster_path: String=""
    var release_date: String=""
    var title: String=""
//    @SerializedName("video") var video: Int,
    var vote_average: Double=0.0
    var vote_count: Int=-1
    var checked: Boolean=false
    var db_id: Int=-1



}















