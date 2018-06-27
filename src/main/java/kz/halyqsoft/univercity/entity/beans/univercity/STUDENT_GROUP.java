package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.COUNTRY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.RELATIVE_TYPE;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

@Entity
public class STUDENT_GROUP extends AbstractEntity {

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1 )
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STUDENT_ID", referencedColumnName = "ID")})
    private STUDENT student;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 2, inEdit = false, inGrid = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "GROUPS_ID", referencedColumnName = "ID")})
    private GROUPS groups;

	@FieldInfo(type = EFieldType.DATETIME, order=3 , required = false, readOnlyFixed = true, inGrid = false, inEdit = false, inView = false)
	@Column(name = "created")
	@Temporal(TemporalType.TIMESTAMP)
	private Date created;



	public STUDENT_GROUP() {
	}

	public STUDENT getStudent() {
		return student;
	}

	public void setStudent(STUDENT student) {
		this.student = student;
	}

	public GROUPS getGroups() {
		return groups;
	}

	public void setGroups(GROUPS groups) {
		this.groups = groups;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
}
