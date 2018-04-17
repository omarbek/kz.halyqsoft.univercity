package kz.halyqsoft.univercity.modules.userarrival;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.TextField;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VUser;
import kz.halyqsoft.univercity.filter.FUserFilter;
import kz.halyqsoft.univercity.filter.panel.UserFilterPanel;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Omarbek
 * @created on 16.04.2018
 */
public class UserArrivalView extends AbstractTaskView implements FilterPanelListener {

    private final UserFilterPanel filterPanel;
    private GridWidget userGW;

    public UserArrivalView(AbstractTask task) throws Exception {
        super(task);
        filterPanel = new UserFilterPanel(new FUserFilter());
    }

    @Override
    public void initView(boolean b) throws Exception {
        filterPanel.addFilterPanelListener(this);
        TextField tf = new TextField();
        tf.setNullRepresentation("");
        tf.setNullSettingAllowed(true);
        filterPanel.addFilterComponent("code", tf);

        tf = new TextField();
        tf.setNullRepresentation("");
        tf.setNullSettingAllowed(true);
        filterPanel.addFilterComponent("firstname", tf);

        tf = new TextField();
        tf.setNullRepresentation("");
        tf.setNullSettingAllowed(true);
        filterPanel.addFilterComponent("lastname", tf);

        CheckBox cb = new CheckBox();
        filterPanel.addFilterComponent("student", cb);

        DateField createdDF = new DateField();
        createdDF.setValue(new Date());
        filterPanel.addFilterComponent("date", createdDF);

        getContent().addComponent(filterPanel);
        getContent().setComponentAlignment(filterPanel, Alignment.TOP_CENTER);

        userGW = new GridWidget(VUser.class);
        userGW.addEntityListener(this);
        userGW.setReadOnly(true);
        userGW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
        userGW.setButtonVisible(AbstractToolbar.ADD_BUTTON, false);
        userGW.setButtonVisible(AbstractToolbar.EDIT_BUTTON, false);
        userGW.setButtonVisible(AbstractToolbar.DELETE_BUTTON, false);
        DBGridModel userGM = (DBGridModel) userGW.getWidgetModel();
        userGM.setTitleVisible(true);
        userGM.setRefreshType(ERefreshType.MANUAL);

        FUserFilter ef = (FUserFilter) filterPanel.getFilterBean();
        if (ef.hasFilter()) {
            doFilter(ef);
        }

        getContent().addComponent(userGW);
        getContent().setComponentAlignment(userGW, Alignment.MIDDLE_CENTER);
    }

    @Override
    public void doFilter(AbstractFilterBean abstractFilterBean) {
        FUserFilter sf = (FUserFilter) abstractFilterBean;
        Map<Integer, Object> params = new HashMap<>();
        StringBuilder userSB = new StringBuilder();
        if (sf.getCode() != null && sf.getCode().trim().length() >= 2) {
            userSB.append(" and lower(usr.CODE) like '");
            userSB.append(sf.getCode().trim().toLowerCase());
            userSB.append("%'");
        }
        if (sf.getFirstname() != null && sf.getFirstname().trim().length() >= 3) {
            userSB.append(" and lower(usr.FIRST_NAME) like '");
            userSB.append(sf.getFirstname().trim().toLowerCase());
            userSB.append("%'");
        }
        if (sf.getLastname() != null && sf.getLastname().trim().length() >= 3) {
            userSB.append(" and lower(usr.LAST_NAME) like '");
            userSB.append(sf.getLastname().trim().toLowerCase());
            userSB.append("%'");
        }
        if (sf.getDate() != null) {
            userSB.append(" and date_trunc('day', usr_arriv.created) = date_trunc('day', TIMESTAMP '");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
            userSB.append(dateFormat.format(sf.getDate()));
            userSB.append("')");
        }

        List<VUser> list = new ArrayList<>();
        userSB.insert(0, " where usr.deleted = FALSE ");
        StringBuilder sqlSB = new StringBuilder("SELECT " +
                "  usr.ID, " +
                "  usr.CODE, " +
                "  trim(usr.LAST_NAME || ' ' || usr.FIRST_NAME || ' ' || coalesce(usr.MIDDLE_NAME, '')) FIO, ");
        if (sf.isStudent()) {
            sqlSB.append("spec.SPEC_NAME                                                            specialty, " +
                    "  year.study_year                                                              studyYear, " +
                    "  NULL                                                                          deptName, " +
                    "  NULL                                                                          postName, " +
                    "  usr_arriv.created " +
                    "FROM USERS usr " +
                    "  INNER JOIN user_arrival usr_arriv ON usr.id = usr_arriv.user_id " +
                    "  INNER JOIN STUDENT stu ON stu.ID = usr.ID " +
                    "  INNER JOIN STUDENT_EDUCATION stu_edu ON stu.ID = stu_edu.STUDENT_ID AND" +
                    " stu_edu.CHILD_ID IS NULL " +
                    "  INNER JOIN SPECIALITY spec ON stu_edu.SPECIALITY_ID = spec.ID " +
                    "  INNER JOIN STUDY_YEAR year ON year.id = stu_edu.study_year_id ");
            sqlSB.append(" AND spec.deleted = FALSE");
        } else {
            sqlSB.append("NULL                                                                      specialty, " +
                    "  NULL                                                                         studyYear, " +
                    "  dep.dept_name                                                                 deptName, " +
                    "  post.post_name                                                                postName, " +
                    "  usr_arriv.created " +
                    "FROM USERS usr " +
                    "  INNER JOIN user_arrival usr_arriv ON usr.id = usr_arriv.user_id " +
                    "  INNER JOIN employee empl ON empl.id = usr.id " +
                    "  INNER JOIN employee_dept empl_dept ON empl.id = empl_dept.employee_id AND" +
                    " empl_dept.DISMISS_DATE IS NULL " +
                    "  INNER JOIN DEPARTMENT dep ON empl_dept.DEPT_ID = dep.ID " +
                    "  INNER JOIN POST post ON empl_dept.POST_ID = post.id ");
        }
        sqlSB.append(userSB.toString());
        sqlSB.append(" ORDER BY FIO");
        try {
            List tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookupItemsList(sqlSB.toString(), params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VUser vs = new VUser();
                    vs.setId(ID.valueOf((long) oo[0]));
                    vs.setCode((String) oo[1]);
                    vs.setFio((String) oo[2]);
                    vs.setSpecialty((String) oo[3]);
                    vs.setStudyYear(oo[4] != null ? ((BigDecimal) oo[4]).intValue() : 0);
                    vs.setDeptName((String) oo[5]);
                    vs.setPostName((String) oo[6]);
                    vs.setCreated((Date) oo[7]);
                    list.add(vs);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load users' list", ex);
        }

        refresh(list);
    }

    @Override
    public void clearFilter() {
        refresh(new ArrayList<>());
    }

    private void refresh(List<VUser> list) {
        ((DBGridModel) userGW.getWidgetModel()).setEntities(list);
        try {
            userGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh users' grid", ex);
        }
    }
}
