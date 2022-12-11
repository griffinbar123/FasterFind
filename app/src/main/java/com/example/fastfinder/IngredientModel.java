package com.example.fastfinder;

public class IngredientModel {
    public String email;
    public int id;

    public IngredientModel(String email, int id) {
        this.email=email;
        this.id=id;
    }

    public String getEmail() {
        return email;
    }

    public int getId() {
        return id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setId(int id) {
        this.id = id;
    }
}
