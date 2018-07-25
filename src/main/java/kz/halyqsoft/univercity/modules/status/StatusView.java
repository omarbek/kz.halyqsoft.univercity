package kz.halyqsoft.univercity.modules.status;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_EDUCATION;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_STUDENT;
import kz.halyqsoft.univercity.filter.FStudentFilter;
import kz.halyqsoft.univercity.filter.panel.StudentFilterPanel;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Omarbek
 * @created on 13.07.2018
 */
public class StatusView extends AbstractTaskView implements EntityListener, FilterPanelListener {

    private final StudentFilterPanel filterPanel;
    private GridWidget studentGW;

    public StatusView(AbstractTask task) throws Exception {
        super(task);
        filterPanel = new StudentFilterPanel(new FStudentFilter());
    }


    @Override
    public void initView(boolean readOnly) throws Exception {
        getContent().setSpacing(true);

        filterPanel.addFilterPanelListener(this);
        TextField tf = new TextField();
        tf.setNullRepresentation("");
        tf.setNullSettingAllowed(true);
        filterPanel.addFilterComponent("code", tf);

        ComboBox cb = new ComboBox();
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
        cb.setFilteringMode(FilteringMode.OFF);
        cb.setPageLength(0);
        QueryModel<STUDENT_EDUCATION_TYPE> educationTypeQM = new QueryModel<>(STUDENT_EDUCATION_TYPE.class);
        BeanItemContainer<STUDENT_EDUCATION_TYPE> educationTypeBIC = new BeanItemContainer<>(
                STUDENT_EDUCATION_TYPE.class, SessionFacadeFactory.getSessionFacade(
                CommonEntityFacadeBean.class).lookup(educationTypeQM));
        cb.setContainerDataSource(educationTypeBIC);
        filterPanel.addFilterComponent("educationType", cb);

        getContent().addComponent(filterPanel);
        getContent().setComponentAlignment(filterPanel, Alignment.TOP_CENTER);

        HorizontalLayout buttonsHL = new HorizontalLayout();
        buttonsHL.setSpacing(true);

        Button transferButton = new Button();
        transferButton.setCaption(getUILocaleUtil().getCaption("transfer"));
        transferButton.setWidth(160, Unit.PIXELS);
        transferButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    openStatusEdit(4);
                } catch (Exception e) {
                    CommonUtils.showMessageAndWriteLog("Unable to get students", e);
                }
            }
        });
        buttonsHL.addComponent(transferButton);
        buttonsHL.setComponentAlignment(transferButton, Alignment.MIDDLE_LEFT);

        Button academicLeaveButton = new Button();
        academicLeaveButton.setCaption(getUILocaleUtil().getCaption("academic.leave"));
        academicLeaveButton.setWidth(160, Unit.PIXELS);
        academicLeaveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    openStatusEdit(5);
                } catch (Exception e) {
                    CommonUtils.showMessageAndWriteLog("Unable to get students", e);
                }
            }
        });
        buttonsHL.addComponent(academicLeaveButton);
        buttonsHL.setComponentAlignment(academicLeaveButton, Alignment.MIDDLE_CENTER);

        Button restoreButton = new Button();
        restoreButton.setCaption(getUILocaleUtil().getCaption("restore"));
        restoreButton.setWidth(160, Unit.PIXELS);
        restoreButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    openStatusEdit(1);
                } catch (Exception e) {
                    CommonUtils.showMessageAndWriteLog("Unable to get students", e);
                }
            }
        });
        buttonsHL.addComponent(restoreButton);
        buttonsHL.setComponentAlignment(restoreButton, Alignment.MIDDLE_RIGHT);

        Button alumnusButton = new Button();
        alumnusButton.setCaption(getUILocaleUtil().getCaption("alumnus"));
        alumnusButton.setWidth(160, Unit.PIXELS);
        alumnusButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    openStatusEdit(2);
                } catch (Exception e) {
                    CommonUtils.showMessageAndWriteLog("Unable to get students", e);
                }
            }
        });
        buttonsHL.addComponent(alumnusButton);
        buttonsHL.setComponentAlignment(alumnusButton, Alignment.BOTTOM_LEFT);

        Button deductButton = new Button();
        deductButton.setCaption(getUILocaleUtil().getCaption("deduct"));
        deductButton.setWidth(160, Unit.PIXELS);
        deductButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    openStatusEdit(3);
                } catch (Exception e) {
                    CommonUtils.showMessageAndWriteLog("Unable to get students", e);
                }
            }
        });
        buttonsHL.addComponent(deductButton);
        buttonsHL.setComponentAlignment(deductButton, Alignment.BOTTOM_CENTER);

        Button otherOrderButton = new Button();
        otherOrderButton.setCaption(getUILocaleUtil().getCaption("other"));
        otherOrderButton.setWidth(160, Unit.PIXELS);
        otherOrderButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    openStatusEdit(null);
                } catch (Exception e) {
                    CommonUtils.showMessageAndWriteLog("Unable to get students", e);
                }
            }
        });
        buttonsHL.addComponent(otherOrderButton);
        buttonsHL.setComponentAlignment(otherOrderButton, Alignment.BOTTOM_RIGHT);

        getContent().addComponent(buttonsHL);
        getContent().setComponentAlignment(buttonsHL, Alignment.BOTTOM_CENTER);

        studentGW = new GridWidget(V_STUDENT.class);
        studentGW.addEntityListener(this);
        studentGW.showToolbar(false);

        DBGridModel gm = (DBGridModel) studentGW.getWidgetModel();
        gm.setTitleVisible(false);
        gm.setMultiSelect(true);
        gm.setRowNumberVisible(true);
        gm.setRowNumberWidth(50);

        FStudentFilter sf = (FStudentFilter) filterPanel.getFilterBean();
        if (sf.hasFilter()) {
            doFilter(sf);
        }

        getContent().addComponent(studentGW);
        getContent().setComponentAlignment(studentGW, Alignment.MIDDLE_CENTER);
    }

    @Override
    public void doFilter(AbstractFilterBean filterBean) {
        FStudentFilter sf = (FStudentFilter) filterBean;
        try {
            boolean condition = false;
            QueryModel studentQM = ((DBGridModel) studentGW.getWidgetModel()).getQueryModel();
            if (sf.getFaculty() != null) {
                studentQM.addWhere("faculty", ECriteria.EQUAL, sf.getFaculty().getId());
                condition = true;
            } else {
                studentQM.addWhere("faculty", ECriteria.EQUAL, null);
            }

            if (sf.getCode() != null) {
                studentQM.addWhere("userCode", ECriteria.LEFT_LIKE, sf.getCode());
                condition = true;
            } else {
                studentQM.addWhere("userCode", ECriteria.LEFT_LIKE, null);
            }

            if (sf.getSpeciality() != null) {
                studentQM.addWhere("speciality", ECriteria.EQUAL, sf.getSpeciality().getId());
                condition = true;
            } else {
                studentQM.addWhere("speciality", ECriteria.EQUAL, null);
            }

            if (sf.getStudentStatus() != null) {
                studentQM.addWhere("studentStatus", ECriteria.EQUAL, sf.getStudentStatus().getId());
                condition = true;
            } else {
                studentQM.addWhere("studentStatus", ECriteria.EQUAL, null);
            }

            if (sf.getStudyYear() != null) {
                studentQM.addWhere("studyYear", ECriteria.EQUAL, sf.getStudyYear().getId());
                condition = true;
            } else {
                studentQM.addWhere("studyYear", ECriteria.EQUAL, null);
            }

            if (sf.getEducationType() != null) {
                studentQM.addWhere("educationType", ECriteria.EQUAL, sf.getEducationType().getId());
                condition = true;
            } else {
                studentQM.addWhere("educationType", ECriteria.EQUAL, null);
            }

            if (condition) {
                studentGW.refresh();
            } else {
                Message.showInfo(getUILocaleUtil().getMessage("select.1.search.condition"));
            }
        } catch (Exception ex) {
            LOG.error("Unable to refresh students grid: ", ex);
        }
    }

    @Override
    public void clearFilter() {
        QueryModel studentQM = ((DBGridModel) studentGW.getWidgetModel()).getQueryModel();
        studentQM.addWhere("faculty", ECriteria.EQUAL, ID.valueOf(-1));
        try {
            studentGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh students grid", ex);
        }
    }

    private void openStatusEdit(Integer i) {
        try {
            STUDENT_STATUS ss = null;
            if (i != null)
                ss = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(STUDENT_STATUS.class, ID.valueOf(i));
            List<Entity> entityList = studentGW.getSelectedEntities();
            List<STUDENT> studentList = new ArrayList<>();
            String a = "";
            for (Entity entity : entityList) {
                if (entity != null) {
                    QueryModel<STUDENT_EDUCATION> seQM = new QueryModel<>(STUDENT_EDUCATION.class);
                    seQM.addWhere("student", ECriteria.EQUAL, entity.getId());
                    seQM.addWhereNullAnd("child");
                    studentList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                            STUDENT.class, entity.getId()));
                    STUDENT_EDUCATION studentEducation = SessionFacadeFactory.getSessionFacade(
                            CommonEntityFacadeBean.class).lookupSingle(seQM);
                    ID statusId = studentEducation.getStatus().getId();
                    if (i != null
                            && ((i == 5 || i == 4 || i == 3)
                            && (statusId.equals(ID.valueOf(3)) || statusId.equals(ID.valueOf(2)))
                            || (i == 1 && (statusId.equals(ID.valueOf(1)) || statusId.equals(ID.valueOf(2))))
                            || (i == 2 && !statusId.equals(ID.valueOf(1))))) {
                        a += "a";
                    }
                } else {
                    LOG.error("Nothing!");
                }
            }
            FormModel fm = new FormModel(STUDENT_EDUCATION.class);
            fm.setReadOnly(false);
            fm.setTitleVisible(false);
            if (a.isEmpty()) {
                StatusEdit statusEdit = new StatusEdit(studentList,ss);
                new StatusDialog(statusEdit, this);
            } else {
                switch (i) {
                    case 1:
                        Message.showInfo(getUILocaleUtil().getMessage("unable.to.restore"));
                        break;
                    case 2:
                        Message.showInfo(getUILocaleUtil().getMessage("unable.to.alumnus"));
                        break;
                    case 3:
                        Message.showInfo(getUILocaleUtil().getMessage("unable.to.deduct"));
                        break;
                    case 4:
                        Message.showInfo(getUILocaleUtil().getMessage("unable.to.transfer"));
                        break;
                    case 5:
                        Message.showInfo(getUILocaleUtil().getMessage("unable.to.give.academic"));
                        break;
                }
            }
        } catch (Exception e) {
            LOG.error("Unable to get students: ", e);
            e.printStackTrace();
        }
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
    public boolean preCreate(Object source, int buttonId) {
        return false;
    }

    @Override
    public void onCreate(Object source, Entity e, int buttonId) {
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

    public void refresh() {
        FStudentFilter studentFilter = (FStudentFilter) filterPanel.getFilterBean();
        if (studentFilter.hasFilter()) {
            doFilter(studentFilter);
        }
    }

    private class FacultyChangeListener implements Property.ValueChangeListener {

        private final ComboBox specialtyCB;

        FacultyChangeListener(ComboBox specialtyCB) {
            this.specialtyCB = specialtyCB;
        }

        @Override
        public void valueChange(Property.ValueChangeEvent ev) {
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
                    CommonUtils.showMessageAndWriteLog("Unable to load specialty list", ex);
                }
            }

            BeanItemContainer<SPECIALITY> specialtyBIC = new BeanItemContainer<>(SPECIALITY.class, list);
            specialtyCB.setContainerDataSource(specialtyBIC);
        }
    }
}
