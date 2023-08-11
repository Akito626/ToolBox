package com.alha_app.toolbox.entities;

public class Tool {
    private int id;
    private String name;
    private String kana;
    private int image;
    private int count;
    private boolean isFavorite;

    public Tool(){
    }

    public Tool(int id, String name, String kana, int image){
        this.id = id;
        this.name = name;
        this.kana = kana;
        this.image = image;
    }

    // Getter
    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }

    public String getKana() {
        return kana;
    }

    public int getImage() {
        return image;
    }
    public int getCount() {
        return count;
    }

    // Setter
    public void setId(int id) {
        this.id = id;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setKana(String kana) {
        this.kana = kana;
    }

    public void setImage(int image) {
        this.image = image;
    }
    public void setCount(int count) {
        this.count = count;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public void addCount(){
        this.count++;
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof Tool)) return false;
        Tool tool = (Tool) o;

        return this.name.equals(tool.getName());
    }

    @Override
    public int hashCode(){
        int result = 17;
        result = 31 * result + id;
        result = 31 * result + image;

        return result;
    }
}
