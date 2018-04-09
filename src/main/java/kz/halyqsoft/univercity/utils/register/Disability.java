package kz.halyqsoft.univercity.utils.register;

import kz.halyqsoft.univercity.entity.beans.univercity.DISABILITY_DOC;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.USER_DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.USER_DOCUMENT_FILE;
import kz.halyqsoft.univercity.modules.regapplicants.ApplicantsForm;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.ErrorUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.facade.CommonIDFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
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

import javax.persistence.NoResultException;

/**
 * @author Omarbek
 * @created on 06.04.2018
 */
public class Disability {

    private GridFormWidget mainGFW;
    private AbstractFormWidget dataAFW;
    private ApplicantsForm applicantsForm;
    private FormModel mainFM;

    public GridFormWidget getMainGFW() {
        return mainGFW;
    }

    public Disability(AbstractFormWidget dataAFW, ApplicantsForm applicantsForm) {
        this.dataAFW = dataAFW;
        this.applicantsForm = applicantsForm;
    }

    public void create(QueryModel<USER_DOCUMENT_FILE> udfQM) throws Exception {
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(ErrorUtils.getUILocaleUtil().getCaption("title.error"));
        sb.append(": ");
        sb.append(ErrorUtils.getUILocaleUtil().getCaption("disability.document"));
        mainGFW = new GridFormWidget(DISABILITY_DOC.class);
        mainGFW.addEntityListener(applicantsForm);
        mainFM = mainGFW.getWidgetModel();
        mainFM.setTitleResource("disability.document");
        mainFM.setErrorMessageTitle(sb.toString());
        mainFM.setButtonsVisible(false);
        mainFM.getFieldModel("expireDate").addStyle("toTop");

        FileListFieldModel disabilityFLFM = (FileListFieldModel) mainFM.getFieldModel("fileList");
        disabilityFLFM.permitMimeType(FileListFieldModel.JPEG);

        if (dataAFW.getWidgetModel().isCreateNew()) {
            mainFM.createNew();
        } else {
            QueryModel<DISABILITY_DOC> disabilityDocQM = new QueryModel<>(DISABILITY_DOC.class);
            FromItem fi = disabilityDocQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
            disabilityDocQM.addWhere(fi, "user", ECriteria.EQUAL, dataAFW.getWidgetModel().getEntity().getId());
            disabilityDocQM.addWhereAnd(fi, "deleted", Boolean.FALSE);
            try {
                DISABILITY_DOC disabilityDoc = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(disabilityDocQM);
                mainFM.loadEntity(disabilityDoc.getId());
                udfQM.addWhere("userDocument", ECriteria.EQUAL, disabilityDoc.getId());
                CommonUtils.addFiles(udfQM, disabilityFLFM);
            } catch (NoResultException ex) {
                mainFM.createNew();
            }
        }
    }

    public boolean preSave(Entity e, boolean isNew) {
        if (dataAFW.getWidgetModel().isCreateNew()) {
            Message.showInfo(ErrorUtils.getUILocaleUtil().getMessage("info.save.base.data.first"));
            return false;
        }
        DISABILITY_DOC dd = (DISABILITY_DOC) e;
        FormModel fm = dataAFW.getWidgetModel();
        if (isNew) {
            try {
                dd.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                dd.setUser((STUDENT) fm.getEntity());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(dd);
                ErrorUtils.showSavedNotification();
            } catch (Exception ex) {
                ErrorUtils.LOG.error("Unable to createCertificate a disability doc: ", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(dd);
                ErrorUtils.showSavedNotification();
            } catch (Exception ex) {
                ErrorUtils.LOG.error("Unable to merge a disability doc: ", ex);
            }
        }

        FileListFieldModel flfm = (FileListFieldModel) mainGFW.getWidgetModel().getFieldModel("fileList");
        for (FileBean fe : flfm.getFileList()) {
            if (fe.isNewFile()) {
                USER_DOCUMENT_FILE udf = new USER_DOCUMENT_FILE();
                udf.setUserDocument(dd);
                udf.setFileName(fe.getFileName());
                udf.setFileBytes(fe.getFileBytes());
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(udf);
                } catch (Exception ex) {
                    ErrorUtils.LOG.error("Unable to save disability doc copy: ", ex);
                }
            }
        }

        for (FileBean fe : flfm.getDeleteList()) {
            try {
                USER_DOCUMENT_FILE udf = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_DOCUMENT_FILE.class, fe.getId());
                udf.setDeleted(true);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(udf);
            } catch (Exception ex) {
                ErrorUtils.LOG.error("Unable to delete disability doc copy: ", ex);
            }
        }

        return false;
    }

    public void save() {
        if (mainFM.isModified())
            mainGFW.save();
    }
}
