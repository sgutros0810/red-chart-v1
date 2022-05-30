package proyecto.red_chart_v1.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import de.hdodenhof.circleimageview.CircleImageView;
import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.fragments.BottomSheetInfo;
import proyecto.red_chart_v1.fragments.BottomSheetSelectImage;
import proyecto.red_chart_v1.fragments.BottomSheetUsername;
import proyecto.red_chart_v1.models.User;
import proyecto.red_chart_v1.providers.AuthProvider;
import proyecto.red_chart_v1.providers.ImageProvider;
import proyecto.red_chart_v1.providers.UsersProvider;
import proyecto.red_chart_v1.utils.MyToolBarSimple;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

//Clase para editar el perfil del usuario
public class ProfileActivity extends AppCompatActivity {

    FloatingActionButton mFabSelectImage;
    BottomSheetSelectImage mBottomSheetSelectImage;
    BottomSheetUsername mBottomSheetUsername;
    BottomSheetInfo mBottomSheetInfo;

    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;
    ImageProvider mImageProvider;

    TextView mTextViewUsername;
    TextView mTextViewPhone;
    TextView mTextViewInfo;

    CircleImageView mCircleImageProfile;
    ImageView mImageViewEditUsername;
    ImageView mImageViewEditInfo;

    User mUser;

    Options mOptions;

    ArrayList<String> mReturnValue = new ArrayList<>();
    File mImageFile;

    ListenerRegistration mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Muestra en el toolbar el titulo y muestra el boton si esta true
        MyToolBarSimple.show(this, "Perfil", true);

        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();

        mTextViewUsername = findViewById(R.id.textViewUsername);
        mTextViewPhone = findViewById(R.id.textViewPhone);
        mTextViewInfo = findViewById(R.id.textViewInfo);
        mCircleImageProfile = findViewById(R.id.circleImageProfile);
        mImageViewEditUsername = findViewById(R.id.imageViewEditUsername);
        mImageViewEditInfo = findViewById(R.id.imageViewEditInfo);


        mOptions = Options.init()
                .setRequestCode(100)                                          //Request code for activity results
                .setCount(1)                                                  //Numero de imagenes que podemos seleccionar
                .setFrontfacing(false)                                         //Por defecto, se pone la camara trasera
                .setPreSelectedUrls(mReturnValue)                             //Pre selected Image Urls
                .setExcludeVideos(true)                                       //No permite videos
                .setVideoDurationLimitinSeconds(0)                            //Duracion del video
                .setScreenOrientation(Options.SCREEN_ORIENTATION_LANDSCAPE)    //Orientacion vertical
                .setPath("/pix/images");


