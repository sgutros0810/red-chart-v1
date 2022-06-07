package proyecto.red_chart_v1.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.activities.ChatActivity;
import proyecto.red_chart_v1.models.Chat;
import proyecto.red_chart_v1.models.Message;
import proyecto.red_chart_v1.models.User;
import proyecto.red_chart_v1.providers.AuthProvider;
import proyecto.red_chart_v1.providers.MessagesProvider;
import proyecto.red_chart_v1.providers.UsersProvider;
import proyecto.red_chart_v1.utils.RelativeTime;

// Clase que recoge los datos de los 'Contactos' de la bd
public class ChatsAdapter extends FirestoreRecyclerAdapter <Chat, ChatsAdapter.ViewHolder> {
    Context context;
    AuthProvider authProvider;
    UsersProvider usersProvider;
    MessagesProvider messagesProvider;
    User user;

    ListenerRegistration listener;
    ListenerRegistration listenerLastMessage;

    //Constructor
    public ChatsAdapter(FirestoreRecyclerOptions options, Context context) {
        super(options);
        this.context = context;
        authProvider = new AuthProvider();
        usersProvider = new UsersProvider();
        messagesProvider = new MessagesProvider();
        user = new User();
    }

    /**
    * Muestra los valores que vienen de la bd en el cardview
    **/
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull final Chat chat) {
        String idUser= "";

        //Bucle que recorre el los campos de ids en 'Chats' para obtener el id del usuario
        for (int i = 0; i < chat.getIds().size(); i++) {
            //Si el id es diferente a mi sesion
            if(!authProvider.getId().equals(chat.getIds().get(i))) {
                idUser = chat.getIds().get(i);
                break;
            }
        }

        //Obtiene el ultimo mensaje del chat
        getLastMessage(holder, chat.getId());

        //Obtiene la información del usuario
        getUserInfo(holder, idUser);

        //Obtiene los mensajes no leidos
        getMessagesNotRead(holder, chat.getId());

        //Me muestra el chat
        clickMyView(holder, chat.getId(), idUser);
    }


    //Mensajes del usuario del chat que no ha leido
    private void getMessagesNotRead(final ViewHolder holder, final String idChat) {
        //Mensajes no leidos del otro usaurio en nuestro chat con la otra persona
        messagesProvider.getReceiverMessagesNotRead(idChat, authProvider.getId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                //Si 'querySnapshot' es diferente de null
                if(querySnapshot != null) {
                    int size = querySnapshot.size();    //Nº de mensajes no leidos
                    //Si el nº de mensajes no leidos es mayor que 0
                    if(size > 0) {
                        //Muestra el nº de mensajes no leidos
                        holder.frameLayoutMessagesNotRead.setVisibility(View.VISIBLE);
                        holder.textViewMessagesNotRead.setText(String.valueOf(size));
                        holder.textViewTimestamp.setTextColor(context.getResources().getColor(R.color.colorNotification));
                    } else {
                        //Si no tenemos ningun mensaje por leer, oculta el numero 0
                        holder.frameLayoutMessagesNotRead.setVisibility(View.GONE);
                        holder.textViewTimestamp.setTextColor(context.getResources().getColor(R.color.colorGrayDark));

                    }
                }
            }
        });
    }

    //Método que obtiene el ultimo mensaje del chat
    private void getLastMessage(final ViewHolder holder, String idChat) {
        //Hace la consulta de obtener el ultimo mensaje
       listenerLastMessage = messagesProvider.getLastMessages(idChat).addSnapshotListener(new EventListener<QuerySnapshot>() {
           @Override
           public void onEvent(@Nullable QuerySnapshot querySnapshots, @Nullable FirebaseFirestoreException error) {
               //Validacion de que los documentos (resultado de la consulta) no sean null
               if(querySnapshots != null) {
                   int size = querySnapshots.size();  //Nº de documentos que retorna
                   //Si hay mas de un documento
                   if(size > 0) {
                       Message message = querySnapshots.getDocuments().get(0).toObject(Message.class);                  //Obtiene el ultimo mensaje de la bd del chat

                       holder.textViewLastMessage.setText(message.getMessage());                                        //Muestra el ultimo mensaje
                       holder.textViewTimestamp.setText(RelativeTime.timeFormatAMPM(message.getTimestamp(), context));  //Muestra la hora del ultimo mensaje


                       //Si el mensaje lo envia el usuario de la sesion de la app
                       if (message.getIdSender().equals(authProvider.getId())) {
                           //Muestra el check
                           holder.imageViewCheck.setVisibility(View.VISIBLE);

                           //Si el estado del check es 'ENVIADO'
                           if (message.getStatus().equals("ENVIADO")) {
                               //Mostrará el doble check gris (lo recibe y no lo ha visto)
                               holder.imageViewCheck.setImageResource(R.drawable.icon_check_double_gris);

                               //Si el estado del check es 'VISTO'
                           } else if (message.getStatus().equals("VISTO")) {
                               //Mostrará el doble check azul (lo recibe y lo ha visto)
                               holder.imageViewCheck.setImageResource(R.drawable.icon_check_double_blue);
                           }

                           //Si ha enviado el mensaje el otro usaurio
                       } else {
                           //Oculta el check
                           holder.imageViewCheck.setVisibility(View.GONE);
                       }
                   }
               }
           }
       });
    }

    //Cuando pulse sobre un usuario, me muestra su chat
    private void clickMyView(ViewHolder holder,  final String idChat, final String idUser) {
        holder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToChatActivity(idChat, idUser);
            }
        });
    }

    // Método que obtiene la informacion del usuario por id
    private void getUserInfo(ViewHolder holder, String idUser) {
        listener = usersProvider.getUserInfo(idUser).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if(documentSnapshot != null){
                    if(documentSnapshot.exists()){
                        user = documentSnapshot.toObject(User.class);
                        holder.textViewUsername.setText(user.getUsername());

                        //Validacion de que la imagen no este null ni vacio
                        if(user.getImage() != null)  {
                            //Muestra la imagen de la bd si no esta vacia
                            if(!user.getImage().equals("")) {
                                Picasso.get().load(user.getImage()).into(holder.circleImageUser);
                            }
                            else {
                                //Muestra la imagen por defecto
                                holder.circleImageUser.setImageResource(R.drawable.ic_perfil_person);
                            }
                        }
                        else {
                            //Muestra la imagen por defecto
                            holder.circleImageUser.setImageResource(R.drawable.ic_perfil_person);
                        }

                    }
                }
            }
        });
    }

    //Método que retorna el listener
    public ListenerRegistration getListener(){
        return listener;
    }

    //Método que retorna el listener
    public ListenerRegistration getListenerLastMessage(){
        return listenerLastMessage;
    }

    //Muestra la pantalla del chat del cusuario con el id
    private void goToChatActivity(String idChat, String idUser) {
        Intent intent = new Intent(context, ChatActivity.class);
        //Enviamos el id del usuario seleccionado por parametro
        intent.putExtra("idUser", idUser);
        intent.putExtra("idChat", idChat);
        context.startActivity(intent);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_chats, parent, false);
        return new ViewHolder(view);
    }


    //Método que devuelve ...
    public  class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewUsername;
        TextView textViewLastMessage;
        CircleImageView circleImageUser;
        ImageView imageViewCheck;
        TextView textViewTimestamp;
        FrameLayout frameLayoutMessagesNotRead;
        TextView textViewMessagesNotRead;

        View myView;

        public ViewHolder(View view){
            //Le estamos pasando el cardview_contacts al view
            super(view);
            myView=view;        //Representa a cada view -> contacto

            //Instaciamos los id del 'cardview_contacts'
            textViewUsername = view.findViewById(R.id.textViewUsername);
            textViewLastMessage = view.findViewById(R.id.textViewLastMessage);
            circleImageUser = view.findViewById(R.id.circleImageUser);
            imageViewCheck= view.findViewById(R.id.imageViewCheck);
            textViewTimestamp= view.findViewById(R.id.textViewTimestamp);
            frameLayoutMessagesNotRead= view.findViewById(R.id.frameLayoutMessagesNotRead);
            textViewMessagesNotRead = view.findViewById(R.id.textViewMessagesNotRead);
        }
    }



}
