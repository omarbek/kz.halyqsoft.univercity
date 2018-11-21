package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;

@Entity
public class PAIR_SUBJECT extends AbstractEntity {

    @FieldInfo(type = EFieldType.TEXT,  max = 255, required = false)
    @Column(name = "CODE")
    private String code;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 2, inEdit = false, inGrid = false, inView = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "ELECTIVE_BINDED_SUBJECT_ID", referencedColumnName = "ID")})
    private ELECTIVE_BINDED_SUBJECT electiveBindedSubject;

    @FieldInfo(type = EFieldType.FK_DIALOG, order = 3)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID")})
    private SUBJECT subject;

    @FieldInfo(type = EFieldType.INTEGER, max = 5, order = 4)
    @Column(name = "PAIR_NUMBER")
    private Integer pairNumber;

    @FieldInfo(type = EFieldType.TEXT, max = 4000, required = false, order = 7, inGrid = false)
    @Column(name = "AIM")
    private String aim;

    @FieldInfo(type = EFieldType.TEXT,  max = 4000,  order = 8, inGrid = false)
    @Column(name = "DESCRIPTION")
    private String description;

    @FieldInfo(type = EFieldType.TEXT, isMemo = true, max = 4000, required = false, order = 9, inGrid = false)
    @Column(name = "COMPETENCE")
    private String competence;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getPairNumber() {
        return pairNumber;
    }

    public void setPairNumber(Integer pairNumber) {
        this.pairNumber = pairNumber;
    }
}
