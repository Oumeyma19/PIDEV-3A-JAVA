package models;

public class User {
    private int id;
    private String firstname;
    private String lastname;
    private String email;
    private String phone;
    private String password;
    private int pointsfid;
    private String nivfid;
    private Boolean statusGuide;
    private UserRole roles; // Use UserRole enum instead of Type
    private Boolean is_active;
    private Boolean is_banned;

    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Modif user
    public User(int id, String firstname, String lastname, String email, String phone, String password, UserRole roles, boolean is_banned, boolean is_active) {
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

    // Add User
    public User(String firstname, String lastname, String email, String phone, String password, UserRole roles) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.roles = roles;
        this.is_banned = false;
        this.is_active = true;
    }

    public User(int id, String firstname, String lastname, String email, String phone, String password, UserRole roles) {
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

    // Add Client
    public User(int id, String firstname, String lastname, String email, String phone, String password, int pointsfid, String nivfid, UserRole roles) {
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

    public User(String firstname, String lastname, String email, String phone, String password, int pointsfid, String nivfid, UserRole roles) {
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

    // Modif Client
    public User(int id, String firstname, String lastname, String email, String phone, String password, int pointsfid, String nivfid, UserRole roles, Boolean is_banned, Boolean is_active) {
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

    // Add Guide
    public User(String firstname, String lastname, String email, String phone, String password, Boolean statusGuide, UserRole roles) {
        this.firstname = firstname;
        this.password = password;
        this.phone = phone;
        this.lastname = lastname;
        this.email = email;
        this.statusGuide = true;
        this.roles = roles;
        this.is_banned = false;
        this.is_active = true;
    }

    public User(int id, String firstname, String email) {
        this.id = id;
        this.firstname = firstname;
        this.email = email;
    }

    public User(int id) {
        this.id = id;
    }

    public User(int id, String firstname, String lastname, String email, String phone, String password, Boolean statusGuide, UserRole roles) {
        this.id = id;
        this.firstname = firstname;
        this.password = password;
        this.phone = phone;
        this.lastname = lastname;
        this.email = email;
        this.statusGuide = true;
        this.roles = roles;
        this.is_banned = false;
        this.is_active = true;
    }

    // Modif Guide
    public User(int id, String firstname, String lastname, String email, String phone, String password, Boolean statusGuide, UserRole roles, Boolean is_banned, Boolean is_active) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.statusGuide = statusGuide;
        this.roles = roles;
        this.is_banned = is_banned;
        this.is_active = is_active;
    }

    // Getters and Setters
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

    public UserRole getRoles() {
        return roles;
    }

    public void setRoles(UserRole roles) {
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

    public String getNivfid() {
        return nivfid;
    }

    public void setNivfid(String nivfid) {
        this.nivfid = nivfid;
    }

    public Boolean getStatusGuide() {
        return statusGuide;
    }

    public void setStatusGuide(Boolean statusGuide) {
        this.statusGuide = statusGuide;
    }

    @Override
    public String toString() {
        if (roles == UserRole.CLIENT) {
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
                    ", is_active=" + is_active +
                    "}\n";
        } else if (roles == UserRole.GUIDE) {
            return "User{" +
                    "id=" + id +
                    ", firstname='" + firstname + '\'' +
                    ", lastname='" + lastname + '\'' +
                    ", email='" + email + '\'' +
                    ", phone='" + phone + '\'' +
                    ", password='" + password + '\'' +
                    ", statusGuide=" + statusGuide +
                    ", roles=" + roles +
                    ", is_banned=" + is_banned +
                    ", is_active=" + is_active +
                    "}\n";
        } else { // ADMIN or other roles
            return "User{" +
                    "id=" + id +
                    ", firstname='" + firstname + '\'' +
                    ", lastname='" + lastname + '\'' +
                    ", email='" + email + '\'' +
                    ", phone='" + phone + '\'' +
                    ", password='" + password + '\'' +
                    ", roles=" + roles +
                    ", is_banned=" + is_banned +
                    ", is_active=" + is_active +
                    "}\n";
        }
    }
}