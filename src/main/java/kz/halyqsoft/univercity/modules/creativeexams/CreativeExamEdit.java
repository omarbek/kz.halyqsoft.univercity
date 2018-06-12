package kz.halyqsoft.univercity.modules.creativeexams;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_CREATIVE_EXAM;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_CREATIVE_EXAM_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.CREATIVE_EXAM_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VCreativeExam;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_STUDENT;
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
import java.util.List;

/**
 * @author Omarbek
 * @created on 23.04.2018
 */
public class CreativeExamEdit extends AbstractDialog {

    private CommonFormWidget studentCreativeExamCFW;
    private TableWidget studentCreativeExamSubjectTW;
    private ID studentCreativeExamId;

    private final boolean isNew;

    CreativeExamEdit(VCreativeExam creativeExam, boolean isNew, CreativeExamView creativeExamView) throws Exception {
        this.isNew = isNew;

        if (creativeExam != null) {
            studentCreativeExamId = creativeExam.getId();
        }

        setWidth(500, Unit.PIXELS);
        setHeight(500, Unit.PIXELS);
        center();

        studentCreativeExamCFW = createStudentCreativeExam();
        getContent().addComponent(studentCreativeExamCFW);
        getContent().setComponentAlignment(studentCreativeExamCFW, Alignment.TOP_CENTER);

        Button saveButton = CommonUtils.createSaveButton();
        saveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent ev) {
                if (studentCreativeExamCFW.getWidgetModel().isModified()) {
                    studentCreativeExamCFW.save();
                    studentCreativeExamSubjectTW.setVisible(true);

                }
            }
        });

        Button cancelButton = CommonUtils.createCancelButton();
        cancelButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent ev) {
                studentCreativeExamCFW.cancel();
            }
        });

        HorizontalLayout buttonsHL = CommonUtils.createButtonPanel();
        buttonsHL.addComponents(saveButton, cancelButton);
        buttonsHL.setComponentAlignment(saveButton, Alignment.MIDDLE_CENTER);
        buttonsHL.setComponentAlignment(cancelButton, Alignment.MIDDLE_CENTER);
        getContent().addComponent(buttonsHL);
        getContent().setComponentAlignment(buttonsHL, Alignment.BOTTOM_CENTER);

        studentCreativeExamSubjectTW = createStudentCreativeExamSubject();
        getContent().addComponent(studentCreativeExamSubjectTW);
        getContent().setComponentAlignment(studentCreativeExamSubjectTW, Alignment.MIDDLE_CENTER);

        Button closeButton = new Button(getUILocaleUtil().getCaption("close"));
        closeButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                close();
                try {
                    creativeExamView.refresh();
                } catch (Exception e) {
                    e.printStackTrace();//TODO catch
                }
            }
        });
        getContent().addComponent(closeButton);
        getContent().setComponentAlignment(closeButton, Alignment.MIDDLE_CENTER);

        AbstractWebUI.getInstance().addWindow(this);
    }

    private CommonFormWidget createStudentCreativeExam() throws Exception {
        CommonFormWidget studentCreativeExamFW = new CommonFormWidget(STUDENT_CREATIVE_EXAM.class);
        studentCreativeExamFW.addEntityListener(new StudentCreativeExamListener());
        final FormModel studentCreativeExamFM = studentCreativeExamFW.getWidgetModel();
        studentCreativeExamFM.setReadOnly(false);
        String errorMessage = getUILocaleUtil().getCaption("title.error").concat(": ").
                concat("student creative exam");//TODO resource
        studentCreativeExamFM.setErrorMessageTitle(errorMessage);
        studentCreativeExamFM.setButtonsVisible(false);

        if (!isNew) {
            studentCreativeExamFM.getFieldModel("student").setReadOnly(true);
        }
        QueryModel<V_STUDENT> studentExamQM = new QueryModel<>(V_STUDENT.class);
        studentExamQM.addJoin(EJoin.INNER_JOIN, "id", STUDENT_CREATIVE_EXAM.class,
                "student");
        List<V_STUDENT> students = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookup(studentExamQM);
        List<ID> ids = new ArrayList<>();
        for (V_STUDENT student : students) {
            ids.add(student.getId());
        }

        QueryModel studentQM = ((FKFieldModel) studentCreativeExamFM.getFieldModel("student")).getQueryModel();
        studentQM.addWhereAnd("category", ECriteria.EQUAL, ID.valueOf(1));
        studentQM.addWhereNotInAnd("id", ids);

        if (isNew) {
            STUDENT_CREATIVE_EXAM studentCreativeExam = (STUDENT_CREATIVE_EXAM) studentCreativeExamFM.createNew();
            studentCreativeExam.setRate(0);
        } else {
            try {
                STUDENT_CREATIVE_EXAM studentCreativeExam = SessionFacadeFactory.getSessionFacade(
                        CommonEntityFacadeBean.class).lookup(STUDENT_CREATIVE_EXAM.class, studentCreativeExamId);
                if (studentCreativeExam != null) {
                    studentCreativeExamFM.loadEntity(studentCreativeExam.getId());
                }
            } catch (NoResultException ex) {
                STUDENT_CREATIVE_EXAM studentCreativeExam = (STUDENT_CREATIVE_EXAM) studentCreativeExamFM.
                        createNew();
                studentCreativeExam.setRate(0);
            }
        }
        return studentCreativeExamFW;
    }

    private TableWidget createStudentCreativeExamSubject() throws Exception {
        if (studentCreativeExamId == null && !studentCreativeExamCFW.getWidgetModel().isCreateNew()) {
            STUDENT_CREATIVE_EXAM studentCreativeExam = (STUDENT_CREATIVE_EXAM) studentCreativeExamCFW.
                    getWidgetModel().getEntity();
            if (studentCreativeExam != null) {
                studentCreativeExamId = studentCreativeExam.getId();
            }
        }

        TableWidget studentCreativeExamSubjectTW = new TableWidget(STUDENT_CREATIVE_EXAM_SUBJECT.class);
        if (isNew) {
            studentCreativeExamSubjectTW.setVisible(false);
        }
        studentCreativeExamSubjectTW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, false);
        studentCreativeExamSubjectTW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
        studentCreativeExamSubjectTW.addEntityListener(new StudentCreativeExamSubjectListener());
        DBTableModel studentCreativeExamSubjectTM = (DBTableModel) studentCreativeExamSubjectTW.getWidgetModel();

        studentCreativeExamSubjectTM.setReadOnly(false);
        QueryModel studentCreativeExamSubjectQM = studentCreativeExamSubjectTM.getQueryModel();
        if (isNew) {
            studentCreativeExamId = ID.valueOf(-1);
        }
        studentCreativeExamSubjectQM.addWhere("studentCreativeExam", ECriteria.EQUAL, studentCreativeExamId);

        refreshSubjects(studentCreativeExamSubjectTW);
        return studentCreativeExamSubjectTW;
    }

    private void refreshSubjects(TableWidget studentCreativeExamSubjectTW) throws Exception {
        QueryModel<CREATIVE_EXAM_SUBJECT> subjectQM = new QueryModel<>(CREATIVE_EXAM_SUBJECT.class);
        FromItem examFI = subjectQM.addJoin(EJoin.INNER_JOIN, "id", STUDENT_CREATIVE_EXAM_SUBJECT.class,
                "creativeExamSubject");
        subjectQM.addWhere(examFI, "studentCreativeExam", ECriteria.EQUAL, studentCreativeExamId);
        List<CREATIVE_EXAM_SUBJECT> writtenSubjects = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookup(subjectQM);
        List<ID> ids = new ArrayList<>();
        for (CREATIVE_EXAM_SUBJECT subject : writtenSubjects) {
            ids.add(subject.getId());
        }

        FormModel examSubjectFM = ((DBTableModel) studentCreativeExamSubjectTW.getWidgetModel()).getFormModel();
        QueryModel examSubjectQM = ((FKFieldModel) examSubjectFM.getFieldModel("creativeExamSubject")).getQueryModel();
        examSubjectQM.addWhereNotIn("id", ids);
    }

    private class StudentCreativeExamListener implements EntityListener {

        @Override
        public boolean preSave(Object o, Entity entity, boolean isNew, int i) throws Exception {
            STUDENT_CREATIVE_EXAM studentCreativeExam = (STUDENT_CREATIVE_EXAM) entity;
            if (isNew) {
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(studentCreativeExam);
                    studentCreativeExamCFW.getWidgetModel().loadEntity(studentCreativeExam.getId());
                    studentCreativeExamCFW.refresh();
                    CommonUtils.showSavedNotification();
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to create student creative exam", ex);
                }
            } else {
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(studentCreativeExam);
                    CommonUtils.showSavedNotification();
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to merge student creative exam", ex);
                }
            }

            return false;
        }

        @Override
        public void handleEntityEvent(EntityEvent entityEvent) {
        }

        @Override
        public boolean preCreate(Object o, int i) {
            return false;
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
            STUDENT_CREATIVE_EXAM_SUBJECT studentCreativeExamSubject = (STUDENT_CREATIVE_EXAM_SUBJECT) entity;
            FormModel fm = studentCreativeExamCFW.getWidgetModel();
            if (isNew) {
                try {
                    STUDENT_CREATIVE_EXAM studentCreativeExam = (STUDENT_CREATIVE_EXAM) fm.getEntity();
                    studentCreativeExamId = studentCreativeExam.getId();
                    studentCreativeExamSubject.setStudentCreativeExam(studentCreativeExam);
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(studentCreativeExamSubject);

                    QueryModel studentCreativeExamSubjectQM = ((DBTableModel) studentCreativeExamSubjectTW.
                            getWidgetModel()).getQueryModel();
                    studentCreativeExamSubjectQM.addWhere("studentCreativeExam", ECriteria.EQUAL, fm.getEntity().getId());

                    studentCreativeExamSubjectTW.refresh();
                    refreshSubjects(studentCreativeExamSubjectTW);
                    CommonUtils.showSavedNotification();
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to create student creative exam subject", ex);
                }
            } else {
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(studentCreativeExamSubject);
                    studentCreativeExamSubjectTW.refresh();
                    refreshSubjects(studentCreativeExamSubjectTW);
                    CommonUtils.showSavedNotification();
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to merge student creative exam subject", ex);
                }
            }
            return false;
        }

        @Override
        public boolean preDelete(Object o, List<Entity> entities, int i) {
            List<STUDENT_CREATIVE_EXAM_SUBJECT> delList = new ArrayList<>();
            for (Entity entity : entities) {
                try {
                    delList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                            lookup(STUDENT_CREATIVE_EXAM_SUBJECT.class, entity.getId()));
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to delete student creative exam subject", ex);
                }
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delList);
                studentCreativeExamSubjectTW.refresh();
                refreshSubjects(studentCreativeExamSubjectTW);
            } catch (Exception ex) {
                CommonUtils.LOG.error("Unable to delete student creative exam subject: ", ex);
                Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
            }

            return false;
        }

        @Override
        public void handleEntityEvent(EntityEvent entityEvent) {
            if (entityEvent.getAction() == EntityEvent.REFRESHED) {
                STUDENT_CREATIVE_EXAM studentCreativeExam = null;
                try {
                    studentCreativeExam = (STUDENT_CREATIVE_EXAM) studentCreativeExamCFW.getWidgetModel().getEntity();
                } catch (Exception e) {
                    e.printStackTrace();//TODO catch
                }
                if (studentCreativeExam != null && studentCreativeExam.getId() != null) {
                    int rate = 0;
                    List<Entity> studentCreativeExamSubjects = studentCreativeExamSubjectTW.getAllEntities();
                    for (Entity e : studentCreativeExamSubjects) {
                        rate += ((STUDENT_CREATIVE_EXAM_SUBJECT) e).getRate();
                    }
                    studentCreativeExam.setRate(rate);
                    try {
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(studentCreativeExam);
                        studentCreativeExamCFW.getWidgetModel().getFieldModel("rate").getField().setValue(
                                String.valueOf(rate));
                    } catch (Exception ex) {
                        CommonUtils.showMessageAndWriteLog("Unable to refresh rates", ex);
                    }
                }
            }
        }

        @Override
        public boolean preCreate(Object o, int i) {
            if (studentCreativeExamCFW.getWidgetModel().isCreateNew()) {
                Message.showInfo(getUILocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }
            if (studentCreativeExamSubjectTW.getEntityCount() > 1) {
                Message.showInfo(getUILocaleUtil().getMessage("count.of.creative.subjects.cannot.be.more.than.2"));
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

    @Override
    protected String createTitle() {
        return "Creative Exam";//TODO
    }
}
