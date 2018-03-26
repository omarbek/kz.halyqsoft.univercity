package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.REGISTRATION_SIGNATURE_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_DATA;
import org.r3a.common.entity.AbstractEntity;

import java.util.Date;

import javax.persistence.*;

/**
 * @author Omarbek
 * @created Apr 3, 2017 5:03:41 PM
 */
@Entity
@NamedQueries({
		@NamedQuery(name = "T_REGISTRATION_SIGNATURE.find",
				query = "SELECT rs FROM REGISTRATION_SIGNATURE rs WHERE rs.semesterData = :semesterData AND " +
						"rs.studentEducation = :studentEducation AND rs.signUser = :signUser")
})
public class REGISTRATION_SIGNATURE extends AbstractEntity {

	private static final long serialVersionUID = 2602578096241627014L;

	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SEMESTER_DATA_ID", referencedColumnName = "ID", nullable = false)})
    private SEMESTER_DATA semesterData;
	
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STUDENT_ID", referencedColumnName = "ID", nullable = false)})
    private STUDENT_EDUCATION studentEducation;
	
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SIGN_USER_ID", referencedColumnName = "ID", nullable = false)})
    private USER signUser;
	
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SIGNATURE_TYPE_ID", referencedColumnName = "ID", nullable = false)})
    private REGISTRATION_SIGNATURE_TYPE signatureType;
	
	@Column(name = "SIGN_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date signDate;
	
	public REGISTRATION_SIGNATURE() {
	}

	public SEMESTER_DATA getSemesterData() {
		return semesterData;
	}

	public void setSemesterData(SEMESTER_DATA semesterData) {
		this.semesterData = semesterData;
	}

	public STUDENT_EDUCATION getStudentEducation() {
		return studentEducation;
	}

	public void setStudentEducation(STUDENT_EDUCATION student) {
		this.studentEducation = student;
	}

	public USER getSignUser() {
		return signUser;
	}

	public void setSignUser(USER signUser) {
		this.signUser = signUser;
	}

	public REGISTRATION_SIGNATURE_TYPE getSignatureType() {
		return signatureType;
	}

	public void setSignatureType(REGISTRATION_SIGNATURE_TYPE signatureType) {
		this.signatureType = signatureType;
	}

	public Date getSignDate() {
		return signDate;
	}

	public void setSignDate(Date signDate) {
		this.signDate = signDate;
	}
}
