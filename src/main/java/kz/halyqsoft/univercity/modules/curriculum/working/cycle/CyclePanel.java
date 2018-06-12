package kz.halyqsoft.univercity.modules.curriculum.working.cycle;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT_CYCLE;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_CURRICULUM_ADD_PROGRAM;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_CURRICULUM_DETAIL;
import kz.halyqsoft.univercity.modules.curriculum.working.CurriculumView;
import kz.halyqsoft.univercity.modules.curriculum.working.AbstractCurriculumPanel;
import kz.halyqsoft.univercity.utils.excel.ExcelStyles;
import kz.halyqsoft.univercity.utils.excel.ExcelUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.select.EAggregate;
import org.r3a.common.entity.query.where.ECriteria;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Omarbek Dinassil
 * @created Feb 29, 2016 10:36:55 AM
 */
@SuppressWarnings("serial")
public class CyclePanel extends AbstractCurriculumPanel {

    private List<CycleDetailPanel> detailPanelList;
    private AddProgramPanel addProgramPanel;
    private String totalTeoretical = getUILocaleUtil().getCaption("total.theoretical");
    private Label totalTeoreticalLabel;

    public CyclePanel(CurriculumView parentView) {
        super(parentView);
    }

    @Override
    public void initPanel() throws Exception {
        totalTeoreticalLabel = new Label();
        totalTeoreticalLabel.setWidthUndefined();
        totalTeoreticalLabel.addStyleName("bold");
        totalTeoreticalLabel.setValue(totalTeoretical + " - 0");
        getContent().addComponent(totalTeoreticalLabel);
        getContent().setComponentAlignment(totalTeoreticalLabel, Alignment.TOP_CENTER);

        //Curriculum detail by cycle
        QueryModel<SUBJECT_CYCLE> qmSubjectCycle = new QueryModel<SUBJECT_CYCLE>(SUBJECT_CYCLE.class);
        qmSubjectCycle.addWhere("id", ECriteria.LESS, 4);
        List<SUBJECT_CYCLE> subjectCycleList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qmSubjectCycle);

        detailPanelList = new ArrayList<CycleDetailPanel>();

        for (SUBJECT_CYCLE sc : subjectCycleList) {
            CycleDetailPanel cdp = new CycleDetailPanel();
            cdp.setWidth(100, Unit.PERCENTAGE);
            cdp.setCurriculum(getCurriculum());
            cdp.setSubjectCycle(sc);
            cdp.setTitle(getUILocaleUtil().getCaption("subject.cycle." + sc.getId().toString()));
            cdp.initPanel();
            detailPanelList.add(cdp);
            getContent().addComponent(cdp);
        }

