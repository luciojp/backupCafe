package com.cafesuspenso.ufcg.cafesuspenso.Model;

import com.cafesuspenso.ufcg.cafesuspenso.Enums.TypeUser;

/**
 * Created by Lucas on 16/06/2017.
 */

public class User {
    private Long id;
    private String name;
    private String email;
    private String password;
    private TypeUser typeUser;

    public User(String name, String email, String password, TypeUser typeUser) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.typeUser = typeUser;
    }

    public User() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public TypeUser getTypeUser() {
        return typeUser;
    }

    public void setTypeUser(TypeUser typeUser) {
        this.typeUser = typeUser;
    }
}
