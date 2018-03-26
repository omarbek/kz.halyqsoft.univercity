package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_DATA;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Omarbek
 * @created Apr 26, 2016 12:20:23 PM
 */
@Entity
@NamedQueries({
		@NamedQuery(name = "T_SCHEDULE.findScheduleBySemesterData",
				query = "SELECT s FROM SCHEDULE s WHERE s.semesterData = :semesterData")
})
public class SCHEDULE extends AbstractEntity {

	private static final long serialVersionUID = 2962239619943762688L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SEMESTER_DATA_ID", referencedColumnName = "ID")})
    private SEMESTER_DATA semesterData;
	
	@FieldInfo(type = EFieldType.FK_COMBO, order = 2)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "CHAIR_ID", referencedColumnName = "ID")})
    private DEPARTMENT chair;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "schedule")
	private Set<SCHEDULE_DETAIL> scheduleDetails;
	
	public SCHEDULE() {
		scheduleDetails = new HashSet<>();
	}

	public SEMESTER_DATA getSemesterData() {
		return semesterData;
	}

	public void setSemesterData(SEMESTER_DATA semesterData) {
		this.semesterData = semesterData;
	}

	public DEPARTMENT getChair() {
		return chair;
	}

	public void setChair(DEPARTMENT chair) {
		this.chair = chair;
	}

	public Set<SCHEDULE_DETAIL> getScheduleDetails() {
		return scheduleDetails;
	}

	public void setScheduleDetails(Set<SCHEDULE_DETAIL> scheduleDetails) {
		this.scheduleDetails = scheduleDetails;
	}
}
