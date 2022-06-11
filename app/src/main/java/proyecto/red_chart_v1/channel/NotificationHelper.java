package proyecto.red_chart_v1.channel;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.models.Message;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;
import androidx.core.graphics.drawable.IconCompat;

import java.util.Date;

public class NotificationHelper extends ContextWrapper {

    private static final String CHANNEL_ID = "proyecto.red_chart_v1";   //Referencia del proyecto
    private static final String CHANNEL_NAME = "red-chart-v1";          //Nombre del proyecto

    private NotificationManager manager;

    //Contructor
    public NotificationHelper(Context base) {
        super(base);

        //Si la version es igual o superior a O (Android version 8.0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels();
        }
    }

    //Metodo que crea canal de notificacion para recibir los mensajes
    @RequiresApi(api = Build.VERSION_CODES.O) //Este metodo se ejecuta cuando la version es igual o superior a O
    private void createChannels() {
        NotificationChannel notificationChannel = new NotificationChannel(
                CHANNEL_ID,                             //id del canal de notificaciones que utilizamos
                CHANNEL_NAME,                           //Nombre del proyecto
                NotificationManager.IMPORTANCE_HIGH     //importancia del canal de notificaciones
        );

        notificationChannel.enableLights(true);                                             //Habilitado la luz de notificacion
        notificationChannel.enableVibration(true);                                          //Habilitado la vibración
        notificationChannel.setLightColor(Color.GRAY);                                      //Color de la luz de notificacion  -> Gris
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(notificationChannel);                         //Crea el canal de notificacion
    }

    //Método
    public NotificationManager getManager() {
        //Si aun no se ha instanciado 'manager'
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    //Metodo de Configuracion de la notificacion de la app
    public NotificationCompat.Builder getNotification(String title, String body) {

        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)                               //Muestra el titulo de la notificacion
                .setContentText(body)                                 //Cuerpo de la notificacion
                .setAutoCancel(true)
                .setColor(Color.GRAY)                                 //Color de la notificacion
                .setSmallIcon(R.mipmap.ic_icono_round)                //Icono
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title));   //Estilo
    }

    //Método de configuracion de estilos de la notificacion
    public NotificationCompat.Builder getNotificationMessage (
            Message[] messages,
            String usernameReceiver,
            String usernameSender,
            Bitmap bitmapReceiver
    ) {
        //Persona que envía el mensaje
        Person sendPerson = new Person.Builder()
                .setName(usernameSender)                                                                        //Nombre del usuario que envia el mensaje
                .setIcon(IconCompat.createWithResource(getApplicationContext(), R.drawable.ic_perfil_person))   //Icono de la persona
                .build();                                                                                       //Crea el objeto

        Person receiverPerson = null;

        //Si el usuario no tiene ninguna imagen de perfil
        if (bitmapReceiver == null) {

            //Persona que recibe el mensaje
            receiverPerson = new Person.Builder()
                    .setName(usernameReceiver)                                                                      //Nombre del usuario que envia el mensaje
                    .setIcon(IconCompat.createWithResource(getApplicationContext(), R.drawable.ic_perfil_person))   //Icono de la persona
                    .build();

        } else {

            //Persona que recibe el mensaje
            receiverPerson = new Person.Builder()
                    .setName(usernameReceiver)                                                                      //Nombre del usuario que envia el mensaje
                    .setIcon(IconCompat.createWithBitmap(bitmapReceiver))   //Icono de la persona
                    .build();
        }

        //Persona que recibe la notificacion se le añade el estilo
        NotificationCompat.MessagingStyle messagingStyle = new NotificationCompat.MessagingStyle(receiverPerson);

        //Recorre el array de mensajes (Recorre los ultimos mensajes no leidos del chat)
        for (Message m: messages) {
            //Configuracion del estilo de notificacion
            NotificationCompat.MessagingStyle.Message messageNotification = new NotificationCompat.MessagingStyle.Message(
                    m.getMessage(),         //Mensaje
                    m.getTimestamp(),       //Cuando se envio el mensaje
                    receiverPerson          //Persona que recibe el mensaje
            );

            //Añade el mensaje
            messagingStyle.addMessage(messageNotification);
        }

        return new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_icono_round)      //Icono por defecto
                .setStyle(messagingStyle);                  //Se aplica el estilo creado

    }


}
