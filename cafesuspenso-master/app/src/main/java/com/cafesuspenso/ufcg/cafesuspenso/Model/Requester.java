package com.cafesuspenso.ufcg.cafesuspenso.Model;

import android.location.Location;

/**
 * Created by Lucas on 16/06/2017.
 */
public class Requester {
    private Long id;
    private String username;
    private String email;
    private String password;
    private String placename;
    private String complement;
    private String cnpj;
    private Location location;
    private String imagem;

    public Requester() {}

    public Requester(String username, String email, String password, String placename, String complement, String cnpj, Location location, String imagem) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.placename = placename;
        this.complement = complement;
        this.cnpj = cnpj;
        this.location = location;
        this.imagem = imagem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getPlacename() {
        return placename;
    }

    public void setPlacename(String placename) {
        this.placename = placename;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
    }

    public String getComplement() {
        return complement;
    }

    public void setComplement(String complement) {
        this.complement = complement;
    }
}