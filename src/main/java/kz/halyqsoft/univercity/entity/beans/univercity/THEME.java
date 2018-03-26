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
public class THEME extends AbstractEntity {
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 1, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SUBJECT_NAME_ID", referencedColumnName = "ID")})
    private TESTING_SUBJECT subject;
	
	@FieldInfo(type = EFieldType.TEXT, order = 2)
	@Column(name = "THEME")
	private String theme;
	
	public THEME() {
	}
	
	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}
	
	public TESTING_SUBJECT getSubject() {
		return subject;
	}

	public void setSubject(TESTING_SUBJECT subject) {
		this.subject = subject;
	}

	@Override
	public String toString() {
		return theme;
	}
}
