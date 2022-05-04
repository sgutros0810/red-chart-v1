package proyecto.red_chart_v1.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.providers.AuthProvider;

public class CodeVerificationActivity extends AppCompatActivity {

    Button mButtonCodeVerification;
    EditText mEditTextCodeVerification;
    String mExtraPhone;
    String mVerificationId;
    AuthProvider mAuthProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_verification);

        mButtonCodeVerification = findViewById(R.id.btnCodeVerification);
        mEditTextCodeVerification = findViewById(R.id.editTextCodeVerification);

        mAuthProvider = new AuthProvider();
        mExtraPhone = getIntent().getStringExtra("phone"); // recoge el valor introducido del mainActivity

        mAuthProvider.sendCodeAuth(mExtraPhone, mCallbacks);

        mButtonCodeVerification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String code = mEditTextCodeVerification.getText().toString();
                if (!code.equals("") && code.length() >= 6) {
                    signIn(code);
                }
                else {
                    Toast.makeText(CodeVerificationActivity.this, "Ingresa el codigo", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    //Verificaciones de autenticación
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        //Verificación de manera exitosa
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();   //Retorna el código que se le envió al usuario en el SMS

            //Si el codigo es diferente de null
            if(code != null){
                mEditTextCodeVerification.setText(code);
                signIn(code);
            }

        }
        //Verificación fallida
        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {

        }

        //Método que obtiene la variable verificationId
        @Override
        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(verificationId, forceResendingToken);
            Toast.makeText(CodeVerificationActivity.this, "El código se envió", Toast.LENGTH_LONG).show();
            mVerificationId =  verificationId;
        }
    };

    //Método que logea con firebase
    private void signIn(String code) {
        mAuthProvider.signInPhone(mVerificationId, code).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(CodeVerificationActivity.this, "La autenticación fue exitosa", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(CodeVerificationActivity.this,"No se puedo autenticar el usuario", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
