package com.gasparaiciukas.owntrainer.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Nutrients {

    @SerializedName("ENERC_KCAL")
    @Expose
    private double eNERCKCAL;
    @SerializedName("CHOCDF")
    @Expose
    private double cHOCDF;
    @SerializedName("FAT")
    @Expose
    private double fAT;
    @SerializedName("PROCNT")
    @Expose
    private double pROCNT;
    @SerializedName("SUGAR")
    @Expose
    private double sUGAR;

    public double getENERCKCAL() {
        return eNERCKCAL;
    }

    public void setENERCKCAL(double eNERCKCAL) {
        this.eNERCKCAL = eNERCKCAL;
    }

    public double getCHOCDF() {
        return cHOCDF;
    }

    public void setCHOCDF(double cHOCDF) {
        this.cHOCDF = cHOCDF;
    }

    public double getFAT() {
        return fAT;
    }

    public void setFAT(double fAT) {
        this.fAT = fAT;
    }

    public double getPROCNT() {
        return pROCNT;
    }

    public void setPROCNT(double pROCNT) {
        this.pROCNT = pROCNT;
    }

    public double getSUGAR() {
        return sUGAR;
    }

    public void setSUGAR(double sUGAR) {
        this.sUGAR = sUGAR;
    }

}