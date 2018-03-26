package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * @author Omarbek
 * @created 06 ���. 2016 �. 15:11:29
 */

@SuppressWarnings("serial")
@Entity
public class STUDENT_TEST extends AbstractEntity {
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 1)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STUDENT_ID", referencedColumnName = "ID")})
    private STUDENT student;
	
	@FieldInfo(type = EFieldType.DATETIME, order = 2, readOnlyFixed = true)
	@Column(name = "TEST_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date testDate;
	
	@FieldInfo(type = EFieldType.DATETIME, order = 3, readOnlyFixed = true)
	@Column(name = "TIME_BEGIN")
    @Temporal(TemporalType.TIME)
    private Date timeBegin;

	@FieldInfo(type = EFieldType.DATETIME, order = 4, readOnlyFixed = true)
	@Column(name = "TIME_END")
    @Temporal(TemporalType.TIME)
    private Date timeEnd;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 5, readOnlyFixed = true)
	@Column(name = "TESTING_TIME")
    private Integer testingTime;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 6)
	@Column(name = "TOTAL_SCORE")
	private Integer totalScore;
	
	public STUDENT getStudent() {
		return student;
	}
	
	public void setStudent(STUDENT student) {
		this.student = student;
	}
	
	public Date getTestDate() {
		return testDate;
	}
	
	public void setTestDate(Date testDate) {
		this.testDate = testDate;
	}
	
	public Date getTimeBegin() {
		return timeBegin;
	}
	
	public void setTimeBegin(Date timeBegin) {
		this.timeBegin = timeBegin;
	}
	
	public Date getTimeEnd() {
		return timeEnd;
	}
	
	public void setTimeEnd(Date timeEnd) {
		this.timeEnd = timeEnd;
	}
	
	public Integer getTotalScore() {
		return totalScore;
	}
	
	public void setTotalScore(Integer totalScore) {
		this.totalScore = totalScore;
	}
	
	public Integer getTestingTime() {
		return testingTime;
	}
	
	public void setTestingTime(Integer testingTime) {
		this.testingTime = testingTime;
	}
	
}
