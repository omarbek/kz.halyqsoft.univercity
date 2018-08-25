package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.Date;

@Entity
public class V_STUDENT_DEBTS extends AbstractEntity {
    private static final long serialVersionUID = -2348574322994827920L;

    @FieldInfo(type = EFieldType.TEXT, order = 1)
    @Column(name = "USER_CODE")
    private String code;

    @FieldInfo(type = EFieldType.TEXT, order = 2)
    @Column(name = "FIO")
    private String fio;

    @FieldInfo(type = EFieldType.DOUBLE, order = 3)
    @Column(name = "DEBT_SUM")
    private Double debtSum;

    public V_STUDENT_DEBTS() {
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Double getDebtSum() {
        return debtSum;
    }

    public void setDebtSum(Double debtSum) {
        this.debtSum = debtSum;
    }
}
