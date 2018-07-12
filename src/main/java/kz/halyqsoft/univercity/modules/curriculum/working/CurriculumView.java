package kz.halyqsoft.univercity.modules.curriculum.working;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.*;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.TabSheet.SelectedTabChangeEvent;
import com.vaadin.ui.TabSheet.SelectedTabChangeListener;
import kz.halyqsoft.univercity.entity.beans.univercity.CURRICULUM;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VCurriculumCreditCycleSum;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_CURRICULUM_ADD_PROGRAM;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_CURRICULUM_AFTER_SEMESTER;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_CURRICULUM_DETAIL;
import kz.halyqsoft.univercity.modules.curriculum.working.cycle.CyclePanel;
import kz.halyqsoft.univercity.modules.curriculum.working.schedule.SchedulePanel;
import kz.halyqsoft.univercity.modules.curriculum.working.semester.AddProgramPanel;
import kz.halyqsoft.univercity.modules.curriculum.working.semester.AfterSemesterProgamPanel;
import kz.halyqsoft.univercity.modules.curriculum.working.semester.CreditCycleSumPanel;
import kz.halyqsoft.univercity.modules.curriculum.working.semester.SemesterDetailPanel;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.excel.ExcelStyles;
import kz.halyqsoft.univercity.utils.excel.ExcelUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.Message;

import javax.persistence.NoResultException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.Calendar;

/**
 * @author Omarbek Dinassil
 * @created Feb 17, 2016 5:12:46 PM
 */
@SuppressWarnings({"serial"})
public final class CurriculumView extends AbstractTaskView implements EntityListener {

    private static final int NUMBER_OF_YEARS_AHEAD = 1;
    private final static int IN_CREATING = 1;

    private CURRICULUM curriculum;
    private ComboBox specialityCB;
    private ComboBox entranceYearCB;
    private ComboBox diplomaTypeCB;
    private Label statusLabel;
    private String createStatus;
    private final Label errorLabel = new Label();
    private Label academicDegreeLabel;
    private Label studyPeriodLabel;
    private Label creditSumLabel;
    private Label totalCreditSumLabel;
    private CyclePanel cyclePanel;
    private SchedulePanel schedulePanel;
    private List<SemesterDetailPanel> detailPanelList;
    private AddProgramPanel addProgramPanel;
    private AfterSemesterProgamPanel afterSemesterProgamPanel;
    private CreditCycleSumPanel cycleSumPanel;
    private Button save;
    private Button conform;
    private Button approve;
    private int totalRequired = 0;
    private int totalAdditional = 0;

    public CurriculumView(AbstractTask task) throws Exception {
        super(task);
    }


