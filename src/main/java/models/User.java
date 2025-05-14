package models;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import util.Type;

public class User {
    private int id;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String password;
    private int pointsfid;
    private int nivfid;
    private Boolean status_guide;
    private Type roles;
    private Boolean is_active;
    private Boolean is_banned;



    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
    //Modif user
    public User(int id, String firstname, String lastname, String email, String phone, String password, Type roles, boolean is_banned ,boolean is_active) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.roles = roles;
        this.is_banned = is_banned;
        this.is_active = is_active;
    }
    //add User
    public User(String firstname, String lastname, String email, String phone, String password, Type roles) {

        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.roles = roles;
        this.is_banned = false;
        this.is_active = true;

    }
    public User(int id,String firstname, String lastname, String email, String phone, String password, Type roles) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.roles = roles;
        this.is_banned = false;
        this.is_active = true;

    }
    //add Client
    public User(int id,String firstname, String lastname, String email, String phone, String password, int pointsfid, int nivfid, Type roles) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.pointsfid = pointsfid;
        this.nivfid = nivfid;
        this.roles = roles;
        this.is_banned = false;
        this.is_active = true;
    }

    public User(String firstname, String lastname, String email, String phone, String password, int pointsfid, int nivfid, Type roles) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.pointsfid = pointsfid;
        this.nivfid = nivfid;
        this.roles = roles;
        this.is_banned = false;
        this.is_active = true;
    }
    public User(String firstname, String lastname, String email, String phone, int pointsfid, int nivfid, Boolean is_banned,Boolean is_active) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.pointsfid = pointsfid;
        this.nivfid = nivfid;
        this.is_banned = is_banned;
        this.is_active = is_active;
    }
    //Modif Client
    public User(int id, String firstname, String lastname, String email, String phone, String password, int pointsfid, int nivfid, Type roles, Boolean is_banned,Boolean is_active) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.pointsfid = pointsfid;
        this.nivfid = nivfid;
        this.roles = roles;
        this.is_banned = is_banned;
        this.is_active = is_active;
    }
    //add Guide
    public User(int id, String firstname, String lastname, String email, String phone, Boolean status_guide, Type roles) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.status_guide = status_guide;
        this.roles = roles;
        this.is_banned = false;
        this.is_active = true;
    }
    public User( String lastname,String firstname, String email, String phone,String password, Boolean status_guide , Boolean is_banned,Boolean is_active) {
        this.lastname = lastname;
        this.firstname = firstname;

        this.email = email;
        this.phone = phone;
        this.password = password;

        this.status_guide = status_guide;
        this.is_banned = is_banned;
        this.is_active = is_active;
    }


    public User(int idClient) {
        this.id = idClient;
    }

    public User(String clientName) {
    }

    public String getstatus_guideDisplay() {
        return status_guide ? "Disponible" : "Indisponible";
    }

    public void setstatus_guideDisplay(String status) {
        this.status_guide = "Disponible".equals(status);
    }

    public String getIsActiveDisplay() {
        return is_active ? "Active" : "Inactive";
    }

    public void setIsActiveDisplay(String active) {
        this.is_active = "Active".equals(active);
    }

    public String getIsBannedDisplay() {
        return is_banned ? "Bannissement" : "Non Bannissement";
    }

    public void setIsBannedDisplay(String banned) {
        this.is_banned = "Bannissement".equals(banned);
    }
    //Modif Guide
    public User(int id, String firstname, String lastname, String email, String phone, String password, Boolean status_guide, Type roles, Boolean is_banned,Boolean is_active) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.status_guide = status_guide;
        this.roles = roles;
        this.is_banned = is_banned;
        this.is_active = is_active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Type getRoles() {
        return roles;
    }

    public void setRoles(Type roles) {
        this.roles = roles;
    }

    public Boolean getIsActive() {
        return is_active;
    }

    public void setIsActive(Boolean is_active) {
        this.is_active = is_active;
    }

    public Boolean getIsBanned() {
        return is_banned;
    }

    public void setIsBanned(Boolean is_banned) {
        this.is_banned = is_banned;
    }

    public int getPointsfid() {
        return pointsfid;
    }

    public void setPointsfid(int pointsfid) {
        this.pointsfid = pointsfid;
    }

    public int getNivfid() {
        return nivfid;
    }

    public void setNivfid(int nivfid) {
        this.nivfid = nivfid;
    }

    public Boolean getstatus_guide() {
        return status_guide;
    }

    public void setstatus_guide(Boolean status_guide) {
        this.status_guide = status_guide;
    }

    @Override
    public String toString() {
        if (roles == Type.CLIENT) {
            return "User{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", pointsfid=" + pointsfid +
                ", nivfid='" + nivfid + '\'' +
                ", roles=" + roles +
                ", is_banned=" + is_banned +
                ", is_active=" + is_active +// Ajout de l'attribut
                "}\n";
        } else if (roles == Type.GUIDE) {
            return "User{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", status_guide='" + status_guide + '\'' +
                ", roles=" + roles +
                ", is_banned=" + is_banned +
                ", is_active=" + is_active +// Ajout de l'attribut
                "}\n";
        } else { // ADMIN ou autres rôles
            return "User{" +
                "id=" + id +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", password='" + password + '\'' +
                ", roles=" + roles +
                ", is_banned=" + is_banned +
                ", is_active=" + is_active +// Ajout de l'attribut
                "}\n";
        }
    }



}
