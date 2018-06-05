package kz.halyqsoft.univercity.modules.subject;

import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.utils.WindowUtils;

/**
 * @author Omarbek
 * @created on 05.05.2018
 */
public class SubjectDialog extends WindowUtils {

    private SubjectEdit subjectEdit;
    private SubjectView subjectView;

    SubjectDialog(SubjectEdit subjectEdit, SubjectView subjectView) {
        this.subjectEdit = subjectEdit;
        this.subjectView = subjectView;
        init(1300, 500);
    }

    @Override
    protected String createTitle() {
        return "StudentEdit";
    }

    @Override
    protected void refresh() throws Exception {
        subjectView.refresh();
    }

    @Override
    protected VerticalLayout getVerticalLayout() {
        VerticalLayout mainVL = new VerticalLayout();
        mainVL.addComponent(subjectEdit);
        return mainVL;
    }
}
