package proyecto.red_chart_v1.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;

import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.adapters.ViewPagerAdapter;
import proyecto.red_chart_v1.fragments.CameraFragment;
import proyecto.red_chart_v1.fragments.ChatsFragment;
import proyecto.red_chart_v1.fragments.ContactsFragment;
import proyecto.red_chart_v1.providers.AuthProvider;

public class HomeActivity extends AppCompatActivity {
    Button mButtonSignOut;
    AuthProvider mAuthProvider;

    TabLayout mTabLayout;
    ViewPager mViewPager;

    ChatsFragment mChatsFragment;           // Fragmento de Chats
    ContactsFragment mContactsFragment;     // Fragmento de Contactos
    CameraFragment mCameraFragment;         // Fragmento de Cámara



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mButtonSignOut = findViewById(R.id.btnSignOut);
        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.viewPager);


        // --- FRAGMENTOS --- //
        mViewPager.setOffscreenPageLimit(2);                                                // Contiene un número de fragmentos: Chats y Contactos
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());       // Tiene como parametro un Fragmento

        // Instacia de fragmetos
        mChatsFragment = new ChatsFragment();
        mContactsFragment = new ContactsFragment();
        mCameraFragment = new CameraFragment();

        // Añade los fragmentos en el adapter (Fragmento, titulo)
        adapter.addFragment(mCameraFragment, "");
        adapter.addFragment(mChatsFragment, "CHATS" );
        adapter.addFragment(mContactsFragment, "CONTACTOS" );
        mViewPager.setAdapter(adapter);                                                     // Lo añade
        mTabLayout.setupWithViewPager(mViewPager);


        setupTabIcom();

        mAuthProvider = new AuthProvider();

        // Cuando pulsa cierra sesion
        mButtonSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signOut();
            }
        });
    }

    // Método que establece el icono en la posicion 0
    private void setupTabIcom() {
        // En el primer elemento, importamos el icono de la camara
        mTabLayout.getTabAt(0).setIcon(R.drawable.ic_camera);

        //Creamos un LinearLayout
        LinearLayout linearLayout = ((LinearLayout) ((LinearLayout) mTabLayout.getChildAt(0)).getChildAt(0));
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
        layoutParams.weight = 0.5f;     // Ancho
        linearLayout.setLayoutParams(layoutParams);
    }

    // Método que cierra sesión
    private void signOut(){
        mAuthProvider.signOut();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);                                           // Borra el historial de pantallas
        startActivity(intent);
    }

}