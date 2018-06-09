package kz.halyqsoft.univercity.utils;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VStudent;
import kz.halyqsoft.univercity.filter.FStudentFilter;
import kz.halyqsoft.univercity.filter.panel.StudentFilterPanel;
import kz.halyqsoft.univercity.modules.student.StudentEdit;
import kz.halyqsoft.univercity.modules.student.StudentOrApplicantView;
import kz.halyqsoft.univercity.utils.changelisteners.FacultyChangeListener;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractCommonView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.form.AbstractFormWidgetView;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;

import java.util.*;

/**
 * @author Omarbek
 * @created on 01.06.2018
 */
public abstract class StudentUtils extends AbstractFormWidgetView implements EntityListener, FilterPanelListener {

    private StudentFilterPanel filterPanel;
    private GridWidget studentGW;
    private StudentOrApplicantView studentOrApplicantView;
    private HorizontalLayout buttonsHL;
    private int categoryType;

    public void setFilterPanel(StudentFilterPanel filterPanel) {
        this.filterPanel = filterPanel;
    }

    public StudentFilterPanel createStudentFilterPanel() throws Exception{
        StudentFilterPanel studentFilterPanel = new StudentFilterPanel(new FStudentFilter());
        studentFilterPanel = new StudentFilterPanel(new FStudentFilter());
        studentFilterPanel.addFilterPanelListener(this);
        studentFilterPanel.setImmediate(true);
        setBackButtonVisible(false);
        TextField tf = new TextField();
        tf.setNullRepresentation("");
        tf.setNullSettingAllowed(true);
        studentFilterPanel.addFilterComponent("code", tf);

        ComboBox cb = new ComboBox();
        cb.setNullSelectionAllowed(true);
        cb.setTextInputAllowed(true);
        cb.setFilteringMode(FilteringMode.STARTSWITH);
        QueryModel<CARD> cardQM = new QueryModel<>(CARD.class);
        FromItem userFI = cardQM.addJoin(EJoin.INNER_JOIN, "id", USERS.class, "card");
        cardQM.addWhere(userFI, "typeIndex", ECriteria.EQUAL, 2);
        BeanItemContainer<CARD> cardBIC = new BeanItemContainer<>(CARD.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(cardQM));
        cb.setContainerDataSource(cardBIC);
        studentFilterPanel.addFilterComponent("card", cb);

        cb = new ComboBox();
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
        studentFilterPanel.addFilterComponent("studentStatus", cb);

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
        studentFilterPanel.addFilterComponent("faculty", cb);

        ComboBox specialtyCB = new ComboBox();
        specialtyCB.setNullSelectionAllowed(true);
        specialtyCB.setTextInputAllowed(true);
        specialtyCB.setFilteringMode(FilteringMode.CONTAINS);
        specialtyCB.setPageLength(0);
        specialtyCB.setWidth(250, Unit.PIXELS);
        cb.addValueChangeListener(new FacultyChangeListener(specialtyCB));
        studentFilterPanel.addFilterComponent("speciality", specialtyCB);

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
        studentFilterPanel.addFilterComponent("studyYear", cb);

        cb = new ComboBox();
        cb.setNullSelectionAllowed(true);
        cb.setTextInputAllowed(false);
        cb.setFilteringMode(FilteringMode.STARTSWITH);
        cb.setPageLength(0);
        QueryModel<STUDENT_EDUCATION_TYPE> educationTypeQM = new QueryModel<>(STUDENT_EDUCATION_TYPE.class);
        BeanItemContainer<STUDENT_EDUCATION_TYPE> educationTypeBIC = new BeanItemContainer<>(STUDENT_EDUCATION_TYPE.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(educationTypeQM));
        cb.setContainerDataSource(educationTypeBIC);
        studentFilterPanel.addFilterComponent("educationType", cb);

        return studentFilterPanel;
    }

    public StudentUtils(int categoryType) throws Exception {
        super();
        this.categoryType = categoryType;

        filterPanel = createStudentFilterPanel();

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

        doFilter(filterPanel.getFilterBean());

        getContent().addComponent(studentGW);
        getContent().setComponentAlignment(studentGW, Alignment.MIDDLE_CENTER);
    }

    @Override
    protected AbstractCommonView getParentView() {
        return null;
    }

    @Override
    public String getViewName() {
        return "StudentUtils";
    }

    @Override
    protected String getViewTitle(Locale locale) {
        if (categoryType == 3) {
            return getUILocaleUtil().getCaption("students");
        }
        return getUILocaleUtil().getCaption("applicants" +
                "");
    }

    @Override
    public void initView(boolean b) throws Exception {
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

//                getContent().removeComponent(buttonsHL);
                getContent().removeComponent(filterPanel);
                getContent().removeComponent(studentGW);
                getContent().addComponent(new StudentEdit(fm, getContent(), studentOrApplicantView));

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
        if (sf.getCard() != null) {
            params.put(i, sf.getCard().getId().getId());
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("usr.card_id = ?");
            sb.append(i++);
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

        List<VStudent> list = new ArrayList<>();
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
                " usr.deleted = FALSE AND dep.deleted = FALSE AND spec.deleted = FALSE" +
                " and stu.category_id = " + categoryType +
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
                    list.add(vs);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load student list", ex);
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
        doFilter(filterPanel.getFilterBean());
    }

    public StudentFilterPanel getFilterPanel() {
        return filterPanel;
    }

    public GridWidget getStudentGW() {
        return studentGW;
    }

    public void setStudentOrApplicantView(StudentOrApplicantView studentOrApplicantView) {
        this.studentOrApplicantView = studentOrApplicantView;
    }

    public HorizontalLayout getButtonsHL() {
        return buttonsHL;
    }

    public void setButtonsHL(HorizontalLayout buttonsHL) {
        this.buttonsHL = buttonsHL;
    }
}
