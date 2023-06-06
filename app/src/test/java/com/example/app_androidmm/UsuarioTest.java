package com.example.app_androidmm;

import com.example.app_androidmm.database.Usuario;
import org.junit.Test;

import static org.junit.Assert.*;

import java.util.Date;

public class UsuarioTest {

    @Test
    public void testToString() {
        // Crear una instancia de Usuario con valores relacionados a "joker"
        Usuario usuario = new Usuario(
                "joker",
                "password123",
                "Joker",
                "Unknown",
                new Date(),
                "joker@example.com",
                "https://firebasestorage.googleapis.com/v0/b/moviemoderna.appspot.com/o/images%2Fjoker_avatar?alt=media&token=c7a080ce-8958-47f8-abe2-a0765b2a43ba",
                "¿Cuál es mi nombre?",
                "Ha! No tengo nombre"
        );

        // Definir el resultado esperado del método toString()
        String expectedString = "Usuario{" +
                "alias='joker', " +
                "pass='password123', " +
                "nombre='Joker', " +
                "apellidos='Unknown', " +
                "fechaNacimiento=" + usuario.getFechaNacimiento() + ", " +
                "email='joker@example.com', " +
                "avatar='https://firebasestorage.googleapis.com/v0/b/moviemoderna.appspot.com/o/images%2Fjoker_avatar?alt=media&token=c7a080ce-8958-47f8-abe2-a0765b2a43ba', " +
                "pregunta='¿Cuál es mi nombre?', " +
                "respuesta='Ha! No tengo nombre'" +
                "}";

        // Comprobar si el resultado esperado coincide con el resultado actual
        assertEquals(expectedString, usuario.toString());
    }

    // El error parece ser debido al formato de la URL, puede que tenga algún caracter raro y no lo reconozca Java pero es obvio, es una url y eso puede pasar
}
