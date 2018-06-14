package kz.halyqsoft.univercity.modules.returnstudent;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.TextField;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VStudent;
import kz.halyqsoft.univercity.filter.FStudentFilter;
import kz.halyqsoft.univercity.filter.panel.StudentFilterPanel;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;

import java.util.*;

public class ReturnStudentView extends AbstractTaskView implements FilterPanelListener {

    private GridWidget returnStudentGW;
    private StudentFilterPanel filterPanel;
    private TextField returnStudentTF;

    public ReturnStudentView(AbstractTask task) throws Exception {
        super(task);
        filterPanel = new StudentFilterPanel(new FStudentFilter());
    }

    @Override
    public void initView(boolean b) throws Exception {
        filterPanel.addFilterPanelListener((this));

        returnStudentTF = new TextField();
        returnStudentTF.setNullRepresentation("");
        returnStudentTF.setNullSettingAllowed(true);
        filterPanel.addFilterComponent("code", returnStudentTF);

        getContent().addComponent(filterPanel);
        getContent().setComponentAlignment(filterPanel, Alignment.TOP_CENTER);

        Button deleteStudentButton = new Button(getUILocaleUtil().getCaption("deleteStudentButton"));
        deleteStudentButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                try {
                    Collection<Entity> selectedEntities = returnStudentGW.getSelectedEntities();
                    for (Object object : selectedEntities) {
                        VStudent vStudent = (VStudent) object;
                        STUDENT deletedStudent = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                lookup(STUDENT.class, vStudent.getId());
                        deletedStudent.setCategory(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                lookup(STUDENT_CATEGORY.class, ID.valueOf(1)));
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(deletedStudent);
                    }
                    refresh();
                }catch(Exception e){
                    CommonUtils.showMessageAndWriteLog("Unable to return students list",e);
                }
            }
        });

        getContent().addComponent(deleteStudentButton);
        getContent().setComponentAlignment(deleteStudentButton, Alignment.TOP_LEFT);

        returnStudentGW = new GridWidget(VStudent.class);
        returnStudentGW.addEntityListener(this);
        returnStudentGW.showToolbar(false);

        DBGridModel deleteStudentGM = (DBGridModel) returnStudentGW.getWidgetModel();
        deleteStudentGM.setMultiSelect(true);
        deleteStudentGM.setRefreshType(ERefreshType.MANUAL);
        deleteStudentGM.getColumnModel("category").setInGrid(false);
        deleteStudentGM.getColumnModel("lockReason").setInGrid(false);

        refresh();

        getContent().addComponent(returnStudentGW);
        getContent().setComponentAlignment(returnStudentGW, Alignment.MIDDLE_CENTER);

    }

    public void refresh() throws Exception {
        FStudentFilter ef = (FStudentFilter) filterPanel.getFilterBean();
        doFilter(ef);
    }

    @Override
    public void doFilter(AbstractFilterBean filterBean) {
        FStudentFilter sf = (FStudentFilter) filterBean;
        int i = 1;
        Map<Integer, Object> params = new HashMap<Integer, Object>();
        StringBuilder sb = new StringBuilder();
        if (sf.getCode() != null && sf.getCode().trim().length() >= 2) {
            sb.append("lower(usr.CODE) like '");
            sb.append(sf.getCode().trim().toLowerCase());
            sb.append("%'");
        }
        if (sf.getCard() != null) {
            params.put(i, sf.getCard().getId().getId());
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("usr.id = ?");
            sb.append(i++);
        }

        List<VStudent> studentList = new ArrayList<>();
        if (sb.length() > 0) {
            sb.append(" and ");
        }
        sb.insert(0, " where ");
        String sql = "SELECT " +
                "  stu.ID, " +
                "  usr.CODE, " +
                "  trim(usr.LAST_NAME || ' ' || usr.FIRST_NAME || ' ' || coalesce(usr.MIDDLE_NAME, '')) FIO, " +
                "  stu_status.STATUS_NAME, " +
                "  dep.DEPT_SHORT_NAME                                                              FACULTY, " +
                "  spec.SPEC_NAME " +
                "FROM STUDENT stu INNER JOIN USERS usr ON stu.ID = usr.ID " +
                "  INNER JOIN STUDENT_EDUCATION stu_edu ON stu.ID = stu_edu.STUDENT_ID AND stu_edu.CHILD_ID IS NULL " +
                "  LEFT JOIN DORM_STUDENT dorm_stu ON dorm_stu.student_id = stu_edu.id" +
                "  INNER JOIN STUDENT_STATUS stu_status ON stu_edu.STUDENT_STATUS_ID = stu_status.ID " +
                "  INNER JOIN DEPARTMENT dep ON stu_edu.FACULTY_ID = dep.ID " +
                "  INNER JOIN SPECIALITY spec ON stu_edu.SPECIALITY_ID = spec.ID " +
                sb.toString() +
                "  usr.deleted = FALSE AND dep.deleted = FALSE AND spec.deleted = FALSE" +
                " and stu.category_id = " + 3 +
                " ORDER BY FIO";

        try {
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(
                    sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VStudent vs = new VStudent();
                    vs.setId(ID.valueOf((long) oo[0]));
                    vs.setCode((String) oo[1]);
                    vs.setFio((String) oo[2]);
                    vs.setStatus((String) oo[3]);
                    vs.setFaculty((String) oo[4]);
                    vs.setSpecialty((String) oo[5]);
                    studentList.add(vs);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load student list", ex);
        }
        refresh(studentList);

    }

    private void refresh(List<VStudent> list) {
        ((DBGridModel) returnStudentGW.getWidgetModel()).setEntities(list);
        try {
            returnStudentGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh students list", ex);
        }
    }

    @Override
    public void clearFilter() {
        refresh(new ArrayList<>());
    }
}
