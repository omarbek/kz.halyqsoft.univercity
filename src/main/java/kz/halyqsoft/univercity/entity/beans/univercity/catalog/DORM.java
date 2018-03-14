package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;

import javax.persistence.Column;
import javax.persistence.Entity;

/**
 * @author Omarbek
 * Created Apr 11, 2017 3:55:42 PM
 */
@Entity
public class DORM extends AbstractEntity {

	private static final long serialVersionUID = -1283316779025285328L;

	@Column(name = "DORM_NAME", nullable = false)
	private String dormName;
	
	@Column(name = "DORM_ADDRESS")
	private String dormAddress;
	
	@Column(name = "ROOM_COUNT", nullable = false)
	private Integer roomCount;
	
	@Column(name = "BED_COUNT", nullable = false)
	private Integer bedCount;
	
	@Column(name = "DELETED", nullable = false)
    private boolean deleted;
	
	public DORM() {
	}

	public String getDormName() {
		return dormName;
	}

	public void setDormName(String dormName) {
		this.dormName = dormName;
	}

	public String getDormAddress() {
		return dormAddress;
	}

	public void setDormAddress(String dormAddress) {
		this.dormAddress = dormAddress;
	}

	public Integer getRoomCount() {
		return roomCount;
	}

	public void setRoomCount(Integer roomCount) {
		this.roomCount = roomCount;
	}

	public Integer getBedCount() {
		return bedCount;
	}

	public void setBedCount(Integer bedCount) {
		this.bedCount = bedCount;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public String toString() {
		return dormName;
	}
}
