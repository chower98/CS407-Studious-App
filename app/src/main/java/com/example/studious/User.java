package com.example.studious;

public class User {
    private String name;
    private String email;
    private String password;
    private String phone;

    public User(String name, String email, String password, String phone) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    public String getName() { return name; }

    public String getEmail() { return email; }

    public String getPassword() { return password; }

    public String getPhone() { return phone; }

    public void setName(String newName) { name = newName; }

    public void setEmail(String newEmail) { email = newEmail; }

    public void setPassword(String newPass) { password = newPass; }

    public void setPhone(String newPhone) { phone = newPhone; }

}
