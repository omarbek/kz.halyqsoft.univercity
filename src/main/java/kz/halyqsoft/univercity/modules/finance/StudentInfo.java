package kz.halyqsoft.univercity.modules.finance;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.Sizeable;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VStudentPayment;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VStudentPaymentInfo;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.EmployeePdfCreator;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.List;

import static kz.halyqsoft.univercity.utils.CommonUtils.getUILocaleUtil;

public class StudentInfo implements EntityListener {

    private VerticalLayout mainVL;
    private GridWidget studentInfoGW;
    private DBGridModel studentInfoGM;
    private Button printBtn;

    private FinanceView financeView;
    private VStudentPayment studentPayment;
    private FileDownloader fileDownloaderr;
    private VStudentPaymentInfo paymentInfo;

    public StudentInfo(VStudentPayment studentPayment, FinanceView financeView) {

        this.studentPayment = studentPayment;
        this.financeView = financeView;

        mainVL = new VerticalLayout();
        mainVL.setImmediate(true);

        init();

    }

    private void init(){

        ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
        String REPORT = getUILocaleUtil().getCaption("report");
        printBtn = new Button();

        printBtn.setCaption(getUILocaleUtil().getCaption("print"));
        printBtn.setWidth(120, Sizeable.Unit.PIXELS);
        printBtn.setIcon(new ThemeResource("img/button/printer.png"));
        printBtn.addStyleName("print");
        printBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {

                Document document = new Document();
                ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
                VStudentPaymentInfo studentPaymentInfo = (VStudentPaymentInfo) studentInfoGW.getSelectedEntity();

                Collection<Entity> selectedEntities = studentInfoGW.getSelectedEntities();
                PdfPTable table = new PdfPTable(6);
                Paragraph h = new Paragraph("Международный Университет SILKWAY",EmployeePdfCreator.getFont(12, Font.BOLD));
                h.setAlignment(Element.ALIGN_CENTER);
                h.setSpacingAfter(10);

                Paragraph student = new Paragraph("Студент: "+studentPayment.getFio(),EmployeePdfCreator.getFont(12, Font.BOLD));
                student.setAlignment(Element.ALIGN_CENTER);
                //student.setSpacingAfter();

                Paragraph begin = new Paragraph("Остаток на "+ studentPayment.getBeginDate()+":" + studentPayment.getDebt(),EmployeePdfCreator.getFont(12, Font.BOLD));
                begin.setAlignment(Element.ALIGN_CENTER);
                begin.setSpacingAfter(10);

                Paragraph debt = new Paragraph("Остаток на "+ studentPayment.getEndDate()+":" + studentPayment.getDebt(),EmployeePdfCreator.getFont(12, Font.BOLD));
                debt.setAlignment(Element.ALIGN_CENTER);
                debt.setSpacingAfter(10);

                String[] headers = new String[]{"Учебный год",
                        "Курс", "Форма обучения", "Спецальность", "Стоимость курса", "Оплачено"};
                try {
                    PdfWriter.getInstance(document, byteArr);
                    document.open();
                    for (String s : headers) {
                        insertCell(table, s, Element.ALIGN_CENTER, 1, EmployeePdfCreator.getFont(12, Font.BOLD));
                    }

                        Map<Integer, Object> params = new HashMap<>();
                        String sql = "select\n" +
                                "        student.entrance_year,\n" +
                                "        (course.study_year)::text,\n" +
                                "        student.diploma_type_name,\n" +
                                "        student.speciality_name,\n" +
                                "        (sum(debt.debt_sum))::text,\n" +
                                "        (sum(payment.payment_sum))::text\n" +
                                "    from v_student_debts debt\n" +
                                "    INNER JOIN v_student student on student.id = debt.id\n" +
                                "    INNER JOIN study_year course on course.id = student.study_year_id\n" +
                                "    INNER JOIN student_payment payment on payment.student_id = student.id\n" +
                                "     WHERE student.user_code = '" + studentPayment.getCode() + "' " +
                                "   GROUP BY student.first_name,student.entrance_year,course.study_year,student.diploma_type_name,\n" +
                                "  student.speciality_name;\n";

                        List<ArrayList<String>> result = new ArrayList();

                        List<Object> tmpList = new ArrayList<>();
                        tmpList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params));
                        if (!tmpList.isEmpty()) {
                            for (Object o : tmpList) {
                                Object[] oo = (Object[]) o;
                                ArrayList<String> valuesList = new ArrayList();

                                for (int i = 0; i < oo.length; i++) {

                                    valuesList.add(oo[i] != null ? (String.valueOf(oo[i])) : "");
                                }
                                result.add(valuesList);
                            }
                        } else {
                            ArrayList<String> valuesList = new ArrayList();
                            for (int i = 0; i < 15; i++) {
                                valuesList.add(" ");
                            }
                            result.add(valuesList);
                        }

                    for (ArrayList<String> one : result) {
                        for (String s : one) {
                            insertCell(table, s, Element.ALIGN_LEFT, 1, EmployeePdfCreator.getFont(12, Font.BOLD));
                        }
                    }

                        document.add(h);
                        document.add(student);
                        document.add(begin);
                        document.add(table);
                        document.add(debt);

                    document.close();

                } catch (DocumentException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (fileDownloaderr == null) {
                    try {
                        fileDownloaderr = new FileDownloader(EmployeePdfCreator.getStreamResourceFromByte(byteArr.toByteArray(), REPORT + ".pdf"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    fileDownloaderr.setFileDownloadResource(EmployeePdfCreator.getStreamResourceFromByte(byteArr.toByteArray(), REPORT + ".pdf"));
                }
                fileDownloaderr.extend(printBtn);
            }
        });

        studentInfoGW = new GridWidget(VStudentPaymentInfo.class);
        studentInfoGW.setImmediate(true);
        studentInfoGW.showToolbar(false);
        studentInfoGW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, true);
        studentInfoGW.addEntityListener(this);

