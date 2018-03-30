package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.USERS;
import org.r3a.common.entity.AbstractEntity;

import javax.persistence.*;

/**
 * @author Omarbek
 * @created Nov 12, 2015 3:03:56 PM
 */
@Entity
public class USER_PHOTO extends AbstractEntity {

    private static final long serialVersionUID = 8637714526263016051L;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "USER_ID", referencedColumnName = "ID")})
    private USERS user;

    @Column(name = "FILE_NAME", nullable = false)
    private String fileName;

    @Column(name = "PHOTO")
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private byte[] photo;

    public USER_PHOTO() {
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

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return fileName;
    }
}
