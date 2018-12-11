package kz.halyqsoft.univercity.modules.practice;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.vaadin.server.FileDownloader;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.PRACTICE_INFORMATION;
import kz.halyqsoft.univercity.entity.beans.univercity.PRACTICE_STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ORGANIZATION;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_STUDENT;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.EmployeePdfCreator;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;

import java.io.ByteArrayOutputStream;
import java.util.*;
import java.util.List;


public class ReportDialog extends AbstractDialog {
    private Button generateBtn;
    private Button downloadBtn;
    FileDownloader fileDownloaderr = null;
    private String REPORT = getUILocaleUtil().getCaption("report");
    List<Entity> practiceInformations;

    public ReportDialog(List<Entity> practiceInformations) {
        this.practiceInformations = practiceInformations;

        GridLayout gridLayout = new GridLayout();
        gridLayout.setRows(1);
        gridLayout.setColumns(2);
        gridLayout.setSizeFull();
        gridLayout.setSpacing(true);

        TextArea fromTA = new TextArea(getUILocaleUtil().getCaption("report.from"));
        fromTA.setSizeFull();
        fromTA.setImmediate(true);
        fromTA.setRequired(true);
        fromTA.setWordwrap(true);
        fromTA.setRows(4);
        TextArea toTA = new TextArea(getUILocaleUtil().getCaption("report.to"));
        toTA.setImmediate(true);
        toTA.setRequired(true);
        toTA.setSizeFull();
        toTA.setWordwrap(true);
        toTA.setRows(4);
        TextArea messageTA = new TextArea(getUILocaleUtil().getCaption("message"));
        messageTA.setImmediate(true);
        messageTA.setRequired(true);
        messageTA.setSizeFull();
        messageTA.setWordwrap(true);
        messageTA.setRows(6);
        DateField dateField = new DateField(getUILocaleUtil().getCaption("date"));
        dateField.setImmediate(true);
        generateBtn = new Button(getUILocaleUtil().getCaption("generate"));
        generateBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                try {
                    createReport(fromTA.getValue(), toTA.getValue(), messageTA.getValue(), dateField.getValue());
                    downloadBtn.setEnabled(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        downloadBtn = new Button(getUILocaleUtil().getCaption("download"));
        downloadBtn.setEnabled(false);
        gridLayout.addComponent(fromTA);
        gridLayout.addComponent(toTA);

        getContent().setSizeFull();
        getContent().setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        getContent().addComponent(gridLayout);
        getContent().addComponent(messageTA);
        getContent().addComponent(dateField);
        getContent().setComponentAlignment(dateField, Alignment.MIDDLE_LEFT);

        HorizontalLayout hl = new HorizontalLayout();
        hl.setSizeFull();
        hl.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        hl.addComponent(generateBtn);
        hl.addComponent(downloadBtn);
        getContent().addComponent(hl);
        setWidth(50, Unit.PERCENTAGE);
        setHeight(50, Unit.PERCENTAGE);
        AbstractWebUI.getInstance().addWindow(this);
    }

    @Override
    protected String createTitle() {
        return REPORT;
    }

    public void createReport(String from, String to, String message, Date date) throws Exception {
        Document document = new Document();
        ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, byteArr);
        document.open();
        Chunk glue = new Chunk(new VerticalPositionMark());
        PdfPTable table = new PdfPTable(2);
        Paragraph fromP = new Paragraph(from + " \n " + CommonUtils.getFormattedDateWithoutTime(date), EmployeePdfCreator.getFont(14, Font.NORMAL));
        fromP.setAlignment(Element.ALIGN_CENTER);
        Paragraph toP = new Paragraph(to, EmployeePdfCreator.getFont(14, Font.NORMAL));
        toP.setAlignment(Element.ALIGN_CENTER);

        table.addCell(fromP);
        table.addCell(toP);
        document.add(table);
        document.add(new Paragraph("      ", EmployeePdfCreator.getFont(15, Font.NORMAL)));

        Paragraph messageP = new Paragraph(message, EmployeePdfCreator.getFont(14, Font.NORMAL));
        document.add(messageP);
        document.add(new Paragraph("      ", EmployeePdfCreator.getFont(15, Font.NORMAL)));

        for (int i = 0; i < practiceInformations.size(); i++) {
            for (int j = 0; j < practiceInformations.size(); j++) {
                if (((PRACTICE_INFORMATION) practiceInformations.get(i)).getGroups().getStudyYear().getStudyYear() > ((PRACTICE_INFORMATION) practiceInformations.get(j)).getGroups().getStudyYear().getStudyYear()) {
                    Collections.swap(practiceInformations, i, j);
                }
            }
        }


        for (Entity entity : practiceInformations) {
            PRACTICE_INFORMATION practiceInformation = (PRACTICE_INFORMATION) entity;
            List<PRACTICE_STUDENT> practiceStudents = new ArrayList<>();
            QueryModel<PRACTICE_STUDENT> practiceStudentQM = new QueryModel<>(PRACTICE_STUDENT.class);
            FromItem studentFI = practiceStudentQM.addJoin(EJoin.INNER_JOIN, "student_id", V_STUDENT.class, "id");
            practiceStudentQM.addWhere(studentFI, "group", ECriteria.EQUAL, practiceInformation.getGroups().getId());
            practiceStudents.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(practiceStudentQM));

            Map<ORGANIZATION, ArrayList<PRACTICE_STUDENT>> orgPracMap = new HashMap<>();
            for (PRACTICE_STUDENT practiceStudent : practiceStudents) {
                if (!orgPracMap.keySet().contains(practiceStudent.getOrganization())) {
                    orgPracMap.put(practiceStudent.getOrganization(), new ArrayList<PRACTICE_STUDENT>());
                }
                orgPracMap.get(practiceStudent.getOrganization()).add(practiceStudent);
            }

            Paragraph header = new Paragraph(practiceInformation.getGroups().getStudyYear().getStudyYear() + "-курс " +
                    practiceInformation.getGroups().getName(),
                    EmployeePdfCreator.getFont(12, Font.BOLDITALIC));
            document.add(header);
            for (ORGANIZATION organization : orgPracMap.keySet()) {
                header = new Paragraph(practiceInformation.getGroups().getSpeciality().getCode() + " - " +
                        practiceInformation.getGroups().getSpeciality().getSpecName() + " мамандығы бойынша",
                        EmployeePdfCreator.getFont(12, Font.BOLD));
                document.add(header);
                header = new Paragraph(practiceInformation.getGroups().getStudyYear().getStudyYear() + "-курс :" +
                        organization.getOrganizationName(),
                        EmployeePdfCreator.getFont(12, Font.BOLD));
                document.add(header);
                for (PRACTICE_STUDENT ps : orgPracMap.get(organization)) {
                    Paragraph student = new Paragraph(ps.getStudent().toString(), EmployeePdfCreator.getFont(12, Font.NORMAL));
                    document.add(student);
                }
                Paragraph footer = new Paragraph("Жетекші:" +
                        practiceInformation.getEmployee().getLastName() + " " +
                        practiceInformation.getEmployee().getFirstName().toUpperCase().charAt(0) + "." +
                        (practiceInformation.getEmployee().getMiddleName() != null ?
                                practiceInformation.getEmployee().getMiddleName().toUpperCase().charAt(0) : "") + " ",
                        EmployeePdfCreator.getFont(12, Font.BOLDITALIC));
                footer.setAlignment(Element.ALIGN_RIGHT);
                document.add(footer);

                document.add(new Paragraph("      ", EmployeePdfCreator.getFont(15, Font.NORMAL)));
            }
            document.add(new Paragraph("      ", EmployeePdfCreator.getFont(15, Font.NORMAL)));
        }

        document.add(new Paragraph("      ", EmployeePdfCreator.getFont(15, Font.NORMAL)));
        document.add(new Paragraph("      ", EmployeePdfCreator.getFont(15, Font.NORMAL)));
        Paragraph footer = new Paragraph("Кафедра меңгерушісі:                 ", EmployeePdfCreator.getFont(15, Font.BOLDITALIC));
        document.add(footer);
        footer = new Paragraph("Шығыс №_____________                                                               Кіріс № _____________", EmployeePdfCreator.getFont(13, Font.ITALIC));
        document.add(footer);
        footer = new Paragraph("«___» ________20___ж                                                                «___» ________20___ж", EmployeePdfCreator.getFont(13, Font.ITALIC));
        document.add(footer);
        document.close();
        if (fileDownloaderr == null) {
            try {
                fileDownloaderr = new FileDownloader(EmployeePdfCreator.getStreamResourceFromByte(byteArr.toByteArray(), REPORT + ".pdf"));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (fileDownloaderr != null) {
            }
        } else {
            fileDownloaderr.setFileDownloadResource(EmployeePdfCreator.getStreamResourceFromByte(byteArr.toByteArray(), REPORT + ".pdf"));
        }
        fileDownloaderr.extend(downloadBtn);
    }
}
