package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;

/**
 * @@author Omarbek
 * @created Dec 26, 2016 1:51:09 PM
 */
public class YES_NO extends AbstractEntity {

	private static final long serialVersionUID = 7800686626426561665L;
	
	private String name;
	
	public YES_NO() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return name;
	}
}
