package proyecto.red_chart_v1.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.providers.AuthProvider;

public class HomeActivity extends AppCompatActivity {
    Button mButtonSignOut;
    AuthProvider mAuthProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mButtonSignOut = findViewById(R.id.btnSignOut);

        mAuthProvider = new AuthProvider();

        // Cuando pulsa cierra sesion
        mButtonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
    }

    // Método que cierra sesión
    private void signOut(){
        mAuthProvider.signOut();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);                                           // Borra el historial de pantallas
        startActivity(intent);
    }

}