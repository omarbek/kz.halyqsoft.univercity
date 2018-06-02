package kz.halyqsoft.univercity.utils.register;

import com.vaadin.data.validator.IntegerRangeValidator;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.EDUCATION_DOC;
import kz.halyqsoft.univercity.entity.beans.univercity.USER_DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.USER_DOCUMENT_FILE;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.changelisteners.SchoolCountryChangeListener;
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
import org.r3a.common.vaadin.widget.DBSelectModel;
import org.r3a.common.vaadin.widget.form.AbstractFormWidget;
import org.r3a.common.vaadin.widget.form.AbstractFormWidgetView;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.GridFormWidget;
import org.r3a.common.vaadin.widget.form.field.filelist.FileListFieldModel;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.table.TableWidget;
import org.r3a.common.vaadin.widget.table.model.DBTableModel;

import javax.persistence.NoResultException;

/**
 * @author Omarbek
 * @created on 06.04.2018
 */
public class EducationDoc {

    private GridFormWidget mainGFW;
    private AbstractFormWidget dataAFW;
    private AbstractFormWidgetView applicantsForm;
    private TableWidget documentsTW;
    private FromItem educationUDFI;
    private FormModel mainFM;
    private boolean saveEduc;

    public boolean isSaveEduc() {
        return saveEduc;
    }

    public GridFormWidget getMainGFW() {
        return mainGFW;
    }

    public EducationDoc(AbstractFormWidget dataAFW,
                        AbstractFormWidgetView applicantsForm, TableWidget documentsTW, FromItem educationUDFI) {
        this.dataAFW = dataAFW;
        this.applicantsForm = applicantsForm;
        this.documentsTW = documentsTW;
        this.educationUDFI = educationUDFI;
    }

    public void create(QueryModel<USER_DOCUMENT_FILE> udfQM) throws Exception {
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(CommonUtils.getUILocaleUtil().getCaption("title.error"));
        sb.append(": ");
        sb.append(CommonUtils.getUILocaleUtil().getCaption("education.document"));
        mainGFW = new GridFormWidget(EDUCATION_DOC.class);
        mainGFW.addEntityListener(applicantsForm);
        mainFM = mainGFW.getWidgetModel();
        mainFM.setButtonsVisible(false);
        mainFM.setTitleResource("education.document");
        FKFieldModel schoolCountryFieldModel = (FKFieldModel) mainFM.getFieldModel("schoolCountry");
        QueryModel schoolCountryQM = schoolCountryFieldModel.getQueryModel();
        schoolCountryQM.addWhereNull("parent");
        schoolCountryQM.addOrder("countryName");

        mainFM.getFieldModel("language").setRequired(true);
        mainFM.getFieldModel("schoolRegion").setRequired(false);

        mainFM.getFieldModel("entryYear").getValidators().add(new IntegerRangeValidator("Значение года не может быть больше текущего года", 0, CommonUtils.currentYear));
        mainFM.getFieldModel("endYear").getValidators().add(new IntegerRangeValidator("Значение года не может быть больше текущего года", 0, CommonUtils.currentYear));

        FKFieldModel schoolRegionFieldModel = (FKFieldModel) mainFM.getFieldModel("schoolRegion");
        QueryModel schoolRegionQM = schoolRegionFieldModel.getQueryModel();
        schoolRegionQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));

        schoolCountryFieldModel.getListeners().add(new SchoolCountryChangeListener(schoolRegionFieldModel, null));


        FileListFieldModel educationFLFM = (FileListFieldModel) mainFM.getFieldModel("fileList");
        educationFLFM.permitMimeType(FileListFieldModel.JPEG);
        educationFLFM.getFileList().clear();
        educationFLFM.getDeleteList().clear();
        if (dataAFW.getWidgetModel().isCreateNew()) {
            mainFM.createNew();
        } else {
            QueryModel<EDUCATION_DOC> educationQM = new QueryModel<>(EDUCATION_DOC.class);
            FromItem sc = educationQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
            educationQM.addWhere(sc, "user", ECriteria.EQUAL, dataAFW.getWidgetModel().getEntity().getId());
            educationQM.addWhereAnd(sc, "deleted", Boolean.FALSE);
            try {
                EDUCATION_DOC educDoc = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(educationQM);
                mainFM.loadEntity(educDoc.getId());
                udfQM.addWhere("userDocument", ECriteria.EQUAL, educDoc.getId());
                CommonUtils.addFiles(udfQM, educationFLFM);
            } catch (NoResultException ex) {
                mainFM.createNew();
            }
        }
    }

    public boolean preSave(Entity e, boolean isNew) {
        EDUCATION_DOC ed = (EDUCATION_DOC) e;
        FormModel fm = dataAFW.getWidgetModel();
        if (isNew) {
            try {
                ed.setId(SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USER_DOCUMENT"));
                ed.setUser((USERS) fm.getEntity());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(ed);

                QueryModel educationQM = ((DBSelectModel) documentsTW.getWidgetModel()).getQueryModel();
                educationQM.addWhere(educationUDFI, "user", ECriteria.EQUAL, dataAFW.getWidgetModel().getEntity().getId());

                documentsTW.refresh();
                CommonUtils.showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to create a education doc", ex);
            }
        } else {
            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(ed);
                documentsTW.refresh();
                CommonUtils.showSavedNotification();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to merge a education doc", ex);
            }
        }

        FileListFieldModel flfm = (FileListFieldModel) ((DBTableModel) documentsTW.getWidgetModel()).getFormModel().getFieldModel("fileList");
        for (FileBean fe : flfm.getFileList()) {
            if (fe.isNewFile()) {
                USER_DOCUMENT_FILE udf = new USER_DOCUMENT_FILE();
                udf.setUserDocument(ed);
                udf.setFileName(fe.getFileName());
                udf.setFileBytes(fe.getFileBytes());
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(udf);
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to save education doc copy", ex);
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
