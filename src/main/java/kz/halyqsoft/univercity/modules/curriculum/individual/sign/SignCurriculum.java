package kz.halyqsoft.univercity.modules.curriculum.individual.sign;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.REGISTRATION_SIGNATURE_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_DATA;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.enumeration.UserType;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VIndividualCurriculumPrint;
import kz.halyqsoft.univercity.filter.FStudentFilter;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.form.AbstractFormWidgetView;

import javax.persistence.NoResultException;
import java.util.*;

/**
 * @author Omarbek
 * @created 10.05.2017.
 */
@SuppressWarnings("serial")
public class SignCurriculum extends AbstractFormWidgetView {

    private static final int BEGIN_LARGE_ALPHABET = 65;

    private static final int END_LARGE_ALPHABET = 90;

    private static final int BEGIN_SMALL_ALPHABET = 97;

    private static final int END_SMALL_ALPHABET = 122;

    private static final int STUDENT_SIGNATURE_TYPE_ID = 1;

    private static final int ADVISOR_SIGNATURE_TYPE_ID = 2;

    private static final int DEPARTMENT_DIRECTOR_SIGNATURE_TYPE_ID = 3;

    private static final int DEPARTMENT_DIRECTOR_POST_ID = 25;

    private static final int DEAN_SIGNATURE_TYPE_ID = 4;

    private static final int DEAN_POST_ID = 8;

    private static final class Columns {

        final static String CODE_COLUMN = getUILocaleUtil().getEntityFieldLabel(VIndividualCurriculumPrint.class, "subjectCode");

        final static int CODE_COLUMN_WIDTH = 150;

        final static String NAME_COLUMN = getUILocaleUtil().getEntityFieldLabel(VIndividualCurriculumPrint.class, "subjectName");

        final static String STATUS_COLUMN = getUILocaleUtil().getEntityFieldLabel(VIndividualCurriculumPrint.class, "subjectStatus");

        final static int STATUS_COLUMN_WIDTH = 100;

        final static String CYCLE_COLUMN = getUILocaleUtil().getEntityFieldLabel(VIndividualCurriculumPrint.class, "cycleShortName");

        final static int CYCLE_COLUMN_WIDTH = 300;

        final static String CREDIT_COLUMN = getUILocaleUtil().getEntityFieldLabel(VIndividualCurriculumPrint.class, "credit");

        final static int CREDIT_COLUMN_WIDTH = 100;

        final static String TEACHER_COLUMN = getUILocaleUtil().getEntityFieldLabel(VIndividualCurriculumPrint.class, "teacherFIO");

        final static int TEACHER_COLUMN_WIDTH = 300;
    }

    private final USERS currentUser;

    private final FStudentFilter filter;

    private final SEMESTER_DATA currentSemesterData = CommonUtils.getCurrentSemesterData();

    private final REGISTRATION_SIGNATURE_TYPE registrationSignatureType;

    private final STUDENT_EDUCATION lastEducation;

    private Button signButton;

    private Button cancelSignButton;

    public SignCurriculum(USERS currentUser, STUDENT student, FStudentFilter filter) {
        this.filter = filter;
        this.currentUser = currentUser;
        this.registrationSignatureType = getRegistrationSignatureType();
        this.lastEducation = student.getLastEducation();

        if (UserType.getUserTypeByIndex(Integer.toString(currentUser.getTypeIndex())).equals(UserType.STUDENT)) {
            setBackButtonVisible(false);
        }

        Set<VIndividualCurriculumPrint> resultSchedule = getResultSchedule();
        if (resultSchedule.isEmpty()) {
            createErrorPanel();
        } else {
            initContent(resultSchedule);
            verify();
        }
    }

    private void createErrorPanel() {
        Panel errorPanel = new Panel(getUILocaleUtil().getMessage("curriculum.not.filled"));
        errorPanel.setIcon(new ThemeResource("img/info.png"));
        errorPanel.addStyleName("info");
        errorPanel.setWidth(100, Unit.PERCENTAGE);
        getContent().addComponent(errorPanel);
        getContent().setComponentAlignment(errorPanel, Alignment.TOP_CENTER);
    }

