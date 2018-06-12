package kz.halyqsoft.univercity.modules.curriculum.working.cycle;

import com.vaadin.data.Container.Indexed;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import kz.halyqsoft.univercity.entity.beans.univercity.CURRICULUM;
import kz.halyqsoft.univercity.entity.beans.univercity.CURRICULUM_DETAIL;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT_CYCLE;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_CURRICULUM_DETAIL;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.select.EAggregate;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.AbstractCommonPanel;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Omarbek Dinassil
 * @created Feb 29, 2016 10:57:37 AM
 */
@SuppressWarnings({"serial"})
final class CycleDetailPanel extends AbstractCommonPanel {

    private String title;
    private CURRICULUM curriculum;
    private SUBJECT_CYCLE subjectCycle;
    private Grid grid;
    private Label totalLabel;
    private String totalText = getUILocaleUtil().getCaption("total");
    private String electiveText = getUILocaleUtil().getCaption("elective.cources");
    private String cycleText;

    public CycleDetailPanel() {
        super();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public CURRICULUM getCurriculum() {
        return curriculum;
    }

    public void setCurriculum(CURRICULUM curriculum) {
        this.curriculum = curriculum;
    }

    public SUBJECT_CYCLE getSubjectCycle() {
        return subjectCycle;
    }

    public void setSubjectCycle(SUBJECT_CYCLE subjectCycle) {
        this.subjectCycle = subjectCycle;
    }

    public void initPanel() {
        Label titleLabel = new Label();
        titleLabel.setValue(title);
        titleLabel.setWidthUndefined();
        titleLabel.addStyleName("select-widget-title");
        getContent().addComponent(titleLabel);
        getContent().setComponentAlignment(titleLabel, Alignment.TOP_LEFT);

        cycleText = getUILocaleUtil().getCaption("total.cycle." + subjectCycle.getId().toString());
        StringBuilder sb = new StringBuilder();
        sb.append(totalText);
        sb.append(" - 0, ");
        sb.append(electiveText);
        sb.append(" - 0, ");
        sb.append(cycleText);
        sb.append(" - 0");

        totalLabel = new Label();
        totalLabel.setWidthUndefined();
        totalLabel.addStyleName("bold");
        totalLabel.setValue(sb.toString());
        getContent().addComponent(totalLabel);
        getContent().setComponentAlignment(totalLabel, Alignment.TOP_CENTER);

        grid = new Grid();
        grid.setSizeFull();
        grid.setColumns("subjectCode", "subjectName", "cycleShortName", "credit", "formula", "recommendedSemester", "controlTypeName");
        grid.getColumn("subjectCode").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_DETAIL.class, "subjectCode"));
        grid.getColumn("subjectName").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_DETAIL.class, "subjectName"));
        grid.getColumn("cycleShortName").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_DETAIL.class, "cycleShortName"));
        grid.getColumn("credit").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_DETAIL.class, "credit"));
        grid.getColumn("formula").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_DETAIL.class, "formula"));
        grid.getColumn("recommendedSemester").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_DETAIL.class, "recommendedSemester"));
        grid.getColumn("controlTypeName").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_DETAIL.class, "controlTypeName"));
        grid.setHeightMode(HeightMode.ROW);
        grid.setHeightByRows(10);

        getContent().addComponent(grid);
        refresh();
    }

    public void refresh() {
        ID curriculumId = (curriculum != null && curriculum.getId() != null) ? curriculum.getId() : ID.valueOf(-1);
        QueryModel<V_CURRICULUM_DETAIL> qm = new QueryModel<V_CURRICULUM_DETAIL>(V_CURRICULUM_DETAIL.class);
        qm.addWhere("curriculum", ECriteria.EQUAL, curriculumId);
        qm.addWhereAnd("subjectCycle", ECriteria.EQUAL, subjectCycle.getId());
        qm.addWhereAnd("elective", Boolean.FALSE);
        qm.addWhereAnd("deleted", Boolean.FALSE);

        try {
            List<V_CURRICULUM_DETAIL> list = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qm);
            QueryModel<CURRICULUM_DETAIL> cdQM = new QueryModel<CURRICULUM_DETAIL>(CURRICULUM_DETAIL.class);
            cdQM.addSelect("semester");
            cdQM.addWhere("curriculum", ECriteria.EQUAL, curriculumId);
            for (V_CURRICULUM_DETAIL vcd : list) {
                V_CURRICULUM_DETAIL vcd1 = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(V_CURRICULUM_DETAIL.class, vcd.getId());
                cdQM.addWhere("subject", ECriteria.EQUAL, vcd1.getSubject().getId());
                List list1 = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(cdQM);
                StringBuilder sb = new StringBuilder();
                boolean first = true;
                for (Object o : list1) {
                    if (!first) {
                        sb.append(", ");
                    }
                    sb.append(o);
                    first = false;
                }

                vcd.setRecommendedSemester(sb.toString());
            }

            BeanItemContainer<V_CURRICULUM_DETAIL> bic = new BeanItemContainer<V_CURRICULUM_DETAIL>(V_CURRICULUM_DETAIL.class, list);
            grid.setContainerDataSource(bic);
        } catch (Exception ex) {
            LOG.error("Unable to load curriculum details: ", ex);
        }

        refreshFooter();
    }

    private void refreshFooter() {
        int credit = 0;
        int totalCredit = 0;
        Indexed ds = grid.getContainerDataSource();
        for (Object item : ds.getItemIds()) {
            credit += (Integer) ds.getItem(item).getItemProperty("credit").getValue();
        }

        StringBuilder sb = new StringBuilder();
        sb.append(totalText);
        sb.append(" - ");
        sb.append(credit);
        sb.append(", ");

        totalCredit = credit;

        ID curriculumId = (curriculum != null && curriculum.getId() != null) ? curriculum.getId() : ID.valueOf(-1);
        QueryModel<V_CURRICULUM_DETAIL> qm = new QueryModel<V_CURRICULUM_DETAIL>(V_CURRICULUM_DETAIL.class);
        qm.addSelect("credit", EAggregate.SUM);
        qm.addWhere("curriculum", ECriteria.EQUAL, curriculumId);
        qm.addWhereAnd("subjectCycle", ECriteria.EQUAL, subjectCycle.getId());
        qm.addWhereAnd("elective", Boolean.TRUE);
        qm.addWhereAnd("deleted", Boolean.FALSE);

        try {
            BigDecimal electiveCredit = (BigDecimal) SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItems(qm);
            if (electiveCredit == null) {
                electiveCredit = BigDecimal.ZERO;
            }

            totalCredit += electiveCredit.intValue();
            sb.append(electiveText);
            sb.append(" - ");
            sb.append(electiveCredit.intValue());
            sb.append(", ");
        } catch (Exception ex) {
            LOG.error("Unable to refresh footer: ", ex);
        }

        sb.append(cycleText);
        sb.append(" - ");
        sb.append(totalCredit);
        totalLabel.setValue(sb.toString());
    }

    public void save() throws Exception {
        //		if (curriculum != null && !curriculum.getCurriculumStatus().getId().equals(ID.valueOf(3))) {
        //			List<CURRICULUM_DETAIL> cdList = new ArrayList<CURRICULUM_DETAIL>();
        //			Indexed ds = grid.getContainerDataSource();
        //			try {
        //				for (Object item : ds.getItemIds()) {
        //					V_CURRICULUM_DETAIL vcd = (V_CURRICULUM_DETAIL)item;
        //					CURRICULUM_DETAIL cd = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(CURRICULUM_DETAIL.class, vcd.getId());
        //					if (vcd.getRecommendedSemester() != null && !vcd.getRecommendedSemester().equals(cd.getRecommendedSemester())) {
        //						cd.setRecommendedSemester(vcd.getRecommendedSemester());
        //						cdList.add(cd);
        //					}
        //				}
        //
        //				if (!cdList.isEmpty()) {
        //					SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(cdList);
        //				}
        //			} catch (Exception ex) {
        //				LOG.error("Unable to save changes: ", ex);
        //				Message.showError(ex.toString());
        //			}
        //		}
    }

    public final void checkForApprove() throws Exception {
        QueryModel<V_CURRICULUM_DETAIL> qm = new QueryModel<V_CURRICULUM_DETAIL>(V_CURRICULUM_DETAIL.class);
        qm.addWhere("curriculum", ECriteria.EQUAL, curriculum.getId());
        qm.addWhereAnd("subjectCycle", ECriteria.EQUAL, subjectCycle.getId());
        qm.addWhereAnd("elective", Boolean.FALSE);
        qm.addWhereAnd("deleted", Boolean.FALSE);

        try {
            List<V_CURRICULUM_DETAIL> list = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qm);
            for (V_CURRICULUM_DETAIL vcd : list) {
                if (vcd.getRecommendedSemester() == null || vcd.getRecommendedSemester().trim().isEmpty()) {
                    throw new Exception(String.format(getUILocaleUtil().getMessage("cycle.recommended.semester.not.filled"), subjectCycle.getCycleShortName()));
                }
            }
        } catch (Exception ex) {
            LOG.error("Unable to check for approve: ", ex);
            throw ex;
        }
    }
}
