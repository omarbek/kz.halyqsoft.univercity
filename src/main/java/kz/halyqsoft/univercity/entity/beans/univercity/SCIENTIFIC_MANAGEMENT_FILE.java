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
 * @@author Omarbek
 * @created Dec 21, 2015 5:14:38 PM
 */
@Entity
public class SCIENTIFIC_MANAGEMENT_FILE extends AbstractEntity implements FileEntity {

	private static final long serialVersionUID = -5388921352861855143L;

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1, inEdit = false, inGrid = false, inView = false)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "SCIENTIFIC_MANAGEMENT_ID", referencedColumnName = "ID")})
    private SCIENTIFIC_MANAGEMENT scientificManagement;
	
	@FieldInfo(type = EFieldType.TEXT, max = 128, order = 2)
	@Column(name = "FILE_NAME", nullable = false)
	private String fileName;
	
	@FieldInfo(type = EFieldType.BLOB, order = 3)
	@Column(name = "FILE_BYTES")
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private byte[] fileBytes;
	
	public SCIENTIFIC_MANAGEMENT_FILE() {
	}

	public SCIENTIFIC_MANAGEMENT getScientificManagement() {
		return scientificManagement;
	}

	public void setScientificManagement(SCIENTIFIC_MANAGEMENT scientificManagement) {
		this.scientificManagement = scientificManagement;
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
}
