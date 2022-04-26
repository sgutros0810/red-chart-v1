package proyecto.red_chart_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button buttonOnSendCode;
    EditText editTextPhome;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonOnSendCode = findViewById(R.id.btnSendCode);
        editTextPhome = findViewById(R.id.editTextPhone);

        buttonOnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //goToCodeVerificationActivity();
                getPhone();
            }
        });
    }

    //Método que captura lo que ha insertado el usuario
    public void getPhone(){
        String phone = editTextPhome.getText().toString();
        Toast.makeText(MainActivity.this, "Teléfono: " + phone,Toast.LENGTH_SHORT).show();
    }

    //Método que nos lleva a la página de verificar código
    public void goToCodeVerificationActivity() {
        Intent intent = new Intent(MainActivity.this, CodeVerificationActivity.class);
        startActivity(intent);
    }
}
