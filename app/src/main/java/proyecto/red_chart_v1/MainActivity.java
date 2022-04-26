package proyecto.red_chart_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button buttonOnSendCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonOnSendCode = findViewById(R.id.btnSendCode);

        buttonOnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToCodeVerificationActivity();
            }
        });
    }
    //Método que nos lleva a la página de verificar código
    public void goToCodeVerificationActivity() {
        Intent intent = new Intent(MainActivity.this, CodeVerificationActivity.class);
        startActivity(intent);
    }
}
