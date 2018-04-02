package kz.halyqsoft.univercity.modules.student;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.server.VaadinService;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDENT_CATEGORY;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_USER_AWARD;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_USER_LANGUAGE;
import kz.halyqsoft.univercity.utils.ErrorUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.locale.UILocaleUtil;
import org.r3a.common.vaadin.widget.dialog.Message;

import javax.persistence.NoResultException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Vector;

//for students and entrants only
public class StudentProfilePDFStream implements StreamSource {

    private static final long serialVersionUID = 1L;

    private static final ID KBTU_ID = ID.valueOf(421);//TODO

    final String NA = "";
    STUDENT student;
    ID studentId;

    STUDENT_EDUCATION se;
    USER_PASSPORT userPassport;

    UNT_CERTIFICATE untCertificate;

    List<UNT_CERT_SUBJECT> untr;
    UNT_CERT_SUBJECT[] untrArr = null;
    USER_SOCIAL_CATEGORY[] tuscArr = null;

    Vector<V_USER_AWARD> awards = null;

    Boolean altynBelgi = null;
    Boolean bolashak = null;

    DISABILITY_DOC disabilityDoc;
    REPATRIATE_DOC repatriateDoc;
    PREEMPTIVE_RIGHT preemptiveRight;

    USER_ADDRESS userRegAddress;
    USER_ADDRESS userFactAddress;

    STUDENT_RELATIVE father;
    STUDENT_RELATIVE mother;
    MILITARY_DOC militaryDoc;
    STUDENT_CONTRACT contract;

    EDUCATION_DOC atestat = null;
    EDUCATION_DOC diplom = null;

    String[] langArr;

    EDUCATION_DOC[] edocs;

    byte[] userPhotoBytes;

    DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    Font font = null;
    BaseFont timesNewRoman;
    Font boldFont = null;
    Image userPhoto = null;
    int fontSize;

