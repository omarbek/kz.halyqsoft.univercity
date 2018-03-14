package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @@author Omarbek
 * @created Dec 21, 2015 3:45:00 PM
 */
@SuppressWarnings("serial")
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "SCIENTIFIC_TYPE_ID", discriminatorType = DiscriminatorType.INTEGER)
public abstract class EMPLOYEE_SCIENTIFIC extends AbstractEntity {

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1, inEdit = false, inGrid = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "ID")})
    private EMPLOYEE employee;
	
	@FieldInfo(type = EFieldType.TEXT, isMemo = true, max = 512, order = 2)
	@Column(name = "TOPIC", nullable = false)
	private String topic;

	public EMPLOYEE getEmployee() {
		return employee;
	}

	public void setEmployee(EMPLOYEE employee) {
		this.employee = employee;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}
}
