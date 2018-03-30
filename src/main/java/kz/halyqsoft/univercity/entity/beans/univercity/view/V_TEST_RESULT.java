package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.USERS;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Omarbek
 * @created 21 ���. 2016 �. 15:11:29
 */
@SuppressWarnings("serial")
@Entity
public class V_TEST_RESULT extends AbstractEntity {

	/*@FieldInfo(type = EFieldType.FK_COMBO, inEdit = false, inView = true, inGrid = true, order = 2)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STUDENT_TEST_ID", referencedColumnName = "ID")})
    private STUDENT_TEST studentTestId;*/

    @FieldInfo(type = EFieldType.FK_COMBO, inEdit = false, inView = false, inGrid = false, order = 3)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "STUDENT_ID", referencedColumnName = "ID")})
    private USERS student;

    @FieldInfo(type = EFieldType.TEXT, max = 64, inEdit = false, inView = true, inGrid = true, order = 4)
    @Column(name = "FIRST_NAME")
    private String studentName;

    @FieldInfo(type = EFieldType.TEXT, max = 64, inEdit = false, inView = true, inGrid = true, order = 5)
    @Column(name = "LAST_NAME")
    private String studentSurname;

    @FieldInfo(type = EFieldType.INTEGER, inEdit = false, inView = false, inGrid = false, order = 6)
    @Column(name = "TOTAL_SCORE")
    private Integer totalScore;

    @FieldInfo(type = EFieldType.TEXT, max = 600, inEdit = false, inView = true, inGrid = true, order = 7)
    @Column(name = "QUESTION")
    private String question;

    @FieldInfo(type = EFieldType.BOOLEAN, inEdit = false, inView = true, inGrid = true, order = 8)
    @Column(name = "CORRECTLY")
    private boolean correctly;

    @FieldInfo(type = EFieldType.DATE, inGrid = true, inEdit = false, inView = true, order = 9)
    @Column(name = "TEST_DATE")
    @Temporal(TemporalType.DATE)
    private Date testDate;

    @FieldInfo(type = EFieldType.DATETIME, inGrid = true, inEdit = false, inView = true, order = 10)
    @Column(name = "TIME_BEGIN")
    @Temporal(TemporalType.TIME)
    private Date timeBegin;

    @FieldInfo(type = EFieldType.DATETIME, inGrid = true, inEdit = false, inView = true, order = 11)
    @Column(name = "TIME_END")
    @Temporal(TemporalType.TIME)
    private Date timeEnd;

    public V_TEST_RESULT() {
    }

	/*public STUDENT_TEST getTestId() {
		return studentTestId;
	}

	public void setTestId(STUDENT_TEST studentTestId) {
		this.studentTestId = studentTestId;
	}*/

    public USERS getStudentId() {
        return student;
    }

    public void setStudentId(USERS studentId) {
        this.student = studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentSurname() {
        return studentSurname;
    }

    public void setStudentSurname(String studentSurname) {
        this.studentSurname = studentSurname;
    }

    public Integer getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(Integer totalScore) {
        this.totalScore = totalScore;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Boolean getCorrectly() {
        return correctly;
    }

    public void setCorrectly(Boolean correctly) {
        this.correctly = correctly;
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

}
