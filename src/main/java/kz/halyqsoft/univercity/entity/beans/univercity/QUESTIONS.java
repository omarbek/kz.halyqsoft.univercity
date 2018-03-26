package kz.halyqsoft.univercity.entity.beans.univercity;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;
import org.r3a.common.entity.file.FileBean;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * @author Omarbek
 * @created 06 ���. 2016 �. 15:11:29
 */

@SuppressWarnings("serial")
@Entity
public class QUESTIONS extends AbstractEntity {
	
	@FieldInfo(type = EFieldType.TEXT, max = 600, order = 1)
	@Column(name = "QUESTION")
	private String question;
	
	@FieldInfo(type = EFieldType.INTEGER, max = 5, order = 2)
	@Column(name = "LEVEL")
	private Integer level;
	
	@FieldInfo(type = EFieldType.FK_COMBO, inGrid = true, inView = true, order = 3)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "THEME_ID", referencedColumnName = "ID")})
    private THEME theme;
	
	/*@FieldInfo(type = EFieldType.BLOB, required = false, inView = false, inEdit = false, inGrid = false, order = 4)
	@Column(name = "QUESTION_PIC")
    @Lob
    @Basic(fetch = FetchType.EAGER)
    private byte[] questionPic;*/
	
	@Transient
	@FieldInfo(type = EFieldType.FILE_LIST, order = 4, required = false, inView = false, inEdit = false, inGrid = false)
	private List<FileBean> questionPic = new ArrayList<FileBean>();
	
	public QUESTIONS() {
	}
	
	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}
	
	public Integer getLevel() {
		return level;
	}
	
	public void setLevel(Integer level) {
		this.level = level;
	}
	
	public THEME getTheme() {
		return theme;
	}
	
	public void setTheme (THEME theme) {
		this.theme = theme;
	}
	
	/*public byte[] getQuestionPic() {
		return questionPic;
	}

	public void setQuestionPic(byte[] questionPic) {
		this.questionPic = questionPic;
	}*/
	
	public List<FileBean> getFileList() {
		return questionPic;
	}

	public void setFileList(List<FileBean> questionPic) {
		this.questionPic = questionPic;
	}
	
	@Override
	public String toString() {
		return question;
	}

}
