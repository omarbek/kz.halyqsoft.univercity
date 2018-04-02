package kz.halyqsoft.univercity.modules.student;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VStudent;
import kz.halyqsoft.univercity.filter.FStudentFilter;
import kz.halyqsoft.univercity.filter.panel.StudentFilterPanel;
import kz.halyqsoft.univercity.utils.ErrorUtils;
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
import org.r3a.common.vaadin.locale.UILocaleUtil;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dinassil Omarbek
 * @created May 24, 2017 12:58:39 PM
 */
public class AbstractStudentList extends VerticalLayout implements EntityListener, FilterPanelListener {

    protected final StudentFilterPanel filterPanel;

    protected GridWidget studentGW;
    private boolean needDorm;
    private boolean needDormFilter;
    private UILocaleUtil uiLocaleUtil;

    public AbstractStudentList(FStudentFilter filter, boolean needDorm, boolean needDormFilter, UILocaleUtil uiLocaleUtil)
            throws Exception {
        super();
        filterPanel = new StudentFilterPanel(filter);
        this.needDorm = needDorm;
        this.needDormFilter = needDormFilter;
        this.uiLocaleUtil = uiLocaleUtil;

        initView();
    }

    public void initView() throws Exception {
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
//        facultyQM.addWhere("type", ECriteria.EQUAL, T_DEPARTMENT_TYPE.FACULTY_ID);//TODO faculty
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

        if (needDormFilter) {
            cb = new ComboBox();
            cb.setNullSelectionAllowed(true);
            cb.setTextInputAllowed(false);
            cb.setFilteringMode(FilteringMode.OFF);
            cb.setPageLength(0);
            cb.addItem("Да");
            cb.addItem("Нет");
            filterPanel.addFilterComponent("dormStatus", cb);
        }

        addComponent(filterPanel);
        setComponentAlignment(filterPanel, Alignment.TOP_CENTER);

        studentGW = new GridWidget(VStudent.class);
        studentGW.addEntityListener(this);
        studentGW.showToolbar(false);
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

        addComponent(studentGW);
        setComponentAlignment(studentGW, Alignment.MIDDLE_CENTER);
    }

    @Override
    public void doFilter(AbstractFilterBean filterBean) {
        FStudentFilter sf = (FStudentFilter) filterBean;
        int i = 1;
        Map<Integer, Object> params = new HashMap<Integer, Object>();
        StringBuilder sb = new StringBuilder();
        if (sf.getCode() != null && sf.getCode().trim().length() >= 2) {
            sb.append("lower(b.CODE) like '");
            sb.append(sf.getCode().trim().toLowerCase());
            sb.append("%'");
        }
        if (sf.getFirstname() != null && sf.getFirstname().trim().length() >= 3) {
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("lower(b.FIRST_NAME) like '");
            sb.append(sf.getFirstname().trim().toLowerCase());
            sb.append("%'");
        }
        if (sf.getLastname() != null && sf.getLastname().trim().length() >= 3) {
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("lower(b.LAST_NAME) like '");
            sb.append(sf.getLastname().trim().toLowerCase());
            sb.append("%'");
        }
        if (sf.getStudentStatus() != null) {
            params.put(i, sf.getStudentStatus().getId().getId());
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("c.STUDENT_STATUS_ID = ?" + i++);
        }
        if (sf.getFaculty() != null) {
            params.put(i, sf.getFaculty().getId().getId());
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("c.FACULTY_ID = ?" + i++);
        }
        if (sf.getSpeciality() != null) {
            params.put(i, sf.getSpeciality().getId().getId());
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("c.SPECIALITY_ID = ?" + i++);
        }
        if (sf.getStudyYear() != null) {
            params.put(i, sf.getStudyYear().getId().getId());
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("c.STUDY_YEAR_ID = ?" + i++);
        }
        if (sf.getEducationType() != null) {
            params.put(i, sf.getEducationType().getId().getId());
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("c.EDUCATION_TYPE_ID = ?" + i++);
        }
        if (needDorm) {
            params.put(i, 1);
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("a.need_dorm = ?" + i++);
        }
        if (sf.getDormStatus() != null) {
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            if ("Да".equals(sf.getDormStatus())) {
                sb.append("g.check_in_date is not null and g.check_out_date is null and g.deleted=0");
            } else {
                sb.append("(g.check_in_date is null or (g.check_in_date is not null and g.check_out_date is not null and g.deleted=0))");
            }
        }

        List<VStudent> list = new ArrayList<>();
        if (sb.length() > 0) {
            sb.insert(0, " where ");
            String sql = "select a.ID, b.CODE, trim(b.LAST_NAME||' '||b.FIRST_NAME||' '||coalesce(b.MIDDLE_NAME, '')) FIO, d.STATUS_NAME, e.DEPT_SHORT_NAME FACULTY, f.SPEC_NAME"
                    + " from STUDENT a inner join USERS b on a.ID = b.ID"
                    + " inner join STUDENT_EDUCATION c on a.ID = c.STUDENT_ID and c.CHILD_ID is null"
                    + " left join DORM_STUDENT g on g.student_id=c.id"
                    + " inner join STUDENT_STATUS d on c.STUDENT_STATUS_ID = d.ID"
                    + " inner join DEPARTMENT e on c.FACULTY_ID = e.ID"
                    + " inner join SPECIALITY f on c.SPECIALITY_ID = f.ID" + sb.toString()
                    + " and b.deleted=0 and e.deleted=0 and f.deleted=0"
                    + " order by FIO";
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
                ErrorUtils.LOG.error("Unable to load student list: ", ex);
                Message.showError(ex.toString());
            }
        } else {
            Message.showInfo(uiLocaleUtil.getMessage("select.1.search.condition"));
        }

