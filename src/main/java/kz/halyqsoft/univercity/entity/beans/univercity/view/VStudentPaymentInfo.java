package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

public class VStudentPaymentInfo extends AbstractEntity {

    @FieldInfo(type = EFieldType.TEXT, order = 2)
    private String entranceYear;

    @FieldInfo(type = EFieldType.INTEGER, order = 3)
    private Integer studyYear;

    @FieldInfo(type = EFieldType.TEXT, order = 4)
    private String diplomaType;

    @FieldInfo(type = EFieldType.TEXT, order = 5)
    private String speciality;

    @FieldInfo(type = EFieldType.INTEGER, order = 6)
    private Integer debtSum;

    @FieldInfo(type = EFieldType.INTEGER, order = 7)
    private Integer paymentSum;

    public VStudentPaymentInfo() {
    }

    public String getEntranceYear() {
        return entranceYear;
    }

    public void setEntranceYear(String entranceYear) {
        this.entranceYear = entranceYear;
    }

    public Integer getStudyYear() {
        return studyYear;
    }

    public void setStudyYear(Integer studyYear) {
        this.studyYear = studyYear;
    }

    public String getDiplomaType() {
        return diplomaType;
    }

    public void setDiplomaType(String diplomaType) {
        this.diplomaType = diplomaType;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public Integer getDebtSum() {
        return debtSum;
    }

    public void setDebtSum(Integer debtSum) {
        this.debtSum = debtSum;
    }

    public Integer getPaymentSum() {
        return paymentSum;
    }

    public void setPaymentSum(Integer paymentSum) {
        this.paymentSum = paymentSum;
    }
}
