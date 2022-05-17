package proyecto.red_chart_v1.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;


import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.models.User;
import proyecto.red_chart_v1.providers.AuthProvider;
import proyecto.red_chart_v1.providers.UsersProvider;

public class CodeVerificationActivity extends AppCompatActivity {

    Button mButtonCodeVerification;
    EditText mEditTextCodeVerification;
    TextView mTextViewSMS;
    ProgressBar mProgressBar;

    String mExtraPhone;
    String mVerificationId;

    AuthProvider mAuthProvider;
    UsersProvider mUsersProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_code_verification);

        mButtonCodeVerification = findViewById(R.id.btnCodeVerification);
        mEditTextCodeVerification = findViewById(R.id.editTextCodeVerification);
        mTextViewSMS = findViewById(R.id.textViewSMS);
        mProgressBar = findViewById(R.id.progressBar);


        mAuthProvider = new AuthProvider();
        mUsersProvider = new UsersProvider();

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
        //Si la verificación es de manera exitosa
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            mProgressBar.setVisibility(View.GONE); //Se oculta el progress bar
            mTextViewSMS.setVisibility(View.GONE); //Se oculta el texto

            String code = phoneAuthCredential.getSmsCode();   //Retorna el código que se le envió al usuario en el SMS

            //Si el codigo es diferente de null
            if(code != null){
                mEditTextCodeVerification.setText(code);
                signIn(code);
            }

        }
        //Si la verificación es fallida
        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            mProgressBar.setVisibility(View.GONE); //Se oculta el progress bar
            mTextViewSMS.setVisibility(View.GONE); //Se oculta el texto
            //Mensaje que muestra el error
            Toast.makeText(CodeVerificationActivity.this, "Se produjo un error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }

        //Método que obtiene la variable verificationId
        @Override
        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(verificationId, forceResendingToken);
            Toast.makeText(CodeVerificationActivity.this, "El código se envió", Toast.LENGTH_SHORT).show();
            mVerificationId =  verificationId;
        }
    };

    //Método que logea con firebase
    private void signIn(String code) {
        mAuthProvider.signInPhone(mVerificationId, code).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    final User user = new User();
                    user.setId(mAuthProvider.getId());  //Recoge el UID del usuario
                    user.setPhone(mExtraPhone);         //Recoge el teléfono del usuario

                    //Valida si existe el UID del usuario
                    mUsersProvider.getUserInfo(mAuthProvider.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            //Si no existe el UID
                            if(!documentSnapshot.exists()){
                                //Crea el usuario
                                mUsersProvider.create(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        goToCompleteInfo();
                                    }
                                });

                            // Si ya existe en la base de datos de ese usuario un nombre de usuario y una imagen
                            } else if(documentSnapshot.contains("username") && documentSnapshot.contains("image")) {
                                String username = documentSnapshot.getString("username");       // Recupera el nombre del usuario
                                String image = documentSnapshot.getString("image");             // Recupera la imagen de perfil del usuario

                                // Diferente de null
                                if(username != null && image != null) {
                                    // Si el nombre de usuario y la imagen no esta vacio
                                    if(!username.equals("") && !image.equals("")){
                                        // Entonces, el usuario habia agregado anteriormente una imagen y un nombre de usuario
                                        goToHomeActivity();

                                    // No tiene en la base de datos información guardada
                                    } else {
                                        goToCompleteInfo();     // Nos dirige a la actividad
                                    }

                                // No tiene informacion guardada
                                } else {
                                    goToCompleteInfo();     // Nos dirige a la actividad
                                }
                            }
                        }
                    });
                } else {
                    Toast.makeText(CodeVerificationActivity.this,"No se puedo autenticar el usuario", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Metodo que dirige a HomeActivity
    private void goToHomeActivity() {
        Intent intent = new Intent(CodeVerificationActivity.this, HomeActivity.class);                                   // Dirige a HomeActivity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);                                           // Borra el historial de pantallas
        startActivity(intent);
    }

    //Método que pasa a la actividad 'CompleteInfoActivity'
    private void goToCompleteInfo() {
        Intent intent = new Intent(CodeVerificationActivity.this, CompleteInfoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);                                           // Borra el historial de pantallas
        startActivity(intent);
    }
}
