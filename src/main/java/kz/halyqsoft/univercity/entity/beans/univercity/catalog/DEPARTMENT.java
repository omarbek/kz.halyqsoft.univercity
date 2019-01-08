package kz.halyqsoft.univercity.entity.beans.univercity.catalog;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;
import org.r3a.common.entity.tree.CommonTree;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

/**
 * @author Omarbek
 * Created Nov 4, 2015 11:07:15 AM
 */
@Entity
public class DEPARTMENT extends AbstractEntity implements CommonTree<DEPARTMENT> {

    private static final long serialVersionUID = -290698274912292023L;

    @FieldInfo(type = EFieldType.TEXT, max = 128, order = 1)
    @Column(name = "DEPT_NAME", nullable = false)
    private String deptName;

    @FieldInfo(type = EFieldType.TEXT, max = 5, order = 2, required = false)
    @Column(name = "DEPT_SHORT_NAME", nullable = false)
    private String deptShortName;

    @FieldInfo(type = EFieldType.TEXT, max = 4, order = 3, required = false, readOnlyFixed = true)
    @Column(name = "CODE", nullable = false)
    private String code;

    @FieldInfo(type = EFieldType.FK_DIALOG, order = 4, required = false, readOnlyFixed = true)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "PARENT_ID", referencedColumnName = "ID")})
    private DEPARTMENT parent;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 5, required = false)
    @Column(name = "FC", nullable = false)
    private boolean forEmployees = false;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 6, required = false, inEdit = false, inGrid = false, inView = false)
    @Column(name = "DELETED", nullable = false)
    private boolean deleted;

    @FieldInfo(type = EFieldType.BOOLEAN, order = 7, required = false)
    @Column(name = "DEP_LANGUAGE", nullable = false)
    private boolean depLanguage;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<DEPARTMENT> children = new ArrayList<>();

    @Transient
    private boolean selected;

    public DEPARTMENT() {
    }

    public String getDeptName() {
        return deptName;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public String getDeptShortName() {
        return deptShortName;
    }

    public void setDeptShortName(String deptShortName) {
        this.deptShortName = deptShortName;
    }

    public boolean isForEmployees() {
        return forEmployees;
    }

    public void setForEmployees(boolean forEmployees) {
        this.forEmployees = forEmployees;
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    @Override
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public DEPARTMENT getParent() {
        return parent;
    }

    @Override
    public void setParent(DEPARTMENT parent) {
        this.parent = parent;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
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
    public List<DEPARTMENT> getChildren() {
        return children;
    }

    public void setChildren(List<DEPARTMENT> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return deptName;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public boolean isDepLanguage() {
        return depLanguage;
    }

    public void setDepLanguage(boolean depLanguage) {
        this.depLanguage = depLanguage;
    }
}
