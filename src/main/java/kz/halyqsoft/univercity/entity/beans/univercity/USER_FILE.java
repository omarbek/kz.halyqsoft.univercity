package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.USERS;
import org.r3a.common.entity.AbstractEntity;

import javax.persistence.*;

/**
 * @author Omarbek
 * @created Mar 16, 2017 2:46:03 PM
 */
@Entity
public class USER_FILE extends AbstractEntity {

    private static final long serialVersionUID = -1871311136603846819L;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "USER_ID", referencedColumnName = "ID")})
    private USERS user;

    @Column(name = "FILE_NAME", nullable = false)
    private String fileName;

    @Column(name = "FOLDER", nullable = false)
    private boolean folder;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "PARENT_ID", referencedColumnName = "ID")})
    private USER_FILE parent;

    @Column(name = "DESCR")
    private String descr;

    public USER_FILE() {
    }

    public USERS getUser() {
        return user;
    }

    public void setUser(USERS user) {
        this.user = user;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isFolder() {
        return folder;
    }

    public void setFolder(boolean folder) {
        this.folder = folder;
    }

    public USER_FILE getParent() {
        return parent;
    }

    public void setParent(USER_FILE parent) {
        this.parent = parent;
    }

    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }
}
