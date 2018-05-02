package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;

/**
 * @author Omarbek
 * @created on 23.04.2018
 */
@Entity
public class STUDENT_CREATIVE_EXAM extends AbstractEntity {

    @FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({@JoinColumn(name = "STUDENT_ID", referencedColumnName = "ID", nullable = false)})
    private STUDENT student;

    @FieldInfo(type = EFieldType.TEXT, order = 3, required = false, max = 128)
    @Column(name = "PLACE")
    private String place;

    @FieldInfo(type = EFieldType.INTEGER, max = 100, order = 4, readOnlyFixed = true)
    @Column(name = "RATE")
    private Integer rate;

    public STUDENT getStudent() {
        return student;
    }

    public void setStudent(STUDENT student) {
        this.student = student;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String workPlace) {
        this.place = workPlace;
    }

    public Integer getRate() {
        return rate;
    }

    public void setRate(Integer rate) {
        this.rate = rate;
    }
}
