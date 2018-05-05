package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.POST;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;

/**
 * @author Omarbek
 * @created on 19.04.2018
 */
@Entity
public class V_COORDINATOR extends AbstractEntity {

    private static final long serialVersionUID = -2968057743108063633L;

    @FieldInfo(type = EFieldType.TEXT, max = 9, order = 1, inGrid = false)
    @Column(name = "CODE")
    private String code;

    @FieldInfo(type = EFieldType.TEXT, max = 64, order = 2)
    @Column(name = "FIO", nullable = false)
    private String fio;

    @FieldInfo(type = EFieldType.FK_DIALOG, order = 3, inGrid = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "DEPT_ID", referencedColumnName = "ID")})
    private DEPARTMENT department;

    @FieldInfo(type = EFieldType.TEXT, order = 4, inEdit = false, inView = false)
    @Column(name = "DEPT_NAME", nullable = false)
    private String deptName;

    @FieldInfo(type = EFieldType.FK_COMBO, order = 5, inGrid = false)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "POST_ID", referencedColumnName = "ID")})
    private POST post;

    @FieldInfo(type = EFieldType.TEXT, order = 6, inEdit = false, inView = false)
    @Column(name = "POST_NAME", nullable = false)
    private String postName;

    public V_COORDINATOR() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public DEPARTMENT getDepartment() {
        return department;
    }

    public void setDepartment(DEPARTMENT department) {
        this.department = department;
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public POST getPost() {
        return post;
    }

    public void setPost(POST post) {
        this.post = post;
    }

    public String getPostName() {
        return postName;
    }

    public void setPostName(String postName) {
        this.postName = postName;
    }

    @Override
    public String toString() {
        return fio;
    }
}
