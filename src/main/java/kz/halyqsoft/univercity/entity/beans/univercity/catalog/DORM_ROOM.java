package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Omarbek
 * Created Apr 11, 2017 4:17:33 PM
 */
@Entity
public class DORM_ROOM extends AbstractEntity {

	private static final long serialVersionUID = 539867413160973719L;

	@ManyToOne
    @JoinColumns({
        @JoinColumn(name = "DORM_ID", referencedColumnName = "ID", nullable = false)})
    private DORM dorm;
	
	@Column(name = "ROOM_NO", nullable = false)
	private String roomNo;
	
	@Column(name = "BED_COUNT", nullable = false)
	private Integer bedCount;
	
	@Column(name = "COST", nullable = false)
	private Double cost;
	
	@Column(name = "DELETED", nullable = false)
    private boolean deleted;
	
	@Column(name = "OLD_ID")
	private String oldId;
	
	public DORM_ROOM() {
	}

	public DORM getDorm() {
		return dorm;
	}

	public void setDorm(DORM dorm) {
		this.dorm = dorm;
	}

	public String getRoomNo() {
		return roomNo;
	}

	public void setRoomNo(String roomNo) {
		this.roomNo = roomNo;
	}

	public Integer getBedCount() {
		return bedCount;
	}

	public void setBedCount(Integer bedCount) {
		this.bedCount = bedCount;
	}

	public Double getCost() {
		return cost;
	}

	public void setCost(Double cost) {
		this.cost = cost;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	
	public String getOldId() {
		return oldId;
	}

	public void setOldId(String oldId) {
		this.oldId = oldId;
	}

	@Override
	public String toString() {
		return roomNo;
	}
}
