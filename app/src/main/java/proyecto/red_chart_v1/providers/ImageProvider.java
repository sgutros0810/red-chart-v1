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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import proyecto.red_chart_v1.models.Message;
import proyecto.red_chart_v1.utils.CompressorBitmapImage;
import proyecto.red_chart_v1.utils.ExtensionFile;

public class ImageProvider {

    StorageReference mStorage;
    FirebaseStorage mFirebaseStorage;
    int index = 0;                      //posicion inicial
    MessagesProvider mMessagesProvider;

    // Contructor vacio con instacias
    public ImageProvider() {
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorage = mFirebaseStorage.getReference();
        mMessagesProvider = new MessagesProvider();
    }

    public UploadTask save(final Context context, File file) {
        byte[] imageByte = CompressorBitmapImage.getImage(context, file.getPath(), 500, 500);
        StorageReference storage = mStorage.child(new Date() + ".jpg");      //El nombre que se guarda en la base de datos de la imagen será: la fecha actual + formato de la imagen (.jpg)
        mStorage = storage;
        UploadTask task = storage.putBytes(imageByte);
        return task;
    }

    //Método que almacena varios archivos
    public void uploadMultiple(final Context context,final ArrayList<Message> messages) {
        Uri[] uri = new Uri[messages.size()];
        File file = null;

        //Si es una imagen
        if(ExtensionFile.isImageFile(messages.get(index).getUrl())){
            file = CompressorBitmapImage.reduceImageSize(new File(messages.get(index).getUrl()));        //Reduce el tamaño de la imagen seleccionada y la guarda en la BD

        //Si es un video
        } else {
            file = new File(messages.get(index).getUrl());
        }


        uri[index] = Uri.parse("file://" + file.getPath());
        String name = UUID.randomUUID().toString();

        //Si es una imagen
        if(ExtensionFile.isImageFile(messages.get(index).getUrl())){
            name = name + ".jpg";           //Guarda el nombre + la extension en la bd

        //Si es un video
        } else {
            name = name + ".mp4";           //Guarda el nombre + la extension en la bd

        }

        final StorageReference ref = mStorage.child(name);                     //Ruta donde se almacenan las imagenes

        //Almacena cada una de las en la BD
        ref.putFile(uri[index]).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if(task.isSuccessful()){
                    //Si se ejecuto correctamente
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //Guarda los nuevos mensajes de cada imagen
                            String url = uri.toString();
                            messages.get(index).setUrl(url);
                            mMessagesProvider.create(messages.get(index));
                            index++;

                            //Si el index es menor al tamaño de los mensajes
                            if(index < messages.size()){
                                uploadMultiple(context, messages);
                            }
                        }
                    });

                } else {
                    Toast.makeText(context, "Hubo un error al almacenar la imagen", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    // Retorna la url de la imagem
    public Task<Uri> getDownloadUri(){
        return mStorage.getDownloadUrl();
    }

    //Eliminar la imagen a traves de la url
    public  Task<Void> delete(String url) {
        return mFirebaseStorage.getReferenceFromUrl(url).delete();
    }

}
