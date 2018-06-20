package kz.halyqsoft.univercity.modules.bindingelectivesubject;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.ELECTIVE_BINDED_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VElectiveBindedSubject;
import kz.halyqsoft.univercity.filter.FElectiveFilter;
import kz.halyqsoft.univercity.filter.panel.ElectiveFilterPanel;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.AbstractYesButtonListener;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.util.*;

/**
 * @author Omarbek
 * @created on 19.06.2018
 */
public class BindingElectiveSubjectView extends AbstractTaskView implements EntityListener, FilterPanelListener {

    private ElectiveFilterPanel filterPanel;

    private ComboBox firstSubjectCB;
    private ComboBox secondSubjectCB;
    private GridWidget electiveSubjectsGW;

    public BindingElectiveSubjectView(AbstractTask task) throws Exception {
        super(task);
        filterPanel = new ElectiveFilterPanel(new FElectiveFilter());
    }

    @Override
    public void initView(boolean b) throws Exception {
        filterPanel.addFilterPanelListener(this);

        Button bindButton = new Button(getUILocaleUtil().getCaption("creation.bind"));
        HorizontalLayout componentHL = new HorizontalLayout();

        bindButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                SUBJECT firstSubject = (SUBJECT) firstSubjectCB.getValue();
                SUBJECT secondSubject = (SUBJECT) secondSubjectCB.getValue();

                if (secondSubject == null || firstSubject == null) {
                    Message.showError(getUILocaleUtil().getMessage("error.required.fields"));
                    return;
                }
                if(firstSubject.equals(secondSubject)){
                    Message.showError("equals");//TODO
                    return;
                }

                ELECTIVE_BINDED_SUBJECT bindedSubject = new ELECTIVE_BINDED_SUBJECT();
                bindedSubject.setSecondSubject(secondSubject);
                bindedSubject.setFirstSubject(firstSubject);
                bindedSubject.setCreated(new Date());
                Message.showConfirm(getUILocaleUtil().getMessage("confirmation.save"), new AbstractYesButtonListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {
                        try {
                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                    create(bindedSubject);
                        } catch (Exception e) {
                            CommonUtils.showMessageAndWriteLog("Unable to bind subjects", e);
                        }
                        refresh();
                    }
                });
            }
        });

        firstSubjectCB = new ComboBox();
        firstSubjectCB.setNullSelectionAllowed(true);
        firstSubjectCB.setTextInputAllowed(true);
        firstSubjectCB.setFilteringMode(FilteringMode.CONTAINS);
        firstSubjectCB.setWidth(300, Unit.PIXELS);
        QueryModel<SUBJECT> firstSubjectQM = new QueryModel<>(SUBJECT.class);
        firstSubjectQM.addWhere("mandatory",Boolean.FALSE);
        BeanItemContainer<SUBJECT> firstSubjectBIC = new BeanItemContainer<>(SUBJECT.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(firstSubjectQM));
        firstSubjectCB.setContainerDataSource(firstSubjectBIC);
        filterPanel.addFilterComponent("firstSubject", firstSubjectCB);

        secondSubjectCB = new ComboBox();
        secondSubjectCB.setNullSelectionAllowed(true);
        secondSubjectCB.setTextInputAllowed(true);
        secondSubjectCB.setFilteringMode(FilteringMode.CONTAINS);
        secondSubjectCB.setWidth(300, Unit.PIXELS);
        QueryModel<SUBJECT> secondSubjectQM = new QueryModel<>(SUBJECT.class);
        secondSubjectQM.addWhere("mandatory",Boolean.FALSE);
        BeanItemContainer<SUBJECT> secondSubjectBIC = new BeanItemContainer<>(SUBJECT.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(secondSubjectQM));
        secondSubjectCB.setContainerDataSource(secondSubjectBIC);
        filterPanel.addFilterComponent("secondSubject", secondSubjectCB);

        filterPanel.getContent().addComponentAsFirst(firstSubjectCB);

        componentHL.addComponent(bindButton);
        componentHL.setComponentAlignment(bindButton, Alignment.MIDDLE_CENTER);


        getContent().addComponent(filterPanel);
        getContent().setComponentAlignment(filterPanel, Alignment.TOP_CENTER);

        getContent().addComponent(componentHL);
        getContent().setComponentAlignment(componentHL, Alignment.MIDDLE_CENTER);

        electiveSubjectsGW = new GridWidget(VElectiveBindedSubject.class);
        electiveSubjectsGW.addEntityListener(this);
        electiveSubjectsGW.setButtonVisible(IconToolbar.EDIT_BUTTON, false);
        electiveSubjectsGW.setButtonVisible(IconToolbar.ADD_BUTTON, false);
        electiveSubjectsGW.setButtonVisible(IconToolbar.PREVIEW_BUTTON, false);
        electiveSubjectsGW.setButtonVisible(IconToolbar.REFRESH_BUTTON, false);

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
        FElectiveFilter urf = (FElectiveFilter) abstractFilterBean;
        int i = 1;
        Map<Integer, Object> params = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        if (urf.getFirstSubject() != null) {
            params.put(i, urf.getFirstSubject().getId().getId());
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("elective_binded_subj.first_subject_id = ?");
            sb.append(i++);
        }
        if (urf.getSecondSubject() != null) {
            params.put(i, urf.getSecondSubject().getId().getId());
            if (sb.length() > 0) {
                sb.append(" and ");
            }
            sb.append("elective_binded_subj.second_subject_id= ?");
            sb.append(i++);
        }

        List<VElectiveBindedSubject> list = new ArrayList<>();
        if (sb.length() > 0) {


            String sql = "SELECT " +
                    "  elective_binded_subj.id, " +
                    "  first.name_ru  firstSubjectName, " +
                    "  second.name_ru secondSubjectName " +
                    "FROM elective_binded_subject elective_binded_subj " +
                    "  INNER JOIN subject first ON first.id = elective_binded_subj.first_subject_id " +
                    "  INNER JOIN subject second ON second.id = elective_binded_subj.second_subject_id" +
                    " where " + sb.toString();
            try {
                List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                        lookupItemsList(sql, params);
                if (!tmpList.isEmpty()) {
                    for (Object o : tmpList) {
                        Object[] oo = (Object[]) o;
                        VElectiveBindedSubject electiveBindedSubject = new VElectiveBindedSubject();
                        electiveBindedSubject.setId(ID.valueOf((long) oo[0]));
                        electiveBindedSubject.setFirstSubjectName((String) oo[1]);
                        electiveBindedSubject.setSecondSubjectName((String) oo[2]);
                        list.add(electiveBindedSubject);
                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load elective subject list", ex);
            }
        } else {
            Message.showInfo(getUILocaleUtil().getMessage("select.1.search.condition"));
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
        refresh(new ArrayList<>());
    }

    private void refresh(List<VElectiveBindedSubject> list) {
        ((DBGridModel) electiveSubjectsGW.getWidgetModel()).setEntities(list);
        try {
            electiveSubjectsGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh elective subject grid", ex);
        }
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

    private void refresh() {
        try {
            electiveSubjectsGW.refresh();
            doFilter(filterPanel.getFilterBean());
        } catch (Exception e) {
            Message.showError(e.getMessage());
        }
    }
}
