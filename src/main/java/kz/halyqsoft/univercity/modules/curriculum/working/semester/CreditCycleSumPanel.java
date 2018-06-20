package kz.halyqsoft.univercity.modules.curriculum.working.semester;

import com.vaadin.data.Container.Indexed;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.Page;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.FooterRow;
import com.vaadin.ui.Grid.HeaderCell;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Label;
import kz.halyqsoft.univercity.entity.beans.univercity.CURRICULUM;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VCurriculumCreditCycleSum;
import kz.halyqsoft.univercity.modules.curriculum.working.AbstractCurriculumPanel;
import kz.halyqsoft.univercity.modules.curriculum.working.CurriculumView;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.vaadin.widget.dialog.Message;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Omarbek Dinassil
 * @created Feb 26, 2016 1:02:58 PM
 */
@SuppressWarnings({"serial"})
public class CreditCycleSumPanel extends AbstractCurriculumPanel {

    private CURRICULUM curriculum;
    private Grid grid;

    public CreditCycleSumPanel(CurriculumView parentView) {
        super(parentView);
    }

    @Override
    public CURRICULUM getCurriculum() {
        return curriculum;
    }

    @Override
    public void setCurriculum(CURRICULUM curriculum) {
        this.curriculum = curriculum;
    }

    @Override
    public void initPanel() {
        Label title = new Label();
        title.setValue(getUILocaleUtil().getEntityLabel(VCurriculumCreditCycleSum.class));
        getContent().addComponent(title);

        grid = new Grid();
        grid.setSizeFull();
        grid.setColumns("cycleShortName", "cycleName", "totalCreditSum", "requiredCreditSum", "electiveCreditSum");
        grid.getColumn("cycleShortName").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(VCurriculumCreditCycleSum.class, "cycleShortName")).setWidth(90);
        grid.getColumn("cycleName").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(VCurriculumCreditCycleSum.class, "cycleName"));
        grid.getColumn("totalCreditSum").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(VCurriculumCreditCycleSum.class, "totalCreditSum")).setWidth(70);
        grid.getColumn("requiredCreditSum").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(VCurriculumCreditCycleSum.class, "requiredCreditSum")).setWidth(120);
        grid.getColumn("electiveCreditSum").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(VCurriculumCreditCycleSum.class, "electiveCreditSum")).setWidth(160);
        grid.setSelectionMode(SelectionMode.NONE);
        grid.setHeightMode(HeightMode.ROW);
        grid.setHeightByRows(8);
        HeaderRow hr = grid.prependHeaderRow();
        HeaderCell hc = hr.join(hr.getCell("totalCreditSum"), hr.getCell("requiredCreditSum"), hr.getCell("electiveCreditSum"));
        hc.setText(getUILocaleUtil().getCaption("credits"));
        Page.getCurrent().getStyles().add(".header-center {text-align: center !important}");
        hc.setStyleName("header-center");
        FooterRow fr = grid.addFooterRowAt(0);
        fr.getCell("cycleName").setText(getUILocaleUtil().getCaption("in.total"));
        getContent().addComponent(grid);
        refresh();
    }

    @Override
    public void refresh() {
        long curriculumId = (curriculum != null && curriculum.getId() != null) ? curriculum.getId().getId().longValue() : -1;
        String sql = "SELECT " +
                "  t1.CYCLE_SHORT_NAME, " +
                "  t1.CYCLE_NAME, " +
                "  coalesce(t1.CREDIT_SUM, 0) + coalesce(t2.CREDIT_SUM, 0) TOTAL_CREDIT_SUM, " +
                "  coalesce(t1.CREDIT_SUM, 0)                              REQUIRED_CREDIT_SUM, " +
                "  coalesce(t2.CREDIT_SUM, 0)                              ELECTIVE_CREDIT_SUM " +
                "FROM (SELECT " +
                "        a.CURRICULUM_ID, " +
                "        b.SUBJECT_CYCLE_ID, " +
                "        c.CYCLE_SHORT_NAME, " +
                "        c.CYCLE_NAME, " +
                "        sum(d.CREDIT) CREDIT_SUM " +
                "      FROM CURRICULUM_DETAIL a INNER JOIN SUBJECT b ON a.SUBJECT_ID = b.ID " +
                "        INNER JOIN SUBJECT_CYCLE c ON b.SUBJECT_CYCLE_ID = c.ID " +
                "        INNER JOIN CREDITABILITY d ON b.CREDITABILITY_ID = d.ID " +
                "      WHERE a.CURRICULUM_ID = ?1 AND a.DELETED = FALSE AND a.CONSIDER_CREDIT = TRUE " +
                "      GROUP BY a.CURRICULUM_ID, b.SUBJECT_CYCLE_ID, c.CYCLE_SHORT_NAME, c.CYCLE_NAME) t1 " +
                "  LEFT JOIN (SELECT " +
                "               a.CURRICULUM_ID, " +
                "               CASE WHEN a.subject_cycle_id IS NULL " +
                "                 THEN cycle.id " +
                "               ELSE a.subject_cycle_id END ELECTIVE_SUBJECT_CYCLE_ID, " +
                "               CASE WHEN " +
                "                 b.CYCLE_SHORT_NAME " +
                "                 IS NULL " +
                "                 THEN cycle.cycle_short_name " +
                "               ELSE b.cycle_short_name END, " +
                "               CASE WHEN b.CYCLE_NAME IS NULL " +
                "                 THEN cycle.cycle_name " +
                "               ELSE b.cycle_name END, " +
                "               sum(credit.credit)          CREDIT_SUM " +
                "             FROM " +
                "               elective_subject a LEFT JOIN SUBJECT_CYCLE b ON a.subject_cycle_id = b.ID " +
                "               INNER JOIN subject subject ON subject.id = a.subject_id " +
                "               INNER JOIN creditability credit ON credit.id = subject.creditability_id " +
                "               INNER JOIN subject_cycle cycle ON cycle.id = subject.subject_cycle_id " +
                "             WHERE a.CURRICULUM_ID = ?1 AND a.DELETED = FALSE AND subject.deleted = FALSE " +
                "             GROUP BY a.CURRICULUM_ID, a.subject_cycle_id, cycle.id, b.CYCLE_SHORT_NAME, " +
                "               b.CYCLE_NAME) t2 " +
                "    ON t1.CURRICULUM_ID = t2.CURRICULUM_ID AND t1.SUBJECT_CYCLE_ID = t2.ELECTIVE_SUBJECT_CYCLE_ID " +
                "UNION " +
                "SELECT " +
                "  t2.CYCLE_SHORT_NAME, " +
                "  t2.CYCLE_NAME, " +
                "  coalesce(t2.CREDIT_SUM, 0) + coalesce(t1.CREDIT_SUM, 0) TOTAL_CREDIT_SUM, " +
                "  coalesce(t1.CREDIT_SUM, 0)                              REQUIRED_CREDIT_SUM, " +
                "  coalesce(t2.CREDIT_SUM, 0)                              ELECTIVE_CREDIT_SUM " +
                "FROM (SELECT " +
                "        a.CURRICULUM_ID, " +
                "        CASE WHEN a.subject_cycle_id IS NULL " +
                "          THEN cycle.id " +
                "        ELSE a.subject_cycle_id END ELECTIVE_SUBJECT_CYCLE_ID, " +
                "        CASE WHEN " +
                "          b.CYCLE_SHORT_NAME " +
                "          IS NULL " +
                "          THEN cycle.cycle_short_name " +
                "        ELSE b.cycle_short_name END cycle_short_name, " +
                "        CASE WHEN b.CYCLE_NAME IS NULL " +
                "          THEN cycle.cycle_name " +
                "        ELSE b.cycle_name END       cycle_name, " +
                "        sum(credit.credit)          CREDIT_SUM " +
                "      FROM " +
                "        elective_subject a LEFT JOIN SUBJECT_CYCLE b ON a.subject_cycle_id = b.ID " +
                "        INNER JOIN subject subject ON subject.id = a.subject_id " +
                "        INNER JOIN creditability credit ON credit.id = subject.creditability_id " +
                "        INNER JOIN subject_cycle cycle ON cycle.id = subject.subject_cycle_id " +
                "      WHERE a.CURRICULUM_ID = ?1 AND a.DELETED = FALSE AND subject.deleted = FALSE " +
                "      GROUP BY a.CURRICULUM_ID, a.subject_cycle_id, cycle.id, b.CYCLE_SHORT_NAME, " +
                "        b.CYCLE_NAME) t2 " +
                "  LEFT JOIN (SELECT " +
                "               a.CURRICULUM_ID, " +
                "               b.SUBJECT_CYCLE_ID, " +
                "               c.CYCLE_SHORT_NAME, " +
                "               c.CYCLE_NAME, " +
                "               sum(d.CREDIT) CREDIT_SUM " +
                "             FROM CURRICULUM_DETAIL a INNER JOIN SUBJECT b ON a.SUBJECT_ID = b.ID " +
                "               INNER JOIN SUBJECT_CYCLE C ON b.SUBJECT_CYCLE_ID = C.ID " +
                "               INNER JOIN CREDITABILITY D ON b.CREDITABILITY_ID = D.ID " +
                "             WHERE a.CURRICULUM_ID = ?1 AND a.DELETED = FALSE AND a.CONSIDER_CREDIT = TRUE " +
                "             GROUP BY a.CURRICULUM_ID, b.SUBJECT_CYCLE_ID, c.CYCLE_SHORT_NAME, c.CYCLE_NAME) t1 " +
                "    ON t1.CURRICULUM_ID = t2.CURRICULUM_ID AND t1.SUBJECT_CYCLE_ID = t2.ELECTIVE_SUBJECT_CYCLE_ID";
        Map<Integer, Object> params = new HashMap<Integer, Object>(5);
        params.put(1, curriculumId);
//        params.put(2, Boolean.FALSE);
//        params.put(3, Boolean.TRUE);
//        params.put(4, curriculumId);
//        params.put(5, Boolean.FALSE);
//        params.put(6, Boolean.FALSE);

        try {
            List tempList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            List<VCurriculumCreditCycleSum> list = new ArrayList<VCurriculumCreditCycleSum>(tempList.size());
            for (Object o : tempList) {
                Object[] oo = (Object[]) o;
                VCurriculumCreditCycleSum vccc = new VCurriculumCreditCycleSum();
                vccc.setCycleShortName((String) oo[0]);
                vccc.setCycleName((String) oo[1]);
                vccc.setTotalCreditSum(((BigDecimal) oo[2]).intValue());
                vccc.setRequiredCreditSum(((BigDecimal) oo[3]).intValue());
                vccc.setElectiveCreditSum(((BigDecimal) oo[4]).intValue());
                list.add(vccc);
            }
            BeanItemContainer<VCurriculumCreditCycleSum> bic = new BeanItemContainer<VCurriculumCreditCycleSum>(VCurriculumCreditCycleSum.class, list);
            grid.setContainerDataSource(bic);
        } catch (Exception ex) {
            LOG.error("Unable to load curriculum credit cycle sum: ", ex);
            Message.showError(ex.toString());
        }

        refreshFooter();
    }

    private void refreshFooter() {
        int totalCreditSum = 0;
        int requiredCreditSum = 0;
        int electiveCreditSum = 0;
        Indexed ds = grid.getContainerDataSource();
        for (Object item : ds.getItemIds()) {
            totalCreditSum += (Integer) ds.getItem(item).getItemProperty("totalCreditSum").getValue();
            requiredCreditSum += (Integer) ds.getItem(item).getItemProperty("requiredCreditSum").getValue();
            electiveCreditSum += (Integer) ds.getItem(item).getItemProperty("electiveCreditSum").getValue();
        }
        FooterRow fr = grid.getFooterRow(0);
        fr.getCell("totalCreditSum").setText(String.valueOf(totalCreditSum));
        fr.getCell("requiredCreditSum").setText(String.valueOf(requiredCreditSum));
        fr.getCell("electiveCreditSum").setText(String.valueOf(electiveCreditSum));
    }

    @Override
    public void save() throws Exception {
    }

    @Override
    protected void cancel() {
    }
}
