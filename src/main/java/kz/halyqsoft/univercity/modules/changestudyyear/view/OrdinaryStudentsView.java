package kz.halyqsoft.univercity.modules.changestudyyear.view;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Sizeable;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.GROUPS;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_EDUCATION;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDENT_CATEGORY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDENT_STATUS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDY_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VChangeToNextYearStudent;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_GROUP;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_STUDENT;
import kz.halyqsoft.univercity.filter.FChangeToNextYearGroupFilter;
import kz.halyqsoft.univercity.filter.panel.ChangeToNextYearGroupFilterPanel;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;

import java.util.*;

import static kz.halyqsoft.univercity.utils.CommonUtils.getUILocaleUtil;

public class OrdinaryStudentsView implements FilterPanelListener {

    private GridWidget studentGW;
    public ChangeToNextYearGroupFilterPanel changeToNextYearGroupFilterPanel;
    private DBGridModel studentGM;
    private List<STUDY_YEAR> studyYears = new ArrayList<>();
    private List<STUDENT_STATUS> studentStatuses = new ArrayList<>();
    private VerticalLayout mainVL;

    public OrdinaryStudentsView() {
        mainVL = new VerticalLayout();
        try {
            initView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public VerticalLayout getContent() {
        return mainVL;
    }

    public void initView() throws Exception {

        studentStatuses.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(new QueryModel<>(STUDENT_STATUS.class)));

        initFilter();
        initGridWidget();

        getContent().addComponent(changeToNextYearGroupFilterPanel);
        Button changeStudyYearBtn = new Button(getUILocaleUtil().getCaption("change.to.next.study.year"));
        changeStudyYearBtn.setImmediate(true);
        changeStudyYearBtn.setWidth(300, Sizeable.Unit.PIXELS);
        changeStudyYearBtn.setIcon(new ThemeResource("img/button/ok.png"));
        changeStudyYearBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (studentGW.getSelectedEntities().size() > 0) {
                    List<ID> ids = new ArrayList<>();
                    for (Entity entity : studentGW.getSelectedEntities()) {
                        ids.add(entity.getId());
                    }

                    QueryModel<STUDENT_EDUCATION> studentEducationQM = new QueryModel<>(STUDENT_EDUCATION.class);
                    studentEducationQM.addWhereNull("child");
                    studentEducationQM.addWhereInAnd("student", ids);
                    studentEducationQM.addWhereNotNullAnd("studyYear");

                    List<STUDENT_EDUCATION> studentEducations = new ArrayList<>();

                    try {
                        studentEducations.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(studentEducationQM));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    STUDENT_STATUS graduated = null;
                    try {
                        graduated = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(STUDENT_STATUS.class, STUDENT_STATUS.GRADUATED_ID);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Set<GROUPS> groupsSet = new HashSet<>();

                    for (STUDENT_EDUCATION se : studentEducations) {
                        STUDY_YEAR studyYear = calculateStudyYear(se.getStudyYear());
                        if (se.getStudyYear().getStudyYear() == se.getGroups().getStudyYear().getStudyYear()) {
                            groupsSet.add(se.getGroups());
                        }
                        if (studyYear != null) {

                            STUDENT_EDUCATION newSE = new STUDENT_EDUCATION();
                            newSE.setCreated(new Date());
                            newSE.setChair(se.getChair());
                            newSE.setEducationType(se.getEducationType());
                            newSE.setFaculty(se.getFaculty());
                            newSE.setGroups(se.getGroups());
                            newSE.setEntryDate(se.getEntryDate());
                            newSE.setLanguage(se.getLanguage());
                            newSE.setStudentSchedules(se.getStudentSchedules());
                            newSE.setStudent(se.getStudent());
                            newSE.setStudyYear(studyYear);
                            newSE.setSpeciality(se.getSpeciality());
                            newSE.setStatus(se.getStatus());

                            try {
                                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(newSE);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            se.setChild(newSE);
                            se.setUpdated(new Date());
                            se.setStatus(getStudentStatusById(STUDENT_STATUS.CHANGED_TO_NEXT_YEAR_ID));

                        } else {
                            se.setStatus(graduated);
                            se.setEndDate(new Date());
                        }
                    }

                    for (GROUPS group : groupsSet) {
                        STUDY_YEAR studyYear = calculateStudyYear(group.getStudyYear());
                        group.setStudyYear(studyYear);
                    }

                    try {
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(studentEducations);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (groupsSet.size() > 0) {
                        try {
                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(new ArrayList<>(groupsSet));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    doFilter(changeToNextYearGroupFilterPanel.getFilterBean());
                    CommonUtils.showSavedNotification();
                } else {
                    Message.showError(getUILocaleUtil().getCaption("chooseARecord"));
                }
            }
        });
        getContent().addComponent(changeStudyYearBtn);
        getContent().setComponentAlignment(changeStudyYearBtn, Alignment.MIDDLE_LEFT);
        getContent().addComponent(studentGW);

        doFilter(changeToNextYearGroupFilterPanel.getFilterBean());
    }

    public STUDENT_STATUS getStudentStatusById(ID id) {
        for (STUDENT_STATUS ss : studentStatuses) {
            if (ss.getId().getId().longValue() == id.getId().longValue()) {
                return ss;
            }
        }
        return null;
    }

    public STUDY_YEAR calculateStudyYear(STUDY_YEAR studyYear) {
        for (int i = 0; i < studyYears.size(); i++) {
            if (studyYears.size() > i + 1) {
                if (studyYear.getStudyYear() == studyYears.get(i).getStudyYear()) {
                    return studyYears.get(i + 1);
                }
            }
        }

        return null;
    }

    public void initGridWidget() {
        studentGW = new GridWidget(VChangeToNextYearStudent.class);
        studentGW.removeEntityListener(studentGW);
        studentGW.setImmediate(true);
        studentGW.setSizeFull();
        studentGW.setMultiSelect(true);
        studentGW.showToolbar(false);

        studentGM = (DBGridModel) studentGW.getWidgetModel();
        studentGM.setRefreshType(ERefreshType.MANUAL);
    }

    public void initFilter() throws Exception {
        changeToNextYearGroupFilterPanel = new ChangeToNextYearGroupFilterPanel(new FChangeToNextYearGroupFilter());
        changeToNextYearGroupFilterPanel.addFilterPanelListener(this);
        changeToNextYearGroupFilterPanel.setImmediate(true);

        ComboBox specialityComboBox = new ComboBox();
        specialityComboBox.setNullSelectionAllowed(true);
        specialityComboBox.setTextInputAllowed(true);
        specialityComboBox.setFilteringMode(FilteringMode.CONTAINS);
        specialityComboBox.setWidth(300, Sizeable.Unit.PIXELS);
        QueryModel<SPECIALITY> specialityQM = new QueryModel<>(SPECIALITY.class);
        specialityQM.addJoin(EJoin.INNER_JOIN, "id", V_STUDENT.class, "speciality");
        BeanItemContainer<SPECIALITY> specialityBIC = new BeanItemContainer<>(SPECIALITY.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(specialityQM));
        specialityComboBox.setContainerDataSource(specialityBIC);
        changeToNextYearGroupFilterPanel.addFilterComponent("speciality", specialityComboBox);


        ComboBox groupNameCB = new ComboBox();
        groupNameCB.setNullSelectionAllowed(true);
        groupNameCB.setTextInputAllowed(true);
        groupNameCB.setFilteringMode(FilteringMode.CONTAINS);
        groupNameCB.setWidth(300, Sizeable.Unit.PIXELS);
        QueryModel<V_GROUP> groupsQM = new QueryModel<>(V_GROUP.class);
        BeanItemContainer<V_GROUP> groupBIC = new BeanItemContainer<V_GROUP>(V_GROUP.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(groupsQM));
        groupNameCB.setContainerDataSource(groupBIC);
        changeToNextYearGroupFilterPanel.addFilterComponent("group", groupNameCB);

        ComboBox studyYearCB = new ComboBox();
        studyYearCB.setNullSelectionAllowed(true);
        studyYearCB.setTextInputAllowed(true);
        studyYearCB.setFilteringMode(FilteringMode.CONTAINS);
        studyYearCB.setWidth(300, Sizeable.Unit.PIXELS);
        QueryModel<STUDY_YEAR> studyYearQM = new QueryModel<>(STUDY_YEAR.class);
        BeanItemContainer<STUDY_YEAR> studyYearBIC = new BeanItemContainer<>(STUDY_YEAR.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(studyYearQM));
        studyYearCB.setContainerDataSource(studyYearBIC);

        for (Object o : studyYearCB.getItemIds()) {
            studyYears.add((STUDY_YEAR) o);
        }

        for (int i = 0; i < studyYears.size(); i++) {
            for (int j = 0; j < studyYears.size(); j++) {
                if (studyYears.get(i).getStudyYear() < studyYears.get(j).getStudyYear()) {
                    Collections.swap(studyYears, i, j);
                }
            }
        }

        changeToNextYearGroupFilterPanel.addFilterComponent("studyYear", studyYearCB);
    }

    @Override
    public void doFilter(AbstractFilterBean abstractFilterBean) {
        FChangeToNextYearGroupFilter fctngf = (FChangeToNextYearGroupFilter) abstractFilterBean;
        Map<Integer, Object> params = new HashMap<>();
        int i = 1;
        StringBuilder sb = new StringBuilder();
        if (fctngf.getGroup() != null) {
            sb.append(" and ");
            params.put(i, fctngf.getGroup().getId().getId());
            sb.append(" vs.groups_id = ?" + i++);

        }

        if (fctngf.getSpeciality() != null) {
            sb.append(" and ");
            params.put(i, fctngf.getSpeciality().getId().getId());
            sb.append(" vs.speciality_id = ?" + i++);

        }

        if (fctngf.getStudyYear() != null) {

            sb.append(" and ");

            params.put(i, fctngf.getStudyYear().getId().getId());
            sb.append(" vs.study_year_id = ?" + i++);

        }
        List<VChangeToNextYearStudent> list = new ArrayList<>();

        sb.insert(0, " where se.child_id isnull and vs.category_id = " + STUDENT_CATEGORY.STUDENT_ID.getId() + " and vs.student_status_id in ( " + STUDENT_STATUS.STUDYING_ID.getId() + " ," + STUDENT_STATUS.DEDUCTED_ID + ") and vs.study_year_id NOTNULL and vs.groups_id NOTNULL ");
        String sql = "SELECT vs.id , vs.first_name , vs.middle_name,  vs.last_name , vs.faculty_name , vs.speciality_name , vs.study_year_id ,vs.group_name , l.lang_name ,vs.student_status_name " +
                " from v_student  vs " +
                " inner join student_education se on vs.id = se.student_id " +
                " inner join language l on l.id = se.language_id "
                + sb.toString();
        try {

            List tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VChangeToNextYearStudent vStudent = new VChangeToNextYearStudent();
                    vStudent.setId(ID.valueOf((long) oo[0]));
                    vStudent.setFio(oo[1] + " " + ((oo[2] != null) ? (String) oo[2] : "") + " " + oo[3]);
                    vStudent.setFacultyName((String) oo[4]);
                    vStudent.setSpeciality((String) oo[5]);
                    vStudent.setStudyYear(((Long) oo[6]).intValue());
                    vStudent.setGroupName((String) oo[7]);
                    vStudent.setLanguageName((String) oo[8]);
                    vStudent.setStatus((String) oo[9]);

                    list.add(vStudent);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load student list", ex);
        }

        refresh(list);
    }

    @Override
    public void clearFilter() {
        doFilter(changeToNextYearGroupFilterPanel.getFilterBean());
    }

    private void refresh(List<VChangeToNextYearStudent> list) {
        studentGM.setEntities(list);
        try {
            studentGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh student list", ex);
        }
    }


}
