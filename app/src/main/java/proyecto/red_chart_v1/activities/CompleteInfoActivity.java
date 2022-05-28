package proyecto.red_chart_v1.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.models.User;
import proyecto.red_chart_v1.providers.AuthProvider;
import proyecto.red_chart_v1.providers.ImageProvider;
import proyecto.red_chart_v1.providers.UsersProvider;

public class CompleteInfoActivity extends AppCompatActivity {

    TextInputEditText mTextInputUsername;
    Button mButtonConfirm;
    CircleImageView mCircleImagePhoto;

    UsersProvider mUserProvider;
    AuthProvider mAuthProvider;
    ImageProvider mImageProvider;

    Options mOptions;

    ArrayList<String> mReturnValue = new ArrayList<>();
    File mImageFile;
    String mUsername = "";

    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_info);

        mTextInputUsername  = findViewById(R.id.textInputUsername);
        mButtonConfirm      = findViewById(R.id.btnConfirm);
        mCircleImagePhoto   = findViewById(R.id.circleImagePhoto);

        mUserProvider       = new UsersProvider();
        mAuthProvider       = new AuthProvider();
        mImageProvider      = new ImageProvider();

        mDialog = new ProgressDialog(CompleteInfoActivity.this);        //Progress Dialog
        mDialog.setTitle("Espere un momento");                                  // Mensaje del progress
        mDialog.setMessage("Guardando información");


        mOptions = Options.init()
                .setRequestCode(100)                                          //Request code for activity results
                .setCount(1)                                                  //Numero de imagenes que podemos seleccionar
                .setFrontfacing(false)                                         //Por defecto, se pone la camara trasera
                .setPreSelectedUrls(mReturnValue)                             //Pre selected Image Urls
                .setExcludeVideos(true)                                       //No permite videos
                .setVideoDurationLimitinSeconds(0)                            //Duracion del video
                .setScreenOrientation(Options.SCREEN_ORIENTATION_LANDSCAPE)    //Orientacion vertical
                .setPath("/pix/images");


        //Cuando pulsa el boton, envía los datos
        mButtonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUsername = mTextInputUsername.getText().toString();  //nombre de usuario que introduce el usuario

                //Validación de que no este vacio el usuario y el suario seleccione una imagen
                if(!mUsername.equals("") && mUsername!= null) {
                    saveImage();      //Primero guardamos la imagen
                } else {
                    Toast.makeText(CompleteInfoActivity.this, "Seleccione una imagen y/o ingrese su nombre de usuario", Toast.LENGTH_LONG).show();
                }
            }
        });

        //Cuando pulsa el usuario en la foto
        mCircleImagePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //inicializa el metodo cuando picha sobre la imagen de perfil
                startPix();
            }
        });
    }

    //Inicializa la libreria de réplica del selector de imágenes de WhatsApp
    private void startPix() {
        Pix.start(CompleteInfoActivity.this, mOptions);
    }

    //Método que envia a la base de datos la imagen de perfil y el nombre de usuario
    private void updateUserInfo(String url) {
        mUsername = mTextInputUsername.getText().toString();  //nombre de usuario que introduce el usuario

        //Creamos el usuario
        User user = new User();
        user.setUsername(mUsername);
        user.setId(mAuthProvider.getId());       //Con el UID del usuario, hacemos que se actualice.
        user.setImage(url);
        mUserProvider.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {

            //Cuando termina de actualizarse correctamente
            @Override
            public void onSuccess(Void unused) {
                goToHomeActivity();
            }
        });

    }

    // Metodo que dirige a HomeActivity
    private void goToHomeActivity() {
        mDialog.dismiss();                                                                                                          // Ocultar el progress dialog
        Toast.makeText(CompleteInfoActivity.this, "La información se ha actualizado", Toast.LENGTH_SHORT).show();      // Mensaje

        Intent intent = new Intent(CompleteInfoActivity.this, HomeActivity.class);                                   // Dirige a HomeActivity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);                                           // Borra el historial de pantallas
        startActivity(intent);
    }

    // Método que guarda la imagen de perfil del usuario
    private void saveImage() {
        mDialog.show();     // Mostrar el progress dialog
        mImageProvider.save(CompleteInfoActivity.this, mImageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                //Si se almacena la imagen correctamente
                if(task.isSuccessful()){
                    mImageProvider.getDownloadUri().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            //Después de conseguir la url, se lo pasamos para actualizar en la base de datos
                            updateUserInfo(url);
                        }
                    });
                } else {
                    mDialog.dismiss();      // Ocultar el progress dialog
                    Toast.makeText(CompleteInfoActivity.this, "No se pudo guardar la imagen", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    //Recibe las imagenes que el usuario selecciono y la guarda
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            mReturnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            mImageFile = new File(mReturnValue.get(0));
            mCircleImagePhoto.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
        }
    }

    //Seleccion de Permisos de camara y galeria
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS) {
            //Si acepta los permisos
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Pix.start(CompleteInfoActivity.this, mOptions);

            //Sino acepta los permisos
            } else {
                Toast.makeText(CompleteInfoActivity.this, "Por favor, acepte los permisos para tener acceso a la cámara.", Toast.LENGTH_LONG).show();
            }
            return;

        }
    }


}