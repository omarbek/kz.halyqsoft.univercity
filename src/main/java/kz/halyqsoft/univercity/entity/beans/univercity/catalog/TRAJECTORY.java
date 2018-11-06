package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;

/**
 * @author Omarbek
 * @created on 06.11.2018
 */
@Entity
public class TRAJECTORY extends AbstractEntity {

    @FieldInfo(type = EFieldType.FK_COMBO)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SPECIALITY_ID", referencedColumnName = "ID")})
    private SPECIALITY speciality;

    @FieldInfo(type = EFieldType.TEXT, order = 2)
    @Column(name = "name", nullable = false)
    private String name;

    public SPECIALITY getSpeciality() {
        return speciality;
    }

    public void setSpeciality(SPECIALITY speciality) {
        this.speciality = speciality;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return getName() + " (" + getSpeciality() + ")";
    }
}
