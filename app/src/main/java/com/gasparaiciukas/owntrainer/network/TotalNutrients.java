package com.gasparaiciukas.owntrainer.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TotalNutrients {

    @SerializedName("ENERC_KCAL")
    @Expose
    private Nutrient eNERCKCAL;
    @SerializedName("FAT")
    @Expose
    private Nutrient fAT;
    @SerializedName("CHOCDF")
    @Expose
    private Nutrient cHOCDF;
    @SerializedName("SUGAR")
    @Expose
    private Nutrient sUGAR;
    @SerializedName("PROCNT")
    @Expose
    private Nutrient pROCNT;

    public Nutrient geteNERCKCAL() {
        return eNERCKCAL;
    }

    public void seteNERCKCAL(Nutrient eNERCKCAL) {
        this.eNERCKCAL = eNERCKCAL;
    }

    public Nutrient getfAT() {
        return fAT;
    }

    public void setfAT(Nutrient fAT) {
        this.fAT = fAT;
    }

    public Nutrient getcHOCDF() {
        return cHOCDF;
    }

    public void setcHOCDF(Nutrient cHOCDF) {
        this.cHOCDF = cHOCDF;
    }

    public Nutrient getsUGAR() {
        return sUGAR;
    }

    public void setsUGAR(Nutrient sUGAR) {
        this.sUGAR = sUGAR;
    }

    public Nutrient getpROCNT() {
        return pROCNT;
    }

    public void setpROCNT(Nutrient pROCNT) {
        this.pROCNT = pROCNT;
    }
}