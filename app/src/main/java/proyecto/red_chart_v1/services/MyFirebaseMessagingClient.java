package proyecto.red_chart_v1.services;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

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
    }

}

