package proyecto.red_chart_v1.activities;

import androidx.appcompat.app.AppCompatActivity;
import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.fragments.BottomSheetSelectImage;
import proyecto.red_chart_v1.providers.UsersProvider;
import proyecto.red_chart_v1.utils.MyToolBarSimple;

import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

//Clase para editar el perfil del usuario
public class ProfileActivity extends AppCompatActivity {

    FloatingActionButton mFabSelectImage;
    BottomSheetSelectImage mBottomSheetSelectImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Muestra en el toolbar el titulo y muestra el boton si esta true
        MyToolBarSimple.show(this, "Perfil", true);


        mFabSelectImage = findViewById(R.id.fabSelectImage);
        mFabSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBottomSheetSelectImage();
            }
        });
    }

    // Muestra el bottom Sheet
    private void openBottomSheetSelectImage() {
        mBottomSheetSelectImage = BottomSheetSelectImage.newInstance();
        mBottomSheetSelectImage.show(getSupportFragmentManager(), mBottomSheetSelectImage.getTag());

    }
}