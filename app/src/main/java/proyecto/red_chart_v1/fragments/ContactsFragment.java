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
import proyecto.red_chart_v1.adapters.ContactsAdapter;
import proyecto.red_chart_v1.models.User;
import proyecto.red_chart_v1.providers.AuthProvider;
import proyecto.red_chart_v1.providers.UsersProvider;


public class ContactsFragment extends Fragment {

    View mView;
    RecyclerView mRecyclerViewContacts;

    ContactsAdapter mContactsAdapter;

    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;

    public ContactsFragment() {
        // Required empty public constructor
    }

    //Se instacia la vista que utilizamos (Fragment de contactos)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_contacts, container, false);
        mRecyclerViewContacts = mView.findViewById(R.id.recyclerViewContacts);

        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();

        //Se posicionan uno debajo del otro
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerViewContacts.setLayoutManager(linearLayoutManager);

        return mView;
    }

    //Metodo en ciclo de vida
    @Override
    public void onStart() {
        super.onStart();
        //Recoge la informacion de la clase UserProviders
        Query query = mUsersProvider.getAllUsersByName();

        FirestoreRecyclerOptions <User> options = new FirestoreRecyclerOptions.Builder<User>()
                                                  .setQuery(query, User.class)
                                                  .build();
        mContactsAdapter = new ContactsAdapter(options, getContext());

        mRecyclerViewContacts.setAdapter(mContactsAdapter);

        //Cambios en tiempo real que sucede en la bd
        mContactsAdapter.startListening();

    }

    //Método que para de escuchar los cambios
    @Override
    public void onStop() {
        super.onStop();
        mContactsAdapter.stopListening();
    }
}