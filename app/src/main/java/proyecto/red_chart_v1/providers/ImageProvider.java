package proyecto.red_chart_v1.providers;

import android.content.Context;
import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Date;

import proyecto.red_chart_v1.utils.CompressorBitmapImage;

public class ImageProvider {

    StorageReference mStorage;
    FirebaseStorage mFirebaseStorage;

    // Contructor vacio con instacias
    public ImageProvider() {
        mFirebaseStorage = FirebaseStorage.getInstance();
        mStorage = mFirebaseStorage.getReference();
    }

    public UploadTask save(Context context, File file) {
        byte[] imageByte = CompressorBitmapImage.getImage(context, file.getPath(), 500, 500);
        StorageReference storage = mStorage.child(new Date() + ".jpg");      //El nombre que se guarda en la base de datos de la imagen será: la fecha actual + formato de la imagen (.jpg)
        mStorage = storage;
        UploadTask task = storage.putBytes(imageByte);
        return task;
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
