package com.example.app_androidmm;

import com.example.app_androidmm.database.Pelicula;
import org.junit.Before;
import org.junit.Test;

import java.sql.Date;

import static org.junit.Assert.*;

public class PeliculaTest {

    private Pelicula pelicula;

    @Before
    public void setUp() {
        pelicula = new Pelicula("Título de prueba", "Descripción de prueba", 4.5f,
                "https://image.tmdb.org/t/p/w500/cij4dd21v2Rk2YtUQbV5kW69WB2.jpg", "Género de prueba", "Calificación de prueba",
                "Director de prueba", new Date(2021, 1, 1), "Plataforma de prueba",
                "Protagonista de prueba");
    }

    @Test
    public void testGettersAndSetters() {
        assertEquals("Título de prueba", pelicula.getTitulo());
        assertEquals("Descripción de prueba", pelicula.getDescripcion());
        assertEquals(4.5f, pelicula.getRating(), 0.01);
        assertEquals("https://image.tmdb.org/t/p/w500/cij4dd21v2Rk2YtUQbV5kW69WB2.jpg", pelicula.getImagen());
        assertEquals("Género de prueba", pelicula.getGenero());
        assertEquals("Calificación de prueba", pelicula.getCalificacion());
        assertEquals("Director de prueba", pelicula.getDirector());
        assertEquals(new Date(2021, 1, 1), pelicula.getFechaPublicacion());
        assertEquals("Plataforma de prueba", pelicula.getPlataforma());
        assertEquals("Protagonista de prueba", pelicula.getProtagonista());

        pelicula.setCalificacion("Nueva calificación");
        assertEquals("Nueva calificación", pelicula.getCalificacion());

        pelicula.setDirector("Nuevo director");
        assertEquals("Nuevo director", pelicula.getDirector());

        Date nuevaFechaPublicacion = new Date(2022, 2, 2);
        pelicula.setFechaPublicacion(nuevaFechaPublicacion);
        assertEquals(nuevaFechaPublicacion, pelicula.getFechaPublicacion());

        pelicula.setPlataforma("Nueva plataforma");
        assertEquals("Nueva plataforma", pelicula.getPlataforma());

        pelicula.setProtagonista("Nuevo protagonista");
        assertEquals("Nuevo protagonista", pelicula.getProtagonista());
    }

    @Test
    public void testToString() {
        String expectedString = "Pelicula{" +
                "titulo='Título de prueba', " +
                "descripcion='Descripción de prueba', " +
                "rating=4.5, " +
                "imagen='https://image.tmdb.org/t/p/w500/cij4dd21v2Rk2YtUQbV5kW69WB2.jpg', " +
                "genero='Género de prueba', " +
                "calificacion='Calificación de prueba', " +
                "director='Director de prueba', " +
                "fechaPublicacion=391, " +
                "plataforma='Plataforma de prueba', " +
                "protagonista='Protagonista de prueba'}";

        assertEquals(expectedString, pelicula.toString());
    }

    // El error se debe a un formateo del Date, más adelante en la app he modificado el formato para arreglar dicho error

}
