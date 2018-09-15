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

//    @FieldInfo(type = EFieldType.FK_COMBO, order = 4)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "PREREQUISITE_ID", referencedColumnName = "ID")})
    private SUBJECT prerequisite;

//    @FieldInfo(type = EFieldType.FK_COMBO, order = 5)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "POSTREQUISITE_ID", referencedColumnName = "ID")})
    private SUBJECT postrequisite;

//    @FieldInfo(type = EFieldType.TEXT, isMemo = true, max = 4000, required = false, order = 6, inGrid = false)
    @Column(name = "AIM")
    private String aim;

    @FieldInfo(type = EFieldType.TEXT, isMemo = true, max = 4000, required = false, order = 7, inGrid = false)
    @Column(name = "DESCRIPTION")
    private String description;

//    @FieldInfo(type = EFieldType.TEXT, isMemo = true, max = 4000, required = false, order = 8, inGrid = false)
    @Column(name = "COMPETENCE")
    private String competence;

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

    public ELECTIVE_BINDED_SUBJECT getElectiveBindedSubject() {
        return electiveBindedSubject;
    }

    public void setElectiveBindedSubject(ELECTIVE_BINDED_SUBJECT electiveBindedSubject) {
        this.electiveBindedSubject = electiveBindedSubject;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
