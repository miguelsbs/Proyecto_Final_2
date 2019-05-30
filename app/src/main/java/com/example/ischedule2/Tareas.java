package com.example.ischedule2;

public class Tareas {

    private String titulo, descrip, url, fecha, hora, img;

    public Tareas(String titulo, String descrip, String url, String fecha, String hora, String img) {
        this.titulo = titulo;
        this.descrip = descrip;
        this.url = url;
        this.fecha = fecha;
        this.hora = hora;
        this.img = img;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

}
