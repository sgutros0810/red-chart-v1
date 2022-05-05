package proyecto.red_chart_v1.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import proyecto.red_chart_v1.models.User;

public class UsersProvider {
    private CollectionReference mCollection;

    //Constructor
    public UsersProvider() {
        //Crea la colección
        mCollection = FirebaseFirestore.getInstance().collection("Users");
    }

    //Método que permite almacenar el usuario en la base de datos
    public Task<Void> create (User user) {
        //Retorna el id del usuario a la colección
        return mCollection.document(user.getId()).set(user);
    }
}
