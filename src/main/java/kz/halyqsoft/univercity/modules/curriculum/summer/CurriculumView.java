package kz.halyqsoft.univercity.modules.curriculum.summer;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.*;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import kz.halyqsoft.univercity.entity.beans.univercity.CURRICULUM_SUMMER;
import kz.halyqsoft.univercity.entity.beans.univercity.CURRICULUM_SUMMER_DETAIL;
import kz.halyqsoft.univercity.entity.beans.univercity.SEMESTER_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VCurriculumDetail;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_SUBJECT_SELECT;
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
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.utility.DateUtils;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.AbstractYesButtonListener;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.footer.EColumnFooterType;
import org.r3a.common.vaadin.widget.grid.footer.IntegerColumnFooterModel;
import org.r3a.common.vaadin.widget.grid.footer.StringColumnFooterModel;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;

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
 * @created Apr 19, 2018 11:46:30 AM
 */
@SuppressWarnings({"serial", "unchecked"})
public final class CurriculumView extends AbstractTaskView implements EntityListener {

    private static final ID MULTI_CYCLE_ID = ID.valueOf(4);
    private CURRICULUM_SUMMER curriculum;
    private ComboBox academicYearCB;
    private ComboBox semesterPeriodCB;
    private Label statusLabel;
    private String createStatus;
    private Button conform;
    private Button approve;
    private GridWidget grid;
    private SubjectSelectDialog subjectSelectDlg;

