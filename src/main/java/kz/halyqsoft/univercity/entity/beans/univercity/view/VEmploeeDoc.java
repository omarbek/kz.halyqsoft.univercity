package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.POST;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

public final class VEmploeeDoc {

    @FieldInfo(type = EFieldType.TEXT)
    private DEPARTMENT department;

    @FieldInfo(type = EFieldType.TEXT)
    private POST post;

    public class VEmployeeDoc {
    }

    public DEPARTMENT getDepartment() {
        return department;
    }

    public void setDepartment(DEPARTMENT department) {
        this.department = department;
    }

    public POST getPost() {
        return post;
    }

    public void setPost(POST post) {
        this.post = post;
    }
}
