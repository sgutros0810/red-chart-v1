package proyecto.red_chart_v1.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import proyecto.red_chart_v1.models.Message;

//Clase que maneja los datos de Firebase de la coleccion de 'Messages'
public class MessagesProvider {

    CollectionReference mCollection;

    public MessagesProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("Messages");
    }

    //Crea la colecion, con id único
    public Task<Void> create (Message message) {
        //Retorna el id del usuario
        DocumentReference document = mCollection.document();
        message.setId(document.getId());                        //Id de la colecion del documento 'Message'
        return document.set(message);
    }

    //Metodo retorna el resultado de una consulta, creo en Firestore Database un índice (doble consulta)
    public Query getMessagesByChat(String idChat){
        //Retorna los mensajes por el id del chat y los ordena de forma ascendente por el campo 'timestamp'
        return mCollection.whereEqualTo("idChat", idChat).orderBy("timestamp", Query.Direction.ASCENDING);
    }
}

