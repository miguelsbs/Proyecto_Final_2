package com.example.ischedule2;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Objects;

public class activity_editar_tarea extends AppCompatActivity {

    //  private static final String TEXT_STATE = "currentText";
    private TextView miMensaje;
    private Spinner temas;
    private TextView titEvento;
    private Switch favorito;
    private String img, idEvento;
    private EditText titulo, descripcion, fecha, hora, url;
    private String[] datosEvento = new String[7];
    private static String[] listaImg = new String[]{"Temas", "Boda", "Cita de Negocios", "Cita Romantica",
            "Comida", "Compras", "Concierto", "Cumpleaños", "Estudios", "Examen", "Médico",
            "Oficina", "Partido", "Reunión Amigos", "Reunión Familiar", "Vacaciones", "Viaje"};
    private Conexion conn;
    private Button btnCrear, btnEditar;
    private int fav = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_tarea);
        temas = (Spinner) findViewById(R.id.sp_1);

        titulo = (EditText) findViewById(R.id.eT_titulo);
        descripcion = (EditText) findViewById(R.id.eT_descrip);
        fecha = (EditText) findViewById(R.id.eT_fecha);
        hora = (EditText) findViewById(R.id.eT_horas);
        url = (EditText) findViewById(R.id.eT_web);
        favorito = (Switch) findViewById(R.id.sw_Fav);
        btnCrear = (Button) findViewById(R.id.btnCrear);
        btnEditar = (Button) findViewById(R.id.btnEdit);
        btnEditar.setVisibility(View.GONE);
        titEvento = (TextView) findViewById(R.id.tV_1);

        conn = new Conexion(this, "db_tareas", null, 1);

        //EXTRAEMOS LO QUE NOS VIENE DESDE LA VENTANA EVENTO
        datosEvento = getIntent().getStringArrayExtra("datosEvento");
        idEvento = getIntent().getStringExtra("id");
        System.out.println("Id del EVENTO: " + idEvento);

        if(!Objects.equals(idEvento, null)){
            //    System.out.println("ESTOY EDITANDO");
            btnCrear.setVisibility(View.GONE);
            titEvento.setText("Editar Evento");
            btnEditar.setVisibility(View.VISIBLE);
            editarEvento();

        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.temas_img, android.R.layout.simple_spinner_item);

        temas.setAdapter(adapter);
        temas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //CAPTURAMOS EL NOMBRE DEL TEMA/SPINNER
                img = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }


        });

        favorito.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fav = 1;
            }
        });

        miMensaje = findViewById(R.id.tFav);
        miMensaje.setVisibility(View.INVISIBLE);
        //SOLO NOS MUESTRA EL MENSAJE DE LA TAREAASYNC SI EL EVENTO NO ES FAVORITO
        if(!favorito.isChecked()){
            miMensaje.setVisibility(View.VISIBLE);
            new TareaAsync(miMensaje).execute();
        }


        ///UPLOAD
        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateEvento();
            }
        });

    }

    public void crearEvento(View view) {
        crear();
    }

    public void crear(){
        //   Conexion conn = new Conexion(this, "db_tareas", null, 1);
        SQLiteDatabase db = conn.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(VariablesGlobales.campo_titulo, titulo.getText().toString());
        values.put(VariablesGlobales.campo_descripcion, descripcion.getText().toString());
        values.put(VariablesGlobales.campo_fecha, fecha.getText().toString());
        values.put(VariablesGlobales.campo_hora, hora.getText().toString());
        values.put(VariablesGlobales.campo_url, url.getText().toString());
        values.put(VariablesGlobales.campo_img, img);
        values.put(VariablesGlobales.campo_favorito, fav);

        try{
            Long id = db.insert(VariablesGlobales.tabla,VariablesGlobales.campo_titulo, values);
            db.close();
        }catch (SQLiteException s){
            s.getMessage();
        }catch (NullPointerException n){
            n.getMessage();
        }finally {
            Intent miIntent = new Intent(activity_editar_tarea.this, MainActivity.class);
            miIntent.putExtra("evento", titulo.getText().toString());//MANDAMOS EL TITULO DEL EVENTO
            activity_editar_tarea.this.finish();
            startActivity(miIntent);
        }

    }

    public static int obtenerPosicionItem(String tema) {
        int posicion = 0;
        for (int i = 0; i < listaImg.length; i++) {
            if (listaImg[i].equalsIgnoreCase(tema)) {
                posicion = i;
            }
        }
        return posicion;
    }

    private void editarEvento() {
        SharedPreferences datosEvento = getSharedPreferences("datos", Context.MODE_PRIVATE);
        String fav = "";
        try{
            titulo.setText(datosEvento.getString("titulo", "Error en la consulta"));
            descripcion.setText(datosEvento.getString("descripcion", "Error en la consulta"));
            fecha.setText(datosEvento.getString("fecha", "Error en la consulta"));
            hora.setText(datosEvento.getString("hora", "Error en la consulta"));
            url.setText(datosEvento.getString("url", "Error en la consulta"));
            temas.setSelection(obtenerPosicionItem(datosEvento.getString("temas", "Error en la consulta")));
            fav = datosEvento.getString("favorito", "Error en la consulta");
            if(Integer.parseInt(fav) == 1){
                favorito.setChecked(true);
            }
        }catch (NullPointerException e){
            e.getMessage();
        }
    }

    public void updateEvento() {
        System.out.println("Id del EVENTO dentro de la funcion UPLOAD: " + idEvento);
        String[] parametro = {idEvento};
        SQLiteDatabase db = conn.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(VariablesGlobales.campo_titulo, titulo.getText().toString());
        values.put(VariablesGlobales.campo_descripcion, descripcion.getText().toString());
        values.put(VariablesGlobales.campo_fecha, fecha.getText().toString());
        values.put(VariablesGlobales.campo_hora, hora.getText().toString());
        values.put(VariablesGlobales.campo_url, url.getText().toString());
        values.put(VariablesGlobales.campo_img, img);
        values.put(VariablesGlobales.campo_favorito, fav);

        try{
            int res = db.update(VariablesGlobales.tabla, values, VariablesGlobales.campo_id+"=?", parametro);
            if(res != 0){
                Toast.makeText(getApplicationContext(), "El Evento "+titulo.getText().toString()+" se ha actualizado en tú I`schedule", Toast.LENGTH_LONG).show();
            }
            db.close();
        }catch (SQLiteException s){
            s.getMessage();
        }catch (NullPointerException n){
            n.getMessage();
        }finally {
            Intent miIntent = new Intent(activity_editar_tarea.this, MainActivity.class);
            activity_editar_tarea.this.finish();
            startActivity(miIntent);
        }
    }
}
