package kz.halyqsoft.univercity.modules.bindingelectivesubject;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Alignment;
import kz.halyqsoft.univercity.entity.beans.univercity.CATALOG_ELECTIVE_SUBJECTS;
import kz.halyqsoft.univercity.entity.beans.univercity.ELECTIVE_BINDED_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.PAIR_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ENTRANCE_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import kz.halyqsoft.univercity.utils.CommonUtils;
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
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.form.CommonFormWidget;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.table.TableWidget;
import org.r3a.common.vaadin.widget.table.model.DBTableModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BindingElectiveSubjectEdit extends AbstractDialog {

    private CommonFormWidget studentBindingElectiveCFW;
    private TableWidget studentBindingElectiveSubjectTW;
    private ID studentbindingElectiveId;
    private BindingElectiveSubjectView bindingElectiveSubjectView;
    private final boolean isNew;

    BindingElectiveSubjectEdit(ELECTIVE_BINDED_SUBJECT electiveBindedSubject, boolean isNew, BindingElectiveSubjectView electiveSubjectView) throws Exception {
        this.isNew = isNew;
        this.bindingElectiveSubjectView = electiveSubjectView;

        if (electiveBindedSubject != null) {
            studentbindingElectiveId = electiveBindedSubject.getId();
        }

        setWidth(500, Unit.PIXELS);
        setHeight(500, Unit.PIXELS);
        center();

        studentBindingElectiveCFW = createStudentElectiveBindedSubject();
        getContent().addComponent(studentBindingElectiveCFW);
        getContent().setComponentAlignment(studentBindingElectiveCFW, Alignment.TOP_CENTER);

        Button saveButton = CommonUtils.createSaveButton();
        saveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent ev) {
                if (studentBindingElectiveCFW.getWidgetModel().isModified()) {
                    studentBindingElectiveCFW.save();
                    studentBindingElectiveSubjectTW.setVisible(true);
                }
            }
        });

        Button cancelButton = CommonUtils.createCancelButton();
        cancelButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent ev) {
                studentBindingElectiveCFW.cancel();
            }
        });

        HorizontalLayout buttonsHL = CommonUtils.createButtonPanel();
        buttonsHL.addComponents(saveButton, cancelButton);
        buttonsHL.setComponentAlignment(saveButton, Alignment.MIDDLE_CENTER);
        buttonsHL.setComponentAlignment(cancelButton, Alignment.MIDDLE_CENTER);
        getContent().addComponent(buttonsHL);
        getContent().setComponentAlignment(buttonsHL, Alignment.BOTTOM_CENTER);

        studentBindingElectiveSubjectTW = createStudentElectiveBindedSubjectTable();
        getContent().addComponent(studentBindingElectiveSubjectTW);
        getContent().setComponentAlignment(studentBindingElectiveSubjectTW, Alignment.MIDDLE_CENTER);

        Button closeButton = new Button(getUILocaleUtil().getCaption("close"));
        closeButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                close();
                try {
                    electiveSubjectView.refresh();
                } catch (Exception e) {
                    e.printStackTrace();//TODO catch
                }
            }
        });
        getContent().addComponent(closeButton);
        getContent().setComponentAlignment(closeButton, Alignment.MIDDLE_CENTER);

        AbstractWebUI.getInstance().addWindow(this);
    }

    private CommonFormWidget createStudentElectiveBindedSubject() throws Exception {
        CommonFormWidget studentBindingElectiveFW = new CommonFormWidget(ELECTIVE_BINDED_SUBJECT.class);
        studentBindingElectiveFW.addEntityListener(new StudentCreativeExamListener());
        final FormModel studentBindingElectiveFM = studentBindingElectiveFW.getWidgetModel();
        studentBindingElectiveFM.setReadOnly(false);
        String errorMessage = getUILocaleUtil().getCaption("title.error").concat(": ").
                concat("Binding elective subject");//TODO resource
        studentBindingElectiveFM.setErrorMessageTitle(errorMessage);
        studentBindingElectiveFM.setButtonsVisible(false);

        if (!isNew) {
            studentBindingElectiveFM.getFieldModel("semester").setReadOnly(true);
        }
        QueryModel<SEMESTER> semesterQM = new QueryModel<>(SEMESTER.class);
        semesterQM.addJoin(EJoin.INNER_JOIN, "id", ELECTIVE_BINDED_SUBJECT.class,
                "semester");
        List<SEMESTER> semesters = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookup(semesterQM);
        List<ID> ids = new ArrayList<>();
        for (SEMESTER semester : semesters) {
            ids.add(semester.getId());
        }

        QueryModel studentQM = ((FKFieldModel) studentBindingElectiveFM.getFieldModel("semester")).getQueryModel();

        if (isNew) {
            ELECTIVE_BINDED_SUBJECT studentCreativeExam = (ELECTIVE_BINDED_SUBJECT) studentBindingElectiveFM.createNew();
        } else {
            try {
                ELECTIVE_BINDED_SUBJECT studentCreativeExam = SessionFacadeFactory.getSessionFacade(
                        CommonEntityFacadeBean.class).lookup(ELECTIVE_BINDED_SUBJECT.class, studentbindingElectiveId);
                if (studentCreativeExam != null) {
                    studentBindingElectiveFM.loadEntity(studentCreativeExam.getId());
                }
            } catch (NoResultException ex) {
                ELECTIVE_BINDED_SUBJECT studentCreativeExam = (ELECTIVE_BINDED_SUBJECT) studentBindingElectiveFM.
                        createNew();
            }
        }
        return studentBindingElectiveFW;
    }

    private TableWidget createStudentElectiveBindedSubjectTable() throws Exception {
        if (studentbindingElectiveId == null && !studentBindingElectiveCFW.getWidgetModel().isCreateNew()) {
            ELECTIVE_BINDED_SUBJECT electiveBindedSubject = (ELECTIVE_BINDED_SUBJECT) studentBindingElectiveCFW.
                    getWidgetModel().getEntity();
            if (electiveBindedSubject != null) {
                studentbindingElectiveId = electiveBindedSubject.getId();
            }
        }

        TableWidget studentElectiveSubjectTW = new TableWidget(PAIR_SUBJECT.class);
        if (isNew) {
            studentElectiveSubjectTW.setVisible(false);
        }
        studentElectiveSubjectTW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, false);
        studentElectiveSubjectTW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
        studentElectiveSubjectTW.addEntityListener(new StudentCreativeExamSubjectListener());

        DBTableModel studentCreativeExamSubjectTM = (DBTableModel) studentElectiveSubjectTW.getWidgetModel();
        FKFieldModel fieldModel = (FKFieldModel) studentCreativeExamSubjectTM.getFormModel().getFieldModel("subject");
        fieldModel.getQueryModel().addWhere("mandatory",ECriteria.EQUAL,false);
        fieldModel.getQueryModel().addWhereNotNull("subjectCycle");

        studentCreativeExamSubjectTM.setReadOnly(false);

        if (isNew) {
            studentbindingElectiveId = ID.valueOf(-1);
        }
        QueryModel studentCreativeExamSubjectQM = studentCreativeExamSubjectTM.getQueryModel();
        studentCreativeExamSubjectQM.addWhere("electiveBindedSubject", ECriteria.EQUAL, studentbindingElectiveId);

        refreshSubjects(studentElectiveSubjectTW);
        return studentElectiveSubjectTW;
    }

    private void refreshSubjects(TableWidget studentCreativeExamSubjectTW) throws Exception {
        QueryModel<ELECTIVE_BINDED_SUBJECT> subjectQM = new QueryModel<>(ELECTIVE_BINDED_SUBJECT.class);
        FromItem examFI = subjectQM.addJoin(EJoin.INNER_JOIN, "id", PAIR_SUBJECT.class,
                "subject");
        subjectQM.addWhere(examFI, "electiveBindedSubject", ECriteria.EQUAL, studentbindingElectiveId);
        List<ELECTIVE_BINDED_SUBJECT> writtenSubjects = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookup(subjectQM);
        List<ID> ids = new ArrayList<>();
        for (ELECTIVE_BINDED_SUBJECT  subject : writtenSubjects) {
            ids.add(subject.getId());
        }
        FormModel subjectFM = ((DBTableModel) studentCreativeExamSubjectTW.getWidgetModel()).getFormModel();
        QueryModel electiveSubjectQM = ((FKFieldModel) subjectFM.getFieldModel("subject")).getQueryModel();
        electiveSubjectQM.addWhereNotIn("id", ids);
    }

    private class StudentCreativeExamListener implements EntityListener {

        @Override
        public boolean preSave(Object o, Entity entity, boolean isNew, int i) throws Exception {
            ELECTIVE_BINDED_SUBJECT electiveBindedSubject = (ELECTIVE_BINDED_SUBJECT) entity;
            if (isNew) {
                try {

                    QueryModel<CATALOG_ELECTIVE_SUBJECTS> catQM = new QueryModel<>(CATALOG_ELECTIVE_SUBJECTS.class);
                    SPECIALITY spec = (SPECIALITY) bindingElectiveSubjectView.getSpecCB().getValue();
                    ENTRANCE_YEAR year = (ENTRANCE_YEAR) bindingElectiveSubjectView.getYearCB().getValue();
                    catQM.addWhere("speciality", ECriteria.EQUAL, spec.getId());
                    catQM.addWhere("entranceYear", ECriteria.EQUAL, year.getId());
                    CATALOG_ELECTIVE_SUBJECTS cat = getCat(catQM, spec, year);
                    electiveBindedSubject.setCatalogElectiveSubjects(cat);
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(electiveBindedSubject);
                    studentBindingElectiveCFW.getWidgetModel().loadEntity(electiveBindedSubject.getId());
                    studentBindingElectiveCFW.refresh();
                    CommonUtils.showSavedNotification();

                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to create binding elective subject", ex);
                }
            } else {
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(electiveBindedSubject);
                    CommonUtils.showSavedNotification();
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to merge binding elective subject", ex);
                }
            }
            return false;
        }

        @Override
        public void handleEntityEvent(EntityEvent entityEvent) {
        }

        @Override
        public boolean preCreate(Object o, int i) {
            if (studentBindingElectiveCFW.getWidgetModel().isCreateNew()) {
                Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }
            return  true;
        }

        @Override
        public void onCreate(Object o, Entity entity, int i) {
        }

        @Override
        public boolean onEdit(Object o, Entity entity, int i) {
            return false;
        }

        @Override
        public boolean onPreview(Object o, Entity entity, int i) {
            return false;
        }

        @Override
        public void beforeRefresh(Object o, int i) {
        }

        @Override
        public void onRefresh(Object o, List<Entity> list) {
        }

        @Override
        public void onFilter(Object o, QueryModel queryModel, int i) {
        }

        @Override
        public void onAccept(Object o, List<Entity> list, int i) {
        }

        @Override
        public boolean preDelete(Object o, List<Entity> list, int i) {
            return false;
        }

        @Override
        public void onDelete(Object o, List<Entity> list, int i) {
        }

        @Override
        public void deferredCreate(Object o, Entity entity) {
        }

        @Override
        public void deferredDelete(Object o, List<Entity> list) {
        }

        @Override
        public void onException(Object o, Throwable throwable) {
        }
    }

    private class StudentCreativeExamSubjectListener implements EntityListener {

        @Override
        public boolean preSave(Object o, Entity entity, boolean isNew, int i) throws Exception {
            PAIR_SUBJECT studentCreativeExamSubject = (PAIR_SUBJECT) entity;
            FormModel fm = studentBindingElectiveCFW.getWidgetModel();
            if (isNew) {
                try {
                    ELECTIVE_BINDED_SUBJECT studentCreativeExam = (ELECTIVE_BINDED_SUBJECT) fm.getEntity();
                    studentbindingElectiveId = studentCreativeExam.getId();
                    studentCreativeExamSubject.setElectveBindedSubject(studentCreativeExam);
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(studentCreativeExamSubject);

                    QueryModel studentCreativeExamSubjectQM = ((DBTableModel) studentBindingElectiveSubjectTW.
                            getWidgetModel()).getQueryModel();
                    studentCreativeExamSubjectQM.addWhere("electiveBindedSubject", ECriteria.EQUAL, fm.getEntity().getId());

                    studentBindingElectiveSubjectTW.refresh();
                    refreshSubjects(studentBindingElectiveSubjectTW);
                    CommonUtils.showSavedNotification();
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to create student binding elective subject", ex);
                }
            } else {
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(studentCreativeExamSubject);
                    studentBindingElectiveSubjectTW.refresh();
                    refreshSubjects(studentBindingElectiveSubjectTW);
                    CommonUtils.showSavedNotification();
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to merge student binding elective subject", ex);
                }
            }
            return false;
        }

        @Override
        public boolean preDelete(Object o, List<Entity> entities, int i) {
            List<PAIR_SUBJECT> delList = new ArrayList<>();
            for (Entity entity : entities) {
                try {
                    delList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                            lookup(PAIR_SUBJECT.class, entity.getId()));
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to delete student binding elective subject", ex);
                }
            }
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delList);
                studentBindingElectiveSubjectTW.refresh();
                refreshSubjects(studentBindingElectiveSubjectTW);
            } catch (Exception ex) {
                CommonUtils.LOG.error("Unable to delete student binding elective subject: ", ex);
                Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
            }
            return false;
        }

        @Override
        public void handleEntityEvent(EntityEvent entityEvent) {
        }

        @Override
        public boolean preCreate(Object o, int i) {
            if (studentBindingElectiveCFW.getWidgetModel().isCreateNew()) {
                Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }
            return true;
        }

        @Override
        public void onCreate(Object o, Entity entity, int i) {
        }

        @Override
        public boolean onEdit(Object o, Entity entity, int i) {
            return true;
        }

        @Override
        public boolean onPreview(Object o, Entity entity, int i) {
            return false;
        }

        @Override
        public void beforeRefresh(Object o, int i) {
        }

        @Override
        public void onRefresh(Object o, List<Entity> list) {
        }

        @Override
        public void onFilter(Object o, QueryModel queryModel, int i) {
        }

        @Override
        public void onAccept(Object o, List<Entity> list, int i) {
        }

        @Override
        public void onDelete(Object o, List<Entity> list, int i) {
        }

        @Override
        public void deferredCreate(Object o, Entity entity) {
        }

        @Override
        public void deferredDelete(Object o, List<Entity> list) {
        }

        @Override
        public void onException(Object o, Throwable throwable) {
        }
    }
    private CATALOG_ELECTIVE_SUBJECTS getCat(QueryModel<CATALOG_ELECTIVE_SUBJECTS> catQM,
                                             SPECIALITY spec, ENTRANCE_YEAR year) throws Exception {
        CATALOG_ELECTIVE_SUBJECTS cat;
        try {
            cat = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(catQM);
        } catch (NoResultException ex) {
            cat = new CATALOG_ELECTIVE_SUBJECTS();
            cat.setCreated(new Date());
            cat.setDeleted(Boolean.FALSE);
            cat.setEntranceYear(year);
            cat.setSpeciality(spec);
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(cat);
        }
        return cat;
    }

    @Override
    protected String createTitle() {
        return "Binding elective subject";//TODO
    }
}
