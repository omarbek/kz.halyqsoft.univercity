package kz.halyqsoft.univercity.modules.bindingelectivesubject;

import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.BaseFont;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.CATALOG_ELECTIVE_SUBJECTS;
import kz.halyqsoft.univercity.entity.beans.univercity.ELECTIVE_BINDED_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.PAIR_SUBJECT;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VAbsents;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_ELECTIVE_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ENTRANCE_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT_REQUISITE;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VPairSubject;
import kz.halyqsoft.univercity.filter.FElectiveFilter;
import kz.halyqsoft.univercity.filter.panel.ElectiveFilterPanel;
import kz.halyqsoft.univercity.modules.userarrival.subview.dialogs.PrintDialog;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.EmployeePdfCreator;
import kz.halyqsoft.univercity.utils.EntityUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.grid.model.GridColumnModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Omarbek
 * @created on 19.06.2018
 */
public class BindingElectiveSubjectView extends AbstractTaskView implements FilterPanelListener {

    private ElectiveFilterPanel filterPanel;
    private ComboBox specCB;
    private ComboBox yearCB;
    private GridWidget electiveSubjectsGW;
    private DBGridModel electiveSubjectGM;
    private BindingElectiveSubjectEdit edit;

    public ComboBox getSpecCB() {
        return specCB;
    }

    public void setSpecCB(ComboBox specCB) {
        this.specCB = specCB;
    }

    public ComboBox getYearCB() {
        return yearCB;
    }

    public void setYearCB(ComboBox yearCB) {
        this.yearCB = yearCB;
    }

    public BindingElectiveSubjectView(AbstractTask task) throws Exception {
        super(task);
        filterPanel = new ElectiveFilterPanel(new FElectiveFilter());
    }

