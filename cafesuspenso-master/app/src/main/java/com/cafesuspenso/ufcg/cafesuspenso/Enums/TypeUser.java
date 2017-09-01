package com.cafesuspenso.ufcg.cafesuspenso.Enums;

/**
 * Created by Lucas on 16/06/2017.
 */

public enum TypeUser {
    ADMIN("ADMIN"), CAFETERIA("CAFETERIA");

    private String type;

    TypeUser(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
