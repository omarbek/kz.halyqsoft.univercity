package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ABSENCE_CAUSE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ATTESTATION_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_EMPLOYEE;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Omarbek
 * @created Sep 28, 2016 3:12:10 PM
 */
@Entity
public class EMPLOYEE_ABSENCE_CAUSE extends AbstractEntity {

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EMPLOYEE_ID", referencedColumnName = "ID")})
    private V_EMPLOYEE employee;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ABSENCE_CAUSE_ID", referencedColumnName = "ID")})
    private ABSENCE_CAUSE absenceCause;


	@FieldInfo(type = EFieldType.DATE, order = 3)
	@Column(name = "STARTING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startingDate;

	@FieldInfo(type = EFieldType.DATE, order = 4)
	@Column(name = "FINAL_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date finalDate;


	@FieldInfo(type = EFieldType.DATE, order = 5, inEdit = false, inView = false, inGrid = false)
	@Column(name = "created")
	@Temporal(TemporalType.TIMESTAMP)
	private Date created = new Date();

	public EMPLOYEE_ABSENCE_CAUSE() {
	}

	public V_EMPLOYEE getEmployee() {
		return employee;
	}

	public void setEmployee(V_EMPLOYEE employee) {
		this.employee = employee;
	}

	public ABSENCE_CAUSE getAbsenceCause() {
		return absenceCause;
	}

	public void setAbsenceCause(ABSENCE_CAUSE absenceCause) {
		this.absenceCause = absenceCause;
	}

	public Date getStartingDate() {
		return startingDate;
	}

	public void setStartingDate(Date startingDate) {
		this.startingDate = startingDate;
	}

	public Date getFinalDate() {
		return finalDate;
	}

	public void setFinalDate(Date finalDate) {
		this.finalDate = finalDate;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
}
