package proyecto.red_chart_v1.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.adapters.OptionsPagerAdapter;
import proyecto.red_chart_v1.models.Message;
import proyecto.red_chart_v1.models.User;
import proyecto.red_chart_v1.providers.AuthProvider;
import proyecto.red_chart_v1.providers.ImageProvider;
import proyecto.red_chart_v1.providers.NotificationProvider;
import proyecto.red_chart_v1.utils.ExtensionFile;
import proyecto.red_chart_v1.utils.ShadowTransformer;

public class ConfirmImageSendActivity extends AppCompatActivity {

    ViewPager mViewPager;                               //es un ViewGroup que permite desplazarnos por distintos layouts o «páginas» dentro de una misma Activity
    String mExtraIdChat;                                //Id del chat
    String mExtraIdReceiver;                            //Id del usuario que recibe el mensaje
    String mExtraIdNotification;                        //Id de la notificacion
    ArrayList<String> data;                             //Array que guarda las rutas de cada iamgen
    ArrayList<Message> messages = new ArrayList<>();    //Array que guarda los mensajes de cada iamgen

    User mExtraUserSend;
    User mExtraUserReceiver;

    AuthProvider mAuthProvider;
    ImageProvider mImageProvider;
    NotificationProvider mNotificationProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_image_send);

        setStatusBarColor();

        mViewPager = findViewById(R.id.viewPager);

        mAuthProvider = new AuthProvider();
        mImageProvider = new ImageProvider();
        mNotificationProvider = new NotificationProvider();

        data = getIntent().getStringArrayListExtra("data");             //contiene la ruta de todas las imagenes seleccionadas
        mExtraIdChat = getIntent().getStringExtra("idChat");            //contiene el id del chat seleccionado
        mExtraIdReceiver = getIntent().getStringExtra("idReceiver");    //contiene el id del usuario que recibe el mensaje
        mExtraIdNotification = getIntent().getStringExtra("idNotification");    //contiene el id del usuario que recibe el mensaje


        String userSend = getIntent().getStringExtra("userSend");
        String userReceiver = getIntent().getStringExtra("userReceiver");

        //Convertimos los String a un Array de 'Message'
        Gson gson = new Gson();
        mExtraUserSend = gson.fromJson(userSend, User.class);
        mExtraUserReceiver = gson.fromJson(userReceiver, User.class);


        //Validación que 'data' sea diferente de null
        if(data != null) {
            //Recorre la variable 'data'(las rutas)
            for (int i = 0; i < data.size(); i++) {
                Message m = new Message();

                m.setIdChat(mExtraIdChat);                  //Id del chat donde vamos a almacenar las imagenes
                m.setIdSender(mAuthProvider.getId());       //Id del usuario que envia los mensajes
                m.setIdReceiver(mExtraIdReceiver);          //Id del usuario que recibe los mensajes
                m.setStatus("ENVIADO");                     //Estado del mensaje
                m.setTimestamp(new Date().getTime());       //Fecha de cuando envia el mensaje
                m.setUrl(data.get(i));                      //Url de la imagen que seleccionamos

                //Si la extension es una imagen
                if(ExtensionFile.isImageFile(data.get(i))) {
                    m.setType("imagen");                       //Tipo de mensaje -> 'imagen'
                    m.setMessage("\uD83D\uDCF7imagen");        //Emoticono de la camara + 'imagen'

                //Si la extension es un video
                } else if(ExtensionFile.isVideoFile(data.get(i))) {
                    m.setType("video");                        //Tipo de mensaje -> 'imagen'
                    m.setMessage("\uD83C\uDFA5video");        //Emoticono de la camara + 'imagen'
                }

                messages.add(m);                            //Añadimos el nuevo mensaje, es decir, lo que esta hecho anteriormente
            }
        }


        //Constructor de la clase 'OptionsPagerAdapter'
        OptionsPagerAdapter pagerAdapter = new OptionsPagerAdapter(
                getApplicationContext(),
                getSupportFragmentManager(),
                dpToPixels(2, this),
                data
        );


        ShadowTransformer transformer = new ShadowTransformer(mViewPager, pagerAdapter);
        transformer.enableScaling(true);

        mViewPager.setAdapter(pagerAdapter);
        mViewPager.setPageTransformer(false, transformer);
    }

    //Método que guarda y envia el texto del editText de cada imagen a la base de datos
    public void send(){
        //almacena varios archivos
        mImageProvider.uploadMultiple(ConfirmImageSendActivity.this, messages);

        //Si es una imagen
        final Message message = new Message();
        message.setIdChat(mExtraIdChat);
        message.setIdSender(mAuthProvider.getId());
        message.setIdReceiver(mExtraIdReceiver);
        message.setMessage("\uD83D\uDCF7 Imagen");   //Si es una imagen
        message.setStatus("ENVIADO");
        message.setType("texto");
        message.setTimestamp(new Date().getTime());
        ArrayList<Message> messages = new ArrayList<>();
        messages.add(message);  //Lo añadimos

        sendNotification(messages);

        //se cierra
        finish();
    }

    //Método que envia la notificacion cuando cree el mensaje
    private void sendNotification(ArrayList<Message> messages) {
        //Envía la notificacion //saber el token del usuario que le envia la notificación
        Map<String, String> data = new HashMap<>();                                        //Variable que sirve para transmitir lo que se muestre en la app
        data.put("title", "MENSAJE");                                                //Titulo
        data.put("body", "texto mensaje");                                           //Body el mensaje que recibimos
        data.put("idNotification", String.valueOf(mExtraIdNotification));               //ID de la notificacion
        data.put("usernameReceiver", mExtraUserReceiver.getUsername());                 //Nombre del usuario que recibe
        data.put("usernameSender", mExtraUserSend.getUsername());                       //Nombre del usuario que envia
        data.put("imageReceiver", mExtraUserReceiver.getImage());                       //Imagen de perfil del usuario que recibe
        data.put("imageSender", mExtraUserSend.getImage());
        data.put("idChat", mExtraIdChat);
        data.put("idSender", mAuthProvider.getId());
        data.put("idReceiver", mExtraIdReceiver);
        data.put("tokenSender", mExtraUserSend.getToken());
        data.put("tokenReceiver", mExtraUserReceiver.getToken());

        Gson gson = new Gson();
        String menssagesJSON = gson.toJson(messages);                                      //Convierto el array 'messages' a un JSON
        data.put("menssagesJSON", menssagesJSON);                                       //Los ultimos 5 mensajes no leidos del chat


        List<String> tokens = new ArrayList<>();                                           //Token del usuario del chat
        tokens.add(mExtraUserReceiver.getToken());                                         //Añade el token del usuario al que enviamos


        //Envia la notificacion al usuario que recibe el mensaje
        mNotificationProvider.send(ConfirmImageSendActivity.this, tokens, data);
    }



    //Método que recoge el texto del editText de cada imagen
    public void setMessages(int position, String message){
        if (message.equals("")) {
            message = "\uD83D\uDCF7imagen";
        }
        messages.get(position).setMessage(message);
    }

    //Método que recibe la densidad de pixeles
    public static float dpToPixels(int dp, Context context) {
        return dp * (context.getResources().getDisplayMetrics().density);
    }

    //Cambia el color de la barra de estado del movil cuando aparece el fragment de enviar imagenes
    private void setStatusBarColor(){
        //Si la version de Android en la que esta la app instalada es mayor a M
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorFullBlack, this.getTheme()));
        }
        //Si la version de Android en la que esta la app instalada es mayor a LOLLIPOP
        else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorFullBlack));
        }
    }

}

