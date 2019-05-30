package com.example.ischedule2;

public class Datos_Mask {

    private String titulo, descripcion;
    private int img, id;

    public Datos_Mask(){

    }

    public Datos_Mask(String titulo, String descripcion, int img, int id) {
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.img = img;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getImg() {
        return img;
    }

    public void setImg(int img) {
        this.img = img;
    }
}
