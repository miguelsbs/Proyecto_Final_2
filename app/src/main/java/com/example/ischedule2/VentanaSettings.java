package com.example.ischedule2;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class VentanaSettings extends AppCompatActivity {

   // private Switch swNoti;
    private Conexion conn;

    private String eventos = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ventana_settings);
        //swNoti = findViewById(R.id.sw_2);
        conn = new Conexion(this, "db_tareas", null, 1);

    }

    public void limpiar(View view) {
        AlertDialog.Builder aviso = new AlertDialog.Builder(view.getContext());
        aviso.setTitle("Aviso!");
        aviso.setMessage("¿Deseas vacíar la lista de Eventos?");
        aviso.setCancelable(false);
        aviso.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface aviso, int id) {
                SQLiteDatabase db = conn.getWritableDatabase();
                try{
                    db.delete(VariablesGlobales.tabla, null, null);
                    db.close();
                }catch (SQLiteException s){
                    s.getMessage();
                }finally {
                    Intent editar = new Intent(VentanaSettings.this, MainActivity.class);
                    VentanaSettings.this.finish();
                    startActivity(editar);
                }

            }
        });

        aviso.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface aviso, int id) {
                aviso.cancel();
            }
        });
        aviso.show();
    }

}