    public CurriculumView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean readOnly) throws Exception {
        SEMESTER_DATA sd = CommonUtils.getCurrentSemesterData();
        if (sd != null) {//TODO check
            HorizontalLayout hl = new HorizontalLayout();
            hl.setWidthUndefined();
            hl.setSpacing(true);
            hl.setMargin(true);
            hl.addStyleName("form-panel");

            Label l = new Label();
            l.addStyleName("bold");
            l.setWidthUndefined();
            l.setValue(getUILocaleUtil().getCaption("study.year.1"));
            hl.addComponent(l);

            QueryModel<ENTRANCE_YEAR> academicYearQM = new QueryModel<ENTRANCE_YEAR>(ENTRANCE_YEAR.class);
            academicYearQM.addWhere("beginYear", ECriteria.LESS_EQUAL, sd.getYear().getBeginYear() + 1);
            academicYearQM.addWhereAnd("endYear", ECriteria.LESS_EQUAL, sd.getYear().getEndYear() + 1);
            academicYearQM.addOrderDesc("beginYear");
            BeanItemContainer<ENTRANCE_YEAR> academicYearBIC = new BeanItemContainer<ENTRANCE_YEAR>(ENTRANCE_YEAR.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(academicYearQM));
            academicYearCB = new ComboBox();
            academicYearCB.setContainerDataSource(academicYearBIC);
            academicYearCB.setImmediate(true);
            academicYearCB.setNullSelectionAllowed(true);
            academicYearCB.setTextInputAllowed(false);
            academicYearCB.setFilteringMode(FilteringMode.OFF);
            academicYearCB.setWidth(100, Unit.PIXELS);
            academicYearCB.addValueChangeListener(new AcademicYearChangeListener());
            hl.addComponent(academicYearCB);

            l = new Label();
            l.addStyleName("bold");
            l.setWidthUndefined();
            l.setValue(getUILocaleUtil().getCaption("semester"));
            hl.addComponent(l);

            QueryModel<SEMESTER_PERIOD> semesterPeriodQM = new QueryModel<SEMESTER_PERIOD>(SEMESTER_PERIOD.class);
            semesterPeriodQM.addWhere("id", ECriteria.MORE_EQUAL, 3);
            semesterPeriodQM.addOrder("id");
            BeanItemContainer<SEMESTER_PERIOD> semesterPeriodBIC = new BeanItemContainer<SEMESTER_PERIOD>(SEMESTER_PERIOD.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(semesterPeriodQM));
            semesterPeriodCB = new ComboBox();
            semesterPeriodCB.setContainerDataSource(semesterPeriodBIC);
            semesterPeriodCB.setImmediate(true);
            semesterPeriodCB.setNullSelectionAllowed(true);
            semesterPeriodCB.setTextInputAllowed(false);
            semesterPeriodCB.setFilteringMode(FilteringMode.OFF);
            semesterPeriodCB.setPageLength(0);
            semesterPeriodCB.setWidth(100, Unit.PIXELS);
            semesterPeriodCB.addValueChangeListener(new SemesterPeriodChangeListener());
            hl.addComponent(semesterPeriodCB);

            statusLabel = new Label();
            statusLabel.addStyleName("bold");
            statusLabel.setWidthUndefined();
            statusLabel.setValue(getUILocaleUtil().getCaption("status"));
            hl.addComponent(statusLabel);

            createStatus = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(CURRICULUM_STATUS.class, ID.valueOf(1)).getStatusName();

            getContent().addComponent(hl);
            getContent().setComponentAlignment(hl, Alignment.TOP_CENTER);

            hl = new HorizontalLayout();
            hl.setWidthUndefined();
            hl.setSpacing(true);

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
            hl.addComponent(conform);

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
            hl.addComponent(approve);

            Button print = new Button();
            print.setCaption(getUILocaleUtil().getCaption("print"));
            print.setWidth(120, Unit.PIXELS);
            print.setIcon(new ThemeResource("img/button/printer.png"));
            print.addStyleName("print");
            hl.addComponent(print);

            FileDownloader fd = new CurriculumDownloader(new StreamResource(new StreamSource() {

                @Override
                public InputStream getStream() {
                    return getDownloadInputStream();
                }
            }, getFilename()));
            fd.extend(print);

            getContent().addComponent(hl);
            getContent().setComponentAlignment(hl, Alignment.TOP_CENTER);

            grid = new GridWidget(VCurriculumDetail.class);
            grid.addEntityListener(this);
            grid.setButtonVisible(AbstractToolbar.ADD_BUTTON, false);
            grid.setButtonVisible(AbstractToolbar.EDIT_BUTTON, false);
            grid.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
            grid.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, false);
            DBGridModel gridGM = (DBGridModel) grid.getWidgetModel();
            gridGM.setMultiSelect(false);
            gridGM.setRefreshType(ERefreshType.MANUAL);
            gridGM.setHeightByRows(15);
            gridGM.setFooterVisible(true);

            StringColumnFooterModel nameFooter = new StringColumnFooterModel();
            nameFooter.setFooterType(EColumnFooterType.CAPTION);
            nameFooter.setColumnName("subjectName");
            nameFooter.setValue(getUILocaleUtil().getCaption("total"));
            gridGM.addFooterModel(nameFooter);

            IntegerColumnFooterModel creditFooter = new IntegerColumnFooterModel();
            creditFooter.setFooterType(EColumnFooterType.SUM);
            creditFooter.setColumnName("credit");
            gridGM.addFooterModel(creditFooter);

            Button add = new Button();
            add.setWidth(120, Unit.PIXELS);
            add.setIcon(new ThemeResource("img/button/new.png"));
            add.addClickListener(new AddListener());
            int i = grid.addButtonFirst(add);
            grid.setButtonDescription(i, "new");

            getContent().addComponent(grid);
            getContent().setComponentAlignment(grid, Alignment.MIDDLE_CENTER);
        }
    }

    private void refresh() {
        curriculum = null;
        List<VCurriculumDetail> list = new ArrayList<VCurriculumDetail>(1);
        try {
            SEMESTER_DATA sd = getOrCreateSemesterData();
            if (sd != null) {
                QueryModel<CURRICULUM_SUMMER> qm = new QueryModel<CURRICULUM_SUMMER>(CURRICULUM_SUMMER.class);
                qm.addWhere("semesterData", ECriteria.EQUAL, sd.getId());
                qm.addWhere("deleted", Boolean.FALSE);

                try {
                    curriculum = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(qm);
                    curriculum = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(CURRICULUM_SUMMER.class, curriculum.getId());
                    statusLabel.setValue(getUILocaleUtil().getCaption("status") + ": " + curriculum.getCurriculumStatus().getStatusName());
                    String sql = "select a.ID, b.CODE SUBJECT_CODE, b.NAME_" + CommonUtils.getLanguage() + " SUBJECT_NAME, decode(b.SUBJECT_CYCLE_ID, 5, f.CYCLE_SHORT_NAME, c.CYCLE_SHORT_NAME) CYCLE_SHORT_NAME, d.CREDIT, e.FORMULA from CURRICULUM_SUMMER_DETAIL a inner join SUBJECT b on a.SUBJECT_ID = b.ID inner join SUBJECT_CYCLE c on b.SUBJECT_CYCLE_ID = c.ID inner join CREDITABILITY d on b.CREDITABILITY_ID = d.ID inner join ACADEMIC_FORMULA e on b.ACADEMIC_FORMULA_ID = e.ID left join SUBJECT_CYCLE f on a.SUBJECT_CYCLE_ID = f.ID where a.CURRICULUM_ID = ?1 and a.DELETED = ?2";
                    Map<Integer, Object> params = new HashMap<Integer, Object>(2);
                    params.put(1, curriculum.getId().getId());
                    params.put(2, Boolean.FALSE);
                    List tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
                    for (Object o : tmpList) {
                        Object[] oo = (Object[]) o;
                        VCurriculumDetail vcd = new VCurriculumDetail();
                        vcd.setId(ID.valueOf((long) oo[0]));
                        vcd.setCode((String) oo[1]);
                        vcd.setSubjectName((String) oo[2]);
                        vcd.setCycleShortName((String) oo[3]);
                        vcd.setCredit(((BigDecimal) oo[4]).intValue());
                        vcd.setFormula((String) oo[5]);
                        list.add(vcd);
                    }
                } catch (NoResultException nrex) {
                    curriculum = new CURRICULUM_SUMMER();
                    curriculum.setSemesterData(sd);
                    try {
                        curriculum.setCurriculumStatus(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(CURRICULUM_STATUS.class, ID.valueOf(1)));
                        statusLabel.setValue(getUILocaleUtil().getCaption("status") + ": " + createStatus);
                    } catch (Exception ex1) {
                        LOG.error("Unable to set curriculum status: ", ex1);
                        Message.showError(ex1.toString());
                    }
                }

                grid.getWidgetModel().setReadOnly(curriculum.getCurriculumStatus().getId().equals(ID.valueOf(3)));
                if (curriculum.getId() != null) {
                    conform.setEnabled(!curriculum.getCurriculumStatus().getId().equals(ID.valueOf(3)) && !curriculum.getCurriculumStatus().getId().equals(ID.valueOf(2)));
                    approve.setEnabled(!curriculum.getCurriculumStatus().getId().equals(ID.valueOf(3)));
                } else {
                    conform.setEnabled(true);
                    approve.setEnabled(true);
                }
            }

            ((DBGridModel) grid.getWidgetModel()).setEntities(list);
            grid.refresh();
        } catch (Exception ex) {
            LOG.error("Unable to refresh curriculum: ", ex);
            Message.showError(ex.toString());
        }
    }

    private void conform() throws Exception {
        if (!grid.getAllEntities().isEmpty()) {
            curriculum.setCurriculumStatus(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(CURRICULUM_STATUS.class, ID.valueOf(2)));
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(curriculum);

            statusLabel.setValue(getUILocaleUtil().getCaption("status") + ": " + curriculum.getCurriculumStatus().getStatusName());
            conform.setEnabled(false);
        }
    }

    private void approve() throws Exception {
        if (!grid.getAllEntities().isEmpty()) {
            if (curriculum.getCurriculumStatus().getId().equals(ID.valueOf(2))) {
                CommonEntityFacadeBean session = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class);
                // Approve only unsaved subjects
                String sql = "select a.* from SUBJECT a inner join CURRICULUM_SUMMER_DETAIL b on a.ID = b.SUBJECT_ID and b.CURRICULUM_ID = ?1 and b.DELETED = ?2 where not exists (select 1 from SEMESTER_SUBJECT c where a.ID = c.SUBJECT_ID and c.SEMESTER_DATA_ID = ?3)";
                Map<Integer, Object> params = new HashMap<Integer, Object>(3);
                params.put(1, curriculum.getId().getId());
                params.put(2, Boolean.FALSE);
                params.put(3, curriculum.getSemesterData().getId().getId());
                List<SUBJECT> subjectList = session.lookup(sql, params, SUBJECT.class);
                if (!subjectList.isEmpty()) {
                    List<SEMESTER_SUBJECT> newList = new ArrayList<SEMESTER_SUBJECT>(subjectList.size());
                    for (SUBJECT s : subjectList) {
                        SEMESTER_SUBJECT ss = new SEMESTER_SUBJECT();
                        ss.setSemesterData(curriculum.getSemesterData());
                        ss.setSubject(s);
                        newList.add(ss);
                    }

                    curriculum.setCurriculumStatus(session.lookup(CURRICULUM_STATUS.class, ID.valueOf(3)));

                    session.create(newList);
                    session.merge(curriculum);

                    statusLabel.setValue(getUILocaleUtil().getCaption("status") + ": " + curriculum.getCurriculumStatus().getStatusName());
                    approve.setEnabled(false);
                    refresh();
                }
            }
        }
    }

    private void save() throws Exception {
        if (curriculum == null) {
            throw new Exception(getUILocaleUtil().getMessage("select.academic.year.and.semester"));
        }

        try {
            if (curriculum.getId() == null) {
                curriculum.setCreated(new Date());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(curriculum);
            }
        } catch (Exception ex) {
            LOG.error("Unable to save curriculum: ", ex);
            throw ex;
        }
    }

    private String getFilename() {
        Calendar c = Calendar.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append("/tmp/curriculum_summer_");
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

            Sheet sheet = wb.createSheet(getUILocaleUtil().getCaption("curriculum.summer"));
            sheet.setDisplayGridlines(true);
            Row row = sheet.createRow(0);
            Cell cell = row.createCell(0);
            cell.setCellValue(getUILocaleUtil().getCaption("curriculum"));
            cell.setCellStyle(styles.get(ExcelStyles.TITLE));
            sheet.addMergedRegion(CellRangeAddress.valueOf("$A$1:$M$1"));

            StringBuilder sb = new StringBuilder();
            sb.append(String.format(getUILocaleUtil().getCaption("study.year"), curriculum.getSemesterData().getYear().toString()));
            sb.append(", ");
            sb.append(String.format(getUILocaleUtil().getCaption("semester.period"), curriculum.getSemesterData().getSemesterPeriod().toString()));

            row = sheet.createRow(1);
            cell = row.createCell(0);
            cell.setCellValue(sb.toString());
            cell.setCellStyle(styles.get(ExcelStyles.SUBTITLE_CENTER));
            sheet.addMergedRegion(CellRangeAddress.valueOf("$A$2:$M$2"));

            row = sheet.createRow(3);
            cell = row.createCell(0);
            cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(VCurriculumDetail.class, "code"));
            cell.setCellStyle(styles.get(ExcelStyles.HEADER));

            cell = row.createCell(1);
            cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(VCurriculumDetail.class, "subjectName"));
            cell.setCellStyle(styles.get(ExcelStyles.HEADER));

            cell = row.createCell(2);
            cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(VCurriculumDetail.class, "cycleShortName"));
            cell.setCellStyle(styles.get(ExcelStyles.HEADER_VERTICAL));

            cell = row.createCell(3);
            cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(VCurriculumDetail.class, "credit"));
            cell.setCellStyle(styles.get(ExcelStyles.HEADER_VERTICAL));

            cell = row.createCell(4);
            cell.setCellValue(getUILocaleUtil().getEntityFieldLabel(VCurriculumDetail.class, "formula"));
            cell.setCellStyle(styles.get(ExcelStyles.HEADER_VERTICAL));

            int startRow = 4;
            int totalCredit = 0;
            List<Entity> list = grid.getAllEntities();
            for (Entity e : list) {
                VCurriculumDetail vcd = (VCurriculumDetail) e;
                row = sheet.createRow(startRow++);
                cell = row.createCell(0);
                cell.setCellValue(vcd.getCode());

                cell = row.createCell(1);
                cell.setCellValue(vcd.getSubjectName());

                cell = row.createCell(2);
                cell.setCellValue(vcd.getCycleShortName());

                cell = row.createCell(3);
                cell.setCellValue(vcd.getCredit());

                cell = row.createCell(4);
                cell.setCellValue(vcd.getFormula());

                totalCredit += vcd.getCredit();
            }

            row = sheet.createRow(startRow++);
            cell = row.createCell(1);
            cell.setCellValue(String.format(getUILocaleUtil().getCaption("credit.sum"), ""));
            cell.setCellStyle(styles.get(ExcelStyles.FOOTER));

            cell = row.createCell(3);
            cell.setCellValue(totalCredit);
            cell.setCellStyle(styles.get(ExcelStyles.FOOTER));

            sheet.setColumnWidth(0, 12 * 256);
            sheet.setColumnWidth(1, 64 * 256);
            sheet.setColumnWidth(2, 5 * 256);
            sheet.setColumnWidth(3, 3 * 256);
            sheet.setColumnWidth(4, 6 * 256);

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

    private ENTRANCE_YEAR getAcademicYear() {
        if (academicYearCB != null) {
            return (ENTRANCE_YEAR) academicYearCB.getValue();
        }

        return null;
    }

    private SEMESTER_PERIOD getSemesterPeriod() {
        if (semesterPeriodCB != null) {
            return (SEMESTER_PERIOD) semesterPeriodCB.getValue();
        }

        return null;
    }

    private SEMESTER_DATA getOrCreateSemesterData() throws Exception {
        ENTRANCE_YEAR ay = getAcademicYear();
        SEMESTER_PERIOD sp = getSemesterPeriod();
        if (ay != null && sp != null) {
            QueryModel<SEMESTER_DATA> sdQM = new QueryModel<SEMESTER_DATA>(SEMESTER_DATA.class);
            sdQM.addWhere("year", ECriteria.EQUAL, ay.getId());
            sdQM.addWhereAnd("semesterPeriod", ECriteria.EQUAL, sp.getId());

            try {
                return SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(sdQM);
            } catch (NoResultException nrex) {
                int year = ay.getEndYear();
                Date beginDate = DateUtils.createDate(1, 6, year);
                Date endDate = DateUtils.createDate(30, 6, year);
                if (sp.getId().equals(ID.valueOf(4))) {
                    beginDate = DateUtils.createDate(1, 7, year);
                    endDate = DateUtils.createDate(31, 7, year);
                } else if (sp.getId().equals(ID.valueOf(5))) {
                    beginDate = DateUtils.createDate(1, 8, year);
                    endDate = DateUtils.createDate(31, 8, year);
                }
                SEMESTER_DATA sd = new SEMESTER_DATA();
                sd.setYear(ay);
                sd.setSemesterPeriod(sp);
                sd.setBeginDate(beginDate);
                sd.setEndDate(endDate);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(sd);

                return sd;
            } catch (Exception ex) {
                LOG.error("Unable to load semester data: ", ex);
                throw ex;
            }
        }

        return null;
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
    }

    @Override
    public boolean preCreate(Object source, int buttonId) {
        return false;
    }

    @Override
    public void onCreate(Object source, Entity e, int buttonId) {
    }

    @Override
    public boolean onEdit(Object source, Entity e, int buttonId) {
        return false;
    }

    @Override
    public boolean onPreview(Object source, Entity e, int buttonId) {
        return false;
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
        return false;
    }

    @Override
    public boolean preDelete(Object source, List<Entity> entities, int buttonId) {
        if (source.equals(grid)) {
            List<SEMESTER_SUBJECT> ssDelList = new ArrayList<SEMESTER_SUBJECT>();
            List<String> notDelList = new ArrayList<String>();
            List<CURRICULUM_SUMMER_DETAIL> delList = new ArrayList<CURRICULUM_SUMMER_DETAIL>();
            try {
                SEMESTER_DATA sd = getOrCreateSemesterData();
                CommonEntityFacadeBean session = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class);
                QueryModel<SEMESTER_SUBJECT> ssQM = new QueryModel<SEMESTER_SUBJECT>(SEMESTER_SUBJECT.class);
                ssQM.addWhere("semesterData", ECriteria.EQUAL, sd.getId());
                for (Entity e : entities) {
                    CURRICULUM_SUMMER_DETAIL csd = session.lookup(CURRICULUM_SUMMER_DETAIL.class, e.getId());
                    ssQM.addWhere("subject", ECriteria.EQUAL, csd.getSubject().getId());
                    SEMESTER_SUBJECT ss = null;
                    try {
                        ss = session.lookupSingle(ssQM);
                    } catch (NoResultException nrex) {
                    }
                    if (ss != null) {
                        String sql = "select sum(coalesce(t1.CNT, 0)) from (select count(a1.ID) CNT from EXAM_SCHEDULE a1 where a1.SUBJECT_ID = ?1 and a1.DELETED = ?2 union select count(a2.ID) CNT from SCHEDULE_DETAIL a2 where a2.SUBJECT_ID = ?3 union select count(a3.SUBJECT_ID) CNT from STUDENT_SUBJECT a3 where a3.SUBJECT_ID = ?4) t1";
                        Map<Integer, Object> params = new HashMap<Integer, Object>(4);
                        params.put(1, ss.getId().getId());
                        params.put(2, Boolean.FALSE);
                        params.put(3, ss.getId().getId());
                        params.put(4, ss.getId().getId());
                        Integer sum = null;
                        try {
                            sum = (Integer) session.lookupSingle(sql, params);
                        } catch (NoResultException nrex) {
                        }
                        if (sum != null && sum > 0) {
                            notDelList.add(csd.getSubject().getNameRU());
                        } else {
                            ssDelList.add(ss);
                            delList.add(csd);
                        }
                    } else {
                        delList.add(csd);
                    }
                }

                if (!delList.isEmpty()) {
                    if (!ssDelList.isEmpty()) {
                        session.delete(ssDelList);
                    }
                    session.delete(delList);
                    refresh();
                }

                if (!notDelList.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(getUILocaleUtil().getMessage("error.recorddependencyexists"));
                    sb.append("\n");
                    boolean first = true;
                    for (String s : notDelList) {
                        if (!first) {
                            sb.append("\n");
                        }
                        sb.append(s);
                        first = false;
                    }
                }
            } catch (Exception ex) {
                LOG.error("Unable to delete curriculum detail: ", ex);
                Message.showError(ex.toString());
            }
        }

        return false;
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

    private class AcademicYearChangeListener implements ValueChangeListener {

        @Override
        public void valueChange(ValueChangeEvent ev) {
            refresh();
        }
    }

    private class SemesterPeriodChangeListener implements ValueChangeListener {

        @Override
        public void valueChange(ValueChangeEvent ev) {
            refresh();
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

    private class AddListener implements ClickListener {

        @Override
        public void buttonClick(ClickEvent ev) {
            try {
                save();
                QueryModel<DEPARTMENT> chairQM = new QueryModel<DEPARTMENT>(DEPARTMENT.class);
                chairQM.addWhereNotNull("parent");
                chairQM.addWhereAnd("deleted", Boolean.FALSE);
                chairQM.addOrder("deptName");
                BeanItemContainer<DEPARTMENT> chairBIC = new BeanItemContainer<DEPARTMENT>(DEPARTMENT.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(chairQM));
                ComboBox chairCB = new ComboBox();
                chairCB.setContainerDataSource(chairBIC);
                chairCB.setImmediate(true);
                chairCB.setNullSelectionAllowed(true);
                chairCB.setTextInputAllowed(true);
                chairCB.setFilteringMode(FilteringMode.CONTAINS);
                chairCB.setWidth(400, Unit.PIXELS);
                chairCB.setPageLength(0);

                QueryModel<LEVEL> levelQM = new QueryModel<LEVEL>(LEVEL.class);
                levelQM.addOrder("levelName");
                BeanItemContainer<LEVEL> levelBIC = new BeanItemContainer<LEVEL>(LEVEL.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(levelQM));
                ComboBox levelCB = new ComboBox();
                levelCB.setContainerDataSource(levelBIC);
                levelCB.setImmediate(true);
                levelCB.setNullSelectionAllowed(true);
                levelCB.setTextInputAllowed(false);
                levelCB.setFilteringMode(FilteringMode.OFF);
                levelCB.setPageLength(0);

                QueryModel<SUBJECT_CYCLE> subjectCycleQM = new QueryModel<SUBJECT_CYCLE>(SUBJECT_CYCLE.class);
                subjectCycleQM.addWhere("id", ECriteria.LESS_EQUAL, ID.valueOf(4));
                subjectCycleQM.addOrder("cycleShortName");
                BeanItemContainer<SUBJECT_CYCLE> subjectCycleBIC = new BeanItemContainer<SUBJECT_CYCLE>(SUBJECT_CYCLE.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(subjectCycleQM));
                ComboBox subjectCycleCB = new ComboBox();
                subjectCycleCB.setContainerDataSource(subjectCycleBIC);
                subjectCycleCB.setImmediate(true);
                subjectCycleCB.setNullSelectionAllowed(true);
                subjectCycleCB.setTextInputAllowed(false);
                subjectCycleCB.setFilteringMode(FilteringMode.OFF);
                subjectCycleCB.setPageLength(0);

                QueryModel<CREDITABILITY> creditabilityQM = new QueryModel<CREDITABILITY>(CREDITABILITY.class);
                creditabilityQM.addOrder("credit");
                BeanItemContainer<CREDITABILITY> creditabilityBIC = new BeanItemContainer<CREDITABILITY>(CREDITABILITY.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(creditabilityQM));
                ComboBox creditabilityCB = new ComboBox();
                creditabilityCB.setContainerDataSource(creditabilityBIC);
                creditabilityCB.setImmediate(true);
                creditabilityCB.setNullSelectionAllowed(true);
                creditabilityCB.setTextInputAllowed(false);
                creditabilityCB.setFilteringMode(FilteringMode.OFF);
                creditabilityCB.setPageLength(0);

                TextField subjectCodeTF = new TextField();
                subjectCodeTF.setWidth(100, Unit.PIXELS);
                subjectCodeTF.setNullRepresentation("");
                subjectCodeTF.setNullSettingAllowed(true);
                subjectCodeTF.setImmediate(true);

                subjectSelectDlg = new SubjectSelectDialog(new AddNewSubjectListener(), V_SUBJECT_SELECT.class);
                QueryModel qm = ((DBGridModel) subjectSelectDlg.getSelectModel()).getQueryModel();
                qm.addWhere("chair", ECriteria.EQUAL, ID.valueOf(-1));
                //				qm.addWhereAnd("mandatory", Boolean.TRUE);
                qm.addWhere("subjectCycle", ECriteria.NOT_EQUAL, ID.valueOf(4));
                subjectSelectDlg.setDialogWidth(600);
                subjectSelectDlg.setDialogHeight(300);
                subjectSelectDlg.getFilterModel().addFilter("chair", chairCB);
                subjectSelectDlg.getFilterModel().addFilter("level", levelCB);
                subjectSelectDlg.getFilterModel().addFilter("subjectCycle", subjectCycleCB);
                subjectSelectDlg.getFilterModel().addFilter("creditability", creditabilityCB);
                subjectSelectDlg.getFilterModel().addFilter("code", subjectCodeTF);
                subjectSelectDlg.setFilterRequired(true);
                subjectSelectDlg.initFilter();
                subjectSelectDlg.open();
            } catch (Exception ex) {
                LOG.error("Unable to open subject select dialog: ", ex);
                Message.showError(ex.getMessage());
            }
        }
    }

    private class AddNewSubjectListener extends AbstractYesButtonListener {

        private boolean can = false;

        @Override
        public void buttonClick(ClickEvent ev) {
            List<Entity> selectedList = subjectSelectDlg.getSelectedEntities();
            if (!selectedList.isEmpty()) {
                List<CURRICULUM_SUMMER_DETAIL> newList = new ArrayList<CURRICULUM_SUMMER_DETAIL>(selectedList.size());
                try {
                    Date created = new Date();
                    for (Entity e : selectedList) {
                        CURRICULUM_SUMMER_DETAIL cd = new CURRICULUM_SUMMER_DETAIL();
                        cd.setCurriculum(curriculum);
                        cd.setSubject(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(SUBJECT.class, e.getId()));
                        if (cd.getSubject().getSubjectCycle().getId().equals(MULTI_CYCLE_ID)) {
                            cd.setSubjectCycle(subjectSelectDlg.getSubjectCycle());
                        }
                        cd.setCreated(created);
                        newList.add(cd);
                    }

                    if (!newList.isEmpty()) {
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(newList);
                        refresh();
                    }
                } catch (Exception ex) {
                    LOG.error("Unable to create new curriculum detail: ", ex);
                    Message.showError(ex.toString());
                }
            }
        }

        @Override
        protected boolean canClose() {
            return can;
        }

        @Override
        protected boolean canProcess() {
            List<Entity> selectedList = subjectSelectDlg.getSelectedEntities();
            can = !selectedList.isEmpty();
            if (can) {
                SUBJECT_CYCLE sc = subjectSelectDlg.getSubjectCycle();
                boolean multi = false;
                QueryModel<SUBJECT_CYCLE> scQM = new QueryModel<SUBJECT_CYCLE>(SUBJECT_CYCLE.class);
                FromItem scFI = scQM.addJoin(EJoin.INNER_JOIN, "id", SUBJECT.class, "subjectCycle");
                List<ID> idList = new ArrayList<ID>(selectedList.size());

                Map<Integer, Object> params = new HashMap<Integer, Object>(2);
                params.put(1, curriculum.getId().getId());
                params.put(2, Boolean.FALSE);
                String sql = "select count(a.SUBJECT_ID) from CURRICULUM_SUMMER_DETAIL a where a.CURRICULUM_ID = ?1 and a.DELETED = ?2 and a.SUBJECT_ID in (";
                StringBuilder sb = new StringBuilder();
                boolean first = true;
                for (Entity e : selectedList) {
                    if (!first) {
                        sb.append(", ");
                    }
                    sb.append(e.getId().getId());
                    first = false;
                    idList.add(e.getId());
                }
                sb.append(")");
                sql = sql + sb.toString();

                scQM.addWhereIn(scFI, "id", idList);
                try {
                    List<SUBJECT_CYCLE> scList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(scQM);
                    for (SUBJECT_CYCLE sc1 : scList) {
                        multi = (sc1.getId().equals(MULTI_CYCLE_ID));
                        if (multi) {
                            break;
                        }
                    }

                    can = (!multi || sc != null);
                    if (!can) {
                        Message.showError(getUILocaleUtil().getMessage("select.curriculum.component"));
                        return can;
                    }

                    Integer count = (Integer) SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(sql, params);
                    can = (count == 0);
                    if (!can) {
                        Message.showError(getUILocaleUtil().getMessage("selected.subjects.already.exists"));
                    }
                } catch (Exception ex) {
                    can = false;
                    LOG.error("Unable to check subjects: ", ex);
                    Message.showError(ex.toString());
                }
            }

            return can;
        }
    }
}
