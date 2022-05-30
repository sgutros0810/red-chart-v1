package proyecto.red_chart_v1.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.File;

import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.activities.CompleteInfoActivity;
import proyecto.red_chart_v1.activities.ProfileActivity;
import proyecto.red_chart_v1.providers.AuthProvider;
import proyecto.red_chart_v1.providers.ImageProvider;
import proyecto.red_chart_v1.providers.UsersProvider;

// Clase que puede deslizar y cerrar la ventana emergente inferior con gestos o acciones
public class BottomSheetSelectImage extends BottomSheetDialogFragment {

    LinearLayout mLinearLayoutDeleteImage;
    LinearLayout mLinearLayoutSelectImage;
    ImageProvider mImageProvider;
    AuthProvider mAuthProvider;
    UsersProvider mUsersProvider;

    String image;

    //Metodo que abre el BottomSheetSelectImage
    public static BottomSheetSelectImage newInstance(String url){
        BottomSheetSelectImage bottomSheetSelectImage = new BottomSheetSelectImage();
        Bundle argumentos = new Bundle();
        argumentos.putString("image", url);

        bottomSheetSelectImage.setArguments(argumentos);
        return bottomSheetSelectImage;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        image = getArguments().getString("image");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_select_item, container, false);
        mLinearLayoutDeleteImage = view.findViewById(R.id.linearLayoutDeleteImage);
        mLinearLayoutSelectImage = view.findViewById(R.id.linearLayoutSelectImage);

        mImageProvider = new ImageProvider();
        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();

        //Si pinchas sobre el 'linearLayoutDeleteImage' llama al metodo de Borrar la imagen de perfil
        mLinearLayoutDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteImage();
            }
        });

        //Si pinchas sobre el 'linearLayoutSelectImage' llama al metodo de Actualizar imagen de perfil
        mLinearLayoutSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateImage();
            }
        });


        return view;
    }

    private void deleteImage() {
        //Eliminar imagen de FirebaseStorage
        mImageProvider.delete(image).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //Si se completo, se eliminó de FirebaseStorage
                if(task.isSuccessful()) {
                    //Actualiza a null
                    mUsersProvider.updateImage(mAuthProvider.getId(), null).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            //Si se completo
                            if(task2.isSuccessful()){
                                //setImageDefault();
                                Toast.makeText(getContext(), "La imagen se eliminó correctamente", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getContext(), "No se puedo eliminar el dato de la imagen", Toast.LENGTH_LONG).show();
                            }
                        }
                    });


                } else {
                    Toast.makeText(getContext(), "No se ha podido eliminar la imagen", Toast.LENGTH_LONG).show();
                }
            }
        });
    }



    //Actualizar imagen de perfil (Elegir iamgen de galeria o tomar una foto con la camara)
    private void updateImage() {
        //Inicializa la libreria para seleccionar una imagen o tomar una foto con la cámara
        ((ProfileActivity)getActivity()).startPix();
    }



    //Muestra la imagen de perfil por defecto
    private void setImageDefault() {
        ((ProfileActivity)getActivity()).setImageDefault();
    }



}