    private void initContent(Set<VIndividualCurriculumPrint> resultSchedule) {
        Table table = createTable(resultSchedule);
        getContent().addComponent(table);

        signButton = createSignButton();
        cancelSignButton = createCancelSignButton();

        HorizontalLayout buttonPanel = new HorizontalLayout();
        buttonPanel.setSpacing(true);
        buttonPanel.setWidthUndefined();
        buttonPanel.addComponents(signButton, cancelSignButton);

        getContent().addComponent(buttonPanel);
        getContent().setComponentAlignment(buttonPanel, Alignment.BOTTOM_RIGHT);
    }

    private REGISTRATION_SIGNATURE_TYPE getRegistrationSignatureType() {
        try {
            if (UserType.getUserTypeByIndex(Integer.toString(currentUser.getTypeIndex())).equals(UserType.STUDENT)) {
                return SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(REGISTRATION_SIGNATURE_TYPE.class, ID.valueOf(STUDENT_SIGNATURE_TYPE_ID));
            }
            Set<EMPLOYEE_DEPT> employeeDepts = ((EMPLOYEE) currentUser).getEmployeeDepts();
            for (EMPLOYEE_DEPT employeeDept : employeeDepts) {
                if (employeeDept.isAdviser()) {
                    return SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(REGISTRATION_SIGNATURE_TYPE.class, ID.valueOf(ADVISOR_SIGNATURE_TYPE_ID));
                }
            }
            for (EMPLOYEE_DEPT employeeDept : employeeDepts) {
                switch (employeeDept.getPost().getId().getId().intValue()) {
                    case DEPARTMENT_DIRECTOR_POST_ID:
                        return SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(REGISTRATION_SIGNATURE_TYPE.class, ID.valueOf(DEPARTMENT_DIRECTOR_SIGNATURE_TYPE_ID));
                    case DEAN_POST_ID:
                        return SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(REGISTRATION_SIGNATURE_TYPE.class, ID.valueOf(DEAN_SIGNATURE_TYPE_ID));
                }
            }
        } catch (Exception e) {
            LOG.error("Ошибка получения типа сигнатуры", e);
        }
        return null;
    }

    private Table createTable(Set<VIndividualCurriculumPrint> resultSchedule) {
        Table table = new Table();
        table.setSortEnabled(false);
        table.setWidth(100, Unit.PERCENTAGE);
        table.addContainerProperty(Columns.CODE_COLUMN, String.class, null);
        table.setColumnWidth(Columns.CODE_COLUMN, Columns.CODE_COLUMN_WIDTH);
        table.addContainerProperty(Columns.NAME_COLUMN, String.class, null);
        table.addContainerProperty(Columns.STATUS_COLUMN, Character.class, null);
        table.setColumnWidth(Columns.STATUS_COLUMN, Columns.STATUS_COLUMN_WIDTH);
        table.addContainerProperty(Columns.CYCLE_COLUMN, String.class, null);
        table.setColumnWidth(Columns.CYCLE_COLUMN, Columns.CYCLE_COLUMN_WIDTH);
        table.addContainerProperty(Columns.CREDIT_COLUMN, Integer.class, null);
        table.setColumnWidth(Columns.CREDIT_COLUMN, Columns.CREDIT_COLUMN_WIDTH);
        table.addContainerProperty(Columns.TEACHER_COLUMN, String.class, null);
        table.setColumnWidth(Columns.TEACHER_COLUMN, Columns.TEACHER_COLUMN_WIDTH);
        for (VIndividualCurriculumPrint item : resultSchedule) {
            table.addItem(new Object[]{ item.getSubjectNameRU(), item.getSubjectStatus(), item.getCycleShortName(), item.getCredit(), item.getTeacherFIO()}, item.getRecordNo());
        }
        return table;
    }

