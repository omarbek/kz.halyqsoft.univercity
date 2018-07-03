package kz.halyqsoft.univercity.modules.search;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.CARD;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VSearchByCard;
import kz.halyqsoft.univercity.filter.FSearchByCardFilter;
import kz.halyqsoft.univercity.filter.panel.SearchByCardFilterPanel;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchByCardView extends AbstractTaskView implements EntityListener, FilterPanelListener {

    private GridWidget userGW;

    private final SearchByCardFilterPanel filterPanel;

    public SearchByCardView(AbstractTask task) throws Exception {
        super(task);
        filterPanel = new SearchByCardFilterPanel(new FSearchByCardFilter());
    }

    @Override
    public void initView(boolean b) throws Exception {

        filterPanel.addFilterPanelListener((this));

        ComboBox cardsCB = new ComboBox();
        cardsCB.setNullSelectionAllowed(true);
        cardsCB.setTextInputAllowed(true);
        cardsCB.setFilteringMode(FilteringMode.STARTSWITH);
        QueryModel<CARD> cardQM = new QueryModel<>(CARD.class);
        BeanItemContainer<CARD> cardBIC = new BeanItemContainer<>(CARD.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(cardQM));
        cardsCB.setContainerDataSource(cardBIC);
        filterPanel.addFilterComponent("card", cardsCB);

        getContent().addComponent(filterPanel);
        getContent().setComponentAlignment(filterPanel, Alignment.TOP_CENTER);

        userGW = new GridWidget(VSearchByCard.class);
        userGW.showToolbar(false);
        DBGridModel priceGM = (DBGridModel) userGW.getWidgetModel();
        priceGM.setMultiSelect(false);
        priceGM.setRefreshType(ERefreshType.MANUAL);

        refresh();

        getContent().addComponent(userGW);
        getContent().setComponentAlignment(userGW, Alignment.MIDDLE_CENTER);
    }

    public void refresh() throws Exception {
        FSearchByCardFilter byCardFilter = (FSearchByCardFilter) filterPanel.getFilterBean();
        doFilter(byCardFilter);

    }


    @Override
    public void doFilter(AbstractFilterBean abstractFilterBean) {

        FSearchByCardFilter byCardFilter = (FSearchByCardFilter) abstractFilterBean;
        Map<Integer, Object> params = new HashMap<>();
        int i = 1;
        StringBuilder sb = new StringBuilder();
        if (byCardFilter.getCard() != null) {

            sb.append(" and ");
            params.put(i, byCardFilter.getCard().getId().getId());
            sb.append("c2.id = ?" + i++);

        }

        List<VSearchByCard> list = new ArrayList<>();

        sb.insert(0, " where usr.deleted = FALSE ");
        String sql = "select " +
                "  usr.code, " +
                "  trim(usr.LAST_NAME || ' ' || usr.FIRST_NAME || ' ' || coalesce(usr.MIDDLE_NAME, '')) FIO, " +
                "  ut.type_name, " +
                "  card.card_name " +
                " FROM USERS usr " +
                " INNER JOIN card c2 ON usr.card_id = c2.id " +
                " INNER JOIN user_type ut ON usr.user_type_id = ut.id " +
                "  INNER JOIN card card ON usr.card_id = card.id "
                + sb.toString()
                + " ORDER BY FIO;";
        try {
            List tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);

            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VSearchByCard search = new VSearchByCard();

                    search.setCode((String) oo[0]);
                    search.setFio((String) oo[1]);
                    search.setTypeName((String) oo[2]);
                    search.setCardName((String) oo[3]);

                    list.add(search);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load users list", ex);
        }

        refresh(list);

    }

    private void refresh(List<VSearchByCard> list) {
        ((DBGridModel) userGW.getWidgetModel()).setEntities(list);
        try {
            userGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh users list", ex);
        }
    }

    @Override
    public void clearFilter() {
        refresh(new ArrayList<>());
    }
}