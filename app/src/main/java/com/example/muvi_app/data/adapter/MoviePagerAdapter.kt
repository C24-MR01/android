import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.muvi_app.R
import com.example.muvi_app.data.response.Movie

class MoviePagerAdapter(private val movies: List<Movie>) : RecyclerView.Adapter<MoviePagerAdapter.MoviePagerViewHolder>() {

    inner class MoviePagerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(movie: Movie) {
            Glide.with(itemView)
                .load("https://image.tmdb.org/t/p/original${movie.posterPath}")
                .into(imageView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviePagerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_movie_pager, parent, false)
        return MoviePagerViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoviePagerViewHolder, position: Int) {
        val movie = movies[position]
        holder.bind(movie)
    }

    override fun getItemCount(): Int {
        return movies.size
    }
}
