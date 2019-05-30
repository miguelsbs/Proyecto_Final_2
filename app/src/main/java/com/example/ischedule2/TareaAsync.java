package com.example.ischedule2;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class TareaAsync extends AsyncTask<Void, Void, String> {

    private WeakReference<TextView> mensaje;
    TareaAsync(TextView msg) {
        mensaje = new WeakReference<>(msg);
    }

    @Override
    protected String doInBackground(Void... voids) {
        Log.d("TAG", "ENTRO AQUI");
        int s = 5000;
        try {
            Thread.sleep(s);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "Haz tu Evento favorito!!!";
    }

    protected void onPostExecute(String result) {
        mensaje.get().setText(result);
    }
}
