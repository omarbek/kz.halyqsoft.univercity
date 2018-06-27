package kz.halyqsoft.univercity.modules.bindingelectivesubject;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.CATALOG_ELECTIVE_SUBJECTS;
import kz.halyqsoft.univercity.entity.beans.univercity.ELECTIVE_BINDED_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ENTRANCE_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import kz.halyqsoft.univercity.filter.FElectiveFilter;
import kz.halyqsoft.univercity.filter.panel.ElectiveFilterPanel;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Omarbek
 * @created on 19.06.2018
 */
public class BindingElectiveSubjectView extends AbstractTaskView implements FilterPanelListener, EntityListener {

    private ElectiveFilterPanel filterPanel;

    private ComboBox specCB;
    private ComboBox yearCB;
    private GridWidget electiveSubjectsGW;

    public BindingElectiveSubjectView(AbstractTask task) throws Exception {
        super(task);
        filterPanel = new ElectiveFilterPanel(new FElectiveFilter());
    }

    @Override
    public void initView(boolean b) throws Exception {
        filterPanel.addFilterPanelListener(this);
        HorizontalLayout componentHL = new HorizontalLayout();

//        Button bindButton = new Button(getUILocaleUtil().getCaption("creation.bind"));
//        bindButton.addClickListener(new Button.ClickListener() {
//            @Override
//            public void buttonClick(Button.ClickEvent clickEvent) {
//                SUBJECT firstSubject = (SUBJECT) specCB.getValue();
//                SUBJECT secondSubject = (SUBJECT) yearCB.getValue();
//                SEMESTER semester = (SEMESTER) semesterCB.getValue();
//
//                if (secondSubject == null || firstSubject == null || semester == null) {
//                    Message.showError(getUILocaleUtil().getMessage("error.required.fields"));
//                    return;
//                }
//                if (firstSubject.equals(secondSubject)) {
//                    Message.showError("equals");//TODO
//                    return;
//                }
//
//                ELECTIVE_BINDED_SUBJECT bindedSubject = new ELECTIVE_BINDED_SUBJECT();
//                bindedSubject.setSecondSubject(secondSubject);
//                bindedSubject.setFirstSubject(firstSubject);
//                bindedSubject.setSemester(semester);
//                bindedSubject.setCreated(new Date());
//                Message.showConfirm(getUILocaleUtil().getMessage("confirmation.save"), new AbstractYesButtonListener() {
//                    @Override
//                    public void buttonClick(Button.ClickEvent clickEvent) {
//                        try {
//                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
//                                    create(bindedSubject);
//                        } catch (Exception e) {
//                            CommonUtils.showMessageAndWriteLog("Unable to bind subjects", e);
//                        }
//                        refresh();
//                    }
//                });
//            }
//        });

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

        electiveSubjectsGW = new GridWidget(ELECTIVE_BINDED_SUBJECT.class);
        electiveSubjectsGW.addEntityListener(this);
        electiveSubjectsGW.setButtonVisible(IconToolbar.EDIT_BUTTON, false);
        electiveSubjectsGW.setButtonVisible(IconToolbar.PREVIEW_BUTTON, false);
        electiveSubjectsGW.setButtonVisible(IconToolbar.REFRESH_BUTTON, false);
        electiveSubjectsGW.setButtonEnabled(IconToolbar.ADD_BUTTON, false);

        DBGridModel electiveSubjectGM = (DBGridModel) electiveSubjectsGW.getWidgetModel();
        electiveSubjectGM.setRowNumberVisible(true);
        electiveSubjectGM.setTitleVisible(false);
        electiveSubjectGM.setMultiSelect(true);
        electiveSubjectGM.setRefreshType(ERefreshType.MANUAL);

        FElectiveFilter ef = (FElectiveFilter) filterPanel.getFilterBean();
        if (ef.hasFilter()) {
            doFilter(ef);
        }
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
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load elective subject list", ex);
            }
        } else {
            electiveSubjectsGW.setButtonEnabled(IconToolbar.ADD_BUTTON, false);
        }

        ((DBGridModel) electiveSubjectsGW.getWidgetModel()).setEntities(list);
        try {
            electiveSubjectsGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh elective subject grid", ex);
        }
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
        try {
            electiveSubjectsGW.refresh();
            doFilter(filterPanel.getFilterBean());
        } catch (Exception e) {
            Message.showError(e.getMessage());
        }
    }

    @Override
    public boolean preSave(Object source, Entity entity, boolean isNew, int buttonId) {
        if (source.equals(electiveSubjectsGW)) {
            try {
                QueryModel<CATALOG_ELECTIVE_SUBJECTS> catQM = new QueryModel<>(CATALOG_ELECTIVE_SUBJECTS.class);
                SPECIALITY spec = (SPECIALITY) specCB.getValue();
                ENTRANCE_YEAR year = (ENTRANCE_YEAR) yearCB.getValue();
                catQM.addWhere("speciality", ECriteria.EQUAL, spec.getId());
                catQM.addWhere("entranceYear", ECriteria.EQUAL, year.getId());
                CATALOG_ELECTIVE_SUBJECTS cat = getCat(catQM, spec, year);
                ELECTIVE_BINDED_SUBJECT electiveBindedSubject = (ELECTIVE_BINDED_SUBJECT) entity;
                electiveBindedSubject.setCatalogElectiveSubjects(cat);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(electiveBindedSubject);
            } catch (Exception e) {
                e.printStackTrace();//TODO catch
            }
        }
        return false;
    }

    private CATALOG_ELECTIVE_SUBJECTS getCat(QueryModel<CATALOG_ELECTIVE_SUBJECTS> catQM,
                                             SPECIALITY spec, ENTRANCE_YEAR year) throws Exception {
        CATALOG_ELECTIVE_SUBJECTS cat;
        try {
            cat = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(catQM);
        } catch (NoResultException ex) {
            cat = new CATALOG_ELECTIVE_SUBJECTS();
            cat.setCreated(new Date());
            cat.setDeleted(Boolean.FALSE);
            cat.setEntranceYear(year);
            cat.setSpeciality(spec);
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(cat);
        }
        return cat;
    }

    @Override
    public boolean preDelete(Object source, List<Entity> entities, int buttonId) {
        if (source.equals(electiveSubjectsGW)) {
            for (Entity entity : entities) {
                try {
                    ELECTIVE_BINDED_SUBJECT electiveBindedSubject = SessionFacadeFactory.getSessionFacade(
                            CommonEntityFacadeBean.class).lookup(ELECTIVE_BINDED_SUBJECT.class,
                            entity.getId());
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(
                            electiveBindedSubject);

                } catch (Exception e) {
                    Message.showError(e.getMessage());
                }
            }
            refresh();
        }
        return false;
    }
}