    @Override
    public void initView(boolean b) throws Exception {
        filterPanel.addFilterPanelListener(this);
        HorizontalLayout componentHL = new HorizontalLayout();

        specCB = new ComboBox();
        specCB.setNullSelectionAllowed(true);
        specCB.setTextInputAllowed(true);
        specCB.setFilteringMode(FilteringMode.CONTAINS);
        specCB.setWidth(300, Unit.PIXELS);
        QueryModel<SPECIALITY> specQM = new QueryModel<>(SPECIALITY.class);
        specQM.addWhere("deleted", Boolean.FALSE);
        BeanItemContainer<SPECIALITY> specBIC = new BeanItemContainer<>(SPECIALITY.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(specQM));
        specCB.setContainerDataSource(specBIC);
        filterPanel.addFilterComponent("speciality", specCB);

        yearCB = new ComboBox();
        yearCB.setNullSelectionAllowed(true);
        yearCB.setTextInputAllowed(true);
        yearCB.setFilteringMode(FilteringMode.CONTAINS);
        yearCB.setWidth(300, Unit.PIXELS);
        QueryModel<ENTRANCE_YEAR> yearQM = new QueryModel<>(ENTRANCE_YEAR.class);
        BeanItemContainer<ENTRANCE_YEAR> yearBIC = new BeanItemContainer<>(ENTRANCE_YEAR.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(yearQM));
        yearCB.setContainerDataSource(yearBIC);
        filterPanel.addFilterComponent("entranceYear", yearCB);

        getContent().addComponent(filterPanel);
        getContent().setComponentAlignment(filterPanel, Alignment.TOP_CENTER);

        getContent().addComponent(componentHL);
        getContent().setComponentAlignment(componentHL, Alignment.MIDDLE_CENTER);

        Button printBtn = new Button(CommonUtils.getUILocaleUtil().getCaption("export"));
        printBtn.setImmediate(true);
        printBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                List<String> tableHeader = new ArrayList<>();
                List<List<String>> tableBody = new ArrayList<>();
                String fileName = "document";

                for (GridColumnModel gcm : electiveSubjectGM.getColumnModels()) {
                    tableHeader.add(gcm.getLabel());
                }
                for (int i = 0; i <  electiveSubjectsGW.getAllEntities().size(); i++) {
                    ELECTIVE_BINDED_SUBJECT subject = (ELECTIVE_BINDED_SUBJECT)  electiveSubjectsGW.getAllEntities().get(i);
                    //PAIR_SUBJECT ps = (PAIR_SUBJECT) edit.getPairSubjectGW().getAllEntities();
                    String slqEmpl = "SELECT pair.id,\n" +
                            "      pair.code,\n" +
                            "      subj.name_kz,\n" +
                            "      credit.credit,\n" +
                            "      ects.ects,\n" +
                            "      sem.semester_name semesterName,\n" +
                            "      pair.pair_number    pairNumber,\n" +
                            "      pair.aim,\n" +
                            "      pair.description   description,\n" +
                            "      pair.competence,\n" +
                            "  requisite.pre_requisite\n" +
                            "      FROM pair_subject pair\n" +
                            "        INNER JOIN subject subj ON subj.id = pair.subject_id\n" +
                            "        INNER JOIN creditability credit ON credit.id = subj.creditability_id\n" +
                            "        INNER JOIN ects ects ON ects.id = subj.ects_id\n" +
                            "        INNER JOIN elective_binded_subject elect_bind ON elect_bind.id = pair.elective_binded_subject_id\n" +
                            "        INNER JOIN semester sem ON sem.id = elect_bind.semester_id\n" +
                            "        INNER JOIN subject_requisite requisite on subj.id = requisite.requisite_id\n" +
                            "      WHERE pair.elective_binded_subject_id = "+ subject.getId() +" AND subj.mandatory = FALSE AND subj.subject_cycle_id\n" +
                            "      IS NOT NULL";
                    if (electiveSubjectsGW.getCaption() != null) {
                        fileName = electiveSubjectsGW.getCaption();
                    }
                    List<String> list = new ArrayList<>();
                    list.add(subject.getSemester().toString());

                    try {
                        Map<Integer, Object> para = null;
                        List tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(slqEmpl,
                                para);
                        if (!tmpList.isEmpty()) {
                            for (Object o : tmpList) {
                                Object[] oo = (Object[]) o;
                                VPairSubject pairSubject = new VPairSubject();
                                list.add((String) oo[2]);
                                list.add(((BigDecimal) oo[3]).toString());
                                list.add(((BigDecimal) oo[4]).toString());
                                list.add((String) oo[5]);
                                list.add(((Long) oo[6]).toString());
                                list.add((String) oo[7]);
                                list.add((String) oo[8]);
                                list.add((String) oo[9]);

                                List<String> preRequisites = new ArrayList<>();
                                List<String> postRequisites = new ArrayList<>();

                                QueryModel<SUBJECT_REQUISITE> requisistesQM = new QueryModel<>(SUBJECT_REQUISITE.class);
                                requisistesQM.addWhere("pairSubject", ECriteria.EQUAL, ID.valueOf((long) oo[0]));
                                List<SUBJECT_REQUISITE> requisites = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                        lookup(requisistesQM);
                                for (SUBJECT_REQUISITE requisite : requisites) {
                                    if (requisite.isPreRequisite()) {
                                        preRequisites.add(requisite.getSubject().toString());
                                    } else {
                                        postRequisites.add(requisite.getSubject().toString());
                                    }
                                }
                                list.add(preRequisites.toString());
                                list.add(postRequisites.toString());
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    tableBody.add(list);
                }
                PrintDialog printDialog = new PrintDialog(tableHeader, tableBody, CommonUtils.getUILocaleUtil().getCaption("print"), fileName);
            }
        });

        getContent().addComponent(printBtn);
        getContent().setComponentAlignment(printBtn, Alignment.MIDDLE_CENTER);

        electiveSubjectsGW = new GridWidget(ELECTIVE_BINDED_SUBJECT.class);
        electiveSubjectsGW.addEntityListener(new CreateElectiveSubjectEntity());
        electiveSubjectsGW.setButtonVisible(IconToolbar.PREVIEW_BUTTON, false);
        electiveSubjectsGW.setButtonVisible(IconToolbar.REFRESH_BUTTON, false);
        electiveSubjectsGW.setButtonEnabled(IconToolbar.ADD_BUTTON, false);
        electiveSubjectsGW.setButtonEnabled(IconToolbar.EDIT_BUTTON, false);

        electiveSubjectGM = (DBGridModel) electiveSubjectsGW.getWidgetModel();
        electiveSubjectGM.setMultiSelect(true);
        electiveSubjectGM.setRefreshType(ERefreshType.MANUAL);
        refresh();
        getContent().addComponent(electiveSubjectsGW);
        getContent().setComponentAlignment(electiveSubjectsGW, Alignment.MIDDLE_CENTER);

    }

