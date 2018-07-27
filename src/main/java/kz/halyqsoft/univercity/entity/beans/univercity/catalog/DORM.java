package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Omarbek
 * Created Apr 11, 2017 3:55:42 PM
 */
@Entity
public class DORM extends AbstractEntity {

	private static final long serialVersionUID = -1283316779025285328L;

	@FieldInfo(type = EFieldType.TEXT, max = 32, order = 1)
	@Column(name = "DORM_NAME", length = 32, nullable = false)
	private String name;

	@FieldInfo(type = EFieldType.TEXT, max = 128, order = 2, required = false)
	@Column(name = "DORM_ADDRESS", length = 128)
	private String address;

	@FieldInfo(type = EFieldType.INTEGER, order = 3, inView = false, inEdit = false)
	@Transient
	private Integer roomCount;

	@FieldInfo(type = EFieldType.INTEGER, order = 4, inView = false, inEdit = false)
	@Transient
	private Integer busyRoomCount;

	@FieldInfo(type = EFieldType.INTEGER, order = 5, inView = false, inEdit = false)
	@Transient
	private Integer bedCount;

	@FieldInfo(type = EFieldType.INTEGER, order = 6, inView = false, inEdit = false)
	@Transient
	private Integer busyBedCount;

	@Column(name = "DELETED", nullable = false)
	private boolean deleted;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "dorm")
	private Set<DORM_ROOM> rooms;

	public DORM() {
		rooms = new HashSet<>();
	}

	public String getName() {
		return name;
	}

	public void setName(String dormName) {
		this.name = dormName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String dormAddress) {
		this.address = dormAddress;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Set<DORM_ROOM> getRooms() {
		return rooms;
	}

	public void setRooms(Set<DORM_ROOM> rooms) {
		this.rooms = rooms;
	}

	public int getRoomCount() {
		if (roomCount == null) {
			roomCount = rooms.size();
		}
		return roomCount;
	}

	public int getBusyRoomCount() {
		if (busyRoomCount == null) {
			busyRoomCount = 0;
			for (DORM_ROOM room : rooms) {
				if (room.getBedCount() <= room.getBusyBedCount()) {
					busyRoomCount++;
				}
			}
		}
		return busyRoomCount;
	}

	public int getBedCount() {
		if (bedCount == null) {
			bedCount = 0;
			for (DORM_ROOM room : rooms) {
				bedCount += room.getBedCount();
			}
		}
		return bedCount;
	}

	public int getBusyBedCount() {
		if (busyBedCount == null) {
			busyBedCount = 0;
			for (DORM_ROOM room : rooms) {
				busyBedCount += room.getBusyBedCount();
			}
		}
		return busyBedCount;
	}

	@Override
	public String toString() {
		return name;
	}
}
