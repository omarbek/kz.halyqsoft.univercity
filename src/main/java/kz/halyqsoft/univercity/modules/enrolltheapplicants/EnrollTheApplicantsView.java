package kz.halyqsoft.univercity.modules.enrolltheapplicants;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VStudent;
import kz.halyqsoft.univercity.filter.FStudentFilter;
import kz.halyqsoft.univercity.filter.panel.StudentFilterPanel;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.changelisteners.FacultyChangeListener;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author Omarbek
 * @created on 04.05.2018
 */
public class EnrollTheApplicantsView extends AbstractTaskView implements EntityListener, FilterPanelListener {

    private Grid studentGrid;
    private final StudentFilterPanel filterPanel;

    public EnrollTheApplicantsView(AbstractTask task) throws Exception {
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
        cb.setTextInputAllowed(true);
        cb.setFilteringMode(FilteringMode.STARTSWITH);
        QueryModel<CARD> cardQM = new QueryModel<>(CARD.class);
        FromItem userFI = cardQM.addJoin(EJoin.INNER_JOIN, "id", USERS.class, "card");
        cardQM.addWhere(userFI, "typeIndex", ECriteria.EQUAL, 2);
        BeanItemContainer<CARD> cardBIC = new BeanItemContainer<>(CARD.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(cardQM));
        cb.setContainerDataSource(cardBIC);
        filterPanel.addFilterComponent("card", cb);

        cb = new ComboBox();
        cb.setNullSelectionAllowed(true);
        cb.setTextInputAllowed(false);
        cb.setFilteringMode(FilteringMode.OFF);
        List<ID> idList = new ArrayList<>(4);
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
        cb.setFilteringMode(FilteringMode.STARTSWITH);
        cb.setPageLength(0);
        QueryModel<STUDENT_EDUCATION_TYPE> educationTypeQM = new QueryModel<>(STUDENT_EDUCATION_TYPE.class);
        BeanItemContainer<STUDENT_EDUCATION_TYPE> educationTypeBIC = new BeanItemContainer<>(STUDENT_EDUCATION_TYPE.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(educationTypeQM));
        cb.setContainerDataSource(educationTypeBIC);
        filterPanel.addFilterComponent("educationType", cb);

        getContent().addComponent(filterPanel);
        getContent().setComponentAlignment(filterPanel, Alignment.TOP_CENTER);

        initButtons();
        initGrid();
    }

    private void initButtons() {
        HorizontalLayout buttonsHL = CommonUtils.createButtonPanel();

        Button enrollButton = new Button();
        enrollButton.setWidth(120.0F, Unit.PIXELS);
        enrollButton.setIcon(new ThemeResource("img/button/ok.png"));
        enrollButton.setCaption(getUILocaleUtil().getCaption("enroll"));
        enrollButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    Collection<Object> selectedRows = studentGrid.getSelectedRows();
                    for (Object object : selectedRows) {
                        VStudent vStudent = (VStudent) object;
                        STUDENT student = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                lookup(STUDENT.class, vStudent.getId());
                        student.setCategory(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                lookup(STUDENT_CATEGORY.class, ID.valueOf(3)));
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(student);
                    }
                    CommonUtils.showSavedNotification();
                    refresh();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        buttonsHL.addComponent(enrollButton);
        buttonsHL.setComponentAlignment(enrollButton, Alignment.MIDDLE_CENTER);

        Button deleteButton = new Button();
        deleteButton.setWidth(120.0F, Unit.PIXELS);
        deleteButton.setIcon(new ThemeResource("img/button/delete.png"));
        deleteButton.setCaption(getUILocaleUtil().getCaption("delete"));
        deleteButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                        AbstractWebUI.getInstance().addWindow(new reasonDeleted());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        buttonsHL.addComponent(deleteButton);
        buttonsHL.setComponentAlignment(deleteButton, Alignment.MIDDLE_CENTER);

        getContent().addComponent(buttonsHL);
        getContent().setComponentAlignment(buttonsHL, Alignment.MIDDLE_LEFT);
    }

