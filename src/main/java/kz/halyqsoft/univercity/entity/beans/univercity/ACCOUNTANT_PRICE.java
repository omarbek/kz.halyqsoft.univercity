package kz.halyqsoft.univercity.entity.beans.univercity;

import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LEVEL;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDENT_DIPLOMA_TYPE;
import kz.halyqsoft.univercity.modules.accountant.AccountantView;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;
import org.r3a.common.vaadin.widget.form.FormModel;

import javax.persistence.*;

@Entity
public class ACCOUNTANT_PRICE extends AbstractEntity {

    @FieldInfo(type = EFieldType.FK_COMBO, order = 1, inGrid = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "STUDENT_DIPLOMA_TYPE_ID", referencedColumnName = "ID")
    })
    private STUDENT_DIPLOMA_TYPE diplomaType;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 2, inGrid = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "LEVEL_ID", referencedColumnName = "ID")
    })
    private LEVEL level;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 3, inGrid = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "CONTRACT_PAYMENT_TYPE_ID", referencedColumnName = "ID")
    })
    private CONTRACT_PAYMENT_TYPE contractPaymentType;

    @FieldInfo(type = EFieldType.DOUBLE, order = 4)
    @Column(name = "PRICE", nullable = false)
    private double price;

    @FieldInfo(type = EFieldType.TEXT, order = 5, inGrid = false)
    @Column(name = "PRICE_IN_LETTERS", nullable = false)
    private String priceInLetters;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 6, required = false, inEdit = false, inGrid = false, inView = false )
    @Column(name = "DELETED", nullable = false)
    private boolean deleted;

    public ACCOUNTANT_PRICE() {
    }

    public STUDENT_DIPLOMA_TYPE getDiplomaType() {
        return diplomaType;
    }

    public void setDiplomaType(STUDENT_DIPLOMA_TYPE diplomaType) {
        this.diplomaType = diplomaType;
    }

    public LEVEL getLevel() {
        return level;
    }

    public void setLevel(LEVEL level) {
        this.level = level;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public CONTRACT_PAYMENT_TYPE getContractPaymentType() {
        return contractPaymentType;
    }

    public void setContractPaymentType(CONTRACT_PAYMENT_TYPE contractPaymentType) {
        this.contractPaymentType = contractPaymentType;
    }
}
