package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import java.util.Date;

public final class VPreviousExperience extends AbstractEntity {

    @FieldInfo(type = EFieldType.TEXT, order = 1)
    private String organizationName;

    @FieldInfo(type = EFieldType.TEXT, order = 2)
    private String postName;

    @FieldInfo(type = EFieldType.DATE , order = 3)
    private Date hireDate;

    @FieldInfo(type = EFieldType.DATE , order = 4)
    private Date dismissDate;

    @FieldInfo(type = EFieldType.TEXT , order = 5)
    private String workPeriod;

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public Date getDismissDate() {
        return dismissDate;
    }

    public void setDismissDate(Date dismissDate) {
        this.dismissDate = dismissDate;
    }

    public String getWorkPeriod() {
        return workPeriod;
    }

    public void setWorkPeriod(String workPeriod) {
        this.workPeriod = workPeriod;
    }
}
