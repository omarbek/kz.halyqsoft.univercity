package kz.halyqsoft.univercity.modules.student;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDENT_EDUCATION_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDENT_STATUS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDY_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VStudent;
import kz.halyqsoft.univercity.filter.FStudentFilter;
import kz.halyqsoft.univercity.filter.panel.StudentFilterPanel;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.changelisteners.FacultyChangeListener;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Omarbek
 * @created on 02.04.2018
 */
public class StudentView extends AbstractTaskView implements EntityListener, FilterPanelListener {

    private final StudentFilterPanel filterPanel;
    private GridWidget studentGW;

//    private boolean needDorm;
//    private boolean needDormFilter;

       public StudentView(AbstractTask task) throws Exception {
        super(task);

        filterPanel = new StudentFilterPanel(new FStudentFilter());
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

        ComboBox cb = new ComboBox();
        cb.setNullSelectionAllowed(true);
        cb.setTextInputAllowed(false);
        cb.setFilteringMode(FilteringMode.OFF);
        List<ID> idList = new ArrayList<ID>(4);
        idList.add(ID.valueOf(1));
        idList.add(ID.valueOf(2));
        idList.add(ID.valueOf(3));
        idList.add(ID.valueOf(5));
        QueryModel<STUDENT_STATUS> ssQM = new QueryModel<>(STUDENT_STATUS.class);
        ssQM.addWhereIn("id", idList);
        ssQM.addOrder("id");
        BeanItemContainer<STUDENT_STATUS> ssBIC = new BeanItemContainer<>(STUDENT_STATUS.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(ssQM));
        cb.setContainerDataSource(ssBIC);
        filterPanel.addFilterComponent("studentStatus", cb);

        cb = new ComboBox();
        cb.setNullSelectionAllowed(true);
        cb.setTextInputAllowed(true);
        cb.setFilteringMode(FilteringMode.CONTAINS);
        cb.setPageLength(0);
        cb.setWidth(250, Unit.PIXELS);
        QueryModel<DEPARTMENT> facultyQM = new QueryModel<>(DEPARTMENT.class);
        facultyQM.addWhereNull("parent");
        facultyQM.addWhereAnd("deleted", Boolean.FALSE);
        facultyQM.addOrder("deptName");
        BeanItemContainer<DEPARTMENT> facultyBIC = new BeanItemContainer<>(DEPARTMENT.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(facultyQM));
        cb.setContainerDataSource(facultyBIC);
        filterPanel.addFilterComponent("faculty", cb);

        ComboBox specialtyCB = new ComboBox();
        specialtyCB.setNullSelectionAllowed(true);
        specialtyCB.setTextInputAllowed(true);
        specialtyCB.setFilteringMode(FilteringMode.CONTAINS);
        specialtyCB.setPageLength(0);
        specialtyCB.setWidth(250, Unit.PIXELS);
        cb.addValueChangeListener(new FacultyChangeListener(specialtyCB));
        filterPanel.addFilterComponent("speciality", specialtyCB);

        cb = new ComboBox();
        cb.setNullSelectionAllowed(true);
        cb.setTextInputAllowed(false);
        cb.setFilteringMode(FilteringMode.OFF);
        cb.setPageLength(0);
        cb.setWidth(70, Unit.PIXELS);
        QueryModel<STUDY_YEAR> studyYearQM = new QueryModel<>(STUDY_YEAR.class);
        studyYearQM.addWhere("studyYear", ECriteria.LESS_EQUAL, 7);
        studyYearQM.addOrder("studyYear");
        BeanItemContainer<STUDY_YEAR> studyYearBIC = new BeanItemContainer<>(STUDY_YEAR.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(studyYearQM));
        cb.setContainerDataSource(studyYearBIC);
        filterPanel.addFilterComponent("studyYear", cb);

        cb = new ComboBox();
        cb.setNullSelectionAllowed(true);
        cb.setTextInputAllowed(false);
        cb.setFilteringMode(FilteringMode.OFF);
        cb.setPageLength(0);
        QueryModel<STUDENT_EDUCATION_TYPE> educationTypeQM = new QueryModel<>(STUDENT_EDUCATION_TYPE.class);
        BeanItemContainer<STUDENT_EDUCATION_TYPE> educationTypeBIC = new BeanItemContainer<>(STUDENT_EDUCATION_TYPE.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(educationTypeQM));
        cb.setContainerDataSource(educationTypeBIC);
        filterPanel.addFilterComponent("educationType", cb);

//        if (needDormFilter) {
//            cb = new ComboBox();
//            cb.setNullSelectionAllowed(true);
//            cb.setTextInputAllowed(false);
//            cb.setFilteringMode(FilteringMode.OFF);
//            cb.setPageLength(0);
//            cb.addItem("Да");
//            cb.addItem("Нет");
//            filterPanel.addFilterComponent("dormStatus", cb);
//        }

        getContent().addComponent(filterPanel);
        getContent().setComponentAlignment(filterPanel, Alignment.TOP_CENTER);

        studentGW = new GridWidget(VStudent.class);
        studentGW.addEntityListener(this);
        //studentGW.showToolbar(false);

        DBGridModel studentGM = (DBGridModel) studentGW.getWidgetModel();
        studentGM.setReadOnly(true);
        studentGM.setRefreshType(ERefreshType.MANUAL);
        studentGM.setTitleVisible(false);
        studentGM.setMultiSelect(false);
        studentGM.setRowNumberVisible(true);
        studentGM.setRowNumberWidth(50);
        studentGM.getColumnModel("category").setInGrid(false);
        studentGM.getColumnModel("lockReason").setInGrid(false);

        FStudentFilter sf = (FStudentFilter) filterPanel.getFilterBean();
        if (sf.hasFilter()) {
            doFilter(sf);
        }

        getContent().addComponent(studentGW);
        getContent().setComponentAlignment(studentGW, Alignment.MIDDLE_CENTER);
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
        if (ev.getSource().equals(studentGW)) {
            if (ev.getAction() == EntityEvent.SELECTED) {
                List<Entity> selectedList = ev.getEntities();
                if (!selectedList.isEmpty()) {
                    onEdit(ev.getSource(), selectedList.get(0), 2);
                }
            }
        }
    }

