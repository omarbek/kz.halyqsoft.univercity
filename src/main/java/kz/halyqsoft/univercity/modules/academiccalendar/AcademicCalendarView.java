package kz.halyqsoft.univercity.modules.academiccalendar;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.*;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import kz.halyqsoft.univercity.entity.beans.univercity.ACADEMIC_CALENDAR;
import kz.halyqsoft.univercity.entity.beans.univercity.ACADEMIC_CALENDAR_DETAIL;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.modules.academiccalendar.item.AbstractACItem;
import kz.halyqsoft.univercity.modules.academiccalendar.item.TwoDateItem;
import kz.halyqsoft.univercity.modules.academiccalendar.layout.AcademicCalendarLayout;
import kz.halyqsoft.univercity.modules.academiccalendar.layout.AllFacultyLayout;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Calendar;
import java.util.List;

/**
 * @author Omarbek Dinassil
 * @created Oct 24, 2016 6:00:17 PM
 */
@SuppressWarnings({"serial"})
public class AcademicCalendarView extends AbstractTaskView {

    private ACADEMIC_CALENDAR academicCalendar;
    private ComboBox facultyCB;
    private ComboBox studyYearCB;
    private Label statusLabel;
    private Label errorLabel = new Label();
    private Button createButton;
    private Button saveButton;
    private Button confirmButton;
    private Button approveButton;
    private Button printButton;
    private AcademicCalendarLayout layout;

    public AcademicCalendarView(AbstractTask task) throws Exception {
        super(task);
    }


    @Override
    public void initView(boolean readOnly) throws Exception {
        HorizontalLayout filterHL = new HorizontalLayout();
        filterHL.setSpacing(true);
        filterHL.setMargin(true);
        filterHL.addStyleName("form-panel");

        Label facultyLabel = new Label();
        facultyLabel.addStyleName("bold");
        facultyLabel.setWidthUndefined();
        facultyLabel.setValue(getUILocaleUtil().getCaption("faculty"));
        filterHL.addComponent(facultyLabel);

        QueryModel<ACADEMIC_CALENDAR_FACULTY> facultyQM = new QueryModel<>(ACADEMIC_CALENDAR_FACULTY.class);
        facultyQM.addOrder("facultyName");
        BeanItemContainer<ACADEMIC_CALENDAR_FACULTY> facultyBIC = new BeanItemContainer<>(
                ACADEMIC_CALENDAR_FACULTY.class, SessionFacadeFactory.getSessionFacade(
                CommonEntityFacadeBean.class).lookup(facultyQM));
        facultyCB = new ComboBox();
        facultyCB.setContainerDataSource(facultyBIC);
        facultyCB.setImmediate(true);
        facultyCB.setNullSelectionAllowed(true);
        facultyCB.setFilteringMode(FilteringMode.OFF);
        facultyCB.setTextInputAllowed(false);
        facultyCB.setWidth(150, Unit.PIXELS);
        facultyCB.setPageLength(0);
        filterHL.addComponent(facultyCB);

        Label studyYearLabel = new Label();
        studyYearLabel.addStyleName("bold");
        studyYearLabel.setWidthUndefined();
        studyYearLabel.setValue(getUILocaleUtil().getCaption("study.year.1"));
        filterHL.addComponent(studyYearLabel);

        Calendar calendar = Calendar.getInstance();
        QueryModel<ENTRANCE_YEAR> entranceYearQM = new QueryModel<>(ENTRANCE_YEAR.class);
        entranceYearQM.addWhere("beginYear", ECriteria.EQUAL, calendar.get(Calendar.YEAR));
        BeanItemContainer<ENTRANCE_YEAR> entranceYearBIC = new BeanItemContainer<>(
                ENTRANCE_YEAR.class, SessionFacadeFactory.getSessionFacade(
                CommonEntityFacadeBean.class).lookup(entranceYearQM));
        studyYearCB = new ComboBox();
        studyYearCB.setContainerDataSource(entranceYearBIC);
        studyYearCB.setImmediate(true);
        studyYearCB.setNullSelectionAllowed(true);
        studyYearCB.setFilteringMode(FilteringMode.OFF);
        studyYearCB.setTextInputAllowed(false);
        filterHL.addComponent(studyYearCB);

        statusLabel = new Label();
        statusLabel.addStyleName("bold");
        statusLabel.setWidthUndefined();
        statusLabel.setValue(getUILocaleUtil().getCaption("status"));
        filterHL.addComponent(statusLabel);

        facultyCB.addValueChangeListener(new FacultyChangeListener());
        studyYearCB.addValueChangeListener(new StudyYearChangeListener());

        getContent().addComponent(filterHL);
        getContent().setComponentAlignment(filterHL, Alignment.TOP_CENTER);

        errorLabel.addStyleName("error");
        errorLabel.setWidthUndefined();
        errorLabel.setValue(getUILocaleUtil().getMessage("academic.calendar.not.filled"));
        errorLabel.setVisible(false);

        getContent().addComponent(errorLabel);
        getContent().setComponentAlignment(errorLabel, Alignment.TOP_CENTER);

        HorizontalLayout toolbar = new HorizontalLayout();
        toolbar.setSpacing(true);

        createButton = new Button();
        saveButton = new Button();
        confirmButton = new Button();
        approveButton = new Button();

        createButton.setCaption(getUILocaleUtil().getCaption("create"));
        createButton.setWidth(120, Unit.PIXELS);
        createButton.addStyleName("create");
        createButton.setVisible(false);
        createButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent ev) {
                try {
                    create();
                    refresh();
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to create academic calendar", ex);
                }
            }
        });
        toolbar.addComponent(createButton);

        saveButton.setCaption(getUILocaleUtil().getCaption("save"));
        saveButton.setWidth(120, Unit.PIXELS);
        saveButton.setIcon(new ThemeResource("img/button/ok.png"));
        saveButton.addStyleName("save");
        saveButton.setVisible(false);
        saveButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent ev) {
                try {
                    save();
                    refresh();
                    CommonUtils.showSavedNotification();
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to save curriculum", ex);
                }
            }
        });
        toolbar.addComponent(saveButton);

        confirmButton.setCaption(getUILocaleUtil().getCaption("conform"));
        confirmButton.setWidth(120, Unit.PIXELS);
        confirmButton.addStyleName("conform");
        confirmButton.setVisible(false);
        confirmButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent ev) {
                try {
                    confirm();
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to confirm curriculum", ex);
                }
            }
        });
        toolbar.addComponent(confirmButton);

        approveButton.setCaption(getUILocaleUtil().getCaption("approve"));
        approveButton.setWidth(120, Unit.PIXELS);
        approveButton.addStyleName("approve");
        approveButton.setVisible(false);
        approveButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent ev) {
                try {
                    approve();
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to approve curriculum", ex);
                }
            }
        });
        toolbar.addComponent(approveButton);
        printButton = new Button();
        printButton.setCaption(getUILocaleUtil().getCaption("print"));
        printButton.setWidth(120, Unit.PIXELS);
        printButton.setIcon(new ThemeResource("img/button/printer.png"));
        printButton.addStyleName("print");
        printButton.setVisible(false);