    private Set<VIndividualCurriculumPrint> getResultSchedule() {
        Set<VIndividualCurriculumPrint> resultSchedule = new HashSet<>();
        Set<STUDENT_SCHEDULE> studentSchedules = lastEducation.getStudentSchedules();
        int count = 0;
        for (STUDENT_SCHEDULE studentSchedule : studentSchedules) {
            SCHEDULE_DETAIL scheduleDetail = studentSchedule.getScheduleDetail();
            if (scheduleDetail.getSchedule().getSemesterData().equals(currentSemesterData)) {
                count++;
                VIndividualCurriculumPrint item = new VIndividualCurriculumPrint();
                item.setRecordNo(count);
                SUBJECT subject = scheduleDetail.getSubject().getSubject();
                item.setSubjectNameRU(subject.getNameRU());
                item.setSubjectStatus(subject.isMandatory() ? 'A' : 'B');
                item.setCycleShortName(subject.getSubjectCycle().getCycleShortName());
                item.setCredit(subject.getCreditability().getCredit());
                EMPLOYEE teacher = scheduleDetail.getTeacher();
                String teacherMiddleName = teacher.getMiddleName().trim();
                char firstCharTeacherMiddleName = Character.MIN_VALUE;
                if (!teacherMiddleName.isEmpty()) {
                    firstCharTeacherMiddleName = teacherMiddleName.substring(0, 1).charAt(0);
                }
                if ((int) firstCharTeacherMiddleName >= BEGIN_LARGE_ALPHABET && (int) firstCharTeacherMiddleName <= END_LARGE_ALPHABET || (int) firstCharTeacherMiddleName >= BEGIN_SMALL_ALPHABET && (int) firstCharTeacherMiddleName <= END_SMALL_ALPHABET) {
                    teacherMiddleName = String.valueOf(firstCharTeacherMiddleName).concat(".");
                } else {
                    teacherMiddleName = "";
                }
                String teacherFio = teacher.getLastName().concat(" ").concat(teacher.getFirstName().substring(0, 1).concat(". ")).concat(teacherMiddleName).trim();
                item.setTeacherFIO(teacherFio);
                resultSchedule.add(item);
            }
        }
        return resultSchedule;
    }

    private Button createSignButton() {
        Button button = new Button(getUILocaleUtil().getCaption("button.sign"));
        button.setIcon(new ThemeResource("img/button/ok.png"));
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                sign();
            }
        });
        return button;
    }

    private void sign() {
        REGISTRATION_SIGNATURE signature = new REGISTRATION_SIGNATURE();
        signature.setSemesterData(currentSemesterData);
        signature.setStudentEducation(lastEducation);
        signature.setSignUser(currentUser);
        signature.setSignatureType(registrationSignatureType);
        signature.setSignDate(new Date());
        try {
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(signature);
        } catch (Exception e) {
            LOG.error("Ошибка подписи", e);
        }
        verify();
    }

    private Button createCancelSignButton() {
        Button button = new Button(getUILocaleUtil().getCaption("button.cancel_sign"));
        button.setIcon(new ThemeResource("img/button/cancel.png"));
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                cancelSign();
            }
        });
        return button;
    }

    private void cancelSign() {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("semesterData", currentSemesterData);
            params.put("studentEducation", lastEducation);
            params.put("signUser", currentUser);
            REGISTRATION_SIGNATURE signature = (REGISTRATION_SIGNATURE) SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).getEntityByNamedQuery("REGISTRATION_SIGNATURE.find", params);
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(signature);
        } catch (Exception e) {
            LOG.error("Ошибка отмены подписи", e);
        }
        verify();
    }

    private void verify() {
        if (registrationSignatureType == null) {
            signButton.setEnabled(false);
            cancelSignButton.setEnabled(false);
        } else {
            try {
                Map<String, Object> params = new HashMap<>();
                params.put("semesterData", currentSemesterData);
                params.put("studentEducation", lastEducation);
                params.put("signUser", currentUser);
                REGISTRATION_SIGNATURE signature = null;
                try {
                    signature = (REGISTRATION_SIGNATURE) SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).getEntityByNamedQuery("REGISTRATION_SIGNATURE.find", params);
                } catch (NoResultException ignored) {
                }
                if (signature == null) {
                    signButton.setEnabled(true);
                    cancelSignButton.setEnabled(false);
                } else {
                    signButton.setEnabled(false);
                    cancelSignButton.setEnabled(true);
                }
            } catch (Exception e) {
                LOG.error("Ошибка верификации подписи", e);
                signButton.setEnabled(false);
                cancelSignButton.setEnabled(false);
            }
        }
    }

    @Override
    protected AbstractTaskView getParentView() {
        try {
            return new StudentList(null, currentUser, filter);//TODO
        } catch (Exception e) {
            e.printStackTrace();//TODO catch
        }
        return null;
    }

    @Override
    public String getViewName() {
        return "SignCurriculum";
    }

    @Override
    protected String getViewTitle(Locale locale) {
        return getUILocaleUtil().getCaption("curriculum.schedule");
    }
}
