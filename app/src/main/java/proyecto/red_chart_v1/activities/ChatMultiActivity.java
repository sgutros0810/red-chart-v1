package proyecto.red_chart_v1.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import de.hdodenhof.circleimageview.CircleImageView;
import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.adapters.MessagesAdapter;
import proyecto.red_chart_v1.models.Chat;
import proyecto.red_chart_v1.models.Message;
import proyecto.red_chart_v1.models.User;
import proyecto.red_chart_v1.providers.AuthProvider;
import proyecto.red_chart_v1.providers.ChatsProvider;
import proyecto.red_chart_v1.providers.FilesProvider;
import proyecto.red_chart_v1.providers.MessagesProvider;
import proyecto.red_chart_v1.providers.NotificationProvider;
import proyecto.red_chart_v1.providers.UsersProvider;
import proyecto.red_chart_v1.utils.AppBackgroundHelper;
import proyecto.red_chart_v1.utils.RelativeTime;

public class ChatMultiActivity extends AppCompatActivity {

    String mExtraIdUser;
    String mExtraidChat;

    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;
    ChatsProvider mChatsProvider;
    MessagesProvider mMessagesProvider;
    FilesProvider mFilesProvider;
    NotificationProvider mNotificationProvider;

    ImageView mImageViewBack;
    ImageView mImageViewSend;
    ImageView mImageViewSelectFiles;
    TextView mTextViewUsername;
    TextView mTextViewOnline;
    CircleImageView mCircleImageView;
    EditText mEditTextMessage;

    ImageView mImageViewSelectPictures;

    MessagesAdapter mAdapter;
    RecyclerView mRecyclerViewMessages;
    LinearLayoutManager mLinearLayoutManager;

    Timer mTimer;

    ListenerRegistration mListenerChat;

    //User mUserReceiver;
    User mUserSend;

    ArrayList<String> mReceiversId = new ArrayList<>();    //ids de los usuarios que reciben el mensaje

    //Variables para la liberia Pix
    Options mOptions;
    ArrayList<String> mReturnValue = new ArrayList<>();
    ArrayList<User> mReceivers = new ArrayList<>();         //Almacenará todos los usuarios que participan en un grupo
    int mCount = 0;
    String mReceiversName = "";                            //Almacenará los nombres de los usuarios del grupo


    final int ACTION_FILE = 2;
    ArrayList<Uri> mFileList;

