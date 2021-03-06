package proyecto.red_chart_v1.models;

import java.util.ArrayList;

public class Message {

    private String id;
    private String idSender;                //Id del usuario que envia el mensaje
    private String idReceiver;              //Id del usuario que recibe el mensaje
    private String idChat;                  //Id del chat
    private String message;                 //mensaje
    private String status;                  //Para saber si el mensaje se ha visto o no (Estado del mensaje)
    private String url;                     //Obtiene las url de las imagenes/archivos
    private String type;                    //Tipo texto|imagen|documento
    private String fileName;                //Nombre del documento
    private String username;                //Nombre del usuario
    private ArrayList<String> receivers;    //id de los usarios que reciben el mensaje
    private long timestamp;                 //Fecha de cuando se envió el mensaje


    //Constructor vacio
    public Message() {
    }

    //Contructor con todas las variables
    public Message(String id, String idSender, String idReceiver, String idChat, String message, String status, String url, String type, String fileName, String username, ArrayList<String> receivers, long timestamp) {
        this.id = id;
        this.idSender = idSender;
        this.idReceiver = idReceiver;
        this.idChat = idChat;
        this.message = message;
        this.status = status;
        this.url = url;
        this.type = type;
        this.fileName = fileName;
        this.username = username;
        this.receivers = receivers;
        this.timestamp = timestamp;
    }

    //Getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdSender() {
        return idSender;
    }

    public void setIdSender(String idSender) {
        this.idSender = idSender;
    }

    public String getIdReceiver() {
        return idReceiver;
    }

    public void setIdReceiver(String idReceiver) {
        this.idReceiver = idReceiver;
    }

    public String getIdChat() {
        return idChat;
    }

    public void setIdChat(String idChat) {
        this.idChat = idChat;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<String> getReceivers() {
        return receivers;
    }

    public void setReceivers(ArrayList<String> receivers) {
        this.receivers = receivers;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "idChat='" + idChat + '\'' +
                ", message='" + message + '\'' +
                ", url='" + url + '\'' +
                '}';
    }
}