package proyecto.red_chart_v1.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

import proyecto.red_chart_v1.models.Chat;

//CLases para trabajar con la base de datos
public class ChatsProvider {

    CollectionReference mCollection;

    //Constructor
    public ChatsProvider(){
        //Creamos una colección de 'chats'
        mCollection = FirebaseFirestore.getInstance().collection("Chats");
    }

    //Retorna una tarea, que crea la informacion en la bd
    public Task <Void> create (Chat chat) {
        return mCollection.document().set(chat);
    }

    //Método que retorna si existe algun chat con el 'Usuario Principal (User1)' + el otro 'Usuario (User2)'
    public Query getChatByUser1AndUser2 (String idUser1, String idUser2) {
        ArrayList<String> ids = new ArrayList<>();
        ids.add(idUser1 + idUser2);

        //Busca la combinacion en 'Chats' del idUser1 + idsUser2
        return mCollection.whereIn("id", ids);
    }
}
