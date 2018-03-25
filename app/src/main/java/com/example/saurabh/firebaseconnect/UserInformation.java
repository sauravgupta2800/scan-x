package com.example.saurabh.firebaseconnect;
public class UserInformation {
    public String FullName;
    public String ContactNumber; //always public in nature
    public String DOB;
    public String Gender;
    public String Height;
    public String Weight;
    public String BloodGroup;
    public String Address;

    public UserInformation(){
    }

    public UserInformation(String fullName, String contactNumber, String DOB, String gender, String height, String weight, String bloodGroup, String address) {
        this.FullName = fullName;
        this.ContactNumber = contactNumber;
        this.DOB = DOB;
        this.Gender = gender;
        this.Height = height;
        this.Weight = weight;
        this.BloodGroup = bloodGroup;
        this.Address = address;
    }
}