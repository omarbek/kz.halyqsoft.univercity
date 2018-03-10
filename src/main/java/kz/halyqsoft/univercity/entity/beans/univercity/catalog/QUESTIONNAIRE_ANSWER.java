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
 * Created Mar 15, 2017 5:51:18 PM
 */
@Entity
public class QUESTIONNAIRE_ANSWER extends AbstractEntity {

	private static final long serialVersionUID = -8888532332444162774L;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 1)
	@ManyToOne
	@JoinColumns({ @JoinColumn(name = "QUESTION_ID", referencedColumnName = "ID") })
	private QUESTIONNAIRE_QUESTION question;
	
	@FieldInfo(type = EFieldType.TEXT, max = 16, order = 2)
	@Column(name = "ANSWER", nullable = false)
	private String answer;
	
	@Column(name = "DELETED", nullable = false)
	    private boolean deleted;
	
	public QUESTIONNAIRE_ANSWER() {
	}

	public QUESTIONNAIRE_QUESTION getQuestion() {
		return question;
	}

	public void setQuestion(QUESTIONNAIRE_QUESTION question) {
		this.question = question;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	@Override
	public String toString() { 
	    return answer;
	}
}
