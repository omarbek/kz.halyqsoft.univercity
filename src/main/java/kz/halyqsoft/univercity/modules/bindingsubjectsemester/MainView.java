package kz.halyqsoft.univercity.modules.bindingsubjectsemester;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import kz.halyqsoft.univercity.entity.beans.univercity.SEMESTER_SUBJECT;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.util.HashMap;

/**
 * @author Assylkhan
 * on 19.12.2018
 * @project kz.halyqsoft.univercity
 */
public class MainView extends AbstractTaskView{

    private GridWidget semSubjectGW;
    private Button generateSubjectsBtn;

    public MainView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        getContent().setSpacing(true);
        generateSubjectsBtn = new Button(getUILocaleUtil().getCaption("generate"));
        generateSubjectsBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                String sql = "INSERT INTO semester_subject  (select nextval('s_semester_subject'), "+CommonUtils.getCurrentSemesterData().getId().getId().longValue()+" , id from subject s where s.id not in (\n" +
                        "select ss.subject_id from semester_subject ss WHERE ss.semester_data_id =  "+CommonUtils.getCurrentSemesterData().getId().getId().longValue()+"  \n" +
                        ") and s.deleted = FALSE );";

                try{
                    CommonUtils.getQuery().lookupSingle(sql ,new HashMap<>());
                    semSubjectGW.refresh();
                }catch (Exception e){

                }
            }
        });
        semSubjectGW = new GridWidget(SEMESTER_SUBJECT.class);
        DBGridModel semSubjectGM = (DBGridModel) semSubjectGW.getWidgetModel();
        semSubjectGM.getQueryModel().addWhere("semesterData", ECriteria.EQUAL, CommonUtils.getCurrentSemesterData().getId());
        semSubjectGW.setButtonVisible(IconToolbar.ADD_BUTTON ,false);
        semSubjectGW.setButtonVisible(IconToolbar.EDIT_BUTTON,false);
        getContent().setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        getContent().addComponent(generateSubjectsBtn);
        getContent().addComponent(semSubjectGW);
    }
}