    @Override
    public void initView(boolean readOnly) throws Exception {
        HorizontalLayout filterHL = new HorizontalLayout();
        filterHL.setSpacing(true);
        filterHL.setMargin(true);
        filterHL.addStyleName("form-panel");

        Label specialityLabel = new Label();
        specialityLabel.addStyleName("bold");
        specialityLabel.setWidthUndefined();
        specialityLabel.setValue(getUILocaleUtil().getEntityFieldLabel(CURRICULUM.class, "speciality"));
        filterHL.addComponent(specialityLabel);

        QueryModel<SPECIALITY> specialityQM = new QueryModel<SPECIALITY>(SPECIALITY.class);
        specialityQM.addWhere("deleted", Boolean.FALSE);
        specialityQM.addOrder("specName");
        BeanItemContainer<SPECIALITY> specialityBIC = new BeanItemContainer<SPECIALITY>(SPECIALITY.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(specialityQM));
        specialityCB = new ComboBox();
        specialityCB.setContainerDataSource(specialityBIC);
        specialityCB.setImmediate(true);
        specialityCB.setNullSelectionAllowed(true);
        specialityCB.setTextInputAllowed(true);
        specialityCB.setFilteringMode(FilteringMode.STARTSWITH);
        specialityCB.setWidth(400, Unit.PIXELS);
        specialityCB.setPageLength(0);
        filterHL.addComponent(specialityCB);

        Label entranceYearLabel = new Label();
        entranceYearLabel.addStyleName("bold");
        entranceYearLabel.setWidthUndefined();
        entranceYearLabel.setValue(getUILocaleUtil().getEntityFieldLabel(CURRICULUM.class, "entranceYear"));
        filterHL.addComponent(entranceYearLabel);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        QueryModel<ENTRANCE_YEAR> entranceYearQM = new QueryModel<ENTRANCE_YEAR>(ENTRANCE_YEAR.class);
        entranceYearQM.addWhere("beginYear", ECriteria.LESS_EQUAL, currentYear + NUMBER_OF_YEARS_AHEAD);
        entranceYearQM.addWhereAnd("endYear", ECriteria.LESS_EQUAL, currentYear + NUMBER_OF_YEARS_AHEAD);
        entranceYearQM.addOrderDesc("beginYear");
        BeanItemContainer<ENTRANCE_YEAR> entranceYearBIC = new BeanItemContainer<ENTRANCE_YEAR>(
                ENTRANCE_YEAR.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
                .lookup(entranceYearQM));
        entranceYearCB = new ComboBox();
        entranceYearCB.setContainerDataSource(entranceYearBIC);
        entranceYearCB.setImmediate(true);
        entranceYearCB.setNullSelectionAllowed(true);
        entranceYearCB.setTextInputAllowed(true);
        entranceYearCB.setFilteringMode(FilteringMode.STARTSWITH);
        filterHL.addComponent(entranceYearCB);

        Label diplomaTypeLabel = new Label();
        diplomaTypeLabel.addStyleName("bold");
        diplomaTypeLabel.setWidthUndefined();
        diplomaTypeLabel.setValue(getUILocaleUtil().getCaption("diploma.type"));
        filterHL.addComponent(diplomaTypeLabel);

        QueryModel<STUDENT_DIPLOMA_TYPE> diplomaTypeQM = new QueryModel<>(STUDENT_DIPLOMA_TYPE.class);
        BeanItemContainer<STUDENT_DIPLOMA_TYPE> diplomaTypeBIC = new BeanItemContainer<>(
                STUDENT_DIPLOMA_TYPE.class, SessionFacadeFactory.getSessionFacade(
                CommonEntityFacadeBean.class).lookup(diplomaTypeQM));
        diplomaTypeCB = new ComboBox();
        diplomaTypeCB.setContainerDataSource(diplomaTypeBIC);
        diplomaTypeCB.setImmediate(true);
        diplomaTypeCB.setNullSelectionAllowed(true);
        diplomaTypeCB.setTextInputAllowed(true);
        diplomaTypeCB.setFilteringMode(FilteringMode.STARTSWITH);
        diplomaTypeCB.setWidth(200, Unit.PIXELS);
        diplomaTypeCB.setPageLength(0);
        filterHL.addComponent(diplomaTypeCB);

        statusLabel = new Label();
        statusLabel.addStyleName("bold");
        statusLabel.setWidthUndefined();
        statusLabel.setValue(getUILocaleUtil().getEntityFieldLabel(CURRICULUM.class, "curriculumStatus"));
        filterHL.addComponent(statusLabel);

        createStatus = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(CURRICULUM_STATUS.class, ID.valueOf(1)).getStatusName();

        specialityCB.addValueChangeListener(new SpecialityChangeListener());
        entranceYearCB.addValueChangeListener(new EntranceYearChangeListener());
        diplomaTypeCB.addValueChangeListener(new DiplomaTypeChangeListener());

        getContent().addComponent(filterHL);
        getContent().setComponentAlignment(filterHL, Alignment.TOP_CENTER);

        academicDegreeLabel = new Label();
        academicDegreeLabel.addStyleName("bold");
        academicDegreeLabel.addStyleName("margin-left-30");
        getContent().addComponent(academicDegreeLabel);
        getContent().setComponentAlignment(academicDegreeLabel, Alignment.TOP_CENTER);

        studyPeriodLabel = new Label();
        studyPeriodLabel.addStyleName("bold");
        studyPeriodLabel.addStyleName("margin-left-30");
        getContent().addComponent(studyPeriodLabel);
        getContent().setComponentAlignment(studyPeriodLabel, Alignment.TOP_CENTER);

        errorLabel.addStyleName("error");
        errorLabel.setWidthUndefined();
        errorLabel.setValue(getUILocaleUtil().getMessage("curriculum.not.filled"));
        errorLabel.setVisible(false);
        getContent().addComponent(errorLabel);
        getContent().setComponentAlignment(errorLabel, Alignment.TOP_CENTER);

        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setSpacing(true);

        save = new Button();
        save.setCaption(getUILocaleUtil().getCaption("save"));
        save.setWidth(120, Unit.PIXELS);
        save.setIcon(new ThemeResource("img/button/ok.png"));
        save.addStyleName("save");
        save.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent ev) {
                try {
                    save();
                    refresh();
                    CommonUtils.showSavedNotification();
                } catch (Exception ex) {
                    LOG.error("Unable to save curriculum: ", ex);
                    Message.showInfo(ex.getMessage());
                }
            }
        });
        toolbar.addComponent(save);

        conform = new Button();
        conform.setCaption(getUILocaleUtil().getCaption("conform"));
        conform.setWidth(120, Unit.PIXELS);
        conform.addStyleName("conform");
        conform.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent ev) {
                try {
                    conform();
                } catch (Exception ex) {
                    LOG.error("Unable to conform curriculum: ", ex);
                    Message.showInfo(ex.getMessage());
                }
            }
        });
        toolbar.addComponent(conform);

        approve = new Button();
        approve.setCaption(getUILocaleUtil().getCaption("approve"));
        approve.setWidth(120, Unit.PIXELS);
        approve.addStyleName("approve");
        approve.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent ev) {
                try {
                    approve();
                } catch (Exception ex) {
                    LOG.error("Unable to approve curriculum: ", ex);
                    String message = ex.getMessage();
                    if (message != null) {
                        Message.showInfo(message);
                    } else {
                        Message.showError(ex.toString());
                    }
                }
            }
        });
        toolbar.addComponent(approve);

        Button print = new Button();
        print.setCaption(getUILocaleUtil().getCaption("print"));
        print.setWidth(120, Unit.PIXELS);
        print.setIcon(new ThemeResource("img/button/printer.png"));
        print.addStyleName("print");
        toolbar.addComponent(print);

        FileDownloader fd = new CurriculumDownloader(new StreamResource(new StreamSource() {

            @Override
            public InputStream getStream() {
                return getDownloadInputStream();
            }
        }, getFilename()));
        fd.extend(print);

        getContent().addComponent(toolbar);
        getContent().setComponentAlignment(toolbar, Alignment.TOP_CENTER);

        creditSumLabel = new Label();
        creditSumLabel.setWidthUndefined();
        creditSumLabel.addStyleName("bold");
        creditSumLabel.addStyleName("margin-left-30");
        creditSumLabel.setValue(String.format(getUILocaleUtil().getCaption("credit.sum"), 0));
        getContent().addComponent(creditSumLabel);
        getContent().setComponentAlignment(creditSumLabel, Alignment.TOP_LEFT);

        totalCreditSumLabel = new Label();
        totalCreditSumLabel.setWidthUndefined();
        totalCreditSumLabel.addStyleName("bold");
        totalCreditSumLabel.addStyleName("margin-left-30");
        totalCreditSumLabel.setValue(String.format(getUILocaleUtil().getCaption("total.credit.sum"), 0));
        getContent().addComponent(totalCreditSumLabel);
        getContent().setComponentAlignment(totalCreditSumLabel, Alignment.TOP_LEFT);

        detailPanelList = new ArrayList<SemesterDetailPanel>();
        TabSheet ts = new TabSheet();

        QueryModel<SEMESTER> semesterQM = new QueryModel<SEMESTER>(SEMESTER.class);
        semesterQM.addWhere("id", ECriteria.LESS_EQUAL, ID.valueOf(8));
        semesterQM.addOrder("id");
        List<SEMESTER> semesterList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(semesterQM);
        for (SEMESTER s : semesterList) {
            SemesterDetailPanel cdp = new SemesterDetailPanel(this, s);
            cdp.setWidth(100, Unit.PERCENTAGE);
            cdp.setCurriculum(curriculum);
            cdp.initPanel();
            detailPanelList.add(cdp);
            ts.addTab(cdp, getUILocaleUtil().getCaption("semester." + s.getId().toString()));
        }

        addProgramPanel = new AddProgramPanel(this);
        addProgramPanel.setCurriculum(curriculum);
        addProgramPanel.initPanel();
        ts.addTab(addProgramPanel, getUILocaleUtil().getCaption("add.education.programm"));

        afterSemesterProgamPanel = new AfterSemesterProgamPanel(this);
        afterSemesterProgamPanel.setCurriculum(curriculum);
        afterSemesterProgamPanel.initPanel();
        ts.addTab(afterSemesterProgamPanel, getUILocaleUtil().getEntityLabel(V_CURRICULUM_AFTER_SEMESTER.class));

        cycleSumPanel = new CreditCycleSumPanel(this);
        cycleSumPanel.setCurriculum(curriculum);
        cycleSumPanel.initPanel();
        ts.addTab(cycleSumPanel, getUILocaleUtil().getEntityLabel(VCurriculumCreditCycleSum.class));

        for (SemesterDetailPanel sdp : detailPanelList) {
            sdp.setCycleSumPanel(cycleSumPanel);
        }

        cyclePanel = new CyclePanel(this);
        cyclePanel.setCurriculum(curriculum);
        cyclePanel.initPanel();
        ts.addTab(cyclePanel, getUILocaleUtil().getCaption("by.cycle"));

        schedulePanel = new SchedulePanel(this);
        schedulePanel.setCurriculum(curriculum);
        schedulePanel.initPanel();
        ts.addTab(schedulePanel, getUILocaleUtil().getCaption("curriculum.schedule"));

        ts.addSelectedTabChangeListener(new CurriculumTabChangeListener());

        getContent().addComponent(ts);
        getContent().setComponentAlignment(ts, Alignment.MIDDLE_CENTER);
        getContent().setExpandRatio(ts, 1);
    }

    private void refresh() {
        try {
            if (curriculum.getId() != null) {
                save.setEnabled(!curriculum.getCurriculumStatus().getId().equals(ID.valueOf(3)));
                conform.setEnabled(!curriculum.getCurriculumStatus().getId().equals(ID.valueOf(3)) && !curriculum.getCurriculumStatus().getId().equals(ID.valueOf(2)));
                approve.setEnabled(!curriculum.getCurriculumStatus().getId().equals(ID.valueOf(3)));
            } else {
                save.setEnabled(true);
                conform.setEnabled(true);
                approve.setEnabled(true);
            }

            for (SemesterDetailPanel cdp : detailPanelList) {
                cdp.setCurriculum(curriculum);
                cdp.refresh();
            }

            addProgramPanel.setCurriculum(curriculum);
            addProgramPanel.refresh();
            afterSemesterProgamPanel.setCurriculum(curriculum);
            afterSemesterProgamPanel.refresh();
            cycleSumPanel.setCurriculum(curriculum);
            cycleSumPanel.refresh();
            cyclePanel.setCurriculum(curriculum);
            cyclePanel.refresh();
            schedulePanel.setCurriculum(curriculum);
            schedulePanel.refresh();
        } catch (Exception ex) {
            LOG.error("Unable to refresh semester panel: ", ex);
        }
    }

    public void setTotalCreditSum() {
        int sum = 0;
        for (SemesterDetailPanel cdp : detailPanelList) {
            sum += cdp.getTotalCredit();
        }
        creditSumLabel.setValue(String.format(getUILocaleUtil().getCaption("credit.sum"), sum));

        int totalSum = sum + addProgramPanel.getTotalCredit() + afterSemesterProgamPanel.getTotalCredit();
        totalCreditSumLabel.setValue(String.format(getUILocaleUtil().getCaption("total.credit.sum"), totalSum));
    }

    public void save() throws Exception {
        if (curriculum == null) {
            throw new Exception(getUILocaleUtil().getMessage("select.speciality.and.entry.year"));
        }

        curriculum.setSpeciality((SPECIALITY) specialityCB.getValue());
        curriculum.setEntranceYear((ENTRANCE_YEAR) entranceYearCB.getValue());
        try {
            if (curriculum.getId() == null) {
                curriculum.setCurriculumStatus(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(CURRICULUM_STATUS.class, ID.valueOf(1)));
                curriculum.setCreated(new Date());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(curriculum);
            } else {
                curriculum.setUpdated(new Date());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(curriculum);
            }

            for (SemesterDetailPanel sdp : detailPanelList) {
                sdp.setCurriculum(curriculum);
                sdp.save();
            }

            addProgramPanel.setCurriculum(curriculum);
            afterSemesterProgamPanel.setCurriculum(curriculum);
            cycleSumPanel.setCurriculum(curriculum);
            cyclePanel.setCurriculum(curriculum);
            //			cyclePanel.save();
            schedulePanel.setCurriculum(curriculum);
            schedulePanel.generateSchedule();
            schedulePanel.save();
        } catch (Exception ex) {
            LOG.error("Unable to save curriculum: ", ex);
            throw ex;
        }
    }

    private void conform() throws Exception {
        checkForConform();

        curriculum.setCurriculumStatus(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(CURRICULUM_STATUS.class, ID.valueOf(2)));
        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(curriculum);

        statusLabel.setValue(getUILocaleUtil().getEntityFieldLabel(CURRICULUM.class, "curriculumStatus") + ": " + curriculum.getCurriculumStatus().getStatusName());
        conform.setEnabled(false);
    }

    private void approve() throws Exception {
        checkForConform();
        //		for (SemesterDetailPanel sdp : detailPanelList) {
        //			sdp.checkForApprove();
        //		}

        //		cyclePanel.checkForApprove();

        for (SemesterDetailPanel sdp : detailPanelList) {
            sdp.approve();
        }

        addProgramPanel.approve();
        afterSemesterProgamPanel.approve();

        curriculum.setCurriculumStatus(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(CURRICULUM_STATUS.class, ID.valueOf(3)));
        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(curriculum);

        statusLabel.setValue(getUILocaleUtil().getEntityFieldLabel(CURRICULUM.class, "curriculumStatus") + ": " + curriculum.getCurriculumStatus().getStatusName());
        approve.setEnabled(false);

        refresh();
    }

    private void checkForConform() throws Exception {
        if (curriculum == null) {
            throw new Exception(getUILocaleUtil().getMessage("select.speciality.and.entry.year"));
        }

        for (SemesterDetailPanel sdp : detailPanelList) {
            sdp.checkForConform();
        }

        addProgramPanel.checkForConform();
        afterSemesterProgamPanel.checkForConform();
        schedulePanel.checkForConform();
    }

    public final boolean isBachelorCurriculum() throws Exception {
        SPECIALITY speciality = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(SPECIALITY.class, curriculum.getSpeciality().getId());

        return speciality.getLevel().getId().equals(ID.valueOf(1));
    }

    private String getFilename() {
        Calendar c = Calendar.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append("/tmp/curriculum_");
        sb.append(c.get(Calendar.YEAR));
        sb.append(c.get(Calendar.MONTH));
        sb.append(c.get(Calendar.DAY_OF_MONTH));
        sb.append(c.get(Calendar.HOUR_OF_DAY));
        sb.append(c.get(Calendar.MINUTE));
        sb.append(c.get(Calendar.SECOND));
        sb.append(c.get(Calendar.MILLISECOND));
        sb.append(".xlsx");

        return sb.toString();
    }

    protected boolean canDownload() {
        return true;
    }

    protected InputStream getDownloadInputStream() {
        ByteArrayOutputStream baos = null;
        try {
            Workbook wb = new XSSFWorkbook();
            Map<ExcelStyles, CellStyle> styles = ExcelUtil.createStyles(wb);

            Sheet sheet = wb.createSheet(getUILocaleUtil().getCaption("by.semester"));
            sheet.setDisplayGridlines(true);
            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue(getUILocaleUtil().getCaption("curriculum"));
            cell.setCellStyle(styles.get(ExcelStyles.TITLE));
            sheet.addMergedRegion(CellRangeAddress.valueOf("$A$1:$M$1"));

            row = sheet.createRow(1);
            cell = row.createCell(0);
            cell.setCellValue(getSpecialityText());
            cell.setCellStyle(styles.get(ExcelStyles.SUBTITLE_CENTER));
            sheet.addMergedRegion(CellRangeAddress.valueOf("$A$2:$M$2"));

            row = sheet.createRow(4);
            cell = row.createCell(1);
            cell.setCellValue(getAcademicDegreeText());
            cell.setCellStyle(styles.get(ExcelStyles.SUBTITLE_LEFT));
            sheet.addMergedRegion(CellRangeAddress.valueOf("$B$5:$M$5"));

            row = sheet.createRow(5);
            cell = row.createCell(1);
            cell.setCellValue(getStudyPeriodText());
            cell.setCellStyle(styles.get(ExcelStyles.SUBTITLE_LEFT));
            sheet.addMergedRegion(CellRangeAddress.valueOf("$B$6:$M$6"));

            row = sheet.createRow(6);
            cell = row.createCell(0);
            cell.setCellValue(getUILocaleUtil().getEntityLabel(STUDY_YEAR.class));
            cell.setCellStyle(styles.get(ExcelStyles.HEADER_VERTICAL));

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
            cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_DETAIL.class, "subjectPrerequisiteCode"));
            cell.setCellStyle(styles.get(ExcelStyles.HEADER_VERTICAL));

            cell = row.createCell(7);
            cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_DETAIL.class, "subjectCode"));
            cell.setCellStyle(styles.get(ExcelStyles.HEADER));

            cell = row.createCell(8);
            cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_DETAIL.class, "subjectName"));
            cell.setCellStyle(styles.get(ExcelStyles.HEADER));

            cell = row.createCell(9);
            cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_DETAIL.class, "cycleShortName"));
            cell.setCellStyle(styles.get(ExcelStyles.HEADER_VERTICAL));

            cell = row.createCell(10);
            cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_DETAIL.class, "credit"));
            cell.setCellStyle(styles.get(ExcelStyles.HEADER_VERTICAL));

            cell = row.createCell(11);
            cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_DETAIL.class, "formula"));
            cell.setCellStyle(styles.get(ExcelStyles.HEADER_VERTICAL));

            cell = row.createCell(12);
            cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_DETAIL.class, "subjectPrerequisiteCode"));
            cell.setCellStyle(styles.get(ExcelStyles.HEADER_VERTICAL));

            totalRequired = 0;
            int startRow = 7;
            QueryModel<STUDY_YEAR> qmStudyYear = new QueryModel<STUDY_YEAR>(STUDY_YEAR.class);
            qmStudyYear.addWhere("studyYear", ECriteria.LESS_EQUAL, 4);
            qmStudyYear.addOrder("studyYear");
            List<STUDY_YEAR> studyYearList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qmStudyYear);

            for (STUDY_YEAR sy : studyYearList) {
                startRow = writeStudyYear(sy, startRow, sheet, styles);
            }

            row = sheet.createRow(startRow);
            cell = row.createCell(2);
            cell.setCellValue(String.format(getUILocaleUtil().getCaption("credit.sum"), ""));
            cell.setCellStyle(styles.get(ExcelStyles.FOOTER));

            cell = row.createCell(4);
            cell.setCellValue(totalRequired);
            cell.setCellStyle(styles.get(ExcelStyles.FOOTER));

            startRow++;

            row = sheet.createRow(startRow);
            cell = row.createCell(0);
            cell.setCellValue(getUILocaleUtil().getEntityLabel(V_CURRICULUM_ADD_PROGRAM.class));
            cell.setCellStyle(styles.get(ExcelStyles.HEADER));
            sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 0, 4));

            cell = row.createCell(7);
            cell.setCellValue(getUILocaleUtil().getEntityLabel(VCurriculumCreditCycleSum.class));
            cell.setCellStyle(styles.get(ExcelStyles.HEADER));
            sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 7, 11));

            startRow++;

            row = sheet.createRow(startRow);
            cell = row.createCell(0);
            cell.setCellValue(getUILocaleUtil().getCaption("number"));
            cell.setCellStyle(styles.get(ExcelStyles.HEADER));

            cell = row.createCell(1);
            cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_ADD_PROGRAM.class, "addEducationProgramCode"));
            cell.setCellStyle(styles.get(ExcelStyles.HEADER));

            cell = row.createCell(2);
            cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_ADD_PROGRAM.class, "addEducationProgramNameRU"));
            cell.setCellStyle(styles.get(ExcelStyles.HEADER));

            cell = row.createCell(3);
            cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_ADD_PROGRAM.class, "credit"));
            cell.setCellStyle(styles.get(ExcelStyles.HEADER_VERTICAL));

            cell = row.createCell(4);
            cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(V_CURRICULUM_ADD_PROGRAM.class, "recommendedSemester"));
            cell.setCellStyle(styles.get(ExcelStyles.HEADER_VERTICAL));

            sheet.addMergedRegion(new CellRangeAddress(startRow, startRow + 1, 0, 0));
            sheet.addMergedRegion(new CellRangeAddress(startRow, startRow + 1, 1, 1));
            sheet.addMergedRegion(new CellRangeAddress(startRow, startRow + 1, 2, 2));
            sheet.addMergedRegion(new CellRangeAddress(startRow, startRow + 1, 3, 3));
            sheet.addMergedRegion(new CellRangeAddress(startRow, startRow + 1, 4, 4));

            cell = row.createCell(7);
            cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(VCurriculumCreditCycleSum.class, "cycleShortName"));
            cell.setCellStyle(styles.get(ExcelStyles.HEADER_VERTICAL));
            sheet.addMergedRegion(new CellRangeAddress(startRow, startRow + 1, 7, 7));

            cell = row.createCell(8);
            cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(VCurriculumCreditCycleSum.class, "cycleName"));
            cell.setCellStyle(styles.get(ExcelStyles.HEADER));
            sheet.addMergedRegion(new CellRangeAddress(startRow, startRow + 1, 8, 8));

            cell = row.createCell(9);
            cell.setCellValue(getUILocaleUtil().getCaption("credits"));
            cell.setCellStyle(styles.get(ExcelStyles.HEADER));
            sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 9, 11));

            startRow++;
            row = sheet.createRow(startRow);
            cell = row.createCell(9);
            cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(VCurriculumCreditCycleSum.class, "totalCreditSum"));
            cell.setCellStyle(styles.get(ExcelStyles.HEADER_VERTICAL));

            cell = row.createCell(10);
            cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(VCurriculumCreditCycleSum.class, "requiredCreditSum"));
            cell.setCellStyle(styles.get(ExcelStyles.HEADER_VERTICAL));

            cell = row.createCell(11);
            cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(VCurriculumCreditCycleSum.class, "electiveCreditSum"));
            cell.setCellStyle(styles.get(ExcelStyles.HEADER_VERTICAL));

            totalAdditional = 0;
            int lastRow = writeAddProgramm(++startRow, sheet, styles);

            writeCycleSum(startRow, lastRow, sheet, styles);

            row = sheet.createRow(++lastRow);
            cell = row.createCell(2);
            cell.setCellValue(String.format(getUILocaleUtil().getCaption("total.credit.sum"), ""));
            cell.setCellStyle(styles.get(ExcelStyles.FOOTER));

            cell = row.createCell(3);
            cell.setCellValue(totalRequired + totalAdditional);
            cell.setCellStyle(styles.get(ExcelStyles.FOOTER));

            sheet.setColumnWidth(0, 3 * 256);
            sheet.setColumnWidth(2, 32 * 256);
            sheet.setColumnWidth(3, 4 * 256);
            sheet.setColumnWidth(4, 3 * 256);
            sheet.setColumnWidth(5, 6 * 256);
            sheet.setColumnWidth(8, 32 * 256);
            sheet.setColumnWidth(9, 3 * 256);
            sheet.setColumnWidth(10, 3 * 256);
            sheet.setColumnWidth(11, 6 * 256);

            cyclePanel.fillWorkBook(wb);
            schedulePanel.fillWorkbook(wb);

            baos = new ByteArrayOutputStream();
            wb.write(baos);

            return new ByteArrayInputStream(baos.toByteArray());
        } catch (Exception ex) {
            LOG.error("Unable to download Excel file: ", ex);
            Message.showError(ex.toString());
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException ex) {
                }
            }
        }

        return null;
    }

    private int writeStudyYear(STUDY_YEAR studyYear, int startRow, Sheet sheet, Map<ExcelStyles, CellStyle> styles) throws Exception {
        QueryModel<SEMESTER> qmSemester = new QueryModel<SEMESTER>(SEMESTER.class);
        qmSemester.addWhere("studyYear", ECriteria.EQUAL, studyYear.getId());
        qmSemester.addOrder("id");
        List<SEMESTER> semesterList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qmSemester);

        QueryModel<V_CURRICULUM_DETAIL> qmVCD = new QueryModel<V_CURRICULUM_DETAIL>(V_CURRICULUM_DETAIL.class);
        qmVCD.addWhere("curriculum", ECriteria.EQUAL, curriculum.getId());
        qmVCD.addWhere("deleted", Boolean.FALSE);

        int lastRow = 0;
        Map<SEMESTER, List<V_CURRICULUM_DETAIL>> recordsMap = new HashMap<SEMESTER, List<V_CURRICULUM_DETAIL>>();
        for (SEMESTER semester : semesterList) {
            qmVCD.addWhere("semester", ECriteria.EQUAL, semester.getId());
            List<V_CURRICULUM_DETAIL> vcdList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qmVCD);
            if (lastRow < (startRow + vcdList.size())) {
                lastRow = startRow + vcdList.size();
            }
            recordsMap.put(semester, vcdList);
        }

        lastRow++;

        for (int i = startRow; i <= lastRow; i++) {
            sheet.createRow(i);
        }

        Row row = sheet.getRow(startRow);
        Cell cell = row.createCell(0);
        cell.setCellValue(studyYear.getStudyYear());
        cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

        int currentRow = startRow;
        int total1 = 0;
        int total2 = 0;
        for (SEMESTER semester : semesterList) {
            int startCell = 1;
            if (semester.getId().getId().intValue() % 2 == 0) {
                startCell = 7;
                currentRow = startRow;
            }

            row = sheet.getRow(currentRow);
            cell = row.createCell(startCell);
            cell.setCellValue(semester.getSemesterName());
            cell.setCellStyle(styles.get(ExcelStyles.HEADER_BORDER_THIN));
            sheet.addMergedRegion(new CellRangeAddress(currentRow, currentRow, startCell, startCell + 5));

            currentRow++;
            List<V_CURRICULUM_DETAIL> vcdList = recordsMap.get(semester);
            if (!vcdList.isEmpty()) {
                StringBuilder sql = new StringBuilder();
                sql.append("SELECT " +
                        "  curr_det.ID, " +
                        "  c.CODE " +
                        "FROM SUBJECT_REQUISITE subj_req INNER JOIN CURRICULUM_DETAIL curr_det ON subj_req.SUBJECT_ID = curr_det.SUBJECT_ID " +
                        "  INNER JOIN SUBJECT c ON subj_req.REQUISITE_ID = c.ID " +
                        "WHERE subj_req.PRE_REQUISITE = ?1 AND curr_det.DELETED = ?2 AND curr_det.ID IN (");
                boolean first = true;
                for (Entity e : vcdList) {
                    if (!first) {
                        sql.append(", ");
                    }

                    sql.append(e.getId().getId());
                    first = false;
                }
                sql.append(')');

                Map<Integer, Object> params = new HashMap<Integer, Object>(2);
                params.put(1, Boolean.TRUE);
                params.put(2, Boolean.FALSE);

                try {
                    List tempList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql.toString(), params);
                    if (!tempList.isEmpty()) {
                        for (V_CURRICULUM_DETAIL vcd : vcdList) {
                            StringBuilder sb = new StringBuilder();
                            first = true;
                            for (Object o : tempList) {
                                Object[] oo = (Object[]) o;
                                if (vcd.getId().getId().longValue() == ((long) oo[0])) {
                                    if (!first) {
                                        sb.append(", ");
                                    }
                                    sb.append(oo[1]);
                                    first = false;
                                }
                            }

                            if (sb.length() > 0) {
                                vcd.setSubjectPrerequisiteCode(sb.toString());
                            }
                        }
                    }
                } catch (Exception ex) {
                    LOG.error("Unable to set prerequisites: ", ex);
                }
            }

            for (V_CURRICULUM_DETAIL vcd : vcdList) {
                row = sheet.getRow(currentRow++);
                cell = row.createCell(startCell++);
                cell.setCellValue(vcd.getSubjectCode());
                cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

                cell = row.createCell(startCell++);
                cell.setCellValue(vcd.getSubjectName());
                cell.setCellStyle(styles.get(ExcelStyles.CONTENT_LEFT));

                cell = row.createCell(startCell++);
                cell.setCellValue(vcd.getCycleShortName());
                cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

                cell = row.createCell(startCell++);
                cell.setCellValue(vcd.getCredit());
                cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

                cell = row.createCell(startCell++);
                cell.setCellValue(vcd.getFormula());
                cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

                cell = row.createCell(startCell++);
                cell.setCellValue(vcd.getSubjectPrerequisiteCode());
                cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

                if (semester.getId().getId().intValue() % 2 == 0) {
                    startCell = 7;
                    total2 += vcd.getCredit();
                } else {
                    startCell = 1;
                    total1 += vcd.getCredit();
                }
            }
        }

        row = sheet.getRow(lastRow);
        cell = row.createCell(2);
        cell.setCellValue(getUILocaleUtil().getCaption("total"));
        cell.setCellStyle(styles.get(ExcelStyles.FOOTER));

        cell = row.createCell(4);
        cell.setCellValue(total1);
        cell.setCellStyle(styles.get(ExcelStyles.FOOTER));

        cell = row.createCell(8);
        cell.setCellValue(getUILocaleUtil().getCaption("total"));
        cell.setCellStyle(styles.get(ExcelStyles.FOOTER));

        cell = row.createCell(10);
        cell.setCellValue(total2);
        cell.setCellStyle(styles.get(ExcelStyles.FOOTER));

        totalRequired += total1 + total2;

        sheet.addMergedRegion(new CellRangeAddress(startRow, lastRow, 0, 0));

        return ++lastRow;
    }

    private int writeAddProgramm(int startRow, Sheet sheet, Map<ExcelStyles, CellStyle> styles) throws Exception {
        QueryModel<V_CURRICULUM_ADD_PROGRAM> qmAddProgramm = new QueryModel<V_CURRICULUM_ADD_PROGRAM>(V_CURRICULUM_ADD_PROGRAM.class);
        qmAddProgramm.addWhere("curriculum", ECriteria.EQUAL, curriculum.getId());
        qmAddProgramm.addWhereAnd("deleted", Boolean.FALSE);
        List<V_CURRICULUM_ADD_PROGRAM> vcapList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qmAddProgramm);

        int lastRow = startRow + vcapList.size();

        String sql = "SELECT " +
                "  t1.CYCLE_SHORT_NAME, " +
                "  t1.CYCLE_NAME, " +
                "  t1.CREDIT_SUM + coalesce(t2.CREDIT_SUM, 0) TOTAL_CREDIT_SUM, " +
                "  t1.CREDIT_SUM                              REQUIRED_CREDIT_SUM, " +
                "  coalesce(t2.CREDIT_SUM, 0)                 ELECTIVE_CREDIT_SUM " +
                "FROM (SELECT " +
                "        a.CURRICULUM_ID, " +
                "        b.SUBJECT_CYCLE_ID, " +
                "        c.CYCLE_SHORT_NAME, " +
                "        c.CYCLE_NAME, " +
                "        sum(d.CREDIT) CREDIT_SUM " +
                "      FROM CURRICULUM_DETAIL a INNER JOIN SUBJECT b ON a.SUBJECT_ID = b.ID " +
                "        INNER JOIN SUBJECT_CYCLE c ON b.SUBJECT_CYCLE_ID = c.ID " +
                "        INNER JOIN CREDITABILITY d ON b.CREDITABILITY_ID = d.ID " +
                "      WHERE a.CURRICULUM_ID = ?1 AND a.DELETED = ?2 AND a.CONSIDER_CREDIT = ?3 " +
                "      GROUP BY a.CURRICULUM_ID, b.SUBJECT_CYCLE_ID, c.CYCLE_SHORT_NAME, c.CYCLE_NAME) t1 LEFT JOIN (SELECT " +
                "                                                                                                      a.CURRICULUM_ID, " +
                "                                                                                                      a.ELECTIVE_SUBJECT_CYCLE_ID, " +
                "                                                                                                      b.CYCLE_SHORT_NAME, " +
                "                                                                                                      b.CYCLE_NAME, " +
                "                                                                                                      sum( " +
                "                                                                                                          a.ELECTIVE_SUBJECT_CREDIT) CREDIT_SUM " +
                "                                                                                                    FROM " +
                "                                                                                                      CURRICULUM_DETAIL a INNER JOIN " +
                "                                                                                                      SUBJECT_CYCLE b " +
                "                                                                                                        ON " +
                "                                                                                                          a.ELECTIVE_SUBJECT_CYCLE_ID " +
                "                                                                                                          = b.ID " +
                "                                                                                                    WHERE " +
                "                                                                                                      a.CURRICULUM_ID = " +
                "                                                                                                      ?4 AND " +
                "                                                                                                      a.DELETED = ?5 " +
                "                                                                                                    GROUP BY " +
                "                                                                                                      a.CURRICULUM_ID, " +
                "                                                                                                      a.ELECTIVE_SUBJECT_CYCLE_ID, " +
                "                                                                                                      b.CYCLE_SHORT_NAME, " +
                "                                                                                                      b.CYCLE_NAME) t2 " +
                "    ON t1.CURRICULUM_ID = t2.CURRICULUM_ID AND t1.SUBJECT_CYCLE_ID = t2.ELECTIVE_SUBJECT_CYCLE_ID";
        Map<Integer, Object> params = new HashMap<Integer, Object>(5);
        params.put(1, curriculum.getId().getId());
        params.put(2, Boolean.FALSE);
        params.put(3, Boolean.TRUE);
        params.put(4, curriculum.getId().getId());
        params.put(5, Boolean.FALSE);

        List tempList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookupItemsList(sql, params);
        List<VCurriculumCreditCycleSum> vcccsList = new ArrayList<>(tempList.size());
        for (Object o : tempList) {
            Object[] oo = (Object[]) o;
            VCurriculumCreditCycleSum vccc = new VCurriculumCreditCycleSum();
            vccc.setCycleShortName((String) oo[0]);
            vccc.setCycleName((String) oo[1]);
            vccc.setTotalCreditSum(((BigDecimal) oo[2]).intValue());
            vccc.setRequiredCreditSum(((BigDecimal) oo[3]).intValue());
            vccc.setElectiveCreditSum(((BigDecimal) oo[4]).intValue());
            vcccsList.add(vccc);
        }

        if (lastRow < (startRow + vcccsList.size())) {
            lastRow = startRow + vcccsList.size();
        }

        for (int i = startRow; i <= lastRow; i++) {
            sheet.createRow(i);
        }

        int currentRow = startRow;
        int i = 1;

        for (V_CURRICULUM_ADD_PROGRAM vcap : vcapList) {
            Row row = sheet.getRow(currentRow++);
            Cell cell = row.createCell(0);
            cell.setCellValue(i++);
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(1);
            cell.setCellValue(vcap.getSubjectCode());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(2);
            cell.setCellValue(vcap.getSubjectNameRU());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_LEFT));

            cell = row.createCell(3);
            cell.setCellValue(vcap.getCredit());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(4);
            cell.setCellValue(vcap.getSemesterName());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            totalAdditional += vcap.getCredit();
        }

        Row row = sheet.getRow(currentRow);
        Cell cell = row.createCell(2);
        cell.setCellValue(getUILocaleUtil().getCaption("total"));
        cell.setCellStyle(styles.get(ExcelStyles.FOOTER));

        cell = row.createCell(3);
        cell.setCellValue(totalAdditional);
        cell.setCellStyle(styles.get(ExcelStyles.FOOTER));

        return lastRow;
    }

    private void writeCycleSum(int startRow, int lastRow, Sheet sheet, Map<ExcelStyles, CellStyle> styles) throws Exception {
        int totalComponentAll = 0;
        int totalComponentRequired = 0;
        int totalComponentElective = 0;

        String sql = "SELECT " +
                "  t1.CYCLE_SHORT_NAME, " +
                "  t1.CYCLE_NAME, " +
                "  t1.CREDIT_SUM + coalesce(t2.CREDIT_SUM, 0) TOTAL_CREDIT_SUM, " +
                "  t1.CREDIT_SUM                              REQUIRED_CREDIT_SUM, " +
                "  coalesce(t2.CREDIT_SUM, 0)                 ELECTIVE_CREDIT_SUM " +
                "FROM (SELECT " +
                "        a.CURRICULUM_ID, " +
                "        b.SUBJECT_CYCLE_ID, " +
                "        c.CYCLE_SHORT_NAME, " +
                "        c.CYCLE_NAME, " +
                "        sum(d.CREDIT) CREDIT_SUM " +
                "      FROM CURRICULUM_DETAIL a INNER JOIN SUBJECT b ON a.SUBJECT_ID = b.ID " +
                "        INNER JOIN SUBJECT_CYCLE c ON b.SUBJECT_CYCLE_ID = c.ID " +
                "        INNER JOIN CREDITABILITY d ON b.CREDITABILITY_ID = d.ID " +
                "      WHERE a.CURRICULUM_ID = ?1 AND a.DELETED = ?2 AND a.CONSIDER_CREDIT = ?3 " +
                "      GROUP BY a.CURRICULUM_ID, b.SUBJECT_CYCLE_ID, c.CYCLE_SHORT_NAME, c.CYCLE_NAME) t1 LEFT JOIN (SELECT " +
                "                                                                                                      a.CURRICULUM_ID, " +
                "                                                                                                      a.ELECTIVE_SUBJECT_CYCLE_ID, " +
                "                                                                                                      b.CYCLE_SHORT_NAME, " +
                "                                                                                                      b.CYCLE_NAME, " +
                "                                                                                                      sum( " +
                "                                                                                                          a.ELECTIVE_SUBJECT_CREDIT) CREDIT_SUM " +
                "                                                                                                    FROM " +
                "                                                                                                      CURRICULUM_DETAIL a INNER JOIN " +
                "                                                                                                      SUBJECT_CYCLE b " +
                "                                                                                                        ON " +
                "                                                                                                          a.ELECTIVE_SUBJECT_CYCLE_ID " +
                "                                                                                                          = b.ID " +
                "                                                                                                    WHERE " +
                "                                                                                                      a.CURRICULUM_ID = " +
                "                                                                                                      ?4 AND " +
                "                                                                                                      a.DELETED = ?5 " +
                "                                                                                                    GROUP BY " +
                "                                                                                                      a.CURRICULUM_ID, " +
                "                                                                                                      a.ELECTIVE_SUBJECT_CYCLE_ID, " +
                "                                                                                                      b.CYCLE_SHORT_NAME, " +
                "                                                                                                      b.CYCLE_NAME) t2 " +
                "    ON t1.CURRICULUM_ID = t2.CURRICULUM_ID AND t1.SUBJECT_CYCLE_ID = t2.ELECTIVE_SUBJECT_CYCLE_ID";
        Map<Integer, Object> params = new HashMap<Integer, Object>(5);
        params.put(1, curriculum.getId().getId());
        params.put(2, Boolean.FALSE);
        params.put(3, Boolean.TRUE);
        params.put(4, curriculum.getId().getId());
        params.put(5, Boolean.FALSE);

        List tempList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
        List<VCurriculumCreditCycleSum> vcccsList = new ArrayList<VCurriculumCreditCycleSum>(tempList.size());
        for (Object o : tempList) {
            Object[] oo = (Object[]) o;
            VCurriculumCreditCycleSum vccc = new VCurriculumCreditCycleSum();
            vccc.setCycleShortName((String) oo[0]);
            vccc.setCycleName((String) oo[1]);
            vccc.setTotalCreditSum(((BigDecimal) oo[2]).intValue());
            vccc.setRequiredCreditSum(((BigDecimal) oo[3]).intValue());
            vccc.setElectiveCreditSum(((BigDecimal) oo[4]).intValue());
            vcccsList.add(vccc);
        }

        for (VCurriculumCreditCycleSum vcccs : vcccsList) {
            Row row = sheet.getRow(startRow++);
            Cell cell = row.createCell(7);
            cell.setCellValue(vcccs.getCycleShortName());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(8);
            cell.setCellValue(vcccs.getCycleName());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_LEFT));

            cell = row.createCell(9);
            cell.setCellValue(vcccs.getTotalCreditSum());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(10);
            cell.setCellValue(vcccs.getRequiredCreditSum());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(11);
            cell.setCellValue(vcccs.getElectiveCreditSum());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            totalComponentAll += vcccs.getTotalCreditSum();
            totalComponentRequired += vcccs.getRequiredCreditSum();
            totalComponentElective += vcccs.getElectiveCreditSum();
        }

        Row row = sheet.getRow(lastRow);
        Cell cell = row.createCell(8);
        cell.setCellValue(getUILocaleUtil().getCaption("total"));
        cell.setCellStyle(styles.get(ExcelStyles.FOOTER));

        cell = row.createCell(9);
        cell.setCellValue(totalComponentAll);
        cell.setCellStyle(styles.get(ExcelStyles.FOOTER));

        cell = row.createCell(10);
        cell.setCellValue(totalComponentRequired);
        cell.setCellStyle(styles.get(ExcelStyles.FOOTER));

        cell = row.createCell(11);
        cell.setCellValue(totalComponentElective);
        cell.setCellStyle(styles.get(ExcelStyles.FOOTER));
    }

    public String getSpecialityText() {
        String specialityText = String.format(getUILocaleUtil().getCaption("curriculum.speciality"), curriculum.getSpeciality().toString(), curriculum.getEntranceYear().toString());

        return specialityText;
    }

    public String getAcademicDegreeText() {
        return academicDegreeLabel.getValue();
    }

    public String getStudyPeriodText() {
        return studyPeriodLabel.getValue();
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
    }

    @Override
    public boolean preCreate(Object source, int buttonId) {
        return true;
    }

    @Override
    public void onCreate(Object source, Entity e, int buttonId) {
    }

    @Override
    public boolean onEdit(Object source, Entity e, int buttonId) {
        return true;
    }

    @Override
    public boolean onPreview(Object source, Entity e, int buttonId) {
        return true;
    }

    @Override
    public void beforeRefresh(Object source, int buttonId) {
    }

    @Override
    public void onRefresh(Object source, List<Entity> entities) {
    }

    @Override
    public void onFilter(Object source, QueryModel qm, int buttonId) {
    }

    @Override
    public void onAccept(Object source, List<Entity> entities, int buttonId) {
    }

    @Override
    public boolean preSave(Object source, Entity e, boolean isNew, int buttonId) {
        return true;
    }

    @Override
    public boolean preDelete(Object source, List<Entity> entities, int buttonId) {
        return true;
    }

    @Override
    public void onDelete(Object source, List<Entity> entities, int buttonId) {
    }

    @Override
    public void deferredCreate(Object source, Entity e) {
    }

    @Override
    public void deferredDelete(Object source, List<Entity> entities) {
    }

    @Override
    public void onException(Object source, Throwable ex) {
    }

    public void showErrorLabel(boolean show) {
        errorLabel.setVisible(show);
    }

    private class SpecialityChangeListener implements ValueChangeListener {

        @Override
        public void valueChange(ValueChangeEvent ev) {
            SPECIALITY s = (SPECIALITY) ev.getProperty().getValue();
            if (s != null) {
                ENTRANCE_YEAR ey = (ENTRANCE_YEAR) entranceYearCB.getValue();
                STUDENT_DIPLOMA_TYPE type = (STUDENT_DIPLOMA_TYPE) diplomaTypeCB.getValue();
                if (ey != null && type != null) {
                    findCurriculum(s, ey, type);
                }

                QueryModel<ACADEMIC_DEGREE> qmAcademicDegree = new QueryModel<ACADEMIC_DEGREE>(ACADEMIC_DEGREE.class);
                qmAcademicDegree.addWhere("speciality", ECriteria.EQUAL, s.getId());
                try {
                    ACADEMIC_DEGREE ad = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(qmAcademicDegree);

                    String str1 = String.format(getUILocaleUtil().getCaption("academic.degree"), ad.getDegreeName());
                    String str2 = String.format(getUILocaleUtil().getCaption("study.period"), ad.getStudyPeriod());

                    academicDegreeLabel.setValue(str1);
                    studyPeriodLabel.setValue(str2);
                } catch (Exception ex) {
                    LOG.error("Unable to locate academic degree: ", ex);
                }
            }
        }

    }

    private void findCurriculum(SPECIALITY s, ENTRANCE_YEAR ey, STUDENT_DIPLOMA_TYPE type) {
        QueryModel<CURRICULUM> qm = new QueryModel<CURRICULUM>(CURRICULUM.class);
        qm.addWhere("speciality", ECriteria.EQUAL, s.getId());
        qm.addWhereAnd("entranceYear", ECriteria.EQUAL, ey.getId());
        qm.addWhereAnd("diplomaType", ECriteria.EQUAL, type.getId());
        qm.addWhere("deleted", Boolean.FALSE);
        try {
            curriculum = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(qm);
            statusLabel.setValue(getUILocaleUtil().getEntityFieldLabel(CURRICULUM.class, "curriculumStatus") + ": " + curriculum.getCurriculumStatus().toString());
            showErrorLabel(false);
        } catch (NoResultException e) {
            showErrorLabel(true);
            curriculum = new CURRICULUM();
            curriculum.setSpeciality(s);
            curriculum.setEntranceYear(ey);
            curriculum.setDiplomaType(type);
            try {
                curriculum.setCurriculumStatus(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(CURRICULUM_STATUS.class, ID.valueOf(IN_CREATING)));
                statusLabel.setValue(getUILocaleUtil().getEntityFieldLabel(CURRICULUM.class, "curriculumStatus") + ": " + createStatus);
            } catch (Exception ex1) {
                LOG.error("Unable to set curriculum status: ", ex1);
                Message.showError(ex1.toString());
            }
        } catch (Exception ex) {
            LOG.error("Unable to find curriculum: ", ex);
            Message.showError(ex.toString());
        }
        if (curriculum != null) {
            refresh();
        }
    }

    private class DiplomaTypeChangeListener implements ValueChangeListener {

        @Override
        public void valueChange(ValueChangeEvent ev) {
            STUDENT_DIPLOMA_TYPE type = (STUDENT_DIPLOMA_TYPE) ev.getProperty().getValue();
            if (type != null) {
                SPECIALITY s = (SPECIALITY) specialityCB.getValue();
                ENTRANCE_YEAR ey = (ENTRANCE_YEAR) entranceYearCB.getValue();
                if (s != null && ey != null) {
                    findCurriculum(s, ey, type);
                }
            }
        }
    }

    private class EntranceYearChangeListener implements ValueChangeListener {

        @Override
        public void valueChange(ValueChangeEvent ev) {
            ENTRANCE_YEAR ey = (ENTRANCE_YEAR) ev.getProperty().getValue();
            if (ey != null) {
                SPECIALITY s = (SPECIALITY) specialityCB.getValue();
                STUDENT_DIPLOMA_TYPE type = (STUDENT_DIPLOMA_TYPE) diplomaTypeCB.getValue();
                if (s != null && type != null) {
                    findCurriculum(s, ey, type);
                }
            }
        }
    }

    private class CurriculumDownloader extends FileDownloader {

        public CurriculumDownloader(StreamResource resource) {
            super(resource);
        }

        @Override
        public boolean handleConnectorRequest(VaadinRequest request, VaadinResponse response, String path) throws IOException {
            if (!canDownload()) {
                return false;
            }

            return super.handleConnectorRequest(request, response, path);
        }
    }

    private class CurriculumTabChangeListener implements SelectedTabChangeListener {

        @Override
        public void selectedTabChange(SelectedTabChangeEvent ev) {
            AbstractCurriculumPanel acp = (AbstractCurriculumPanel) ev.getTabSheet().getSelectedTab();
            acp.setCurriculum(curriculum);
            try {
                acp.refresh();
            } catch (Exception ex) {
                LOG.error("Unable to refresh: ", ex);
            }
        }
    }
}
