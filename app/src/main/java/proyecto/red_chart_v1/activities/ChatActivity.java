package proyecto.red_chart_v1.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.models.User;
import proyecto.red_chart_v1.providers.AuthProvider;
import proyecto.red_chart_v1.providers.UsersProvider;

public class ChatActivity extends AppCompatActivity {

    String mExtraIdUser;
    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;

    ImageView mImageViewBack;
    TextView mTextViewUsername;
    CircleImageView mCircleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mExtraIdUser = getIntent().getStringExtra("id");
        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();

        //Muestra el toolbar
        showChatToolbar(R.layout.chat_toolbar);

        //Recoge los datos del usuario seleccionado
        getUserInfo();
    }

    //
    private void getUserInfo() {
        mUsersProvider.getUserInfo(mExtraIdUser).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                //Si el document snapshot es diferente de null
                if(documentSnapshot != null){
                    //si existe en la base de datos el documentsnapshot
                    if(documentSnapshot.exists()){
                        User user = documentSnapshot.toObject(User.class);              //Obtiene la informacion del usuario seleccionado
                        mTextViewUsername.setText(user.getUsername());

                        //Si la imagen no es null ni vacio
                        if(user.getImage() != null){
                            if(!user.getImage().equals("")){
                                Picasso.get().load(user.getImage()).into(mCircleImageView);
                            }
                        }


                    }
                }
            }
        });
    }

    //Muestra el toolbar del chat
    private void showChatToolbar(int resource){
        Toolbar toolbar = findViewById(R.id.toolBarSimple);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(resource, null);
        actionBar.setCustomView(view);

        //instacia de los elementos del toolbar del chat
        mImageViewBack = view.findViewById(R.id.imageViewBack);
        mTextViewUsername = view.findViewById(R.id.textViewUsername);
        mCircleImageView = view.findViewById(R.id.circleImageUser);


        //Vuelve a la actividad anterior
        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}