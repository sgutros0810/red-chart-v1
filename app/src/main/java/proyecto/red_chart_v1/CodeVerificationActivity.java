package proyecto.red_chart_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class CodeVerificationActivity extends AppCompatActivity {

    Button buttonCodeVerification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_verification);

        buttonCodeVerification = findViewById(R.id.btnCodeVerification);

        buttonCodeVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(CodeVerificationActivity.this, "El usuario pichó el botón", Toast.LENGTH_LONG).show();
            }
        });
    }
}
