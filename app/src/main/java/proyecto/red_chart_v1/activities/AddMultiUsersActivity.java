package proyecto.red_chart_v1.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.adapters.ContactsAdapter;
import proyecto.red_chart_v1.adapters.MultiUsersAdapter;
import proyecto.red_chart_v1.models.User;
import proyecto.red_chart_v1.providers.AuthProvider;
import proyecto.red_chart_v1.providers.UsersProvider;
import proyecto.red_chart_v1.utils.MyToolBarSimple;

public class AddMultiUsersActivity extends AppCompatActivity {

    RecyclerView mRecyclerViewContacts;
    FloatingActionButton mFabCheck;

    MultiUsersAdapter mMultiUsersAdapter;

    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;

    //Listar todos los contactos
    ArrayList<User> mUsersSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_multi_users);
        MyToolBarSimple.show(AddMultiUsersActivity.this, "Añadir grupo", true );

        mRecyclerViewContacts = findViewById(R.id.recyclerViewContacts);
        mFabCheck = findViewById(R.id.fabCheck);

        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();

        //Se posicionan uno debajo del otro
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AddMultiUsersActivity.this);
        mRecyclerViewContacts.setLayoutManager(linearLayoutManager);

        //si pinchamos sobre algun usuario, se selecciona
        mFabCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Si el array mUsersSelected mo esta null
                if(mUsersSelected != null){
                    //imprime cada uno de los usuarios seleccionados
                    for (User user: mUsersSelected) {
                        Log.d("USUARIO", "nombre: " + user.getUsername());
                    }
                }

            }
        });

    }

    //Metodo donde se guarda todos los usuarios seleccionados
    public void setUsers(ArrayList<User> users){
        mUsersSelected = users;
    }

    //Metodo en ciclo de vida
    @Override
    public void onStart() {
        super.onStart();
        //Recoge la informacion de la clase UserProviders
        Query query = mUsersProvider.getAllUsersByName();

        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();
        mMultiUsersAdapter = new MultiUsersAdapter(options, AddMultiUsersActivity.this);

        mRecyclerViewContacts.setAdapter(mMultiUsersAdapter);

        //Cambios en tiempo real que sucede en la bd
        mMultiUsersAdapter.startListening();

    }

    //Método que para de escuchar los cambios
    @Override
    public void onStop() {
        super.onStop();
        mMultiUsersAdapter.stopListening();
    }

}