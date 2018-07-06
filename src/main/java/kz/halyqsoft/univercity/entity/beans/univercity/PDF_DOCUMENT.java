package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.USERS;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;

@Entity
public class PDF_DOCUMENT extends AbstractEntity {

    @FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, inEdit = false, inView = false, order = 1)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "USER_ID", referencedColumnName = "ID", nullable = false)})
    private USERS user;

    @FieldInfo(order = 2)
    @Column(name = "title")
    private String title;

    @FieldInfo(order = 3)
    @Column(name = "file_name", nullable = false)
    private String fileName;

    @FieldInfo(inGrid = false , inView = false , inEdit = false, order = 4)
    @Column(name = "deleted", nullable = false)
    private Boolean deleted;

    @FieldInfo(inGrid = false , inView = false , inEdit = false, order = 5)
    @Column(name = "file_byte")
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] fileByte;

    public PDF_DOCUMENT() {
    }

    public USERS getUser() {
        return user;
    }

    public void setUser(USERS user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getFileByte() {
        return fileByte;
    }

    public void setFileByte(byte[] fileByte) {
        this.fileByte = fileByte;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}
