package kz.halyqsoft.univercity.modules.student;

import com.vaadin.ui.HorizontalLayout;
import kz.halyqsoft.univercity.utils.StudentUtils;

/**
 * @author Omarbek
 * @created on 01.06.2018
 */
public class StudentOrApplicantView extends StudentUtils {

    public StudentOrApplicantView(int categoryType, HorizontalLayout buttonsHL, boolean forDorm) throws Exception {
        super(categoryType,forDorm);

        setStudentOrApplicantView(this);
        setButtonsHL(buttonsHL);
    }
}