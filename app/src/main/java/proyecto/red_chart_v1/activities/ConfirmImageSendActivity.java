package proyecto.red_chart_v1.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import java.util.ArrayList;

import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.adapters.OptionsPagerAdapter;
import proyecto.red_chart_v1.utils.ShadowTransformer;

public class ConfirmImageSendActivity extends AppCompatActivity {

    ViewPager mViewPager;       //es un ViewGroup que permite desplazarnos por distintos layouts o «páginas» dentro de una misma Activity
    ArrayList<String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_image_send);

        setStatusBarColor();

        mViewPager = findViewById(R.id.viewPager);

        data = getIntent().getStringArrayListExtra("data");     //contiene la ruta de todas las imagenes seleccionadas

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

