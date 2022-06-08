package proyecto.red_chart_v1.fragments;

import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.adapters.CardAdapter;


public class ImagePagerFragment extends Fragment {

    View mView;
    CardView mCardViewOptions;

    public static Fragment newInstance(int position) {
        ImagePagerFragment fragment = new ImagePagerFragment();
        Bundle args = new Bundle();
        args.putInt("position", position);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         mView = inflater.inflate(R.layout.fragment_image_pager, container, false);

         //instancia el cardview
         mCardViewOptions = mView.findViewById(R.id.cardViewOptions);

         //El cardview tiene el metodo 'setMaxCardElevation'
        mCardViewOptions.setMaxCardElevation(mCardViewOptions.getCardElevation() * CardAdapter.MAX_ELEVATION_FACTOR);

         return mView;
    }

    //Método que retorna un CardView
    public CardView getCardView(){
        return mCardViewOptions;
    }
}