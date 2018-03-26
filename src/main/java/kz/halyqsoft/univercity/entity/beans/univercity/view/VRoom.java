package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

/**
 * @author Omarbek
 * @created Apr 10, 2017 3:31:46 PM
 */
public final class VRoom extends AbstractEntity {

	private static final long serialVersionUID = -8885207633816701710L;

	@FieldInfo(type = EFieldType.TEXT, order = 2, columnWidth = 120)
	private String corpusName;
	
	@FieldInfo(type = EFieldType.TEXT, order = 3)
	private String roomNo;
	
	@FieldInfo(type = EFieldType.TEXT, order = 4, columnWidth = 120)
	private String roomTypeName;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 5, columnWidth = 120)
	private int capacity;
	
	public VRoom() {
	}

	public String getCorpusName() {
		return corpusName;
	}

	public void setCorpusName(String corpusName) {
		this.corpusName = corpusName;
	}

	public String getRoomNo() {
		return roomNo;
	}

	public void setRoomNo(String roomNo) {
		this.roomNo = roomNo;
	}

	public String getRoomTypeName() {
		return roomTypeName;
	}

	public void setRoomTypeName(String roomTypeName) {
		this.roomTypeName = roomTypeName;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
}
