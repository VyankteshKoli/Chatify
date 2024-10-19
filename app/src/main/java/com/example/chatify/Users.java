package com.example.chatify;

import com.google.android.material.textfield.TextInputEditText;

public class Users {

    String uid;
    String name;
    String dob;
    String phone;
    String gender;
    String email;
    String password;
    String status;
    String imageUri;

    // No-argument constructor
    public Users() {


    }

    public Users(String uid, String name, String dob, String gender, String phone, String email, String password, String status, String imageUri) {
        this.uid = uid;
        this.name = name;
        this.dob = dob;
        this.phone = phone;
        this.gender = gender;
        this.email = email;
        this.password = password;
        this.status = status;
        this.imageUri = imageUri;
    }

    // Getters and setters

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }
}
