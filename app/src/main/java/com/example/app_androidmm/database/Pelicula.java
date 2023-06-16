package com.example.app_androidmm.database;

import java.sql.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;

import static com.example.app_androidmm.utilidades.Utilidades.datesqlToLocalDate;

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
    private static ArrayList<Pelicula> peliculas = new ArrayList<>();


    public static ArrayList<Pelicula> getPeliculas() {
        return peliculas;
    }

    public static void setPeliculas(Pelicula pelicula) {
        Pelicula.peliculas.add(pelicula);
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

    // Calcula la similitud entre dos películas utilizando técnicas avanzadas de procesamiento de texto
    /*public double calcularSimilitud(Pelicula otraPelicula) {
        // Preprocesamiento de texto
        String[] genero1 = genero.toLowerCase().split(", ");
        String[] genero2 = otraPelicula.getGenero().toLowerCase().split(", ");

        // Vectorización de texto
        double[] vector1 = vectorizarTexto(genero1);
        double[] vector2 = vectorizarTexto(genero2);

        // Cálculo de similitud utilizando la similitud del coseno
        double similitudGenero = calcularSimilitudCoseno(vector1, vector2);

        // Cálculo de similitud adicional para los demás campos
        double similitudRating = calcularSimilitudRating(otraPelicula.getRating());
        double similitudFechaPublicacion = calcularSimilitudFechaPublicacion(datesqlToLocalDate(otraPelicula.getFechaPublicacion()));
        double similitudDirector = calcularSimilitudTexto(director, otraPelicula.getDirector());
        double similitudProtagonista = calcularSimilitudTexto(protagonista, otraPelicula.getProtagonista());

        // Ponderación de la similitud de cada campo según su importancia
        double pesoGenero = 0.4;
        double pesoRating = 0.2;
        double pesoFechaPublicacion = 0.1;
        double pesoDirector = 0.15;
        double pesoProtagonista = 0.15;

        // Cálculo de la similitud final ponderada
        double similitudFinal = (similitudGenero * pesoGenero) + (similitudRating * pesoRating)
                + (similitudFechaPublicacion * pesoFechaPublicacion) + (similitudDirector * pesoDirector)
                + (similitudProtagonista * pesoProtagonista);

        return similitudFinal;
    }*/

    public double calcularSimilitud(Pelicula otraPelicula) {
        // Preprocesamiento de texto
        String[] genero1 = genero.toLowerCase().split(", ");
        String[] genero2 = otraPelicula.getGenero().toLowerCase().split(", ");

        // Vectorización de texto
        double[] vector1 = vectorizarTexto(genero1);
        double[] vector2 = vectorizarTexto(genero2);

        // Cálculo de similitud utilizando la similitud del coseno
        double similitudGenero = calcularSimilitudCoseno(vector1, vector2);
        double similitudRating = calcularSimilitudRating(otraPelicula.getRating());
        double similitudFechaPublicacion = calcularSimilitudFechaPublicacion(datesqlToLocalDate(otraPelicula.getFechaPublicacion()));
        double similitudDirector = calcularSimilitudTexto(director, otraPelicula.getDirector());
        double similitudProtagonista = calcularSimilitudTexto(protagonista, otraPelicula.getProtagonista());

        // Contador de atributos conocidos
        int atributosConocidos = 0;

        // Suma de las similitudes de los atributos conocidos
        double sumaSimilitudes = 0.0;

        // Ponderación de la similitud de cada atributo según su importancia
        double pesoGenero = 0.4;
        double pesoRating = 0.2;
        double pesoFechaPublicacion = 0.1;
        double pesoDirector = 0.15;
        double pesoProtagonista = 0.15;

        // Verificar y agregar las similitudes de los atributos conocidos
        if (!genero.equalsIgnoreCase("Desconocido") && !otraPelicula.getGenero().equalsIgnoreCase("Desconocido")) {
            sumaSimilitudes += similitudGenero * pesoGenero;
            atributosConocidos++;
        }

        if (rating != 0.0 && otraPelicula.getRating() != 0.0) {
            sumaSimilitudes += similitudRating * pesoRating;
            atributosConocidos++;
        }

        if (fechaPublicacion != null && otraPelicula.getFechaPublicacion() != null) {
            sumaSimilitudes += similitudFechaPublicacion * pesoFechaPublicacion;
            atributosConocidos++;
        }

        if (!director.equalsIgnoreCase("Desconocido") && !otraPelicula.getDirector().equalsIgnoreCase("Desconocido")) {
            sumaSimilitudes += similitudDirector * pesoDirector;
            atributosConocidos++;
        }

        if (!protagonista.equalsIgnoreCase("Desconocido") && !otraPelicula.getProtagonista().equalsIgnoreCase("Desconocido")) {
            sumaSimilitudes += similitudProtagonista * pesoProtagonista;
            atributosConocidos++;
        }

        // Calcular la similitud final ponderada
        double similitudFinal = 0.0;

        if (atributosConocidos > 0) {
            similitudFinal = sumaSimilitudes / atributosConocidos;
        }

        return similitudFinal;
    }


    // Método para vectorizar el texto utilizando el modelo de la bolsa de palabras (bag of words)
    private double[] vectorizarTexto(String[] texto) {
        // Implementación básica utilizando un arreglo de conteo de palabras
        int numPalabras = 1000; // Número de palabras únicas en el vocabulario
        double[] vector = new double[numPalabras];

        for (String palabra : texto) {
            int indice = obtenerIndicePalabra(palabra); // Obtener el índice de la palabra en el vocabulario
            if (indice != -1) {
                vector[indice] += 1.0; // Incrementar el conteo de la palabra en el vector
            }
        }

        return vector;
    }

    // Método para calcular la similitud del coseno entre dos vectores
    private double calcularSimilitudCoseno(double[] vector1, double[] vector2) {
        double productoPunto = calcularProductoPunto(vector1, vector2);
        double magnitud1 = calcularMagnitud(vector1);
        double magnitud2 = calcularMagnitud(vector2);

        return productoPunto / (magnitud1 * magnitud2);
    }

    // Método para calcular el producto punto entre dos vectores
    private double calcularProductoPunto(double[] vector1, double[] vector2) {
        double productoPunto = 0.0;

        for (int i = 0; i < vector1.length; i++) {
            productoPunto += vector1[i] * vector2[i];
        }

        return productoPunto;
    }

    // Método para calcular la magnitud de un vector
    private double calcularMagnitud(double[] vector) {
        double magnitud = 0.0;

        for (double valor : vector) {
            magnitud += valor * valor;
        }

        return Math.sqrt(magnitud);
    }

    // Método para calcular la similitud de rating
    private double calcularSimilitudRating(double otroRating) {
        // Implementación básica utilizando la diferencia absoluta entre los ratings
        double diferencia = Math.abs(rating - otroRating);
        double similitud = 1.0 - diferencia / 10.0; // Escala de rating de 0 a 10

        return similitud;
    }

    // Método para calcular la similitud de fecha de publicación
    private double calcularSimilitudFechaPublicacion(LocalDate otraFechaPublicacion) {
        // Implementación básica utilizando la diferencia en años entre las fechas de publicación
        int diferencia = Math.abs(fechaPublicacion.getYear() - otraFechaPublicacion.getYear());
        double similitud = 1.0 - diferencia / 10.0; // Rango de 10 años

        return similitud;
    }

    // Método para calcular la similitud de texto utilizando el coeficiente de Jaccard
    private double calcularSimilitudTexto(String texto1, String texto2) {
        // Implementación básica utilizando el coeficiente de Jaccard
        String[] palabras1 = texto1.toLowerCase().split(" ");
        String[] palabras2 = texto2.toLowerCase().split(" ");

        int interseccion = contarPalabrasComunes(palabras1, palabras2);
        int union = palabras1.length + palabras2.length - interseccion;

        double similitud = (double) interseccion / union;

        return similitud;
    }

    // Método para contar las palabras comunes entre dos arreglos de palabras
    private int contarPalabrasComunes(String[] palabras1, String[] palabras2) {
        int contador = 0;

        for (String palabra1 : palabras1) {
            for (String palabra2 : palabras2) {
                if (palabra1.equals(palabra2)) {
                    contador++;
                    break;
                }
            }
        }

        return contador;
    }

    // Método para obtener el índice de una palabra en el vocabulario
    private int obtenerIndicePalabra(String palabra) {
        // Implementación básica utilizando una lista de palabras predefinida
        String[] vocabulario = {"genero", "rating", "fecha", "director", "protagonista"};

        for (int i = 0; i < vocabulario.length; i++) {
            if (vocabulario[i].equals(palabra)) {
                return i;
            }
        }

        return -1; // La palabra no se encuentra en el vocabulario
    }


}
