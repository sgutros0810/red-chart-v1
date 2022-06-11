package proyecto.red_chart_v1.models;

import java.util.ArrayList;

//Clase que tiene la respuesta que retorna el servidor cuando el usuario envía la notificación
public class FCMResponse {


    private long multicast_id;
    private int success;
    private int failure;
    private int canonical_ids;
    ArrayList<Object> results = new ArrayList<>();

    //Constructor con todas las variables
    public FCMResponse(long multicast_id, int success, int failure, int canonical_ids, ArrayList<Object> results) {
        this.multicast_id = multicast_id;
        this.success = success;
        this.failure = failure;
        this.canonical_ids = canonical_ids;
        this.results = results;
    }

    //Getters y setters
    public long getMulticast_id() {
        return multicast_id;
    }

    public void setMulticast_id(long multicast_id) {
        this.multicast_id = multicast_id;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getFailure() {
        return failure;
    }

    public void setFailure(int failure) {
        this.failure = failure;
    }

    public int getCanonical_ids() {
        return canonical_ids;
    }

    public void setCanonical_ids(int canonical_ids) {
        this.canonical_ids = canonical_ids;
    }

    public ArrayList<Object> getResults() {
        return results;
    }

    public void setResults(ArrayList<Object> results) {
        this.results = results;
    }
}