        addProgramPanel = new AddProgramPanel();
        addProgramPanel.setWidth(100, Unit.PERCENTAGE);
        addProgramPanel.setCurriculum(getCurriculum());
        addProgramPanel.initPanel();
        getContent().addComponent(addProgramPanel);
    }

    @Override
    public void refresh() throws Exception {
        for (CycleDetailPanel cdp : detailPanelList) {
            cdp.setCurriculum(getCurriculum());
            cdp.refresh();
        }
        addProgramPanel.setCurriculum(getCurriculum());
        addProgramPanel.refresh();

        QueryModel<V_CURRICULUM_DETAIL> qm1 = new QueryModel<V_CURRICULUM_DETAIL>(V_CURRICULUM_DETAIL.class);
        qm1.addSelect("credit", EAggregate.SUM);
        qm1.addWhere("curriculum", ECriteria.EQUAL, (getCurriculum() != null && getCurriculum().getId() != null) ? getCurriculum().getId() : ID.valueOf(-1));
        qm1.addWhereAnd("deleted", Boolean.FALSE);

        try {
            BigDecimal totalTheoretical = (BigDecimal) SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItems(qm1);
            if (totalTheoretical == null) {
                totalTheoretical = BigDecimal.ZERO;
            }

            totalTeoreticalLabel.setValue(totalTeoretical + " - " + totalTheoretical.intValue());
        } catch (Exception ex) {
            LOG.error("Unable to refresh footer: ", ex);
        }
    }

    @Override
    public void save() throws Exception {
        //		for (CycleDetailPanel cdp : detailPanelList) {
        //			cdp.save();
        //		}
    }

    @Override
    protected void cancel() {
    }

    public final void checkForApprove() throws Exception {
        for (CycleDetailPanel cdp : detailPanelList) {
            cdp.checkForApprove();
        }
    }

    public void fillWorkBook(Workbook wb) throws Exception {
        Map<ExcelStyles, CellStyle> styles = ExcelUtil.createStyles(wb);

        Sheet sheet = wb.createSheet(getUILocaleUtil().getCaption("by.cycle"));
        sheet.setDisplayGridlines(true);
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue(getUILocaleUtil().getCaption("curriculum.1"));
        cell.setCellStyle(styles.get(ExcelStyles.TITLE));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$A$1:$H$1"));

        row = sheet.createRow(1);
        cell = row.createCell(0);
        cell.setCellValue(getParentView().getSpecialityText());
        cell.setCellStyle(styles.get(ExcelStyles.SUBTITLE_CENTER));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$A$2:$H$2"));

        row = sheet.createRow(4);
        cell = row.createCell(1);
        cell.setCellValue(getParentView().getAcademicDegreeText());
        cell.setCellStyle(styles.get(ExcelStyles.SUBTITLE_LEFT));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$B$5:$H$5"));

        row = sheet.createRow(5);
        cell = row.createCell(1);
        cell.setCellValue(getParentView().getStudyPeriodText());
        cell.setCellStyle(styles.get(ExcelStyles.SUBTITLE_LEFT));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$B$6:$H$6"));

        row = sheet.createRow(6);
        cell = row.createCell(0);
        cell.setCellValue(getUILocaleUtil().getCaption("number"));
        cell.setCellStyle(styles.get(ExcelStyles.HEADER));

        cell = row.createCell(1);
        cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_DETAIL.class, "subjectCode"));
        cell.setCellStyle(styles.get(ExcelStyles.HEADER));

        cell = row.createCell(2);
        cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_DETAIL.class, "subjectName"));
        cell.setCellStyle(styles.get(ExcelStyles.HEADER));

        cell = row.createCell(3);
        cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_DETAIL.class, "cycleShortName"));
        cell.setCellStyle(styles.get(ExcelStyles.HEADER_VERTICAL));

        cell = row.createCell(4);
        cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_DETAIL.class, "credit"));
        cell.setCellStyle(styles.get(ExcelStyles.HEADER_VERTICAL));

        cell = row.createCell(5);
        cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_DETAIL.class, "formula"));
        cell.setCellStyle(styles.get(ExcelStyles.HEADER_VERTICAL));

        cell = row.createCell(6);
        cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_DETAIL.class, "recommendedSemester"));
        cell.setCellStyle(styles.get(ExcelStyles.HEADER_VERTICAL));

        cell = row.createCell(7);
        cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_DETAIL.class, "controlTypeName"));
        cell.setCellStyle(styles.get(ExcelStyles.HEADER_VERTICAL));

        QueryModel<SUBJECT_CYCLE> qmSubjectCycle = new QueryModel<SUBJECT_CYCLE>(SUBJECT_CYCLE.class);
        qmSubjectCycle.addWhere("id", ECriteria.LESS_EQUAL, ID.valueOf(3));
        qmSubjectCycle.addOrder("id");
        List<SUBJECT_CYCLE> subjectCycleList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qmSubjectCycle);

        QueryModel<V_CURRICULUM_DETAIL> qmVCD = new QueryModel<V_CURRICULUM_DETAIL>(V_CURRICULUM_DETAIL.class);
        qmVCD.addWhere("curriculum", ECriteria.EQUAL, getCurriculum().getId());
        qmVCD.addWhere("deleted", Boolean.FALSE);

        int startRow = 7;
        int totalAll = 0;
        for (SUBJECT_CYCLE sc : subjectCycleList) {
            row = sheet.createRow(startRow);
            cell = row.createCell(0);
            cell.setCellValue(getUILocaleUtil().getCaption("subject.cycle." + sc.getId().toString()));
            cell.setCellStyle(styles.get(ExcelStyles.HEADER));
            sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 0, 7));

            startRow++;

            row = sheet.createRow(startRow);
            cell = row.createCell(2);
            cell.setCellValue(getUILocaleUtil().getCaption("obligatory.cources"));
            cell.setCellStyle(styles.get(ExcelStyles.HEADER));

            qmVCD.addWhere("subjectCycle", ECriteria.EQUAL, sc.getId());
            List<V_CURRICULUM_DETAIL> vcdList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qmVCD);

            startRow++;
            int totalRequired = 0;
            int totalElective = 0;
            int totalCycle = 0;
            int i = 1;
            for (V_CURRICULUM_DETAIL vcd : vcdList) {
                if (!vcd.isElective()) {
                    row = sheet.createRow(startRow);
                    cell = row.createCell(0);
                    cell.setCellValue(i);
                    cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

                    cell = row.createCell(1);
                    cell.setCellValue(vcd.getSubjectCode());
                    cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

                    cell = row.createCell(2);
                    cell.setCellValue(vcd.getSubjectName());
                    cell.setCellStyle(styles.get(ExcelStyles.CONTENT_LEFT));

                    cell = row.createCell(3);
                    cell.setCellValue(vcd.getCycleShortName());
                    cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

                    cell = row.createCell(4);
                    cell.setCellValue(vcd.getCredit());
                    cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

                    cell = row.createCell(5);
                    cell.setCellValue(vcd.getFormula());
                    cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

                    cell = row.createCell(6);
                    cell.setCellValue(vcd.getRecommendedSemester());
                    cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

                    cell = row.createCell(7);
                    cell.setCellValue(vcd.getControlTypeName());
                    cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

                    totalRequired += vcd.getCredit();
                    startRow++;
                    i++;
                } else {
                    totalElective += vcd.getCredit();
                }
            }

            totalCycle = totalRequired + totalElective;
            totalAll += totalCycle;

            row = sheet.createRow(startRow++);
            cell = row.createCell(2);
            cell.setCellValue(getUILocaleUtil().getCaption("total"));
            cell.setCellStyle(styles.get(ExcelStyles.FOOTER));

            cell = row.createCell(4);
            cell.setCellValue(totalRequired);
            cell.setCellStyle(styles.get(ExcelStyles.FOOTER));

            row = sheet.createRow(startRow++);
            cell = row.createCell(2);
            cell.setCellValue(getUILocaleUtil().getCaption("elective.cources"));
            cell.setCellStyle(styles.get(ExcelStyles.FOOTER));

            cell = row.createCell(4);
            cell.setCellValue(totalElective);
            cell.setCellStyle(styles.get(ExcelStyles.FOOTER));

            row = sheet.createRow(startRow++);
            cell = row.createCell(2);
            cell.setCellValue(getUILocaleUtil().getCaption("total.cycle." + sc.getId().toString()));
            cell.setCellStyle(styles.get(ExcelStyles.FOOTER));

            cell = row.createCell(4);
            cell.setCellValue(totalCycle);
            cell.setCellStyle(styles.get(ExcelStyles.FOOTER));
        }

        row = sheet.createRow(startRow++);
        cell = row.createCell(2);
        cell.setCellValue(getUILocaleUtil().getCaption("total.theoretical"));
        cell.setCellStyle(styles.get(ExcelStyles.FOOTER));

        cell = row.createCell(4);
        cell.setCellValue(totalAll);
        cell.setCellStyle(styles.get(ExcelStyles.FOOTER));

        startRow++;

        row = sheet.createRow(startRow++);
        cell = row.createCell(0);
        cell.setCellValue(getUILocaleUtil().getCaption("number"));
        cell.setCellStyle(styles.get(ExcelStyles.HEADER));

        cell = row.createCell(1);
        cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_DETAIL.class, "subjectCode"));
        cell.setCellStyle(styles.get(ExcelStyles.HEADER));

        cell = row.createCell(2);
        cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_DETAIL.class, "subjectName"));
        cell.setCellStyle(styles.get(ExcelStyles.HEADER));

        cell = row.createCell(3);
        cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_DETAIL.class, "cycleShortName"));
        cell.setCellStyle(styles.get(ExcelStyles.HEADER_VERTICAL));

        cell = row.createCell(4);
        cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_DETAIL.class, "credit"));
        cell.setCellStyle(styles.get(ExcelStyles.HEADER_VERTICAL));

        cell = row.createCell(5);
        cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_DETAIL.class, "formula"));
        cell.setCellStyle(styles.get(ExcelStyles.HEADER_VERTICAL));

        cell = row.createCell(6);
        cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_DETAIL.class, "recommendedSemester"));
        cell.setCellStyle(styles.get(ExcelStyles.HEADER_VERTICAL));

        cell = row.createCell(7);
        cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_DETAIL.class, "controlTypeName"));
        cell.setCellStyle(styles.get(ExcelStyles.HEADER_VERTICAL));

        row = sheet.createRow(startRow);
        cell = row.createCell(0);
        cell.setCellValue(getUILocaleUtil().getEntityLabel(V_CURRICULUM_ADD_PROGRAM.class));
        cell.setCellStyle(styles.get(ExcelStyles.HEADER));
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 0, 7));

        QueryModel<V_CURRICULUM_ADD_PROGRAM> qmAddProgram = new QueryModel<V_CURRICULUM_ADD_PROGRAM>(V_CURRICULUM_ADD_PROGRAM.class);
        qmAddProgram.addWhere("curriculum", ECriteria.EQUAL, getCurriculum().getId());
        qmAddProgram.addWhereAnd("deleted", Boolean.FALSE);
        List<V_CURRICULUM_ADD_PROGRAM> addProgramList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qmAddProgram);

        startRow++;
        int totalAddProgram = 0;
        int i = 1;
        for (V_CURRICULUM_ADD_PROGRAM vcap : addProgramList) {
            row = sheet.createRow(startRow);
            cell = row.createCell(0);
            cell.setCellValue(i);
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(1);
            cell.setCellValue(vcap.getSubjectCode());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(2);
            cell.setCellValue(vcap.getSubjectNameRU());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_LEFT));

            cell = row.createCell(3);
            cell.setCellValue("");
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(4);
            cell.setCellValue(vcap.getCredit());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(5);
            cell.setCellValue("");
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(6);
            cell.setCellValue(vcap.getSemesterName());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(7);
            cell.setCellValue("");
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            totalAddProgram += vcap.getCredit();
            startRow++;
            i++;
        }

        row = sheet.createRow(startRow++);
        cell = row.createCell(2);
        cell.setCellValue(getUILocaleUtil().getCaption("in.total"));
        cell.setCellStyle(styles.get(ExcelStyles.FOOTER));

        cell = row.createCell(4);
        cell.setCellValue(totalAddProgram);
        cell.setCellStyle(styles.get(ExcelStyles.FOOTER));

        row = sheet.createRow(startRow++);
        cell = row.createCell(2);
        cell.setCellValue(getUILocaleUtil().getCaption("total.speciality"));
        cell.setCellStyle(styles.get(ExcelStyles.FOOTER));

        cell = row.createCell(4);
        cell.setCellValue(totalAll + totalAddProgram);
        cell.setCellStyle(styles.get(ExcelStyles.FOOTER));

        sheet.setColumnWidth(0, 3 * 256);
        sheet.setColumnWidth(2, 32 * 256);
        sheet.setColumnWidth(3, 4 * 256);
        sheet.setColumnWidth(4, 3 * 256);
        sheet.setColumnWidth(5, 6 * 256);
        sheet.setColumnWidth(6, 5 * 256);
        sheet.setColumnWidth(7, 10 * 256);
    }
}
