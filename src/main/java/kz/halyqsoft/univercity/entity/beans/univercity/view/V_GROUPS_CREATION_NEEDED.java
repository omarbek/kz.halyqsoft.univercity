package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.LESSON;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;


@Entity
public class V_GROUPS_CREATION_NEEDED extends AbstractEntity {

	@FieldInfo(type = EFieldType.FK_COMBO, order = 1)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "SPECIALITY_ID", referencedColumnName = "ID")})
	private SPECIALITY speciality;

	@FieldInfo(type = EFieldType.FK_COMBO , order = 2)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "LANGUAGE_ID" ,referencedColumnName = "ID")})
	private LANGUAGE language;

	@FieldInfo(type = EFieldType.FK_COMBO , order = 3)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "ENTRANCE_YEAR_ID", referencedColumnName = "ID")
	})
	private ENTRANCE_YEAR entranceYear;

	@FieldInfo(type = EFieldType.FK_COMBO , order = 4)
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "STUDY_YEAR_ID", referencedColumnName = "ID")
	})
	private STUDY_YEAR studyYear;

	@FieldInfo(type = EFieldType.FK_COMBO , order = 5)
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "CORPUS_ID" , referencedColumnName = "ID")})
	private CORPUS corpus;

	public SPECIALITY getSpeciality() {
		return speciality;
	}

	public void setSpeciality(SPECIALITY speciality) {
		this.speciality = speciality;
	}

	public LANGUAGE getLanguage() {
		return language;
	}

	public void setLanguage(LANGUAGE language) {
		this.language = language;
	}

	public ENTRANCE_YEAR getEntranceYear() {
		return entranceYear;
	}

	public void setEntranceYear(ENTRANCE_YEAR entranceYear) {
		this.entranceYear = entranceYear;
	}

	public CORPUS getCorpus() {
		return corpus;
	}

	public void setCorpus(CORPUS corpus) {
		this.corpus = corpus;
	}

	public STUDY_YEAR getStudyYear() {
		return studyYear;
	}

	public void setStudyYear(STUDY_YEAR studyYear) {
		this.studyYear = studyYear;
	}
}

