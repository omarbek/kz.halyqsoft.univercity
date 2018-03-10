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
 * Created Mar 15, 2017 5:27:11 PM
 */
@Entity
public class QUESTIONNAIRE_SECTION extends AbstractEntity {

	private static final long serialVersionUID = -2928879760670436644L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1)
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "QUESTIONNAIRE_ID", referencedColumnName = "ID")})
    private QUESTIONNAIRE questionnaire;
	
	@FieldInfo(type = EFieldType.TEXT, max = 512, order = 2)
	@Column(name = "SECTION_NAME", nullable = false)
	private String sectionName;
	
	@Column(name = "DELETED", nullable = false)
    private boolean deleted;
	
	public QUESTIONNAIRE_SECTION() {
	}

	public QUESTIONNAIRE getQuestionnaire() {
		return questionnaire;
	}

	public void setQuestionnaire(QUESTIONNAIRE questionnaire) {
		this.questionnaire = questionnaire;
	}

	public String getSectionName() {
		return sectionName;
	}

	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
}
