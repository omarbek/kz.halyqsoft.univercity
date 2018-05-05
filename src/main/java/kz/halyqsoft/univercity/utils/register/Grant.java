package kz.halyqsoft.univercity.utils.register;

import com.vaadin.data.validator.RegexpValidator;
import kz.halyqsoft.univercity.entity.beans.univercity.GRANT_DOC;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.USER_DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.USER_DOCUMENT_FILE;
import kz.halyqsoft.univercity.modules.regapplicants.ApplicantsForm;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.CommonUtils;
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
public class Grant {

    private GridFormWidget mainGFW;
    private AbstractFormWidget dataAFW;
    private ApplicantsForm applicantsForm;
    private FormModel mainFM;

    public GridFormWidget getMainGFW() {
        return mainGFW;
    }

    public Grant(AbstractFormWidget dataAFW, ApplicantsForm applicantsForm) {
        this.dataAFW = dataAFW;
        this.applicantsForm = applicantsForm;
    }

    public void create(QueryModel<USER_DOCUMENT_FILE> udfQM) throws Exception {
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(CommonUtils.getUILocaleUtil().getCaption("title.error"));
        sb.append(": ");
        sb.append(CommonUtils.getUILocaleUtil().getCaption("grant.document"));
        mainGFW = new GridFormWidget(GRANT_DOC.class);
        mainGFW.addEntityListener(applicantsForm);
        mainFM = mainGFW.getWidgetModel();
        mainFM.setTitleResource("grant.document");
        mainFM.setErrorMessageTitle(sb.toString());
        mainFM.setButtonsVisible(false);
        mainFM.getFieldModel("ict").getValidators().add(new RegexpValidator("^\\d{9}$", "ИКТ должен состоять из 9 цифр"));

        FileListFieldModel grantDocFLFM = (FileListFieldModel) mainFM.getFieldModel("fileList");
        grantDocFLFM.permitMimeType(FileListFieldModel.JPEG);

        if (dataAFW.getWidgetModel().isCreateNew()) {
            mainFM.createNew();
        } else {
            QueryModel<GRANT_DOC> grantDocQM = new QueryModel<>(GRANT_DOC.class);
            FromItem fi = grantDocQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
            grantDocQM.addWhere(fi, "user", ECriteria.EQUAL, dataAFW.getWidgetModel().getEntity().getId());
            grantDocQM.addWhereAnd(fi, "deleted", Boolean.FALSE);
            try {
                GRANT_DOC grantDoc = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(grantDocQM);
                mainFM.loadEntity(grantDoc.getId());
                udfQM.addWhere("userDocument", ECriteria.EQUAL, grantDoc.getId());
                CommonUtils.addFiles(udfQM, grantDocFLFM);
            } catch (NoResultException ex) {
                mainFM.createNew();
            }
        }
    }

    public boolean preSave(Entity e, boolean isNew) {
        if (dataAFW.getWidgetModel().isCreateNew()) {
            Message.showInfo(CommonUtils.getUILocaleUtil().getMessage("info.save.base.data.first"));
            return false;
        }
        GRANT_DOC gd = (GRANT_DOC) e;
        FormModel fm = dataAFW.getWidgetModel();

        if (isNew) {
            try {
                gd.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                gd.setUser((STUDENT) fm.getEntity());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(gd);
                CommonUtils.showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a grant doc", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(gd);
                CommonUtils.showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a grant doc", ex);
            }
        }

        FileListFieldModel flfm = (FileListFieldModel) mainGFW.getWidgetModel().getFieldModel("fileList");
        for (FileBean fe : flfm.getFileList()) {
            if (fe.isNewFile()) {
                USER_DOCUMENT_FILE udf = new USER_DOCUMENT_FILE();
                udf.setUserDocument(gd);
                udf.setFileName(fe.getFileName());
                udf.setFileBytes(fe.getFileBytes());
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(udf);
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to save grant doc copy", ex);
                }
            }
        }

        for (FileBean fe : flfm.getDeleteList()) {
            try {
                USER_DOCUMENT_FILE udf = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_DOCUMENT_FILE.class, fe.getId());
                udf.setDeleted(true);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(udf);
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to delete grant doc copy", ex);
            }
        }

        return false;
    }

    public void save() {
        if (mainFM.isModified())
            mainGFW.save();
    }
}