//        toolbar.addComponent(printButton);

        String filename = String.valueOf(new Random().nextDouble()) + ".pdf";
        final StreamResource resource = new StreamResource(new StreamSource() {
            @Override
            public InputStream getStream() {
                return getDownloadInputStream(academicCalendar);
            }
        }, filename);
        resource.setCacheTime(0);
        resource.setMIMEType("application/pdf");
        final BrowserWindowOpener opener = new BrowserWindowOpener(resource);

        printButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                opener.setResource(null);
                resource.setFilename(String.valueOf(new Random().nextDouble()) + ".pdf");
                opener.setResource(resource);

            }
        });
        opener.extend(printButton);
        getContent().addComponent(toolbar);
        getContent().setComponentAlignment(toolbar, Alignment.TOP_CENTER);

        initContent();
    }

    protected InputStream getDownloadInputStream(ACADEMIC_CALENDAR academicCalendar) {
        int fontSize;
        int bigFontSize;
        BaseFont timesNewRoman;
        ByteArrayOutputStream baos = null;
        Font font = null;
        Font boldFont = null;

        try {
            String fontPath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/WEB-INF/fonts";
            fontSize = 12;
            bigFontSize = 10;
            timesNewRoman = BaseFont.createFont(fontPath + "/TimesNewRoman/times.ttf", BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED);
            font = new Font(timesNewRoman, fontSize);
            boldFont = new Font(timesNewRoman, bigFontSize, Font.BOLD);

            if (layout != null) {
                List<AbstractACItem> list = layout.getItems();
                if (layout instanceof AllFacultyLayout) {
                    for (AbstractACItem i : list) {
                        ACADEMIC_CALENDAR_ITEM aci = i.getAcademicCalendarDetail().getAcademicCalendarItem();
                        String text = aci.getItemName();
                        if (aci.getItemType().equals("a")) {
                            TwoDateItem tdi = (TwoDateItem) i;
                            @SuppressWarnings("unused")
                            Date date1 = tdi.getDate1();
                            @SuppressWarnings("unused")
                            Date date2 = tdi.getDate2();

                        } else if (aci.getItemType().equals("b")) {
                            TwoDateItem tdi = (TwoDateItem) i;
                            @SuppressWarnings("unused")
                            Date date1 = tdi.getDate1();
                            @SuppressWarnings("unused")
                            Date date2 = tdi.getDate2();
                        }
                    }
                    // TODO: ALll
                }
            }
        } catch (DocumentException e) {
            LOG.error("Unable to download font: ", e);
        } catch (IOException e) {
            LOG.error("Unable to reach: ", e);
            e.printStackTrace();
        }

        try {

            Document document = new Document();
            // document.setMargins(-20, -20, 20, 20);
            baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);

            QueryModel<ACADEMIC_CALENDAR_DETAIL> acdQM = new QueryModel<>(ACADEMIC_CALENDAR_DETAIL.class);
            acdQM.addWhere("academicCalendar", ECriteria.EQUAL, academicCalendar.getId());
            acdQM.addOrder("academicCalendarItem");
            List<ACADEMIC_CALENDAR_DETAIL> tmpList = SessionFacadeFactory.getSessionFacade(
                    CommonEntityFacadeBean.class).lookup(acdQM);

            List<ACADEMIC_CALENDAR_DETAIL> acdList = new ArrayList<ACADEMIC_CALENDAR_DETAIL>();
            for (ACADEMIC_CALENDAR_DETAIL acd : tmpList) {
                acdList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                        ACADEMIC_CALENDAR_DETAIL.class, acd.getId()));
            }

            QueryModel<DEPARTMENT> deptQM = new QueryModel<>(DEPARTMENT.class);
            List<DEPARTMENT> tempList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookup(deptQM);

            List<DEPARTMENT> deptList = new ArrayList<DEPARTMENT>();
            for (DEPARTMENT acd : tempList) {
                deptList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                        DEPARTMENT.class, acd.getId()));
            }

            document.open();

            // Number of columns
            int tableColspan = 2;
            PdfPTable table = new PdfPTable(tableColspan);
            PdfPCell cell;

            addCell(getUILocaleUtil().getCaption("pdf.academic.calendar.prorector") + "\n"
                    + getUILocaleUtil().getCaption("pdf.academic.calendar.vice.director") + "\n"
                    + getUILocaleUtil().getCaption("pdf.academic.calendar.prorector.name"), table, 1, boldFont);
            cell = new PdfPCell(new Phrase(getUILocaleUtil().getCaption("pdf.academic.calendar.rector")
                    + "\n" + getUILocaleUtil().getCaption("pdf.academic.calendar.rector.name"), boldFont));
            cell.setColspan(2);
            cell.setPaddingLeft(80);
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setPaddingBottom(10);
            table.addCell(cell);
            addCell(getUILocaleUtil().getCaption("pdf.academic.calendar.date") + String.valueOf(
                    Calendar.getInstance().get(Calendar.YEAR))
                    + getUILocaleUtil().getCaption("pdf.academic.calendar.date.year"), table, 1, boldFont);
            cell = new PdfPCell(new Phrase(getUILocaleUtil().getCaption("pdf.academic.calendar.date")
                    + String.valueOf(Calendar.getInstance().get(Calendar.YEAR))
                    + getUILocaleUtil().getCaption("pdf.academic.calendar.date.year"), boldFont));
            cell.setColspan(2);
            cell.setPaddingLeft(80);
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setPaddingBottom(10);
            table.addCell(cell);
            document.add(table);

            PdfPTable tableForCalendar = new PdfPTable(1);

            StringBuilder depts = new StringBuilder();
            for (DEPARTMENT dept : deptList) {
                if (!dept.hasParent() && !dept.isDeleted()) {
                    depts.append(dept.getDeptShortName()).append(", ");
                }
            }
            depts = new StringBuilder(depts.substring(0, depts.length() - 2));

            cell = new PdfPCell(new Phrase(depts + "\n" + getUILocaleUtil().getCaption("pdf.academic.calendar")
                    + "\n" + String.valueOf(Calendar.getInstance().get(Calendar.YEAR)) + " - "
                    + String.valueOf((Calendar.getInstance().get(Calendar.YEAR) + 1)), boldFont));
            cell.setPaddingLeft(120);
            cell.setPaddingRight(120);
            cell.setBorder(PdfPCell.NO_BORDER);
            cell.setPaddingBottom(10);
            cell.setHorizontalAlignment(Element.ALIGN_MIDDLE);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            tableForCalendar.addCell(cell);

            document.add(tableForCalendar);

            PdfPTable tableForDates = new PdfPTable(2);
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

            cell = new PdfPCell(new Phrase(getUILocaleUtil().getCaption("pdf.academic.calendar.order"), font));
            tableForDates.addCell(cell);
            cell = new PdfPCell(new Phrase(getUILocaleUtil().getCaption("pdf.academic.calendar.period"), font));
            tableForDates.addCell(cell);

            String caption, item, itemName;
            int beginYear, endYear;
            // dealing with temporary common variables for cycles works faster
            // switch works as hashtable or lookup table if cases > 5, constant
            // time for all elements
            for (ACADEMIC_CALENDAR_DETAIL acd : acdList) {
                item = acd.getAcademicCalendarItem().getItemType();
                itemName = acd.getAcademicCalendarItem().getItemName();
                caption = null;
                switch (item) {
                    case "c":
                    case "e":
                    case "13":
                    case "29":
                    case "37":
                    case "40":
                        cell = new PdfPCell(new Phrase(String.format(itemName, academicCalendar.getYear().toString()), font));
                        break;
                    case "f":
                    case "48":
                    case "118":
                        beginYear = academicCalendar.getYear().getBeginYear() + 1;
                        endYear = academicCalendar.getYear().getEndYear() + 1;
                        cell = new PdfPCell(new Phrase(String.format(itemName, beginYear + "-" + endYear), font));
                        break;
                    case "k":
                    case "l":
                        cell = new PdfPCell(new Phrase(getUILocaleUtil().getCaption("examination.session") + ": "
                                + itemName, font));
                        break;
                    case "m":
                        cell = new PdfPCell(new Phrase("��������: asd" + itemName, font));
                        break;
                    case "n":
                    case "o":
                    case "p":
                    case "75":
                    case "76":
                    case "77":
                        cell = new PdfPCell(new Phrase(getUILocaleUtil().getCaption("summer.break") + ": "
                                + itemName, font));
                        break;
                    case "s":
                    case "t":
                        cell = new PdfPCell(new Phrase(getUILocaleUtil().getCaption("undergraduate.practice")
                                + ": " + itemName, font));
                        break;
                    case "u":
                    case "v":
                        cell = new PdfPCell(new Phrase(getUILocaleUtil().getCaption("undergraduate.defense")
                                + ": " + itemName, font));
                        break;
                    case "y":
                    case "z":
                        cell = new PdfPCell(new Phrase(getUILocaleUtil().getCaption("diploma.defense") + ": "
                                + itemName, font));
                        break;
                    case "0":
                    case "16":
                    case "55":
                    case "64":
                        cell = new PdfPCell(new Phrase(String.format(itemName,
                                academicCalendar.getYear().getEndYear()), font));
                        break;
                    case "8":
                    case "9":
                    case "72":
                    case "73":
                        cell = new PdfPCell(new Phrase(getUILocaleUtil().getCaption("summer.semester") + ": "
                                + itemName, font));
                        break;
                    case "10":
                        cell = new PdfPCell(new Phrase(getUILocaleUtil().getCaption("summer.semester.bs") + ": "
                                + itemName, font));
                        break;
                    case "11":
                        caption = getUILocaleUtil().getCaption("fall.semester").toUpperCase();
                        break;
                    case "39":
                        caption = getUILocaleUtil().getCaption("spring.semester").toUpperCase();
                        break;
                    case "127":
                        caption = "KMA Entrance Exams I Period";
                        break;
                    case "129":
                        caption = "KMA Entrance Exams II Period";
                        break;
                    case "78":
                    case "131":
                        caption = "FALL SEMESTER";
                        break;
                    case "97":
                    case "155":
                        caption = "SPRING SEMESTER";
                        break;
                    case "175":
                        caption = "SUMMER SEMESTER";
                        break;
                    default:
                        cell = new PdfPCell(new Phrase(itemName, font));
                        break;
                }
                if (caption != null) {
                    cell = new PdfPCell(new Phrase(caption, boldFont));
                    cell.setBorder(PdfPCell.NO_BORDER);
                    cell.setPaddingTop(10);
                    cell.setPaddingBottom(10);
                    tableForDates.addCell(cell);

                    cell = new PdfPCell(new Phrase("", font));
                    cell.setBorder(PdfPCell.NO_BORDER);
                    tableForDates.addCell(cell);
                    cell = new PdfPCell(new Phrase(itemName, font));
                }
                tableForDates.addCell(cell);

                String dates = "";
                if (acd.getDate1() != null) {
                    dates = dates + dateFormat.format(acd.getDate1());
                }
                if (acd.getDate2() != null) {
                    dates = dates + " - " + dateFormat.format(acd.getDate2());
                }
                if (acd.getDate3() != null) {
                    dates = dates + " " + dateFormat.format(acd.getDate3());
                }
                if (acd.getDate4() != null) {
                    dates = dates + " - " + dateFormat.format(acd.getDate4());
                }
                if (acd.getDescr() != null) {
                    dates = dates + " " + acd.getDescr();
                }
                cell = new PdfPCell(new Phrase(dates, font));
                tableForDates.addCell(cell);
            }
            document.add(tableForDates);

            if (academicCalendar.getFaculty().getFacultyName().contains("���")) {
                PdfPTable tableLast = new PdfPTable(1);
                addCell(getUILocaleUtil().getCaption("pdf.academic.calendar.ise.director") + "                                                      " + getUILocaleUtil().getCaption("pdf.academic.calendar.ise.director.name"), tableLast, 1, boldFont);
                document.add(tableLast);
            } else if (academicCalendar.getFaculty().getFacultyName().contains("���")) {
                PdfPTable tableLast = new PdfPTable(1);
                addCell(getUILocaleUtil().getCaption("pdf.academic.calendar.kma.director") + "                                                      " + getUILocaleUtil().getCaption("pdf.academic.calendar.kma.director.name"), tableLast, 1, boldFont);
                addCell(getUILocaleUtil().getCaption("pdf.academic.calendar.kma.sign")
                        + String.valueOf(Calendar.getInstance().get(Calendar.YEAR)), tableLast, 1, boldFont);
                document.add(tableLast);
            } else if (academicCalendar.getFaculty().getFacultyName().contains("���")) {
                PdfPTable tableForDeans = new PdfPTable(2);
                addCell(getUILocaleUtil().getCaption("pdf.academic.calendar.dean.bf"), tableForDeans, 1, boldFont);
                addCell(getUILocaleUtil().getCaption("pdf.academic.calendar.dean.bf.name"), tableForDeans, 2, boldFont);
                addCell(getUILocaleUtil().getCaption("pdf.academic.calendar.dean.fengi"), tableForDeans, 1, boldFont);
                addCell(getUILocaleUtil().getCaption("pdf.academic.calendar.dean.fengi.name"), tableForDeans, 2, boldFont);
                addCell(getUILocaleUtil().getCaption("pdf.academic.calendar.dean.bs"), tableForDeans, 1, boldFont);
                addCell(getUILocaleUtil().getCaption("pdf.academic.calendar.dean.bs.name"), tableForDeans, 2, boldFont);
                addCell(getUILocaleUtil().getCaption("pdf.academic.calendar.dean.fit"), tableForDeans, 1, boldFont);
                addCell(getUILocaleUtil().getCaption("pdf.academic.calendar.dean.fit.name"), tableForDeans, 2, boldFont);
                addCell(getUILocaleUtil().getCaption("pdf.academic.calendar.dean.chemistry"), tableForDeans, 1, boldFont);
                addCell(getUILocaleUtil().getCaption("pdf.academic.calendar.dean.chemistry.name"), tableForDeans, 2, boldFont);
                addCell(getUILocaleUtil().getCaption("pdf.academic.calendar.dean.math"), tableForDeans, 1, boldFont);
                addCell(getUILocaleUtil().getCaption("pdf.academic.calendar.dean.math.name"), tableForDeans, 2, boldFont);
                addCell(getUILocaleUtil().getCaption("pdf.academic.calendar.dean.or"), tableForDeans, 1, boldFont);
                addCell(getUILocaleUtil().getCaption("pdf.academic.calendar.dean.or.name"), tableForDeans, 2, boldFont);
                document.add(tableForDeans);
            }

            document.close();
            return new ByteArrayInputStream(baos.toByteArray());
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to download pdf file", ex);
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException ex) {
                    ex.printStackTrace();//TODO
                }
            }
        }
        return null;
    }

    private void addCell(String a, PdfPTable table, int colspan, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(a, font));
        cell.setColspan(colspan);
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setPaddingBottom(10);
        table.addCell(cell);
    }

    private void initContent() {
        if (layout != null) {
            layout.getLayout().removeAllComponents();
            getContent().removeComponent(layout.getLayout());
        }

        if (academicCalendar != null) {
            layout = new AllFacultyLayout(academicCalendar);
            try {
                layout.initLayout();
                getContent().addComponent(layout.getLayout());
                getContent().setComponentAlignment(layout.getLayout(), Alignment.MIDDLE_CENTER);
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to initialize academic calendar layout", ex);
            }
        }
    }

    private void create() throws Exception {
        ACADEMIC_CALENDAR_FACULTY acf = (ACADEMIC_CALENDAR_FACULTY) facultyCB.getValue();
        LOG.info(acf.toString());

        academicCalendar = new ACADEMIC_CALENDAR();
        academicCalendar.setYear((ENTRANCE_YEAR) studyYearCB.getValue());
        academicCalendar.setFaculty(acf);
        academicCalendar.setCreated(new Date());
        academicCalendar.setStatus(1);

        QueryModel<ACADEMIC_CALENDAR_ITEM> aciQM = new QueryModel<>(ACADEMIC_CALENDAR_ITEM.class);
        aciQM.addWhere("faculty", ECriteria.EQUAL, academicCalendar.getFaculty().getId());
        aciQM.addWhere("deleted", Boolean.FALSE);
        try {
            List<ACADEMIC_CALENDAR_ITEM> aciList = SessionFacadeFactory.getSessionFacade(
                    CommonEntityFacadeBean.class).lookup(aciQM);
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(academicCalendar);
            Calendar c = Calendar.getInstance();
            List<ACADEMIC_CALENDAR_DETAIL> newList = new ArrayList<ACADEMIC_CALENDAR_DETAIL>();
            for (ACADEMIC_CALENDAR_ITEM aci : aciList) {
                ACADEMIC_CALENDAR_DETAIL acd = new ACADEMIC_CALENDAR_DETAIL();
                acd.setAcademicCalendar(academicCalendar);
                acd.setAcademicCalendarItem(aci);
                if (aci.getItemType().equals("a")) {
                    c.clear();
                    c.set(Calendar.DATE, 13);
                    c.set(Calendar.MONTH, Calendar.JUNE);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 20);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("b")) {
                    c.clear();
                    c.set(Calendar.DATE, 8);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 20);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("c")) {
                    c.clear();
                    c.set(Calendar.DATE, 8);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 20);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("d")) {
                    c.clear();
                    c.set(Calendar.DATE, 7);
                    c.set(Calendar.MONTH, Calendar.NOVEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 26);
                    c.set(Calendar.MONTH, Calendar.NOVEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("e")) {
                    c.clear();
                    c.set(Calendar.DATE, 26);
                    c.set(Calendar.MONTH, Calendar.DECEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 7);
                    c.set(Calendar.MONTH, Calendar.JANUARY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("f")) {
                    c.clear();
                    c.set(Calendar.DATE, 30);
                    c.set(Calendar.MONTH, Calendar.JANUARY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 1);
                    c.set(Calendar.MONTH, Calendar.APRIL);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("h")) {
                    c.clear();
                    c.set(Calendar.DATE, 15);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 20);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("i")) {
                    c.clear();
                    c.set(Calendar.DATE, 22);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 3);
                    c.set(Calendar.MONTH, Calendar.DECEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("j")) {
                    c.clear();
                    c.set(Calendar.DATE, 9);
                    c.set(Calendar.MONTH, Calendar.JANUARY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 22);
                    c.set(Calendar.MONTH, Calendar.APRIL);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("k")) {
                    c.clear();
                    c.set(Calendar.DATE, 5);
                    c.set(Calendar.MONTH, Calendar.DECEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 24);
                    c.set(Calendar.MONTH, Calendar.DECEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("l")) {
                    c.clear();
                    c.set(Calendar.DATE, 24);
                    c.set(Calendar.MONTH, Calendar.APRIL);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 13);
                    c.set(Calendar.MONTH, Calendar.MAY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("m")) {
                    c.clear();
                    c.set(Calendar.DATE, 26);
                    c.set(Calendar.MONTH, Calendar.DECEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 8);
                    c.set(Calendar.MONTH, Calendar.JANUARY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("n")) {
                    c.clear();
                    c.set(Calendar.DATE, 5);
                    c.set(Calendar.MONTH, Calendar.JUNE);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 20);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("o")) {
                    c.clear();
                    c.set(Calendar.DATE, 5);
                    c.set(Calendar.MONTH, Calendar.JUNE);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 20);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("p")) {
                    c.clear();
                    c.set(Calendar.DATE, 15);
                    c.set(Calendar.MONTH, Calendar.MAY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 20);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());

                    acd.setDescr("(���. 4-� ��������� ���������������� ��������)");
                } else if (aci.getItemType().equals("q")) {
                    c.clear();
                    c.set(Calendar.DATE, 1);
                    c.set(Calendar.MONTH, Calendar.NOVEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 30);
                    c.set(Calendar.MONTH, Calendar.NOVEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("r")) {
                    c.clear();
                    c.set(Calendar.DATE, 1);
                    c.set(Calendar.MONTH, Calendar.APRIL);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 30);
                    c.set(Calendar.MONTH, Calendar.APRIL);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("s")) {
                    c.clear();
                    c.set(Calendar.DATE, 9);
                    c.set(Calendar.MONTH, Calendar.JANUARY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 4);
                    c.set(Calendar.MONTH, Calendar.FEBRUARY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("t")) {
                    c.clear();
                    c.set(Calendar.DATE, 15);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 10);
                    c.set(Calendar.MONTH, Calendar.SEPTEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("u")) {
                    c.clear();
                    c.set(Calendar.DATE, 6);
                    c.set(Calendar.MONTH, Calendar.FEBRUARY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 25);
                    c.set(Calendar.MONTH, Calendar.FEBRUARY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());

                    acd.setDescr("(�� ������ �������)");
                } else if (aci.getItemType().equals("v")) {
                    c.clear();
                    c.set(Calendar.DATE, 12);
                    c.set(Calendar.MONTH, Calendar.SEPTEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 17);
                    c.set(Calendar.MONTH, Calendar.SEPTEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("w")) {
                    c.clear();
                    c.set(Calendar.DATE, 27);
                    c.set(Calendar.MONTH, Calendar.FEBRUARY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 18);
                    c.set(Calendar.MONTH, Calendar.MARCH);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());

                    acd.setDescr("(�� ������ �������)");
                } else if (aci.getItemType().equals("x")) {
                    c.clear();
                    c.set(Calendar.DATE, 19);
                    c.set(Calendar.MONTH, Calendar.SEPTEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 1);
                    c.set(Calendar.MONTH, Calendar.OCTOBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("y")) {
                    c.clear();
                    c.set(Calendar.DATE, 27);
                    c.set(Calendar.MONTH, Calendar.MARCH);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 20);
                    c.set(Calendar.MONTH, Calendar.MAY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("z")) {
                    c.clear();
                    c.set(Calendar.DATE, 3);
                    c.set(Calendar.MONTH, Calendar.OCTOBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 26);
                    c.set(Calendar.MONTH, Calendar.NOVEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("0")) {
                    c.clear();
                    c.set(Calendar.DATE, 31);
                    c.set(Calendar.MONTH, Calendar.MARCH);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());
                } else if (aci.getItemType().equals("1")) {
                    c.clear();
                    c.set(Calendar.DATE, 15);
                    c.set(Calendar.MONTH, Calendar.MAY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 27);
                    c.set(Calendar.MONTH, Calendar.MAY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("2")) {
                    c.clear();
                    c.set(Calendar.DATE, 29);
                    c.set(Calendar.MONTH, Calendar.MAY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 3);
                    c.set(Calendar.MONTH, Calendar.JUNE);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("3")) {
                    c.clear();
                    c.set(Calendar.DATE, 15);
                    c.set(Calendar.MONTH, Calendar.MAY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 27);
                    c.set(Calendar.MONTH, Calendar.MAY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("4")) {
                    c.clear();
                    c.set(Calendar.DATE, 29);
                    c.set(Calendar.MONTH, Calendar.MAY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 3);
                    c.set(Calendar.MONTH, Calendar.JUNE);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("5")) {
                    c.clear();
                    c.set(Calendar.DATE, 15);
                    c.set(Calendar.MONTH, Calendar.MAY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 12);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("6")) {
                    c.clear();
                    c.set(Calendar.DATE, 28);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 9);
                    c.set(Calendar.MONTH, Calendar.SEPTEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("7")) {
                    c.clear();
                    c.set(Calendar.DATE, 1);
                    c.set(Calendar.MONTH, Calendar.MAY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 27);
                    c.set(Calendar.MONTH, Calendar.MAY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("8")) {
                    c.clear();
                    c.set(Calendar.DATE, 12);
                    c.set(Calendar.MONTH, Calendar.JUNE);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 8);
                    c.set(Calendar.MONTH, Calendar.JULY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("9")) {
                    c.clear();
                    c.set(Calendar.DATE, 10);
                    c.set(Calendar.MONTH, Calendar.JULY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 5);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("10")) {
                    c.clear();
                    c.set(Calendar.DATE, 12);
                    c.set(Calendar.MONTH, Calendar.JUNE);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 22);
                    c.set(Calendar.MONTH, Calendar.JULY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("11")) {
                    c.clear();
                    c.set(Calendar.DATE, 13);
                    c.set(Calendar.MONTH, Calendar.JUNE);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 20);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("12")) {
                    c.clear();
                    c.set(Calendar.DATE, 8);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 20);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("13")) {
                    c.clear();
                    c.set(Calendar.DATE, 8);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 20);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("14")) {
                    c.clear();
                    c.set(Calendar.DATE, 15);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 10);
                    c.set(Calendar.MONTH, Calendar.SEPTEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("15")) {
                    c.clear();
                    c.set(Calendar.DATE, 15);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 20);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("16")) {
                    c.clear();
                    c.set(Calendar.DATE, 22);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());
                } else if (aci.getItemType().equals("17")) {
                    c.clear();
                    c.set(Calendar.DATE, 22);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 26);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("18")) {
                    c.clear();
                    c.set(Calendar.DATE, 26);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());
                } else if (aci.getItemType().equals("19")) {
                    c.clear();
                    c.set(Calendar.DATE, 30);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());
                } else if (aci.getItemType().equals("20")) {
                    c.clear();
                    c.set(Calendar.DATE, 29);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 2);
                    c.set(Calendar.MONTH, Calendar.SEPTEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("21")) {
                    c.clear();
                    c.set(Calendar.DATE, 22);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 17);
                    c.set(Calendar.MONTH, Calendar.SEPTEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("22")) {
                    c.clear();
                    c.set(Calendar.DATE, 5);
                    c.set(Calendar.MONTH, Calendar.SEPTEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 17);
                    c.set(Calendar.MONTH, Calendar.SEPTEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("23")) {
                    c.clear();
                    c.set(Calendar.DATE, 12);
                    c.set(Calendar.MONTH, Calendar.SEPTEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 17);
                    c.set(Calendar.MONTH, Calendar.SEPTEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("24")) {
                    c.clear();
                    c.set(Calendar.DATE, 19);
                    c.set(Calendar.MONTH, Calendar.SEPTEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 1);
                    c.set(Calendar.MONTH, Calendar.OCTOBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("25")) {
                    c.clear();
                    c.set(Calendar.DATE, 3);
                    c.set(Calendar.MONTH, Calendar.OCTOBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 26);
                    c.set(Calendar.MONTH, Calendar.NOVEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("26")) {
                    c.clear();
                    c.set(Calendar.DATE, 1);
                    c.set(Calendar.MONTH, Calendar.NOVEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 30);
                    c.set(Calendar.MONTH, Calendar.NOVEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("27")) {
                    c.clear();
                    c.set(Calendar.DATE, 10);
                    c.set(Calendar.MONTH, Calendar.OCTOBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 15);
                    c.set(Calendar.MONTH, Calendar.OCTOBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("28")) {
                    c.clear();
                    c.set(Calendar.DATE, 23);
                    c.set(Calendar.MONTH, Calendar.OCTOBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    acd.setDescr("(�� 00:00 �.���.)");
                } else if (aci.getItemType().equals("29")) {
                    c.clear();
                    c.set(Calendar.DATE, 7);
                    c.set(Calendar.MONTH, Calendar.NOVEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 26);
                    c.set(Calendar.MONTH, Calendar.NOVEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("30")) {
                    c.clear();
                    c.set(Calendar.DATE, 28);
                    c.set(Calendar.MONTH, Calendar.NOVEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 3);
                    c.set(Calendar.MONTH, Calendar.DECEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("31")) {
                    c.clear();
                    c.set(Calendar.DATE, 4);
                    c.set(Calendar.MONTH, Calendar.DECEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    acd.setDescr("(�� 00:00 �.���.)");
                } else if (aci.getItemType().equals("32")) {
                    c.clear();
                    c.set(Calendar.DATE, 1);
                    c.set(Calendar.MONTH, Calendar.DECEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());
                } else if (aci.getItemType().equals("33")) {
                    c.clear();
                    c.set(Calendar.DATE, 3);
                    c.set(Calendar.MONTH, Calendar.DECEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());
                } else if (aci.getItemType().equals("34")) {
                    c.clear();
                    c.set(Calendar.DATE, 5);
                    c.set(Calendar.MONTH, Calendar.DECEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 24);
                    c.set(Calendar.MONTH, Calendar.DECEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("35")) {
                    c.clear();
                    c.set(Calendar.DATE, 31);
                    c.set(Calendar.MONTH, Calendar.DECEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    acd.setDescr("(�� 00:00 �.���.)");
                } else if (aci.getItemType().equals("36")) {
                    c.clear();
                    c.set(Calendar.DATE, 16);
                    c.set(Calendar.MONTH, Calendar.DECEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 17);
                    c.set(Calendar.MONTH, Calendar.DECEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("37")) {
                    c.clear();
                    c.set(Calendar.DATE, 26);
                    c.set(Calendar.MONTH, Calendar.DECEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 7);
                    c.set(Calendar.MONTH, Calendar.JANUARY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("38")) {
                    c.clear();
                    c.set(Calendar.DATE, 26);
                    c.set(Calendar.MONTH, Calendar.DECEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getBeginYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 8);
                    c.set(Calendar.MONTH, Calendar.JANUARY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("39")) {
                    c.clear();
                    c.set(Calendar.DATE, 9);
                    c.set(Calendar.MONTH, Calendar.JANUARY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());
                } else if (aci.getItemType().equals("40")) {
                    c.clear();
                    c.set(Calendar.DATE, 9);
                    c.set(Calendar.MONTH, Calendar.JANUARY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 13);
                    c.set(Calendar.MONTH, Calendar.JANUARY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 9);
                    c.set(Calendar.MONTH, Calendar.JANUARY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate3(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 16);
                    c.set(Calendar.MONTH, Calendar.JANUARY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate4(c.getTime());
                } else if (aci.getItemType().equals("41")) {
                    c.clear();
                    c.set(Calendar.DATE, 9);
                    c.set(Calendar.MONTH, Calendar.JANUARY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 13);
                    c.set(Calendar.MONTH, Calendar.JANUARY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("42")) {
                    c.clear();
                    c.set(Calendar.DATE, 13);
                    c.set(Calendar.MONTH, Calendar.JANUARY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());
                } else if (aci.getItemType().equals("43")) {
                    c.clear();
                    c.set(Calendar.DATE, 16);
                    c.set(Calendar.MONTH, Calendar.JANUARY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 20);
                    c.set(Calendar.MONTH, Calendar.JANUARY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("44")) {
                    c.clear();
                    c.set(Calendar.DATE, 9);
                    c.set(Calendar.MONTH, Calendar.JANUARY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 4);
                    c.set(Calendar.MONTH, Calendar.FEBRUARY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("45")) {
                    c.clear();
                    c.set(Calendar.DATE, 23);
                    c.set(Calendar.MONTH, Calendar.JANUARY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 4);
                    c.set(Calendar.MONTH, Calendar.FEBRUARY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("46")) {
                    c.clear();
                    c.set(Calendar.DATE, 9);
                    c.set(Calendar.MONTH, Calendar.JANUARY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 4);
                    c.set(Calendar.MONTH, Calendar.FEBRUARY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("47")) {
                    c.clear();
                    c.set(Calendar.DATE, 6);
                    c.set(Calendar.MONTH, Calendar.FEBRUARY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 25);
                    c.set(Calendar.MONTH, Calendar.FEBRUARY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());

                    acd.setDescr("(�� ������ �������)");
                } else if (aci.getItemType().equals("48")) {
                    c.clear();
                    c.set(Calendar.DATE, 30);
                    c.set(Calendar.MONTH, Calendar.JANUARY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 1);
                    c.set(Calendar.MONTH, Calendar.APRIL);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("49")) {
                    c.clear();
                    c.set(Calendar.DATE, 27);
                    c.set(Calendar.MONTH, Calendar.FEBRUARY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 4);
                    c.set(Calendar.MONTH, Calendar.MARCH);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("50")) {
                    c.clear();
                    c.set(Calendar.DATE, 12);
                    c.set(Calendar.MONTH, Calendar.MARCH);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    acd.setDescr("(�� 00:00 �.���.)");
                } else if (aci.getItemType().equals("51")) {
                    c.clear();
                    c.set(Calendar.DATE, 8);
                    c.set(Calendar.MONTH, Calendar.MARCH);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());
                } else if (aci.getItemType().equals("52")) {
                    c.clear();
                    c.set(Calendar.DATE, 27);
                    c.set(Calendar.MONTH, Calendar.FEBRUARY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 18);
                    c.set(Calendar.MONTH, Calendar.MARCH);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());

                    acd.setDescr("(�� ������ �������)");
                } else if (aci.getItemType().equals("53")) {
                    c.clear();
                    c.set(Calendar.DATE, 21);
                    c.set(Calendar.MONTH, Calendar.MARCH);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 23);
                    c.set(Calendar.MONTH, Calendar.MARCH);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("54")) {
                    c.clear();
                    c.set(Calendar.DATE, 27);
                    c.set(Calendar.MONTH, Calendar.MARCH);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 20);
                    c.set(Calendar.MONTH, Calendar.MAY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("55")) {
                    c.clear();
                    c.set(Calendar.DATE, 31);
                    c.set(Calendar.MONTH, Calendar.MARCH);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());
                } else if (aci.getItemType().equals("56")) {
                    c.clear();
                    c.set(Calendar.DATE, 17);
                    c.set(Calendar.MONTH, Calendar.APRIL);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 22);
                    c.set(Calendar.MONTH, Calendar.APRIL);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("57")) {
                    c.clear();
                    c.set(Calendar.DATE, 23);
                    c.set(Calendar.MONTH, Calendar.APRIL);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    acd.setDescr("(�� 00:00 �.���.)");
                } else if (aci.getItemType().equals("58")) {
                    c.clear();
                    c.set(Calendar.DATE, 22);
                    c.set(Calendar.MONTH, Calendar.APRIL);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());
                } else if (aci.getItemType().equals("59")) {
                    c.clear();
                    c.set(Calendar.DATE, 24);
                    c.set(Calendar.MONTH, Calendar.APRIL);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 13);
                    c.set(Calendar.MONTH, Calendar.MAY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("60")) {
                    c.clear();
                    c.set(Calendar.DATE, 1);
                    c.set(Calendar.MONTH, Calendar.MAY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());
                } else if (aci.getItemType().equals("61")) {
                    c.clear();
                    c.set(Calendar.DATE, 7);
                    c.set(Calendar.MONTH, Calendar.MAY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());
                } else if (aci.getItemType().equals("62")) {
                    c.clear();
                    c.set(Calendar.DATE, 9);
                    c.set(Calendar.MONTH, Calendar.MAY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());
                } else if (aci.getItemType().equals("63")) {
                    c.clear();
                    c.set(Calendar.DATE, 21);
                    c.set(Calendar.MONTH, Calendar.MAY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    acd.setDescr("(�� 00:00 �.���.)");
                } else if (aci.getItemType().equals("64")) {
                    c.clear();
                    c.set(Calendar.DATE, 22);
                    c.set(Calendar.MONTH, Calendar.MAY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 27);
                    c.set(Calendar.MONTH, Calendar.MAY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 29);
                    c.set(Calendar.MONTH, Calendar.MAY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate3(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 31);
                    c.set(Calendar.MONTH, Calendar.MAY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate4(c.getTime());
                } else if (aci.getItemType().equals("65")) {
                    c.clear();
                    c.set(Calendar.DATE, 15);
                    c.set(Calendar.MONTH, Calendar.MAY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 27);
                    c.set(Calendar.MONTH, Calendar.MAY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("66")) {
                    c.clear();
                    c.set(Calendar.DATE, 29);
                    c.set(Calendar.MONTH, Calendar.MAY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 3);
                    c.set(Calendar.MONTH, Calendar.JUNE);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("67")) {
                    c.clear();
                    c.set(Calendar.DATE, 15);
                    c.set(Calendar.MONTH, Calendar.MAY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 27);
                    c.set(Calendar.MONTH, Calendar.MAY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("68")) {
                    c.clear();
                    c.set(Calendar.DATE, 29);
                    c.set(Calendar.MONTH, Calendar.MAY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 3);
                    c.set(Calendar.MONTH, Calendar.JUNE);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("69")) {
                    c.clear();
                    c.set(Calendar.DATE, 15);
                    c.set(Calendar.MONTH, Calendar.MAY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 12);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("70")) {
                    c.clear();
                    c.set(Calendar.DATE, 28);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 9);
                    c.set(Calendar.MONTH, Calendar.SEPTEMBER);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("71")) {
                    c.clear();
                    c.set(Calendar.DATE, 1);
                    c.set(Calendar.MONTH, Calendar.MAY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 27);
                    c.set(Calendar.MONTH, Calendar.MAY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("72")) {
                    c.clear();
                    c.set(Calendar.DATE, 12);
                    c.set(Calendar.MONTH, Calendar.JUNE);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 8);
                    c.set(Calendar.MONTH, Calendar.JULY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("73")) {
                    c.clear();
                    c.set(Calendar.DATE, 10);
                    c.set(Calendar.MONTH, Calendar.JULY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 5);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("74")) {
                    c.clear();
                    c.set(Calendar.DATE, 6);
                    c.set(Calendar.MONTH, Calendar.JULY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());
                } else if (aci.getItemType().equals("75")) {
                    c.clear();
                    c.set(Calendar.DATE, 5);
                    c.set(Calendar.MONTH, Calendar.JUNE);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 20);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("76")) {
                    c.clear();
                    c.set(Calendar.DATE, 5);
                    c.set(Calendar.MONTH, Calendar.JUNE);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 20);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());
                } else if (aci.getItemType().equals("77")) {
                    c.clear();
                    c.set(Calendar.DATE, 15);
                    c.set(Calendar.MONTH, Calendar.MAY);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate1(c.getTime());

                    c.clear();
                    c.set(Calendar.DATE, 20);
                    c.set(Calendar.MONTH, Calendar.AUGUST);
                    c.set(Calendar.YEAR, academicCalendar.getYear().getEndYear());
                    acd.setDate2(c.getTime());

                    acd.setDescr("(���. 4-� ��������� ���������������� ��������)");
                }

                newList.add(acd);
            }

            if (!newList.isEmpty()) {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(newList);
                refresh();
            }
        } catch (Exception ex) {
            LOG.error("Unable to create a new academic calendar: ", ex);
            throw new Exception("Unable to create a new academic calendar");
        }
    }

    private void save() throws Exception {
        layout.save();
    }

    private void confirm() throws Exception {
        academicCalendar.setStatus(2);
        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(academicCalendar);
        confirmButton.setEnabled(false);
    }

    private void approve() throws Exception {
        academicCalendar.setStatus(3);
        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(academicCalendar);
        saveButton.setEnabled(false);
        confirmButton.setEnabled(false);
        approveButton.setEnabled(false);
    }

    private void refresh() {
        if (academicCalendar == null) {
            showErrorLabel(true);
            createButton.setVisible(true);
            saveButton.setVisible(false);
            confirmButton.setVisible(false);
            approveButton.setVisible(false);
            printButton.setVisible(false);
        } else {
            showErrorLabel(false);
            createButton.setVisible(false);
            saveButton.setVisible(true);
            saveButton.setEnabled(academicCalendar.getStatus() != 3);
            confirmButton.setVisible(true);
            confirmButton.setEnabled(academicCalendar.getStatus() == 1);
            approveButton.setVisible(true);
            approveButton.setEnabled(academicCalendar.getStatus() != 3);
            printButton.setVisible(true);
        }

        initContent();
    }

    @SuppressWarnings("unused")
    private String getFilename() {
        Calendar c = Calendar.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append("/tmp/academic_calendar_");
        sb.append(c.get(Calendar.YEAR));
        sb.append(c.get(Calendar.MONTH));
        sb.append(c.get(Calendar.DAY_OF_MONTH));
        sb.append(c.get(Calendar.HOUR_OF_DAY));
        sb.append(c.get(Calendar.MINUTE));
        sb.append(c.get(Calendar.SECOND));
        sb.append(c.get(Calendar.MILLISECOND));
        sb.append(".pdf");

        return sb.toString();
    }

    protected boolean canDownload() {
        return academicCalendar != null;
    }

    private String getAcademicCalendarStatus(int status) {
        if (status == 1) {
            return getUILocaleUtil().getCaption("creation.status");
        } else if (status == 2) {
            return getUILocaleUtil().getCaption("conforming.status");
        } else {
            return getUILocaleUtil().getCaption("approved.status");
        }
    }

    public void showErrorLabel(boolean show) {
        errorLabel.setVisible(show);
    }

    private class FacultyChangeListener implements ValueChangeListener {

        @Override
        public void valueChange(ValueChangeEvent ev) {
            ACADEMIC_CALENDAR_FACULTY acf = (ACADEMIC_CALENDAR_FACULTY) ev.getProperty().getValue();
            ENTRANCE_YEAR sy = (ENTRANCE_YEAR) studyYearCB.getValue();
            setStatusLabel(acf, sy);
        }
    }

    private class StudyYearChangeListener implements ValueChangeListener {

        @Override
        public void valueChange(ValueChangeEvent ev) {
            ENTRANCE_YEAR sy = (ENTRANCE_YEAR) ev.getProperty().getValue();
            ACADEMIC_CALENDAR_FACULTY acf = (ACADEMIC_CALENDAR_FACULTY) facultyCB.getValue();
            setStatusLabel(acf, sy);
        }
    }

    private void setStatusLabel(ACADEMIC_CALENDAR_FACULTY acf, ENTRANCE_YEAR sy) {
        if (acf != null && sy != null) {
            QueryModel<ACADEMIC_CALENDAR> qm = new QueryModel<ACADEMIC_CALENDAR>(ACADEMIC_CALENDAR.class);
            qm.addWhere("faculty", ECriteria.EQUAL, acf.getId());
            qm.addWhereAnd("year", ECriteria.EQUAL, sy.getId());
            try {
                academicCalendar = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                        lookupSingle(qm);
                academicCalendar = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                        lookup(ACADEMIC_CALENDAR.class, academicCalendar.getId());
                statusLabel.setValue(getUILocaleUtil().getCaption("status") + ": "
                        + getAcademicCalendarStatus(academicCalendar.getStatus()));
                refresh();
            } catch (Exception ex) {
                LOG.error("Academic calendar not created: ", ex);
                academicCalendar = null;
                refresh();
            }
        }
    }

    @SuppressWarnings("unused")
    private class AcademicCalendarDownloader extends FileDownloader {

        public AcademicCalendarDownloader(StreamResource resource) {
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
}
