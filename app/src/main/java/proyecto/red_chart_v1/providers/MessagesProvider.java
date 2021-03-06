package proyecto.red_chart_v1.providers;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import proyecto.red_chart_v1.models.Message;

//Clase que maneja los datos de Firebase de la coleccion de 'Messages'
public class MessagesProvider {

    CollectionReference mCollection;

    public MessagesProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("Messages");
    }


    //Crea la coleccion, con id único
    public Task<Void> create (Message message) {
        //Retorna el id del usuario
        DocumentReference document = mCollection.document();
        message.setId(document.getId());                        //Id de la colecion del documento 'Message'
        return document.set(message);
    }


    //Metodo retorna el resultado de una consulta, creo en Firestore Database un índice (doble consulta)
    public Query getMessagesByChat(String idChat){
        //Retorna los mensajes por el id del chat y los ordena de forma ascendente por el campo 'timestamp'
        return mCollection
                .whereEqualTo("idChat", idChat)
                .orderBy("timestamp", Query.Direction.ASCENDING);
    }

    //Metodo retorna el id del mensaje
    public DocumentReference getMessagesById(String idMessage){
        //Retorna los mensajes por el id del chat y los ordena de forma ascendente por el campo 'timestamp'
        return mCollection
                .document(idMessage);
    }

    //Método que obtiene los ultimos mensajes por chat y enviados o recibidos
    public Query getLastMessagesByChatAndSender(String idChat, String idSender) {
        ArrayList<String> status = new ArrayList<>();                       //lista que contiene string de los estados de mensaje
        status.add("ENVIADO");                                              //Añado el estado 'ENVIADO'
        status.add("RECIBIDO");                                             //Añado el estado 'RECIBIDO'

        return mCollection
                .whereEqualTo("idChat", idChat)
                .whereEqualTo("idSender", idSender)
                .whereIn("status", status)                            //Estado del mensaje que sea 'ENVIADO' o 'RECIBIDO'
                .orderBy("timestamp", Query.Direction.DESCENDING)     //Ordenar los mensajes de orden descendente -> ultimos mensajes
                .limit(5);                                                 //Maximo 5 resultados
    }


    //Método que actualiza en el id del Mensaje el campo 'status'
    public Task<Void> updateStatus (String idMessage, String status) {
        Map<String, Object> map = new HashMap<>();
        map.put("status", status);                      //Actualiza el campo de la bd "status" a la variable status

        //Retorna con el id del mensaje el estado
        return mCollection
                .document(idMessage)
                .update(map);
    }



    //Método retorna el resultado de la consulta para Saber qué mensajes actualizar, creo en Firestore Database un índice (doble consulta)
    public Query getMessagesByNotRead(String idChat) {
        //Retorna los mensajes de un chat, donde el campo 'status' sea 'ENVIADO' o 'RECIBIDO'
        ArrayList<String> status = new ArrayList<>();          //lista que contiene string de los estados de mensaje
        status.add("ENVIADO");                                 //Añado el estado 'ENVIADO'
        status.add("RECIBIDO");                                //Añado el estado 'RECIBIDO'

        return  mCollection
                .whereEqualTo("idChat", idChat)
                .whereIn("status", status);
    }




    //Método retorna el resultado de la consulta para mostrar los mensajes no leidos, creo en Firestore Database un índice (doble consulta)
    public Query getReceiverMessagesNotRead(String idChat, String idReceiver) {
        //Retorna los mensajes de un chat, donde el campo 'status' sea 'ENVIADO' o 'RECIBIDO' return  mCollection.whereEqualTo("idChat", idChat).whereEqualTo("status", "ENVIADO").whereEqualTo("idReceiver", idReceiver);
        ArrayList<String> status = new ArrayList<>();          //lista que contiene string de los estados de mensaje
        status.add("ENVIADO");                                 //Añado el estado 'ENVIADO'
        status.add("RECIBIDO");                                //Añado el estado 'RECIBIDO'

        return mCollection
                .whereEqualTo("idChat", idChat)
                .whereIn("status", status)
                .whereEqualTo("idReceiver", idReceiver);
    }




    //Metodo retorna el resultado de una consulta, que devuelve el ultimo mensaje del chat. Creo en Firestore Database un índice (doble consulta)
    public Query getLastMessages(String idChat) {
        return  mCollection
                .whereEqualTo("idChat", idChat)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(1);
    }

}

