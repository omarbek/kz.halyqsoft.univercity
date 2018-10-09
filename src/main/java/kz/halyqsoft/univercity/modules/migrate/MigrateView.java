package kz.halyqsoft.univercity.modules.migrate;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Upload;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.ENTRANT_SPECIALITY;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_EDUCATION;
import kz.halyqsoft.univercity.entity.beans.univercity.USER_ADDRESS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.modules.test.TestView;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Omarbek
 * @created on 08.10.2018
 */
public class MigrateView extends AbstractTaskView {

    private Upload fileUpload;
    private File file;

    public MigrateView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        HorizontalLayout loadHL = new HorizontalLayout();
        loadHL.setSpacing(true);

        FileReceiver fr = new FileReceiver();
        fileUpload = new Upload();
        fileUpload.setCaption(null);
        fileUpload.setButtonCaption(getUILocaleUtil().getCaption("select.file"));
        fileUpload.setReceiver(fr);
        fileUpload.addSucceededListener(fr);
        fileUpload.setImmediate(true);
        fileUpload.setVisible(true);
        loadHL.addComponent(fileUpload);

        Button debtButton = new Button();
        debtButton.setCaption(getUILocaleUtil().getCaption("uploadDebt"));
        debtButton.addClickListener(new DebtLoadDataListener());
        loadHL.addComponent(debtButton);

