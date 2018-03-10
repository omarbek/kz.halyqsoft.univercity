package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

/**
 * Author Rakymzhan A. Kenzhegul
 * Created Jan 25, 2017 12:25:24 PM
 */
@Entity
public class PROPERTY_TYPE extends AbstractTypeEntity {

	private static final long serialVersionUID = 3800574959196184444L;
	
	@OneToMany(mappedBy = "propertyType", fetch = FetchType.LAZY)
	private List<PROPERTY> propertyList = new ArrayList<PROPERTY>();

	public PROPERTY_TYPE() {
	}

	public List<PROPERTY> getPropertyList() {
		return propertyList;
	}

	public void setPropertyList(List<PROPERTY> propertyList) {
		this.propertyList = propertyList;
	}
}
