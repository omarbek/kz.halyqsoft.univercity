package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;
import org.r3a.common.entity.tree.CommonTree;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import java.util.List;

/**
 * @author Omarbek
 * Created Nov 3, 2015 9:48:09 AM
 */
@Entity
public class COUNTRY extends AbstractEntity implements CommonTree<COUNTRY> {

	private static final long serialVersionUID = 7890277111114588274L;

	@FieldInfo(type = EFieldType.TEXT, max = 64)
	@Column(name = "COUNTRY_NAME", nullable = false)
	private String countryName;
	
	@FieldInfo(type = EFieldType.FK_DIALOG, order = 2, required = false, readOnlyFixed = true)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "PARENT_ID", referencedColumnName = "ID")})
    private COUNTRY parent;

	@OneToMany(mappedBy = "parent")
	private List<COUNTRY> children;
	
	@Transient
    private boolean selected;
	
	@Transient
    private boolean deleted;
	
	public COUNTRY() {
	}
	
	public String getCountryName() {
		return countryName;
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}

	@Override
	public COUNTRY getParent() {
		return parent;
	}

	@Override
	public void setParent(COUNTRY parent) {
		this.parent = parent;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public String getIconPath() {
		return null;
	}

	@Override
	public boolean hasParent() {
		return (parent != null);
	}

	@Override
	public List<COUNTRY> getChildren() {
        return children;
	}

	public void setChildren(List<COUNTRY> children) {
		this.children = children;
	}

	@Override
	public String toString() {
		return countryName;
	}
}
