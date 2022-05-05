package proyecto.red_chart_v1.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import proyecto.red_chart_v1.models.User;

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

    //Método que permite almacenar el usuario en la base de datos
    public Task<Void> create (User user) {
        //Retorna el id del usuario a la colección
        return mCollection.document(user.getId()).set(user);
    }

    //Método que actualiza los datos del usuario en la base de datos (solo actualiza el nombre de usuario e imagen de perfil)
    public Task<Void> update (User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("username", user.getUsername());
        return mCollection.document(user.getId()).update(map);
    }

}
