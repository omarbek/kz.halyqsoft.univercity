package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ACADEMIC_STATUS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ENTRANCE_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LEVEL;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDENT_CATEGORY;
import kz.halyqsoft.univercity.entity.beans.univercity.enumeration.UserType;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_ADVISOR;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_COORDINATOR;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Omarbek
 * @created Nov 9, 2015 12:21:38 PM
 */
@Entity
@DiscriminatorValue(value = UserType.STUDENT_INDEX)
@NamedQueries({
        @NamedQuery(name = "T_STUDENT.getStudentByLogin",
                query = "SELECT s FROM STUDENT s WHERE s.login = :login")
})
public class STUDENT extends USERS {

    private static final long serialVersionUID = -7293682451972401050L;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 30, inGrid = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "LEVEL_ID", referencedColumnName = "ID")})
    private LEVEL level;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 31, inGrid = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "CATEGORY_ID", referencedColumnName = "ID")})
    private STUDENT_CATEGORY category;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 32, inGrid = false, required = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "ACADEMIC_STATUS_ID", referencedColumnName = "ID")})
    private ACADEMIC_STATUS academicStatus;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 33, required = false, inGrid = false)
    @Column(name = "NEED_DORM", nullable = false)
    private boolean needDorm;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 34, inGrid = false, readOnlyFixed = true)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "ENTRANCE_YEAR_ID", referencedColumnName = "ID")})
    private ENTRANCE_YEAR entranceYear;

    @FieldInfo(type = EFieldType.FK_DIALOG, order = 35, inGrid = false, required = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "ADVISOR_ID", referencedColumnName = "ID")})
    private V_ADVISOR advisor;

    @FieldInfo(type = EFieldType.FK_DIALOG, order = 37, inGrid = false, required = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "COORDINATOR_ID", referencedColumnName = "ID")})
    private V_COORDINATOR coordinator;

    @Column(name = "PLOGIN")
    private String parentLogin;

    @Column(name = "PPASSWD")
    private String parentPasswd;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "student")
    private Set<STUDENT_EDUCATION> studentEducations;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "student")
    private Set<ENTRANT_SPECIALITY> entrantSpecialities;

    @OneToOne
    @PrimaryKeyJoinColumn(name = "ID", referencedColumnName = "ID")
    private GRADUATION_PROJECT graduationProject;

    public STUDENT() {
        studentEducations = new HashSet<>();
    }

    public LEVEL getLevel() {
        return level;
    }

    public void setLevel(LEVEL level) {
        this.level = level;
    }

    public STUDENT_CATEGORY getCategory() {
        return category;
    }

    public void setCategory(STUDENT_CATEGORY category) {
        this.category = category;
    }

    public ACADEMIC_STATUS getAcademicStatus() {
        return academicStatus;
    }

    public void setAcademicStatus(ACADEMIC_STATUS academicStatus) {
        this.academicStatus = academicStatus;
    }

    public boolean isNeedDorm() {
        return needDorm;
    }

    public void setNeedDorm(boolean needDorm) {
        this.needDorm = needDorm;
    }

    public ENTRANCE_YEAR getEntranceYear() {
        return entranceYear;
    }

    public void setEntranceYear(ENTRANCE_YEAR entranceYear) {
        this.entranceYear = entranceYear;
    }

    public V_ADVISOR getAdvisor() {
        return advisor;
    }

    public void setAdvisor(V_ADVISOR advisor) {
        this.advisor = advisor;
    }

    public V_COORDINATOR getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(V_COORDINATOR coordinator) {
        this.coordinator = coordinator;
    }

    public String getParentLogin() {
        return parentLogin;
    }

    public void setParentLogin(String parentLogin) {
        this.parentLogin = parentLogin;
    }

    public String getParentPasswd() {
        return parentPasswd;
    }

    public void setParentPasswd(String parentPasswd) {
        this.parentPasswd = parentPasswd;
    }

    public Set<STUDENT_EDUCATION> getStudentEducations() {
        return studentEducations;
    }

    public void setStudentEducations(Set<STUDENT_EDUCATION> studentEducations) {
        this.studentEducations = studentEducations;
    }

    public STUDENT_EDUCATION getLastEducation() {
        for (STUDENT_EDUCATION education : studentEducations) {
            if (education.getChild() == null) {
                return education;
            }
        }
        return null;
    }

    public Set<ENTRANT_SPECIALITY> getEntrantSpecialities() {
        return entrantSpecialities;
    }

    public void setEntrantSpecialities(Set<ENTRANT_SPECIALITY> entrantSpecialities) {
        this.entrantSpecialities = entrantSpecialities;
    }

    public GRADUATION_PROJECT getGraduationProject() {
        return graduationProject;
    }

    public void setGraduationProject(GRADUATION_PROJECT graduationProject) {
        this.graduationProject = graduationProject;
    }
}
