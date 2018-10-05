package kz.halyqsoft.univercity.modules.practice;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.server.FileDownloader;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ENTRANCE_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ORGANIZATION;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDY_YEAR;
import kz.halyqsoft.univercity.filter.FStudentPracticeFilter;
import kz.halyqsoft.univercity.filter.panel.InformationPracticeFilterPanel;
import kz.halyqsoft.univercity.filter.FInformationPracticeFilter;
import kz.halyqsoft.univercity.filter.panel.StudentPracticeFilterPanel;
import kz.halyqsoft.univercity.modules.reports.MenuColumn;
import kz.halyqsoft.univercity.modules.workflow.MyItem;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.EmployeePdfCreator;
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
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import javax.persistence.NoResultException;
import javax.persistence.criteria.From;
import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.List;

import static kz.halyqsoft.univercity.utils.CommonUtils.getUILocaleUtil;

public class PracticeView extends AbstractTaskView implements FilterPanelListener, EntityListener{
    private VerticalLayout mainVL;

    private InformationPracticeFilterPanel informationPracticeFP;
    private GridWidget informationPracticeGW;
    private DBGridModel informationPracticeGM;

    private StudentPracticeFilterPanel studentPracticeFP;
    private GridWidget studentPracticeGW;
    private DBGridModel studentPracticeGM;

    private HorizontalSplitPanel mainHSP;
    private static String FIRST_ROW = getUILocaleUtil().getEntityLabel(PRACTICE_INFORMATION.class);
    private static String SECOND_ROW = getUILocaleUtil().getEntityLabel(PRACTICE_STUDENT.class);
    private String REPORT = getUILocaleUtil().getCaption("report");
    private Button reportBtn;
    private Button downloadTableBtn;
    FileDownloader fileDownloaderr = null;
    List<Entity> practiceInformations;

    public PracticeView(AbstractTask task) throws Exception {
        super(task);

        mainVL = new VerticalLayout();
        mainVL.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        mainHSP = new HorizontalSplitPanel();
        mainHSP.setImmediate(true);
        mainHSP.setSizeFull();
        mainHSP.setSplitPosition(15);

    }

