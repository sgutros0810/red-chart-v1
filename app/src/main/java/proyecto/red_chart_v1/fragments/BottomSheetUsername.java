package proyecto.red_chart_v1.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.activities.ProfileActivity;
import proyecto.red_chart_v1.providers.AuthProvider;
import proyecto.red_chart_v1.providers.ImageProvider;
import proyecto.red_chart_v1.providers.UsersProvider;

// Clase que puede deslizar y cerrar la ventana emergente inferior con gestos o acciones
public class BottomSheetUsername extends BottomSheetDialogFragment {

    Button mButtonCancel;
    Button mButtonSave;
    EditText mEditTextUsername;

    ImageProvider mImageProvider;
    AuthProvider mAuthProvider;
    UsersProvider mUsersProvider;

    String username;

    //Metodo que abre el BottomSheetSelectImage
    public static BottomSheetUsername newInstance(String username){
        BottomSheetUsername bottomSheetSelectImage = new BottomSheetUsername();
        Bundle argumentos = new Bundle();
        argumentos.putString("username", username);

        bottomSheetSelectImage.setArguments(argumentos);
        return bottomSheetSelectImage;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        username = getArguments().getString("username");
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_username, container, false);
        mButtonCancel = view.findViewById(R.id.btnCancel);
        mButtonSave = view.findViewById(R.id.btnSave);
        mEditTextUsername = view.findViewById(R.id.editTextUsername);
        mEditTextUsername.setText(username);            //El nombre del usuario actual de la base de datos, se establece cuando abre el bottomSheet

        mImageProvider = new ImageProvider();
        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();

        //Cuando pincha sobre guardar, actualizael nombre del usuario, que inserta en el edit text, cuando pincha en el boton 'Guardar'
        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUsername();
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

    //Actualizar el nombre del usuario en la bd
    private void updateUsername() {
        String username = mEditTextUsername.getText().toString();
        //Si el nombre de usuario no esta vacio
        if(!username.equals("")){
            //Si se ejecuta correctamente
            mUsersProvider.updateUsername(mAuthProvider.getId(), username).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    dismiss();
                    Toast.makeText(getContext(), "El nombre de usuario se ha actualizado", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

}
