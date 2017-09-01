package com.cafesuspenso.ufcg.cafesuspenso.Model;

public class QRCodeData {
    private Long id;
    private String placename;
    private Integer numberProduct;
    private String imagem;

    public QRCodeData(Long id, String nome, Integer numCoffees, String imagem) {
        this.id = id;
        this.placename = nome;
        this.numberProduct = numCoffees;
        this.imagem = imagem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return placename;
    }

    public void setName(String name) {
        this.placename = name;
    }

    public Integer getNumCoffees() {
        return numberProduct;
    }

    public void setNumCoffees(Integer numCoffees) {
        this.numberProduct = numCoffees;
    }

    public String getPlaceImg() {
        return imagem;
    }

    public void setPlaceImg(String placeImg) {
        this.imagem = placeImg;
    }
}
