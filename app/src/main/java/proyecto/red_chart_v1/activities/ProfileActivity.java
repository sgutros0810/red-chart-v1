package proyecto.red_chart_v1.activities;

import androidx.appcompat.app.AppCompatActivity;

import de.hdodenhof.circleimageview.CircleImageView;
import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.fragments.BottomSheetSelectImage;
import proyecto.red_chart_v1.models.User;
import proyecto.red_chart_v1.providers.AuthProvider;
import proyecto.red_chart_v1.providers.UsersProvider;
import proyecto.red_chart_v1.utils.MyToolBarSimple;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

//Clase para editar el perfil del usuario
public class ProfileActivity extends AppCompatActivity {

    FloatingActionButton mFabSelectImage;
    BottomSheetSelectImage mBottomSheetSelectImage;

    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;

    TextView mTextViewUsername;
    //TextView mTextViewStatus;
    TextView mTextViewPhone;
    CircleImageView mCircleImageProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Muestra en el toolbar el titulo y muestra el boton si esta true
        MyToolBarSimple.show(this, "Perfil", true);

        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();

        mTextViewUsername = findViewById(R.id.textViewUsername);
        //mTextViewStatus = findViewById(R.id.textViewStatus);
        mTextViewPhone = findViewById(R.id.textViewPhone);
        mCircleImageProfile = findViewById(R.id.circleImageProfile);

        mFabSelectImage = findViewById(R.id.fabSelectImage);
        mFabSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBottomSheetSelectImage();
            }
        });

        getUserInfo();
    }

    //Metodo que devuelve la informacion del usuario
    private void getUserInfo() {
        mUsersProvider.getUserInfo(mAuthProvider.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //Si el documento(UID) existe en la base de datos
                if(documentSnapshot.exists()){
                   //Recogemos los datos de la base de datos en model/User
                    User user = documentSnapshot.toObject(User.class);

                    //Establecemos los valores a los TextView
                    mTextViewUsername.setText(user.getUsername());
                    mTextViewPhone.setText(user.getPhone());

                    //Validación de que la imagen no se encuentre vacia ni null
                    if(user.getImage() != null) {
                        if(!user.getImage().equals("")) {
                            //Libreria que muestra la imagen a nuestra aplicacion mediante una url
                            Picasso.get().load(user.getImage()).into(mCircleImageProfile);
                        }
                    }

                }
            }
        });
    }

    // Muestra el bottom Sheet
    private void openBottomSheetSelectImage() {
        mBottomSheetSelectImage = BottomSheetSelectImage.newInstance();
        mBottomSheetSelectImage.show(getSupportFragmentManager(), mBottomSheetSelectImage.getTag());

    }
}