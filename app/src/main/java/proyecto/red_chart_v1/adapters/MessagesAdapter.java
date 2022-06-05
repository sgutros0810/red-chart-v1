package proyecto.red_chart_v1.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import proyecto.red_chart_v1.models.Message;
import proyecto.red_chart_v1.models.User;
import proyecto.red_chart_v1.providers.AuthProvider;
import proyecto.red_chart_v1.providers.UsersProvider;
import proyecto.red_chart_v1.utils.RelativeTime;

// Clase que recoge los datos de los 'Contactos' de la bd
public class MessagesAdapter extends FirestoreRecyclerAdapter <Message, MessagesAdapter.ViewHolder> {
    Context context;
    AuthProvider authProvider;
    UsersProvider usersProvider;
    User user;

    ListenerRegistration listener;

    //Constructor
    public MessagesAdapter(FirestoreRecyclerOptions options, Context context) {
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
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull final Message message) {
        holder.textViewMessage.setText(message.getMessage());   //Obtenemos el mensaje y lo muestra
        holder.textViewDate.setText(RelativeTime.timeFormatAMPM(message.getTimestamp(),context));  //Muestra la fecha personalizada

        //Si nosotros enviado el mensaje
        if(message.getIdSender().equals(authProvider.getId())){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);                                                              //Lo posicionamos a la derecha
            params.setMargins(150, 0, 0, 0);                                                            //Margenes
            holder.linearLayoutMessage.setLayoutParams(params);
            holder.linearLayoutMessage.setPadding(30, 20, 50, 20);                                       //Padding al linear layout
            holder.linearLayoutMessage.setBackground(context.getResources().getDrawable(R.drawable.bubble_corner_right));   //Fondo que queremos
            holder.textViewMessage.setTextColor(Color.BLACK);                                                               //Color del texto de mensaje
            holder.textViewDate.setTextColor(Color.DKGRAY);                                                                 //Color del texto de la fecha/hora
            holder.imageViewCheck.setVisibility(View.VISIBLE);                                                              //Mostrar el estado del check

        } else {
            //Si somos los usuarios que reciben el mensaje (Receptores)
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);                                                              //Lo posicionamos a la derecha
            params.setMargins(0, 0, 150, 0);                                                            //Margenes
            holder.linearLayoutMessage.setLayoutParams(params);
            holder.linearLayoutMessage.setPadding(80, 20, 30, 20);                                       //Padding al linear layout
            holder.linearLayoutMessage.setBackground(context.getResources().getDrawable(R.drawable.bubble_corner_left));    //Fondo que queremos
            holder.textViewMessage.setTextColor(Color.BLACK);                                                               //Color del texto de mensaje
            holder.textViewDate.setTextColor(Color.DKGRAY);                                                                 //Color del texto de la fecha/hora
            holder.imageViewCheck.setVisibility(View.GONE);                                                                 //Ocultar el estado del check

        }
    }


    //Metodo que retorna el listener
    public ListenerRegistration getListener(){
        return listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_message, parent, false);
        return new ViewHolder(view);
    }

    //Mrtodo que devuelve ...
    public  class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewMessage;
        TextView textViewDate;
        ImageView imageViewCheck;
        LinearLayout linearLayoutMessage;

        View myView;

        public ViewHolder(View view){
            //Le estamos pasando el cardview_contacts al view
            super(view);
            myView=view;        //Representa a cada view -> contacto

            //Instaciamos los id del 'cardview_contacts'
            textViewMessage = view.findViewById(R.id.textViewMessage);
            textViewDate = view.findViewById(R.id.textViewDate);
            imageViewCheck= view.findViewById(R.id.imageViewCheck);
            linearLayoutMessage = view.findViewById(R.id.linearLayoutMessage);
        }
    }



}
