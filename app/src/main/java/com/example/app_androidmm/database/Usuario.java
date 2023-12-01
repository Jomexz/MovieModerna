package com.example.app_androidmm.database;

import java.util.Arrays;
import java.util.Date;

public class Usuario {
    private static Usuario instancia;
    private int pkUsuario;
    private String alias;
    private String pass;
    private String nombre;
    private String apellidos;
    private Date fechaNacimiento;
    private String email;
    private String avatar;
    private boolean verificado;
    private String pregunta, respuesta;

    // Constructor vacío
    public Usuario() {
    }

    public static Usuario getInstance() {
        if (instancia == null) {
            instancia = new Usuario();
        }
        return instancia;
    }

    public Usuario(String alias, String pass, String nombre, String apellidos, Date fechaNacimiento, String email, String avatar, String pregunta, String respuesta) {
        this.alias = alias;
        this.pass = pass;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.email = email;
        this.avatar = avatar;
        this.pregunta = pregunta;
        this.respuesta = respuesta;
    }

    public Usuario(String alias, String pass, String nombre, String apellidos, Date fechaNacimiento, String email, String avatar, boolean verificado, String pregunta, String respuesta) {
        this.alias = alias;
        this.pass = pass;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.fechaNacimiento = fechaNacimiento;
        this.email = email;
        this.avatar = avatar;
        this.verificado = verificado;
        this.pregunta = pregunta;
        this.respuesta = respuesta;
    }

    // Método para actualizar los campos con nuevos valores
    public void actualizarDatos(Usuario nuevoUsuario) {
        this.alias = nuevoUsuario.getAlias();
        this.pass = nuevoUsuario.getPass();
        this.pkUsuario = nuevoUsuario.getPkUsuario();
        this.email = nuevoUsuario.getEmail();
        this.nombre = nuevoUsuario.getNombre();
        this.apellidos = nuevoUsuario.getApellidos();
        this.fechaNacimiento = nuevoUsuario.getFechaNacimiento();
        this.verificado = nuevoUsuario.isVerificado();
        this.pregunta = nuevoUsuario.getPregunta();
        this.respuesta = nuevoUsuario.getRespuesta();
        this.avatar = nuevoUsuario.getAvatar();
    }
    public int getPkUsuario() {
        return pkUsuario;
    }

    public void setPkUsuario(int pkUsuario) {
        this.pkUsuario = pkUsuario;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isVerificado() {
        return verificado;
    }

    public void setVerificado(boolean verificado) {
        this.verificado = verificado;
    }

    public String getPregunta() {
        return pregunta;
    }

    public void setPregunta(String pregunta) {
        this.pregunta = pregunta;
    }

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "alias='" + alias + '\'' +
                ", pass='" + pass + '\'' +
                ", nombre='" + nombre + '\'' +
                ", apellidos='" + apellidos + '\'' +
                ", fechaNacimiento=" + fechaNacimiento +
                ", email='" + email + '\'' +
                ", avatar=" + avatar +
                ", pregunta='" + pregunta + '\'' +
                ", respuesta='" + respuesta + '\'' +
                '}';
    }

    public static class UsuarioQuery implements ConnectionManager.Query<Usuario> {
        private String id;

        public UsuarioQuery(String id) {
            this.id = id;
        }

        @Override
        public String getNode() {
            return "usuarios/" + id;
        }

        @Override
        public Class<Usuario> getDataClass() {
            return Usuario.class;
        }
    }
}


