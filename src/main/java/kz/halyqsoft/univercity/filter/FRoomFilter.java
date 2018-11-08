package kz.halyqsoft.univercity.filter;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.CORPUS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ROOM_TYPE;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;

/**
 * @author Omarbek
 * @created Apr 10, 2018 3:10:38 PM
 */
public final class FRoomFilter extends AbstractFilterBean {

	private static final long serialVersionUID = 2949661250675144595L;
	
	private CORPUS corpus;
	private ROOM_TYPE roomType;
	private String roomNo;
	
	public FRoomFilter() {
	}
	
	public CORPUS getCorpus() {
		return corpus;
	}

	public void setCorpus(CORPUS corpus) {
		this.corpus = corpus;
	}

	public ROOM_TYPE getRoomType() {
		return roomType;
	}

	public void setRoomType(ROOM_TYPE roomType) {
		this.roomType = roomType;
	}

	public String getRoomNo() {
		return roomNo;
	}

	public void setRoomNo(String roomNo) {
		this.roomNo = roomNo;
	}

	@Override
	public boolean hasFilter() {
		return false;
	}
}
