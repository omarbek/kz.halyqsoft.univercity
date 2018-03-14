package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;

/**
 * @author Omarbek
 * Created Dec 26, 2016 1:53:22 PM
 */
public class PPS extends AbstractEntity {

	private static final long serialVersionUID = 994880026342978648L;
	
	private String name;
	
	public PPS() {
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
