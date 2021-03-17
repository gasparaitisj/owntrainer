package com.gasparaiciukas.owntrainer.network;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostResponse {

    @SerializedName("uri")
    @Expose
    private String uri;
    @SerializedName("calories")
    @Expose
    private int calories;
    @SerializedName("totalWeight")
    @Expose
    private int totalWeight;
    @SerializedName("totalNutrients")
    @Expose
    private TotalNutrients totalNutrients;
    @SerializedName("totalDaily")
    @Expose
    private TotalNutrients totalDaily;
    @SerializedName("ingredients")
    @Expose
    private List<ResponseIngredient> ingredients = null;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public int getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(int totalWeight) {
        this.totalWeight = totalWeight;
    }

    public TotalNutrients getTotalNutrients() {
        return totalNutrients;
    }

    public void setTotalNutrients(TotalNutrients totalNutrients) {
        this.totalNutrients = totalNutrients;
    }

    public TotalNutrients getTotalDaily() {
        return totalDaily;
    }

    public void setTotalDaily(TotalNutrients totalDaily) {
        this.totalDaily = totalDaily;
    }

    public List<ResponseIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<ResponseIngredient> ingredients) {
        this.ingredients = ingredients;
    }

}