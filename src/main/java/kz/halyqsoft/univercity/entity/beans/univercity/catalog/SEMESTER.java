package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * Author Rakymzhan A. Kenzhegul
 * Created Oct 27, 2015 10:30:44 AM
 */
@Entity
public class SEMESTER extends AbstractEntity {

	private static final long serialVersionUID = -5881919468546426645L;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 1)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STUDY_YEAR_ID", referencedColumnName = "ID")})
    private STUDY_YEAR studyYear;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SEMESTER_PERIOD_ID", referencedColumnName = "ID")})
    private SEMESTER_PERIOD semesterPeriod;
	
	@FieldInfo(type = EFieldType.TEXT, max = 9, order = 3)
	@Column(name = "SEMESTER_NAME", nullable = false)
	private String semesterName;
	
	public SEMESTER() {
	}
	
	public STUDY_YEAR getStudyYear() {
		return studyYear;
	}

	public void setStudyYear(STUDY_YEAR studyYear) {
		this.studyYear = studyYear;
	}
	
	public SEMESTER_PERIOD getSemesterPeriod() {
		return semesterPeriod;
	}

	public void setSemesterPeriod(SEMESTER_PERIOD semesterPeriod) {
		this.semesterPeriod = semesterPeriod;
	}

	public String getSemesterName() {
		return semesterName;
	}

	public void setSemesterName(String semesterName) {
		this.semesterName = semesterName;
	}

	@Override
	public String toString() {
		return semesterName;
	}
}
