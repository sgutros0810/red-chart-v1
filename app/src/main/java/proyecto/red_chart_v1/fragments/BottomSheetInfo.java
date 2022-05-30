package proyecto.red_chart_v1.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.providers.AuthProvider;
import proyecto.red_chart_v1.providers.ImageProvider;
import proyecto.red_chart_v1.providers.UsersProvider;

// Clase que puede deslizar y cerrar la ventana emergente inferior con gestos o acciones
public class BottomSheetInfo extends BottomSheetDialogFragment {

    Button mButtonCancel;
    Button mButtonSave;
    EditText mEditTextInfo;

    ImageProvider mImageProvider;
    AuthProvider mAuthProvider;
    UsersProvider mUsersProvider;

    String info;

    //Metodo que abre el BottomSheetSelectImage
    public static BottomSheetInfo newInstance(String info){
        BottomSheetInfo bottomSheetSelectImage = new BottomSheetInfo();
        Bundle argumentos = new Bundle();
        argumentos.putString("info", info);

        bottomSheetSelectImage.setArguments(argumentos);
        return bottomSheetSelectImage;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        info = getArguments().getString("info");
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_info, container, false);
        mButtonCancel = view.findViewById(R.id.btnCancel);
        mButtonSave = view.findViewById(R.id.btnSave);

        mEditTextInfo = view.findViewById(R.id.editTextInfo);
        mEditTextInfo.setText(info);            //El estado del usuario actual de la base de datos, se establece cuando abre el bottomSheet

        mImageProvider = new ImageProvider();
        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();

        //Cuando pincha sobre guardar, actualiza el estado del usuario, que inserta en el edit text, cuando pincha en el boton 'Guardar'
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateInfo();
            }
        });

        //Cuando pincha sobre cancelar, cierra el bottomSheet
        mButtonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Oculta el bottomSheet
                dismiss();
            }
        });
        return view;
    }

    //Actualizar el estado del usuario en la bd
    private void updateInfo() {
        String info = mEditTextInfo.getText().toString();
        //Si el nombre de usuario no esta vacio
        if(!info.equals("")){
            //Si se ejecuta correctamente
            mUsersProvider.updateInfo(mAuthProvider.getId(), info).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    dismiss();
                    Toast.makeText(getContext(), "El estado se ha actualizado", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
