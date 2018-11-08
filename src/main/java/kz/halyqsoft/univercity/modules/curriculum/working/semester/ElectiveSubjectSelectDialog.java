package kz.halyqsoft.univercity.modules.curriculum.working.semester;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.EDUCATION_MODULE_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT_CYCLE;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.widget.dialog.AbstractYesButtonListener;
import org.r3a.common.vaadin.widget.dialog.select.custom.grid.CustomGridSelectDialog;

/**
 * @author Omarbek Dinassil
 * @created Apr 20, 2017 2:03:36 PM
 */
@SuppressWarnings("serial")
final class ElectiveSubjectSelectDialog extends CustomGridSelectDialog {

    private CheckBox considerCreditCB;
    private TextField codeTF;
    private ComboBox educationModuleCB;

    public ElectiveSubjectSelectDialog(AbstractYesButtonListener yesListener, Class<? extends Entity> entityClass) {
        super(yesListener, entityClass);
    }

    @Override
    protected void initAddContent() {
        FormLayout paramsFL = new FormLayout();

        codeTF = new TextField();
        codeTF.setRequired(true);
        codeTF.setCaption(getUILocaleUtil().getCaption("code"));
        paramsFL.addComponent(codeTF);

        educationModuleCB = new ComboBox();
        educationModuleCB.setRequired(true);
        educationModuleCB.setCaption(getUILocaleUtil().getEntityLabel(EDUCATION_MODULE_TYPE.class));
        educationModuleCB.setImmediate(true);
        educationModuleCB.setNullSelectionAllowed(true);
        educationModuleCB.setTextInputAllowed(true);
        educationModuleCB.setFilteringMode(FilteringMode.STARTSWITH);
        educationModuleCB.setPageLength(0);
        QueryModel<EDUCATION_MODULE_TYPE> eduModuleQM = new QueryModel<>(EDUCATION_MODULE_TYPE.class);
        BeanItemContainer<EDUCATION_MODULE_TYPE> eduModuleBIC = null;
        try {
            eduModuleBIC = new BeanItemContainer<>(EDUCATION_MODULE_TYPE.class, SessionFacadeFactory.
                    getSessionFacade(CommonEntityFacadeBean.class).lookup(eduModuleQM));
        } catch (Exception e) {
            CommonUtils.showMessageAndWriteLog("Unable to get education module type container",e);
        }
        educationModuleCB.setContainerDataSource(eduModuleBIC);
        paramsFL.addComponent(educationModuleCB);

        considerCreditCB = new CheckBox();
        considerCreditCB.setCaption(getUILocaleUtil().getCaption("consider.credit"));
        paramsFL.addComponent(considerCreditCB);
        considerCreditCB.setValue(true);

        QueryModel<SUBJECT_CYCLE> subjectCycleQM = new QueryModel<SUBJECT_CYCLE>(SUBJECT_CYCLE.class);
        getContent().addComponent(paramsFL);
        getContent().setComponentAlignment(paramsFL, Alignment.MIDDLE_CENTER);
    }

    public boolean isConsiderCredit() {
        return considerCreditCB.getValue();
    }

    public String getCode(){
        return codeTF.getValue();
    }

    public EDUCATION_MODULE_TYPE getEducationModuleType(){
        return (EDUCATION_MODULE_TYPE) educationModuleCB.getValue();
    }
}
