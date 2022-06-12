package proyecto.red_chart_v1.services;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Map;
import java.util.Random;

import proyecto.red_chart_v1.activities.ChatActivity;
import proyecto.red_chart_v1.activities.HomeActivity;
import proyecto.red_chart_v1.channel.NotificationHelper;
import proyecto.red_chart_v1.models.Message;

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

            //Si el titulo es igual a 'MENSAJE'
            if (title.equals("MENSAJE")) {
                getImageReceiver(data);
            }
            else {
                showNotification(title, body, idNotification);
            }
        }
    }


    //Método que muestra la notificación
    private void showNotification(String title, String body, String idNotification) {
        NotificationHelper helper = new NotificationHelper(getBaseContext());
        NotificationCompat.Builder builder = helper.getNotification(title, body);

        int id = Integer.parseInt(idNotification);  //Pasamos el String a un int
        Log.d("NOTIFICACION", "ID :" + id);
        helper.getManager().notify(id, builder.build());
    }


    private void getImageReceiver(final Map<String, String> data){
        final String imageReceiver = data.get("imageReceiver");               //Obtiene la imagen

        Log.d("NOTIFICACION", "ID: " + imageReceiver);

        //Si tiene la imagen por defecto o vacio
        if(imageReceiver == null || imageReceiver.equals("")){
            showNotificationMessage(data, null);          //Muestra la notificacion y la imagen (url) por defecto
            return;
        }


        new Handler(Looper.getMainLooper())
                .post(new Runnable() {
                    @Override
                    public void run() {
                        Picasso.get().load(imageReceiver).into(new Target() {

                            //Cuando la imagen (url) se haya cargado correctamente, nos devuelve un bitmap
                            @Override
                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                showNotificationMessage(data, bitmap);          //Muestra la notificacion y la imagen (url)
                            }

                            //En caso de que no exista en la bd y no se pudo cargar
                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                showNotificationMessage(data, null);          //Muestra la notificación, pero el bitmap (imagen) = null
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
                    }
                });


    }

    //Método que muestra el contenido de la notificación
    private void showNotificationMessage(Map<String, String> data, Bitmap bitmapReceiver) {
        //String body = data.get("body");                               //Captura el valor del body
        String idNotification = data.get("idNotification");             //Captura el valor del idNotification
        String usernameSender = data.get("usernameSender");             //Captura el valor del usernameSender
        String usernameReceiver = data.get("usernameReceiver");         //Captura el valor del usernameReceiver
        String menssagesJSON = data.get("menssagesJSON");               //Captura el valor de un String de los ultimos 5 mensajes no leidos
        int id = Integer.parseInt(idNotification);

        String idChat = data.get("idChat");
        String idSender = data.get("idSender");
        String idReceiver = data.get("idReceiver");

        Gson gson = new Gson();
        //Convertimos el String a un Array de 'Message'
        Message[] messages = gson.fromJson(menssagesJSON, Message[].class);

        NotificationHelper helper = new NotificationHelper(getBaseContext());

        //Cuando pincha una notificacion va al 'HomeActivity'
        Intent chatIntent = new Intent(getApplicationContext(), HomeActivity.class);
        chatIntent.putExtra("idUser", idSender);
        chatIntent.putExtra("idChat", idChat);
        PendingIntent contentIntent = PendingIntent.getActivity(getBaseContext(), id, chatIntent, PendingIntent.FLAG_ONE_SHOT);


        NotificationCompat.Builder builder = helper.getNotificationMessage(
                messages, usernameReceiver, usernameSender, bitmapReceiver, contentIntent);


        //Pruebas
        Log.d("NOTIFICACION", "ID: " + id);
        Log.d("NOTIFICACION", "usernameSender: " + usernameSender);
        Log.d("NOTIFICACION", "usernameReceiver: " + usernameReceiver);


        helper.getManager().notify(id, builder.build());
    }

}