        mFabSelectImage = findViewById(R.id.fabSelectImage);
        mFabSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBottomSheetSelectImage();
            }
        });


        // si pincha sobre el imageViewEditUsername
        mImageViewEditUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Abre el Bottom Sheet
                openBottomSheetUsername();
            }
        });


        // si pincha sobre el imageViewEditInfo
        mImageViewEditInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Abre el Bottom Sheet
                openBottomSheetInfo();
            }
        });
        getUserInfo();
    }




    //Metodo que deja de escuchar los eventos en tiempo real de Firebase
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mListener != null) {
            //Elimina el evento 'addSnapshotListener', para que no se llame siempre
            mListener.remove();
        }
    }


    //Metodo que devuelve la informacion del usuario
    private void getUserInfo() {
        //con 'addSnapshotListener', muestra la informacion del usuario en tiempo real de la bd
        mListener = mUsersProvider.getUserInfo(mAuthProvider.getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                if(documentSnapshot != null){
                    //Si el documento(UID) existe en la base de datos
                    if(documentSnapshot.exists()){
                        //Recogemos los datos de la base de datos en model/User
                        mUser = documentSnapshot.toObject(User.class);

                        //Establecemos los valores a los TextView
                        mTextViewUsername.setText(mUser.getUsername());
                        mTextViewPhone.setText(mUser.getPhone());
                        mTextViewInfo.setText(mUser.getInfo());

                        //Validación de que la imagen no este null
                        if(mUser.getImage() != null) {
                            //Validación de que la url de la imagen no se encuentre vacia
                            if(!mUser.getImage().equals("")) {
                                //Libreria que muestra la imagen a nuestra aplicacion mediante una url
                                Picasso.get().load(mUser.getImage()).into(mCircleImageProfile);

                            //En caso de que la url de la imagen este vacia, muestra una imagen por defecto
                            } else {
                                setImageDefault();
                            }

                        //Si la imagen se encuentra null, muestra una imagen por defecto
                        } else {
                            setImageDefault();
                        }
                    }
                }
            }
        });

    }

    // Muestra el bottom Sheet de nombre de usuario
    private void openBottomSheetInfo() {
        //Validacion de que usuario no sea null
        if(mUser != null){
            mBottomSheetInfo = BottomSheetInfo.newInstance(mUser.getInfo());
            mBottomSheetInfo.show(getSupportFragmentManager(), mBottomSheetInfo.getTag());
        } else {
            Toast.makeText(this, "La informacion no se ha podido cargar", Toast.LENGTH_LONG).show();
        }
    }

    // Muestra el bottom Sheet de nombre de usuario
    private void openBottomSheetUsername() {
        //Validacion de que usuario no sea null
        if(mUser != null){
            mBottomSheetUsername = BottomSheetUsername.newInstance(mUser.getUsername());
            mBottomSheetUsername.show(getSupportFragmentManager(), mBottomSheetUsername.getTag());
        } else {
            Toast.makeText(this, "La informacion no se ha podido cargar", Toast.LENGTH_LONG).show();
        }
    }


    // Muestra el bottom Sheet de la imagen
    private void openBottomSheetSelectImage() {
        //Validacion de que usuario no sea null
        if(mUser != null){
            mBottomSheetSelectImage = BottomSheetSelectImage.newInstance(mUser.getImage());
            mBottomSheetSelectImage.show(getSupportFragmentManager(), mBottomSheetSelectImage.getTag());
        } else {
            Toast.makeText(this, "La informacion no se ha podido cargar", Toast.LENGTH_LONG).show();
        }
    }



    //imagen de perfil por defecto
    public void setImageDefault(){
        mCircleImageProfile.setImageResource(R.drawable.ic_person_white);
    }

    //Inicializa la libreria de réplica del selector de imágenes de WhatsApp
    public void startPix() {
        Pix.start(ProfileActivity.this, mOptions);
    }


    //Recibe las imagenes que el usuario selecciono y la guarda
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            mReturnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            mImageFile = new File(mReturnValue.get(0));
            mCircleImageProfile.setImageBitmap(BitmapFactory.decodeFile(mImageFile.getAbsolutePath()));
            saveImage();
        }
    }

    //Seleccion de Permisos de camara y galeria
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS) {
            //Si acepta los permisos
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Pix.start(ProfileActivity.this, mOptions);

                //Sino acepta los permisos
            } else {
                Toast.makeText(ProfileActivity.this, "Por favor, acepte los permisos para tener acceso a la cámara.", Toast.LENGTH_LONG).show();
            }
            return;
        }
    }

    // Método que guarda la imagen de perfil del usuario
    private void saveImage() {
        mImageProvider = new ImageProvider();
        mImageProvider.save(ProfileActivity.this, mImageFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                //Si se almacena la imagen correctamente
                if(task.isSuccessful()){
                    mImageProvider.getDownloadUri().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            //Después de conseguir la url, se lo pasamos para actualizar en la base de datos con url
                            mUsersProvider.updateImage(mAuthProvider.getId(), url).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(ProfileActivity.this, "La imagen se actualizó correctamente", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                } else {
                    Toast.makeText(ProfileActivity.this, "No se pudo guardar la imagen", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}