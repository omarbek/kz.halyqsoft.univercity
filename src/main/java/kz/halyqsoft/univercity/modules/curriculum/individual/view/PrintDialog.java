package kz.halyqsoft.univercity.modules.curriculum.individual.view;

import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.utils.WindowUtils;

/**
 * @author Omarbek
 * @created on 21.06.2018
 */
public class PrintDialog extends WindowUtils {

    private PrintView printView;
    private StudentListView studentListView;

    PrintDialog(PrintView printView, StudentListView subjectView) {
        this.printView = printView;
        this.studentListView = subjectView;
        init(1300, 500);
    }

    @Override
    protected String createTitle() {
        return "PrintView";
    }

    @Override
    protected void refresh() throws Exception {
        studentListView.refresh();
    }

    @Override
    protected VerticalLayout getVerticalLayout() {
        VerticalLayout mainVL = new VerticalLayout();
        mainVL.addComponent(printView);
        return mainVL;
    }
}
