package com.gasparaiciukas.owntrainer.network;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetResponse {

    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("hints")
    @Expose
    private List<Hint> hints = null;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Hint> getHints() {
        return hints;
    }

    public void setHints(List<Hint> hints) {
        this.hints = hints;
    }

}