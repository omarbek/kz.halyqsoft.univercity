package kz.halyqsoft.univercity.entity.beans.univercity.view;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Dmitry Dobrin.
 * @created 28.04.2017.
 */
public class V_DIPLOMA extends V_EXAM_RESULT {

    private BigDecimal ectsTotal;

    private BigDecimal gpa;

    private String protocolNumber;

    private Date protocolDate;

    private String degree;

    private String speciality;

    private String educationProgram;

    private String qualification;

    private String number;

    private Date issueDate;

    private String regNumber;

    public V_DIPLOMA() {

    }

    public BigDecimal getEctsTotal() {
        if (ectsTotal == null) {
            ectsTotal = new BigDecimal(0);
        }
        return ectsTotal.setScale(0, BigDecimal.ROUND_HALF_UP);
    }

    public void setEctsTotal(BigDecimal ectsTotal) {
        this.ectsTotal = ectsTotal;
    }

    public BigDecimal getGpa() {
        if (gpa == null) {
            gpa = new BigDecimal(0);
        }
        return gpa.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public void setGpa(BigDecimal gpa) {
        this.gpa = gpa;
    }

    public String getProtocolNumber() {
        return protocolNumber;
    }

    public void setProtocolNumber(String protocolNumber) {
        this.protocolNumber = protocolNumber;
    }

    public Date getProtocolDate() {
        return protocolDate;
    }

    public String getProtocolDay() {
        return protocolDate == null ? null : dateFormatOfDay.format(protocolDate);
    }

    public String getProtocolMonth() {
        return protocolDate == null ? null : dateFormatOfMonth.format(protocolDate);
    }

    public String getProtocolYear() {
        return protocolDate == null ? null : dateFormatOfYear.format(protocolDate);
    }

    public void setProtocolDate(Date protocolDate) {
        this.protocolDate = protocolDate;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getSpeciality() {
        return speciality;
    }

    public void setSpeciality(String speciality) {
        this.speciality = speciality;
    }

    public String getEducationProgram() {
        return educationProgram;
    }

    public void setEducationProgram(String educationProgram) {
        this.educationProgram = educationProgram;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public String getRegNumber() {
        return regNumber;
    }

    public void setRegNumber(String regNumber) {
        this.regNumber = regNumber;
    }
}
