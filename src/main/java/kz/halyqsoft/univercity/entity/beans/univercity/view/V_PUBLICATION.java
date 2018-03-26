package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.PUBLICATION_TYPE;
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
 * @created Dec 30, 2015 11:46:51 AM
 */
@Entity
public class V_PUBLICATION extends AbstractEntity {

	private static final long serialVersionUID = -4708254180887396293L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1, inEdit = false, inGrid = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "ID")})
    private EMPLOYEE employee;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 2, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "PUBLICATION_TYPE_ID", referencedColumnName = "ID")})
    private PUBLICATION_TYPE publicationType;
	
	@FieldInfo(type = EFieldType.TEXT, order = 3, inEdit = false, inView = false, columnWidth = 200)
	@Column(name = "PUBLICATION_TYPE_NAME", nullable = false)
	private String publicationTypeName;
	
	@FieldInfo(type = EFieldType.TEXT, isMemo = true, max = 512, order = 4)
	@Column(name = "TOPIC", nullable = false)
	private String topic;
	
	public V_PUBLICATION() {
	}

	public EMPLOYEE getEmployee() {
		return employee;
	}

	public void setEmployee(EMPLOYEE employee) {
		this.employee = employee;
	}

	public PUBLICATION_TYPE getPublicationType() {
		return publicationType;
	}

	public void setPublicationType(PUBLICATION_TYPE publicationType) {
		this.publicationType = publicationType;
	}

	public String getPublicationTypeName() {
		return publicationTypeName;
	}

	public void setPublicationTypeName(String publicationTypeName) {
		this.publicationTypeName = publicationTypeName;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}
}
