package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

public class PAIR_SUBJECT extends AbstractEntity {

    @FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID")})
    private SUBJECT subject;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 3)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "ELECTIVE_BINDED_SUBJECT_ID", referencedColumnName = "ID")})
    private ELECTIVE_BINDED_SUBJECT electveBindedSubject;

    @FieldInfo(type = EFieldType.INTEGER, order = 4)
    @Column(name = "PAIR_NUMBER")
    private Integer pairNumber;

    public PAIR_SUBJECT(SUBJECT subject, ELECTIVE_BINDED_SUBJECT electveBindedSubject, Integer pairNumber) {
        this.subject = subject;
        this.electveBindedSubject = electveBindedSubject;
        this.pairNumber = pairNumber;
    }

    public SUBJECT getSubject() {
        return subject;
    }

    public void setSubject(SUBJECT subject) {
        this.subject = subject;
    }

    public ELECTIVE_BINDED_SUBJECT getElectveBindedSubject() {
        return electveBindedSubject;
    }

    public void setElectveBindedSubject(ELECTIVE_BINDED_SUBJECT electveBindedSubject) {
        this.electveBindedSubject = electveBindedSubject;
    }

    public Integer getPairNumber() {
        return pairNumber;
    }

    public void setPairNumber(Integer pairNumber) {
        this.pairNumber = pairNumber;
    }
}
