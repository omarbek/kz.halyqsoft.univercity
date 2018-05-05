package kz.halyqsoft.univercity.utils.register;

import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.ui.Button;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_UNT_CERT_SUBJECT;
import kz.halyqsoft.univercity.modules.regapplicants.ApplicantsForm;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.facade.CommonIDFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.file.FileBean;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.form.AbstractFormWidget;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.GridFormWidget;
import org.r3a.common.vaadin.widget.form.field.filelist.FileListFieldModel;
import org.r3a.common.vaadin.widget.table.TableWidget;
import org.r3a.common.vaadin.widget.table.model.DBTableModel;

import javax.persistence.NoResultException;

/**
 * @author Omarbek
 * @created on 06.04.2018
 */
public class Unt {

    private GridFormWidget certificateGFW;
    private TableWidget untRatesTW;
    private AbstractFormWidget dataAFW;
    private ApplicantsForm applicantsForm;
    private boolean saveUNT;
    private FormModel untCertificateFM;

    public GridFormWidget getCertificateGFW() {
        return certificateGFW;
    }

    public TableWidget getUntRatesTW() {
        return untRatesTW;
    }

    public boolean isSaveUNT() {
        return saveUNT;
    }

    public Unt(AbstractFormWidget dataAFW, ApplicantsForm applicantsForm) {
        this.dataAFW = dataAFW;
        this.applicantsForm = applicantsForm;
    }

    public boolean preSaveRates(Entity e, boolean isNew) {
        V_UNT_CERT_SUBJECT vucs = (V_UNT_CERT_SUBJECT) e;
        UNT_CERT_SUBJECT ucs;
        FormModel fm = certificateGFW.getWidgetModel();
        if (isNew) {
            ucs = new UNT_CERT_SUBJECT();
            try {
                UNT_CERTIFICATE untCertificate = (UNT_CERTIFICATE) fm.getEntity();
                ucs.setUntCertificate(untCertificate);
                ucs.setUntSubject(vucs.getUntSubject());
                ucs.setRate(vucs.getRate());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(ucs);
                untCertificate.setRate(untCertificate.getRate() + vucs.getRate());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(untCertificate);
                saveUNT = true;
                QueryModel untRatesQM = ((DBTableModel) untRatesTW.getWidgetModel()).getQueryModel();
                untRatesQM.addWhere("untCertificate", ECriteria.EQUAL, fm.getEntity().getId());

                untRatesTW.refresh();
                certificateGFW.refresh();
                CommonUtils.showSavedNotification();

            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a Unt rate", ex);
            }
        } else {
            try {
                UNT_CERTIFICATE untCertificate = (UNT_CERTIFICATE) fm.getEntity();
                ucs = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(UNT_CERT_SUBJECT.class, vucs.getId());
                ucs.setUntSubject(vucs.getUntSubject());
                ucs.setRate(vucs.getRate());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(ucs);
                untCertificate.setRate(untCertificate.getRate() + vucs.getRate());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(untCertificate);
                saveUNT = true;
                untRatesTW.refresh();
                certificateGFW.refresh();
                CommonUtils.showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a Unt rate", ex);
            }
        }
        return false;
    }

    public boolean preSaveCertificate(Entity e, boolean isNew) {
        if (dataAFW.getWidgetModel().isCreateNew()) {
            Message.showInfo(CommonUtils.getUILocaleUtil().getMessage("info.save.base.data.first"));
            return false;
        }
        UNT_CERTIFICATE uc = (UNT_CERTIFICATE) e;
        FormModel fm = dataAFW.getWidgetModel();

        if (isNew) {
            try {
                uc.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                uc.setUser((STUDENT) fm.getEntity());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(uc);
                certificateGFW.getWidgetModel().loadEntity(uc.getId());
                CommonUtils.showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a Unt certificate", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(uc);
                CommonUtils.showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a Unt certificate", ex);
            }
        }

        FileListFieldModel flfm = (FileListFieldModel) certificateGFW.getWidgetModel().getFieldModel("fileList");
        for (FileBean fe : flfm.getFileList()) {
            if (fe.isNewFile()) {
                USER_DOCUMENT_FILE udf = new USER_DOCUMENT_FILE();
                udf.setUserDocument(uc);
                udf.setFileName(fe.getFileName());
                udf.setFileBytes(fe.getFileBytes());
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(udf);
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to save Unt certificate copy", ex);
                }
            }
        }

