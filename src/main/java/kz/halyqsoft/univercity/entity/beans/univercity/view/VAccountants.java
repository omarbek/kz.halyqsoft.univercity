package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.CONTRACT_PAYMENT_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LEVEL;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDENT_DIPLOMA_TYPE;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

public final class VAccountants extends AbstractEntity {

    @FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    private String diplomaType;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 3)
    private String level;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 4)
    private String contractPaymentType;

    @FieldInfo(type = EFieldType.TEXT, order = 5)
    private double price;

    @FieldInfo(type = EFieldType.TEXT, order = 6)
    private String priceInLetters;

    public VAccountants() {
    }

    public String getDiplomaType() {
        return diplomaType;
    }

    public void setDiplomaType(String diplomaType) {
        this.diplomaType = diplomaType;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getContractPaymentType() {
        return contractPaymentType;
    }

    public void setContractPaymentType(String contractPaymentType) {
        this.contractPaymentType = contractPaymentType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPriceInLetters() {
        return priceInLetters;
    }

    public void setPriceInLetters(String priceInLetters) {
        this.priceInLetters = priceInLetters;
    }
}
