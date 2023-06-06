package com.example.app_androidmm.interfaz;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.app_androidmm.R;
import com.example.app_androidmm.database.Pelicula;

import java.text.SimpleDateFormat;
import java.util.List;

import static com.example.app_androidmm.utilidades.Utilidades.loadImageFromUrl;

public class AdaptadorCompartir extends RecyclerView.Adapter<AdaptadorCompartir.ViewHolder> {
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 1;
    private List<Pelicula> pData;
    private LayoutInflater pInflater;
    private Context context;
    private OnShareClickListener onShareClickListener;

    public AdaptadorCompartir(List<Pelicula> pData, Context context) {
        this.pInflater = LayoutInflater.from(context);
        this.context = context;
        this.pData = pData;
    }

    public void setOnShareClickListener(OnShareClickListener listener) {
        this.onShareClickListener = listener;
    }

    @Override
    public int getItemCount() {
        return pData.size();
    }

    @Override
    public AdaptadorCompartir.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = pInflater.inflate(R.layout.pelicula_compartir, parent, false);
        return new AdaptadorCompartir.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AdaptadorCompartir.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.bindData(pData.get(position));
        holder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onShareClickListener != null) {
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                PERMISSION_REQUEST_EXTERNAL_STORAGE);
                    } else {
                        onShareClickListener.onShareClick(((BitmapDrawable) holder.poster.getDrawable()).getBitmap(),
                                pData.get(position).getTitulo(), pData.get(position).getDescripcion(), pData.get(position).getProtagonista(), pData.get(position).getGenero(),
                                pData.get(position).getDirector(), pData.get(position).getPlataforma(), position);
                    }
                }
            }
        });
    }



    public void setItems(List<Pelicula> items) {
        pData = items;
    }

    public Pelicula getPeliculaAtPosition(int position) {
        if (position >= 0 && position < pData.size()) {
            return pData.get(position);
        }
        return null;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, descripcion, actor, calificacionEdad, genero, director, fecha, plataforma;
        ImageView poster;
        Button shareButton;

        ViewHolder(View itemView) {
            super(itemView);
            poster = itemView.findViewById(R.id.posterImageView);
            titulo = itemView.findViewById(R.id.titleTextView);
            descripcion = itemView.findViewById(R.id.descriptionTextView);
            actor = itemView.findViewById(R.id.actor);
            calificacionEdad = itemView.findViewById(R.id.ageRatingTextView);
            genero = itemView.findViewById(R.id.genreTextView);
            director = itemView.findViewById(R.id.directorTextView);
            fecha = itemView.findViewById(R.id.releaseDateTextView);
            plataforma = itemView.findViewById(R.id.platformTextView);
            shareButton = itemView.findViewById(R.id.shareButton);
        }

        void bindData(final Pelicula item) {
            loadImageFromUrl(item.getImagen(), poster);
            titulo.setText(item.getTitulo());
            descripcion.setText(Html.fromHtml("<b>Descripción: </b>").toString()  + item.getDescripcion());
            actor.setText(Html.fromHtml("<b>Actor principal: </b>").toString()  + item.getProtagonista());
            genero.setText(Html.fromHtml("<b>Género: </b>".toString() + item.getGenero()));
            calificacionEdad.setText(Html.fromHtml("<b>Calificación de edad: </b>").toString()  + item.getCalificacion());
            director.setText(Html.fromHtml("<b>Director: </b>").toString()  + item.getDirector());
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String releaseDate = dateFormat.format(item.getFechaPublicacion());
            fecha.setText(Html.fromHtml("<b>Fecha de estreno: </b>").toString() + releaseDate);
            plataforma.setText(Html.fromHtml("<b>Plataforma de Streaming: </b>").toString() + item.getPlataforma());
        }
    }

    public interface OnShareClickListener {
        void onShareClick(Bitmap bitmap, String title, String description, String actor, String genero, String director, String plataforma, int adapterPosition);
    }


}
