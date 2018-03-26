package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Omarbek
 * @created 06 ���. 2016 �. 15:11:29
 */

@SuppressWarnings("serial")
@Entity
public class STUDENT_ANSWER extends AbstractEntity {
	
	@FieldInfo(type = EFieldType.FK_COMBO,  inEdit = false, inGrid = false, inView = false, order = 1)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STUDENT_TEST_ID", referencedColumnName = "ID")})
    private STUDENT_TEST studentTest;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "QUESTION_ID", referencedColumnName = "ID")})
    private QUESTIONS question;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 3)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ANSWER_ID", referencedColumnName = "ID")})
    private ANSWER answer;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 4, required = false, inEdit = false, inGrid = false, inView = false)
	@Column(name = "CORRECTLY", nullable = false)
    private boolean correctly;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 5, required = false, inEdit = false, inGrid = false, inView = false)
	@Column(name = "YESNO", nullable = false)
    private boolean yesno;
	
	@FieldInfo(type = EFieldType.BOOLEAN, required = false, order = 6)
	@Column(name = "NUMBER1", nullable = false)
    private boolean number1;
	
	@FieldInfo(type = EFieldType.BOOLEAN, required = false, order = 7)
	@Column(name = "NUMBER2", nullable = false)
    private boolean number2;
	
	@FieldInfo(type = EFieldType.BOOLEAN, required = false, order = 8)
	@Column(name = "NUMBER3", nullable = false)
    private boolean number3;

	@FieldInfo(type = EFieldType.BOOLEAN, required = false, order = 9)
	@Column(name = "NUMBER4", nullable = false)
    private boolean number4;
	
	@FieldInfo(type = EFieldType.BOOLEAN, required = false, order = 10)
	@Column(name = "NUMBER5", nullable = false)
    private boolean number5;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 11, readOnlyFixed = true)
	@Column(name = "NUMBER_Q")
    private Integer number_q;
	
	public STUDENT_ANSWER() {
	}
	
	public STUDENT_TEST getStudentTest() {
		return studentTest;
	}
	
	public void setStudentTest(STUDENT_TEST studentTest) {
		this.studentTest = studentTest;
	}
	
	public QUESTIONS getQuestion() {
		return question;
	}
	
	public void setQuestion(QUESTIONS question) {
		this.question = question;
	}
	
	public ANSWER getAnswer() {
		return answer;
	}
	
	public void setAnswer(ANSWER answer) {
		this.answer = answer;
	}
	
	public boolean getCorrectly() {
		return correctly;
	}

	public void setCorrectly(boolean correctly) {
		this.correctly = correctly;
	}
	
	public boolean getYesno() {
		return yesno;
	}

	public void setYesno(boolean yesno) {
		this.yesno = yesno;
	}
	
	public boolean getNumber1() {
		return number1;
	}

	public void setNumber1(boolean number1) {
		this.number1 = number1;
	}
	
	public boolean getNumber2() {
		return number2;
	}

	public void setNumber2(boolean number2) {
		this.number2 = number2;
	}
	
	public boolean getNumber3() {
		return number3;
	}

	public void setNumber3(boolean number3) {
		this.number3 = number3;
	}
	
	public boolean getNumber4() {
		return number4;
	}

	public void setNumber4(boolean number4) {
		this.number4 = number4;
	}
	
	public boolean getNumber5() {
		return number5;
	}

	public void setNumber5(boolean number5) {
		this.number5 = number5;
	}
	
	public Integer getNumberQ() {
		return number_q;
	}
	
	public void setNumberQ(Integer number_q) {
		this.number_q = number_q;
	}

}