    @Override
    public boolean onEdit(Object source, Entity e, int buttonId) {
        return openStudentEdit(source, e, false);
    }

    private boolean openStudentEdit(Object source, Entity e, boolean readOnly) {
        if (source.equals(studentGW)) {
            FormModel fm = new FormModel(STUDENT.class);
            fm.setReadOnly(readOnly);
            fm.setTitleVisible(false);
            try {
                fm.loadEntity(e.getId());
                getContent().removeComponent(filterPanel);
                getContent().removeComponent(studentGW);
                StudentEdit studentEdit = new StudentEdit(fm, getContent(), this);
                getContent().addComponent(studentEdit);

                return false;
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to open studentEdit", ex);
            }
        }

        return true;
    }

    @Override
    public boolean onPreview(Object source, Entity e, int buttonId) {
        return openStudentEdit(source, e, true);
    }

    @Override
    public boolean preDelete(Object source, List<Entity> entities, int buttonId) {
        if (source.equals(studentGW)) {
            List<STUDENT> mergeList = new ArrayList<>();
            for (Entity e : entities) {
                try {
                    STUDENT s = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                            lookup(STUDENT.class, e.getId());
                    s.setDeleted(true);
                    mergeList.add(s);
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to delete the student", ex);
                }
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(mergeList);
                studentGW.refresh();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to delete the students", ex);
            }
            return false;
        }

        return true;
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
        if (sf.getFirstname() != null && sf.getFirstname().trim().length() >= 3) {
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("lower(usr.FIRST_NAME) like '");
            sb.append(sf.getFirstname().trim().toLowerCase());
            sb.append("%'");
        }
        if (sf.getLastname() != null && sf.getLastname().trim().length() >= 3) {
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("lower(usr.LAST_NAME) like '");
            sb.append(sf.getLastname().trim().toLowerCase());
            sb.append("%'");
        }
        if (sf.getStudentStatus() != null) {
            params.put(i, sf.getStudentStatus().getId().getId());
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("stu_edu.STUDENT_STATUS_ID = ?" + i++);
        }
        if (sf.getFaculty() != null) {
            params.put(i, sf.getFaculty().getId().getId());
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("stu_edu.FACULTY_ID = ?" + i++);
        }
        if (sf.getSpeciality() != null) {
            params.put(i, sf.getSpeciality().getId().getId());
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("stu_edu.SPECIALITY_ID = ?" + i++);
        }
        if (sf.getStudyYear() != null) {
            params.put(i, sf.getStudyYear().getId().getId());
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("stu_edu.STUDY_YEAR_ID = ?" + i++);
        }
        if (sf.getEducationType() != null) {
            params.put(i, sf.getEducationType().getId().getId());
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("stu_edu.EDUCATION_TYPE_ID = ?" + i++);
        }
//        if (needDorm) {
//            params.put(i, 1);
//            if (sb.length() > 0) {
//                sb.append(" and ");
//            }
//            sb.append("stu.need_dorm = ?" + i++);
//        }
        if (sf.getDormStatus() != null) {
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            if ("Да".equals(sf.getDormStatus())) {
                sb.append("dorm_stu.check_in_date is not null and dorm_stu.check_out_date is null" +
                        " and dorm_stu.deleted=0");
            } else {
                sb.append("(dorm_stu.check_in_date is null or (dorm_stu.check_in_date is not null" +
                        " and dorm_stu.check_out_date is not null and dorm_stu.deleted=0))");
            }
        }

        List<VStudent> list = new ArrayList<>();
        if (sb.length() > 0) {
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
                    "  LEFT JOIN DORM_STUDENT dorm_stu ON dorm_stu.student_id = stu_edu.id " +
                    "  INNER JOIN STUDENT_STATUS stu_status ON stu_edu.STUDENT_STATUS_ID = stu_status.ID " +
                    "  INNER JOIN DEPARTMENT dep ON stu_edu.FACULTY_ID = dep.ID " +
                    "  INNER JOIN SPECIALITY spec ON stu_edu.SPECIALITY_ID = spec.ID " +
                    sb.toString() +
                    " and usr.deleted = FALSE AND dep.deleted = FALSE AND spec.deleted = FALSE " +
                    "ORDER BY FIO";
            try {
                List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
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
                        list.add(vs);
                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load student list", ex);
            }
        } else {
            Message.showInfo(getUILocaleUtil().getMessage("select.1.search.condition"));
        }

        ((DBGridModel) studentGW.getWidgetModel()).setEntities(list);
        try {
            studentGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh student grid", ex);
        }
    }

