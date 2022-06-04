package proyecto.red_chart_v1.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

import proyecto.red_chart_v1.models.Chat;

//Clase que maneja los datos de Firebase de la coleccion de 'Chats'
public class ChatsProvider {

    CollectionReference mCollection;

    //Constructor
    public ChatsProvider(){
        //Creamos una colección de 'chats'
        mCollection = FirebaseFirestore.getInstance().collection("Chats");
    }

    //Retorna la colecion con el id del chat, que crea la informacion en la bd
    public Task <Void> create (Chat chat) {
        return mCollection.document(chat.getId()).set(chat);
    }


    public Query getUserChats(String idUser){
        //Se encuentra n el array el id del usuario lo retorna
        return mCollection.whereArrayContains("ids",idUser);
    }

    //Método que retorna si existe algun chat con el 'Usuario Principal (User1)' + el otro 'Usuario (User2)'
    public Query getChatByUser1AndUser2 (String idUser1, String idUser2) {
        ArrayList<String> ids = new ArrayList<>();
        ids.add(idUser1 + idUser2);

        //Busca la combinacion en 'Chats' del idUser1 + idsUser2
        return mCollection.whereIn("id", ids);
    }

}