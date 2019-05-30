package com.example.ischedule2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Datos_Mask> listaDatos;
    private RecyclerView tareas;
    private Conexion conn;

    private static final String ACTION_UPDATE_NOTIFICATION = "com.android.example.notifyme.ACTION_UPDATE_NOTIFICATION";
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private static final int NOTIFICATION_ID = 0;

    private NotificationManager mNotifyManager;
    private NotificationReceiver mReceiver = new NotificationReceiver();
    private String evento = "";
    private String servicioDatos = "";

    private final static int ID_SERVICE = 69;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent miIntent = new Intent(MainActivity.this, activity_editar_tarea.class);
                startActivity(miIntent);

            }
        });
        conn = new Conexion(this, "db_tareas", null, 1);
        listaDatos = new ArrayList<Datos_Mask>();
        tareas = (RecyclerView) findViewById(R.id.listaTareas);
        tareas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        llenarLista();
        Adaptador_Mask adapter = new Adaptador_Mask(listaDatos);
        final Intent MiIntent = new Intent(MainActivity.this, Evento.class);

        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = String.valueOf(listaDatos.get(tareas.getChildAdapterPosition(v)).getId());
                MiIntent.putExtra("id", id);
                startActivity(MiIntent);
            }
        });
        tareas.setAdapter(adapter);


        //NOTIFICACIONES
        createNotificationChannel();
        registerReceiver(mReceiver, new IntentFilter(ACTION_UPDATE_NOTIFICATION));

        //SI VENIMOS DE CREAR UN NUEVO EVENTO
        evento = getIntent().getStringExtra("evento");
        if(!Objects.equals(evento, null)){
            sendNotification(evento);
        }

        //MODIFICAR ESTO
        //sendNotification("IR AL CINE");

    }

    private void llenarLista() {
        SQLiteDatabase db = conn.getReadableDatabase();
        Datos_Mask datos = null;
        Cursor cursor = db.rawQuery(VariablesGlobales.Consul_select+VariablesGlobales.tabla, null);
        //Cursor cursor = db.rawQuery(variables_globales.Consul_select+variables_globales.tabla+" WHERE "+variables_globales.campo_favorito+"=1", null);
        while(cursor.moveToNext()){
            datos = new Datos_Mask(cursor.getString(1),cursor.getString(2),(int) enviarDatos(cursor.getString(6)), cursor.getInt(0));
            listaDatos.add(datos);
        }
        db.close();
    }

    private int enviarDatos(String nomImg) {
        int aux = 0;
        int imgPosition[] = {R.drawable.temas, R.drawable.boda, R.drawable.cita_negocios, R.drawable.cita_romantica, R.drawable.comida, R.drawable.compras, R.drawable.concierto, R.drawable.cumpleanos, R.drawable.estudios, R.drawable.examen, R.drawable.medico, R.drawable.oficina, R.drawable.partido, R.drawable.reunion_amigos, R.drawable.reunion_familiar, R.drawable.vacaciones, R.drawable.viaje};
        String listaImg[] = new String[]{"Temas", "Boda", "Cita de Negocios", "Cita Romantica",
                "Comida", "Compras", "Concierto", "Cumpleaños", "Estudios", "Examen", "Médico",
                "Oficina", "Partido", "Reunión Amigos", "Reunión Familiar", "Vacaciones", "Viaje"};
        for(int i=0; i < listaImg.length; i++){
            if(Objects.equals(listaImg[i], nomImg)){
                aux = imgPosition[i];
            }else if(nomImg == null){
                aux = imgPosition[0];
            }
        }
        return aux;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settings = new Intent(this, VentanaSettings.class);
            startActivity(settings);
            return true;
        }else if(id == R.id.action_favoritos){
            favoritos(item);
            return true;
        }else if(id == R.id.action_nofav){
            favoritosNo(item);
            return true;
        }else if(id == R.id.action_info){
            //  servicioDatos = "Datos Conectados!";
            Toast.makeText(getApplicationContext(), servicioDatos, Toast.LENGTH_LONG).show();
            return true;
        }else if(id == R.id.action_job){
            llamarServicio();
        }

        return super.onOptionsItemSelected(item);
    }

    private void llamarServicio() {
        SharedPreferences datosEvento = getSharedPreferences("eventosPendientes", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = datosEvento.edit();

        String aux = "";
        aux = actiNoti();
        editor.putString("titulo", aux);
        editor.commit();

        ComponentName componentName = new ComponentName(getApplicationContext(), MiServicio.class);
        JobInfo info;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            info = new JobInfo.Builder(ID_SERVICE, componentName)
                    .setRequiresCharging(false)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .setMinimumLatency(1000)
                    .build();
        }else{
            info = new JobInfo.Builder(ID_SERVICE, componentName)
                    .setRequiresCharging(false)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                    .setPersisted(true)
                    .setPeriodic(1000)
                    .build();
        }
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultado = scheduler.schedule(info);
        if(resultado == JobScheduler.RESULT_SUCCESS){
            Log.d("TAG", "Servicio Acabado!");
        }else{
            Log.d("TAG", "Servicio ha FALLADO!");
        }

    }

    public String actiNoti() {
       String title = "";
       SQLiteDatabase db = conn.getReadableDatabase();
       ArrayList<String> titulos = new ArrayList<>();
       ArrayList<String> fechas = new ArrayList<>();
       String fecevento;
            try{
                Cursor cursor = db.rawQuery(VariablesGlobales.Consul_select+VariablesGlobales.tabla, null);
                while(cursor.moveToNext()){
                    //  System.out.println("Fecha del evento: " + cursor.getString(3));
                    fechas.add(cursor.getString(3));
                    titulos.add(cursor.getString(1));
                }
                db.close();
                title = formateo(fechas, titulos);
            }catch (SQLiteException s){
                s.getMessage();
            }
        return title;
    }

    public String formateo(ArrayList fechas, ArrayList titulos){
        String eventos = "";
        String hoy = new SimpleDateFormat("ddMMyyyy").format(new Date());
        System.out.println("HOY: "+ hoy);
        ArrayList<String> newfecha = null;
        ArrayList<String> newtitulos = new ArrayList<String>();
        String aux = null;
        for(int i=0; i < fechas.size(); i++){
            aux = fechas.get(i).toString().replaceAll("[-+.^/:,]","");
            System.out.println("Fecha modificada: "+ aux);
            if(Objects.equals(aux, hoy)){
//                System.out.println("Eventos de hoy: "+ aux);
//                System.out.println("Titulo" + titulos.get(i).toString());
                newtitulos.add(titulos.get(i).toString());
                eventos += titulos.get(i).toString() + ".\n";
            }
        }
        // System.out.println(eventos);
        return eventos;
    }

    public void favoritos(MenuItem item) {
        Toast.makeText(getApplicationContext(), "Favoritos Ocultos", Toast.LENGTH_LONG).show();
        final ArrayList<Datos_Mask> listaDatos = new ArrayList<Datos_Mask>();
        final RecyclerView tareas = (RecyclerView) findViewById(R.id.listaTareas);
        tareas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        SQLiteDatabase db = conn.getReadableDatabase();
        Datos_Mask datos = null;
        Cursor cursor = db.rawQuery(VariablesGlobales.Consul_select+VariablesGlobales.tabla+" WHERE "+VariablesGlobales.campo_favorito+"=0", null);
        while(cursor.moveToNext()){
            datos = new Datos_Mask(cursor.getString(1),cursor.getString(2),(int) enviarDatos(cursor.getString(6)), cursor.getInt(0));
            listaDatos.add(datos);
        }
        db.close();
        Adaptador_Mask adapter2 = new Adaptador_Mask(listaDatos);
        final Intent ir = new Intent(MainActivity.this, Evento.class);
        adapter2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = String.valueOf(listaDatos.get(tareas.getChildAdapterPosition(v)).getId());
                ir.putExtra("id", id);
                startActivity(ir);
            }
        });
        tareas.setAdapter(adapter2);
    }

    public void favoritosNo(MenuItem item) {
        Toast.makeText(getApplicationContext(), "Incluir Favoritos", Toast.LENGTH_LONG).show();
        final ArrayList<Datos_Mask> listaDatos = new ArrayList<Datos_Mask>();
        final RecyclerView tareas = (RecyclerView) findViewById(R.id.listaTareas);
        tareas.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        SQLiteDatabase db = conn.getReadableDatabase();
        Datos_Mask datos = null;
        Cursor cursor = db.rawQuery(VariablesGlobales.Consul_select+VariablesGlobales.tabla, null);
        while(cursor.moveToNext()){
            System.out.println("ENTRO EN ESTE BUCLE");
            datos = new Datos_Mask(cursor.getString(1),cursor.getString(2),(int) enviarDatos(cursor.getString(6)), cursor.getInt(0));
            listaDatos.add(datos);
        }
        db.close();
        Adaptador_Mask adapter2 = new Adaptador_Mask(listaDatos);
        final Intent ir = new Intent(MainActivity.this, Evento.class);
        adapter2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = String.valueOf(listaDatos.get(tareas.getChildAdapterPosition(v)).getId());
                ir.putExtra("id", id);
                startActivity(ir);
            }
        });
        tareas.setAdapter(adapter2);
    }

    ///////////////////////////////////////////////////////
    ///////////////NOTIFICACIONES/////////////////////////
    /////////////////////////////////////////////////////
    @Override
    protected void onDestroy() {
        unregisterReceiver(mReceiver);
        super.onDestroy();
    }

    public void createNotificationChannel() {
        mNotifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(PRIMARY_CHANNEL_ID,"Mascot Notification", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setDescription(getString(R.string.notification_channel_description));
            mNotifyManager.createNotificationChannel(notificationChannel);
        }
    }

    private NotificationCompat.Builder getNotificationBuilder(String evento) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notifyBuilder = new NotificationCompat
                .Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle(getString(R.string.tituloNotificacion))
                .setContentText(evento)
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(true).setContentIntent(notificationPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL);
        return notifyBuilder;
    }

    public void sendNotification(String titulo) {
        Intent updateIntent = new Intent(ACTION_UPDATE_NOTIFICATION);
        PendingIntent updatePendingIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, updateIntent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notifyBuilder = getNotificationBuilder(titulo);
        mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());

    }

    public class NotificationReceiver extends BroadcastReceiver {

        public NotificationReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            // Update the notification.
            //updateNotification();
        }
    }


    //////////////////////////////////////////////////
    /////////////BROADCAST//////////////////////////
    ////////////////////////////////////////////////

    private BroadcastReceiver networkStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo info = manager.getActiveNetworkInfo();
            verificarConexion(info);
        }
    };
    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));
    }
    @Override
    public void onPause() {
        unregisterReceiver(networkStateReceiver);
        super.onPause();
    }
    private void verificarConexion(NetworkInfo networkInfo) {
        if (networkInfo != null) {
            if (networkInfo.isConnected()) {
                servicioDatos = "Datos Conectados!";
            }
        }else{
            servicioDatos = "Datos Desconectados!";
        }
    }

}
