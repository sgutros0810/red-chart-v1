package proyecto.red_chart_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;

public class MainActivity extends AppCompatActivity {

    Button buttonOnSendCode;
    EditText editTextPhome;
    CountryCodePicker countryCodePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonOnSendCode = findViewById(R.id.btnSendCode);
        editTextPhome = findViewById(R.id.editTextPhone);
        countryCodePicker = findViewById(R.id.ccp);

        buttonOnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //goToCodeVerificationActivity();
                getData();
            }
        });
    }
    //Método que nos lleva a la página de verificar código
    public void goToCodeVerificationActivity() {
        Intent intent = new Intent(MainActivity.this, CodeVerificationActivity.class);
        startActivity(intent);
    }

    //Método que captura los datos seleccionados
    public void getData(){
        //obtiene el código del pais seleccionado
        String codeCountry = countryCodePicker.getSelectedCountryCodeWithPlus();
        //captura el número de teléfono que ha insertado el usuario
        String phone = editTextPhome.getText().toString();

        //si el telefono está vacio
       if(phone.equals("")){
           Toast.makeText(MainActivity.this, "Debe introducir el teléfono.",Toast.LENGTH_SHORT).show();
       }
       else {
           Toast.makeText(MainActivity.this, "Teléfono: " + codeCountry + " " + phone, Toast.LENGTH_SHORT).show();
       }
    }




}
