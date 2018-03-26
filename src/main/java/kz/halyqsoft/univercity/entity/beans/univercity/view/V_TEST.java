package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.ANSWER;
import kz.halyqsoft.univercity.entity.beans.univercity.QUESTIONS;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_TEST;
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
 * @created 13 ���. 2016 �. 15:11:29
 */
@SuppressWarnings("serial")
@Entity
public class V_TEST extends AbstractEntity {
	
	@FieldInfo(type = EFieldType.FK_COMBO, inEdit = false, inView = false, inGrid = false, order = 1)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ID", referencedColumnName = "ID")})
    private STUDENT_TEST id;
	
	@FieldInfo(type = EFieldType.FK_COMBO, inEdit = false, inView = false, inGrid = false, order = 2)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STUDENT_ID", referencedColumnName = "ID")})
    private STUDENT student;
	
	@FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, order = 3)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "QUESTION_ID", referencedColumnName = "ID")})
    private QUESTIONS questionId;
	
	@FieldInfo(type = EFieldType.TEXT, max = 600, order = 4, inEdit = false, inView = true, inGrid = true)
	@Column(name = "QUESTION", nullable = false)
	private String question;
	
	@FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, order = 5)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ANSWER_ID1", referencedColumnName = "ID")})
    private ANSWER answerId1;
	
	@FieldInfo(type = EFieldType.TEXT, max = 600, order = 6, inEdit = false, inView = true, inGrid = true)
	@Column(name = "ANSWER1", nullable = false)
	private String answer1;
	
	@FieldInfo(type = EFieldType.BOOLEAN, required = false, order = 7)
	@Column(name = "NUMBER1", nullable = false)
    private boolean number1;
	
	@FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, order = 8)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ANSWER_ID2", referencedColumnName = "ID")})
    private ANSWER answerId2;
	
	@FieldInfo(type = EFieldType.TEXT, max = 600, order = 9, inEdit = false, inView = true, inGrid = true)
	@Column(name = "ANSWER2", nullable = false)
	private String answer2;
	
	@FieldInfo(type = EFieldType.BOOLEAN, required = false, order = 10)
	@Column(name = "NUMBER2", nullable = false)
    private boolean number2;
	
	@FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, order = 11)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ANSWER_ID3", referencedColumnName = "ID")})
    private ANSWER answerId3;
	
	@FieldInfo(type = EFieldType.TEXT, max = 600, order = 12, inEdit = false, inView = true, inGrid = true)
	@Column(name = "ANSWER3", nullable = false)
	private String answer3;
	
	@FieldInfo(type = EFieldType.BOOLEAN, required = false, order = 13)
	@Column(name = "NUMBER3", nullable = false)
    private boolean number3;
	
	@FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, order = 14)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ANSWER_ID4", referencedColumnName = "ID")})
    private ANSWER answerId4;
	
	@FieldInfo(type = EFieldType.TEXT, max = 600, order = 15, inEdit = false, inView = true, inGrid = true)
	@Column(name = "ANSWER4", nullable = false)
	private String answer4;
	
	@FieldInfo(type = EFieldType.BOOLEAN, required = false, order = 16)
	@Column(name = "NUMBER4", nullable = false)
    private boolean number4;
	
	@FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, order = 17)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ANSWER_ID5", referencedColumnName = "ID")})
    private ANSWER answerId5;
	
	@FieldInfo(type = EFieldType.TEXT, max = 600, order = 18, inEdit = false, inView = true, inGrid = true)
	@Column(name = "ANSWER5", nullable = false)
	private String answer5;
	
	@FieldInfo(type = EFieldType.BOOLEAN, required = false, order = 19)
	@Column(name = "NUMBER5", nullable = false)
    private boolean number5;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 20)
	@Column(name = "NUMBER_Q")
    private Integer number_q;
	
	public V_TEST() {
	}
	
	public STUDENT_TEST getTestId() {
		return id;
	}

	public void setTestId(STUDENT_TEST id) {
		this.id = id;
	}
	
	public STUDENT getStudentId() {
		return student;
	}

	public void setStudentId(STUDENT student) {
		this.student = student;
	}
	
	public QUESTIONS getQuestionId() {
		return questionId;
	}

	public void setQuestionId(QUESTIONS questionId) {
		this.questionId = questionId;
	}
	
	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public ANSWER getAnswerId1() {
		return answerId1;
	}

	public void setAnswerId1(ANSWER answerId1) {
		this.answerId1 = answerId1;
	}
	
	public String getAnswer1() {
		return answer1;
	}

	public void setAnswer1(String answer1) {
		this.answer1 = answer1;
	}
	
	public boolean getNumber1() {
		return number1;
	}

	public void setNumber1(boolean number1) {
		this.number1 = number1;
	}
	
	public ANSWER getAnswerId2() {
		return answerId2;
	}

	public void setAnswerId2(ANSWER answerId2) {
		this.answerId2 = answerId2;
	}
	
	public String getAnswer2() {
		return answer2;
	}

	public void setAnswer2(String answer2) {
		this.answer2 = answer2;
	}
	
	public boolean getNumber2() {
		return number2;
	}

	public void setNumber2(boolean number2) {
		this.number2 = number2;
	}
	
	public ANSWER getAnswerId3() {
		return answerId3;
	}

	public void setAnswerId3(ANSWER answerId3) {
		this.answerId3 = answerId3;
	}
	
	public String getAnswer3() {
		return answer3;
	}

	public void setAnswer3(String answer3) {
		this.answer3 = answer3;
	}
	
	public boolean getNumber3() {
		return number3;
	}

	public void setNumber3(boolean number3) {
		this.number3 = number3;
	}
	
	public ANSWER getAnswerId4() {
		return answerId4;
	}

	public void setAnswerId4(ANSWER answerId4) {
		this.answerId4 = answerId4;
	}
	
	public String getAnswer4() {
		return answer4;
	}

	public void setAnswer4(String answer4) {
		this.answer4 = answer4;
	}
	
	public boolean getNumber4() {
		return number4;
	}

	public void setNumber4(boolean number4) {
		this.number4 = number4;
	}
	
	public ANSWER getAnswerId5() {
		return answerId5;
	}

	public void setAnswerId5(ANSWER answerId5) {
		this.answerId5 = answerId5;
	}
	
	public String getAnswer5() {
		return answer5;
	}

	public void setAnswer5(String answer5) {
		this.answer5 = answer5;
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
