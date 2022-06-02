package proyecto.red_chart_v1.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.models.User;

// Clase que recoge los datos de los 'Contactos' de la bd
public class ContactsAdapter extends FirestoreRecyclerAdapter <User, ContactsAdapter.ViewHolder> {

    Context context;

    //Constructor
    public ContactsAdapter (FirestoreRecyclerOptions options, Context context) {
        super(options);
        this.context = context;
    }

    /** Muestra los valores que vienen de la bd en el cardview
    * holder    -> accede a los campos creados en la clase ViewHolder y introduce los datos de la base de datos para mostrarlos
    **/
    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull User user) {
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

        public ViewHolder(View view){
            //Le estamos pasando el cardview_contacts al view
            super(view);

            //Instaciamos los id del 'cardview_contacts'
            textViewUsername = view.findViewById(R.id.textViewUsername);
            textViewInfo= view.findViewById(R.id.textViewInfo);
            circleImageUser = view.findViewById(R.id.circleImageUser);

        }
    }



}
