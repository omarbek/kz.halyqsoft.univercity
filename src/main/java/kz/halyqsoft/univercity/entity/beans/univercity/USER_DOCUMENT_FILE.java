package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;
import org.r3a.common.entity.file.FileEntity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;

/**
 * @author Omarbek
 * @created Nov 13, 2015 9:53:15 AM
 */
@Entity
public class USER_DOCUMENT_FILE extends AbstractEntity implements FileEntity {

	private static final long serialVersionUID = 9120870079721039718L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1, inEdit = false, inGrid = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "USER_DOC_ID", referencedColumnName = "ID")})
    private USER_DOCUMENT userDocument;
	
	@FieldInfo(type = EFieldType.TEXT, max = 128, order = 2)
	@Column(name = "FILE_NAME", nullable = false)
	private String fileName;
	
	@FieldInfo(type = EFieldType.BLOB, order = 3)
	@Column(name = "FILE_BYTES")
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private byte[] fileBytes;
	
	@FieldInfo(type = EFieldType.BOOLEAN, order = 4, required = false, inEdit = false, inGrid = false, inView = false)
	@Column(name = "DELETED", nullable = false)
    private boolean deleted;

	public USER_DOCUMENT_FILE() {
	}

	public USER_DOCUMENT getUserDocument() {
		return userDocument;
	}

	public void setUserDocument(USER_DOCUMENT userDocument) {
		this.userDocument = userDocument;
	}

	@Override
	public String getFileName() {
		return fileName;
	}

	@Override
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	@Override
	public byte[] getFileBytes() {
		return fileBytes;
	}

	@Override
	public void setFileBytes(byte[] fileBytes) {
		this.fileBytes = fileBytes;
	}
	
	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public String toString() {
		return fileName;
	}
}
