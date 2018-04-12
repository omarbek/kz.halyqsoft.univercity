package kz.halyqsoft.univercity.utils.register;

import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.PREEMPTIVE_RIGHT;
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
public class PreemptiveRight {

    private GridFormWidget mainGFW;
    private AbstractFormWidget dataAFW;
    private AbstractFormWidgetView applicantsForm;
    private FormModel mainFM;

    public GridFormWidget getMainGFW() {
        return mainGFW;
    }

    public PreemptiveRight(AbstractFormWidget dataAFW, AbstractFormWidgetView applicantsForm) {
        this.dataAFW = dataAFW;
        this.applicantsForm = applicantsForm;
    }

    public void create(QueryModel<USER_DOCUMENT_FILE> udfQM) throws Exception {
        String sb = CommonUtils.getUILocaleUtil().getCaption("title.error") + ": "
                + CommonUtils.getUILocaleUtil().getCaption("preemptive.right");
        mainGFW = new GridFormWidget(PREEMPTIVE_RIGHT.class);
        mainGFW.addEntityListener(applicantsForm);
        mainFM = mainGFW.getWidgetModel();
        mainFM.setTitleResource("preemptive.right");
        mainFM.setErrorMessageTitle(sb);
        mainFM.setButtonsVisible(false);

        FileListFieldModel preemptiveRightFLFM = (FileListFieldModel) mainFM.getFieldModel("fileList");
        preemptiveRightFLFM.permitMimeType(FileListFieldModel.JPEG);

        if (dataAFW.getWidgetModel().isCreateNew()) {
            mainFM.createNew();
        } else {
            QueryModel<PREEMPTIVE_RIGHT> preemptiveRightQM = new QueryModel<>(PREEMPTIVE_RIGHT.class);
            FromItem fi = preemptiveRightQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
            preemptiveRightQM.addWhere(fi, "user", ECriteria.EQUAL, dataAFW.getWidgetModel().getEntity().getId());
            preemptiveRightQM.addWhereAnd(fi, "deleted", Boolean.FALSE);
            try {
                PREEMPTIVE_RIGHT preemptiveRight = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(preemptiveRightQM);
                if (preemptiveRight != null) {
                    mainFM.loadEntity(preemptiveRight.getId());
                    udfQM.addWhere("userDocument", ECriteria.EQUAL, preemptiveRight.getId());
                    CommonUtils.addFiles(udfQM, preemptiveRightFLFM);
                }
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
        PREEMPTIVE_RIGHT pr = (PREEMPTIVE_RIGHT) e;
        FormModel fm = dataAFW.getWidgetModel();
        if (isNew) {
            try {
                pr.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                pr.setUser((USERS) fm.getEntity());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(pr);
                CommonUtils.showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a preemptive right", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(pr);
                CommonUtils.showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a preemptive right", ex);
            }
        }

        FileListFieldModel flfm = (FileListFieldModel) mainGFW.getWidgetModel().
                getFieldModel("fileList");
        for (FileBean fe : flfm.getFileList()) {
            if (fe.isNewFile()) {
                USER_DOCUMENT_FILE udf = new USER_DOCUMENT_FILE();
                udf.setUserDocument(pr);
                udf.setFileName(fe.getFileName());
                udf.setFileBytes(fe.getFileBytes());
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(udf);
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to save preemptive right copy", ex);
                }
            }
        }

        for (FileBean fe : flfm.getDeleteList()) {
            try {
                USER_DOCUMENT_FILE udf = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_DOCUMENT_FILE.class, fe.getId());
                udf.setDeleted(true);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(udf);
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to delete preemptive right copy", ex);
            }
        }

        return false;
    }

    public void save() {
        if (mainFM.isModified()) {
            mainGFW.save();
        }
    }
}
