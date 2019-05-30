package com.example.ischedule2;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class MiServicio extends JobService {


    @Override
    public boolean onStartJob(JobParameters JobParameters) {

        llamarNotificacion(JobParameters);
        return true;
    }

    private void llamarNotificacion(final JobParameters jobParameters){

        SharedPreferences datosEvento = getSharedPreferences("eventosPendientes", Context.MODE_PRIVATE);
        Toast.makeText(getApplicationContext(), "Eventos Pendientes:\n"+datosEvento.getString("titulo", "Error en la consulta")+"\n", Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d("TAG", "Servicio Cancelado");
        return false;
    }
}
