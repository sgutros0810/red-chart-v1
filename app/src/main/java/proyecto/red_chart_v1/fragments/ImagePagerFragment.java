package proyecto.red_chart_v1.fragments;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.VideoView;

import java.io.File;

import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.activities.ConfirmImageSendActivity;
import proyecto.red_chart_v1.adapters.CardAdapter;
import proyecto.red_chart_v1.utils.ExtensionFile;


public class ImagePagerFragment extends Fragment {

    View mView;
    CardView mCardViewOptions;
    ImageView mImageViewPicture;
    ImageView mImageViewBack;
    ImageView mImageViewSend;
    FrameLayout mFrameLayoutVideo;
    LinearLayout mLinearLayoutViewPager;
    EditText mEditTextComment;
    VideoView mVideo;
    View mViewVideo;
    ImageView mImageViewVideo;


    public static Fragment newInstance(int position, String imagePath, int size) {
        ImagePagerFragment fragment = new ImagePagerFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        args.putInt("size", size);           //numero de las imagenes seleccionadas
        args.putString("image", imagePath);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView                   = inflater.inflate(R.layout.fragment_image_pager, container, false);

        //Instancias
        mCardViewOptions        = mView.findViewById(R.id.cardViewOptions);
        mImageViewPicture       = mView.findViewById(R.id.imageViewPicture);
        mImageViewBack          = mView.findViewById(R.id.imageViewBack);
        mImageViewSend          = mView.findViewById(R.id.imageViewSend);
        mFrameLayoutVideo       = mView.findViewById(R.id.frameLayoutVideo);
        mLinearLayoutViewPager  = mView.findViewById(R.id.linearLayoutViewPager);
        mEditTextComment        = mView.findViewById(R.id.editTextComment);
        mVideo = mView.findViewById(R.id.videoView);
        mViewVideo              = mView.findViewById(R.id.viewVideo);
        mImageViewVideo         = mView.findViewById(R.id.imageViewVideo);

        //El cardview tiene el metodo 'setMaxCardElevation'
        mCardViewOptions.setMaxCardElevation(mCardViewOptions.getCardElevation() * CardAdapter.MAX_ELEVATION_FACTOR);

        String imagePath = getArguments().getString("image");       //Obtiene las rutas de 'data' del campo 'image'
        int size = getArguments().getInt("size");                   //Obtiene el número de imagenes
        int position = getArguments().getInt("position");           //Obtiene el número de posicion de la imagen


        //Si el archivo es una imagen
        if(ExtensionFile.isImageFile(imagePath)){
            mFrameLayoutVideo.setVisibility(View.GONE);     //Oculta el frameLayout
            mImageViewPicture.setVisibility(View.VISIBLE);  //Muestra la imagen

            //Validacion de si no viene las rutas de las imagenes en null
            if(imagePath != null){
                //Obtiene el archivo
                File file = new File(imagePath);

                //Recibe un archivo por su ruta
                mImageViewPicture.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
            }

        //Si el archivo es un video
        } else if(ExtensionFile.isVideoFile(imagePath)){
            mFrameLayoutVideo.setVisibility(View.VISIBLE);  //Muestra el frameLayout
            mImageViewPicture.setVisibility(View.GONE);     //Oculta la imagen

            Uri uri = Uri.parse(imagePath);
            mVideo.setVideoURI(uri);                    //Ruta de la galeria
        }




        //Si solo seleccionam solo una imagen
        if(size == 1){
            //Personalizamos los padding
            mLinearLayoutViewPager.setPadding(0,0,0,0);

            //Establecemos unos margenes
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mImageViewBack.getLayoutParams();
            params.leftMargin = 10;
            params.topMargin = 35;
        }



        //Si pincha sobre el botón atrás,
        mImageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().finish(); //Vuelve a la anterior actividad
            }
        });


        mEditTextComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            //Evento que permite saber cuando el usuario escribe ('charSequence' -> 'Obtiene el mensaje de cada imagen')
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                ((ConfirmImageSendActivity) getActivity()).setMessages(position, charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //Si pincha sobre el botón 'imageViewSend'
        mImageViewSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((ConfirmImageSendActivity) getActivity()).send();
            }
        });

        //Si pincha sobre el video -> 'frameLayoutVideo', se reproduce
        mFrameLayoutVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Si el usuario no esta mirando el video
                if(!mVideo.isPlaying()){
                    mViewVideo.setVisibility(View.GONE);         //Oculta el video
                    mImageViewVideo.setVisibility(View.GONE);    //Oculta el icono del play
                    mVideo.start();                              //Inicializa el video

                //Si el usuario esta mirando el video (en reproduccion)
                } else {
                    mViewVideo.setVisibility(View.VISIBLE);      //Muestra el video
                    mImageViewVideo.setVisibility(View.VISIBLE); //Muestra el icono del play
                    mVideo.pause();                              //Para el video
                }
            }
        });

         return mView;
    }

    //Reenscribo el metodo de pausar, para saber si se esta reproducciendo, y si sale de la app, se para
    @Override
    public void onPause() {
        super.onPause();

        //Si se esta reproducciendo, y se sale de la app, se para
        if(mVideo.isPlaying()){
            mVideo.pause();
        }

    }

    //Método que retorna un CardView
    public CardView getCardView(){
        return mCardViewOptions;
    }
}