    @Override
    public void initView(boolean b) throws Exception {

        HierarchicalContainer optionHC = new HierarchicalContainer();
        ArrayList<MyItem> myItems = new ArrayList<>();
        myItems.add(new MyItem(optionHC.addItem(FIRST_ROW) ,FIRST_ROW, null));
        myItems.add(new MyItem(optionHC.addItem(SECOND_ROW) ,SECOND_ROW, null));

        TreeTable sideMenu = new TreeTable();
        sideMenu.setContainerDataSource(optionHC);
        sideMenu.setColumnReorderingAllowed(false);
        sideMenu.setMultiSelect(false);
        sideMenu.setNullSelectionAllowed(false);
        sideMenu.setSizeFull();
        sideMenu.setImmediate(true);
        sideMenu.setSelectable(true);

        sideMenu.setChildrenAllowed(FIRST_ROW, false);
        sideMenu.setChildrenAllowed(SECOND_ROW, false);
        MenuColumn firstColumn = new MenuColumn();
        sideMenu.addGeneratedColumn("first" , firstColumn);
        sideMenu.setColumnHeader("first" , getUILocaleUtil().getCaption("menu"));
        sideMenu.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                mainVL.removeAllComponents();

                if(valueChangeEvent.getProperty().getValue().toString().equals(FIRST_ROW)){
                    mainVL.addComponent(informationPracticeFP);
                    mainVL.addComponent(informationPracticeGW);
                    doFilter(informationPracticeFP.getFilterBean());
                }else if(valueChangeEvent.getProperty().getValue().toString().equals(SECOND_ROW)){
                    mainVL.addComponent(studentPracticeFP);
                    mainVL.addComponent(studentPracticeGW);
                    doFilter(studentPracticeFP.getFilterBean());
                }
            }
        });
        initGridWidget();
        initFilter();

        mainHSP.setFirstComponent(sideMenu);
        mainHSP.setSecondComponent(mainVL);
        getContent().addComponent(mainHSP);
    }

    private void initGridWidget(){
        reportBtn = new Button(this.REPORT);
        reportBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if(informationPracticeGW.getSelectedEntities()!=null && informationPracticeGW.getSelectedEntities().size()> 0){
                    ReportDialog reportDialog = new ReportDialog(informationPracticeGW.getSelectedEntities());
                }else{
                    Message.showError(getUILocaleUtil().getCaption("chooseARecord"));
                }
            }
        });
        downloadTableBtn = new Button(getUILocaleUtil().getCaption("downloadTable"));
        downloadTableBtn.setEnabled(true);
        downloadTableBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                try {
                    if(informationPracticeGW.getSelectedEntities()!=null && informationPracticeGW.getSelectedEntities().size()>0){
                        createTable(informationPracticeGW.getSelectedEntities());
                    }else{
                        Message.showError(getUILocaleUtil().getCaption("chooseARecord"));
                    }

                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        informationPracticeGW = new GridWidget(PRACTICE_INFORMATION.class);
        informationPracticeGW.setImmediate(true);
        informationPracticeGW.setSizeFull();
        informationPracticeGW.removeEntityListener(informationPracticeGW);
        informationPracticeGW.showToolbar(true);
        informationPracticeGW.addEntityListener(this);
        informationPracticeGW.setMultiSelect(true);
        informationPracticeGW.setButtonVisible(IconToolbar.REFRESH_BUTTON, false);

        informationPracticeGW.getToolbarPanel().addComponent(reportBtn);
        informationPracticeGW.getToolbarPanel().addComponent(downloadTableBtn);

        informationPracticeGM = (DBGridModel)informationPracticeGW.getWidgetModel();
        informationPracticeGM.setRefreshType(ERefreshType.MANUAL);
        informationPracticeGM.setMultiSelect(true);
        informationPracticeGM.setRowNumberVisible(true);
        informationPracticeGM.setRowNumberWidth(50);
        FKFieldModel groupsFM = (FKFieldModel) informationPracticeGM.getFormModel().getFieldModel("groups");
        groupsFM.getQueryModel().addWhere("deleted" , ECriteria.EQUAL,false);

        FKFieldModel employeeFM = (FKFieldModel) informationPracticeGM.getFormModel().getFieldModel("employee");
        employeeFM.getQueryModel().addJoin(EJoin.INNER_JOIN , "id", EMPLOYEE.class ,"id");
        employeeFM.getQueryModel().addWhere("deleted" , ECriteria.EQUAL, false);


        studentPracticeGW = new GridWidget(PRACTICE_STUDENT.class);
        studentPracticeGW.setImmediate(true);
        studentPracticeGW.setSizeFull();
        studentPracticeGW.removeEntityListener(studentPracticeGW);
        studentPracticeGW.setMultiSelect(true);
        studentPracticeGW.addEntityListener(this);
        studentPracticeGW.setButtonVisible(IconToolbar.REFRESH_BUTTON, false);

        studentPracticeGM = (DBGridModel) studentPracticeGW.getWidgetModel();
        studentPracticeGM.setRefreshType(ERefreshType.MANUAL);
        studentPracticeGM.setMultiSelect(true);
        studentPracticeGM.setRowNumberVisible(true);
        studentPracticeGM.setRowNumberWidth(50);

        FKFieldModel studentFM = (FKFieldModel) studentPracticeGM.getFormModel().getFieldModel("student");
        FromItem fi1 = studentFM.getQueryModel().addJoin(EJoin.INNER_JOIN , "id", STUDENT.class ,"id");
        FromItem fi2 = fi1.addJoin( EJoin.INNER_JOIN,"id", STUDENT_EDUCATION.class, "student_id");
        FromItem fi3 = fi2.addJoin(EJoin.INNER_JOIN,"groups_id", GROUPS.class, "id");
        FromItem fi4 = fi3.addJoin(EJoin.INNER_JOIN, "id", PRACTICE_INFORMATION.class,"groups_id");

        studentFM.getQueryModel().addWhere("deleted" , ECriteria.EQUAL, false);
        studentFM.getQueryModel().addWhereAnd(fi3, "deleted" , ECriteria.EQUAL ,false);
        studentFM.getQueryModel().addWhereNullAnd(fi2, "child");
    }

    private void initFilter() throws Exception{

        informationPracticeFP = new InformationPracticeFilterPanel(new FInformationPracticeFilter());
        informationPracticeFP.addFilterPanelListener(this);
        informationPracticeFP.setImmediate(true);

        ComboBox groupsCB = new ComboBox();
        groupsCB.setNullSelectionAllowed(true);
        groupsCB.setTextInputAllowed(true);
        groupsCB.setFilteringMode(FilteringMode.CONTAINS);
        groupsCB.setWidth(200, Unit.PIXELS);
        QueryModel<GROUPS> groupsQM = new QueryModel<>(GROUPS.class);
        groupsQM.addWhere("deleted" , ECriteria.EQUAL,false);
        BeanItemContainer<GROUPS> groupsBIC = new BeanItemContainer<>(GROUPS.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(groupsQM));
        groupsCB.setContainerDataSource(groupsBIC);
        informationPracticeFP.addFilterComponent("groups", groupsCB);

        ComboBox employeeCB = new ComboBox();
        employeeCB.setNullSelectionAllowed(true);
        employeeCB.setTextInputAllowed(true);
        employeeCB.setFilteringMode(FilteringMode.CONTAINS);
        employeeCB.setWidth(200, Unit.PIXELS);
        QueryModel<USERS> employeeQM = new QueryModel<>(USERS.class);
        employeeQM.addJoin(EJoin.INNER_JOIN, "id", EMPLOYEE.class , "id");
        employeeQM.addWhere("deleted" , ECriteria.EQUAL, false);

        BeanItemContainer<USERS> employeeBIC = new BeanItemContainer<>(USERS.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(employeeQM));
        employeeCB.setContainerDataSource(employeeBIC);
        informationPracticeFP.addFilterComponent("employee", employeeCB);

        ComboBox studyYearCB = new ComboBox();
        studyYearCB.setNullSelectionAllowed(true);
        studyYearCB.setTextInputAllowed(true);
        studyYearCB.setFilteringMode(FilteringMode.CONTAINS);
        studyYearCB.setWidth(200, Unit.PIXELS);
        QueryModel<STUDY_YEAR> studyYearQM = new QueryModel<>(STUDY_YEAR.class);

        BeanItemContainer<STUDY_YEAR> studyYearBIC = new BeanItemContainer<>(STUDY_YEAR.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(studyYearQM));
        studyYearCB.setContainerDataSource(studyYearBIC);
        informationPracticeFP.addFilterComponent("studyYear", studyYearCB);

        ComboBox entranceYearCB = new ComboBox();
        entranceYearCB.setNullSelectionAllowed(true);
        entranceYearCB.setTextInputAllowed(true);
        entranceYearCB.setFilteringMode(FilteringMode.CONTAINS);
        entranceYearCB.setWidth(200, Unit.PIXELS);
        QueryModel<ENTRANCE_YEAR> entranceYearQM = new QueryModel<>(ENTRANCE_YEAR.class);

        BeanItemContainer<ENTRANCE_YEAR> entranceYearBIC = new BeanItemContainer<>(ENTRANCE_YEAR.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(entranceYearQM));
        entranceYearCB.setContainerDataSource(entranceYearBIC);
        informationPracticeFP.addFilterComponent("entranceYear", entranceYearCB);

        DateField createdDF = new DateField();
        informationPracticeFP.addFilterComponent("created", createdDF);


        studentPracticeFP = new StudentPracticeFilterPanel(new FStudentPracticeFilter());
        studentPracticeFP.addFilterPanelListener(this);
        studentPracticeFP.setImmediate(true);

        ComboBox studentCB = new ComboBox();
        studentCB.setNullSelectionAllowed(true);
        studentCB.setTextInputAllowed(true);
        studentCB.setFilteringMode(FilteringMode.CONTAINS);
        studentCB.setWidth(200, Unit.PIXELS);
        QueryModel<USERS> studentQM = new QueryModel<>(USERS.class);
        studentQM.addJoin(EJoin.INNER_JOIN, "id", STUDENT.class , "id");
        studentQM.addWhere("deleted" , ECriteria.EQUAL, false);

        BeanItemContainer<USERS> studentBIC = new BeanItemContainer<>(USERS.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(studentQM));
        studentCB.setContainerDataSource(studentBIC);
        studentPracticeFP.addFilterComponent("student", studentCB);

        ComboBox organizationCB = new ComboBox();
        organizationCB.setNullSelectionAllowed(true);
        organizationCB.setTextInputAllowed(true);
        organizationCB.setFilteringMode(FilteringMode.CONTAINS);
        organizationCB.setWidth(200,Unit.PIXELS);
        QueryModel<ORGANIZATION> organizationQM = new QueryModel<>(ORGANIZATION.class);

        BeanItemContainer<ORGANIZATION> organizationBIC = new BeanItemContainer<ORGANIZATION>(ORGANIZATION.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(organizationQM));
        organizationCB.setContainerDataSource(organizationBIC);
        studentPracticeFP.addFilterComponent("organization", organizationCB);

        DateField comeInDF = new DateField();
        studentPracticeFP.addFilterComponent("comeInDate", comeInDF);

        DateField comeOutDF = new DateField();
        studentPracticeFP.addFilterComponent("comeOutDate", comeOutDF);
    }

    public void createTable(List entities) throws Exception {

        List<PRACTICE_INFORMATION> practiceInformations = (List<PRACTICE_INFORMATION>) entities;

        Document document = new Document();

        QueryModel<PRACTICE_STUDENT> psQM = new QueryModel<>(PRACTICE_STUDENT.class);
        FromItem oItem = psQM.addJoin(EJoin.INNER_JOIN,"organization",ORGANIZATION.class,"id");
        psQM.addWhere("student",ECriteria.EQUAL,CommonUtils.getCurrentUser().getId());
        ByteArrayOutputStream byteArr = new ByteArrayOutputStream();

        List<PRACTICE_STUDENT> practiceStudents = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(psQM);

        try {
            for (PRACTICE_INFORMATION pi : practiceInformations) {


                String sql2 = "  SELECT c.credit,sd.year_id  FROM subject s\n" +
                        "    INNER JOIN creditability c on s.creditability_id=c.id\n" +
                        "    INNER JOIN student_subject ss on s.id=ss.subject_id\n" +
                        "    INNER JOIN student_education se ON ss.student_id = se.id\n" +
                        "    INNER JOIN practice_student ps on  s.id = ps.student_id\n" +
                        "    INNER JOIN semester_data sd ON ss.semester_data_id = sd.id\n" +
                        "    INNER JOIN practice_information pi on  pi.groups_id=se.groups_id\n" +
                        "  WHERE s.practice_type_id!=null and pi.groups_id= "+pi.getGroups().getId();



                QueryModel<PRACTICE_STUDENT> practiceStudentQM = new QueryModel<>(PRACTICE_STUDENT.class);
                FromItem orgItem = practiceStudentQM.addJoin(EJoin.INNER_JOIN, "organization", ORGANIZATION.class, "id");
                FromItem sItem = practiceStudentQM.addJoin(EJoin.INNER_JOIN, "student", STUDENT.class, "id");
                FromItem seItem = sItem.addJoin(EJoin.INNER_JOIN, "id", STUDENT_EDUCATION.class, "student");
                FromItem piItem = seItem.addJoin(EJoin.INNER_JOIN, "groups", PRACTICE_INFORMATION.class, "groups");
                practiceStudentQM.addWhere(seItem, "groups", ECriteria.EQUAL, pi.getGroups().getId());

                List<PRACTICE_STUDENT> practiceStudents1 = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(practiceStudentQM);

                String sql = "  SELECT c.credit,sd.year_id  FROM subject s\n" +
                        "    INNER JOIN creditability c on s.creditability_id=c.id\n" +
                        "    INNER JOIN student_subject ss on s.id=ss.subject_id\n" +
                        "    INNER JOIN student_education se ON ss.student_id = se.id\n" +
                        "    INNER JOIN practice_student ps on  s.id = ps.student_id\n" +
                        "    INNER JOIN semester_data sd ON ss.semester_data_id = sd.id\n" +
                        "    INNER JOIN practice_information pi on  pi.groups_id=se.groups_id\n" +
                        "  WHERE s.practice_type_id!=null and pi.groups_id= "+pi.getGroups().getId();

                try {
                    for (PRACTICE_STUDENT ps : practiceStudents1) {

                        PdfWriter.getInstance(document, byteArr);
                        document.open();
                        Paragraph title = new Paragraph("ЮЖНО-КАЗАХСТАНСКИЙ ПЕДАГОГИЧЕСКИЙ УНИВЕРСИТЕТ", EmployeePdfCreator.getFont(15, Font.BOLD));
                        title.setAlignment(Element.ALIGN_CENTER);
                        Paragraph title1 = new Paragraph("ВЕДОМОСТЬ № 290\n" +
                                "Іс тәжірибе туралы/ по практике", EmployeePdfCreator.getFont(15, Font.BOLD));
                        title1.setAlignment(Element.ALIGN_CENTER);

                        Paragraph content = new Paragraph("Факультет: " + pi.getGroups().getSpeciality().getSpecName() + //pi.getSpeciality().getSpecName() +
                                "\nТобы/группа: " + pi.getGroups().getName() +
                                "\nБаза аты/Вид базы:" + ps.getOrganization().getOrganizationName() +
                                "      База аты/Наименование базы:" + ps.getOrganization().getAddress() +
                                "\nСтуденттің іс тәжірибесінің мерзімі/" +
                                "Срок практики студентов " + ps.getComeInDate() + "     " + ps.getComeOutDate() +
                                "\n Кафедраның іс тәжірибе жетекшісі /" +
                                "Руководитель практики от кафедры " + pi.getEmployee().getLastName() + " " +
                                pi.getEmployee().getFirstName().toUpperCase().charAt(0) + "." +
                                (pi.getEmployee().getMiddleName() != null ?
                                        pi.getEmployee().getMiddleName().toUpperCase().charAt(0) : "") + " " +
                                "\nІс тәжірибе түрі/Наименование практики: Производственная(педагогическая) практика " +
                                "\nКредиттер саны/Количество кредитов ", EmployeePdfCreator.getFont(15, Font.NORMAL));

                        document.add(title);
                        document.add(title1);
                        document.add(content);

                        PdfPTable table = new PdfPTable(8);

                        insertCell(table, "№:", Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(12, Font.BOLD));

                        insertCell(table, "Тегі, аты жөні / Фамилия, имя, отчество", Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(12, Font.BOLD));

                        insertCell(table, "Өндірістік бөлім/Производственная часть", Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(12, Font.BOLD));

                        insertCell(table, "Есептік бөлім/Отчетная часть", Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(12, Font.BOLD));

                        insertCell(table, "Қорытынды балы/Итоговый баллы", Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(12, Font.BOLD));

                        insertCell(table, "Әріптік жүйедегі бағасы/ Оценка по буквенной системе", Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(12, Font.BOLD));

                        insertCell(table, "Дәстүрлі бағасы/Тадиционная оценка", Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(12, Font.BOLD));

                        insertCell(table, "Оқытушының қолы/Подпись преподавателя", Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(12, Font.BOLD));

                        table.setWidthPercentage(100);

                        Font font = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD, BaseColor.WHITE);

                        String sql1 = "SELECT   ROW_NUMBER() OVER(ORDER BY vs.id ASC) AS Row,trim(vs.LAST_NAME || ' ' || vs.FIRST_NAME || ' ' || coalesce(vs.MIDDLE_NAME, '')) FIO,\n" +
                                "  vs.group_name,\n" +
                                "ser.total_rk,ser.final,((ser.total_rk)*0.6+(ser.final)*0.4) final,\n" +
                                "  CASE WHEN (final=100) THEN  'A'\n" +
                                "    WHEN (final BETWEEN 90 AND 94) THEN  'A-'\n" +
                                "  WHEN (final BETWEEN 85 AND 89) THEN  'B+'\n" +
                                "  WHEN (final BETWEEN 80 AND 84) THEN  'B'\n" +
                                "  WHEN (final BETWEEN 75 AND 79) THEN  'B-'\n" +
                                "  WHEN (final BETWEEN 70 AND 74) THEN  'C+'\n" +
                                "  WHEN (final BETWEEN 65 AND 69) THEN  'C'\n" +
                                "  WHEN (final BETWEEN 60 AND 64) THEN  'C-'\n" +
                                "  WHEN (final BETWEEN 55 AND 59) THEN  'D+'\n" +
                                "  WHEN (final BETWEEN 50 AND 54) THEN  'D'\n" +
                                "    ELSE 'F' END letter,\n" +
                                "  CASE  WHEN (final BETWEEN 90 AND 100) THEN  '5'\n" +
                                "  WHEN (final BETWEEN 75 AND 89) THEN  '4'\n" +
                                "  WHEN (final BETWEEN 50 AND 74) THEN  '3'\n" +
                                "  ELSE '2' END point\n" +
                                "FROM  v_student vs\n" +
                                "INNER JOIN student_edu_rate ser on vs.id=ser.student_id\n" +
                                "WHERE vs.groups_id=" + pi.getGroups().getId();
//
                        Map<Integer, Object> params = new HashMap<>();
                        try {
                            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql1, params);
                            if (!tmpList.isEmpty()) {
                                for (Object o : tmpList) {
                                    Object[] oo = (Object[]) o;

                                    for (int i = 0; i < 8; i++) {
                                        insertCell(table, oo[i] != null ? oo[i] instanceof String ? (String) oo[i] : String.valueOf(oo[i]) : "", Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(12, Font.NORMAL));
                                    }
                                }
                            }
                        } catch (Exception ex) {
                            CommonUtils.showMessageAndWriteLog("Unable to load absents list", ex);
                        }
                        document.add(table);
                    }
                } catch (Exception e) {
                    CommonUtils.showMessageAndWriteLog("Unable to load absents list", e);
                }

            }

            Paragraph content2 = new Paragraph("Кафедраның іс тәжірибе жетекшісі/\n" +
                    "Руководитель практики от кафедры____________\n " +
                    "Іс тәжірибе бойынша униыерситет аға әдіскері/\n" +
                    "Ст.методист университета по практике____________\n" +
                    "Факультет деканы/\n" +
                    "Декан факультета____________", EmployeePdfCreator.getFont(15, Font.NORMAL));

            //   }
            document.add(content2);

        }catch (Exception e1) {
            e1.printStackTrace();
        }




        document.close();

        if(fileDownloaderr==null){
            try{
                fileDownloaderr = new FileDownloader(EmployeePdfCreator.getStreamResourceFromByte(byteArr.toByteArray(), REPORT +".pdf"));
            }catch (Exception e){
                e.printStackTrace();
            }
            if(fileDownloaderr!=null){
            }
        }else{
            fileDownloaderr.setFileDownloadResource(EmployeePdfCreator.getStreamResourceFromByte(byteArr.toByteArray() , REPORT +".pdf"));
        }
        fileDownloaderr.extend(downloadTableBtn);


    }


    private void insertCell(PdfPTable table, String text, int align, int colspan, Font font){

        //create a new cell with the specified Text and Font
        PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
        //set the cell alignment
        cell.setHorizontalAlignment(align);
        //set the cell column span in case you want to merge two or more cells
        cell.setColspan(colspan);
        //in case there is no text and you wan to create an empty row
        if(text.trim().equalsIgnoreCase("")){
            cell.setMinimumHeight(2f);
        }
        //add the call to the table
        table.addCell(cell);

    }


    @Override
    public void doFilter(AbstractFilterBean abstractFilterBean) {
        if(abstractFilterBean instanceof FInformationPracticeFilter){
            FInformationPracticeFilter informationPracticeFilter = (FInformationPracticeFilter) abstractFilterBean;
            Map<Integer, Object> params = new HashMap<>();
            int i = 1;
            StringBuilder sb = new StringBuilder();

            if (informationPracticeFilter.getEmployee() != null) {

                if(i!=1){
                    sb.append(" and ");
                }
                params.put(i, informationPracticeFilter.getEmployee().getId().getId());
                sb.append("pi.teacher_id = ?" + i++);

            }

            if (informationPracticeFilter.getGroups()!=null) {

                if(i!=1){
                    sb.append(" and ");
                }
                params.put(i, informationPracticeFilter.getGroups().getId().getId());
                sb.append("g.id = ?" + i++);

            }

            if (informationPracticeFilter.getStudyYear() != null) {

                if(i!=1){
                    sb.append(" and ");
                }
                params.put(i, informationPracticeFilter.getStudyYear().getId().getId());
                sb.append(" g.study_year_id = ?" + i++);

            }

            if (informationPracticeFilter.getCreated() != null) {

                if(i!=1){
                    sb.append(" and ");
                }
                i++;
                sb.append(" date_trunc ('day' ,  pi.created ) = date_trunc('day' , TIMESTAMP  '"+ CommonUtils.getFormattedDate(informationPracticeFilter.getCreated())+ "') ");

            }

            if (informationPracticeFilter.getEntranceYear() != null) {

                if(i!=1){
                    sb.append(" and ");
                }
                i++;
                sb.append(" pi.entrance_year_id = " + informationPracticeFilter.getEntranceYear().getId().getId().longValue()+" ");

            }

            List<PRACTICE_INFORMATION> list = new ArrayList<>();

            if(i > 1){
                sb.insert(0, " WHERE  ");
            }
            String sql = "select pi.* from practice_information pi\n" +
                    "  INNER JOIN groups g\n" +
                    "    ON pi.groups_id = g.id\n"
                    + sb.toString();
            try {

                List tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
                if (!tmpList.isEmpty()) {
                    for (Object o : tmpList) {
                        Object[] oo = (Object[]) o;

                        PRACTICE_INFORMATION practiceInformation = new PRACTICE_INFORMATION();
                        practiceInformation.setId(ID.valueOf((Long)oo[0]));

                        try{
                            practiceInformation.setGroups(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(GROUPS.class , ID.valueOf((Long)oo[1])));
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        try{
                            practiceInformation.setEmployee(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USERS.class , ID.valueOf((Long)oo[2])));
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        practiceInformation.setCreated((Date)oo[3]);

                        try{
                            practiceInformation.setEntranceYear(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(ENTRANCE_YEAR.class , ID.valueOf((Long)oo[4])));
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        list.add(practiceInformation);
                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load accounts list", ex);
            }
            refresh(list);
        }else if(abstractFilterBean instanceof FStudentPracticeFilter){
            FStudentPracticeFilter fStudentPracticeFilter = (FStudentPracticeFilter) abstractFilterBean;
            Map<Integer, Object> params = new HashMap<>();
            int i = 1;
            StringBuilder sb = new StringBuilder();

            if (fStudentPracticeFilter.getStudent() != null) {

                if(i!=1){
                    sb.append(" and ");
                }
                params.put(i, fStudentPracticeFilter.getStudent().getId().getId());
                sb.append("ps.student_id = ?" + i++);

            }

            if (fStudentPracticeFilter.getOrganization()!=null) {

                if(i!=1){
                    sb.append(" and ");
                }
                params.put(i, fStudentPracticeFilter.getOrganization().getId().getId());
                sb.append("ps.organization_id = ?" + i++);

            }

            if (fStudentPracticeFilter.getComeInDate() != null) {

                if(i!=1){
                    sb.append(" and ");
                }
                i++;
                sb.append(" date_trunc ('day' ,  ps.come_in ) = date_trunc('day' , TIMESTAMP  '"+ CommonUtils.getFormattedDate(fStudentPracticeFilter.getComeInDate())+ "') ");

            }

            if (fStudentPracticeFilter.getComeOutDate() != null) {

                if(i!=1){
                    sb.append(" and ");
                }
                i++;
                sb.append(" date_trunc ('day' ,  ps.come_out ) = date_trunc('day' , TIMESTAMP  '"+ CommonUtils.getFormattedDate(fStudentPracticeFilter.getComeOutDate())+ "') ");

            }

            List<PRACTICE_STUDENT> list = new ArrayList<>();

            if(i > 1){
                sb.insert(0, " WHERE  ");
            }
            String sql = " select ps.* from practice_student ps where true "
                    + sb.toString();
            try {

                List tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
                if (!tmpList.isEmpty()) {
                    for (Object o : tmpList) {
                        Object[] oo = (Object[]) o;

                        PRACTICE_STUDENT practiceStudent = new PRACTICE_STUDENT();
                        practiceStudent.setId(ID.valueOf((Long)oo[0]));

                        try{
                            practiceStudent.setStudent(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USERS.class , ID.valueOf((Long)oo[1])));
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        try{
                            practiceStudent.setOrganization(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(ORGANIZATION.class , ID.valueOf((Long)oo[2])));
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        practiceStudent.setComeInDate((Date)oo[3]);
                        practiceStudent.setComeOutDate((Date)oo[4]);

                        list.add(practiceStudent);
                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load accounts list", ex);
            }
            refresh(list);
        }
    }

    @Override
    public void clearFilter() {
        if(studentPracticeFP.isAttached()){
            doFilter(studentPracticeFP.getFilterBean());
        }else if(informationPracticeFP.isAttached()) {
            doFilter(informationPracticeFP.getFilterBean());
        }
    }

    private void refresh(List list) {
        if (informationPracticeFP.isAttached()) {
            informationPracticeGM.setEntities(list);
            try{
                informationPracticeGW.refresh();
            }catch (Exception e){
                e.printStackTrace();
            }
        } else if (studentPracticeFP.isAttached()) {
            studentPracticeGM.setEntities(list);
            try{
                studentPracticeGW.refresh();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean preSave(Object source, Entity e, boolean isNew, int buttonId) {
        if(source.equals(informationPracticeGW) ){
            PRACTICE_INFORMATION pi = ((PRACTICE_INFORMATION)e);
            if(isNew){
                pi.setCreated(new Date());
                QueryModel<PRACTICE_INFORMATION> practiceInformationQM = new QueryModel<>(PRACTICE_INFORMATION.class);
                practiceInformationQM.addWhere("groups", ECriteria.EQUAL,pi.getGroups().getId());
                practiceInformationQM.addWhereAnd("entranceYear", ECriteria.EQUAL,pi.getEntranceYear().getId());
                try{
                    if(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(practiceInformationQM).size()>0){
                        Message.showError(getUILocaleUtil().getMessage("practice.already.exists.for.this.year"));
                        return false;
                    }

                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(e);
                }catch (NoResultException nre){
                    System.out.println("GOOD");
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }else{
                try{
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(e);
                }catch (Exception e1){
                    e1.printStackTrace();
                }
            }


        }else if(source.equals(studentPracticeGW)){
            try{
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(e);
            }catch (Exception e1){
                e1.printStackTrace();
            }
        }

        try{
            refresh(source);
        }catch (Exception e1){
            e1.printStackTrace();
        }

        return false;
    }



    @Override
    public void handleEntityEvent(EntityEvent ev) {
        super.handleEntityEvent(ev);
    }


    @Override
    public void onRefresh(Object source, List<Entity> entities) {
        super.onRefresh(source,entities);
    }


    @Override
    public boolean preDelete(Object source, List<Entity> entities, int buttonId) {
        try{
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(entities);
            refresh(source);
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public boolean onEdit(Object source, Entity e, int buttonId) {

        return true;
    }

    public void refresh(Object source){
        if(source.equals(informationPracticeGW)){
            doFilter(informationPracticeFP.getFilterBean());
        }else if(source.equals(studentPracticeGW)){
            doFilter(studentPracticeFP.getFilterBean());
        }
    }
}
