package kz.halyqsoft.univercity.utils.register;

import com.vaadin.data.validator.RegexpValidator;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.USER_DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.USER_DOCUMENT_FILE;
import kz.halyqsoft.univercity.entity.beans.univercity.USER_PASSPORT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.COUNTRY;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.changelisteners.BirthCountryChangeListener;
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
import org.r3a.common.vaadin.widget.form.AbstractFormWidgetView;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.GridFormWidget;
import org.r3a.common.vaadin.widget.form.field.filelist.FileListFieldModel;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;

import javax.persistence.NoResultException;
import java.util.List;

/**
 * @author Omarbek
 * @created on 06.04.2018
 */
public class Passport {

    private GridFormWidget mainGFW;
    private AbstractFormWidget dataAFW;
    private AbstractFormWidgetView applicantsForm;
    private FormModel mainFM;
    private boolean savePass;

    public boolean isSavePass() {
        return savePass;
    }

    public GridFormWidget getMainGFW() {
        return mainGFW;
    }

    public Passport(AbstractFormWidget dataAFW, AbstractFormWidgetView applicantsForm) {
        this.dataAFW = dataAFW;
        this.applicantsForm = applicantsForm;
    }

    public void create(QueryModel<USER_DOCUMENT_FILE> udfQM) throws Exception {
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(CommonUtils.getUILocaleUtil().getCaption("title.error"));
        sb.append(": ");
        sb.append(CommonUtils.getUILocaleUtil().getCaption("identity.document"));
        mainGFW = new GridFormWidget(USER_PASSPORT.class);
        mainGFW.addEntityListener(applicantsForm);
        mainGFW.focus();
        mainFM = mainGFW.getWidgetModel();
        mainFM.setTitleResource("identity.document");
        mainFM.setErrorMessageTitle(sb.toString());
        mainFM.setButtonsVisible(false);
        mainFM.getFieldModel("expireDate").setRequired(true);

        COUNTRY birthRegion = null;

        FKFieldModel birthCountryFieldModel = (FKFieldModel) mainFM.getFieldModel("birthCountry");
        QueryModel birthCountryQM = birthCountryFieldModel.getQueryModel();
        birthCountryQM.addWhereNull("parent");
        birthCountryQM.addOrder("countryName");

        FKFieldModel birthRegionFieldModel = (FKFieldModel) mainFM.getFieldModel("birthRegion");
        QueryModel birthRegionQM = birthRegionFieldModel.getQueryModel();
        birthRegionQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));

        mainFM.getFieldModel("iin").getValidators().add(new RegexpValidator("^\\d{12}$", "ИИН должен состоять из 12 цифр"));

        FileListFieldModel passportFLFM = (FileListFieldModel) mainFM.getFieldModel("fileList");
        passportFLFM.permitMimeType(FileListFieldModel.JPEG);

        if (dataAFW.getWidgetModel().isCreateNew()) {
            mainFM.createNew();
        } else {
            QueryModel<USER_PASSPORT> userPassportQM = new QueryModel<>(USER_PASSPORT.class);
            FromItem fi = userPassportQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
            userPassportQM.addWhere(fi, "user", ECriteria.EQUAL, dataAFW.getWidgetModel().getEntity().getId());
            userPassportQM.addWhereAnd(fi, "deleted", Boolean.FALSE);
            try {
                USER_PASSPORT userPassport = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(userPassportQM);
                if (userPassport != null) {
                    mainFM.loadEntity(userPassport.getId());
                    birthRegion = ((USER_PASSPORT) mainFM.getEntity()).getBirthRegion();
                    udfQM.addWhere("userDocument", ECriteria.EQUAL, userPassport.getId());
                    List udfList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(udfQM);
                    if (!udfList.isEmpty()) {
                        for (Object o : udfList) {
                            Object[] oo = (Object[]) o;
                            FileBean fe = new FileBean(USER_DOCUMENT_FILE.class);
                            fe.setId(ID.valueOf((Long) oo[0]));
                            fe.setFileName((String) oo[1]);
                            CommonUtils.LOG.error("It works! " + fe.toString());
                            fe.setNewFile(false);
                            passportFLFM.getFileList().add(fe);
                        }
                    }
                }
            } catch (NoResultException ex) {
                mainFM.createNew();
            }
        }
        birthCountryFieldModel.getListeners().add(new BirthCountryChangeListener(birthRegionFieldModel, birthRegion));
    }

    public boolean preSave(Entity e, boolean isNew) {
        if (dataAFW.getWidgetModel().isCreateNew()) {
            Message.showInfo(CommonUtils.getUILocaleUtil().getMessage("info.save.base.data.first"));
            return false;
        }

        USER_PASSPORT p = (USER_PASSPORT) e;
        FormModel fm = dataAFW.getWidgetModel();
        if (isNew) {
            try {
                p.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                p.setUser((USERS) fm.getEntity());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(p);
                CommonUtils.showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a passport", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(p);
                CommonUtils.showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a passport", ex);
            }
        }

        FileListFieldModel flfm = (FileListFieldModel) mainGFW.getWidgetModel().getFieldModel("fileList");
        for (FileBean fe : flfm.getFileList()) {
            if (fe.isNewFile()) {
                USER_DOCUMENT_FILE udf = new USER_DOCUMENT_FILE();
                udf.setUserDocument(p);
                udf.setFileName(fe.getFileName());
                udf.setFileBytes(fe.getFileBytes());
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(udf);
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to save passport copy", ex);
                }
            }
        }

        for (FileBean fe : flfm.getDeleteList()) {
            try {
                USER_DOCUMENT_FILE udf = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_DOCUMENT_FILE.class, fe.getId());
                udf.setDeleted(true);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(udf);
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to delete passport copy", ex);
            }
        }

        return false;
    }

    public Boolean save() {
        if (mainFM.isModified()) {
            return mainGFW.save();
        }
        return null;
    }
}
