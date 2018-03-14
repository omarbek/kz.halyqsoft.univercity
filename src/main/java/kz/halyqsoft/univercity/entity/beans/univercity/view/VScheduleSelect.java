package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;

import java.util.Locale;

/**
 * @@author Omarbek
 * @created Feb 21, 2017 5:47:44 PM
 */
public final class VScheduleSelect extends AbstractEntity {

	private static final long serialVersionUID = -3148184306650532561L;
	
	private String subjectNameKZ;
	private String subjectNameEN;
	private String subjectNameRU;
	private String lessonTypeShortName;
	private String dayShortNameKZ;
	private String dayShortNameEN;
	private String dayShortNameRU;
	private String timePeriod;
	
	public VScheduleSelect() {
	}

	public String getSubjectNameKZ() {
		return subjectNameKZ;
	}

	public void setSubjectNameKZ(String subjectNameKZ) {
		this.subjectNameKZ = subjectNameKZ;
	}

	public String getSubjectNameEN() {
		return subjectNameEN;
	}

	public void setSubjectNameEN(String subjectNameEN) {
		this.subjectNameEN = subjectNameEN;
	}

	public String getSubjectNameRU() {
		return subjectNameRU;
	}

	public void setSubjectNameRU(String subjectNameRU) {
		this.subjectNameRU = subjectNameRU;
	}

	public String getLessonTypeShortName() {
		return lessonTypeShortName;
	}

	public void setLessonTypeShortName(String lessonTypeShortName) {
		this.lessonTypeShortName = lessonTypeShortName;
	}

	public String getDayShortNameKZ() {
		return dayShortNameKZ;
	}

	public void setDayShortNameKZ(String dayShortNameKZ) {
		this.dayShortNameKZ = dayShortNameKZ;
	}

	public String getDayShortNameEN() {
		return dayShortNameEN;
	}

	public void setDayShortNameEN(String dayShortNameEN) {
		this.dayShortNameEN = dayShortNameEN;
	}

	public String getDayShortNameRU() {
		return dayShortNameRU;
	}

	public void setDayShortNameRU(String dayShortNameRU) {
		this.dayShortNameRU = dayShortNameRU;
	}

	public String getTimePeriod() {
		return timePeriod;
	}

	public void setTimePeriod(String timePeriod) {
		this.timePeriod = timePeriod;
	}

	public String toString(Locale locale) {
		StringBuilder sb = new StringBuilder();
		if (locale.getLanguage().equals("kk")) {
			sb.append(subjectNameKZ);
		} else if (locale.getLanguage().equals("en")) {
			sb.append(subjectNameEN);
		} else {
			sb.append(subjectNameRU);
		}
		
		sb.append(" ");
		sb.append(lessonTypeShortName);
		sb.append(" ");
		
		if (locale.getLanguage().equals("kk")) {
			sb.append(dayShortNameKZ);
		} else if (locale.getLanguage().equals("en")) {
			sb.append(dayShortNameEN);
		} else {
			sb.append(dayShortNameRU);
		}
		
		sb.append(" ");
		sb.append(timePeriod);
		
		return sb.toString();
	}
}
