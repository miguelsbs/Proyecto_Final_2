package com.example.ischedule2;

public class VariablesGlobales {

    //Variables globales de la tabla tarea
    public static final String tabla = "tarea";
    public static final String campo_id = "_id";
    public static final String campo_titulo = "titulo";
    public static final String campo_descripcion = "descripcion";
    public static final String campo_fecha = "fecha";
    public static final String campo_hora = "hora";
    public static final String campo_url = "url";
    public static final String campo_img = "img";
    public static final String campo_favorito = "fav";

    public static final String CREAR_TABLA_TAREA = "CREATE TABLE "+tabla+" (_id INTEGER PRIMARY KEY AUTOINCREMENT, "+campo_titulo+" TEXT, "+campo_descripcion+"" +
            " TEXT, "+campo_fecha+" TEXT, "+campo_hora+" TEXT, "+campo_url+" TEXT, "+campo_img+" TEXT, "+campo_favorito+" INT)";

    public static final String Consul_select = "SELECT * FROM ";
}