    private void initGrid() {
        studentGrid = new Grid();
        studentGrid.setCaption(getUILocaleUtil().getCaption("applicants"));
        studentGrid.setSizeFull();
        studentGrid.addStyleName("lesson-detail");
        studentGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        studentGrid.addColumn("code").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(VStudent.class,
                "code")).setHidable(false).setWidth(120);
        studentGrid.addColumn("fio").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(VStudent.class,
                "fio")).setHidable(false);
        studentGrid.addColumn("category").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(VStudent.class,
                "category")).setHidable(false);
        studentGrid.addColumn("status").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(VStudent.class,
                "status")).setHidable(false);
        studentGrid.addColumn("faculty").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(VStudent.class,
                "faculty")).setHidable(false);
        studentGrid.addColumn("specialty").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(VStudent.class,
                "specialty")).setHidable(false);
        studentGrid.addColumn("studyYear").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(VStudent.class,
                "studyYear")).setHidable(false);
        studentGrid.addColumn("languageName").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(VStudent.class,
                "languageName")).setHidable(false);

        refresh();

        getContent().addComponent(studentGrid);
        getContent().setComponentAlignment(studentGrid, Alignment.MIDDLE_CENTER);
    }

    private void fillTables() {
        FStudentFilter ef = (FStudentFilter) filterPanel.getFilterBean();
        doFilter(ef);
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
        if (ev.getAction() == EntityEvent.CREATED
                || ev.getAction() == EntityEvent.MERGED
                || ev.getAction() == EntityEvent.REMOVED) {
            fillTables();
        }
    }


    @Override
    public void doFilter(AbstractFilterBean abstractFilterBean) {
        FStudentFilter sf = (FStudentFilter) abstractFilterBean;
        int i = 1;
        Map<Integer, Object> params = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        if (sf.getCode() != null && sf.getCode().trim().length() >= 2) {
            sb.append(" and x.user_code ilike '");
            sb.append(sf.getCode().trim());
            sb.append("%'");
        }
        if (sf.getFirstname() != null && sf.getFirstname().trim().length() >= 3) {
            sb.append(" and x.FIRST_NAME ilike '");
            sb.append(sf.getFirstname().trim());
            sb.append("%'");
        }
        if (sf.getLastname() != null && sf.getLastname().trim().length() >= 3) {
            sb.append(" and x.LAST_NAME ilike '");
            sb.append(sf.getLastname().trim());
            sb.append("%'");
        }
        if (sf.getCard() != null) {
            params.put(i, sf.getCard().getId().getId());
            sb.append(" and x.card_id = ?");
            sb.append(i++);
        }
        if (sf.getStudentStatus() != null) {
            params.put(i, sf.getStudentStatus().getId().getId());
            sb.append(" and x.student_status_id = ?");
            sb.append(i++);
        }
        if (sf.getFaculty() != null) {
            params.put(i, sf.getFaculty().getId().getId());
            sb.append(" and x.faculty_id = ?");
            sb.append(i++);
        }
        if (sf.getSpeciality() != null) {
            params.put(i, sf.getSpeciality().getId().getId());
            sb.append(" and x.speciality_id = ?");
            sb.append(i++);
        }
        if (sf.getStudyYear() != null) {
            params.put(i, sf.getStudyYear().getId().getId());
            sb.append(" and x.study_year_id = ?");
            sb.append(i++);
        }
        if (sf.getEducationType() != null) {
            params.put(i, sf.getEducationType().getId().getId());
            sb.append(" and x.education_type_id = ?");
            sb.append(i);
        }

        filterUser(sb, params);
    }

    private void filterUser(StringBuilder sb, Map<Integer, Object> params) {
        List<VStudent> Vlist = new ArrayList<>();
        String sql = "SELECT x.id,x.user_code,trim(x.LAST_NAME||' '||x.FIRST_NAME||' '||coalesce(x.MIDDLE_NAME, '')) fio,sc.category_name,\n" +
                "  ss.status_name,d.dept_name,s.spec_name,sy.study_year,l.lang_name\n" +
                "FROM v_student x\n" +
                "  INNER JOIN student_category sc on x.category_id=sc.id\n" +
                "  INNER JOIN student_status ss on x.student_status_id=ss.id\n" +
                "  INNER JOIN department d on x.faculty_id=d.id\n" +
                "  INNER JOIN speciality s on x.speciality_id=s.id\n" +
                "  INNER JOIN study_year sy on x.study_year_id=sy.id\n" +
                "  INNER JOIN student_education se ON x.id = se.student_id\n" +
                "  INNER JOIN language l ON se.language_id = l.id\n" +
                "where x.deleted=false  and x.category_id=1 "
                + sb.toString();
        fillList(Vlist, sql, params);

        BeanItemContainer<VStudent> Vbic = new BeanItemContainer<>(VStudent.class, Vlist);
        studentGrid.setContainerDataSource(Vbic);
    }


    void fillList(List<VStudent> list, String sql, Map<Integer, Object> params) {
        try {
            List tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql,
                    params);
            if (!tmpList.isEmpty()) {
                    for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VStudent sp = new VStudent();
                    sp.setId(ID.valueOf((long)oo[0]));
                    sp.setCode((String) oo[1]);
                    sp.setFio((String) oo[2]);
                    sp.setCategory((String) oo[3]);
                    sp.setStatus((String) oo[4]);
                    sp.setFaculty((String) oo[5]);
                    sp.setSpecialty((String) oo[6]);
                    sp.setStudyYear(oo[7] != null ? ((BigDecimal) oo[7]).intValue() : 0);
                    sp.setLanguageName((String) oo[8]);
                    list.add(sp);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load students' list", ex);
        }

    }

    @Override
    public void clearFilter() {
        refresh();
    }

    public void refresh() {

        List<VStudent> list = new ArrayList<>();
        String sql = "SELECT stu.ID, stu.user_code code, trim(stu.LAST_NAME || ' ' || stu.FIRST_NAME || ' ' || coalesce(stu.MIDDLE_NAME, '')) FIO,\n" +
                "  sc.category_name,\n" +
                "stu.student_status_name STATUS_NAME,\n" +
                "stu.faculty_short_name FACULTY, stu.speciality_name\n" +
                "FROM V_STUDENT stu\n" +
                "  INNER JOIN student_category sc on stu.category_id=sc.id\n" +
                "WHERE stu.category_id = 1 ORDER BY FIO";
        try {
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, new HashMap<>());
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VStudent vs = new VStudent();
                    vs.setId(ID.valueOf((long) oo[0]));
                    vs.setCode((String) oo[1]);
                    vs.setFio((String) oo[2]);
                    vs.setCategory((String)oo[3]);
                    vs.setStatus((String) oo[4]);
                    vs.setFaculty((String) oo[5]);
                    vs.setSpecialty((String) oo[6]);
                    list.add(vs);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load student list", ex);
        }

        final BeanItemContainer<VStudent> bic = new BeanItemContainer<>(VStudent.class, list);
        studentGrid.setContainerDataSource(bic);
    }

    private class reasonDeleted extends AbstractDialog{

        reasonDeleted(){

            setWidth(500, Unit.PIXELS);
            setHeight(300, Unit.PIXELS);
            center();

            TextArea ta = new TextArea();
            ta.setCaption("<html><b>" + getUILocaleUtil().getCaption("reason") + "</b>");
            ta.setWidth(100, Unit.PERCENTAGE);
            ta.setCaptionAsHtml(true);
            getContent().addComponent(ta);

            Button deleteButton = new Button();
            deleteButton.setWidth(120.0F, Unit.PIXELS);
            deleteButton.setIcon(new ThemeResource("img/button/delete.png"));
            deleteButton.setCaption(getUILocaleUtil().getCaption("delete"));
            deleteButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                    Collection<Object> selectedRows = studentGrid.getSelectedRows();
                    for (Object object : selectedRows) {
                        VStudent vStudent = (VStudent) object;
                        USERS user = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                lookup(USERS.class, vStudent.getId());
                        user.setDeleted(true);
                        user.setUpdated(new Date());
                        user.setUpdatedBy(CommonUtils.getCurrentUserLogin());
                        user.setReason(ta.getValue());
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(user);
                    }
                    refresh();
                    close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            getContent().addComponent(deleteButton);
            getContent().setComponentAlignment(deleteButton, Alignment.MIDDLE_CENTER);
        }

        @Override
        protected String createTitle() {
            return null;
        }
    }
}
