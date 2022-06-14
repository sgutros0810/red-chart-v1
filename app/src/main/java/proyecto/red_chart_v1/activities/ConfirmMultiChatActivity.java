package proyecto.red_chart_v1.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.models.Chat;
import proyecto.red_chart_v1.models.User;

public class ConfirmMultiChatActivity extends AppCompatActivity {
    //Recibimos la informacion que hemos recibido en el chatJSON de la actividad 'AddMultiUsersActivity'

    Chat mExtraChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Obtemos la informacion del chat
        String chat = getIntent().getStringExtra("chat");
        Gson gson = new Gson();
        mExtraChat = gson.fromJson(chat, Chat.class);

        //PRUEBA
        for (String id: mExtraChat.getIds()) {
            Log.d("USUARIO", "nombre: " + id );
        }

    }


}