package proyecto.red_chart_v1.activities;

import androidx.appcompat.app.AppCompatActivity;
import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.utils.MyToolBarSimple;

import android.os.Bundle;

//Clase para editar el perfil del usuario
public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        // Muestra en el toolbar el titulo y muestra el boton si esta true
        MyToolBarSimple.show(this, "Perfil", true);
    }
}