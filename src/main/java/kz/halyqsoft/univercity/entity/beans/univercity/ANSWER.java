package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

/**
 * @author Omarbek
 * @created 06 ���. 2016 �. 15:11:29
 */

@SuppressWarnings("serial")
@Entity
public class ANSWER extends AbstractEntity {
	
	
	@FieldInfo(type = EFieldType.TEXT, order = 1)
	@Column(name = "ANSWER")
	private String answer;
	
	@FieldInfo(type = EFieldType.FK_COMBO, inEdit = true, inGrid = true, inView = true, order = 2)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "QUESTION_ID", referencedColumnName = "ID")})
    private QUESTIONS question;
	
	@FieldInfo(type = EFieldType.BOOLEAN, required = false, order = 3)
	@Column(name = "CORRECTLY", nullable = false)
    private boolean correctly;
	
	@FieldInfo(type = EFieldType.BLOB, required = false, inView = false, inEdit = false, inGrid = false, order = 4)
	@Column(name = "ANSWER_PIC")
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private byte[] answerPic;
	
	@FieldInfo(type = EFieldType.INTEGER, max = 5, order = 5)
	@Column(name = "NUMBER")
	private Integer number;
	
	public ANSWER() {
	}
	
	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}
	
	public QUESTIONS getQuestion() {
		return question;
	}
	
	public void setQuestion(QUESTIONS question) {
		this.question = question;
	}
	
	public boolean getCorrectly() {
		return correctly;
	}

	public void setCorrectly(boolean correctly) {
		this.correctly = correctly;
	}
	
	public byte[] getAnswerPic() {
		return answerPic;
	}

	public void setAnswerPic(byte[] answerPic) {
		this.answerPic = answerPic;
	}
	
	public Integer getNumber() {
		return number;
	}
	
	public void setNumber(Integer number) {
		this.number = number;
	}
	
	@Override
	public String toString() {
		return answer;
	}

}
