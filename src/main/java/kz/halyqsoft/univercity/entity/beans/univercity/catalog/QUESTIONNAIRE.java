package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Omarbek
 * Created Mar 15, 2018 5:06:31 PM
 */
@Entity
public class QUESTIONNAIRE extends AbstractEntity {

	private static final long serialVersionUID = -3007137912410833514L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1)
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "QUESTIONNAIRE_TYPE_ID", referencedColumnName = "ID")})
    private QUESTIONNAIRE_TYPE questionnaireType;
	
	@FieldInfo(type = EFieldType.TEXT, max = 64, order = 2)
	@Column(name = "QUESTIONNAIRE_NAME", nullable = false)
	private String questionnaireName;
	
	@Column(name = "CREATED", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
	
	@Column(name = "UPDATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;
	
	@Column(name = "DELETED", nullable = false)
    private boolean deleted;
	
	public QUESTIONNAIRE() {
	}

	public QUESTIONNAIRE_TYPE getQuestionnaireType() {
		return questionnaireType;
	}

	public void setQuestionnaireType(QUESTIONNAIRE_TYPE questionnaireType) {
		this.questionnaireType = questionnaireType;
	}

	public String getQuestionnaireName() {
		return questionnaireName;
	}

	public void setQuestionnaireName(String questionnaireName) {
		this.questionnaireName = questionnaireName;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}
