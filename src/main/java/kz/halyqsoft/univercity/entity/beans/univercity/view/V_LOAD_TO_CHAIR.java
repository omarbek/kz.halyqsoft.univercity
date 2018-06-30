package kz.halyqsoft.univercity.entity.beans.univercity.view;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;

import javax.persistence.*;

/**
 * @author Omarbek
 * @created on 29.06.2018
 */
@Entity
public class V_LOAD_TO_CHAIR extends AbstractEntity{

    @FieldInfo(type = EFieldType.FK_DIALOG, order = 2, inGrid = false, columnWidth = 80)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "SUBJECT_ID", referencedColumnName = "ID")})
    private SUBJECT subject;

    @FieldInfo(type = EFieldType.FK_DIALOG, order = 3, inGrid = false, columnWidth = 80)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "CURRICULUM_SUBJECT_ID", referencedColumnName = "ID")})
    private V_CURRICULUM_SUBJECT curriculumSubject;

    @FieldInfo(type = EFieldType.TEXT, order = 4)
    @Column(name = "SUBJECT_NAME_KZ", nullable = false)
    private String subjectName;
}