    private void init() {
        // student
        try {
            student = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookup(STUDENT.class, studentId);
        } catch (Exception e5) {
            Message.showError(e5.toString());
            ErrorUtils.LOG.error("PDF: student", e5);
        }

        // education

        try {

            QueryModel<STUDENT_EDUCATION> seQM = new QueryModel<>(STUDENT_EDUCATION.class);
            seQM.addWhere("student", ECriteria.EQUAL, student.getId());
            seQM.addWhereNullAnd("child");
            STUDENT_EDUCATION se1 = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookupSingle(seQM);
            se = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookup(STUDENT_EDUCATION.class, se1.getId());
            if (se != null)
                ErrorUtils.LOG.error("PDF: education NORM NORM ");

        } catch (NoResultException ex) {

        } catch (Exception e5) {
            ErrorUtils.LOG.error("PDF: education", e5);
            Message.showError(e5.toString());
        }

        // passport
        try {
            QueryModel<USER_PASSPORT> userPassportQM = new QueryModel<>(USER_PASSPORT.class);
            FromItem fi = userPassportQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
            userPassportQM.addWhere(fi, "user", ECriteria.EQUAL, student.getId());
            userPassportQM.addWhereAnd(fi, "deleted", Boolean.FALSE);

            USER_PASSPORT userPassport1 = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookupSingle(userPassportQM);
            userPassport = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookup(USER_PASSPORT.class, userPassport1.getId());
        } catch (NoResultException ex) {

        } catch (Exception e1) {
            ErrorUtils.LOG.error("PDF: passport", e1);
            Message.showError(e1.toString());
        }

        // untCertificate

        try {
            QueryModel<UNT_CERTIFICATE> untCertificateQM = new QueryModel<>(UNT_CERTIFICATE.class);
            FromItem fi2 = untCertificateQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
            untCertificateQM.addWhere(fi2, "user", ECriteria.EQUAL, student.getId());
            untCertificateQM.addWhereAnd(fi2, "deleted", Boolean.FALSE);
            UNT_CERTIFICATE untCertificate1 = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookupSingle(untCertificateQM);
            untCertificate = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookup(UNT_CERTIFICATE.class, untCertificate1.getId());
        } catch (NoResultException ex) {
            ex.printStackTrace();
        } catch (Exception e5) {
            ErrorUtils.LOG.error("PDF: passport", e5);
        }

        // rates
        try {

            QueryModel<UNT_CERT_SUBJECT> untRatesQM = new QueryModel<>(UNT_CERT_SUBJECT.class);
            untRatesQM.addWhere("untCertificate", ECriteria.EQUAL, untCertificate.getId());

            untr = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(untRatesQM);
            if (untr != null && untr.size() != 0) {

                untrArr = new UNT_CERT_SUBJECT[untr.size()];

                for (int i = 0; i < untrArr.length; i++) {
                    if (untr.get(i) != null)
                        untrArr[i] = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                lookup(UNT_CERT_SUBJECT.class, untr.get(i).getId());
                }
            }

        } catch (Exception e1) {
            ErrorUtils.LOG.error("PDF: rates", e1);
        }

        // education
        try {
            QueryModel<EDUCATION_DOC> educationQM = new QueryModel<>(EDUCATION_DOC.class);
            FromItem educationUDFI = educationQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
            educationQM.addWhere(educationUDFI, "deleted", Boolean.FALSE);

            educationQM.addWhereAnd(educationUDFI, "user", ECriteria.EQUAL, student.getId());

            List<EDUCATION_DOC> list = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookup(educationQM);

            edocs = new EDUCATION_DOC[list.size()];

            for (int i = 0; i < list.size(); i++) {
                EDUCATION_DOC element = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(EDUCATION_DOC.class, list.get(i).getId());
                edocs[i] = element;
            }

        } catch (Exception e) {
            ErrorUtils.LOG.error("PDF: education", e);
        }
        // diplom/ atestat

        if (edocs != null && edocs.length != 0) {
            for (EDUCATION_DOC tmp : edocs) {
                if (tmp.getEducationDocType().getTypeName().equals("Аттестат"))
                    atestat = tmp;
                else if (tmp.getEducationDocType().getTypeName().equals("Диплом"))
                    diplom = tmp;
            }
        }

        // military
        try {
            QueryModel<MILITARY_DOC> militaryDocQM = new QueryModel<>(MILITARY_DOC.class);
            FromItem fi1 = militaryDocQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
            militaryDocQM.addWhere(fi1, "user", ECriteria.EQUAL, studentId);
            militaryDocQM.addWhereAnd(fi1, "deleted", Boolean.FALSE);
            militaryDoc = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookupSingle(militaryDocQM);
            militaryDoc = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookup(MILITARY_DOC.class, militaryDoc.getId());

        } catch (NoResultException ex) {
            ex.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
            Message.showError("1:" + e1.toString());
        }

        // preemptiveRight

        try {
            QueryModel<PREEMPTIVE_RIGHT> preemptiveRightQM = new QueryModel<>(PREEMPTIVE_RIGHT.class);
            FromItem fi = preemptiveRightQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
            preemptiveRightQM.addWhere(fi, "user", ECriteria.EQUAL, studentId);
            preemptiveRightQM.addWhereAnd(fi, "deleted", Boolean.FALSE);
            preemptiveRight = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(preemptiveRightQM);
            preemptiveRight = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(PREEMPTIVE_RIGHT.class, preemptiveRight.getId());
        } catch (NoResultException ex) {
            ex.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
            Message.showError("2:" + e1.toString());
        }

        // disabilityDoc

        try {
            QueryModel<DISABILITY_DOC> disabilityDocQM = new QueryModel<DISABILITY_DOC>(DISABILITY_DOC.class);
            FromItem fi = disabilityDocQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
            disabilityDocQM.addWhere(fi, "user", ECriteria.EQUAL, studentId);
            disabilityDocQM.addWhereAnd(fi, "deleted", Boolean.FALSE);
            disabilityDoc = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(disabilityDocQM);
            disabilityDoc = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(DISABILITY_DOC.class, disabilityDoc.getId());
        } catch (NoResultException ex) {
            ex.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
            Message.showError("3:" + e1.toString());
        }

        // oralman

        try {
            QueryModel<REPATRIATE_DOC> repatriateDocQM = new QueryModel<REPATRIATE_DOC>(REPATRIATE_DOC.class);
            FromItem fi = repatriateDocQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
            repatriateDocQM.addWhere(fi, "user", ECriteria.EQUAL, studentId);
            repatriateDocQM.addWhereAnd(fi, "deleted", Boolean.FALSE);
            repatriateDoc = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(repatriateDocQM);
            repatriateDoc = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(REPATRIATE_DOC.class, repatriateDoc.getId());
        } catch (NoResultException ex) {
            ex.printStackTrace();
        } catch (Exception e1) {
            e1.printStackTrace();
            Message.showError("4:" + e1.toString());
        }

        // address
        try {

            QueryModel<USER_ADDRESS> addressRegQM = new QueryModel<USER_ADDRESS>(USER_ADDRESS.class);
            addressRegQM.addWhere("user", ECriteria.EQUAL, student.getId());
            addressRegQM.addWhereAnd("addressType", ECriteria.EQUAL, ID.valueOf(1));
            userRegAddress = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(addressRegQM);
            userRegAddress = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_ADDRESS.class, userRegAddress.getId());

            QueryModel<USER_ADDRESS> addressFactQM = new QueryModel<USER_ADDRESS>(USER_ADDRESS.class);
            addressFactQM.addWhere("user", ECriteria.EQUAL, student.getId());
            addressFactQM.addWhereAnd("addressType", ECriteria.EQUAL, ID.valueOf(2));

            userFactAddress = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(addressFactQM);
            userFactAddress = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_ADDRESS.class, userFactAddress.getId());
        } catch (NoResultException ex) {

        } catch (Exception e4) {
            ErrorUtils.LOG.error("PDF: address", e4);
            Message.showError(e4.toString());
        }
        // contract
        try {
            QueryModel<STUDENT_CONTRACT> q = new QueryModel<STUDENT_CONTRACT>(STUDENT_CONTRACT.class);
            FromItem fi1 = q.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "id");
            q.addWhere(fi1, "user", ECriteria.EQUAL, student.getId());
            contract = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(q);
            contract = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(STUDENT_CONTRACT.class, contract.getId());
            // ErrorUtils.LOG.error("PDF: " + tud.size() + " " +
            // student.getId().getId().intValue());
        } catch (NoResultException ex) {
        } catch (Exception e3) {
            ErrorUtils.LOG.error("PDF: contract", e3);
        }
        // parents
        try {
            QueryModel<STUDENT_RELATIVE> fatherQM = new QueryModel<STUDENT_RELATIVE>(STUDENT_RELATIVE.class);
            fatherQM.addWhere("student", ECriteria.EQUAL, studentId);
            fatherQM.addWhereAnd("relativeType", ECriteria.EQUAL, ID.valueOf(1));
            father = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(fatherQM);
            father = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(STUDENT_RELATIVE.class, father.getId());
        } catch (Exception e1) {
            ErrorUtils.LOG.error("5:" + e1.toString());
        }
        try {
            QueryModel<STUDENT_RELATIVE> motherQM = new QueryModel<STUDENT_RELATIVE>(STUDENT_RELATIVE.class);
            motherQM.addWhere("student", ECriteria.EQUAL, studentId);
            motherQM.addWhereAnd("relativeType", ECriteria.EQUAL, ID.valueOf(2));
            mother = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(motherQM);
            mother = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(STUDENT_RELATIVE.class, mother.getId());
        } catch (Exception e1) {
            ErrorUtils.LOG.error("5:" + e1.toString());
        }

        // awards
        try {
            altynBelgi = Boolean.FALSE;
            bolashak = Boolean.FALSE;
            QueryModel<V_USER_AWARD> awardsQM = new QueryModel<V_USER_AWARD>(V_USER_AWARD.class);
            awardsQM.addWhere("user", ECriteria.EQUAL, studentId);

            List<V_USER_AWARD> list = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(awardsQM);
            if (list != null && list.size() != 0) {
                awards = new Vector<V_USER_AWARD>();
                for (int i = 0; i < list.size(); i++) {
                    V_USER_AWARD element = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(V_USER_AWARD.class, list.get(i).getId());
                    if (element.getAward().getAwardName().equals("Обладатель Алтын белгі"))
                        altynBelgi = Boolean.TRUE;
                    else if (element.getAward().getAwardName().equals("Претендент на Болашак"))
                        bolashak = Boolean.TRUE;
                    else
                        awards.add(element);
                }
            }
        } catch (NoResultException ex) {

        } catch (Exception e2) {
            ErrorUtils.LOG.error("PDF: awards", e2);
        }
        // languages
        try {
            QueryModel<V_USER_LANGUAGE> languageQM1 = new QueryModel<V_USER_LANGUAGE>(V_USER_LANGUAGE.class);
            languageQM1.addWhere("user", ECriteria.EQUAL, student.getId());
            List<V_USER_LANGUAGE> langs;
            langs = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(languageQM1);
            langArr = new String[6];
            for (int i = 0; i < langArr.length; i++)
                langArr[i] = " ";
            if (langs != null && langs.size() != 0) {
                for (V_USER_LANGUAGE tmp : langs) {
                    if (tmp.getLanguageName().equals("Прочие языки"))
                        langArr[5] = "X";
                    else if (tmp.getLanguageName().equals("Казахский"))
                        langArr[0] = "X";
                    else if (tmp.getLanguageName().equals("Русский"))
                        langArr[1] = "X";
                    else if (tmp.getLanguageName().equals("Английский"))
                        langArr[2] = "X";
                    else if (tmp.getLanguageName().equals("Немецкий"))
                        langArr[3] = "X";
                    else if (tmp.getLanguageName().equals("Французский"))
                        langArr[4] = "X";
                }
            }
        } catch (Exception e) {
            ErrorUtils.LOG.error("PDF: languages", e);
        }

        // socialCategory
        try {
            QueryModel<USER_SOCIAL_CATEGORY> tuscQM1 = new QueryModel<USER_SOCIAL_CATEGORY>(USER_SOCIAL_CATEGORY.class);
            tuscQM1.addWhere("user", ECriteria.EQUAL, student.getId());
            List<USER_SOCIAL_CATEGORY> tuscList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(tuscQM1);
            if (tuscList != null && tuscList.size() != 0) {
                tuscArr = new USER_SOCIAL_CATEGORY[tuscList.size()];

                int i = 0;
                for (USER_SOCIAL_CATEGORY tmp : tuscList) {
                    tuscArr[i++] = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_SOCIAL_CATEGORY.class, tmp.getId());
                }
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            Message.showError("6:" + e1.toString());
        }

        try {
            String fontPath = VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/WEB-INF/fonts";
            fontSize = 11;
            timesNewRoman = BaseFont.createFont(fontPath + "/TimesNewRoman/times.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            font = new Font(timesNewRoman, fontSize);
            boldFont = new Font(timesNewRoman, fontSize, Font.BOLD);
            if (userPhotoBytes != null)
                userPhoto = Image.getInstance(userPhotoBytes);
            else if (student.getSex().toString().equals("Женский"))
                userPhoto = Image.getInstance(VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/WEB-INF/img/female.png");
            else
                userPhoto = Image.getInstance(VaadinService.getCurrent().getBaseDirectory().getAbsolutePath() + "/WEB-INF/img/male.png");
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            Message.showError("7:" + e.toString());
        }
    }

    @Override
    public InputStream getStream() {

        ByteArrayOutputStream baos = null;
        try {
            init();

            Document document = new Document();
            document.setMargins(-20, -20, 20, 20);
            baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);

            document.open();

            int tableColspan = 6;
            PdfPTable table = new PdfPTable(tableColspan);
            PdfPCell cell;

            cell = new PdfPCell(userPhoto);
            cell.setRowspan(8);
            cell.setBorder(PdfPCell.NO_BORDER);
            table.addCell(cell);

            setTableForm1(getUILocaleUtil().getCaption("pdf.student.id"), student.getCode(), table, 2, 3);
            setTableForm1(getUILocaleUtil().getCaption("pdf.student.mobile.telephone"),
                    student.getPhoneMobile(), table, 2, 3);
            setTableForm1(getUILocaleUtil().getCaption("pdf.student.email"), student.getEmail(), table, 2, 3);

            boolean filledEducation = false;
            if (student.getCategory().getId().equals(STUDENT_CATEGORY.ENROLLEE_ID)) {
                for (ENTRANT_SPECIALITY entrantSpeciality : student.getEntrantSpecialities()) {
                    if (entrantSpeciality.getUniversity().getId().equals(KBTU_ID)) {
                        setTableForm1(getUILocaleUtil().getCaption("pdf.student.studying.language"),
                                entrantSpeciality.getLanguage().getLangName(), table, 2, 3);
                        DEPARTMENT department = entrantSpeciality.getSpeciality().getDepartment();
                        while (department.hasParent()) {
                            department = department.getParent();
                        }
                        setTableForm1(getUILocaleUtil().getCaption("pdf.student.faculty"), department.getDeptName(), table, 2, 3);
                        setTableForm1(getUILocaleUtil().getCaption("pdf.student.speciality.code"), entrantSpeciality.getSpeciality().getCode(), table, 2, 3);
                        setTableForm1(getUILocaleUtil().getCaption("pdf.student.speciality.name"), entrantSpeciality.getSpeciality().getSpecName(), table, 2, 3);
                        setTableForm1(getUILocaleUtil().getCaption("pdf.student.payment.method"), NA, table, 2, 3);
                        filledEducation = true;
                    }
                }
            } else if (student.getCategory().getId().equals(STUDENT_CATEGORY.STUDENT_ID)) {
                if (se != null) {
                    setTableForm1(getUILocaleUtil().getCaption("pdf.student.studying.language"), se.getLanguage().getLangName(), table, 2, 3);
                    setTableForm1(getUILocaleUtil().getCaption("pdf.student.faculty"), se.getFaculty().getDeptName(), table, 2, 3);
                    setTableForm1(getUILocaleUtil().getCaption("pdf.student.speciality.code"), se.getSpeciality().getCode(), table, 2, 3);
                    setTableForm1(getUILocaleUtil().getCaption("pdf.student.speciality.name"), se.getSpeciality().getSpecName(), table, 2, 3);
                    setTableForm1(getUILocaleUtil().getCaption("pdf.student.payment.method"), se.getEducationType().getTypeName(), table, 2, 3);
                    filledEducation = true;
                }
            }
            if (!filledEducation) {
                setTableForm1("Язык обучения", NA, table, 2, 3);
                setTableForm1("факультет:", NA, table, 2, 3);
                setTableForm1("Шифр специальности:", NA, table, 2, 3);
                setTableForm1("Наименование специальности:", NA, table, 2, 3);
                setTableForm1("Форма оплаты :", NA, table, 2, 3);
            }

            addCell(table, tableColspan);

            setTableForm1(getUILocaleUtil().getCaption("pdf.student.surname"), student.getLastName(), table, 2, 4);
            setTableForm1(getUILocaleUtil().getCaption("pdf.student.firstname"), student.getFirstName(), table, 2, 4);
            setTableForm1(getUILocaleUtil().getCaption("pdf.student.lastname"), student.getMiddleName(), table, 2, 4);

            addCell(table, tableColspan);

            addCell(getUILocaleUtil().getCaption("pdf.student.passport"), table, tableColspan, boldFont);

            if (userPassport != null) {

                setTableForm1(getUILocaleUtil().getCaption("pdf.student.document.type"), userPassport.getPassportType().getTypeName(), table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.document.issue.date"), dateFormat.format(userPassport.getIssueDate()), table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.document.number"), userPassport.getDocumentNo().toString(), table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.document.issuer.name"), userPassport.getIssuerName().toString(), table, 2, 1);
            } else {
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.document.type"), NA, table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.document.issue.date"), NA, table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.document.number"), NA, table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.document.issuer.name"), NA, table, 2, 1);
            }

            addCell(table, tableColspan);

            addCell(getUILocaleUtil().getCaption("pdf.student.certificate"), table, tableColspan, boldFont);

            if (untCertificate != null) {

                setTableForm1(getUILocaleUtil().getCaption("pdf.student.certificate.number.ict"), untCertificate.getIct(), table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.certificate.number"), untCertificate.getDocumentNo(), table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.certificate.issue.date"), dateFormat.format(untCertificate.getIssueDate()), table, 2, 1);

            } else {
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.certificate.number.ict"), NA, table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.certificate.number"), NA, table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.certificate.issue.date"), NA, table, 2, 1);
            }
            addCell(table, 3);

            addCell(getUILocaleUtil().getCaption("pdf.student.certificate.estimates"), table, 2, boldFont);

            if (untCertificate != null && untr != null && untrArr != null && untrArr.length != 0) {
                for (UNT_CERT_SUBJECT tmp : untrArr) {
                    setTableForm1(tmp.getUntSubject().getSubjectName(), tmp.getRate().toString(), table, 1, 1);
                }
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.certificate.overall.rating"), untCertificate.getRate().toString(), table, 1, 1);

            } else {
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.certificate.language"), NA, table, 1, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.certificate.history"), NA, table, 1, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.certificate.math"), NA, table, 1, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.certificate.physics"), NA, table, 1, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.certificate.overall.rating"), NA, table, 1, 1);
            }

            addCell(getUILocaleUtil().getCaption("pdf.student.attestate"), table, tableColspan, boldFont);

            if (atestat != null) {
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.attestate.type"), atestat.getSchoolCertificateType().getTypeName(), table, 1, 2);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.attestate.number"), atestat.getDocumentNo(), table, 1, 2);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.attestate.issue.date"), dateFormat.format(atestat.getIssueDate()), table, 1, 2);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.attestate.success"), atestat.getGpa().toString(), table, 2, 1);
            } else {

                setTableForm1(getUILocaleUtil().getCaption("pdf.student.attestate.type"), NA, table, 1, 2);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.attestate.number"), NA, table, 1, 2);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.attestate.issue.date"), NA, table, 1, 2);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.attestate.success"), NA, table, 2, 1);
            }

            addCell(getUILocaleUtil().getCaption("pdf.student.diploma"), table, tableColspan, boldFont);

            if (diplom != null) {
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.diploma.number"), diplom.getDocumentNo(), table, 1, 2);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.diploma.issue.date"), dateFormat.format(diplom.getIssueDate()), table, 1, 2);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.diploma.success"), diplom.getGpa().toString(), table, 2, 1);
            } else {

                setTableForm1(getUILocaleUtil().getCaption("pdf.student.diploma.number"), NA, table, 1, 2);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.diploma.success"), NA, table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.diploma.issue.date"), NA, table, 1, 2);
            }
            addCell(table, 3);

            addCell(table, tableColspan);

            addCell(getUILocaleUtil().getCaption("pdf.student.personal.data"), table, tableColspan, boldFont);

            setTableForm1(getUILocaleUtil().getCaption("pdf.student.sex"), student.getSex().toString(), table, 2, 1);
            setTableForm1(getUILocaleUtil().getCaption("pdf.student.birth.date"), dateFormat.format(student.getBirthDate()), table, 1, 2);
            setTableForm1(getUILocaleUtil().getCaption("pdf.student.nationality"), student.getNationality().getNationName(), table, 2, 1);
            setTableForm1(getUILocaleUtil().getCaption("pdf.student.citizenship"), student.getCitizenship().getCountryName(), table, 1, 2);
            setTableForm1(getUILocaleUtil().getCaption("pdf.student.marital.status"), student.getMaritalStatus().getStatusName(), table, 2, 1);
            // setTableForm1("РНН", NA ,table, 1 , 2);
            addCell(table, 3);

            if (diplom != null) {
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.school.type"), diplom.getSchoolType().getTypeName(), table, 2, 1);
                addCell(table, 3);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.school.language"), diplom.getLanguage().getLangName(), table, 2, 1);
                if (atestat.getEndYear() != null)
                    setTableForm1(getUILocaleUtil().getCaption("pdf.student.school.graduate.date"), diplom.getEndYear().toString(), table, 2, 1);
                else
                    setTableForm1(getUILocaleUtil().getCaption("pdf.student.school.graduate.date"), NA, table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.school.name"), diplom.getSchoolName(), table, 2, 4);

                setTableForm1(getUILocaleUtil().getCaption("pdf.student.additional.info"), NA, table, 2, 4);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.dormitory.need"), NA, table, 2, 4);

                addCell(getUILocaleUtil().getCaption("pdf.student.institution.location"), table, tableColspan, boldFont);

                setTableForm1(getUILocaleUtil().getCaption("pdf.student.institution.region"), diplom.getSchoolRegion().getCountryName(), table, 2, 4);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.institution.district"), NA, table, 2, 4); // no
                // in
                // DB
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.institution.village"), NA, table, 2, 4); // no
                // in
                // DB
            } else if (atestat != null) {
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.school.type"), atestat.getSchoolType().getTypeName(), table, 2, 1);
                addCell(table, 3);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.school.language"), atestat.getLanguage().getLangName(), table, 2, 1);
                if (atestat.getEndYear() != null)
                    setTableForm1(getUILocaleUtil().getCaption("pdf.student.school.graduate.date"), atestat.getEndYear().toString(), table, 2, 1);
                else
                    setTableForm1(getUILocaleUtil().getCaption("pdf.student.school.graduate.date"), NA, table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.school.name"), atestat.getSchoolName(), table, 2, 4);

                setTableForm1(getUILocaleUtil().getCaption("pdf.student.additional.info"), NA, table, 2, 4);
                if (student.isNeedDorm())
                    setTableForm1(getUILocaleUtil().getCaption("pdf.student.dormitory.need"), getUILocaleUtil().getCaption("pdf.socialCategory.yes"), table, 2, 4);
                else
                    setTableForm1(getUILocaleUtil().getCaption("pdf.student.dormitory.need"), NA, table, 2, 4);

                addCell(getUILocaleUtil().getCaption("pdf.student.institution.location"), table, tableColspan, boldFont);

                setTableForm1(getUILocaleUtil().getCaption("pdf.student.institution.region"),
                        atestat.getSchoolRegion() == null ?
                                NA : atestat.getSchoolRegion().getCountryName(), table, 2, 4);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.institution.district"), NA, table, 2, 4);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.institution.village"), NA, table, 2, 4);
            } else {

                setTableForm1(getUILocaleUtil().getCaption("pdf.student.school.type"), NA, table, 2, 1);
                addCell(table, 3);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.school.language"), NA, table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.school.graduate.date"), NA, table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.school.name"), NA, table, 2, 4);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.additional.info"), NA, table, 2, 4);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.dormitory.need"), NA, table, 2, 4);
                addCell(getUILocaleUtil().getCaption("pdf.student.institution.location"), table, tableColspan, boldFont);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.institution.region"), NA, table, 2, 4);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.institution.district"), NA, table, 2, 4);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.institution.village"), NA, table, 2, 4);

            }

            addCell(table, tableColspan);

            // setTableForm1("Средний балл по всем предметам (аттестата/диплома)",
            // NA ,table, 5 ,1 ,Font.BOLD, Font.NORMAL , 11, 11);

            addCell(getUILocaleUtil().getCaption("pdf.student.language.knowledge"), table, tableColspan, boldFont);

            cell = new PdfPCell(new Phrase(getUILocaleUtil().getCaption("pdf.student.language.kazakh"), font));
            cell.setColspan(1);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(getUILocaleUtil().getCaption("pdf.student.language.russian"), font));
            cell.setColspan(1);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(getUILocaleUtil().getCaption("pdf.student.language.english"), font));
            cell.setColspan(1);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(getUILocaleUtil().getCaption("pdf.student.language.german"), font));
            cell.setColspan(1);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(getUILocaleUtil().getCaption("pdf.student.language.french"), font));
            cell.setColspan(1);
            table.addCell(cell);
            cell = new PdfPCell(new Phrase(getUILocaleUtil().getCaption("pdf.student.language.other"), font));
            cell.setColspan(1);
            table.addCell(cell);
            addCellWithBorderCenter(langArr[0], table, 1, font);
            addCellWithBorderCenter(langArr[1], table, 1, font);
            addCellWithBorderCenter(langArr[2], table, 1, font);
            addCellWithBorderCenter(langArr[3], table, 1, font);
            addCellWithBorderCenter(langArr[4], table, 1, font);
            addCellWithBorderCenter(langArr[5], table, 1, font);

            addCell(table, tableColspan);

            if (altynBelgi)
                setTableForm1(getUILocaleUtil().getCaption("student.gold.owner"), getUILocaleUtil().getCaption("student.yes"), table, 2, 1);
            else
                setTableForm1(getUILocaleUtil().getCaption("student.gold.owner"), NA, table, 2, 1);

            if (bolashak)
                setTableForm1(getUILocaleUtil().getCaption("student.apply.bolashak"), getUILocaleUtil().getCaption("student.yes"), table, 2, 1);
            else
                setTableForm1(getUILocaleUtil().getCaption("student.apply.bolashak"), NA, table, 2, 1);

            addCell(table, tableColspan);

            addCell(getUILocaleUtil().getCaption("student.apply.olimp"), table, tableColspan, boldFont);
            if (awards != null && awards.size() != 0) {

                for (V_USER_AWARD tmp : awards) {
                    addCell(tmp.getAward().getAwardName(), table, tableColspan, font);
                }

            } else {
                addCell(NA, table, tableColspan, font);
            }

            addCell(getUILocaleUtil().getCaption("student.military"), table, tableColspan, boldFont);
            if (militaryDoc != null) {
                setTableForm1(getUILocaleUtil().getCaption("student.military.attitude"), militaryDoc.getMilitaryStatus().getStatusName(), table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("student.military.region"), NA, table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("student.military.district"), NA, table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("student.military.village"), NA, table, 2, 1);

            } else {
                setTableForm1(getUILocaleUtil().getCaption("student.military.attitude"), NA, table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("student.military.region"), NA, table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("student.military.district"), NA, table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("student.military.village"), NA, table, 2, 1);
            }

            addCell(getUILocaleUtil().getCaption("pdf.student.better.right.document"), table, tableColspan, boldFont);
            if (preemptiveRight != null) {
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.better.right.document.type"), NA, table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.better.right.document.number"), preemptiveRight.getDocumentNo(), table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.better.right.document.descr"), preemptiveRight.getDescr(), table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.better.right.document.issue.date"), dateFormat.format(preemptiveRight.getIssueDate()), table, 2, 1);
            } else {

                setTableForm1(getUILocaleUtil().getCaption("pdf.student.better.right.document.type"), NA, table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.better.right.document.number"), NA, table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.better.right.document.descr"), NA, table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.better.right.document.issue.date"), NA, table, 2, 1);

            }

            if (disabilityDoc != null) {
                addCell(getUILocaleUtil().getCaption("pdf.student.invalid.document"), table, tableColspan, boldFont);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.invalid.document.number"), disabilityDoc.getDocumentNo(), table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.invalid.document.issuer.name"), disabilityDoc.getIssuerName(), table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.invalid.document.issue.date"), dateFormat.format(disabilityDoc.getIssueDate()), table, 2, 1);
                addCell(table, 3);
            }

            addCell(getUILocaleUtil().getCaption("pdf.repatriant"), table, tableColspan, boldFont);

            if (repatriateDoc != null) {

                setTableForm1(getUILocaleUtil().getCaption("pdf.repatriant.document.number"), repatriateDoc.getDocumentNo(), table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.repatriant.document.issuer.name"), repatriateDoc.getIssuerName(), table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.repatriant.document.issue.date"), dateFormat.format(repatriateDoc.getIssueDate()), table, 2, 1);
                addCell(table, 3);

            } else {
                setTableForm1(getUILocaleUtil().getCaption("pdf.repatriant.document.number"), NA, table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.repatriant.document.issuer.name"), NA, table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.repatriant.document.issue.date"), NA, table, 2, 1);
                addCell(table, 3);
            }

            addCell(getUILocaleUtil().getCaption("pdf.contract"), table, tableColspan, boldFont);
            if (contract != null) {
                setTableForm1(getUILocaleUtil().getCaption("pdf.contract.agreement"), contract.getContractType().getTypeName(), table, 2, 1);

                if (contract.getIssueDate() != null)
                    setTableForm1(getUILocaleUtil().getCaption("pdf.contract.date"), dateFormat.format(contract.getIssueDate()), table, 2, 1);
                else
                    setTableForm1(getUILocaleUtil().getCaption("pdf.contract.date"), NA, table, 2, 1);

                setTableForm1(getUILocaleUtil().getCaption("pdf.contract.payment"), contract.getPaymentType().getTypeName(), table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.contract.number"), contract.getDocumentNo(), table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.contract.organization.name"), contract.getOrganization().getOrganizationName(), table, 4, 2);
                setTableForm1(getUILocaleUtil().getCaption("pdf.contract.organization.location"), contract.getOrganization().getAddress(), table, 4, 2);
                setTableForm1(getUILocaleUtil().getCaption("pdf.contract.organization.telephone"), contract.getOrganization().getPhone(), table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.contract.organization.mobile.telephone"), contract.getOrganization().getPhoneMobile(), table, 2, 1);

            } else {

                setTableForm1(getUILocaleUtil().getCaption("pdf.contract.agreement"), NA, table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.contract.date"), NA, table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.contract.payment"), NA, table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.contract.number"), NA, table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.contract.organization.name"), NA, table, 4, 2);
                setTableForm1(getUILocaleUtil().getCaption("pdf.contract.organization.location"), NA, table, 4, 2);
                setTableForm1(getUILocaleUtil().getCaption("pdf.contract.organization.telephone"), NA, table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.contract.organization.mobile.telephone"), NA, table, 2, 1);
            }

            addCell(table, tableColspan);

            addCell(getUILocaleUtil().getCaption("pdf.student.address"), table, tableColspan, boldFont);

            if (userRegAddress != null) {
                if (userRegAddress.getCountry() != null)
                    setTableForm1(getUILocaleUtil().getCaption("pdf.student.address.location"), userRegAddress.getCountry().getCountryName(), table, 2, 1);
                else
                    setTableForm1(getUILocaleUtil().getCaption("pdf.student.address.location"), NA, table, 2, 1);
                if (userRegAddress.getPostalCode() != null)
                    setTableForm1(getUILocaleUtil().getCaption("pdf.student.address.post"), userRegAddress.getPostalCode(), table, 2, 1);
                else
                    setTableForm1(getUILocaleUtil().getCaption("pdf.student.address.post"), NA, table, 2, 1);
                if (userRegAddress.getRegion() != null)
                    setTableForm1(getUILocaleUtil().getCaption("pdf.student.address.region"), userRegAddress.getRegion().toString(), table, 2, 4);
                else
                    setTableForm1(getUILocaleUtil().getCaption("pdf.student.address.region"), NA, table, 2, 4);
                if (userRegAddress.getCity() != null)
                    setTableForm1(getUILocaleUtil().getCaption("pdf.student.address.district"), userRegAddress.getCity().getCountryName(), table, 2, 4);
                else
                    setTableForm1(getUILocaleUtil().getCaption("pdf.student.address.district"), NA, table, 2, 4);
                if (userRegAddress.getVillage() != null)
                    setTableForm1(getUILocaleUtil().getCaption("pdf.student.address.village"), userRegAddress.getVillage().getCountryName(), table, 2, 4);
                else
                    setTableForm1(getUILocaleUtil().getCaption("pdf.student.address.village"), NA, table, 2, 4);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.address.exact"), userRegAddress.getStreet(), table, 2, 4);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.address.telephone"), userRegAddress.getPhoneHome(), table, 2, 4);

            } else {

                setTableForm1(getUILocaleUtil().getCaption("pdf.student.address.location"), NA, table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.address.post"), NA, table, 2, 1);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.address.region"), NA, table, 2, 4);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.address.district"), NA, table, 2, 4);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.address.village"), NA, table, 2, 4);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.address.exact"), NA, table, 2, 4);
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.address.telephone"), NA, table, 2, 4);
            }

            if (userFactAddress != null) {
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.address.fact"), userFactAddress.getStreet(), table, 2, 4);
            } else {
                setTableForm1(getUILocaleUtil().getCaption("pdf.student.address.fact"), NA, table, 2, 4);
            }

            addCell(table, tableColspan);

            addCell(getUILocaleUtil().getCaption("pdf.parents"), table, tableColspan, boldFont);
            addCell(getUILocaleUtil().getCaption("pdf.parent.father"), table, tableColspan, font);
            if (father != null) {
                setTableForm1(getUILocaleUtil().getCaption("pdf.parent.father.fio"), father.getFio(), table, 2, 4);
                setTableForm1(getUILocaleUtil().getCaption("pdf.parent.father.home"), father.getStreet(), table, 2, 4);
                setTableForm1(getUILocaleUtil().getCaption("pdf.parent.father.work"), father.getWorkPlace(), table, 2, 4);
                setTableForm1(getUILocaleUtil().getCaption("pdf.parent.father.position"), father.getPostName(), table, 2, 4);
                setTableForm1(getUILocaleUtil().getCaption("pdf.parent.father.home.phone"), father.getPhoneHome(), table, 2, 4);
                setTableForm1(getUILocaleUtil().getCaption("pdf.parent.father.mobile.phone"), father.getPhoneMobile(), table, 2, 4);
                setTableForm1(getUILocaleUtil().getCaption("pdf.parent.father.email"), father.getEmail(), table, 2, 4);

            } else {
                addCell(NA, table, tableColspan, font);
            }

            addCell(getUILocaleUtil().getCaption("pdf.parent.mother"), table, tableColspan, font);
            if (mother != null) {
                setTableForm1(getUILocaleUtil().getCaption("pdf.parent.mother.fio"), mother.getFio(), table, 2, 4);
                setTableForm1(getUILocaleUtil().getCaption("pdf.parent.mother.home"), mother.getStreet(), table, 2, 4);
                setTableForm1(getUILocaleUtil().getCaption("pdf.parent.mother.work"), mother.getWorkPlace(), table, 2, 4);
                setTableForm1(getUILocaleUtil().getCaption("pdf.parent.mother.position"), mother.getPostName(), table, 2, 4);
                setTableForm1(getUILocaleUtil().getCaption("pdf.parent.mother.home.phone"), mother.getPhoneHome(), table, 2, 4);
                setTableForm1(getUILocaleUtil().getCaption("pdf.parent.mother.mobile.phone"), mother.getPhoneMobile(), table, 2, 4);
                setTableForm1(getUILocaleUtil().getCaption("pdf.parent.mother.email"), mother.getEmail(), table, 2, 4);

            } else {
                addCell(NA, table, tableColspan, font);
            }

            addCell(table, tableColspan);
            addCell(getUILocaleUtil().getCaption("pdf.socialCategory.socialCategory"), table, tableColspan, boldFont);

            if (tuscArr != null && tuscArr.length != 0) {

                for (USER_SOCIAL_CATEGORY tmp : tuscArr) {
                    addCell(tmp.getSocialCategory().getCategoryName(), table, tableColspan, font);
                }
            } else {
                addCell(getUILocaleUtil().getCaption("pdf.socialCategory.no"), table, tableColspan, font);
            }

            addCell(table, tableColspan);

            cell = new PdfPCell(new Phrase(getUILocaleUtil().getCaption("pdf.dateForm"), font));
            cell.setColspan(3);
            cell.setBorder(PdfPCell.NO_BORDER);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(getUILocaleUtil().getCaption("pdf.signature") + "______________", font));
            cell.setColspan(3);

            cell.setBorder(PdfPCell.NO_BORDER);
            table.addCell(cell);

            document.add(table);
            document.close();

            return new ByteArrayInputStream(baos.toByteArray());
        } catch (Exception ex) {
            ErrorUtils.LOG.error("Unable to download pdf file: ", ex);
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

    protected static UILocaleUtil getUILocaleUtil() {
        return AbstractWebUI.getInstance().getUILocaleUtil();
    }

    @SuppressWarnings("unused")
    private void setTableForm1(String a, String b, PdfPTable table, int colspan1, int colspan2, int aType, int bType, int aSize, int bSize) {

        Font f = new Font(timesNewRoman, aSize, aType);
        PdfPCell cell = new PdfPCell(new Phrase(a, f));
        cell.setColspan(colspan1);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);

        f = new Font(timesNewRoman, bSize, bType);
        cell = new PdfPCell(new Phrase(b, f));
        cell.setColspan(colspan2);

        // cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);
    }

    private void setTableForm1(String a, String b, PdfPTable table, int colspan1, int colspan2) {

        PdfPCell cell = new PdfPCell(new Phrase(a, font));
        cell.setColspan(colspan1);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);

        cell = new PdfPCell(new Phrase(b, font));
        cell.setColspan(colspan2);

        // cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);
    }

    @SuppressWarnings("unused")
    private String check(String word) {
        if (word == null) {
            return "\t\t\t";
        } else
            return word;
    }

    private void addCell(PdfPTable table, int colspan) {
        PdfPCell cell = new PdfPCell(new Phrase(" "));
        cell.setColspan(colspan);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);
    }

    private void addCell(String a, PdfPTable table, int colspan, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(a, font));
        cell.setColspan(colspan);
        cell.setBorder(PdfPCell.NO_BORDER);
        table.addCell(cell);
    }

    private void addCellWithBorderCenter(String a, PdfPTable table, int colspan, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(a, font));
        cell.setColspan(colspan);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    public StudentProfilePDFStream() {
    }

    public void setStudent(ID studentId) {
        this.studentId = studentId;
    }

    public void setUserPhotoBytes(byte[] userPhotoBytes) {
        this.userPhotoBytes = userPhotoBytes;
    }
}