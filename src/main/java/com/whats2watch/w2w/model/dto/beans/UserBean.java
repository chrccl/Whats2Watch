package com.whats2watch.w2w.model.dto.beans;

import com.whats2watch.w2w.model.Gender;

public class UserBean {

    private String name;

    private String surname;

    private Gender gender;

    private String email;

    private String password;

    public UserBean(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public UserBean(String name, String surname, Gender gender, String email, String password) {
        this.name = name;
        this.surname = surname;
        this.gender = gender;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
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

}
