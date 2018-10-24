package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * @created on 22.10.2018
 */
@Entity
public class PRACTICE_BREAKDOWN extends AbstractEntity {

    @Column(name = "FORMULA", nullable = false)
    private String formula;

    @Column(name = "FIRST_DIGIT", nullable = false)
    private Integer firstDigit;

    @Column(name = "SECOND_DIGIT", nullable = false)
    private Integer secondDigit;

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public Integer getFirstDigit() {
        return firstDigit;
    }

    public void setFirstDigit(Integer firstDigit) {
        this.firstDigit = firstDigit;
    }

    public Integer getSecondDigit() {
        return secondDigit;
    }

    public void setSecondDigit(Integer secondDigit) {
        this.secondDigit = secondDigit;
    }

    @Override
    public String toString() {
        return formula;
    }
}