        studentInfoGM = (DBGridModel) studentInfoGW.getWidgetModel();
        studentInfoGM.setRowNumberVisible(false);
        studentInfoGM.setRowNumberWidth(30);
        studentInfoGM.setMultiSelect(false);
        studentInfoGM.setEntities(getStudentInfo());
        studentInfoGM.setRefreshType(ERefreshType.MANUAL);

        mainVL.addComponent(printBtn);
        mainVL.addComponent(studentInfoGW);
    }

    private void insertCell(PdfPTable table, String text, int align, int colspan, Font font) {

        PdfPCell cell = new PdfPCell(new Phrase(text.trim(), font));
        cell.setHorizontalAlignment(align);
        cell.setColspan(colspan);
        if (text.trim().equalsIgnoreCase("")) {
            cell.setMinimumHeight(10f);
        }
        table.addCell(cell);
    }

    public List<VStudentPaymentInfo> getStudentInfo() {

        List<VStudentPaymentInfo> list = new ArrayList<>();

        Map<Integer, Object> params = new HashMap<>();
        String sql = "select\n" +
                "        debt.id,\n" +
                "        student.entrance_year,\n" +
                "        (course.study_year)::INTEGER,\n" +
                "        student.diploma_type_name,\n" +
                "        student.speciality_name,\n" +
                "        (sum(debt.debt_sum))::INTEGER,\n" +
                "        (sum(payment.payment_sum))::INTEGER\n" +
                "    from v_student_debts debt\n" +
                "    INNER JOIN v_student student on student.id = debt.id\n" +
                "    INNER JOIN study_year course on course.id = student.study_year_id\n" +
                "    INNER JOIN student_payment payment on payment.student_id = student.id\n" +
                "     WHERE student.user_code = '" + studentPayment.getCode() + "' " +
                "   GROUP BY debt.id,student.first_name,student.entrance_year,course.study_year,student.diploma_type_name,\n" +
                "  student.speciality_name;\n" +
                "\n";

        try {
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VStudentPaymentInfo ve = new VStudentPaymentInfo();
                    ve.setId(ID.valueOf((long) oo[0]));
                    ve.setEntranceYear((String) oo[1]);
                    ve.setStudyYear((Integer) oo[2]);
                    ve.setDiplomaType((String) oo[3]);
                    ve.setSpeciality((String) oo[4]);
                    ve.setDebtSum((Integer) oo[5]);
                    ve.setPaymentSum((Integer) oo[6]);

                    list.add(ve);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load teacher list", ex);
        }
        refreshList(list);
        return list;
    }

    private void refreshList(List<VStudentPaymentInfo> list) {
        ((DBGridModel) studentInfoGW.getWidgetModel()).setEntities(list);
        try {
            studentInfoGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh vgroup list", ex);
        }
    }

    public VerticalLayout getMainVL() {
        return mainVL;
    }

    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {

    }

    @Override
    public boolean preSave(Object o, Entity entity, boolean b, int i) throws Exception {
        return false;
    }

    @Override
    public boolean preDelete(Object o, List<Entity> list, int i) {
        return false;
    }

    @Override
    public void onAccept(Object o, List<Entity> list, int i) {

    }

    @Override
    public void onException(Object o, Throwable throwable) {

    }

    @Override
    public void onRefresh(Object o, List<Entity> list) {

    }

    @Override
    public void beforeRefresh(Object o, int i) {

    }

    @Override
    public boolean preCreate(Object o, int i) {
        return false;
    }

    @Override
    public void onCreate(Object o, Entity entity, int i) {

    }

    @Override
    public boolean onEdit(Object o, Entity entity, int i) {
        return false;
    }

    @Override
    public boolean onPreview(Object o, Entity entity, int i) {
        return false;
    }

    @Override
    public void onFilter(Object o, QueryModel queryModel, int i) {

    }

    @Override
    public void onDelete(Object o, List<Entity> list, int i) {

    }

    @Override
    public void deferredCreate(Object o, Entity entity) {

    }

    @Override
    public void deferredDelete(Object o, List<Entity> list) {

    }

}
