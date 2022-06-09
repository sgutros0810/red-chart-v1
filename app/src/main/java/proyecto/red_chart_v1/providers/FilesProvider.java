package proyecto.red_chart_v1.providers;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;

import proyecto.red_chart_v1.models.Message;
import proyecto.red_chart_v1.utils.FileUtil;

//Clase que maneja los datos de Firebase de los archivos
public class FilesProvider {

    StorageReference mStorage;
    MessagesProvider mMessagesProvider;     //Guarda los mensajes
    AuthProvider mAuthProvider;

    //Contructor
    public FilesProvider() {
        mStorage = FirebaseStorage.getInstance().getReference();
        mMessagesProvider = new MessagesProvider();
        mAuthProvider = new AuthProvider();
    }

    //Método que guarda los archivos
    public void saveFiles(final Context context, ArrayList<Uri> files, final String idChat, final String idReceiver) {

        //Recorre cada archivo seleccionado
        for (int i = 0; i < files.size(); i++) {

            final Uri f = files.get(i);

            //Nombre que se le crea al archivo
            final StorageReference ref = mStorage.child(FileUtil.getFileName(context, f));

            //Evento que cuando termina de ejecutar
            ref.putFile(f).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    //Si fue existoso
                    if (task.isSuccessful()) {
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {    //uri -> obtiene la url del archivo que se guarda en FirebaseStorage
                                String url = uri.toString();    //Obtiene la url

                                Message message = new Message();
                                message.setIdChat(idChat);                      //Id del chat
                                message.setIdReceiver(idReceiver);
                                message.setIdSender(mAuthProvider.getId());
                                message.setType("documento");                   //Tipo -> documento
                                message.setUrl(url);                            //url que hemos obtenido anteriormente
                                message.setStatus("ENVIADO");                   //Estado
                                message.setTimestamp(new Date().getTime());
                                message.setMessage(FileUtil.getFileName(context, f));   //Muestra el nombre del archivo en el mensaje

                                mMessagesProvider.create(message);
                            }
                        });
                    }
                    //Si dio fallo
                    else {
                        Toast.makeText(context, "No se pudo guardar el archivo", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

}
