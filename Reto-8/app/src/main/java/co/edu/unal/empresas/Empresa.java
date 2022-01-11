package co.edu.unal.empresas;

public class Empresa {
    private Integer ID;
    private String name;
    private String url;
    private String phone;
    private String email;
    private String services;
    private String classification;


    public Empresa(Integer ID, String name, String url, String phone, String email, String services, String classification) {
        this.ID = ID;
        this.name = name;
        this.url = url;
        this.phone = phone;
        this.email = email;
        this.services = services;
        this.classification = classification;
    }

    public Integer getID() {
        return ID;
    }

    public void setID(Integer ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }
}
