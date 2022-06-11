package proyecto.red_chart_v1.providers;

import proyecto.red_chart_v1.models.FCMBody;
import proyecto.red_chart_v1.models.FCMResponse;
import proyecto.red_chart_v1.retrofit.IFCMApi;
import proyecto.red_chart_v1.retrofit.RetrofitClient;
import retrofit2.Call;


public class NotificationProvider {

    private String url = "https://fcm.googleapis.com"; //ruta donde se hace la petición

    //Contructor vacio
    public  NotificationProvider() {

    }

    //Método que envia la notificacion
    public Call<FCMResponse> sendNotification(FCMBody body) {
        return RetrofitClient.getClient(url).create(IFCMApi.class).send(body);
    }
}