    @Override
    public void doFilter(AbstractFilterBean abstractFilterBean) {
        FElectiveFilter electiveFilter = (FElectiveFilter) abstractFilterBean;
        List<ELECTIVE_BINDED_SUBJECT> list = new ArrayList<>();
        if (electiveFilter.getSpeciality() != null && electiveFilter.getEntranceYear() != null) {
            try {
                QueryModel<ELECTIVE_BINDED_SUBJECT> electiveBindedSubjectQM = new QueryModel<>(
                        ELECTIVE_BINDED_SUBJECT.class);
                FromItem catFI = electiveBindedSubjectQM.addJoin(EJoin.INNER_JOIN, "catalogElectiveSubjects",
                        CATALOG_ELECTIVE_SUBJECTS.class, "id");
                electiveBindedSubjectQM.addWhere(catFI, "speciality", ECriteria.EQUAL, electiveFilter.getSpeciality().
                        getId());
                electiveBindedSubjectQM.addWhere(catFI, "entranceYear", ECriteria.EQUAL, electiveFilter.getEntranceYear().
                        getId());
                list = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(electiveBindedSubjectQM);
                electiveSubjectsGW.setButtonEnabled(IconToolbar.ADD_BUTTON, true);
                electiveSubjectsGW.setButtonEnabled(IconToolbar.EDIT_BUTTON, true);
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load elective subject list", ex);
            }
        } else {
            electiveSubjectsGW.setButtonEnabled(IconToolbar.ADD_BUTTON, false);
        }
        refresh(list);
    }

    @Override
    public void clearFilter() {
        electiveSubjectsGW.setButtonEnabled(IconToolbar.ADD_BUTTON, false);
        refresh(new ArrayList<>());
    }

    private void refresh(List<ELECTIVE_BINDED_SUBJECT> list) {
        ((DBGridModel) electiveSubjectsGW.getWidgetModel()).setEntities(list);
        try {
            electiveSubjectsGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh elective subject grid", ex);
        }
    }

    public void refresh() {
        FElectiveFilter ef = (FElectiveFilter) filterPanel.getFilterBean();
        if (ef.hasFilter()) {
            doFilter(ef);
        }
    }

    private class CreateElectiveSubjectEntity extends EntityUtils {

        @Override
        protected void init(Object source, Entity e, boolean isNew) throws Exception {
            ELECTIVE_BINDED_SUBJECT electiveBinded = (ELECTIVE_BINDED_SUBJECT) e;
            new BindingElectiveSubjectEdit((SPECIALITY) specCB.getValue(), electiveBinded, isNew,
                    BindingElectiveSubjectView.this);
        }

        @Override
        protected GridWidget getGridWidget() {
            return electiveSubjectsGW;
        }

        @Override
        protected String getModuleName() {
            return "binded elective subject";
        }

        @Override
        protected Class<? extends Entity> getEntityClass() {
            return ELECTIVE_BINDED_SUBJECT.class;
        }

        @Override
        protected void refresh() throws Exception {
            BindingElectiveSubjectView.this.refresh();
        }

        @Override
        protected void removeChildrenEntity(List<Entity> delList) throws Exception {
            for (Entity entity : delList) {
                QueryModel<PAIR_SUBJECT> pairSubjectQM = new QueryModel<>(PAIR_SUBJECT.class);
                pairSubjectQM.addWhere("electiveBindedSubject", ECriteria.EQUAL, entity.getId());
                List<PAIR_SUBJECT> subjects = SessionFacadeFactory.getSessionFacade(
                        CommonEntityFacadeBean.class).lookup(pairSubjectQM);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(subjects);
            }
        }
    }
}
