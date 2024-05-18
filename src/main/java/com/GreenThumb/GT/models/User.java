package com.GreenThumb.GT.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Builder
@Entity
@NoArgsConstructor

public class User {


    @Id
    @NotBlank
    private String email;

    @NotBlank

    private String password;




    @NotBlank

    private String firstName;

    @NotBlank

    private String lastName;


    @NotBlank

    private String phoneNumber;



    @NotBlank

    private String alternatePhoneNumber;


    @NotBlank

    private String universityMajor;


    @NotBlank

    private String workPlace;



    private int age;




    private int userType;


    public User(String email, String password, String firstName, String lastName, String phoneNumber, String alternatePhoneNumber, String universityMajor, String workPlace, int age, int userType) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.alternatePhoneNumber = alternatePhoneNumber;
        this.universityMajor = universityMajor;
        this.workPlace = workPlace;
        this.age = age;
        this.userType = userType;
    }

}
