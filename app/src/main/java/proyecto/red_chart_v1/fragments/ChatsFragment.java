package proyecto.red_chart_v1.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.adapters.ChatsAdapter;
import proyecto.red_chart_v1.models.Chat;
import proyecto.red_chart_v1.models.User;
import proyecto.red_chart_v1.providers.AuthProvider;
import proyecto.red_chart_v1.providers.ChatsProvider;
import proyecto.red_chart_v1.providers.UsersProvider;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatsFragment} factory method to
 * create an instance of this fragment.
 */
public class ChatsFragment extends Fragment {

    View mView;
    RecyclerView mRecyclerViewChats;

    ChatsAdapter mChatsAdapter;

    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;
    ChatsProvider mChatsProvider;

    public ChatsFragment() {
        // Required empty public constructor
    }

    //Se instacia la vista que utilizamos (Fragment de contactos)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_chats, container, false);
        mRecyclerViewChats = mView.findViewById(R.id.recyclerViewChats);

        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();
        mChatsProvider = new ChatsProvider();

        //Se posicionan uno debajo del otro
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerViewChats.setLayoutManager(linearLayoutManager);

        return mView;
    }

    //Metodo en ciclo de vida
    @Override
    public void onStart() {
        super.onStart();
        //Recoge la informacion de la clase UserProviders
        Query query = mChatsProvider.getUserChats(mAuthProvider.getId());

        FirestoreRecyclerOptions<Chat> options = new FirestoreRecyclerOptions.Builder<Chat>()
                .setQuery(query, Chat.class)
                .build();

        mChatsAdapter = new ChatsAdapter(options, getContext());
        mRecyclerViewChats.setAdapter(mChatsAdapter);
        //Cambios en tiempo real que sucede en la bd
        mChatsAdapter.startListening();
    }

    //Método que para de escuchar los cambios
    @Override
    public void onStop() {
        super.onStop();
        mChatsAdapter.stopListening();
    }


    //Método que deja de escuchar los eventos en tiempo real de Firebase
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mChatsAdapter.getListener() != null) {
            //Elimina el evento 'addSnapshotListener', para que no se llame siempre
            mChatsAdapter.getListener().remove();
        }

        if(mChatsAdapter.getListenerLastMessage() != null){
            mChatsAdapter.getListenerLastMessage().remove();
        }
    }



}