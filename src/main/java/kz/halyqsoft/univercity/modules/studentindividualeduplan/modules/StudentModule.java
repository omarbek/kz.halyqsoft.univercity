package kz.halyqsoft.univercity.modules.studentindividualeduplan.modules;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_COORDINATOR;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_STUDENT;
import kz.halyqsoft.univercity.modules.regapplicants.TableForm;
import kz.halyqsoft.univercity.modules.regapplicants.TableFormRus;
import kz.halyqsoft.univercity.modules.studentindividualeduplan.StudentPlan;
import kz.halyqsoft.univercity.modules.studentindividualeduplan.dialogs.LanguageDialog;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.dialog.Message;

import javax.persistence.NoResultException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

public class StudentModule extends BaseModule{

    private static String replaced;
    private static String inLettersEdu;
    private static String moneyForEducation;
    private static String answerEdu;


    private static final int FATHER = 1;
    private static final int MOTHER = 2;
    private static final int ADDRESS_FACT = 2;

    private Button downloadBtn;
    public StudentModule(BaseModule baseModule, StudentPlan studentPlan) {
        super(V_STUDENT.class, baseModule, studentPlan);
        getMainGW().setMultiSelect(true);
        getMainGM().getQueryModel().addWhere("group" , ECriteria.EQUAL,baseModule.getMainGW().getSelectedEntity().getId());

        downloadBtn = new Button(CommonUtils.getUILocaleUtil().getCaption("download"));
        downloadBtn.setStyleName(ValoTheme.BUTTON_LARGE);
        downloadBtn.setIcon(FontAwesome.DOWNLOAD);
        downloadBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if(getMainGW().getSelectedEntities().size() > 0){
                    List<STUDENT> studentList = new ArrayList<>();
                    for(Entity vStudent : getMainGW().getSelectedEntities()){

                        STUDENT student = null;
                        try{
                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean .class)
                                    .lookup(STUDENT.class , vStudent.getId());
                        }catch (Exception e){
                            e.printStackTrace();
                            return;
                        }
                        studentList.add(student);
                    }
                    LanguageDialog languageDialog = new LanguageDialog(studentList);
                }else{
                    Message.showError(CommonUtils.getUILocaleUtil().getMessage("choose.field"));
                }
            }
        });
        getButtonsPanel().addComponent(downloadBtn);
    }


