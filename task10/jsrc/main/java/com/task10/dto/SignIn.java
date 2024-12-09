package com.task10.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.json.JSONObject;

@Data
@AllArgsConstructor
public class SignIn {
    private String email;
    private String password;

    public SignIn() {
        if (email == null || password == null) {
            throw new IllegalArgumentException("Incomplete data");
        }
    }

    public static SignIn signInFromJson(String jsonString) {
        JSONObject json = new JSONObject(jsonString);
        String email = json.optString("email", null);
        String password = json.optString("password", null);
        return new SignIn(email, password);
    }


}
