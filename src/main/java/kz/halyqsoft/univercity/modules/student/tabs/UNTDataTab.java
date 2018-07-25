package kz.halyqsoft.univercity.modules.student.tabs;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_UNT_CERT_SUBJECT;
import kz.halyqsoft.univercity.modules.student.StudentEdit;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.facade.CommonIDFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.file.FileBean;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.form.CommonFormWidget;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.field.filelist.FileListFieldModel;
import org.r3a.common.vaadin.widget.table.TableWidget;
import org.r3a.common.vaadin.widget.table.model.DBTableModel;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Omarbek
 * @created 17.05.2017.
 */
@SuppressWarnings({"serial", "unchecked"})
public class UNTDataTab extends VerticalLayout {

    private static final String USER_DOCUMENT_SEQUENCE = "S_USER_DOCUMENT";

    private final StudentEdit.StudentEditHelper studentEditHelper;

    private final boolean readOnly;

    private CommonFormWidget untCertificateFW;

    private TableWidget untRatesTW;

    public UNTDataTab(STUDENT student, StudentEdit.StudentEditHelper studentEditHelper, boolean readOnly) throws Exception {
        this.studentEditHelper = studentEditHelper;
        this.readOnly = readOnly;
        setSpacing(true);
        setSizeFull();

        untCertificateFW = createUntCertificate();
        addComponent(untCertificateFW);
        setComponentAlignment(untCertificateFW, Alignment.TOP_CENTER);

        untRatesTW = createUntRates();
        addComponent(untRatesTW);
        setComponentAlignment(untRatesTW, Alignment.MIDDLE_CENTER);

        if (!readOnly) {
            Button save = studentEditHelper.createSaveButton();
            save.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent ev) {
                    if (untCertificateFW.getWidgetModel().isModified()) {
                        untCertificateFW.save();
                    }
                    try {
                        StudentEdit.studentEditPdfDownload(student);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            Button cancel = studentEditHelper.createCancelButton();
            cancel.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent ev) {
                    untCertificateFW.cancel();
                }
            });

