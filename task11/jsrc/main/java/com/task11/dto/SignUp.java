package com.task11.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.json.JSONObject;

@AllArgsConstructor

@Data
public class SignUp {
    private String firstName;
    private String lastName;
    private String email;
    private String password;


    public SignUp() {
        if (firstName == null || lastName == null || email == null || password == null) {
            throw new IllegalArgumentException("Incomplete data");
        }
    }

    public static SignUp singUpFromJson(String jsonString) {
        JSONObject json = new JSONObject(jsonString);
        String firstName = json.optString("firstName", null);
        String lastName = json.optString("lastName", null);
        String email = json.optString("email", null);
        String password = json.optString("password", null);
        return new SignUp(firstName, lastName, email, password);
    }

}
