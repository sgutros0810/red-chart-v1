package proyecto.red_chart_v1.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import proyecto.red_chart_v1.models.User;

//Clase que maneja los datos de Firebase
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

    //Método que Actualiza el campo 'image' del usuario de la base de datos
    public Task<Void> updateImage(String id, String url){
        Map<String, Object> map = new HashMap<>();
        map.put("image", url);
        return mCollection.document(id).update(map);
    }

    //Método que Actualiza el campo 'username' del usuario de la base de datos
    public Task<Void> updateUsername(String id, String username){
        Map<String, Object> map = new HashMap<>();
        map.put("username", username);
        return mCollection.document(id).update(map);
    }

    //Método que Actualiza el campo 'info', el estado del usuario de la base de datos
    public Task<Void> updateInfo(String id, String info){
        Map<String, Object> map = new HashMap<>();
        map.put("info", info);
        return mCollection.document(id).update(map);
    }

}
