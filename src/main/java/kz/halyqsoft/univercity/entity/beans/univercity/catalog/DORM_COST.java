package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Omarbek
 * Created Apr 12, 2017 9:33:41 AM
 */
@Entity
public class DORM_COST extends AbstractEntity {

	private static final long serialVersionUID = 1951020717584408461L;

	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "DORM_ID", referencedColumnName = "ID", nullable = false)})
    private DORM dorm;
	
	@Column(name = "BED_COUNT", nullable = false)
	private Integer bedCount;
	
	@Column(name = "GPA_MIN", nullable = false)
	private Double gpaMin;
	
	@Column(name = "GPA_MAX", nullable = false)
	private Double gpaMax;
	
	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "STUDY_YEAR_ID", referencedColumnName = "ID", nullable = false)})
    private STUDY_YEAR studyYear;
	
	@Column(name = "COST", nullable = false)
	private Double cost;
	
	public DORM_COST() {
	}

	public DORM getDorm() {
		return dorm;
	}

	public void setDorm(DORM dorm) {
		this.dorm = dorm;
	}

	public Integer getBedCount() {
		return bedCount;
	}

	public void setBedCount(Integer bedCount) {
		this.bedCount = bedCount;
	}

	public Double getGpaMin() {
		return gpaMin;
	}

	public void setGpaMin(Double gpaMin) {
		this.gpaMin = gpaMin;
	}

	public Double getGpaMax() {
		return gpaMax;
	}

	public void setGpaMax(Double gpaMax) {
		this.gpaMax = gpaMax;
	}

	public STUDY_YEAR getStudyYear() {
		return studyYear;
	}

	public void setStudyYear(STUDY_YEAR studyYear) {
		this.studyYear = studyYear;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}
}
