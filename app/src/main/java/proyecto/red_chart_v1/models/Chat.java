package proyecto.red_chart_v1.models;

import java.util.ArrayList;

public class Chat {
    private String id;
    private long timestamp;         //Fecha exacta de cuando se creó el chat
    private ArrayList<String> ids;  //Almacena los ids de los usuarios de los contactos
    private int numberMessages;     //Nº de mensajes en el chat

    //Constructor vacío
    public Chat() {
    }

    //Constructor con las variables
    public Chat(String id, long timestamp, ArrayList<String> ids, int numberMessages) {
        this.id = id;
        this.timestamp = timestamp;
        this.ids = ids;
        this.numberMessages = numberMessages;
    }

    //Getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public ArrayList<String> getIds() {
        return ids;
    }

    public void setIds(ArrayList<String> ids) {
        this.ids = ids;
    }

    public int getNumberMessages() {
        return numberMessages;
    }

    public void setNumberMessages(int numberMessages) {
        this.numberMessages = numberMessages;
    }
}
