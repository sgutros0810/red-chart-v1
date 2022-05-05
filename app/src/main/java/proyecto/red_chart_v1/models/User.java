package proyecto.red_chart_v1.models;

public class User {

    private String id;          // Id del usuario
    private String username;    // Nombre del usuario
    private String phone;       // Teléfono del usuario
    private String image;       // Imagen de perfil del usuario

    // Contructor vacío
    public User() {

    }

    // Contructor con 4 parámetros
    public User(String id, String username, String phone, String image) {
        this.id = id;
        this.username = username;
        this.phone = phone;
        this.image = image;
    }

    //Getters y setters
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
}
