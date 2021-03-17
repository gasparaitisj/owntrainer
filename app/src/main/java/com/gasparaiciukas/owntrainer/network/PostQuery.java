package com.gasparaiciukas.owntrainer.network;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostQuery {

    @SerializedName("ingredients")
    @Expose
    private List<Ingredient> ingredients = null;

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

}