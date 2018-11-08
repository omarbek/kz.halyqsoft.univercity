package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.QUESTIONNAIRE_ANSWER;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.QUESTIONNAIRE_QUESTION;
import org.r3a.common.entity.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Omarbek
 * @created Mar 15, 2018 6:19:29 PM
 */
@Entity
public class QUESTIONING_DETAIL extends AbstractEntity {

	private static final long serialVersionUID = -5345484857184350383L;

	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "QUESTIONING_ID", referencedColumnName = "ID")})
    private QUESTIONING questioning;
	
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "QUESTION_ID", referencedColumnName = "ID")})
    private QUESTIONNAIRE_QUESTION question;
	
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ANSWER_ID", referencedColumnName = "ID")})
    private QUESTIONNAIRE_ANSWER answer;
	
	@Column(name = "ANSWER_TEXT")
	private String answerText;
	
	public QUESTIONING_DETAIL() {
	}

	public QUESTIONING getQuestioning() {
		return questioning;
	}

	public void setQuestioning(QUESTIONING questioning) {
		this.questioning = questioning;
	}

	public QUESTIONNAIRE_QUESTION getQuestion() {
		return question;
	}

	public void setQuestion(QUESTIONNAIRE_QUESTION question) {
		this.question = question;
	}

	public QUESTIONNAIRE_ANSWER getAnswer() {
		return answer;
	}

	public void setAnswer(QUESTIONNAIRE_ANSWER answer) {
		this.answer = answer;
	}

	public String getAnswerText() {
		return answerText;
	}

	public void setAnswerText(String answerText) {
		this.answerText = answerText;
	}
}
