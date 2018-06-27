package kz.halyqsoft.univercity.modules.curriculum.working.semester;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.EDUCATION_MODULE_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT_CYCLE;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.dialog.AbstractYesButtonListener;
import org.r3a.common.vaadin.widget.dialog.select.custom.grid.CustomGridSelectDialog;

/**
 * @author Omarbek Dinassil
 * @created Dec 20, 2016 1:17:47 PM
 */
@SuppressWarnings("serial")
final class SubjectSelectDialog extends CustomGridSelectDialog {

    private CheckBox considerCreditCB;
    private ComboBox subjectCycleCB;
    private TextField codeTF;
    private ComboBox educationModuleCB;

    public SubjectSelectDialog(AbstractYesButtonListener yesListener, Class<? extends Entity> entityClass) {
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
            e.printStackTrace();//TODO catch
        }
        educationModuleCB.setContainerDataSource(eduModuleBIC);
        paramsFL.addComponent(educationModuleCB);

        considerCreditCB = new CheckBox();
        considerCreditCB.setCaption(getUILocaleUtil().getCaption("consider.credit"));
        paramsFL.addComponent(considerCreditCB);
        considerCreditCB.setValue(true);

        QueryModel<SUBJECT_CYCLE> subjectCycleQM = new QueryModel<SUBJECT_CYCLE>(SUBJECT_CYCLE.class);
        subjectCycleQM.addWhere("id", ECriteria.LESS, ID.valueOf(4));
        subjectCycleQM.addOrder("cycleShortName");
        try {
            BeanItemContainer<SUBJECT_CYCLE> subjectCycleBIC = new BeanItemContainer<SUBJECT_CYCLE>(SUBJECT_CYCLE.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(subjectCycleQM));
            subjectCycleCB = new ComboBox();
            subjectCycleCB.setCaption(getUILocaleUtil().getCaption("curriculum.component"));
            subjectCycleCB.setContainerDataSource(subjectCycleBIC);
            subjectCycleCB.setImmediate(true);
            subjectCycleCB.setNullSelectionAllowed(true);
            subjectCycleCB.setTextInputAllowed(false);
            subjectCycleCB.setFilteringMode(FilteringMode.OFF);
            subjectCycleCB.setPageLength(0);
            paramsFL.addComponent(subjectCycleCB);
        } catch (Exception ex) {
            LOG.error("Unable to load subject cycle list: ", ex);
        }

        getContent().addComponent(paramsFL);
        getContent().setComponentAlignment(paramsFL, Alignment.MIDDLE_CENTER);
    }

    public boolean isConsiderCredit() {
        return considerCreditCB.getValue();
    }

    public SUBJECT_CYCLE getSubjectCycle() {
        return (SUBJECT_CYCLE) subjectCycleCB.getValue();
    }

    public String getCode(){
        return codeTF.getValue();
    }

    public EDUCATION_MODULE_TYPE getEducationModuleType(){
        return (EDUCATION_MODULE_TYPE) educationModuleCB.getValue();
    }
}