        for (FileBean fe : flfm.getDeleteList()) {
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

    public void create(QueryModel<USER_DOCUMENT_FILE> udfQM) throws Exception {
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(CommonUtils.getUILocaleUtil().getCaption("title.error"));
        sb.append(": ");
        sb.append(CommonUtils.getUILocaleUtil().getCaption("unt"));
        certificateGFW = new GridFormWidget(UNT_CERTIFICATE.class);
        certificateGFW.addEntityListener(applicantsForm);
        untCertificateFM = certificateGFW.getWidgetModel();
        untCertificateFM.setTitleResource("unt.certificate");
        untCertificateFM.setErrorMessageTitle(sb.toString());
        untCertificateFM.setButtonsVisible(false);
        untCertificateFM.getFieldModel("ict").getValidators().add(new RegexpValidator("^\\d{9}$", "ИКТ должен состоять из 9 цифр"));
        untCertificateFM.getFieldModel("rate").setInEdit(true);
        FileListFieldModel untCertificateFLFM = (FileListFieldModel) untCertificateFM.getFieldModel("fileList");
        untCertificateFLFM.permitMimeType(FileListFieldModel.JPEG);

        if (dataAFW.getWidgetModel().isCreateNew()) {
            UNT_CERTIFICATE cert = (UNT_CERTIFICATE) untCertificateFM.createNew();
            cert.setRate(0);
        } else {
            QueryModel<UNT_CERTIFICATE> untCertificateQM = new QueryModel<>(UNT_CERTIFICATE.class);
            FromItem fi = untCertificateQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
            untCertificateQM.addWhere(fi, "user", ECriteria.EQUAL, dataAFW.getWidgetModel().getEntity().getId());
            untCertificateQM.addWhereAnd(fi, "deleted", Boolean.FALSE);
            try {
                UNT_CERTIFICATE untCertificate = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(untCertificateQM);
                if (untCertificate != null) {
                    untCertificateFM.loadEntity(untCertificate.getId());
                    udfQM.addWhere("userDocument", ECriteria.EQUAL, untCertificate.getId());
                    CommonUtils.addFiles(udfQM, untCertificateFLFM);
                }
            } catch (NoResultException ex) {
                untCertificateFM.createNew();
            }
        }
    }

    public Button createRates(Button saveButton) {
        ID untCertificateId = ID.valueOf(-1);
        if (!untCertificateFM.isCreateNew()) {
            UNT_CERTIFICATE untCertificate = null;
            try {
                untCertificate = (UNT_CERTIFICATE) untCertificateFM.getEntity();
            } catch (Exception e) {
                e.printStackTrace();//TODO catch
            }
            if (untCertificate != null) {
                untCertificateId = untCertificate.getId();
            }
        }
        untRatesTW = new TableWidget(V_UNT_CERT_SUBJECT.class);
        untRatesTW.addEntityListener(applicantsForm);
        untRatesTW.setWidth("667px");
        DBTableModel untRatesTM = (DBTableModel) untRatesTW.getWidgetModel();
        QueryModel untRatesQM = untRatesTM.getQueryModel();
        untRatesQM.addWhere("untCertificate", ECriteria.EQUAL, untCertificateId);

        saveButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if (untCertificateFM.isModified()) {
                    certificateGFW.save();
                } else {
                    Message.showError("fill main data");
                }
            }
        });
        return saveButton;
    }
}
