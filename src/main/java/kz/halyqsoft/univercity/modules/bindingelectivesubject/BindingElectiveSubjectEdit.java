package kz.halyqsoft.univercity.modules.bindingelectivesubject;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.CATALOG_ELECTIVE_SUBJECTS;
import kz.halyqsoft.univercity.entity.beans.univercity.ELECTIVE_BINDED_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.PAIR_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ENTRANCE_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VPairSubject;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.form.CommonFormWidget;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.table.model.DBTableModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;

import javax.persistence.NoResultException;
import java.math.BigDecimal;
import java.util.*;

public class BindingElectiveSubjectEdit extends AbstractDialog {

    private CommonFormWidget studentBindingElectiveCFW;
    private GridWidget pairSubjectGW;
    private ID studentBindingElectiveId;
    private BindingElectiveSubjectView bindingElectiveSubjectView;
    private ELECTIVE_BINDED_SUBJECT electiveBindedSubject;
    private final boolean isNew;

    BindingElectiveSubjectEdit(ELECTIVE_BINDED_SUBJECT electiveBindedSubject, boolean isNew,
                               BindingElectiveSubjectView electiveSubjectView) throws Exception {
        this.isNew = isNew;
        this.bindingElectiveSubjectView = electiveSubjectView;
        this.electiveBindedSubject = electiveBindedSubject;

        if (electiveBindedSubject != null) {
            studentBindingElectiveId = electiveBindedSubject.getId();
        }

        setWidth(700, Unit.PIXELS);
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
                    pairSubjectGW.setVisible(true);
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

        pairSubjectGW = pairSubjectGridWidget();
        refresh(electiveBindedSubject);
        getContent().addComponent(pairSubjectGW);
        getContent().setComponentAlignment(pairSubjectGW, Alignment.MIDDLE_CENTER);

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

    public void refresh(ELECTIVE_BINDED_SUBJECT electiveBindedSubject) {
        List<VPairSubject> list = new ArrayList<>();
        String sql = "SELECT " +
                "  pair.id," +
                "  subj.name_ru      subjectName, " +
                "  credit.credit, " +
                "  ects.ects, " +
                "  sem.semester_name semesterName, " +
                "  pair.description   description, " +
                "  pair.pair_number    pairNumber  " +
                "FROM pair_subject pair " +
                "  INNER JOIN subject subj ON subj.id = pair.subject_id " +
                "  INNER JOIN creditability credit ON credit.id = subj.creditability_id " +
                "  INNER JOIN ects ects ON ects.id = subj.ects_id " +
                "  INNER JOIN elective_binded_subject elect_bind ON elect_bind.id = pair.elective_binded_subject_id " +
                "  INNER JOIN semester sem ON sem.id = elect_bind.semester_id " +
                "WHERE pair.elective_binded_subject_id = ?1 AND subj.mandatory = FALSE AND subj.subject_cycle_id " +
                "IS NOT NULL";
        Map<Integer, Object> params = new HashMap<>();
        if (electiveBindedSubject != null) {
            params.put(1, electiveBindedSubject.getId().getId());
        } else {
            params.put(1, -1);
        }
        try {
            List tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VPairSubject pairSubject = new VPairSubject();
                    pairSubject.setId(ID.valueOf((long) oo[0]));
                    pairSubject.setSubjectName((String) oo[1]);
                    pairSubject.setCredit(((BigDecimal) oo[2]).intValue());
                    pairSubject.setEcts(((BigDecimal) oo[3]).intValue());
                    pairSubject.setSemesterName((String) oo[4]);
                    pairSubject.setDescription(((String) oo[5]));
                    pairSubject.setPairNumber((Long) oo[6]);
                    list.add(pairSubject);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load subjects table", ex);
        }

        ((DBGridModel) pairSubjectGW.getWidgetModel()).setEntities(list);
        try {
            pairSubjectGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh subjects table", ex);
        }
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
                        CommonEntityFacadeBean.class).lookup(ELECTIVE_BINDED_SUBJECT.class, studentBindingElectiveId);
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

    private GridWidget pairSubjectGridWidget() throws Exception {
        if (studentBindingElectiveId == null && !studentBindingElectiveCFW.getWidgetModel().isCreateNew()) {
            ELECTIVE_BINDED_SUBJECT electiveBindedSubject = (ELECTIVE_BINDED_SUBJECT) studentBindingElectiveCFW.
                    getWidgetModel().getEntity();
            if (electiveBindedSubject != null) {
                studentBindingElectiveId = electiveBindedSubject.getId();
            }
        }

        GridWidget studentElectiveSubjectGW = new GridWidget(VPairSubject.class);
        if (isNew) {
            studentElectiveSubjectGW.setVisible(false);
            studentBindingElectiveId = ID.valueOf(-1);
        }
        studentElectiveSubjectGW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, false);
        studentElectiveSubjectGW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
        studentElectiveSubjectGW.addEntityListener(new StudentCreativeExamSubjectListener());

        DBGridModel studentElectiveSubjectGM = (DBGridModel) studentElectiveSubjectGW.getWidgetModel();
        studentElectiveSubjectGM.setTitleVisible(false);
        studentElectiveSubjectGM.setMultiSelect(false);
        studentElectiveSubjectGM.setRefreshType(ERefreshType.MANUAL);

        return studentElectiveSubjectGW;
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

                    BindingElectiveSubjectEdit.this.electiveBindedSubject=electiveBindedSubject;

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
            return true;
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
                    studentBindingElectiveId = studentCreativeExam.getId();
                    studentCreativeExamSubject.setElectveBindedSubject(studentCreativeExam);
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(studentCreativeExamSubject);

                    QueryModel studentCreativeExamSubjectQM = ((DBTableModel) pairSubjectGW.
                            getWidgetModel()).getQueryModel();
                    studentCreativeExamSubjectQM.addWhere("electiveBindedSubject", ECriteria.EQUAL, fm.getEntity().getId());

                    pairSubjectGW.refresh();
                    CommonUtils.showSavedNotification();
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to create student binding elective subject", ex);
                }
            } else {
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(studentCreativeExamSubject);
                    pairSubjectGW.refresh();
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
                refresh(electiveBindedSubject);
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
            new BindingElectiveSubjectDialog(electiveBindedSubject, BindingElectiveSubjectEdit.this, null, false);
            return false;
        }

        @Override
        public void onCreate(Object o, Entity entity, int i) {
        }

        @Override
        public boolean onEdit(Object o, Entity entity, int i) {
            try {
                PAIR_SUBJECT pairSubject = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                        lookup(PAIR_SUBJECT.class, entity.getId());
                new BindingElectiveSubjectDialog(electiveBindedSubject, BindingElectiveSubjectEdit.this,
                        pairSubject, true);
            } catch (Exception e) {
                e.printStackTrace();//TODO catch
            }
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