            HorizontalLayout buttonPanel = studentEditHelper.createButtonPanel();
            buttonPanel.addComponents(save, cancel);
            buttonPanel.setComponentAlignment(save, Alignment.MIDDLE_CENTER);
            buttonPanel.setComponentAlignment(cancel, Alignment.MIDDLE_CENTER);
            addComponent(buttonPanel);
            setComponentAlignment(buttonPanel, Alignment.BOTTOM_CENTER);
        }
    }

    private CommonFormWidget createUntCertificate() throws Exception {
        QueryModel<USER_DOCUMENT_FILE> userDocumentFileQM = new QueryModel<>(USER_DOCUMENT_FILE.class);
        userDocumentFileQM.addSelect("id");
        userDocumentFileQM.addSelect("fileName");
        userDocumentFileQM.addWhere("userDocument", ECriteria.EQUAL, null);
        userDocumentFileQM.addWhereAnd("deleted", Boolean.FALSE);

        CommonFormWidget untCertificateFW = new CommonFormWidget(UNT_CERTIFICATE.class);
        untCertificateFW.addEntityListener(new UntCertificateListener());
        final FormModel untCertificateFM = untCertificateFW.getWidgetModel();
        untCertificateFM.setReadOnly(readOnly);
        untCertificateFM.setTitleResource("unt.certificate");
        String errorMessage = studentEditHelper.getUiLocaleUtil().getCaption("title.error").concat(": ").concat(studentEditHelper.getUiLocaleUtil().getCaption("unt"));
        untCertificateFM.setErrorMessageTitle(errorMessage);
        untCertificateFM.setButtonsVisible(false);

        FileListFieldModel untCertificateFLFM = (FileListFieldModel) untCertificateFM.getFieldModel("fileList");
        untCertificateFLFM.permitMimeType(FileListFieldModel.JPEG);

        if (studentEditHelper.isStudentNew()) {
            UNT_CERTIFICATE untCertificate = (UNT_CERTIFICATE) untCertificateFM.createNew();
            untCertificate.setRate(0);
        } else {
            QueryModel<UNT_CERTIFICATE> untCertificateQM = new QueryModel<>(UNT_CERTIFICATE.class);
            FromItem fi = untCertificateQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
            untCertificateQM.addWhere(fi, "user", ECriteria.EQUAL, studentEditHelper.getStudent().getId());
            untCertificateQM.addWhereAnd(fi, "deleted", Boolean.FALSE);
            try {
                UNT_CERTIFICATE untCertificate = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(untCertificateQM);

                if (untCertificate != null) {
                    untCertificateFM.loadEntity(untCertificate.getId());

                    userDocumentFileQM.addWhere("userDocument", ECriteria.EQUAL, untCertificate.getId());
                    List udfList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(userDocumentFileQM);
                    if (!udfList.isEmpty()) {
                        for (Object o : udfList) {
                            Object[] oo = (Object[]) o;
                            FileBean fe = new FileBean(USER_DOCUMENT_FILE.class);
                            fe.setId(ID.valueOf((Long) oo[0]));
                            fe.setFileName((String) oo[1]);
                            fe.setNewFile(false);
                            untCertificateFLFM.getFileList().add(fe);
                        }
                    }
                }
            } catch (NoResultException ex) {
                if (readOnly) {
                    untCertificateFM.loadEntity(ID.valueOf(-1));
                } else {
                    UNT_CERTIFICATE tnc = (UNT_CERTIFICATE) untCertificateFM.createNew();
                    tnc.setRate(0);
                }
            }
        }
        return untCertificateFW;
    }

    private TableWidget createUntRates() throws Exception {

            TableWidget untRatesTW = new TableWidget(V_UNT_CERT_SUBJECT.class);
            untRatesTW.addEntityListener(new UntRatesListener());
            DBTableModel untRatesTM = (DBTableModel) untRatesTW.getWidgetModel();

            untRatesTM.setReadOnly(readOnly);
            QueryModel untRatesQM = untRatesTM.getQueryModel();
            ID untCertificateId = ID.valueOf(-1);
            if (!untCertificateFW.getWidgetModel().isCreateNew()) {
                UNT_CERTIFICATE untCertificate = (UNT_CERTIFICATE) untCertificateFW.getWidgetModel().getEntity();
                untCertificateId = untCertificate.getId();
            }
            untRatesQM.addWhere("untCertificate", ECriteria.EQUAL, untCertificateId);

            FormModel untRatesFM = ((DBTableModel) untRatesTW.getWidgetModel()).getFormModel();

        return untRatesTW;
    }

    private class UntCertificateListener implements EntityListener {

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
        public boolean preSave(Object o, Entity entity, boolean isNew, int i) throws Exception {
            if (studentEditHelper.isStudentNew()) {
                Message.showInfo(studentEditHelper.getUiLocaleUtil().getMessage("info.save.base.data.first"));
                return false;
            }
            UNT_CERTIFICATE untCertificate = (UNT_CERTIFICATE) entity;
            if (isNew) {
                try {
                    untCertificate.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID(USER_DOCUMENT_SEQUENCE));
                    untCertificate.setUser(studentEditHelper.getStudent());
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(untCertificate);
                    untCertificateFW.getWidgetModel().loadEntity(untCertificate.getId());
                    untCertificateFW.refresh();
                    studentEditHelper.showSavedNotification();
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to create a Unt certificate", ex);
                }
            } else {
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(untCertificate);
                    studentEditHelper.showSavedNotification();
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to merge a Unt certificate", ex);
                }
            }

            FileListFieldModel fileListFieldModel = (FileListFieldModel) untCertificateFW.getWidgetModel().getFieldModel("fileList");
            for (FileBean fe : fileListFieldModel.getFileList()) {
                if (fe.isNewFile()) {
                    USER_DOCUMENT_FILE userDocumentFile = new USER_DOCUMENT_FILE();
                    userDocumentFile.setUserDocument(untCertificate);
                    userDocumentFile.setFileName(fe.getFileName());
                    userDocumentFile.setFileBytes(fe.getFileBytes());
                    try {
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(userDocumentFile);
                    } catch (Exception ex) {
                        CommonUtils.showMessageAndWriteLog("Unable to save Unt certificate copy", ex);
                    }
                }
            }

            for (FileBean fe : fileListFieldModel.getDeleteList()) {
                try {
                    USER_DOCUMENT_FILE udf = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_DOCUMENT_FILE.class, fe.getId());
                    udf.setDeleted(true);
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(udf);
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to delete Unt certificate copy", ex);
                }
            }

            return false;
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

    private class UntRatesListener implements EntityListener {

        @Override
        public void handleEntityEvent(EntityEvent entityEvent) {
            if (entityEvent.getAction() == EntityEvent.REFRESHED) {
                UNT_CERTIFICATE untCertificate = null;
                try {
                    untCertificate = (UNT_CERTIFICATE) untCertificateFW.getWidgetModel().getEntity();
                } catch (Exception e) {
                    e.printStackTrace();//TODO catch
                }
                if (untCertificate.getId() != null) {
                    int rate = 0;
                    List<Entity> untRateList = untRatesTW.getAllEntities();
                    for (Entity e : untRateList) {
                        rate += ((V_UNT_CERT_SUBJECT) e).getRate();
                    }
                    untCertificate.setRate(rate);
                    try {
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(untCertificate);
                        untCertificateFW.getWidgetModel().getFieldModel("rate").getField().setValue(String.valueOf(rate));
                    } catch (Exception ex) {
                        CommonUtils.showMessageAndWriteLog("Unable to refresh rates", ex);
                    }
                }
            }
        }

        @Override
        public boolean preCreate(Object o, int i) {
            if (untCertificateFW.getWidgetModel().isCreateNew()) {
                Message.showInfo(studentEditHelper.getUiLocaleUtil().getMessage("info.save.base.data.first"));
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
        public boolean preSave(Object o, Entity entity, boolean isNew, int i) throws Exception {
            V_UNT_CERT_SUBJECT vtUntCertSubject = (V_UNT_CERT_SUBJECT) entity;
            UNT_CERT_SUBJECT untCertSubject;
            FormModel fm = untCertificateFW.getWidgetModel();
            if (isNew) {
                untCertSubject = new UNT_CERT_SUBJECT();
                try {
                    untCertSubject.setUntCertificate((UNT_CERTIFICATE) fm.getEntity());
                    untCertSubject.setUntSubject(vtUntCertSubject.getUntSubject());
                    untCertSubject.setRate(vtUntCertSubject.getRate());
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(untCertSubject);

                    QueryModel untRatesQM = ((DBTableModel) untRatesTW.getWidgetModel()).getQueryModel();
                    untRatesQM.addWhere("untCertificate", ECriteria.EQUAL, fm.getEntity().getId());

                    untRatesTW.refresh();
                    studentEditHelper.showSavedNotification();
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to create a Unt rate", ex);
                }
            } else {
                try {
                    untCertSubject = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(UNT_CERT_SUBJECT.class, vtUntCertSubject.getId());
                    untCertSubject.setUntSubject(vtUntCertSubject.getUntSubject());
                    untCertSubject.setRate(vtUntCertSubject.getRate());
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(untCertSubject);
                    untRatesTW.refresh();
                    studentEditHelper.showSavedNotification();
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to merge a Unt rate", ex);
                }
            }
            return false;
        }

        @Override
        public boolean preDelete(Object o, List<Entity> entities, int i) {
            List<UNT_CERT_SUBJECT> delList = new ArrayList<>();
            for (Entity entity : entities) {
                try {
                    delList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(UNT_CERT_SUBJECT.class, entity.getId()));
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to delete user Unt rates", ex);
                }
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(delList);
                untRatesTW.refresh();
            } catch (Exception ex) {
                CommonUtils.LOG.error("Unable to delete user Unt rates: ", ex);
                Message.showError(studentEditHelper.getUiLocaleUtil().getMessage("error.cannotdelentity"));
            }

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
}
