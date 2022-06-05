package proyecto.red_chart_v1.models;

import java.util.ArrayList;

public class Chat {
    private String id;
    private long timestamp;         //Fecha exacta de cuando se creó el chat
    private ArrayList<String> ids;  //Almacena los ids de los usuarios de los contactos

    //Constructor vacío
    public Chat() {
    }

    //Constructor con las variables
    public Chat(String id, long timestamp, ArrayList<String> ids) {
        this.id = id;
        this.timestamp = timestamp;
        this.ids = ids;
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


}