        getContent().addComponent(loadHL);
        getContent().setComponentAlignment(loadHL, Alignment.MIDDLE_CENTER);
    }

    private class FileReceiver implements Upload.Receiver, Upload.SucceededListener, Upload.FailedListener {

        @Override
        public void uploadFailed(Upload.FailedEvent ev) {
            LOG.error("Unable to upload the file: " + ev.getFilename());
            LOG.error(ev.getReason().toString());
        }

        @Override
        public void uploadSucceeded(Upload.SucceededEvent ev) {
        }

        @Override
        public OutputStream receiveUpload(String filename, String mimeType) {
            FileOutputStream fos = null;
            try {
//                file = new File("/tmp/files/" + filename);
                file = new File("C:/Users/Omarbek/IdeaProjects/kz.halyqsoft.univercity/tmp/files/" + filename);
                if (file.exists()) {
                    file.delete();
                }
                fos = new FileOutputStream(file);
            } catch (Exception ex) {
                LOG.error("Cannot upload file: " + filename, ex);
            }

            return fos;
        }
    }

    private class DebtLoadDataListener implements Button.ClickListener {

        @Override
        public void buttonClick(Button.ClickEvent ev) {
            try {
                Workbook wb = null;
                if (file.getName().substring(file.getName().lastIndexOf('.') + 1).equals("xlsx")) {
                    wb = new XSSFWorkbook(new FileInputStream(file));
                } else if (file.getName().substring(file.getName().lastIndexOf('.') + 1).equals("xls")) {
                    wb = new HSSFWorkbook(new FileInputStream(file));
                } else {
                    throw new Exception(getUILocaleUtil().getMessage("finance.file.error"));
                }
                Sheet sheet = wb.getSheetAt(0);
                int lastRowNo = sheet.getLastRowNum();

                int YEAR_2013 = 9;
                int SPEC_IT = 90;

                for (int i = 6; i <= lastRowNo; i++) {
                    Row row = sheet.getRow(i);
                    Cell cell = row.getCell(1);
                    String lastName = cell.getStringCellValue().trim();

                    cell = row.getCell(2);
                    String firstName = cell.getStringCellValue().trim();

                    cell = row.getCell(3);
                    String middleName = cell.getStringCellValue().trim();

                    cell=row.getCell(4);
                    String year=cell.getStringCellValue().trim().substring(0,1);
                    Integer studyYear=Integer.parseInt(year);

                    cell=row.getCell(5);
                    String specCode=cell.getStringCellValue().trim();
//                    QueryModel<>

                    USERS user = new USERS();
                    user.setFirstName(firstName);
                    user.setLastName(lastName);
                    user.setMiddleName(middleName);
                    user.setFirstNameEN(TestView.transliterate(firstName));
                    user.setLastNameEN(TestView.transliterate(lastName));
                    user.setMiddleNameEN(TestView.transliterate(middleName));
                    user.setTypeIndex(2);
                    user.setBirthDate(new Date());
                    user.setSex(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                            SEX.class, ID.valueOf(1)));
                    user.setMaritalStatus(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                            MARITAL_STATUS.class, ID.valueOf(3)));
                    user.setCitizenship(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                            COUNTRY.class, ID.valueOf(1)));
                    user.setNationality(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                            NATIONALITY.class, ID.valueOf(1)));
                    String code = CommonUtils.getCode("13");
                    user.setCode(code);
                    user.setLogin(code);
                    user.setPasswd("12345678");
                    user.setPhoneMobile("");
                    user.setLocked(false);
                    user.setLockReason(null);
                    user.setDeleted(false);
                    user.setCreated(new Date());
                    user.setCreatedBy("admin");
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(user);

                    STUDENT student = new STUDENT();
                    student.setId(user.getId());
                    student.setLevel(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                            LEVEL.class, ID.valueOf(1)));
                    student.setCategory(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                            STUDENT_CATEGORY.class, ID.valueOf(1)));
                    student.setAcademicStatus(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                            ACADEMIC_STATUS.class, ID.valueOf(1)));
                    student.setNeedDorm(false);
                    student.setEntranceYear(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                            ENTRANCE_YEAR.class, ID.valueOf(YEAR_2013)));
                    student.setDiplomaType(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                            STUDENT_DIPLOMA_TYPE.class, ID.valueOf(1)));
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(student);

                    ENTRANT_SPECIALITY entrantSpeciality = new ENTRANT_SPECIALITY();
                    entrantSpeciality.setStudent(student);
                    entrantSpeciality.setLanguage(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                            lookup(LANGUAGE.class, ID.valueOf(1)));
                    entrantSpeciality.setUniversity(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                            lookup(UNIVERSITY.class, ID.valueOf(1)));
                    entrantSpeciality.setSpeciality(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                            lookup(SPECIALITY.class, ID.valueOf(SPEC_IT)));
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(entrantSpeciality);

                    USER_ADDRESS userAddress = new USER_ADDRESS();
                    userAddress.setUser(user);
                    userAddress.setAddressType(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                            ADDRESS_TYPE.class, ID.valueOf(2)));
                    userAddress.setPostalCode("160000");
                    userAddress.setStreet("");
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(userAddress);

                    STUDENT_EDUCATION studentEducation=new STUDENT_EDUCATION();
                    studentEducation.setStudent(student);
//                    studentEducation.setFaculty(speciality.getDepartment().getParent());
//                    studentEducation.setChair(speciality.getDepartment());
//                    studentEducation.setSpeciality(speciality);
                    studentEducation.setStudyYear(SessionFacadeFactory.getSessionFacade(
                            CommonEntityFacadeBean.class).lookup(STUDY_YEAR.class, ID.valueOf(studyYear)));
                    studentEducation.setLanguage(SessionFacadeFactory.getSessionFacade(
                            CommonEntityFacadeBean.class).lookup(LANGUAGE.class, ID.valueOf(1)));
                    studentEducation.setEducationType(SessionFacadeFactory.getSessionFacade(
                            CommonEntityFacadeBean.class).lookup(STUDENT_EDUCATION_TYPE.class, ID.valueOf(1)));
                    DateFormat uriDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    studentEducation.setStatus(SessionFacadeFactory.getSessionFacade(
                            CommonEntityFacadeBean.class).lookup(STUDENT_STATUS.class, ID.valueOf(1)));
                    studentEducation.setCreated(new Date());

                    break;
                }
            } catch (Exception ex) {
                LOG.error("Unable to load students' debt: ", ex);
                Message.showError(ex.toString());
            } finally {
                file.delete();
            }
        }
    }
}
