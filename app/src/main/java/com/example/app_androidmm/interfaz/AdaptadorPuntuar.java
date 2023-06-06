package com.example.app_androidmm.interfaz;

import android.support.annotation.NonNull;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.app_androidmm.R;
import com.example.app_androidmm.database.Pelicula;

import java.util.List;

import static com.example.app_androidmm.utilidades.Utilidades.loadImageFromUrl;

public class AdaptadorPuntuar extends RecyclerView.Adapter<AdaptadorPuntuar.PeliculaViewHolder> {

    private List<Pelicula> listaPeliculas;
    private OnRatingClickListener onRatingClickListener;

    public interface OnRatingClickListener {
        void onRatingClick(int position, float rating);
    }

    public AdaptadorPuntuar(List<Pelicula> listaPeliculas) {
        this.listaPeliculas = listaPeliculas;
    }

    public void setOnRatingClickListener(OnRatingClickListener listener) {
        this.onRatingClickListener = listener;
    }

    @NonNull
    @Override
    public PeliculaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.pelicula_calificar, parent, false);
        return new PeliculaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PeliculaViewHolder holder, int position) {
        Pelicula pelicula = listaPeliculas.get(position);
        holder.titleTextView.setText(pelicula.getTitulo());
        holder.descriptionTextView.setText(Html.fromHtml("<b>Descripción: </b>").toString()  + pelicula.getDescripcion());
        loadImageFromUrl(pelicula.getImagen(), holder.posterImageView); // Cargar imagen desde la URL
        holder.genreTextView.setText(Html.fromHtml("<b>Género: </b>".toString() + pelicula.getGenero()));
        holder.ageRatingTextView.setText(Html.fromHtml("<b>Calificación de edad: </b>").toString()  + pelicula.getCalificacion());
        holder.directorTextView.setText(Html.fromHtml("<b>Director: </b>").toString()  + pelicula.getDirector());
        holder.releaseDateTextView.setText(Html.fromHtml("<b>Fecha de estreno: </b>").toString() + pelicula.getFechaPublicacion().toString());
        holder.platformTextView.setText(Html.fromHtml("<b>Plataforma de Streaming: </b>").toString() + pelicula.getPlataforma());
        holder.actor.setText(Html.fromHtml("<b>Actor principal: </b>").toString()  + pelicula.getProtagonista());

        holder.ratingBar.setRating(pelicula.getRating());
        holder.setOnRatingClickListener(onRatingClickListener);

        // Dentro del método onBindViewHolder del adaptador
        holder.calificarButton.setOnClickListener(view -> {
            if (calificarClickListener != null) {
                float rating = holder.ratingBar.getRating();
                calificarClickListener.onCalificarClick(position, rating);
            }
        });
    }
    public void clearViews() {
        listaPeliculas.clear();
        notifyDataSetChanged();
    }



    @Override
    public int getItemCount() {
        return listaPeliculas.size();
    }

    public class PeliculaViewHolder extends RecyclerView.ViewHolder {
        private TextView titleTextView;
        private TextView descriptionTextView;
        private ImageView posterImageView;
        private TextView genreTextView;
        private TextView ageRatingTextView;
        private TextView directorTextView;
        private TextView releaseDateTextView;
        private TextView platformTextView;
        private TextView actor;
        private RatingBar ratingBar;
        private Button calificarButton;

        public PeliculaViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
            posterImageView = itemView.findViewById(R.id.posterImageView);
            genreTextView = itemView.findViewById(R.id.genreTextView);
            ageRatingTextView = itemView.findViewById(R.id.ageRatingTextView);
            directorTextView = itemView.findViewById(R.id.directorTextView);
            releaseDateTextView = itemView.findViewById(R.id.releaseDateTextView);
            platformTextView = itemView.findViewById(R.id.platformTextView);
            actor = itemView.findViewById(R.id.actor);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            calificarButton = itemView.findViewById(R.id.calificarButton);

            calificarButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                float rating = ratingBar.getRating();
                if (position != RecyclerView.NO_POSITION && onRatingClickListener != null) {
                    onRatingClickListener.onRatingClick(position, rating);
                }
            });
        }

        public void setOnRatingClickListener(OnRatingClickListener listener) {
            onRatingClickListener = listener;
        }
    }

    public interface OnCalificarClickListener {
        void onCalificarClick(int position, float rating);
    }

    private OnCalificarClickListener calificarClickListener;

    public void setOnCalificarClickListener(OnCalificarClickListener listener) {
        this.calificarClickListener = listener;
    }

}

