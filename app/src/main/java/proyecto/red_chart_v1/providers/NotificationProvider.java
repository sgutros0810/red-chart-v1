package proyecto.red_chart_v1.providers;

import android.content.Context;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import proyecto.red_chart_v1.activities.ChatActivity;
import proyecto.red_chart_v1.activities.ConfirmImageSendActivity;
import proyecto.red_chart_v1.models.FCMBody;
import proyecto.red_chart_v1.models.FCMResponse;
import proyecto.red_chart_v1.retrofit.IFCMApi;
import proyecto.red_chart_v1.retrofit.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NotificationProvider {

    private String url = "https://fcm.googleapis.com"; //ruta donde se hace la petición

    //Contructor vacio
    public  NotificationProvider() {

    }

    //Método que envia la notificacion
    public Call<FCMResponse> sendNotification(FCMBody body) {
        return RetrofitClient.getClient(url).create(IFCMApi.class).send(body);
    }

    //Método que no retorna
    public void send(Context context, List<String> tokens, Map<String, String> data){   //token del usuario que envia la nofiticacion,
        FCMBody body = new FCMBody(tokens, "high", "4500s", data);    //EL TOKEN DEL USUARIO RECIBE LA NOTIFICACION, PRIORIDAD DEL MENSAJE, TIEMPO QUE SE MUESTRA Y LA INFORMACION QUE ENVIAMOS

        sendNotification(body).enqueue(new Callback<FCMResponse>() {
            //Si lo envia
            @Override
            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                //Si el cuerpo de la notificacion esta null
                if (response.body() != null) {
                    //Si no se envia correctamente
                    if (response.body().getSuccess() != 1) {
                        Toast.makeText(context, "La notificacion no se pudo enviar", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(context, "no hubo respuesta del servidor", Toast.LENGTH_SHORT).show();
                }

            }

            //Si falla
            @Override
            public void onFailure(Call<FCMResponse> call, Throwable t) {
                Toast.makeText(context, "Fallo la peticion con retrofit: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
