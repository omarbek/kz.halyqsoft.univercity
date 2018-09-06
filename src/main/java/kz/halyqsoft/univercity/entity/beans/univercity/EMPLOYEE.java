package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.EMPLOYEE_STATUS;
import kz.halyqsoft.univercity.entity.beans.univercity.enumeration.UserType;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;

import java.util.Set;

/**
 * @author Omarbek
 * @created Dec 21, 2015 9:17:14 AM
 */
@Entity
@DiscriminatorValue(value = UserType.EMPLOYEE_INDEX)
@NamedQueries({
		@NamedQuery(name = "EMPLOYEE.getEmployeeByLogin",
				query = "SELECT e FROM EMPLOYEE e WHERE e.login = :login")
})
public class EMPLOYEE extends USERS {

	private static final long serialVersionUID = -4068099239028431993L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 32, inGrid = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "EMPLOYEE_STATUS_ID", referencedColumnName = "ID")})
    private EMPLOYEE_STATUS status;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 30, required = false, inGrid = false)
	@Column(name = "BACHELOR", nullable = false)
    private boolean bachelor;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 31, required = false, inGrid = false)
	@Column(name = "MASTER", nullable = false)
    private boolean master;

	@OneToMany(orphanRemoval = true, cascade = CascadeType.ALL, mappedBy = "employee")
	private Set<EMPLOYEE_DEPT> employeeDepts;
	
	public EMPLOYEE() {
	}

	public EMPLOYEE_STATUS getStatus() {
		return status;
	}

	public void setStatus(EMPLOYEE_STATUS status) {
		this.status = status;
	}

	public boolean isBachelor() {
		return bachelor;
	}

	public void setBachelor(boolean bachelor) {
		this.bachelor = bachelor;
	}

	public boolean isMaster() {
		return master;
	}

	public void setMaster(boolean master) {
		this.master = master;
	}

	public Set<EMPLOYEE_DEPT> getEmployeeDepts() {
		return employeeDepts;
	}

	public void setEmployeeDepts(Set<EMPLOYEE_DEPT> employeeDepts) {
		this.employeeDepts = employeeDepts;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getLastName());
		sb.append(" ");
		sb.append(getFirstName());
		if (getMiddleName() != null) {
			sb.append(" ");
			sb.append(getMiddleName());
		}

		return sb.toString();
	}
}
