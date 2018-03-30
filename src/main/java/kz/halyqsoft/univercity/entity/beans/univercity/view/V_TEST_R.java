package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.USERS;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Omarbek
 * @created 22 ���. 2016 �. 15:11:29
 */

@SuppressWarnings("serial")
@Entity
public class V_TEST_R extends AbstractEntity {

    @FieldInfo(type = EFieldType.FK_COMBO, inEdit = false, inView = false, inGrid = false, order = 1)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "STUDENT_ID", referencedColumnName = "ID", insertable = false, updatable = false)})
    private USERS student;

    @FieldInfo(type = EFieldType.TEXT, max = 64, inEdit = false, inView = true, inGrid = true, order = 2)
    @Column(name = "FIRST_NAME", insertable = false, updatable = false)
    private String studentName;

    @FieldInfo(type = EFieldType.TEXT, max = 64, inEdit = false, inView = true, inGrid = true, order = 3)
    @Column(name = "LAST_NAME", insertable = false, updatable = false)
    private String studentSurname;

    @FieldInfo(type = EFieldType.INTEGER, inEdit = false, inView = true, inGrid = true, order = 4)
    @Column(name = "TOTAL_SCORE", insertable = false, updatable = false)
    private Integer totalScore;

    @FieldInfo(type = EFieldType.DATE, inGrid = true, inEdit = false, inView = true, order = 5)
    @Column(name = "TEST_DATE", insertable = false, updatable = false)
    @Temporal(TemporalType.DATE)
    private Date testDate;

    @FieldInfo(type = EFieldType.DATETIME, inGrid = true, inEdit = false, inView = true, order = 6)
    @Column(name = "TIME_BEGIN", insertable = false, updatable = false)
    @Temporal(TemporalType.TIME)
    private Date timeBegin;

    @FieldInfo(type = EFieldType.DATETIME, inGrid = true, inEdit = false, inView = true, order = 7)
    @Column(name = "TIME_END", insertable = false, updatable = false)
    @Temporal(TemporalType.TIME)
    private Date timeEnd;

    @FieldInfo(type = EFieldType.TEXT, max = 600, inEdit = false, inView = true, inGrid = true, order = 8)
    @Column(name = "QUESTION1", insertable = false, updatable = false)
    private String question1;

    @FieldInfo(type = EFieldType.BOOLEAN, inEdit = false, inView = true, inGrid = true, order = 9)
    @Column(name = "COR1", insertable = false, updatable = false)
    private boolean correctly1;

    @FieldInfo(type = EFieldType.TEXT, max = 600, inEdit = false, inView = true, inGrid = true, order = 10)
    @Column(name = "QUESTION2", insertable = false, updatable = false)
    private String question2;

    @FieldInfo(type = EFieldType.BOOLEAN, inEdit = false, inView = true, inGrid = true, order = 11)
    @Column(name = "COR2", insertable = false, updatable = false)
    private boolean correctly2;

    @FieldInfo(type = EFieldType.TEXT, max = 600, inEdit = false, inView = true, inGrid = true, order = 12)
    @Column(name = "QUESTION2", insertable = false, updatable = false)
    private String question3;

    @FieldInfo(type = EFieldType.BOOLEAN, inEdit = false, inView = true, inGrid = true, order = 13)
    @Column(name = "COR3", insertable = false, updatable = false)
    private boolean correctly3;

    @FieldInfo(type = EFieldType.TEXT, max = 600, inEdit = false, inView = true, inGrid = true, order = 14)
    @Column(name = "QUESTION4", insertable = false, updatable = false)
    private String question4;

    @FieldInfo(type = EFieldType.BOOLEAN, inEdit = false, inView = true, inGrid = true, order = 15)
    @Column(name = "COR4", insertable = false, updatable = false)
    private boolean correctly4;

    @FieldInfo(type = EFieldType.TEXT, max = 600, inEdit = false, inView = true, inGrid = true, order = 16)
    @Column(name = "QUESTION5", insertable = false, updatable = false)
    private String question5;

    @FieldInfo(type = EFieldType.BOOLEAN, inEdit = false, inView = true, inGrid = true, order = 17)
    @Column(name = "COR5", insertable = false, updatable = false)
    private boolean correctly5;

    @FieldInfo(type = EFieldType.TEXT, max = 600, inEdit = false, inView = true, inGrid = true, order = 18)
    @Column(name = "QUESTION6", insertable = false, updatable = false)
    private String question6;

    @FieldInfo(type = EFieldType.BOOLEAN, inEdit = false, inView = true, inGrid = true, order = 19)
    @Column(name = "COR6", insertable = false, updatable = false)
    private boolean correctly6;

    @FieldInfo(type = EFieldType.TEXT, max = 600, inEdit = false, inView = true, inGrid = true, order = 20)
    @Column(name = "QUESTION7", insertable = false, updatable = false)
    private String question7;

    @FieldInfo(type = EFieldType.BOOLEAN, inEdit = false, inView = true, inGrid = true, order = 21)
    @Column(name = "COR7", insertable = false, updatable = false)
    private boolean correctly7;

    @FieldInfo(type = EFieldType.TEXT, max = 600, inEdit = false, inView = true, inGrid = true, order = 22)
    @Column(name = "QUESTION8", insertable = false, updatable = false)
    private String question8;

    @FieldInfo(type = EFieldType.BOOLEAN, inEdit = false, inView = true, inGrid = true, order = 23)
    @Column(name = "COR8", insertable = false, updatable = false)
    private boolean correctly8;

    @FieldInfo(type = EFieldType.TEXT, max = 600, inEdit = false, inView = true, inGrid = true, order = 24)
    @Column(name = "QUESTION9", insertable = false, updatable = false)
    private String question9;

    @FieldInfo(type = EFieldType.BOOLEAN, inEdit = false, inView = true, inGrid = true, order = 25)
    @Column(name = "COR9", insertable = false, updatable = false)
    private boolean correctly9;

    @FieldInfo(type = EFieldType.TEXT, max = 600, inEdit = false, inView = true, inGrid = true, order = 26)
    @Column(name = "QUESTION10", insertable = false, updatable = false)
    private String question10;

    @FieldInfo(type = EFieldType.BOOLEAN, inEdit = false, inView = true, inGrid = true, order = 27)
    @Column(name = "COR10", insertable = false, updatable = false)
    private boolean correctly10;

    public V_TEST_R() {
    }

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

    public String getQuestion1() {
        return question1;
    }

    public void setQuestion1(String question1) {
        this.question1 = question1;
    }

    public Boolean getCorrectly1() {
        return correctly1;
    }

    public void setCorrectly1(Boolean correctly1) {
        this.correctly1 = correctly1;
    }

    public String getQuestion2() {
        return question2;
    }

    public void setQuestion2(String question2) {
        this.question2 = question2;
    }

    public Boolean getCorrectly2() {
        return correctly2;
    }

    public void setCorrectly2(Boolean correctly2) {
        this.correctly2 = correctly2;
    }

    public String getQuestion3() {
        return question3;
    }

    public void setQuestion3(String question3) {
        this.question3 = question3;
    }

    public Boolean getCorrectly3() {
        return correctly3;
    }

    public void setCorrectly3(Boolean correctly3) {
        this.correctly3 = correctly3;
    }

    public String getQuestion4() {
        return question4;
    }

    public void setQuestion4(String question4) {
        this.question4 = question4;
    }

    public Boolean getCorrectly4() {
        return correctly4;
    }

    public void setCorrectly4(Boolean correctly4) {
        this.correctly4 = correctly4;
    }

    public String getQuestion5() {
        return question5;
    }

    public void setQuestion5(String question5) {
        this.question5 = question5;
    }

    public Boolean getCorrectly5() {
        return correctly5;
    }

    public void setCorrectly5(Boolean correctly5) {
        this.correctly5 = correctly5;
    }

    public String getQuestion6() {
        return question6;
    }

    public void setQuestion6(String question6) {
        this.question6 = question6;
    }

    public Boolean getCorrectly6() {
        return correctly6;
    }

    public void setCorrectly6(Boolean correctly6) {
        this.correctly6 = correctly6;
    }

    public String getQuestion7() {
        return question7;
    }

    public void setQuestion7(String question7) {
        this.question7 = question7;
    }

    public Boolean getCorrectly7() {
        return correctly7;
    }

    public void setCorrectly7(Boolean correctly7) {
        this.correctly7 = correctly7;
    }

    public String getQuestion8() {
        return question8;
    }

    public void setQuestion8(String question8) {
        this.question8 = question8;
    }

    public Boolean getCorrectly8() {
        return correctly8;
    }

    public void setCorrectly8(Boolean correctly8) {
        this.correctly8 = correctly8;
    }

    public String getQuestion9() {
        return question9;
    }

    public void setQuestion9(String question9) {
        this.question9 = question9;
    }

    public Boolean getCorrectly9() {
        return correctly9;
    }

    public void setCorrectly9(Boolean correctly9) {
        this.correctly9 = correctly9;
    }

    public String getQuestion10() {
        return question10;
    }

    public void setQuestion10(String question10) {
        this.question10 = question10;
    }

    public Boolean getCorrectly10() {
        return correctly10;
    }

    public void setCorrectly10(Boolean correctly10) {
        this.correctly10 = correctly10;
    }

}
