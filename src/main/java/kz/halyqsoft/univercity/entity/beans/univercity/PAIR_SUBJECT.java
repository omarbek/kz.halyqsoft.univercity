package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;

@Entity
public class PAIR_SUBJECT extends AbstractEntity {

    @FieldInfo(type = EFieldType.FK_COMBO, order = 1, inEdit = false, inGrid = false, inView = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "ELECTIVE_BINDED_SUBJECT_ID", referencedColumnName = "ID")})
    private ELECTIVE_BINDED_SUBJECT electiveBindedSubject;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID")})
    private SUBJECT subject;

    @FieldInfo(type = EFieldType.INTEGER, max = 5, order = 3)
    @Column(name = "PAIR_NUMBER")
    private Integer pairNumber;

    public PAIR_SUBJECT() {
    }

    public SUBJECT getSubject() {
        return subject;
    }

    public void setSubject(SUBJECT subject) {
        this.subject = subject;
    }

    public ELECTIVE_BINDED_SUBJECT getElectveBindedSubject() {
        return electiveBindedSubject;
    }

    public void setElectveBindedSubject(ELECTIVE_BINDED_SUBJECT electveBindedSubject) {
        this.electiveBindedSubject = electveBindedSubject;
    }

    public Integer getPairNumber() {
        return pairNumber;
    }

    public void setPairNumber(Integer pairNumber) {
        this.pairNumber = pairNumber;
    }


}