//    public static StreamResource createResourceStudent(String value, STUDENT student) {
//
//
//        String fileName = "";
//        if (value.equals("97")) {
//            fileName = "ИУПС_" + Calendar.getInstance().getTimeInMillis() + ".pdf";
//        } else if (value.equals("96")) {
//            fileName = "ИУПС_рус_" + Calendar.getInstance().getTimeInMillis() + ".pdf";
//        }
//        return new StreamResource(new StreamResource.StreamSource() {
//            @Override
//            public InputStream getStream() {
//                Document docum = new Document();
//                Paragraph title = new Paragraph();
//                QueryModel<PDF_PROPERTY> propertyQM = new QueryModel<>(PDF_PROPERTY.class);
//                FromItem doc = propertyQM.addJoin(EJoin.INNER_JOIN, "pdfDocument", PDF_DOCUMENT.class, "id");
//
//                propertyQM.addWhere(doc, "id", ECriteria.EQUAL, value);
//                propertyQM.addOrder("orderNumber");
//                List<PDF_PROPERTY> properties = null;
//                try {
//                    properties = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(propertyQM);
//                    ByteArrayOutputStream byteArrayOutputStream1 = new ByteArrayOutputStream();
//                    PdfWriter pdfWriter = PdfWriter.getInstance(docum, byteArrayOutputStream1);
//                    docum.open();
//                    PDF_DOCUMENT pdf_document = null;
//                    try {
//                        QueryModel<PDF_DOCUMENT> pdfDocumentQueryModel = new QueryModel<>(PDF_DOCUMENT.class);
//                        pdfDocumentQueryModel.addWhere("id", ECriteria.EQUAL, value);
//                        pdf_document = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
//                                .lookupSingle(pdfDocumentQueryModel);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                    title = new Paragraph(pdf_document.getTitle(),
//                            getFont(12, Font.BOLD));
//                    title.setAlignment(Element.ALIGN_CENTER);
//
//                    docum.add(title);
//
//                    for (PDF_PROPERTY property : properties) {
//
//                        String text = property.getText();
//                            if (student != null) {
//                                if(text.contains("$table")) {
//                                    ID studentID = student.getId();
//                                    TableForm tableForm = new TableForm(docum,studentID);
//                                }else  if(text.contains("$rus")) {
//                                    ID studentID = student.getId();
//                                    TableFormRus tableFormRus = new TableFormRus(docum,studentID);
//                                }else{
//                                    setReplaced(text, student);
//                                }
//                            }
//                        Paragraph paragraph = new Paragraph(replaced,
//                                getFont(Integer.parseInt(property.getSize().toString()),
//                                        CommonUtils.getFontMap(property.getFont())));
//
//                        if (property.isCenter()) {
//                            paragraph.setAlignment(Element.ALIGN_CENTER);
//                        }
//                        paragraph.setSpacingBefore(property.getY());
//                        paragraph.setIndentationLeft(property.getX());
//
//
//                        docum.add(paragraph);
//                    }
//
//                    pdfWriter.close();
//                    docum.close();
//                    return new ByteArrayInputStream(byteArrayOutputStream1.toByteArray());
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return null;
//                }
//            }
//        }, fileName);
//    }
//
//    private static String getStringBeforeSlash(String name) {
//        String returnName;
//        try {
//            returnName = name.substring(0, name.lastIndexOf('/') - 1);
//        } catch (Exception e) {
//            returnName = name;
//        }
//        return returnName;
//    }
//    public static Font getFont(int fontSize, int font) {
//        String fontPath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/WEB-INF/classes/fonts";
//        BaseFont timesNewRoman = null;
//        try {
//            timesNewRoman = BaseFont.createFont(fontPath + "/TimesNewRoman/times.ttf", BaseFont.IDENTITY_H,
//                    BaseFont.EMBEDDED);
//        } catch (Exception e) {
//            e.printStackTrace();//TODO catch
//        }
//        return new Font(timesNewRoman, fontSize, font);
//    }
//
//    private static void setReplaced(String text, STUDENT student) throws Exception {
//
//        Date date = Calendar.getInstance().getTime();
//
//        STUDENT_EDUCATION studentEducation =student.getLastEducation();
//
//        SPECIALITY speciality = student.getEntrantSpecialities().iterator().next().getSpeciality();
//
//        STUDENT_RELATIVE studentRelativeMother = getStudent_relative(student, MOTHER);
//        STUDENT_RELATIVE studentRelativeFather = getStudent_relative(student, FATHER);
//
//        USER_ADDRESS userAddress = null;
//        QueryModel<USER_ADDRESS> userAddressQueryModel = new QueryModel<>(USER_ADDRESS.class);
//        userAddressQueryModel.addWhere("user", ECriteria.EQUAL, student.getId());
//        userAddressQueryModel.addWhereAnd("addressType", ECriteria.EQUAL, ID.valueOf(ADDRESS_FACT));
//        try{
//            userAddress = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(userAddressQueryModel);
//        }catch (NoResultException nre){
//            nre.printStackTrace();
//        }
//
//        QueryModel<EDUCATION_DOC> educationDocQueryModel = new QueryModel<>(EDUCATION_DOC.class);
//
//        EDUCATION_DOC educationDoc = null;
//        FromItem sc = educationDocQueryModel.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
//        educationDocQueryModel.addWhere(sc, "user", ECriteria.EQUAL, student.getId());
//        try{
//            educationDoc = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(educationDocQueryModel);
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//        UNT_CERTIFICATE untCertificate;
//        QueryModel<UNT_CERTIFICATE> untCertificateQueryModel = new QueryModel<>(UNT_CERTIFICATE.class);
//        FromItem unt = untCertificateQueryModel.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
//        untCertificateQueryModel.addWhere(unt, "user", ECriteria.EQUAL, student.getId());
//        try {
//            untCertificate = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
//                    .lookupSingle(untCertificateQueryModel);
//        } catch (NoResultException ex) {
//            untCertificate = null;
//        }
//
//        String inLettersDorn = "";
//        String moneyForDorm = "";
//        ACCOUNTANT_PRICE accountantPriceDorm = getAccountantPrice(student, 1);
//        if (accountantPriceDorm != null) {
//            moneyForDorm = String.valueOf(accountantPriceDorm.getPrice());
//            inLettersDorn = accountantPriceDorm.getPriceInLetters();
//        } else {
//            moneyForDorm = "0";
//        }
//        String answerDorm = String.valueOf(Double.valueOf(moneyForDorm) / 8);
//
//        String ochnii = student.getDiplomaType().toString();
//        DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
//        String today = formatter.format(date);
//        if (student.getDiplomaType().toString().equals("Очный")) {
//            ochnii = "очной";
//        } else if (student.getDiplomaType().toString().equals("Заочный") ||
//                student.getDiplomaType().toString().equals("Заочный после колледжа") ||
//                student.getDiplomaType().toString().equals("Заочный 2-высшее")) {
//            ochnii = "заочной";
//        }
//
//        String pdfProperty = "";
//        String tableType = "ansEdu тенге ";
//
//        DateFormat form = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
//
//        Date dateBirth = form.parse(student.getBirthDate() != null ? student.getBirthDate().toString() : new Date().toString());
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(dateBirth);
//        String birthdayDate = cal.get(Calendar.DATE) + "." + (cal.get(Calendar.MONTH) + 1) + "." + cal.get(Calendar.YEAR);
//        Date date1 = formatter.parse(birthdayDate);
//        String birthday = formatter.format(date1);
//
//
//        Date dateDocument = form.parse(educationDoc.getIssueDate()!=null ? educationDoc.getIssueDate().toString() : new Date().toString());
//        Calendar cal1 = Calendar.getInstance();
//        cal1.setTime(dateDocument);
//
//        String formatDate = cal1.get(Calendar.DATE) + "." + (cal1.get(Calendar.MONTH) + 1) + "." + cal1.get(Calendar.YEAR);
//        Date date2 = formatter.parse(formatDate);
//        String attestationDate = formatter.format(date2);
//
//        Date created = form.parse(student.getCreated().toString());
//        Calendar cal2 = Calendar.getInstance();
//        cal2.setTime(created);
//        String format = cal2.get(Calendar.DATE) + "." + (cal2.get(Calendar.MONTH) + 1) + "." + cal2.get(Calendar.YEAR);
//        Date date3 = formatter.parse(format);
//        String createdDate = formatter.format(date3);
//
//        String dorm = "қажет емес";
//        if (student.isNeedDorm()) {
//            dorm = "қажет";
//        }
//
//        if (student.getMiddleName() == null) {
//            student.setMiddleName("");
//        }
//
//        V_COORDINATOR coordinator = new V_COORDINATOR();
//        coordinator.setFio("-");
//        if (student.getCoordinator() == null) {
//            student.setCoordinator(coordinator);
//        }
//        if(educationDoc!=null){
//            if (educationDoc.getEndYear() == null) {
//                educationDoc.setEndYear(Calendar.getInstance().get(Calendar.YEAR));
//            }
//        }
//        if (studentRelativeFather.getPhoneMobile() == null) {
//            studentRelativeFather.setPhoneMobile("***");
//        }
//        if (studentRelativeMother.getPhoneMobile() == null) {
//            studentRelativeMother.setPhoneMobile("***");
//        }
//
//        if (studentRelativeMother.getWorkPlace() == null) {
//            studentRelativeMother.setWorkPlace("-");
//        }
//        if (studentRelativeFather.getWorkPlace() == null) {
//            studentRelativeFather.setWorkPlace("-");
//        }
//        if (studentRelativeFather.getPostName() == null) {
//            studentRelativeFather.setPostName("-");
//        }
//        if (studentRelativeMother.getPostName() == null) {
//            studentRelativeMother.setPostName("-");
//        }
//
//        String facultyName = getStringBeforeSlash(studentEducation.getFaculty().toString());
//        String specialityName = getStringBeforeSlash(speciality.getSpecName());
//
//        USER_PASSPORT user_passport = null;
//        try {
//            QueryModel<USER_PASSPORT> qm = new QueryModel<>(USER_PASSPORT.class);
//            FromItem fi1 = qm.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
//            FromItem fi = fi1.addJoin(EJoin.INNER_JOIN, "user", USERS.class, "id");
//            qm.addWhere(fi, "deleted", ECriteria.EQUAL, false);
//            qm.addWhere(fi, "id", ECriteria.EQUAL, student.getId());
//
//
//            user_passport = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(qm);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        String iin = "";
//        String passportNumber = "";
//        if (user_passport != null) {
//            iin = user_passport.getIin();
//            passportNumber = user_passport.getDocumentNo();
//        }
//        String fullAddress = "";
//        if(userAddress!=null){
//            if (userAddress.getCountry() != null)
//                fullAddress += " " + userAddress.getCountry();
//            if (userAddress.getRegion() != null)
//                fullAddress += " " + userAddress.getRegion();
//            if (userAddress.getCity() != null)
//                fullAddress += " " + userAddress.getCity();
//            if (userAddress.getStreet() != null)
//                fullAddress += " " + userAddress.getStreet();
//        }
//        String firstCourseMoney = moneyForEducation;
//        String secondCourseMoney = moneyForEducation;
//
//
//        LocalDateTime now = LocalDateTime.now();
//
//        Map<String, Object> params = new HashMap<>();
//        String createdBy = student.getCreatedBy();
//        params.put("login", createdBy);
//        USERS tecnhik = CommonUtils.getEmployee(params);
//        replaced = text.replaceAll("\\$fio", student.toString())
//                .replaceAll("\\$money", moneyForEducation)
//                //   .replaceAll(tableType, pdfProperty)
//                .replaceAll("\\$ansEdu", answerEdu)
//                .replaceAll("\\$educode", studentEducation.getSpeciality().getCode())
//                .replaceAll("\\$language", studentEducation.getLanguage().getLangName())
//                .replaceAll("\\$ansDorm", answerDorm)
//                //            .replaceAll("\\$code", student.getCode())
//                .replaceAll("\\$firstCourseMoney", firstCourseMoney)
//                .replaceAll("\\$secondCourseMoney", secondCourseMoney)
////                .replaceAll("\\$year", now.getYear() + "")
////                .replaceAll("\\$month", now.getMonth().getValue() + "")
////                .replaceAll("\\$data", now.getDayOfMonth() + "")
//                .replaceAll("\\$year", "_______")
//                .replaceAll("\\$month", "")
//                .replaceAll("\\$data", "")
//                .replaceAll("\\$iin", iin)
//                .replaceAll("\\$passportNumber", passportNumber)
//                .replaceAll("\\$address", fullAddress)
//                .replaceAll("\\$faculty", facultyName)
//                .replaceAll("\\$DataMonthYear", today + " года")
//                .replaceAll("\\$formaobuch", ochnii)
//                .replaceAll("\\$data\\$month\\$year", today)
//                .replaceAll("\\$email", userAddress.getPostalCode())
//                .replaceAll("\\$rekvizit", userAddress.getStreet())
//                .replaceAll("\\$phone", "+7" + student.getPhoneMobile())
//                .replaceAll("\\$InLetters", inLettersEdu)
//                .replaceAll("\\$Obshaga", moneyForDorm)
//                .replaceAll("\\$Dorm", inLettersDorn)
//                .replaceAll("\\$aboutMe", "-")
//                .replaceAll("\\$country", student.getCitizenship().toString())
//                .replaceAll("\\$status", student.getMaritalStatus().toString())
//                .replaceAll("\\$father", studentRelativeFather.getFio())
//                .replaceAll("\\$mother", studentRelativeMother.getFio())
//                .replaceAll("\\$numFather", "+7 " + studentRelativeFather.getPhoneMobile())
//                .replaceAll("\\$numMother", "+7 " + studentRelativeMother.getPhoneMobile())
//                .replaceAll("\\$gender", student.getSex().toString())
//                .replaceAll("\\$birthYear", birthday)
//                .replaceAll("\\$language", educationDoc.getLanguage().getLangName())
//                .replaceAll("\\$nationality", student.getNationality().toString())
//                .replaceAll("\\$info", educationDoc.getEndYear().toString() + ", "
//                        + educationDoc.getEducationType() + ", " + educationDoc.getSchoolName())
//                .replaceAll("\\$speciality", specialityName)
//                .replaceAll("\\$parentsAddress", studentRelativeFather.getFio() + ", "
//                        + studentRelativeFather.getWorkPlace() + "    "
//                        + studentRelativeFather.getPostName() + '\n'
//                        + studentRelativeMother.getFio() + ", "
//                        + studentRelativeMother.getWorkPlace() + "    "
//                        + studentRelativeMother.getPostName())
//                .replaceAll("\\$trudovoe", "-")
//                .replaceAll("\\$name", student.getFirstName())
//                .replaceAll("\\$surname", student.getLastName())
//                .replaceAll("\\$firstName", student.getMiddleName())
//                .replaceAll("\\$education", educationDoc.getEducationType().toString())
//                .replaceAll("\\$technic", tecnhik.toString())
//                .replaceAll("\\$attestat", attestationDate)
//                .replaceAll("\\$nomer", educationDoc.getDocumentNo())
//                .replaceAll("\\$ent", untCertificate == null ? "" : untCertificate.getDocumentNo())
////                .replaceAll("\\$document", createdDate)
//                .replaceAll("\\$document", "_______")
//                .replaceAll("\\$diplomaType", student.getDiplomaType().toString())
//                // .replaceAll("\\$group", sdf())
//                .replaceAll("қажет, қажет емес", dorm)
//                //.replaceAll("$educode", studentEducation.getSpeciality().getCode());
//                //  .replaceAll("$language", studentEducation.getLanguage().getLangName())
//                .replaceAll("\\$code", student.getCode());
//    }
//
//
//
//    private static ACCOUNTANT_PRICE getAccountantPrice(STUDENT student, int contractPaymentTypeId) throws Exception {
//        ACCOUNTANT_PRICE accountantPrice;
//        QueryModel<ACCOUNTANT_PRICE> accountantPriceQueryModel = new QueryModel<>(ACCOUNTANT_PRICE.class);
//        accountantPriceQueryModel.addWhere("diplomaType", ECriteria.EQUAL, student.getDiplomaType().getId());
//        accountantPriceQueryModel.addWhere("level", ECriteria.EQUAL, student.getLevel().getId());
//        accountantPriceQueryModel.addWhere("contractPaymentType", ECriteria.EQUAL, ID.valueOf(contractPaymentTypeId));
//        accountantPriceQueryModel.addWhere("deleted", ECriteria.EQUAL, false);
//        try {
//            accountantPrice = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(accountantPriceQueryModel);
//        } catch (NoResultException e) {
//            accountantPrice = null;
//        }
//
//        return accountantPrice;
//    }
//
//    public static STUDENT_RELATIVE getStudent_relative(STUDENT student, int relativeType) throws Exception {
//        STUDENT_RELATIVE studentRelative;
//        try {
//            QueryModel<STUDENT_RELATIVE> relative = new QueryModel<>(STUDENT_RELATIVE.class);
//            relative.addWhere("student", ECriteria.EQUAL, student.getId());
//            relative.addWhere("relativeType", ECriteria.EQUAL, ID.valueOf(relativeType));
//            studentRelative = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
//                    .lookupSingle(relative);
//        } catch (NoResultException ex) {
//            studentRelative = new STUDENT_RELATIVE();
//            studentRelative.setFio("-");
//        }
//        return studentRelative;
//    }

    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {

    }
}
