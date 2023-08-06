package com.alha_app.toolbox.entities;

public class Tool {
    private int id;
    private String name;
    private int image;

    public Tool(int id, String name, int image){
        this.id = id;
        this.name = name;
        this.image = image;
    }

    // Getter
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public int getImage() {
        return image;
    }

    // Setter
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setImage(int image) {
        this.image = image;
    }
}
