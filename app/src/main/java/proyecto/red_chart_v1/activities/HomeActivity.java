package proyecto.red_chart_v1.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.material.tabs.TabLayout;

import proyecto.red_chart_v1.R;
import proyecto.red_chart_v1.adapters.ViewPagerAdapter;
import proyecto.red_chart_v1.fragments.CameraFragment;
import proyecto.red_chart_v1.fragments.ChatsFragment;
import proyecto.red_chart_v1.fragments.ContactsFragment;
import proyecto.red_chart_v1.providers.AuthProvider;
import proyecto.red_chart_v1.providers.UsersProvider;
import proyecto.red_chart_v1.utils.AppBackgroundHelper;

public class HomeActivity extends AppCompatActivity {
    Button mButtonSignOut;
    AuthProvider mAuthProvider;
    UsersProvider mUsersProvider;

    Toolbar mToolBar;
    MenuItem item;
    TabLayout mTabLayout;
    ViewPager mViewPager;

    ChatsFragment mChatsFragment;           // Fragmento de Chats
    ContactsFragment mContactsFragment;     // Fragmento de Contactos
    CameraFragment mCameraFragment;         // Fragmento de Cámara

    int mTabSelected = 1;                   // el tab de CHATS

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager = findViewById(R.id.viewPager);
        mToolBar = (Toolbar) findViewById(R.id.toolBar);

        // -- ToolBar --
        setSupportActionBar(mToolBar);


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
        mViewPager.setCurrentItem(mTabSelected);                                            // Cuando se abre la aplicacion, se abre por defecto en el tab seleccionado

        setupTabIcom();

        mUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();

        createToken();
    }

    //Método que crea el token al usuario
    private void createToken() {
        mUsersProvider.createToken(mAuthProvider.getId());
    }

    //Cuando esta dentro de la aplicación
    @Override
    protected void onStart() {
        super.onStart();
        //Se encuentra dentro de la app
        AppBackgroundHelper.online(HomeActivity.this, true);
    }

    //Cuando sale de la aplicación
    @Override
    protected void onStop() {
        super.onStop();
        //Se encuentra en segundo plano o la ha cerrado la app
        AppBackgroundHelper.online(HomeActivity.this, false);
    }

    //Opciones del menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    //Boton de mas opciones
    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            //Cierra la session del usuario
            case R.id.itemCerrarSesion:
                signOut();
                return true;

            case R.id.itemPerfil:
                goToProfile();
                return true;

            case R.id.itemAdd:
                goToAddMultiUsers();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    //Método que va a la actividad de seleccionar usuarios para añadirlos a un grupo
    private void goToAddMultiUsers() {
        Intent intent = new Intent(HomeActivity.this, AddMultiUsersActivity.class);
        startActivity(intent);
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

    // Método que lleva al Perfil del usuario
    private void goToProfile() {
        Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
        startActivity(intent);
    }

}