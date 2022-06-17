package proyecto.red_chart_v1.adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.ListenerRegistration;
import com.squareup.picasso.Picasso;

import java.io.File;

import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.activities.ShowImageOrVideoActivity;
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
        holder.textViewMessage.setText(message.getMessage());                                      //Obtenemos el mensaje y lo muestra
        holder.textViewDate.setText(RelativeTime.timeFormatAMPM(message.getTimestamp(),context));  //Muestra la fecha personalizada

        //Si nosotros enviado el mensaje
        if(message.getIdSender().equals(authProvider.getId())){
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);                                                              //Lo posicionamos a la derecha
            params.setMargins(100, 0, 0, 0);                                                            //Margenes
            holder.linearLayoutMessage.setLayoutParams(params);
            holder.linearLayoutMessage.setPadding(30, 20, 30, 20);                                       //Padding al linear layout
            holder.linearLayoutMessage.setBackground(context.getResources().getDrawable(R.drawable.bubble_corner_right));   //Fondo que queremos
            holder.textViewMessage.setTextColor(Color.BLACK);                                                               //Color del texto de mensaje
            holder.textViewDate.setTextColor(Color.DKGRAY);                                                                 //Color del texto de la fecha/hora
            holder.imageViewCheck.setVisibility(View.VISIBLE);                                                              //Mostrar el estado del check

            //Si el estado del check es 'ENVIADO'
            if(message.getStatus().equals("ENVIADO")) {
                //Mostrará el doble check gris (No lo recibe el otro usuario pero se ha enviado)
                holder.imageViewCheck.setImageResource(R.drawable.icon_check_gris);

                //Si el estado del check es 'RECIBIDO'
            } else if(message.getStatus().equals("RECIBIDO")) {
                //Mostrará el doble check azul (lo recibe y no lo ha visto)
                holder.imageViewCheck.setImageResource(R.drawable.icon_check_double_gris);

                //Si el estado del check es 'VISTO'
            }  else if(message.getStatus().equals("VISTO")) {
                //Mostrará el check gris (lo recibe y lo ha visto)
                holder.imageViewCheck.setImageResource(R.drawable.icon_check_double_blue);
            }

        } else {
            //Si somos los usuarios que reciben el mensaje (Receptores)
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
            );

            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);                                                              //Lo posicionamos a la derecha
            params.setMargins(0, 0, 100, 0);                                                            //Margenes
            holder.linearLayoutMessage.setLayoutParams(params);
            holder.linearLayoutMessage.setPadding(50, 20, 30, 20);                                       //Padding al linear layout
            holder.linearLayoutMessage.setBackground(context.getResources().getDrawable(R.drawable.bubble_corner_left));    //Fondo que queremos
            holder.textViewMessage.setTextColor(Color.BLACK);                                                               //Color del texto de mensaje
            holder.textViewDate.setTextColor(Color.DKGRAY);                                                                 //Color del texto de la fecha/hora
            holder.imageViewCheck.setVisibility(View.GONE);                                                                 //Ocultar el estado del check


            //Si el mensaje se envia en un grupo
            if(message.getReceivers() != null){
                holder.textViewUsername.setVisibility(View.VISIBLE);    //Se muestra el nombre de usuario
                holder.textViewUsername.setText(message.getUsername()); //Pone el nombre del usuario que envia el mensaje
                //si es un mensaje a un solo usuario
            } else {
                holder.textViewUsername.setVisibility(View.GONE);       //Se muestra el nombre de usuario
               // holder.textViewUsername.setText(message.getUsername()); //Pone el nombre del usuario que envia el mensaje

            }

        }

        showImage(holder, message);
        showVideo(holder, message);
        showDocument(holder, message);
        openMessage(holder, message);
    }



    //Método para descargar un archivo
    private void openMessage(ViewHolder holder, Message message) {
        holder.myView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //si el mensaje tiene el tipo documento
                if(message.getType().equals("documento")){
                    File file = new File(context.getExternalFilesDir(null), "file");
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(message.getUrl()))  //URL DEL ARCHIVO QUE QUEREMOS DESCARGAR
                            .setTitle(message.getMessage())        //Nombre del archivo
                            .setDescription("Download")
                            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                            .setDestinationUri(Uri.fromFile(file))
                            .setAllowedOverMetered(true)
                            .setAllowedOverRoaming(true);

                    DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    downloadManager.enqueue(request);  //Ejecuta la descarga

                } else if(message.getType().equals("imagen") || message.getType().equals("video")){
                    Intent intent = new Intent(context, ShowImageOrVideoActivity.class);
                    intent.putExtra("type", message.getType());
                    intent.putExtra("url", message.getUrl());
                    context.startActivity(intent);

                }
            }
        });
    }

    //Método que muestra el documento
    private void showDocument(ViewHolder holder, Message message) {
        if(message.getType().equals("documento")){
            if(message.getUrl() != null ){
                if(!message.getUrl().equals("")){
                    holder.linearLayoutDocument.setVisibility(View.VISIBLE);
                    //holder.imageViewMessage.setVisibility(View.GONE);

                } else {
                    holder.linearLayoutDocument.setVisibility(View.GONE);

                }
            } else {
                holder.linearLayoutDocument.setVisibility(View.GONE);

            }
        } else {
            holder.linearLayoutDocument.setVisibility(View.GONE);
        }
    }

    //Método que muestra el video
    private void showVideo(ViewHolder holder, Message message){
        //Si el tipo de mensaje es video
        if (message.getType().equals("video")) {
            if (message.getUrl() != null) {
                if (!message.getUrl().equals("")) {
                    holder.frameLayoutVideo.setVisibility(View.VISIBLE);
                    //Libreria que muestra la imagen a nuestra aplicacion mediante una url
                    Picasso.get().load(message.getUrl()).into(holder.imageViewMessage);

                    if (message.getMessage().equals("\uD83C\uDFA5video")) {
                        holder.textViewMessage.setVisibility(View.GONE);
                        //holder.textViewDate.setPadding(0,0,10,0);
                        ViewGroup.MarginLayoutParams marginDate = (ViewGroup.MarginLayoutParams) holder.textViewDate.getLayoutParams();
                        ViewGroup.MarginLayoutParams marginCheck = (ViewGroup.MarginLayoutParams) holder.imageViewCheck.getLayoutParams();
                        marginDate.topMargin = 15;
                        marginCheck.topMargin = 15;

                    }
                    else {
                        holder.textViewMessage.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    holder.frameLayoutVideo.setVisibility(View.GONE);
                    holder.textViewMessage.setVisibility(View.VISIBLE);
                }
            }
            else {
                holder.frameLayoutVideo.setVisibility(View.GONE);
                holder.textViewMessage.setVisibility(View.VISIBLE);
            }
        }
        else {
            holder.frameLayoutVideo.setVisibility(View.GONE);
            holder.textViewMessage.setVisibility(View.VISIBLE);
        }

    }



    //Método que muestra la imagen
    private void showImage(ViewHolder holder, Message message) {

        //Si el tipo de mensaje es imagen
        if (message.getType().equals("imagen")) {
            if (message.getUrl() != null) {
                if (!message.getUrl().equals("")) {
                    holder.imageViewMessage.setVisibility(View.VISIBLE);
                    //Libreria que muestra la imagen a nuestra aplicacion mediante una url
                    Picasso.get().load(message.getUrl()).into(holder.imageViewMessage);

                    if (message.getMessage().equals("\uD83D\uDCF7imagen")) {
                        holder.textViewMessage.setVisibility(View.GONE);
                        //holder.textViewDate.setPadding(0,0,10,0);
                        ViewGroup.MarginLayoutParams marginDate = (ViewGroup.MarginLayoutParams) holder.textViewDate.getLayoutParams();
                        ViewGroup.MarginLayoutParams marginCheck = (ViewGroup.MarginLayoutParams) holder.imageViewCheck.getLayoutParams();
                        marginDate.topMargin = 15;
                        marginCheck.topMargin = 15;

                    }
                    else {
                        holder.textViewMessage.setVisibility(View.VISIBLE);
                    }
                }
                else {
                    holder.imageViewMessage.setVisibility(View.GONE);
                    holder.textViewMessage.setVisibility(View.VISIBLE);
                }
            }
            else {
                holder.imageViewMessage.setVisibility(View.GONE);
                holder.textViewMessage.setVisibility(View.VISIBLE);
            }
        }
        else {
            holder.imageViewMessage.setVisibility(View.GONE);
            holder.textViewMessage.setVisibility(View.VISIBLE);
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
        TextView textViewUsername;
        ImageView imageViewCheck;
        ImageView imageViewMessage;
        LinearLayout linearLayoutMessage;
        LinearLayout linearLayoutDocument;
        FrameLayout frameLayoutVideo;
        View viewVideo;
        ImageView imageViewVideo;

        View myView;

        public ViewHolder(View view){
            //Le estamos pasando el cardview_contacts al view
            super(view);
            myView=view;        //Representa a cada view -> contacto

            //Instaciamos los id del 'cardview_contacts'
            textViewMessage         = view.findViewById(R.id.textViewMessage);
            textViewDate            = view.findViewById(R.id.textViewDate);
            textViewUsername        = view.findViewById(R.id.textViewUsername);
            imageViewCheck          = view.findViewById(R.id.imageViewCheck);
            imageViewMessage        = view.findViewById(R.id.imageViewMessage);
            linearLayoutMessage     = view.findViewById(R.id.linearLayoutMessage);
            linearLayoutDocument    = view.findViewById(R.id.linearLayoutDocument);
            frameLayoutVideo        = view.findViewById(R.id.frameLayoutVideo);
            viewVideo               = view.findViewById(R.id.viewVideo);
            imageViewVideo          = view.findViewById(R.id.imageViewVideo);
        }
    }



}