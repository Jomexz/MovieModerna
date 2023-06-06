package com.example.app_androidmm.database;

import java.sql.Date;

public class Pelicula {
    private static Pelicula instanciaPelicula;
    private int pkPelicula;
    private String titulo;
    private String descripcion;
    private float rating;
    private String imagen;
    private String genero;
    private String calificacion;
    private String director;
    private Date fechaPublicacion;
    private String plataforma;
    private String protagonista;

    public Pelicula() {

    }

    public static Pelicula getInstance() {
        if (instanciaPelicula == null) {
            instanciaPelicula = new Pelicula();
        }
        return instanciaPelicula;
    }

    // Constructor
    public Pelicula(String titulo, String descripcion, float rating, String imagen, String genero,
                    String calificacion, String director, Date fechaPublicacion, String plataforma,
                    String protagonista) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.rating = rating;
        this.imagen = imagen;
        this.genero = genero;
        this.calificacion = calificacion;
        this.director = director;
        this.fechaPublicacion = fechaPublicacion;
        this.plataforma = plataforma;
        this.protagonista = protagonista;
    }

    public Pelicula(int pkPelicula, String titulo, String descripcion, float rating, String imagen, String genero, String calificacion, String director, Date fechaPublicacion, String plataforma, String protagonista) {
        this.pkPelicula = pkPelicula;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.rating = rating;
        this.imagen = imagen;
        this.genero = genero;
        this.calificacion = calificacion;
        this.director = director;
        this.fechaPublicacion = fechaPublicacion;
        this.plataforma = plataforma;
        this.protagonista = protagonista;
    }
// Getters and Setters

    public int getPkPelicula() {
        return pkPelicula;
    }

    public void setPkPelicula(int pkPelicula) {
        this.pkPelicula = pkPelicula;
    }

    public void setCalificacion(String calificacion) {
        this.calificacion = calificacion;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public Date getFechaPublicacion() {
        return fechaPublicacion;
    }

    public void setFechaPublicacion(Date fechaPublicacion) {
        this.fechaPublicacion = fechaPublicacion;
    }

    public String getPlataforma() {
        return plataforma;
    }

    public void setPlataforma(String plataforma) {
        this.plataforma = plataforma;
    }

    public String getProtagonista() {
        return protagonista;
    }

    public void setProtagonista(String protagonista) {
        this.protagonista = protagonista;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getCalificacion() {
        return calificacion;
    }

    @Override
    public String toString() {
        return "Pelicula{" +
                "titulo='" + titulo + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", rating=" + rating +
                ", imagen='" + imagen + '\'' +
                ", genero='" + genero + '\'' +
                ", calificacion='" + calificacion + '\'' +
                ", director='" + director + '\'' +
                ", fechaPublicacion=" + fechaPublicacion +
                ", plataforma='" + plataforma + '\'' +
                ", protagonista='" + protagonista + '\'' +
                '}';
    }
}
