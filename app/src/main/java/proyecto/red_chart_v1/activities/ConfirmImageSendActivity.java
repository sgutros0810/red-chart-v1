package proyecto.red_chart_v1.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.adapters.OptionsPagerAdapter;
import proyecto.red_chart_v1.models.Message;
import proyecto.red_chart_v1.providers.AuthProvider;
import proyecto.red_chart_v1.providers.ImageProvider;
import proyecto.red_chart_v1.utils.ShadowTransformer;

public class ConfirmImageSendActivity extends AppCompatActivity {

    ViewPager mViewPager;                               //es un ViewGroup que permite desplazarnos por distintos layouts o «páginas» dentro de una misma Activity
    String mExtraIdChat;                                //Id del chat
    String mExtraIdReceiver;                            //Id del usuario que recibe el mensaje
    ArrayList<String> data;                             //Array que guarda las rutas de cada iamgen
    ArrayList<Message> messages = new ArrayList<>();    //Array que guarda los mensajes de cada iamgen

    AuthProvider mAuthProvider;
    ImageProvider mImageProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_image_send);

        setStatusBarColor();

        mViewPager = findViewById(R.id.viewPager);
        mAuthProvider = new AuthProvider();
        mImageProvider = new ImageProvider();

        data = getIntent().getStringArrayListExtra("data");     //contiene la ruta de todas las imagenes seleccionadas
        mExtraIdChat = getIntent().getStringExtra("idChat");    //contiene el id del chat seleccionado
        mExtraIdReceiver = getIntent().getStringExtra("idReceiver");    //contiene el id del usuario que recibe el mensaje


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
                m.setType("imagen");                        //Tipo de mensaje -> 'imagen'
                m.setUrl(data.get(i));                      //Url de la imagen que seleccionamos
                m.setMessage("\uD83D\uDCF7imagen");        //Emoticono de la camara + 'imagen'

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

        //se cierra
        finish();
    }

    //Método que recoge el texto del editText de cada imagen
    public void setMessages(int position, String message){
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

