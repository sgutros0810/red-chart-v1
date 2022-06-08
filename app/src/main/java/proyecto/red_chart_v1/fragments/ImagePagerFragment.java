package proyecto.red_chart_v1.fragments;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.adapters.CardAdapter;


public class ImagePagerFragment extends Fragment {

    View mView;
    CardView mCardViewOptions;
    ImageView mImageViewPicture;
    ImageView mImageViewBack;

    public static Fragment newInstance(int position, String imagePath) {
        ImagePagerFragment fragment = new ImagePagerFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
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

        //El cardview tiene el metodo 'setMaxCardElevation'
        mCardViewOptions.setMaxCardElevation(mCardViewOptions.getCardElevation() * CardAdapter.MAX_ELEVATION_FACTOR);

        //Obtiene las rutas de 'data' del campo 'image'
        String imagePath = getArguments().getString("image");

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

         return mView;
    }

    //Método que retorna un CardView
    public CardView getCardView(){
        return mCardViewOptions;
    }
}