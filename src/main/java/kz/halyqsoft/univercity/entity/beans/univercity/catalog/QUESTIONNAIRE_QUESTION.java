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
 * @author Omarbek
 * Created Mar 15, 2017 5:36:26 PM
 */
@Entity
public class QUESTIONNAIRE_QUESTION extends AbstractEntity {

    private static final long serialVersionUID = 1554790661080053928L;

    @ManyToOne
    @JoinColumns({ @JoinColumn(name = "SECTION_ID", referencedColumnName = "ID") })
    private QUESTIONNAIRE_SECTION section;

    @FieldInfo(type = EFieldType.TEXT, max = 512, order = 1)
    @Column(name = "QUESTION", nullable = false)
    private String question;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 2)
    @Column(name = "ANSWER_TEXT", nullable = false)
    private boolean answerText;

    @Column(name = "DELETED", nullable = false)
    private boolean deleted;

    @Column(name = "QUESTION_NO", nullable = false)
    private int questionNo;

    public QUESTIONNAIRE_QUESTION() {
    }

    public QUESTIONNAIRE_SECTION getSection() {
	return section;
    }

    public void setSection(QUESTIONNAIRE_SECTION section) {
	this.section = section;
    }

    public String getQuestion() {
	return question;
    }

    public void setQuestion(String question) {
	this.question = question;
    }

    public boolean isAnswerText() {
	return answerText;
    }

    public void setAnswerText(boolean answerText) {
	this.answerText = answerText;
    }

    public boolean isDeleted() {
	return deleted;
    }

    public void setDeleted(boolean deleted) {
	this.deleted = deleted;
    }

    public int getQuestionNo() {
	return questionNo;
    }

    public void setQuestionNo(int questionNo) {
	this.questionNo = questionNo;
    }
}
