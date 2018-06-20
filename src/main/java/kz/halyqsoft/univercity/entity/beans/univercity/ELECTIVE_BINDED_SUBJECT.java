package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_DATA;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Omarbek
 * @created 19.06.2018
 */
@Entity
public class ELECTIVE_BINDED_SUBJECT extends AbstractEntity {

	@FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "FIRST_SUBJECT_ID", referencedColumnName = "ID")})
    private SUBJECT firstSubject;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 3)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "SECOND_SUBJECT_ID", referencedColumnName = "ID")})
	private SUBJECT secondSubject;

	@Column(name = "CREATED")
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;

	public SUBJECT getFirstSubject() {
		return firstSubject;
	}

	public void setFirstSubject(SUBJECT firstSubject) {
		this.firstSubject = firstSubject;
	}

	public SUBJECT getSecondSubject() {
		return secondSubject;
	}

	public void setSecondSubject(SUBJECT secondSubject) {
		this.secondSubject = secondSubject;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
}
