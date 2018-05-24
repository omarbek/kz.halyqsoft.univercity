package kz.halyqsoft.univercity.modules.accountant;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LEVEL;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDENT_DIPLOMA_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VAccountants;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VCreativeExam;
import kz.halyqsoft.univercity.filter.FAccountantFilter;
import kz.halyqsoft.univercity.filter.FEmployeeFilter;
import kz.halyqsoft.univercity.filter.FStudentFilter;
import kz.halyqsoft.univercity.filter.panel.AccountantFilterPanel;
import kz.halyqsoft.univercity.filter.panel.EmployeeFilterPanel;
import kz.halyqsoft.univercity.modules.creativeexams.CreativeExamEdit;
import kz.halyqsoft.univercity.modules.creativeexams.CreativeExamView;
import kz.halyqsoft.univercity.modules.employee.EmployeeEdit;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.EntityUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;
import org.r3a.common.vaadinaddon.DoubleField;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AccountantView extends AbstractTaskView implements EntityListener, FilterPanelListener {

    private final AccountantFilterPanel filterPanel;
    private GridWidget priceGW;

    public AccountantView(AbstractTask task) throws Exception {
        super(task);
        filterPanel = new AccountantFilterPanel(new FAccountantFilter());
    }

    @Override
    public void initView(boolean b) throws Exception {
        filterPanel.addFilterPanelListener(this);

        ComboBox diplomaComboBox = new ComboBox();
        diplomaComboBox.setNullSelectionAllowed(true);
        diplomaComboBox.setTextInputAllowed(true);
        diplomaComboBox.setFilteringMode(FilteringMode.CONTAINS);
        diplomaComboBox.setPageLength(0);
        diplomaComboBox.setWidth(300, Unit.PIXELS);
        QueryModel<STUDENT_DIPLOMA_TYPE> diplomaQM = new QueryModel<>(STUDENT_DIPLOMA_TYPE.class);
        BeanItemContainer<STUDENT_DIPLOMA_TYPE> diplomaBIC = new BeanItemContainer<>(STUDENT_DIPLOMA_TYPE.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(diplomaQM));
        diplomaComboBox.setContainerDataSource(diplomaBIC);
        filterPanel.addFilterComponent("diplomaType", diplomaComboBox);

        ComboBox levelComboBox = new ComboBox();
        levelComboBox.setNullSelectionAllowed(true);
        levelComboBox.setTextInputAllowed(true);
        levelComboBox.setFilteringMode(FilteringMode.CONTAINS);
        levelComboBox.setPageLength(0);
        levelComboBox.setWidth(300, Unit.PIXELS);
        QueryModel<LEVEL> levelQM = new QueryModel<>(LEVEL.class);
        BeanItemContainer<LEVEL> levelBIC = new BeanItemContainer<>(LEVEL.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(levelQM));
        levelComboBox.setContainerDataSource(levelBIC);
        filterPanel.addFilterComponent("level", levelComboBox);

        ComboBox paymentComboBox = new ComboBox();
        paymentComboBox.setNullSelectionAllowed(true);
        paymentComboBox.setTextInputAllowed(true);
        paymentComboBox.setFilteringMode(FilteringMode.OFF);
        paymentComboBox.setPageLength(0);
        paymentComboBox.setWidth(300, Unit.PIXELS);
        QueryModel<CONTRACT_PAYMENT_TYPE> contractQM = new QueryModel<>(CONTRACT_PAYMENT_TYPE.class);
        BeanItemContainer<CONTRACT_PAYMENT_TYPE> contractBIC = new BeanItemContainer<>(CONTRACT_PAYMENT_TYPE.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(contractQM));
        paymentComboBox.setContainerDataSource(contractBIC);
        filterPanel.addFilterComponent("contractPaymentType", paymentComboBox);

        getContent().addComponent(filterPanel);
        getContent().setComponentAlignment(filterPanel, Alignment.TOP_CENTER);

        priceGW = new GridWidget(VAccountants.class);
        priceGW.addEntityListener(new CreateAccountantEntity());
        priceGW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, false);
        priceGW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
        DBGridModel priceGM = (DBGridModel) priceGW.getWidgetModel();
        priceGM.setMultiSelect(true);
        priceGM.setRefreshType(ERefreshType.MANUAL);

        refresh();

        getContent().addComponent(priceGW);
        getContent().setComponentAlignment(priceGW, Alignment.MIDDLE_CENTER);

    }

    @Override
    public void doFilter(AbstractFilterBean abstractFilterBean) {
        FAccountantFilter sf = (FAccountantFilter) abstractFilterBean;
        Map<Integer, Object> params = new HashMap<>();
        int i = 1;
        StringBuilder sb = new StringBuilder();
        if (sf.getLevel() != null) {

            sb.append(" and ");
            params.put(i, sf.getLevel().getId().getId());
            sb.append("lvl.ID = ?" + i++);

        }

        if (sf.getContractPaymentType() != null) {

            sb.append(" and ");
            params.put(i, sf.getContractPaymentType().getId().getId());
            sb.append("cont_pay_type.ID = ?" + i++);

        }
        if (sf.getDiplomaType() != null) {

            sb.append(" and ");
            params.put(i, sf.getDiplomaType().getId().getId());
            sb.append("stud_diploma_type.ID = ?" + i++);

        }

        List<VAccountants> list = new ArrayList<>();

        sb.insert(0, " where acc_price.deleted = false ");
        String sql = "SELECT acc_price.id, stud_diploma_type.type_name, lvl.level_name, cont_pay_type.type_name, " +
                "  acc_price.price, acc_price.price_in_letters FROM accountant_price acc_price " +
                "  INNER JOIN student_diploma_type stud_diploma_type ON acc_price.student_diploma_type_id = stud_diploma_type.id " +
                "  INNER JOIN level lvl ON acc_price.level_id = lvl.id " +
                "  INNER JOIN contract_payment_type cont_pay_type ON acc_price.contract_payment_type_id = cont_pay_type.id"
                + sb.toString();
        try {
            List tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VAccountants accountants = new VAccountants();
                    accountants.setId(ID.valueOf((long) oo[0]));
                    accountants.setDiplomaType((String) oo[1]);
                    accountants.setLevel((String) oo[2]);
                    accountants.setContractPaymentType((String) oo[3]);
                    accountants.setPrice(((BigDecimal) oo[4]).doubleValue());
                    accountants.setPriceInLetters((String) oo[5]);
                    list.add(accountants);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load accounts list", ex);
        }


        refresh(list);
    }


    private void refresh(List<VAccountants> list) {
        ((DBGridModel) priceGW.getWidgetModel()).setEntities(list);
        try {
            priceGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh accounts list", ex);
        }
    }

    @Override
    public void clearFilter() {
        refresh(new ArrayList<>());
    }


    public void refresh() throws Exception {
        FAccountantFilter ef = (FAccountantFilter) filterPanel.getFilterBean();
        doFilter(ef);

    }

    private class CreateAccountantEntity extends EntityUtils {

        @Override
        public void init(Object source, Entity e, boolean isNew) throws Exception {
            VAccountants vaccountants = (VAccountants) e;
            new AccountantEdit(vaccountants, isNew, AccountantView.this);

        }

        @Override
        public boolean preDelete(Object o, List<Entity> list, int i) {
            List<ACCOUNTANT_PRICE> delList = new ArrayList<>();
            for (Entity entity : list) {
                try {
                    delList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                            lookup(ACCOUNTANT_PRICE.class, entity.getId()));

                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to delete account", ex);
                }
            }

            for (ACCOUNTANT_PRICE entity : delList) {
                entity.setDeleted(true);
            }

            try {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(delList);
                priceGW.refresh();
                refresh();
            } catch (Exception ex) {
                CommonUtils.LOG.error("Unable to delete account: ", ex);
                Message.showError(getUILocaleUtil().getMessage("error.cannotdelentity"));
            }

            return false;
        }

        @Override
        protected GridWidget getGridWidget() {
            return priceGW;
        }


        @Override
        public String getModuleName() {
            return "Accountant edit";
        }

        @Override
        public Class<? extends Entity> getEntityClass() {
            return ACCOUNTANT_PRICE.class;
        }

        @Override
        protected void removeChildrenEntity(List<Entity> delList) throws Exception {

        }


        @Override
        protected void refresh() throws Exception {
            AccountantView.this.refresh();
        }
    }
}