        ((DBGridModel) studentGW.getWidgetModel()).setEntities(list);
        try {
            studentGW.refresh();
        } catch (Exception ex) {
            ErrorUtils.LOG.error("Unable to refresh student grid: ", ex);
            Message.showError(ex.toString());
        }
    }

    @Override
    public void clearFilter() {
        ((DBGridModel) studentGW.getWidgetModel()).setEntities(new ArrayList<>(1));
        try {
            studentGW.refresh();
        } catch (Exception ex) {
            ErrorUtils.LOG.error("Unable to refresh student grid: ", ex);
            Message.showError(ex.toString());
        }
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
    }

    @Override
    public boolean preCreate(Object source, int buttonId) {
        return false;
    }

    @Override
    public boolean onEdit(Object source, Entity e, int buttonId) {
        return true;
    }

    @Override
    public boolean onPreview(Object source, Entity e, int buttonId) {
        return true;
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
    public boolean preDelete(Object source, List<Entity> entities, int buttonId) {
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

    private class FacultyChangeListener implements ValueChangeListener {

        private final ComboBox specialtyCB;

        public FacultyChangeListener(ComboBox specialtyCB) {
            this.specialtyCB = specialtyCB;
        }

        @Override
        public void valueChange(ValueChangeEvent ev) {
            List<SPECIALITY> list = new ArrayList<>(1);
            DEPARTMENT faculty = (DEPARTMENT) ev.getProperty().getValue();
            if (faculty != null) {
                QueryModel<SPECIALITY> specialtyQM = new QueryModel<>(SPECIALITY.class);
                FromItem fi = specialtyQM.addJoin(EJoin.INNER_JOIN, "department", DEPARTMENT.class, "id");
                specialtyQM.addWhere("deleted", Boolean.FALSE);
                specialtyQM.addWhereAnd(fi, "parent", ECriteria.EQUAL, faculty.getId());
                specialtyQM.addOrder("specName");
                try {
                    list = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(specialtyQM);
                } catch (Exception ex) {
                    ErrorUtils.LOG.error("Unable to load specialty list: ", ex);
                    Message.showError(ex.toString());
                }
            }

            BeanItemContainer<SPECIALITY> specialtyBIC = new BeanItemContainer<>(SPECIALITY.class, list);
            specialtyCB.setContainerDataSource(specialtyBIC);
        }
    }
}
