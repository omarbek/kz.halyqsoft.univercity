package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.ANSWER;
import kz.halyqsoft.univercity.entity.beans.univercity.THEME;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;

/**
 * @author Omarbek
 * @created 11 ���. 2016 �. 15:11:29
 */
@Entity
public class V_QUESTION_ANSWER extends AbstractEntity {
	
	private static final long serialVersionUID = 3697737980849912283L;

	@FieldInfo(type = EFieldType.TEXT, order = 2)
	@Column(name = "QUESTION")
	private String question;
	
	@FieldInfo(type = EFieldType.INTEGER, order = 3)
	@Column(name = "LEVEL")
	private Integer level;
	
	@FieldInfo(type = EFieldType.TEXT, max = 128, order = 4, inEdit = false, inView = true, inGrid = true)
	@Column(name = "THEME", nullable = false)
	private String themeName;
	
	@FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, order = 5)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "THEME_ID", referencedColumnName = "ID")})
    private THEME themeId;
	
	@FieldInfo(type = EFieldType.TEXT, max = 128, order = 6)
	@Column(name = "ANSWER1", nullable = false)
	private String answer1;
	
	@FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, inEdit = false, inView = false, order = 7)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ID1", referencedColumnName = "ID")})
    private ANSWER answerId1;
	
	@FieldInfo(type = EFieldType.BOOLEAN, required = false, order = 8)
	@Column(name = "CORRECTLY1", nullable = false)
    private boolean correctly1;
	
	@FieldInfo(type = EFieldType.TEXT, max = 128, order = 9)
	@Column(name = "ANSWER2", nullable = false)
	private String answer2;
	
	@FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, inEdit = false, inView = false, order = 10)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ID2", referencedColumnName = "ID")})
    private ANSWER answerId2;
	
	@FieldInfo(type = EFieldType.BOOLEAN, required = false, order = 11)
	@Column(name = "CORRECTLY2", nullable = false)
    private boolean correctly2;
	
	@FieldInfo(type = EFieldType.TEXT, max = 128, order = 12)
	@Column(name = "ANSWER3", nullable = false)
	private String answer3;
	
	@FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, inEdit = false, inView = false, order = 13)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ID3", referencedColumnName = "ID")})
    private ANSWER answerId3;
	
	@FieldInfo(type = EFieldType.BOOLEAN, required = false, order = 14)
	@Column(name = "CORRECTLY3", nullable = false)
    private boolean correctly3;
	
	@FieldInfo(type = EFieldType.TEXT, max = 128, order = 15)
	@Column(name = "ANSWER4", nullable = false)
	private String answer4;
	
	@FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, inEdit = false, inView = false, order = 16)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ID4", referencedColumnName = "ID")})
    private ANSWER answerId4;
	
	@FieldInfo(type = EFieldType.BOOLEAN, required = false, order = 17)
	@Column(name = "CORRECTLY4", nullable = false)
    private boolean correctly4;
	
	@FieldInfo(type = EFieldType.TEXT, max = 128, order = 18)
	@Column(name = "ANSWER5", nullable = false)
	private String answer5;
	
	@FieldInfo(type = EFieldType.FK_COMBO, inGrid = false, inEdit = false, inView = false, order = 19)
    @ManyToOne
    @JoinColumns({
        @JoinColumn(name = "ID5", referencedColumnName = "ID")})
    private ANSWER answerId5;
	
	@FieldInfo(type = EFieldType.BOOLEAN, required = false, order = 20)
	@Column(name = "CORRECTLY5", nullable = false)
    private boolean correctly5;
		
	public V_QUESTION_ANSWER() {
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
	
	public String getThemeName() {
		return themeName;
	}

	public void setThemeName(String themeName) {
		this.themeName = themeName;
	}
	
	public THEME getThemeId() {
		return themeId;
	}

	public void setThemeId(THEME themeId) {
		this.themeId = themeId;
	}
	
	public String getAnswer1() {
		return answer1;
	}

	public void setAnswer1(String answer1) {
		this.answer1 = answer1;
	}
	
	public ANSWER getAnswerId1() {
		return answerId1;
	}

	public void setAnswerId1(ANSWER answerId1) {
		this.answerId1 = answerId1;
	}

	public boolean getCorrectly1() {
		return correctly1;
	}

	public void setCorrectly1(boolean correctly1) {
		this.correctly1 = correctly1;
	}
	
	public String getAnswer2() {
		return answer2;
	}

	public void setAnswer2(String answer2) {
		this.answer2 = answer2;
	}
	
	public ANSWER getAnswerId2() {
		return answerId2;
	}

	public void setAnswerId2(ANSWER answerId2) {
		this.answerId2 = answerId2;
	}

	public boolean getCorrectly2() {
		return correctly2;
	}

	public void setCorrectly2(boolean correctly2) {
		this.correctly2 = correctly2;
	}
	
	public String getAnswer3() {
		return answer3;
	}

	public void setAnswer3(String answer3) {
		this.answer3 = answer3;
	}
	
	public ANSWER getAnswerId3() {
		return answerId3;
	}

	public void setAnswerId3(ANSWER answerId3) {
		this.answerId3 = answerId3;
	}

	public boolean getCorrectly3() {
		return correctly3;
	}

	public void setCorrectly3(boolean correctly3) {
		this.correctly3 = correctly3;
	}
	
	public String getAnswer4() {
		return answer4;
	}

	public void setAnswer4(String answer4) {
		this.answer4 = answer4;
	}
	
	public ANSWER getAnswerId4() {
		return answerId4;
	}

	public void setAnswerId4(ANSWER answerId4) {
		this.answerId4 = answerId4;
	}

	public boolean getCorrectly4() {
		return correctly4;
	}

	public void setCorrectly4(boolean correctly4) {
		this.correctly4 = correctly4;
	}
	
	public String getAnswer5() {
		return answer5;
	}

	public void setAnswer5(String answer5) {
		this.answer5 = answer5;
	}
	
	public ANSWER getAnswerId5() {
		return answerId5;
	}

	public void setAnswerId5(ANSWER answerId5) {
		this.answerId5 = answerId5;
	}

	public boolean getCorrectly5() {
		return correctly5;
	}

	public void setCorrectly5(boolean correctly5) {
		this.correctly5 = correctly5;
	}

}
