package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.enumeration.UserType;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_ADVISOR;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_COORDINATOR;
import org.r3a.common.entity.AbstractEntity;
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
public class STUDENT_ADDITIONAL_INFORMATION extends AbstractEntity{

    @FieldInfo(type = EFieldType.FK_COMBO, order = 1, inGrid = false, inView = false, inEdit = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "STUDENT_ID", referencedColumnName = "ID")})
    private STUDENT student;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 1, required = false)
    @Column(name = "CONVICTION_AVAILABILITY", nullable = false)
    private boolean convictionAvailability = false;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 2, required = false)
    @Column(name = "PERSONAL_COMPUTER_AVAILABILITY", nullable = false)
    private boolean personalComputerAvailability = false;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 3, required = false)
    @Column(name = "COMPUTER_SKILLS_AVAILABILITY", nullable = false)
    private boolean computerSkillsAvailability = false;

    @FieldInfo(type = EFieldType.TEXT, order = 4, required = false)
    @Column(name = "INFORMATION_SOURCE", nullable = false)
    private String informationSource;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 5, required = false)
    @Column(name = "USE_AVAILABILITY", nullable = false)
    private boolean useAvailability = false;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 6, required = false)
    @Column(name = "CERTIFIED_COPY_ATTESTATE", nullable = false)
    private boolean certifiedCopyAttestate = false;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 7, required = false)
    @Column(name = "CONTRACT_AVAILABILITY", nullable = false)
    private boolean contractAvailability = false;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 8, required = false)
    @Column(name = "PHOTO_AVAILABILITY", nullable = false)
    private boolean photoAvailability = false;

    @FieldInfo(type = EFieldType.TEXT, order = 9, required = false)
    @Column(name = "SCHOOL_DIPLOMA_NUMBER", nullable = false)
    private String schoolDiplomaNumber;

    @FieldInfo(type = EFieldType.TEXT, order = 10, required = false)
    @Column(name = "WORK_PLACE", nullable = false)
    private String workPlace;

    public STUDENT_ADDITIONAL_INFORMATION() {

    }

    public STUDENT getStudent() {
        return student;
    }

    public void setStudent(STUDENT student) {
        this.student = student;
    }

    public boolean isConvictionAvailability() {
        return convictionAvailability;
    }

    public void setConvictionAvailability(boolean convictionAvailability) {
        this.convictionAvailability = convictionAvailability;
    }

    public boolean isPersonalComputerAvailability() {
        return personalComputerAvailability;
    }

    public void setPersonalComputerAvailability(boolean personalComputerAvailability) {
        this.personalComputerAvailability = personalComputerAvailability;
    }

    public boolean isComputerSkillsAvailability() {
        return computerSkillsAvailability;
    }

    public void setComputerSkillsAvailability(boolean computerSkillsAvailability) {
        this.computerSkillsAvailability = computerSkillsAvailability;
    }

    public String getInformationSource() {
        return informationSource;
    }

    public void setInformationSource(String informationSource) {
        this.informationSource = informationSource;
    }

    public boolean isUseAvailability() {
        return useAvailability;
    }

    public void setUseAvailability(boolean useAvailability) {
        this.useAvailability = useAvailability;
    }

    public boolean isCertifiedCopyAttestate() {
        return certifiedCopyAttestate;
    }

    public void setCertifiedCopyAttestate(boolean certifiedCopyAttestate) {
        this.certifiedCopyAttestate = certifiedCopyAttestate;
    }

    public boolean isContractAvailability() {
        return contractAvailability;
    }

    public void setContractAvailability(boolean contractAvailability) {
        this.contractAvailability = contractAvailability;
    }

    public boolean isPhotoAvailability() {
        return photoAvailability;
    }

    public void setPhotoAvailability(boolean photoAvailability) {
        this.photoAvailability = photoAvailability;
    }

    public String getSchoolDiplomaNumber() {
        return schoolDiplomaNumber;
    }

    public void setSchoolDiplomaNumber(String schoolDiplomaNumber) {
        this.schoolDiplomaNumber = schoolDiplomaNumber;
    }

    public String getWorkPlace() {
        return workPlace;
    }

    public void setWorkPlace(String workPlace) {
        this.workPlace = workPlace;
    }


}
