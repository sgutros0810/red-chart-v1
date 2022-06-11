package proyecto.red_chart_v1.retrofit;

import proyecto.red_chart_v1.models.FCMBody;
import proyecto.red_chart_v1.models.FCMResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAQZK6ArQ:APA91bEOcVpNjcczZVi8fnKqtD8uBgYEAYfKWkt81Pc2654OdAE4nltPy8A7wjhMSV4q3Y1uQMWii5h4GoyE_k6w_4pC6raDFlvc1LXCKqebZike0XVsHI0HHW16I9DVwnaLjuUKLhUm"
    })


    @POST("fcm/send")   //tipo de peticion que pide un string a la ruta
    Call<FCMResponse> send(@Body FCMBody body); //Hace la peticion a Firebase

}
