package com.example.muvi_app.repository

import android.util.Log
import com.example.muvi_app.data.network.ApiConfig
import com.example.muvi_app.data.network.ApiService
import com.example.muvi_app.data.pref.UserPreference
import com.example.muvi_app.data.response.MLCResponse
import com.example.muvi_app.data.response.MLSResponse
import com.example.muvi_app.data.response.Movie
import com.example.muvi_app.data.response.MovieDetailResponse
import com.example.muvi_app.data.response.MultMovieResponse
import com.example.muvi_app.data.response.SearchMovieResponse
import com.google.gson.Gson
import kotlinx.coroutines.flow.first
import retrofit2.HttpException

class MovieRepository(private val userPreference: UserPreference) {

    private val movieApiService = ApiConfig.getApiService(userPreference)
    private val mlApiService = ApiConfig.getApiServiceMl()

    suspend fun getMovies(name: String): List<Movie> {
        val token = "Bearer ${userPreference.getSession().first().token}"
        println("Running search $name with token $token")

        try {
            val response = movieApiService.searchMovies(token, name)
            println("Response data ${response.results}")
            return response.results ?: emptyList()
        } catch (e: Exception) {
            println("Error fetching movies: ${e.message}")
            return emptyList()
        }
    }

    suspend fun getMovieDetail(movieId: Int): MovieDetailResponse {
        val token = "Bearer ${userPreference.getSession().first().token}"
        return movieApiService.getMovieDetail(token, movieId)
    }

    suspend fun getMovieRecent(type: String): List<Movie> {
        val token = "Bearer ${userPreference.getSession().first().token}"
        println("Running recent with query $type with token $token")

        try {
            val response = movieApiService.getRecentMovie(token, type)
            println("Response data ${response.results}")
            return response.results ?: emptyList()
        } catch (e: Exception) {
            println("Error fetching movies: ${e.message}")
            return emptyList()
        }
    }

    suspend fun getMultMovie(movieIds: Array<String>): MultMovieResponse {
        val token = "Bearer ${userPreference.getSession().first().token}"
        val Ids = ApiService.MultId(movieIds)
        println("Id $Ids")
        return movieApiService.getMultMovie(token, Ids)
    }

    suspend fun getIdCollab(userId: String): MLCResponse {
        return mlApiService.getColab(userId)
    }

    suspend fun getIdSynopsys(movieId: Int): MLSResponse? {
        return try {
            val responseBody = mlApiService.getSynopsys(movieId)
            val responseString = responseBody.string()
            Log.d("MovieRepository", "getIdSynopsys response: $responseString")

            val gson = Gson()
            val response = gson.fromJson(responseString, MLSResponse::class.java)
            response
        } catch (e: HttpException) {
            // HTTP error
            Log.e("MovieRepository", "HTTP error: ${e.code()} - ${e.message()}", e)
            null
        } catch (e: Exception) {
            // Other errors
            Log.e("MovieRepository", "Error fetching synopsis: ${e.message}", e)
            null
        }
    }

    companion object {
        @Volatile
        private var instance: MovieRepository? = null

        fun getInstance(userPreference: UserPreference): MovieRepository =
            instance ?: synchronized(this) {
                instance ?: MovieRepository(userPreference)
            }.also { instance = it }
    }
}