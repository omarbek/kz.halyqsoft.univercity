package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created May 4, 2018 2:46:40 PM
 */
@Entity
public class TIME extends AbstractEntity {

    private static final long serialVersionUID = 1026235979423190728L;

    @Column(name = "TIME_VALUE", nullable = false)
    private double timeValue;

    @FieldInfo(type = EFieldType.TEXT, min = 4, max = 5)
    @Column(name = "TIME_NAME", nullable = false)
    private String timeName;

    public TIME() {
    }

    public double getTimeValue() {
        return timeValue;
    }

    public void setTimeValue(double timeValue) {
        this.timeValue = timeValue;
    }

    public String getTimeName() {
        return timeName;
    }

    public void setTimeName(String timeName) {
        this.timeName = timeName;
    }

    @Override
    public String toString() {
        return timeName;
    }
}
