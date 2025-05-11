package entitiesAmineM;

public class User {
    private int id;
    private String name;
    private String email;
    private String password;
    private String userType;
    private String phone;
    private String address;
    private String passport;
    private byte[] profilePhoto;

    public User(int id, String name, String email, String password, String userType, String phone, String address, String passport, Object o) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.phone = phone;
        this.address = address;
        this.passport = passport;
        this.profilePhoto = (byte[]) o;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassport() {
        return passport;
    }

    public void setPassport(String passport) {
        this.passport = passport;
    }

    public byte[] getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(byte[] profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public User getUser() {
        return null;
    }
}