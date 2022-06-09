package proyecto.red_chart_v1.fragments;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.File;

import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.activities.ConfirmImageSendActivity;
import proyecto.red_chart_v1.adapters.CardAdapter;


public class ImagePagerFragment extends Fragment {

    View mView;
    CardView mCardViewOptions;
    ImageView mImageViewPicture;
    ImageView mImageViewBack;
    ImageView mImageViewSend;
    LinearLayout mLinearLayoutViewPager;
    EditText mEditTextComment;



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
        mView = inflater.inflate(R.layout.fragment_image_pager, container, false);

        //Instancias
        mCardViewOptions = mView.findViewById(R.id.cardViewOptions);
        mImageViewPicture = mView.findViewById(R.id.imageViewPicture);
        mImageViewBack = mView.findViewById(R.id.imageViewBack);
        mImageViewSend = mView.findViewById(R.id.imageViewSend);
        mLinearLayoutViewPager = mView.findViewById(R.id.linearLayoutViewPager);
        mEditTextComment = mView.findViewById(R.id.editTextComment);

        //El cardview tiene el metodo 'setMaxCardElevation'
        mCardViewOptions.setMaxCardElevation(mCardViewOptions.getCardElevation() * CardAdapter.MAX_ELEVATION_FACTOR);


        String imagePath = getArguments().getString("image");       //Obtiene las rutas de 'data' del campo 'image'
        int size = getArguments().getInt("size");                   //Obtiene el número de imagenes
        int position = getArguments().getInt("position");           //Obtiene el número de posicion de la imagen

        //Si solo seleccionam solo una imagen
        if(size == 1){
            //Personalizamos los padding
            mLinearLayoutViewPager.setPadding(0,0,0,0);

            //Establecemos unos margenes
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) mImageViewBack.getLayoutParams();
            params.leftMargin = 10;
            params.topMargin = 35;
        }

        //Validacion de si no viene las rutas de las imagenes en null
        if(imagePath != null){
            //Obtiene el archivo
            File file = new File(imagePath);

            //Recibe un archivo por su ruta
            mImageViewPicture.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
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

         return mView;
    }

    //Método que retorna un CardView
    public CardView getCardView(){
        return mCardViewOptions;
    }
}