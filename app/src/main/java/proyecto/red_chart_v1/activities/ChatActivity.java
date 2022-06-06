package proyecto.red_chart_v1.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.adapters.ChatsAdapter;
import proyecto.red_chart_v1.adapters.MessagesAdapter;
import proyecto.red_chart_v1.models.Chat;
import proyecto.red_chart_v1.models.Message;
import proyecto.red_chart_v1.models.User;
import proyecto.red_chart_v1.providers.AuthProvider;
import proyecto.red_chart_v1.providers.ChatsProvider;
import proyecto.red_chart_v1.providers.MessagesProvider;
import proyecto.red_chart_v1.providers.UsersProvider;

public class ChatActivity extends AppCompatActivity {

    String mExtraIdUser;
    String mExtraidChat;

    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;
    ChatsProvider mChatsProvider;
    MessagesProvider mMessagesProvider;

    ImageView mImageViewBack;
    ImageView mImageViewSend;
    TextView mTextViewUsername;
    CircleImageView mCircleImageView;
    EditText mEditTextMessage;

    MessagesAdapter mAdapter;
    RecyclerView mRecyclerViewMessages;
    LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mExtraIdUser = getIntent().getStringExtra("idUser");
        mExtraidChat = getIntent().getStringExtra("idChat");

        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();
        mChatsProvider = new ChatsProvider();
        mMessagesProvider = new MessagesProvider();

        mEditTextMessage = findViewById(R.id.editTextMessage);
        mImageViewSend = findViewById(R.id.imageViewSend);
        mRecyclerViewMessages = findViewById(R.id.recyclerViewMessages);

        mLinearLayoutManager  = new LinearLayoutManager(ChatActivity.this);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerViewMessages.setLayoutManager(mLinearLayoutManager);       //Se mostrara un mensaje debajo de otro

        //Muestra el toolbar
        showChatToolbar(R.layout.chat_toolbar);

        //Recoge los datos del usuario seleccionado
        getUserInfo();

        //comprueba si existe un chat
        checkIfExistChat();


        //Si pincha en el botón 'imageViewSend'
        mImageViewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Envia el mensaje
                createMessage();
            }
        });
    }

    //Metodos de ciclo de vida de android
    @Override
    protected void onStart() {
        super.onStart();

        //Muestra siempre los mensajes
        if(mAdapter != null) {
            mAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    //Crea el mensaje del usuario1
    private void createMessage() {
        String textMessage = mEditTextMessage.getText().toString();         //obtiene el texto que envió el usuario

        //Validar que el mensaje no sea vacio
        if(!textMessage.equals("")){
            Message message = new Message();

            message.setIdChat(mExtraidChat);                //Id del chat entre el usuario1 y el usuario2
            message.setIdSender(mAuthProvider.getId());     //El usuario1 que envia el mensaje de texto
            message.setIdReceiver(mExtraIdUser);            //El usuario2 es el que recibe el mensaje de texto
            message.setMessage(textMessage);                //Mensaje que envia
            message.setStatus("ENVIADO");                   //Estado del mensaje
            message.setTimestamp(new Date().getTime());     //Fecha de cuando envia el mensaje

            //Si se ejecuta correctamente
            mMessagesProvider.create(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    mEditTextMessage.setText("");           //Cuando envié el mensaje se borra

                    //Validación de que MessagesAdapter no sea null
                    if(mAdapter != null){
                        mAdapter.notifyDataSetChanged();        //Cambia si hubo algún cambio
                    }
                    //Actualiza el numero de mensajes
                    mChatsProvider.updateNumberMessage(mExtraidChat);
                    //Toast.makeText(ChatActivity.this, "El mensaje se creó", Toast.LENGTH_SHORT).show();
                }
            });

        //Si el mensaje esta vacio
        } else  {
            Toast.makeText(ChatActivity.this, "Ingresa el mensaje", Toast.LENGTH_SHORT).show();
        }
    }

    //Método que comprueba si existe un chat con esos ids (el usuario y el usuario con el que chatea)
    private void checkIfExistChat() {
        mChatsProvider.getChatByUser1AndUser2(mExtraIdUser, mAuthProvider.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                //Validación de que el documento no es null
                if(queryDocumentSnapshots != null){
                    if(queryDocumentSnapshots.size() == 0){
                        //Crea un chat si no encuentra ningun chat con esos ids
                        createChat();
                    } else {
                        mExtraidChat = queryDocumentSnapshots.getDocuments().get(0).getId();        //Obtiene el id del chat si viene null
                        getMessagesByChat();
                        updateStatus();                                                             //Actualiza el estado del mensaje a "VISTO"

                        //Toast.makeText(ChatActivity.this, "El chat ya existe entre estos dos usuarios", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    //Método que actualiza el estado del mensaje a "VISTO"
    private void updateStatus() {
        //El metodo 'getMessagesByNotRead()', retorna los documentos con el campo 'idChat' y 'status' con los valores establecidos
        mMessagesProvider.getMessagesByNotRead(mExtraidChat).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                //Recorre los documentos recogidos de queryDocumentSnapshots
                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {

                    Message message = document.toObject(Message.class);           //Transforma un Documento a un Objeto

                    //Validación de que el  usuario que envió el mensaje, no sea igual al usuario que tenemos en la sesión de la app
                    if(!message.getIdSender().equals(mAuthProvider.getId())){
                        //Cambia en el campo 'status' de 'ENVIADO' a 'VISTO'
                        mMessagesProvider.updateStatus(message.getId(), "VISTO");
                    }
                }

            }
        });
    }

    private void getMessagesByChat() {
        //Recoge la información de la clase MessagesProviders
        Query query = mMessagesProvider.getMessagesByChat(mExtraidChat);

        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .build();

        mAdapter = new MessagesAdapter(options, ChatActivity.this);
        mRecyclerViewMessages.setAdapter(mAdapter);

        //Cambios en tiempo real que sucede en la bd
        mAdapter.startListening();


        //Para saber cuando se envió un nuevo mensaje
        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);

                updateStatus();                                                                         //Actualiza el estado del mensaje a "VISTO"

                int numberMessage = mAdapter.getItemCount();                                            //Nº de los mensajes actuales
                int lastMessagePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition(); //Último mensaje actual

                //Muestra el ultimo mensaje cuando lo recibo
                if(lastMessagePosition == -1 || (positionStart >=(numberMessage - 1) && lastMessagePosition == (positionStart - 1))) {
                    mRecyclerViewMessages.scrollToPosition(positionStart);
                }
            }
        });
    }


    //Método que crea el chat
    private void createChat() {
        Chat chat = new Chat();
        //Establece la informacion
        chat.setId(mAuthProvider.getId() + mExtraIdUser); //Obtengo el id del usuario + el id del otro usuario
        chat.setTimestamp(new Date().getTime());          //Muestra la fecha exacta que se creó el chat de tipo long
        chat.setNumberMessages(0);                        //Se crea el campo 'numberMessages' a 0


        ArrayList<String> ids = new ArrayList<>();
        ids.add(mAuthProvider.getId());  //El primer usuario que vamos insertar es usuario principal
        ids.add(mExtraIdUser);           //Insertamos el otro usuario

        chat.setIds(ids);               //Pasamos los ids al array

        mExtraidChat = chat.getId();

        //Si se ha creado correctamente...
        mChatsProvider.create(chat).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                getMessagesByChat();
                //Toast.makeText(ChatActivity.this, "El chat se creo correctamente", Toast.LENGTH_LONG).show();
            }
        });
    }

    //Muestra la informacion del usuario
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