package kz.halyqsoft.univercity.utils.register;

import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.MILITARY_DOC;
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
public class Military {
    private GridFormWidget mainGFW;
    private AbstractFormWidget dataAFW;
    private AbstractFormWidgetView applicantsForm;
    private FormModel mainFM;
    private FileListFieldModel militaryFLFM;

    public GridFormWidget getMainGFW() {
        return mainGFW;
    }

    public FileListFieldModel getMilitaryFLFM() {
        return militaryFLFM;
    }

    public Military(AbstractFormWidget dataAFW, AbstractFormWidgetView applicantsForm) {
        this.dataAFW = dataAFW;
        this.applicantsForm = applicantsForm;
    }

    public FormModel create(QueryModel<USER_DOCUMENT_FILE> udfQM) throws Exception {
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(CommonUtils.getUILocaleUtil().getCaption("title.error"));
        sb.append(": ");
        sb.append(CommonUtils.getUILocaleUtil().getCaption("military.document"));
        mainGFW = new GridFormWidget(MILITARY_DOC.class);
        mainGFW.addEntityListener(applicantsForm);
        mainFM = mainGFW.getWidgetModel();
        mainFM.setTitleResource("military.document");
        mainFM.setErrorMessageTitle(sb.toString());
        mainFM.setButtonsVisible(false);
        mainFM.getFieldModel("militaryDocType").addStyle("toTop");

        militaryFLFM = (FileListFieldModel) mainFM.getFieldModel("fileList");
        militaryFLFM.permitMimeType(FileListFieldModel.JPEG);

        if (dataAFW.getWidgetModel().isCreateNew()) {
            mainFM.createNew();
        } else {
            QueryModel<MILITARY_DOC> militaryDocQM = new QueryModel<>(MILITARY_DOC.class);
            FromItem fi = militaryDocQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
            militaryDocQM.addWhere(fi, "user", ECriteria.EQUAL, dataAFW.getWidgetModel().getEntity().getId());
            militaryDocQM.addWhereAnd(fi, "deleted", Boolean.FALSE);
            try {
                MILITARY_DOC militaryDoc = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(militaryDocQM);
                mainFM.loadEntity(militaryDoc.getId());
                udfQM.addWhere("userDocument", ECriteria.EQUAL, militaryDoc.getId());
                CommonUtils.addFiles(udfQM, militaryFLFM);
            } catch (NoResultException ex) {
                mainFM.createNew();
            }
        }
        return mainFM;
    }

    public boolean preSave(Entity e, boolean isNew) {
        if (dataAFW.getWidgetModel().isCreateNew()) {
            Message.showInfo(CommonUtils.getUILocaleUtil().getMessage("info.save.base.data.first"));
            return false;
        }
        MILITARY_DOC md = (MILITARY_DOC) e;
        FormModel fm = dataAFW.getWidgetModel();
        if (isNew) {
            try {
                md.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                md.setUser((USERS) fm.getEntity());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(md);
                CommonUtils.showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a military doc", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(md);
                CommonUtils.showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a military doc", ex);
            }
        }

        FileListFieldModel flfm = (FileListFieldModel) mainGFW.getWidgetModel().getFieldModel("fileList");
        for (FileBean fe : flfm.getFileList()) {
            if (fe.isNewFile()) {
                USER_DOCUMENT_FILE udf = new USER_DOCUMENT_FILE();
                udf.setUserDocument(md);
                udf.setFileName(fe.getFileName());
                udf.setFileBytes(fe.getFileBytes());
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(udf);
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to save military doc copy", ex);
                }
            }
        }

        for (FileBean fe : flfm.getDeleteList()) {
            try {
                USER_DOCUMENT_FILE udf = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_DOCUMENT_FILE.class, fe.getId());
                udf.setDeleted(true);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(udf);
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to delete military doc copy", ex);
            }
        }

        return false;
    }

    public void save() {
        if (mainFM.isModified())
            mainGFW.save();
    }

}
