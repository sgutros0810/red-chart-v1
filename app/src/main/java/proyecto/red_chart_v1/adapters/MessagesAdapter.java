package proyecto.red_chart_v1.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

        View myView;

        public ViewHolder(View view){
            //Le estamos pasando el cardview_contacts al view
            super(view);
            myView=view;        //Representa a cada view -> contacto

            //Instaciamos los id del 'cardview_contacts'
            textViewMessage = view.findViewById(R.id.textViewMessage);
            textViewDate = view.findViewById(R.id.textViewDate);
            imageViewCheck= view.findViewById(R.id.imageViewCheck);
        }
    }



}