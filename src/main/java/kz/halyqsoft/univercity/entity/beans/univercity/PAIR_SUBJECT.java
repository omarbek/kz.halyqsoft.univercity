package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;

@Entity
public class PAIR_SUBJECT extends AbstractEntity {

    @FieldInfo(type = EFieldType.INTEGER,  max = 4000, required = false, order = 1, inGrid = false)
    @Column(name = "CODE")
    private int code;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 2, inEdit = false, inGrid = false, inView = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "ELECTIVE_BINDED_SUBJECT_ID", referencedColumnName = "ID")})
    private ELECTIVE_BINDED_SUBJECT electiveBindedSubject;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 3)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID")})
    private SUBJECT subject;

    @FieldInfo(type = EFieldType.INTEGER, max = 5, order = 4)
    @Column(name = "PAIR_NUMBER")
    private int pairNumber;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 5)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "PREREQUISITE_ID", referencedColumnName = "ID")})
    private SUBJECT prerequisite;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 6)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "POSTREQUISITE_ID", referencedColumnName = "ID")})
    private SUBJECT postrequisite;

    @FieldInfo(type = EFieldType.TEXT, max = 4000, required = false, order = 7, inGrid = false)
    @Column(name = "AIM")
    private String aim;

    @FieldInfo(type = EFieldType.TEXT,  max = 4000, required = false, order = 8, inGrid = false)
    @Column(name = "DESCRIPTION")
    private String description;


    @FieldInfo(type = EFieldType.TEXT, isMemo = true, max = 4000, required = false, order = 9, inGrid = false)
    @Column(name = "COMPETENCE")
    private String competence;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public SUBJECT getPrerequisite() {
        return prerequisite;
    }

    public void setPrerequisite(SUBJECT prerequisite) {
        this.prerequisite = prerequisite;
    }

    public SUBJECT getPostrequisite() {
        return postrequisite;
    }

    public void setPostrequisite(SUBJECT postrequisite) {
        this.postrequisite = postrequisite;
    }

    public String getAim() {
        return aim;
    }

    public void setAim(String aim) {
        this.aim = aim;
    }

    public String getCompetence() {
        return competence;
    }

    public void setCompetence(String competence) {
        this.competence = competence;
    }

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

    public int getPairNumber() {
        return pairNumber;
    }

    public void setPairNumber(int pairNumber) {
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
