package proyecto.red_chart_v1.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;

import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.providers.AuthProvider;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "GGGG" ;
    Button mButtonOnSendCode;
    EditText mEditTextPhone;
    CountryCodePicker mCountryCodePicker;

    AuthProvider mAuthProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonOnSendCode = findViewById(R.id.btnSendCode);
        mEditTextPhone = findViewById(R.id.editTextPhone);
        mCountryCodePicker = findViewById(R.id.ccp);

        mAuthProvider = new AuthProvider();

        mButtonOnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //goToCodeVerificationActivity();
                getData();
            }
        });
    }

    //Validación para saber si el usuario ya se ha autenticado
    @Override
    protected void onStart() {
        super.onStart();
        //Si el usuario se ha autenticado
        if(mAuthProvider.getSessionUser() != null){
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);                                   // Dirige a HomeActivity
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);                                  // Borra el historial de pantallas
            startActivity(intent);
        }

    }

    //Método que captura los datos seleccionados
    public void getData(){

        String codeCountry = mCountryCodePicker.getSelectedCountryCodeWithPlus();    //obtiene el código del pais seleccionado
        String phone = mEditTextPhone.getText().toString();                          //captura el número de teléfono que ha insertado el usuario

        //si el telefono está vacio
       if(phone.equals("")){
           Toast.makeText(MainActivity.this, "Debe introducir el teléfono.",Toast.LENGTH_SHORT).show();
       }
       else {
           goToCodeVerificationActivity(codeCountry + phone);
       }
    }

    //Método que nos lleva a la página de verificar código
    public void goToCodeVerificationActivity(String phone) {
        Intent intent = new Intent(MainActivity.this, CodeVerificationActivity.class);
        intent.putExtra("phone", phone);
        startActivity(intent);

    }
}
