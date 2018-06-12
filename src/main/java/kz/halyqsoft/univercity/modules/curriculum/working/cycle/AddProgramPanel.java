package kz.halyqsoft.univercity.modules.curriculum.working.cycle;

import com.vaadin.data.Container.Indexed;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import kz.halyqsoft.univercity.entity.beans.univercity.CURRICULUM;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_CURRICULUM_ADD_PROGRAM;
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
 * @created Feb 29, 2016 2:27:20 PM
 */
@SuppressWarnings("serial")
public class AddProgramPanel extends AbstractCommonPanel {

    private CURRICULUM curriculum;
    private Grid grid;
    private Label inTotalLabel;
    private String inTotalText = getUILocaleUtil().getCaption("in.total");
    private String specTotalText = getUILocaleUtil().getCaption("total.speciality");

    public CURRICULUM getCurriculum() {
        return curriculum;
    }

    public void setCurriculum(CURRICULUM curriculum) {
        this.curriculum = curriculum;
    }

    public void initPanel() {
        Label titleLabel = new Label();
        titleLabel.setValue(getUILocaleUtil().getEntityLabel(V_CURRICULUM_ADD_PROGRAM.class));
        titleLabel.setWidthUndefined();
        titleLabel.addStyleName("select-widget-title");
        getContent().addComponent(titleLabel);
        getContent().setComponentAlignment(titleLabel, Alignment.TOP_LEFT);

        StringBuilder sb = new StringBuilder();
        sb.append(inTotalText);
        sb.append(" - 0");
        sb.append(", ");
        sb.append(specTotalText);
        sb.append(" - 0");
        inTotalLabel = new Label();
        inTotalLabel.setWidthUndefined();
        inTotalLabel.addStyleName("bold");
        inTotalLabel.setValue(sb.toString());
        getContent().addComponent(inTotalLabel);
        getContent().setComponentAlignment(inTotalLabel, Alignment.TOP_CENTER);

        grid = new Grid();
        grid.setSizeFull();
        grid.setColumns("subjectCode", "subjectNameRU", "credit", "semesterName");
        grid.getColumn("subjectCode").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_ADD_PROGRAM.class, "subjectCode")).setWidth(120);
        grid.getColumn("subjectNameRU").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_ADD_PROGRAM.class, "subjectNameRU"));
        grid.getColumn("credit").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_ADD_PROGRAM.class, "credit")).setWidth(80);
        grid.getColumn("semesterName").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_ADD_PROGRAM.class, "semesterName")).setWidth(120);

        getContent().addComponent(grid);
        refresh();
    }

    public void refresh() {
        ID curriculumId = (curriculum != null && curriculum.getId() != null) ? curriculum.getId() : ID.valueOf(-1);
        QueryModel<V_CURRICULUM_ADD_PROGRAM> qm = new QueryModel<V_CURRICULUM_ADD_PROGRAM>(V_CURRICULUM_ADD_PROGRAM.class);
        qm.addWhere("curriculum", ECriteria.EQUAL, curriculumId);
        qm.addWhereAnd("deleted", Boolean.FALSE);

        try {
            List<V_CURRICULUM_ADD_PROGRAM> list = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qm);
            BeanItemContainer<V_CURRICULUM_ADD_PROGRAM> bic = new BeanItemContainer<V_CURRICULUM_ADD_PROGRAM>(V_CURRICULUM_ADD_PROGRAM.class, list);
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
        sb.append(inTotalText);
        sb.append(" - ");
        sb.append(credit);

        totalCredit = credit;

        ID curriculumId = (curriculum != null && curriculum.getId() != null) ? curriculum.getId() : ID.valueOf(-1);
        QueryModel<V_CURRICULUM_DETAIL> qm1 = new QueryModel<V_CURRICULUM_DETAIL>(V_CURRICULUM_DETAIL.class);
        qm1.addSelect("credit", EAggregate.SUM);
        qm1.addWhere("curriculum", ECriteria.EQUAL, curriculumId);
        qm1.addWhereAnd("deleted", Boolean.FALSE);

        try {
            BigDecimal totalTheoretical = (BigDecimal) SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItems(qm1);
            if (totalTheoretical != null) {
                totalCredit += totalTheoretical.intValue();
            }

            sb.append(", ");
            sb.append(specTotalText);
            sb.append(" - ");
            sb.append(totalCredit);
        } catch (Exception ex) {
            LOG.error("Unable to refresh footer: ", ex);
        }

        inTotalLabel.setValue(sb.toString());
    }
}
