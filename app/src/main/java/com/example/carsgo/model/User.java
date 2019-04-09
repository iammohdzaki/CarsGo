package com.example.carsgo.model;

public class User {

    String firstName,lastName,email,mobileNumber,homeAddress;

    public User(){}

    public User(String firstName, String lastName, String email, String mobileNumber, String homeAddress) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.homeAddress = homeAddress;
    }


    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

}
