package proyecto.red_chart_v1.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import proyecto.red_chart_v1.R;

// Clase que puede deslizar y cerrar la ventana emergente inferior con gestos o acciones
public class BottomSheetSelectImage extends BottomSheetDialogFragment {

    //Metodo que abre el BottomSheetSelectImage
    public static BottomSheetSelectImage newInstance(){
        BottomSheetSelectImage bottomSheetSelectImage = new BottomSheetSelectImage();
        return bottomSheetSelectImage;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_select_item, container, false);
        return view;
    }
}
