package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.view.V_STUDENT;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;

@Entity
public class GRADUATE_EMPLOYMENT extends AbstractEntity {

    @FieldInfo(type = EFieldType.FK_COMBO, order = 1)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "STUDENT_ID", referencedColumnName = "ID")})
    private V_STUDENT student;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 2, required = false)
    @Column(name = "EMPLOYED", nullable = false)
    private boolean employed;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 3,required = false)
    @Column(name = "BY_SPECIALITY",nullable = false)
    private boolean bySpeciality;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 4,required = false)
    @Column(name = "MASTER",nullable = false)
    private boolean master;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 5,required = false)
    @Column(name = "DECREE",nullable = false)
    private boolean decree;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 6,required = false)
    @Column(name = "ARMY",nullable = false)
    private boolean army;

    public GRADUATE_EMPLOYMENT() {
    }

    public V_STUDENT getStudent() {
        return student;
    }

    public void setStudent(V_STUDENT student) {
        this.student = student;
    }

    public boolean isEmployed() {
        return employed;
    }

    public void setEmployed(boolean employed) {
        this.employed = employed;
    }

    public boolean isBySpeciality() {
        return bySpeciality;
    }

    public void setBySpeciality(boolean bySpeciality) {
        this.bySpeciality = bySpeciality;
    }

    public boolean isMaster() {
        return master;
    }

    public void setMaster(boolean master) {
        this.master = master;
    }

    public boolean isDecree() {
        return decree;
    }

    public void setDecree(boolean decree) {
        this.decree = decree;
    }

    public boolean isArmy() {
        return army;
    }

    public void setArmy(boolean army) {
        this.army = army;
    }
}
