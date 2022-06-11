package proyecto.red_chart_v1.providers;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import proyecto.red_chart_v1.models.User;

//Clase que maneja los datos de Firebase de la coleccion 'Users'
public class UsersProvider {

    private CollectionReference mCollection;

    //Constructor
    public UsersProvider() {
        //Crea la colección
        mCollection = FirebaseFirestore.getInstance().collection("Users");
    }

    //Método que verifica si el usuario ya existe mediante el id
    public DocumentReference getUserInfo(String id) {
        return mCollection.document(id);
    }

    //Método que muestra todos los usuarios por el nombre de usuario
    public Query getAllUsersByName(){
        return mCollection.orderBy("username");
    }

    //Método que permite almacenar el usuario en la base de datos
    public Task<Void> create (User user) {
        //Retorna la informacion del usuario por Id a la colección
        return mCollection.document(user.getId()).set(user);
    }

    //Método que actualiza los datos del usuario en la base de datos (solo actualiza el nombre de usuario e imagen de perfil)
    public Task<Void> update (User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("username", user.getUsername());
        map.put("image", user.getImage());

        return mCollection.document(user.getId()).update(map);
    }

    //Método que actualiza el campo 'image' del usuario de la base de datos
    public Task<Void> updateImage(String id, String url){
        Map<String, Object> map = new HashMap<>();
        map.put("image", url);
        return mCollection.document(id).update(map);
    }

    //Método que actualiza el campo 'username' del usuario de la base de datos
    public Task<Void> updateUsername(String id, String username){
        Map<String, Object> map = new HashMap<>();
        map.put("username", username);
        return mCollection.document(id).update(map);
    }

    //Método que actualiza el campo 'info', el estado de perfil de la base de datos
    public Task<Void> updateInfo(String id, String info){
        Map<String, Object> map = new HashMap<>();
        map.put("info", info);
        return mCollection.document(id).update(map);
    }

    //Método que actualiza el campo 'online' y el campo 'lastConnect'
    public Task<Void> updateOnline(String idUser, boolean status){
        Map<String, Object> map = new HashMap<>();
        map.put("online", status);                      //Si se encuentra 'En linea' o no
        map.put("lastConnect", new Date().getTime());   //Última vez que se conectó

        return mCollection.document(idUser).update(map);
    }

    //Método que no retorna nada, crea el token por usuario
    public void createToken(final String idUser) {
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                //Cuando termina de ejecutar correctamente, actualiza el campo 'token' en la BD
                String token = instanceIdResult.getToken();       //crea el Token
                Map<String, Object> map = new HashMap<>();
                map.put("token", token);

                mCollection.document(idUser).update(map);
            }
        });
    }
}
