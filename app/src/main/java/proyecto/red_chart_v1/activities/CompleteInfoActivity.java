package proyecto.red_chart_v1.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.models.User;
import proyecto.red_chart_v1.providers.AuthProvider;
import proyecto.red_chart_v1.providers.UsersProvider;

public class CompleteInfoActivity extends AppCompatActivity {

    TextInputEditText mTextInputUsername;
    Button mButtonConfirm;

    UsersProvider mUserProvider;
    AuthProvider mAuthProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_info);

        mTextInputUsername = findViewById(R.id.textInputUsername);
        mButtonConfirm = findViewById(R.id.btnConfirm);

        mUserProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();

        //Vuando pulsa el boton, envía los datos
        mButtonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUserInfo();
            }
        });
    }

    //Método que envia a la base de datos la imagen de perfil y el nombre de usuario
    private void updateUserInfo() {
        String username = mTextInputUsername.getText().toString();  //nombre de usuario que introduce el usuario

        //Si no esta vacio el nombre de usuario
        if(!username.equals("")) {
            //Creamos el usuario
            User user = new User();
            user.setUsername(username);
            user.setId(mAuthProvider.getId());       //Con el UID del usuario, hacemos que se actualice.
            mUserProvider.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(CompleteInfoActivity.this, "La información se ha actualizado", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }
}