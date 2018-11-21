package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ATTESTATION_TYPE;
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

	private static final long serialVersionUID = 5436195498756128045L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SCHEDULE_DETAIL_ID", referencedColumnName = "ID")})
    private SCHEDULE_DETAIL scheduleDetail;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ATTESTATION_TYPE_ID", referencedColumnName = "ID")})
    private ATTESTATION_TYPE attestationType;


	@FieldInfo(type = EFieldType.DATE, order = 4)
	@Column(name = "STARTING_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date startingDate;

	@FieldInfo(type = EFieldType.DATE, order = 5)
	@Column(name = "FINAL_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date finalDate;


	@FieldInfo(type = EFieldType.DATE, order = 6)
	@Column(name = "created")
	@Temporal(TemporalType.TIMESTAMP)
	private Date created = new Date();

}
