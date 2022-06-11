package proyecto.red_chart_v1.services;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

import proyecto.red_chart_v1.channel.NotificationHelper;

public class MyFirebaseMessagingClient extends FirebaseMessagingService {

    //Método que crea un token de notificaciones, que permite enviar las notificaciones
    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

    }

    //Método que sirve para recibir los datos que se envía en la notificación
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        //obtener la informacion que nos envian en la notificacion
        Map<String, String> data = remoteMessage.getData();             //OBTIENE TODA LA INFORMACIÓN QUE ENVIA LA NOTIFICACION
        String title = data.get("title");                               //Captura el valor del titulo
        String body = data.get("body");                                 //Captura el valor del body
        String idNotification = data.get("idNotification");             //Captura el valor del idNotification

        //Si el titulo no es nulo (la notificaión tiene al menos un titulo)
        if (title != null) {
            showNotification(title, body, idNotification);
        }
    }


    //Método que muestra la notificación
    private void showNotification(String title, String body, String idNotification) {
        NotificationHelper helper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder = helper.getNotification(title, body);

        int id = Integer.parseInt(idNotification);  //Pasamos el String a un int
        //Log.d("NOTIFICACION", "ID :" + id);
        helper.getManager().notify(id, builder.build());
    }

}

