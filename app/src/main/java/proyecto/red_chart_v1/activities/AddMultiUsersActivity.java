package proyecto.red_chart_v1.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.Query;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.adapters.ContactsAdapter;
import proyecto.red_chart_v1.adapters.MultiUsersAdapter;
import proyecto.red_chart_v1.models.Chat;
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

    Menu mMenu;

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

                    //Si el numero de usuarios selecionados son mas que uno
                    if(mUsersSelected.size() >= 2){
                        createChat();

                    } else {
                        Toast.makeText(AddMultiUsersActivity.this, "Seleccione al menos dos usuarios", Toast.LENGTH_SHORT).show();
                    }



                    /*
                        //PRUEBA  imprime cada uno de los usuarios seleccionados
                        for (User user: mUsersSelected) {
                            Log.d("USUARIO", "nombre: " + user.getUsername());
                        }
                     */
                } else {
                    Toast.makeText(AddMultiUsersActivity.this, "Por favor, seleccione los usuarios", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    //Crea el chat de grupo
    private void createChat(){
        Random random = new Random();
        int n = random.nextInt(100000); //Numero aletorio entre 0-100000 para el id de notificaciones del chat

        Chat chat = new Chat();
        chat.setId(UUID.randomUUID().toString());   //ID DEL CHAT ALETORIO ÚNICO
        chat.setTimestamp(new Date().getTime());
        chat.setNumberMessages(1);                  //Nº maximo de mensajes d elos usuarios que aparezcan en la lista
        chat.setWriting("");
        chat.setIdNotification(n);                  //Nº aleatorio para el id de la notificacion
        chat.setMultiChat(true);                    //Tiene varios usuarios (Grupo)

        //Añadimos los usuarios que ha seleccionado el usuario
        ArrayList<String> ids = new ArrayList<>();
        ids.add(mAuthProvider.getId());   //Añade al propio usuario por defecto

        //Recorre la lista de todos usuarios seleccionados
        for (User u: mUsersSelected) {
            ids.add(u.getId()); //Añade con el id del usuario seleccionado
        }

        chat.setIds(ids);                       //Pasamos al chat de grupo los ids de los usuarios
        Gson gson = new Gson();                 //Transformamos el String en un objeto JSON
        String chatJSON = gson.toJson(chat);    //Lo contiene un string

        Intent intent = new Intent(AddMultiUsersActivity.this, ConfirmMultiChatActivity.class);
        intent.putExtra("chat", chatJSON); //Toda la informacion del chat que hemos creado
        startActivity(intent);
    }


    //Metodo donde se guarda todos los usuarios seleccionados
    public void setUsers(ArrayList<User> users){
        //Cuenta los usuarios seleccionados
        if(mMenu != null){
            mUsersSelected = users;

            //PRUEBA
            for (User u: mUsersSelected) {
                Log.d("USUARIO", "nombre: " + u.getUsername() + " " + users.size());
            }

            if(users.size() > 0){
                //Muestra los numeros de usuarios selecionados
                mMenu.findItem(R.id.itemCount).setTitle(Html.fromHtml("<font color='#ffffff'>" + users.size() + "</font>"));

            } else {
                //No muestra nada
                mMenu.findItem(R.id.itemCount).setTitle("");

            }
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_user_menu, menu);
        mMenu=menu;
        return true;
    }
}