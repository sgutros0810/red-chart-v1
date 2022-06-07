package proyecto.red_chart_v1.providers;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import proyecto.red_chart_v1.models.Chat;

//Clase que maneja los datos de Firebase de la coleccion de 'Chats'
public class ChatsProvider {

    CollectionReference mCollection;

    //Constructor
    public ChatsProvider(){
        //Creamos una colección de 'chats'
        mCollection = FirebaseFirestore.getInstance().collection("Chats");
    }

    //Retorna la coleccion con el id del chat, que crea la informacion en la bd
    public Task <Void> create (Chat chat) {
        return mCollection.document(chat.getId()).set(chat);
    }

    //Método que retorna todos los chats del usuario y filtra si es mayor o igual a 1
    public Query getUserChats(String idUser){
        //Si encuentra en el array el id del usuario y si el campo 'numberMessages' es mayor o igual a 1, lo retorna
        return mCollection.whereArrayContains("ids",idUser).whereGreaterThanOrEqualTo("numberMessages", 1);
    }

    //Obtiene el id de un documento en Chats
    public DocumentReference getChatById(String idChat){
        //Si encuentra en el array el id del usuario y si el campo 'numberMessages' es mayor o igual a 1, lo retorna
        return mCollection.document(idChat);
    }


    //Método que actualiza el estado de escribir en la base de datos
    public Task <Void> updateWriting (String idChat, String idUser) {
        Map<String, Object> map = new HashMap<>();  //Clave, valor
        map.put("writing", idUser);               //Actualizamos 'campo', 'valor'

        return mCollection.document(idChat).update(map);
    }


    //Método que retorna si existe algun chat con el 'Usuario Principal (User1)' + el otro 'Usuario (User2)'
    public Query getChatByUser1AndUser2 (String idUser1, String idUser2) {
        ArrayList<String> ids = new ArrayList<>();
        ids.add(idUser1 + idUser2);
        ids.add(idUser2 + idUser1);

        //Busca la combinacion en 'Chats' del idUser1 + idsUser2
        return mCollection.whereIn("id", ids);
    }

    //Método que obtiene el documento 'Chat' que se actualiza
    public void updateNumberMessage (final String idChat){

        //Consulta al documento que contiene ese id del chat
        mCollection.document(idChat).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //Si el documento existe en la bd
                if(documentSnapshot.exists()){

                    //Si dentro del documento contiene un campo llamado 'numberMessages'
                    if(documentSnapshot.contains("numberMessages")){

                        //Obtiene el valor del campo + 1
                        long numberMessages = documentSnapshot.getLong("numberMessages") + 1;
                        Map<String, Object> map = new HashMap<>();
                        map.put("numberMessages", numberMessages);      //Campo , valor que queremos actualizar
                        mCollection.document(idChat).update(map);

                    //Si no existe un campo llamado 'numberMessages'
                    } else {
                        //Se crea el primer mensaje
                        Map<String, Object> map = new HashMap<>();
                        map.put("numberMessages", 1);      //Campo , valor que queremos actualizar
                        mCollection.document(idChat).update(map);
                    }
                }
            }
        });
    }


}
