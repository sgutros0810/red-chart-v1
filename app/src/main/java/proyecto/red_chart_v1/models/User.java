package proyecto.red_chart_v1.models;

public class User {

    private String id;          // Id del usuario
    private String username;    // Nombre del usuario
    private String phone;       // Teléfono del usuario
    private String image;       // Imagen de perfil del usuario
    private String info;        //info del perfil
    private String token;       //token para las notificaciones
    private long lastConnect;   //Última vez que se conecto
    private boolean online;     //Esta 'En linea'

    // Contructor vacío
    public User() {

    }

    // Contructor con todas las variables
    public User(String id, String username, String phone, String image, String info, String token, long lastConnect, boolean online) {
        this.id = id;
        this.username = username;
        this.phone = phone;
        this.image = image;
        this.info = info;
        this.token = token;
        this.lastConnect = lastConnect;
        this.online = online;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getLastConnect() {
        return lastConnect;
    }

    public void setLastConnect(long lastConnect) {
        this.lastConnect = lastConnect;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }
}
