package kz.halyqsoft.univercity.utils.register;

import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.REPATRIATE_DOC;
import kz.halyqsoft.univercity.entity.beans.univercity.USER_DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.USER_DOCUMENT_FILE;
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
import org.r3a.common.vaadin.widget.form.AbstractFormWidgetView;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.GridFormWidget;
import org.r3a.common.vaadin.widget.form.field.filelist.FileListFieldModel;

import javax.persistence.NoResultException;

/**
 * @author Omarbek
 * @created on 06.04.2018
 */
public class Repatriate {

    private GridFormWidget mainGFW;
    private AbstractFormWidget dataAFW;
    private AbstractFormWidgetView applicantsForm;
    private FormModel mainFM;

    public GridFormWidget getMainGFW() {
        return mainGFW;
    }

    public Repatriate(AbstractFormWidget dataAFW, AbstractFormWidgetView applicantsForm) {
        this.dataAFW = dataAFW;
        this.applicantsForm = applicantsForm;
    }

    public void create(QueryModel<USER_DOCUMENT_FILE> udfQM) throws Exception {
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(CommonUtils.getUILocaleUtil().getCaption("title.error"));
        sb.append(": ");
        sb.append(CommonUtils.getUILocaleUtil().getCaption("repatriate.document"));
        mainGFW = new GridFormWidget(REPATRIATE_DOC.class);
        mainGFW.addEntityListener(applicantsForm);
        mainFM = mainGFW.getWidgetModel();
        mainFM.setTitleResource("repatriate.document");
        mainFM.setErrorMessageTitle(sb.toString());
        mainFM.setButtonsVisible(false);

        FileListFieldModel repatriateFLFM = (FileListFieldModel) mainFM.getFieldModel("fileList");
        repatriateFLFM.permitMimeType(FileListFieldModel.JPEG);

        if (dataAFW.getWidgetModel().isCreateNew()) {
            mainFM.createNew();
        } else {
            QueryModel<REPATRIATE_DOC> repatriateDocQM = new QueryModel<>(REPATRIATE_DOC.class);
            FromItem fi = repatriateDocQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
            repatriateDocQM.addWhere(fi, "user", ECriteria.EQUAL, dataAFW.getWidgetModel().getEntity().getId());
            repatriateDocQM.addWhereAnd(fi, "deleted", Boolean.FALSE);
            try {
                REPATRIATE_DOC repatriateDoc = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(repatriateDocQM);
                mainFM.loadEntity(repatriateDoc.getId());
                udfQM.addWhere("userDocument", ECriteria.EQUAL, repatriateDoc.getId());
                CommonUtils.addFiles(udfQM, repatriateFLFM);
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
        REPATRIATE_DOC rd = (REPATRIATE_DOC) e;
        FormModel fm = dataAFW.getWidgetModel();
        if (isNew) {
            try {
                rd.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                rd.setUser((USERS) fm.getEntity());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(rd);
                CommonUtils.showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a repatriate doc", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(rd);
                CommonUtils.showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a repatriate doc", ex);
            }
        }

        FileListFieldModel flfm = (FileListFieldModel) mainGFW.getWidgetModel().getFieldModel("fileList");
        for (FileBean fe : flfm.getFileList()) {
            if (fe.isNewFile()) {
                USER_DOCUMENT_FILE udf = new USER_DOCUMENT_FILE();
                udf.setUserDocument(rd);
                udf.setFileName(fe.getFileName());
                udf.setFileBytes(fe.getFileBytes());
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(udf);
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to save repatriate doc copy", ex);
                }
            }
        }

        CommonUtils.deleteFiles(flfm);

        return false;
    }

    public Boolean save() {
        if (mainFM.isModified()) {
            return mainGFW.save();
        }
        return null;
    }
}
