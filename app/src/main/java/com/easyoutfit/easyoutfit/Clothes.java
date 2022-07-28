package com.easyoutfit.easyoutfit;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Diogo on 29/01/2018.
 */

@Entity
class Clothes {

    static final int TYPE_SHIRT = 1;
    static final int TYPE_PANTS = 2;
    static final int TYPE_SKIRT = 3;
    static final int TYPE_SHORTS = 4;
    static final int TYPE_SHOES = 5;
    static final int TYPE_COAT = 6;
    static final int TYPE_JACKET = 7;
    static final int TYPE_DRESS = 8;
    static final int TYPE_BELT = 9;
    static final int TYPE_HANDBAG = 10;
    static final int TYPE_HAT = 11;
    static final int TYPE_ACCESSORY = 12;

    static final int CATEGORY_CASUAL = 1;
    static final int CATEGORY_WORK = 2;
    static final int CATEGORY_SPORT = 3;
    static final int CATEGORY_NIGHT = 4;
    static final int CATEGORY_PARTY = 5;




    @PrimaryKey(autoGenerate = true)
    private int id;
    private String imagePath;
    private int type;
    private int primary_color;
    private int secondary_color_1;
    private int secondary_color_2;
    private boolean seasonWinter;
    private boolean seasonSummer;
    private boolean seasonFall;
    private boolean seasonSpring;
    private int category;
    private boolean wishlist;
    private String brand;
    private float price;
    private boolean noSeason;
    private String thumbnailPath;

    Clothes(String imagePath, String thumbnailPath, int type, int primary_color, int secondary_color_1, int secondary_color_2, boolean seasonWinter, boolean seasonSummer, boolean seasonFall, boolean seasonSpring, int category, boolean wishlist, String brand, float price) {
        this.imagePath = imagePath;
        this.type = type;
        this.primary_color = primary_color;
        this.secondary_color_1 = secondary_color_1;
        this.secondary_color_2 = secondary_color_2;
        this.seasonWinter = seasonWinter;
        this.seasonSummer = seasonSummer;
        this.seasonFall = seasonFall;
        this.seasonSpring = seasonSpring;
        this.category = category;
        this.wishlist = wishlist;
        this.brand = brand;
        this.price = price;
        this.thumbnailPath = thumbnailPath;

        this.noSeason = !seasonWinter && !seasonFall && !seasonSpring && !seasonSummer;
    }

    String getThumbnailPath() {
        return thumbnailPath;
    }

    void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    String getBrand() {
        return brand;
    }

    void setBrand(String brand) {
        this.brand = brand;
    }

    float getPrice() {
        return price;
    }

    void setPrice(float price) {
        this.price = price;
    }



    boolean isNoSeason() {
        return noSeason;
    }

    void setNoSeason(boolean noSeason) {
        this.noSeason = noSeason;
    }

    boolean isSeasonWinter() {
        return seasonWinter;
    }

    void setSeasonWinter(boolean seasonWinter) {
        this.seasonWinter = seasonWinter;
    }

    boolean isSeasonSummer() {
        return seasonSummer;
    }

    void setSeasonSummer(boolean seasonSummer) {
        this.seasonSummer = seasonSummer;
    }

    boolean isSeasonFall() {
        return seasonFall;
    }

    void setSeasonFall(boolean seasonFall) {
        this.seasonFall = seasonFall;
    }

    boolean isSeasonSpring() {
        return seasonSpring;
    }

    void setSeasonSpring(boolean seasonSpring) {
        this.seasonSpring = seasonSpring;
    }



    String getImagePath() {
        return imagePath;
    }

    void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    int getType() {
        return type;
    }

    void setType(int type) {
        this.type = type;
    }

    int getPrimary_color() {
        return primary_color;
    }

    void setPrimary_color(int primary_color) {
        this.primary_color = primary_color;
    }

    int getSecondary_color_1() {
        return secondary_color_1;
    }

    int getSecondary_color_2() {
        return secondary_color_2;
    }

    void setSecondary_color_1(int secondary_color_1) {
        this.secondary_color_1 = secondary_color_1;
    }

    void setSecondary_color_2(int secondary_color_2) {
        this.secondary_color_2 = secondary_color_2;
    }


    int getCategory() {
        return category;
    }

    void setCategory(int category) {
        this.category = category;
    }

    boolean isWishlist() {
        return wishlist;
    }

    void setWishlist(boolean wishlist) {
        this.wishlist = wishlist;
    }

    int getId() {
        return id;
    }

    void setId(int id) {
        this.id = id;
    }


}
