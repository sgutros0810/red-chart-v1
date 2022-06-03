package proyecto.red_chart_v1.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.activities.ChatActivity;
import proyecto.red_chart_v1.models.User;
import proyecto.red_chart_v1.providers.AuthProvider;

// Clase que recoge los datos de los 'Contactos' de la bd
public class ContactsAdapter extends FirestoreRecyclerAdapter <User, ContactsAdapter.ViewHolder> {

    Context context;
    AuthProvider authProvider;

    //Constructor
    public ContactsAdapter (FirestoreRecyclerOptions options, Context context) {
        super(options);
        this.context = context;
        authProvider = new AuthProvider();
    }

    /** Muestra los valores que vienen de la bd en el cardview
    * holder    -> accede a los campos creados en la clase ViewHolder y introduce los datos de la base de datos para mostrarlos
    **/
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull User user) {
        //Si el id del usuario es el mismo que se esta utilizando
        if(user.getId().equals(authProvider.getId())){
            //Ocultar el recyclerView, al usuario propìo
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            params.height = 0;
            params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            params.topMargin = 0;
            params.bottomMargin = 0;
            holder.itemView.setVisibility(View.VISIBLE);
        }

        holder.textViewUsername.setText(user.getUsername());    //Muestra el nombre del contacto
        holder.textViewInfo.setText(user.getInfo());            //Muestra el info del usuario

        //Validacion de que la imagen no este null ni vacio
        if(user.getImage() != null)  {

            //Muestra la imagen de la bd
            if(!user.getImage().equals("")) {
                Picasso.get().load(user.getImage()).into(holder.circleImageUser);
            }
            //Muestra la imagen por defecto
            else {

                holder.circleImageUser.setImageResource(R.drawable.ic_perfil_person);
            }

        }
        //Muestra la imagen por defecto
        else {
            holder.circleImageUser.setImageResource(R.drawable.ic_perfil_person);
        }
        holder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToChatActivity();
            }
        });
    }

    //Muestra la pantalla del chat del contacto
    private void goToChatActivity() {
        Intent intent = new Intent(context, ChatActivity.class);
        context.startActivity(intent);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_contacts, parent, false);
        return new ViewHolder(view);
    }

    //Mrtodo que devuelve ...
    public  class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewUsername;
        TextView textViewInfo;
        CircleImageView circleImageUser;
        View myView;

        public ViewHolder(View view){
            //Le estamos pasando el cardview_contacts al view
            super(view);
            myView=view;        //Representa a cada view -> contacto

            //Instaciamos los id del 'cardview_contacts'
            textViewUsername = view.findViewById(R.id.textViewUsername);
            textViewInfo= view.findViewById(R.id.textViewInfo);
            circleImageUser = view.findViewById(R.id.circleImageUser);

        }
    }



}
