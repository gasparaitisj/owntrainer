package com.gasparaiciukas.owntrainer.network;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseIngredient {

    @SerializedName("parsed")
    @Expose
    private List<Parsed> parsed = null;

    public List<Parsed> getParsed() {
        return parsed;
    }

    public void setParsed(List<Parsed> parsed) {
        this.parsed = parsed;
    }

}