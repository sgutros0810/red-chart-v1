package proyecto.red_chart_v1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hbb20.CountryCodePicker;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "GGGG" ;
    Button buttonOnSendCode;
    EditText editTextPhome;
    CountryCodePicker countryCodePicker;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonOnSendCode = findViewById(R.id.btnSendCode);
        editTextPhome = findViewById(R.id.editTextPhone);
        countryCodePicker = findViewById(R.id.ccp);
        firestore = FirebaseFirestore.getInstance();


        buttonOnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //goToCodeVerificationActivity();
                getData();
            }
        });

        saveData();
    }
    //Método de prueba
    private void saveData(){
        Map<String,Object> map = new HashMap<>();
        //almacena un nombre
        map.put("name","Sofia");
        map.put("nombre","Laura");
        //firestore.collection("Usuarios").document().set(map);
        firestore.collection("Usuarios")
                .add(map)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
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
           //Mensaje largo
           Toast.makeText(MainActivity.this, "Teléfono: " + codeCountry + " " + phone, Toast.LENGTH_SHORT).show();
       }
    }




}
