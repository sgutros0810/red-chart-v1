package proyecto.red_chart_v1.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.activities.ChatActivity;
import proyecto.red_chart_v1.models.Chat;
import proyecto.red_chart_v1.models.User;
import proyecto.red_chart_v1.providers.AuthProvider;
import proyecto.red_chart_v1.providers.UsersProvider;

// Clase que recoge los datos de los 'Contactos' de la bd
public class ChatsAdapter extends FirestoreRecyclerAdapter <Chat, ChatsAdapter.ViewHolder> {
    Context context;
    AuthProvider authProvider;
    UsersProvider usersProvider;
    User user;

    ListenerRegistration listener;

    //Constructor
    public ChatsAdapter(FirestoreRecyclerOptions options, Context context) {
        super(options);
        this.context = context;
        authProvider = new AuthProvider();
        usersProvider = new UsersProvider();
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

        //Obtener la información
        getUserInfo(holder, idUser);

        //Me muestra el chat
        clickMyView(holder, chat.getId(), idUser);
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

    //Metodo que retorna el listener
    public ListenerRegistration getListener(){
        return listener;
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

    //Mrtodo que devuelve ...
    public  class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewUsername;
        TextView textViewLastMessage;
        CircleImageView circleImageUser;
        ImageView imageViewCheck;
        TextView textViewTimestamp;
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
        }
    }



}