    @Override
    public void clearFilter() {
        ((DBGridModel) studentGW.getWidgetModel()).setEntities(new ArrayList<>(1));
        try {
            studentGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh student grid", ex);
        }
    }

    @Override
    public boolean preCreate(Object source, int buttonId) {
        return false;
    }

    @Override
    public void beforeRefresh(Object source, int buttonId) {
    }

    @Override
    public void onRefresh(Object source, List<Entity> entities) {
    }

    @Override
    public void onFilter(Object source, QueryModel qm, int buttonId) {
    }

    @Override
    public void onAccept(Object source, List<Entity> entities, int buttonId) {
    }

    @Override
    public boolean preSave(Object source, Entity e, boolean isNew, int buttonId) {
        return true;
    }


    @Override
    public void onDelete(Object source, List<Entity> entities, int buttonId) {
    }

    @Override
    public void deferredCreate(Object source, Entity e) {
    }

    @Override
    public void deferredDelete(Object source, List<Entity> entities) {
    }

    @Override
    public void onException(Object source, Throwable ex) {
    }

    @Override
    public void onCreate(Object arg0, Entity arg1, int arg2) {
    }

    public StudentFilterPanel getFilterPanel() {
        return filterPanel;
    }

    public GridWidget getStudentGW() {
        return studentGW;
    }
}
