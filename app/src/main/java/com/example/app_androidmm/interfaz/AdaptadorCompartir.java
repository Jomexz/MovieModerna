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
import java.sql.Date;
import java.util.List;

import static com.example.app_androidmm.utilidades.Utilidades.loadImageFromUrl;

public class AdaptadorCompartir extends RecyclerView.Adapter<AdaptadorCompartir.ViewHolder> {
    public static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 1;
    private List<Pelicula> pData;
    private final Context context;
    private final Activity activity;
    private OnShareClickListener onShareClickListener;

    public AdaptadorCompartir(List<Pelicula> pData, Context context, Activity activity) {
        this.context = context;
        this.pData = pData;
        this.activity = activity;
    }

    public void setOnShareClickListener(OnShareClickListener listener) {
        this.onShareClickListener = listener;
    }

    public void clearViews() {
        pData.clear();
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return pData.size();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pelicula_compartir, parent, false);
        return new AdaptadorCompartir.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final AdaptadorCompartir.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.bindData(pData.get(position));
        holder.shareButton.setOnClickListener(view -> {
            if (onShareClickListener != null) {
//                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
//                        == PackageManager.PERMISSION_DENIED) {
//                    ActivityCompat.requestPermissions(activity,
//                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                            PERMISSION_REQUEST_EXTERNAL_STORAGE);
//                } else {
                    onShareClickListener.onShareClick(((BitmapDrawable) holder.poster.getDrawable()).getBitmap(),
                            pData.get(position).getTitulo(), pData.get(position).getDescripcion(), pData.get(position).getProtagonista(), pData.get(position).getGenero(),
                            pData.get(position).getDirector(), pData.get(position).getPlataforma(), pData.get(position).getFechaPublicacion(), position);
//                }
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
            descripcion.setText(Html.fromHtml("<b>Descripción: </b>" + item.getDescripcion()));
            actor.setText(Html.fromHtml("<b>Actor principal: </b>" + item.getProtagonista()));
            genero.setText(Html.fromHtml("<b>Género: </b>" + item.getGenero()));
            calificacionEdad.setText(Html.fromHtml("<b>Calificación de edad: </b>" + item.getCalificacion()));
            director.setText(Html.fromHtml("<b>Director: </b>" + item.getDirector()));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String releaseDate = dateFormat.format(item.getFechaPublicacion());
            fecha.setText(Html.fromHtml("<b>Fecha de estreno: </b>" + releaseDate));
            plataforma.setText(Html.fromHtml("<b>Plataforma de Streaming: </b>" + item.getPlataforma()));
        }
    }

    public interface OnShareClickListener {
        void onShareClick(Bitmap bitmap, String title, String description, String actor, String genero, String director, String plataforma, Date fechapublicacion, int adapterPosition);
    }


}
