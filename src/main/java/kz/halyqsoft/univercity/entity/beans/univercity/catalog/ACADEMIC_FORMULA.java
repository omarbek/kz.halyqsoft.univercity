package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Omarbek
 * Created Dec 24, 2015 12:32:05 PM
 */
@Entity
public class ACADEMIC_FORMULA extends AbstractEntity {

    private static final long serialVersionUID = -3242865292527999765L;

    @FieldInfo(type = EFieldType.FK_COMBO)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "CREDITABILITY_ID", referencedColumnName = "ID")})
    private CREDITABILITY creditability;

    @FieldInfo(type = EFieldType.TEXT, min = 5, max = 5, order = 2, inEdit = false, inView = false)
    @Column(name = "FORMULA", nullable = false)
    private String formula;

    @FieldInfo(type = EFieldType.INTEGER, min = 1, max = 2, order = 3)
    @Column(name = "LC_COUNT", nullable = false)
    private Integer lcCount;

    @FieldInfo(type = EFieldType.INTEGER, min = 1, max = 2, order = 4, inView = false)
    @Column(name = "LB_COUNT", nullable = false)
    private Integer lbCount;

    @FieldInfo(type = EFieldType.INTEGER, min = 1, max = 2, order = 5)
    @Column(name = "PR_COUNT", nullable = false)
    private Integer prCount;

    public ACADEMIC_FORMULA() {
    }

    public CREDITABILITY getCreditability() {
        return creditability;
    }

    public void setCreditability(CREDITABILITY creditability) {
        this.creditability = creditability;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public Integer getLcCount() {
        return lcCount;
    }

    public void setLcCount(Integer lcCount) {
        this.lcCount = lcCount;
    }

    public Integer getLbCount() {
        return lbCount;
    }

    public void setLbCount(Integer lbCount) {
        this.lbCount = lbCount;
    }

    public Integer getPrCount() {
        return prCount;
    }

    public void setPrCount(Integer prCount) {
        this.prCount = prCount;
    }

    @Override
    public String toString() {
        return formula;
    }
}