    Chat mChat;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_multi);


        String chat = getIntent().getStringExtra("chat"); //obtiene la informacion del campo "chat
        Gson gson = new Gson();
        mChat = gson.fromJson(chat, Chat.class);

        mExtraIdUser = getIntent().getStringExtra("idUser");
        mExtraidChat = getIntent().getStringExtra("idChat");

        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();
        mChatsProvider = new ChatsProvider();
        mMessagesProvider = new MessagesProvider();
        mFilesProvider = new FilesProvider();
        mNotificationProvider = new NotificationProvider();     //implemento la notificaion


        //Recorre la informacion del Chat.class
        for (String id : mChat.getIds()) {
            //Si el usuario no es igual al id del usuario principal
            if(!id.equals(mAuthProvider.getId())) {
                //Añade al array todas las ids de los usuarios que se encuentran en el chat del grupo (Menos con usuario logeado)
                mReceiversId.add(id);
            }
        }


        mEditTextMessage = findViewById(R.id.editTextMessage);
        mImageViewSend = findViewById(R.id.imageViewSend);
        mImageViewSelectFiles = findViewById(R.id.imageViewSelectFiles);
        mImageViewSelectPictures = findViewById(R.id.imageViewSelectPictures);
        mRecyclerViewMessages = findViewById(R.id.recyclerViewMessages);

        mLinearLayoutManager  = new LinearLayoutManager(ChatMultiActivity.this);
        mLinearLayoutManager.setStackFromEnd(true);
        mRecyclerViewMessages.setLayoutManager(mLinearLayoutManager);       //Se mostrara un mensaje debajo de otro


        mOptions = Options.init()
                .setRequestCode(100)                                          //Request code for activity results
                .setCount(5)                                                  //Numero de imagenes que podemos seleccionar
                .setFrontfacing(false)                                        //Por defecto, se pone la camara trasera
                .setPreSelectedUrls(mReturnValue)                             //Pre selected Image Urls
                .setExcludeVideos(false)                                      //Permite videos
                .setVideoDurationLimitinSeconds(0)                            //Duracion del video
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)    //Orientacion vertical
                .setPath("/pix/images");


        //Muestra el toolbar
        showChatToolbar(R.layout.chat_toolbar);

        getUserSend();

        //Obtiene la informacion de todos los usuarios que se encuentran en un grupo
        getReceiversInfo();

        //comprueba si existe un chat
        checkIfExistChat();

        //Para saber si el otro usuario esta escribiendo
        setWriting();



        //Si pincha en el botón 'imageViewSend'
        mImageViewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Envia el mensaje y la notificacion
                createMessage();

            }
        });

        //Si pincha en el icono de 'imageViewSelectPictures'
        mImageViewSelectPictures.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Abre la libreria 'Pix' -> Sirve para seleccionar imagenes
                startPix();
            }
        });

        //Si pincha en el icono de 'imageViewSelectFiles'
        mImageViewSelectFiles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFiles();
            }
        });
    }


    //Metodo que trata de obtener la informacion de los usuarios de un grupo
    private void getReceiversInfo() {
        //Recorre los ids de los usuarios que estan dentro del grupo
        for (String id: mReceiversId) {
            mUsersProvider.getUserInfo(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    User user = documentSnapshot.toObject(User.class);  //Obtiene la informacion de un usuario
                    mReceivers.add(user);                               //Lo añadimos al array de usuarios 'mReceivers'
                    mCount++;                                           //incrementa 1

                    //Si termino de ejecutarse la consulta (Si mCount es igual al tamaño de los usuarios que hay)
                    if(mCount == mReceiversId.size()) {
                        //Recorre la lista de usuarios obtenida
                        for (User u : mReceivers) {
                            mReceiversName = mReceiversName + u.getUsername() + ", ";   //Añadimos el nombre de un suario y despues una coma
                        }
                        mTextViewOnline.setText(mReceiversName);//Nombre de losusuarios que participan
                    }

                }
            });
        }
    }



    //Metodo que trata de obtener los tipos de archivos que son validos para subir en el 'ChatActivity'
    private void selectFiles() {
        String[] mimeTypes =
                {"application/msword","application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
                        "application/vnd.ms-powerpoint","application/vnd.openxmlformats-officedocument.presentationml.presentation", // .ppt & .pptx
                        "application/vnd.ms-excel","application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xls & .xlsx
                        "text/plain",
                        "application/pdf",
                        "application/zip"};

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            intent.setType(mimeTypes.length == 1 ? mimeTypes[0] : "*/*");
            if (mimeTypes.length > 0) {
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            }
        } else {
            String mimeTypesStr = "";
            for (String mimeType : mimeTypes) {
                mimeTypesStr += mimeType + "|";
            }
            intent.setType(mimeTypesStr.substring(0,mimeTypesStr.length() - 1));
        }
        startActivityForResult(Intent.createChooser(intent,"ChooseFile"), ACTION_FILE);
    }



    private void setWriting() {
        //Eventos de editText
        mEditTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            //Método para saber cuando esta escribiendo el usuario
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //Validación de que el Timer sea dieferente de null
                if(mTimer != null) {
                    //Validación de que mExtraidChat no sea null
                    if (mExtraidChat != null) {
                        //Actualiza el campo 'writing' a "Escribiendo..."
                        mChatsProvider.updateWriting(mExtraidChat, mAuthProvider.getId());
                        mTimer.cancel();   //Timer deja de escuchar
                    }
                }
            }

            //Método para saber cuando deja de escribir el usuario
            @Override
            public void afterTextChanged(Editable editable) {

                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        //Validación de que mExtraidChat no sea null
                        if(mExtraidChat != null) {
                            //Actualiza el campo 'writing' a ""
                            mChatsProvider.updateWriting(mExtraidChat, "");
                        }
                    }
                }, 2000); //Despues de estar escribiendo, dura unos 2 segundos más, y el campo 'writing' para a vacio -> ""
            }
        });
    }

    //Metodos de ciclo de vida de android
    //Cuando esta dentro de la aplicación
    @Override
    protected void onStart() {
        super.onStart();

        //Para saber que la app esta abierta, se pone el 'online' -> 'true'
        AppBackgroundHelper.online(ChatMultiActivity.this, true);

        //Muestra siempre los mensajes
        if(mAdapter != null) {
            mAdapter.startListening();
        }
    }


    //Cuando sale de la aplicación
    @Override
    protected void onStop() {
        super.onStop();

        if(mAdapter != null) {
            mAdapter.stopListening();
        }

        //Para saber que la app esta cerra/segundo plano, se pone el campo 'online' -> 'false' y pone la ultima hora que se conectó
        AppBackgroundHelper.online(ChatMultiActivity.this, false);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mListenerChat != null) {
            mListenerChat.remove();
        }
    }


    //Inicializa la libreria de réplica del selector de imágenes de WhatsApp
    private void startPix() {
        Pix.start(ChatMultiActivity.this, mOptions);
    }





    //Crea el mensaje del usuario1
    private void createMessage() {
        String textMessage = mEditTextMessage.getText().toString();         //obtiene el texto que envió el usuario

        //Validar que el mensaje no sea vacio
        if(!textMessage.equals("")){
            Message message = new Message();

            message.setIdChat(mChat.getId());               //Id del chat entre el usuario1 y el usuario2
            message.setIdSender(mAuthProvider.getId());     //El usuario1 que envia el mensaje de texto
            message.setIdReceiver(mExtraIdUser);            //El usuario2 es el que recibe el mensaje de texto
            message.setMessage(textMessage);                //Mensaje que envia
            message.setStatus("ENVIADO");                   //Estado del mensaje
            message.setType("texto");                       //Tipo de mensaje -> 'texto'
            message.setTimestamp(new Date().getTime());     //Fecha de cuando envia el mensaje
            message.setReceivers(mReceiversId);             //Todos los usuarios que reciben las notificaciones
            message.setUsername(mUserSend.getUsername());   //Almacena el nombre del usuario que lo envia

            //PRUEBA DEL NOMBRE DEL USUARIO QUE ENVIA EL MENSAJE (GRUPO)
            Log.d("USUARIO", "usuario que envia: " + mUserSend.getUsername());

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
                    //sendNotification(message.getMessage());     //Envia el mensaje que envió
                    getLastMessages(message);                     //Obtiene los ultimos 5 mensajes no leidos
                }
            });

            //Si el mensaje esta vacio
        } else  {
            Toast.makeText(ChatMultiActivity.this, "Ingresa el mensaje", Toast.LENGTH_SHORT).show();
        }
    }


    //Método que obtiene los ultimos 5 mensajes no leidos del chat
    private void getLastMessages(final Message message) {
        mMessagesProvider.getLastMessagesByChatAndSender(mExtraidChat, mAuthProvider.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                if (querySnapshot != null) {
                    ArrayList<Message> messages = new ArrayList<>();

                    //Recorre cada uno de los documentos que ha retornado la consulta
                    for(DocumentSnapshot document: querySnapshot.getDocuments()) {
                        Message m = document.toObject(Message.class);
                        messages.add(m);
                    }
                    //Si el array de messages es igual a 0 (No encuentra ningun mensaje)
                    if (messages.size() == 0) {
                        messages.add(message);
                    }
                    Collections.reverse(messages);
                    sendNotification(messages); //Envia los mensajes
                }
            }
        });
    }


    //Método que envia la notificacion cuando cree el mensaje
    private void sendNotification(ArrayList<Message> messages) {
        //Envía la notificacion //saber el token del usuario que le envia la notificación
        Map<String, String> data = new HashMap<>();                                        //Variable que sirve para transmitir lo que se muestre en la app
        data.put("title", "MENSAJE");                                                //Titulo
        data.put("body", "texto mensaje");                                           //Body el mensaje que recibimos
        data.put("idNotification", String.valueOf(mChat.getIdNotification()));          //ID de la notificacion
        data.put("usernameSender",  "");                                             //Nombre del usuario que envia

        data.put("usernameReceiver", mUserSend.getUsername());                          //Nombre del usuario que recibe
        data.put("imageReceiver", "");                                               //Imagen de perfil del usuario

        Gson gson = new Gson();
        String menssagesJSON = gson.toJson(messages);                                      //Convierto el array 'messages' a un JSON

        data.put("menssagesJSON", menssagesJSON);                                       //Los ultimos 5 mensajes no leidos del chat


        List<String> tokens = new ArrayList<>();                                           //Token de los usuarios que participan en el grupo

        //Recorre la lista de usuarios que hay en el grupo
        for ( User u: mReceivers) {
            //Añade el token de cada usuario, en la lista de string 'tokens'
            tokens.add(u.getToken());
        }
        
        
        //Envia la notificacion al usuario que recibe el mensaje
        mNotificationProvider.send(ChatMultiActivity.this, tokens, data);
    }


    //Método que comprueba si existe un chat con esos ids (el usuario y el usuario con el que chatea)
    private void checkIfExistChat() {
        mChatsProvider.getChatByUser1AndUser2( mChat.getId(), "").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                        //getChatInfo();
                        //Toast.makeText(ChatActivity.this, "El chat ya existe entre estos dos usuarios", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


/*
    //Método
    private void getChatInfo() {
        //Obtiene el id de un documento en Chats
        mListenerChat = mChatsProvider.getChatById(mExtraidChat).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                //Validacion de que contiene datos leídos de un documento en su base de datos y no sea null
                if(documentSnapshot != null) {
                    //Validacion de si existe
                    if(documentSnapshot.exists()) {
                        //Retorna un chat
                        mChat = documentSnapshot.toObject(Chat.class);  //Obtiene la información y lo transforma a un objeto

                        //Validación de que la informacion obtenida no sea null y no este vacio
                        if(mChat.getWriting() != null){
                            if(!mChat.getWriting().equals("")){

                                //Si el id es diferente al usuario principal pone el estado a 'Escribiendo...'
                                if(!mChat.getWriting().equals(mAuthProvider.getId())){
                                    mTextViewOnline.setText("Escribiendo...");
                                } else if(mUserReceiver != null) {

                                    //Si el usuario se encuentra 'En linea' muestra un texto
                                    if(mUserReceiver.isOnline()){
                                        mTextViewOnline.setText("En línea");
                                        //Muestra la ultima vez que se conectó
                                    } else {
                                        //Variable que contiene la ultima vez que se conecto de la app
                                        String relativeTime = RelativeTime.getTimeAgo(mUserReceiver.getLastConnect(), ChatMultiActivity.this);
                                        //Muestra la fecha
                                        mTextViewOnline.setText(relativeTime);
                                    }

                                } else {
                                    mTextViewOnline.setText("");
                                }


                            } else if(mUserReceiver != null) {

                                //Si el usuario se encuentra 'En linea' muestra un texto
                                if (mUserReceiver.isOnline()) {

                                    mTextViewOnline.setText("En línea");

                                    //Muestra la ultima vez que se conectó
                                } else {
                                    //Variable que contiene la ultima vez que se conecto de la app
                                    String relativeTime = RelativeTime.getTimeAgo(mUserReceiver.getLastConnect(), ChatMultiActivity.this);

                                    //Muestra la fecha
                                    mTextViewOnline.setText(relativeTime);
                                }
                            }
                        }
                    }
                }
            }
        });
    }

 */

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
        Query query = mMessagesProvider.getMessagesByChat(mChat.getId());

        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .build();

        mAdapter = new MessagesAdapter(options, ChatMultiActivity.this);
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

        Random random = new Random();
        int n = random.nextInt(100000);      //Genera un numero aleatorio entre 0 y 10000

        mChat = new Chat();
        //Establece la informacion por defecto
        mChat.setId(mAuthProvider.getId() + mExtraIdUser); //Obtengo el id del usuario + el id del otro usuario
        mChat.setTimestamp(new Date().getTime());          //Muestra la fecha exacta que se creó el chat de tipo long
        mChat.setNumberMessages(0);                        //Se crea el campo 'numberMessages' a 0
        mChat.setWriting("");
        mChat.setIdNotification(n);                         //Se crea un campo id con un número aleatorio para las notificaciones

        ArrayList<String> ids = new ArrayList<>();
        ids.add(mAuthProvider.getId());                     //El primer usuario que vamos insertar es usuario principal
        ids.add(mExtraIdUser);                              //Insertamos el otro usuario

        mChat.setIds(ids);                                   //Pasamos los ids al array

        mExtraidChat = mChat.getId();

        //Si se ha creado correctamente...
        mChatsProvider.create(mChat).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                getMessagesByChat();
                //Toast.makeText(ChatActivity.this, "El chat se creo correctamente", Toast.LENGTH_LONG).show();
            }
        });
    }


    public void getUserSend() {
        mUsersProvider.getUserInfo(mAuthProvider.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    mUserSend = documentSnapshot.toObject(User.class);
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
        mTextViewOnline = view.findViewById(R.id.textViewOnline);

        Picasso.get().load(mChat.getGroupImage()).into(mCircleImageView);//PONE LA IMAGEN DEL GRUPO
        mTextViewUsername.setText(mChat.getGroupName());                 //PONE EL NOMBRE DEL GRUPO

        //Vuelve a la actividad anterior
        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    //Recibe las imagenes que el usuario selecciono y la guarda
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            mReturnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);     //Retorna las imagenes que hemos seleccionado

            Intent intent = new Intent(ChatMultiActivity.this, ConfirmImageSendActivity.class);       //Pasamos el contexto y a la actividad que queremos ir
            intent.putExtra("data", mReturnValue);          //Hace que establezca un 'dato' -> (Todas las rutas de las imagenes seleccionadas)  que envia a la clase 'ConfirmImageSendActivity.class'
            intent.putExtra("idChat", mExtraidChat);        //Envía el id del chat
            intent.putExtra("idReceiver", mExtraIdUser);    //Envía el id del usuario que recibe el mensaje

            Gson gson = new Gson();
            String userSendJSON = gson.toJson(mUserSend);                                      //Convierto el array 'messages' a un JSON
            //String userRecieverJSON = gson.toJson(mUserReceiver);

            intent.putExtra("userSend", userSendJSON);                      //Envía el id del usuario que recibe el mensaje
            //intent.putExtra("userReceiver", userRecieverJSON);
            intent.putExtra("idNotification", String.valueOf(mChat.getIdNotification()));   //Envía el id de la notificacion

            startActivity(intent);
        }

        //Si el usuario hizo la accion de seleccionar un archivo
        if(requestCode == ACTION_FILE && resultCode == RESULT_OK){
            mFileList = new ArrayList<>();      //Inicializamos el array

            ClipData clipData = data.getClipData();

            //Si selecciona un único archivo
            if(clipData == null){
                Uri uri = data.getData();
                mFileList.add(uri);
            }
            //Si selecciona varios archivos
            else {
                int count = clipData.getItemCount();       //Cuenta cuantos archivos selecciono

                //Captura los elementos seleccionados por el usuario
                for (int i = 0; i < count; i++) {
                    Uri uri = clipData.getItemAt(i).getUri();
                    mFileList.add(uri);
                }
            }

            //Recibe como parametro la activity, los archivos, el id chat y el id de usuario
            mFilesProvider.saveFiles(ChatMultiActivity.this, mFileList, mExtraidChat, mExtraIdUser );


            //Si es un documento
            final Message message = new Message();
            message.setIdChat(mExtraidChat);
            message.setIdSender(mAuthProvider.getId());
            message.setIdReceiver(mExtraIdUser);
            message.setMessage("\uD83D\uDCC4 Documento");   //Si es un documento
            message.setStatus("ENVIADO");
            message.setType("texto");
            message.setTimestamp(new Date().getTime());
            ArrayList<Message> messages = new ArrayList<>();
            messages.add(message);  //Lo añadimos

            sendNotification(messages);
        }

    }

    //Seleccion de Permisos de camara y galeria
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS) {
            //Si acepta los permisos
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Pix.start(ChatMultiActivity.this, mOptions);

                //Sino acepta los permisos
            } else {
                Toast.makeText(ChatMultiActivity.this, "Por favor, acepte los permisos para tener acceso a la cámara.", Toast.LENGTH_LONG).show();
            }
            return;

        }
    }

}