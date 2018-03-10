package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Dmitry Dobrin.
 * @created 27.04.2017.
 */
public class V_EXAM_RESULT extends AbstractEntity {

    protected final SimpleDateFormat dateFormatOfDay = new SimpleDateFormat("dd");

    protected final SimpleDateFormat dateFormatOfMonth = new SimpleDateFormat("MM");

    protected final SimpleDateFormat dateFormatOfYear = new SimpleDateFormat("yyyy");

    private String code;

    private String name;

    private BigDecimal credits;

    private BigDecimal ects;

    private String gradeChar;

    private BigDecimal gradeNumber;

    private BigDecimal gradeTraditional;

    private Date date;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCredits() {
        if (credits == null) {
            credits = new BigDecimal(0);
        }
        return credits.setScale(0, BigDecimal.ROUND_HALF_UP);
    }

    public void setCredits(BigDecimal credits) {
        this.credits = credits;
    }

    public BigDecimal getEcts() {
        if (ects == null) {
            ects = new BigDecimal(0);
        }
        return ects.setScale(0, BigDecimal.ROUND_HALF_UP);
    }

    public void setEcts(BigDecimal ects) {
        this.ects = ects;
    }

    public String getGradeChar() {
        return gradeChar;
    }

    public void setGradeChar(String gradeChar) {
        this.gradeChar = gradeChar;
    }

    public BigDecimal getGradeNumber() {
        if (gradeNumber == null) {
            gradeNumber = new BigDecimal(0);
        }
        return gradeNumber.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public void setGradeNumber(BigDecimal gradeNumber) {
        this.gradeNumber = gradeNumber;
    }

    public BigDecimal getGradeTraditional() {
        if (gradeTraditional == null) {
            gradeTraditional = new BigDecimal(0);
        }
        return gradeTraditional.setScale(0, BigDecimal.ROUND_HALF_UP);
    }

    public void setGradeTraditional(BigDecimal gradeTraditional) {
        this.gradeTraditional = gradeTraditional;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDay() {
        return date == null ? null : dateFormatOfDay.format(date);
    }

    public String getMonth() {
        return date == null ? null : dateFormatOfMonth.format(date);
    }

    public String getYear() {
        return date == null ? null : dateFormatOfYear.format(date);
    }
}
