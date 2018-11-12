package kz.halyqsoft.univercity.utils;

import com.itextpdf.text.Font;
import com.vaadin.server.Sizeable;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import kz.halyqsoft.univercity.entity.beans.ROLES;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.USER_ROLES;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.facade.CommonIDFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.file.FileBean;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.AbstractSecureWebUI;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.locale.UILocaleUtil;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.field.filelist.FileListFieldModel;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.NoResultException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Omarbek
 * @created on 15.03.2018
 */
public class CommonUtils {

    public static final String PDF_MIME_TYPE = "application/pdf";

    public static final String DATETIME = "yyyy-MM-dd' 'HH:mm:ss";
    public static final String DATE = "dd.MM.yyyy";

    public static final Logger LOG = LoggerFactory.getLogger("ROOT");
    public static int currentYear = Calendar.getInstance().get(Calendar.YEAR);
    private static int sequenceForEmployee = 1;

    public static String getCurrentUserLogin() {
        return AbstractSecureWebUI.getInstance().getUsername();
    }

    public static USERS getCurrentUser() {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("login", getCurrentUserLogin());
            EMPLOYEE employee = getEmployee(params);
            if (employee != null) {
                return employee;
            }
            STUDENT student = getStudent(params);
            if (student != null) {
                return student;
            }
        } catch (Exception e) {
            CommonUtils.showMessageAndWriteLog("Unable to get user", e);
        }
        return null;
    }

    public static List<ROLES> getCurrentUserRolesList() {
        ArrayList<ROLES> roles = new ArrayList<>();
        QueryModel<USER_ROLES> userRolesQM = new QueryModel<>(USER_ROLES.class);
        userRolesQM.addWhere("user", ECriteria.EQUAL, getCurrentUser().getId());

        List<USER_ROLES> userRoles = new ArrayList<>();
        try {
            userRoles.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(userRolesQM));
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (USER_ROLES ur : userRoles) {
            roles.add(ur.getRole());
        }

        return roles;
    }

    private static STUDENT getStudent(Map<String, Object> params) {
        try {
            return (STUDENT) SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    getEntityByNamedQuery("STUDENT.getStudentByLogin", params);
        } catch (Exception ignored) {
        }
        return null;
    }

    public static EMPLOYEE getEmployee(Map<String, Object> params) {
        try {
            return (EMPLOYEE) SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    getEntityByNamedQuery("EMPLOYEE.getEmployeeByLogin", params);
        } catch (Exception ignored) {
        }
        return null;
    }

    public static String getCode(Integer count) {
        String code = String.valueOf(count);
        if (count < 10) {
            code = "000" + code;
        } else if (count < 100) {
            code = "00" + code;
        } else if (count < 1000) {
            code = "0" + code;
        }
        return code;
    }

    public static int getFontMap(String font) {
        Map<String, Integer> fontMap = new HashMap<>();
        fontMap.put("Bold", Font.BOLD);
        fontMap.put("Normal", Font.NORMAL);
        fontMap.put("Italic", Font.ITALIC);
        fontMap.put("Underline", Font.UNDERLINE);
        fontMap.put("BoldItalic", Font.BOLDITALIC);
        return fontMap.get(font);
    }

    private static String getCodeBuilder(Integer count) {
        String code = String.valueOf(count);
        StringBuilder codeSB = new StringBuilder();
        if (count < 10) {
            codeSB.append("000");
        } else if (count < 100) {
            codeSB.append("00");
        } else if (count < 1000) {
            codeSB.append("0");
        }
        codeSB.append(code);
        return codeSB.toString();
    }

    public static void addFiles(QueryModel<USER_DOCUMENT_FILE> udfQM, FileListFieldModel medicalCheckupFLFM) {
        try {
            List udfList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(udfQM);
            if (!udfList.isEmpty()) {
                for (Object o : udfList) {
                    Object[] oo = (Object[]) o;
                    FileBean fe = new FileBean(USER_DOCUMENT_FILE.class);
                    fe.setId(ID.valueOf((Long) oo[0]));
                    fe.setFileName((String) oo[1]);
                    fe.setNewFile(false);
                    medicalCheckupFLFM.getFileList().add(fe);
                }
            }
        } catch (Exception ex) {
            LOG.error("Unable to load education document copies: ", ex);
        }
    }

    public static void deleteFiles(FileListFieldModel flfm) {
        for (FileBean fe : flfm.getDeleteList()) {
            try {
                USER_DOCUMENT_FILE udf = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_DOCUMENT_FILE.class, fe.getId());
                udf.setDeleted(true);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(udf);
            } catch (Exception ex) {
                LOG.error("Unable to delete repatriate doc copy: ", ex);
            }
        }
    }

    public static UILocaleUtil getUILocaleUtil() {
        return AbstractWebUI.getInstance().getUILocaleUtil();
    }

    public static void showSavedNotification() {
        AbstractWebUI.getInstance().showNotificationInfo(getUILocaleUtil().getMessage("info.record.saved"));
    }

    public static void showMessageAndWriteLog(String message, Exception ex) {
        LOG.error(message + ": ", ex);
        Message.showError(ex.toString());
    }

    public static HorizontalLayout createButtonPanel() {
        HorizontalLayout buttonPanel = new HorizontalLayout();
        buttonPanel.setSpacing(true);
        buttonPanel.setWidthUndefined();
        buttonPanel.setImmediate(true);
        return buttonPanel;
    }

    public static Button createSaveButton() {
        Button save = new Button();
        save.setData(10);
        save.setWidth(120.0F, Sizeable.Unit.PIXELS);
        save.setIcon(new ThemeResource("img/button/ok.png"));
        save.addStyleName("save");
        save.setCaption(getUILocaleUtil().getCaption("save"));
        return save;
    }

    public static Button createCancelButton() {
        Button cancel = new Button();
        cancel.setData(11);
        cancel.setWidth(120.0F, Sizeable.Unit.PIXELS);
        cancel.setIcon(new ThemeResource("img/button/cancel.png"));
        cancel.addStyleName("cancel");
        cancel.setCaption(getUILocaleUtil().getCaption("cancel"));
        return cancel;
    }

    public static Button createRefreshButton() {
        Button refreshButton = new Button();
        refreshButton.setWidth(120.0F, Sizeable.Unit.PIXELS);
        refreshButton.setIcon(new ThemeResource("img/button/refresh.png"));
        refreshButton.setCaption(getUILocaleUtil().getCaption("update"));
        return refreshButton;
    }

    public static void setCards(FormModel baseDataFM) throws Exception {
        String sql = "SELECT id " +
                "FROM card " +
                "WHERE created = (SELECT max(card.created) " +
                "                 FROM card card LEFT " +
                "                   JOIN users usr ON usr.card_id = card.id " +
                "                 WHERE usr.card_id IS NULL)";
        Long cardId;
        try {
            cardId = (Long) SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(sql,
                    new HashMap<>());
        } catch (NoResultException e) {
            cardId = -1L;
        }

        FKFieldModel cardFM = (FKFieldModel) baseDataFM.getFieldModel("card");
        QueryModel cardQM = cardFM.getQueryModel();

        cardQM.addWhere("id", ECriteria.EQUAL, ID.valueOf(cardId));
    }

    public static String getLogin(String login) throws Exception {
        QueryModel<USERS> usersQM = new QueryModel<>(USERS.class);
        usersQM.addWhere("login", ECriteria.EQUAL, login);
        List<USERS> users = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(usersQM);
        if (!users.isEmpty()) {
            login = getLogin(login + sequenceForEmployee++);
        }
        return login;
    }

    public static String getFormattedDate(Date date) {
        DateFormat formatter = new SimpleDateFormat(DATETIME);
        return formatter.format(date);
    }

    public static String getTimeFromDate(Date date) {
        SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm:ss");
        String time = localDateFormat.format(date);
        return time;
    }

    public static String getFormattedDateWithoutTime(Date date) {
        DateFormat formatter = new SimpleDateFormat(DATE);
        return formatter.format(date);
    }

    public static String getCode(String beginYear) {
        String code = null;
        try {
            Integer usersCode = SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USERS_CODE").getId().intValue();
            if (usersCode < 10) {
                code = beginYear + "000" + usersCode;
            } else if (usersCode < 100) {
                code = beginYear + "00" + usersCode;
            } else if (usersCode < 1000) {
                code = beginYear + "0" + usersCode;
            } else if (usersCode < 10000) {
                code = beginYear + usersCode;
            } else if (usersCode < 100000) {
                code = beginYear + usersCode;
            } else {
                SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("S_USERS_CODE").
                        setId(BigInteger.valueOf(0));//TODO check
                code = getCode(beginYear);
            }
        } catch (Exception e) {
            e.printStackTrace();//TODO catch
        }
        return code;
    }

    public static SEMESTER_DATA getCurrentSemesterData() throws NoResultException {
        try {
            return (SEMESTER_DATA) SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
                    .getEntityByNamedQuery("SEMESTER_DATA.findCurrentSemesterData", Collections.EMPTY_MAP);
        } catch (Exception e) {
            return null;
        }
    }

    public static Label getSemesterIsGoingNowLabel() {
        Label semIsNotGoingNowLabel = new Label();
        semIsNotGoingNowLabel.setCaption(getUILocaleUtil().getMessage("semester.not.going.now"));
        semIsNotGoingNowLabel.setWidthUndefined();
        return semIsNotGoingNowLabel;
    }

    public static List<GROUPS> getGroupsByStream(STREAM stream) throws Exception {
        QueryModel<GROUPS> groupsQM = new QueryModel<>(GROUPS.class);
        FromItem streamGroupFI = groupsQM.addJoin(EJoin.INNER_JOIN, "id", STREAM_GROUP.class,
                "group");
        groupsQM.addWhere(streamGroupFI, "stream", ECriteria.EQUAL, stream.getId());
        groupsQM.addWhere("deleted", Boolean.FALSE);
        return SessionFacadeFactory.getSessionFacade(
                CommonEntityFacadeBean.class).
                lookup(groupsQM);
    }

    public static SEMESTER_DATA createSemesterDataBySemester(SEMESTER semester, CURRICULUM curriculum) throws Exception {
        SEMESTER_DATA sd;
        ENTRANCE_YEAR studyYear = curriculum.getEntranceYear();
        if (!semester.getId().equals(ID.valueOf(1)) && !semester.getId().equals(ID.valueOf(2))) {
            int beginYear = studyYear.getBeginYear();
            int endYear = studyYear.getEndYear();
            if (semester.getId().equals(ID.valueOf(3)) || semester.getId().equals(ID.valueOf(4))) {
                beginYear++;
                endYear++;
            } else if (semester.getId().equals(ID.valueOf(5)) || semester.getId().equals(ID.valueOf(6))) {
                beginYear += 2;
                endYear += 2;
            } else if (semester.getId().equals(ID.valueOf(7)) || semester.getId().equals(ID.valueOf(8))) {
                beginYear += 3;
                endYear += 3;
            }

            QueryModel<ENTRANCE_YEAR> syQM = new QueryModel<>(ENTRANCE_YEAR.class);
            syQM.addWhere("beginYear", ECriteria.EQUAL, beginYear);
            syQM.addWhereAnd("endYear", ECriteria.EQUAL, endYear);

            try {
                studyYear = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(syQM);
            } catch (NoResultException nrex) {
                studyYear = new ENTRANCE_YEAR();
                studyYear.setBeginYear(beginYear);
                studyYear.setEndYear(endYear);
                studyYear.setEntranceYear(beginYear + "-" + endYear);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(studyYear);
            }
        }

        SEMESTER_PERIOD sp = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(SEMESTER.class, semester.getId()).getSemesterPeriod();

        QueryModel<SEMESTER_DATA> sdQM = new QueryModel<>(SEMESTER_DATA.class);
        sdQM.addWhere("year", ECriteria.EQUAL, studyYear.getId());
        sdQM.addWhereAnd("semesterPeriod", ECriteria.EQUAL, sp.getId());

        try {
            sd = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(sdQM);
        } catch (NoResultException nrex) {
            sd = createSemesterData(studyYear, sp);
        }

        return sd;
    }

    private static SEMESTER_DATA createSemesterData(ENTRANCE_YEAR studyYear, SEMESTER_PERIOD sp) {
        SEMESTER_DATA sd;
        sd = new SEMESTER_DATA();
        sd.setYear(studyYear);
        sd.setSemesterPeriod(sp);

        Calendar c = Calendar.getInstance();
        c.clear();
        if (sp.getId().equals(ID.valueOf(1))) {
            c.set(Calendar.DAY_OF_MONTH, 20);
            c.set(Calendar.MONTH, Calendar.AUGUST);
            c.set(Calendar.YEAR, studyYear.getBeginYear());
            sd.setBeginDate(c.getTime());

            c.clear();
            c.set(Calendar.DAY_OF_MONTH, 31);
            c.set(Calendar.MONTH, Calendar.DECEMBER);
            c.set(Calendar.YEAR, studyYear.getBeginYear());
            sd.setEndDate(c.getTime());
        } else if (sp.getId().equals(ID.valueOf(2))) {
            c.set(Calendar.DAY_OF_MONTH, 10);
            c.set(Calendar.MONTH, Calendar.JANUARY);
            c.set(Calendar.YEAR, studyYear.getEndYear());
            sd.setBeginDate(c.getTime());

            c.clear();
            c.set(Calendar.DAY_OF_MONTH, 25);
            c.set(Calendar.MONTH, Calendar.MAY);
            c.set(Calendar.YEAR, studyYear.getEndYear());
            sd.setEndDate(c.getTime());
        }

        try {
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(sd);
        } catch (Exception e) {
            e.printStackTrace();//TODO catch
        }
        return sd;
    }

    public static boolean isCurrentUserHasAdminPrivileges() {
        List<USER_ROLES> userRoles = getCurrentUser().getUserRoles();
        for (USER_ROLES userRole : userRoles) {
            if (userRole.getId().getId().longValue() == 3) {
                return true;
            }
        }
        return false;
    }

    public static boolean isAdmin() {
        return getCurrentUser().getId().getId().intValue() == 1
                || getCurrentUser().getId().getId().intValue() == 2;
    }

    public static String getLanguage() {
        String lang = getUILocaleUtil().getCurrentLocale().toString().toLowerCase();
        if (lang.startsWith("en")) {
            return lang.substring(0, 2);
        }
        return lang.substring(3);
    }

    public static SEMESTER_DATA getSemesterDataBySemesterAndEntranceYear(SEMESTER semester,
                                                                         ENTRANCE_YEAR entranceYear) {
        SEMESTER_DATA semesterData = new SEMESTER_DATA();
        try {
            QueryModel<SEMESTER_DATA> semesterDataQM = new QueryModel<>(SEMESTER_DATA.class);
            FromItem yearFI = semesterDataQM.addJoin(EJoin.INNER_JOIN, "year", ENTRANCE_YEAR.class, "id");
            semesterDataQM.addWhere(yearFI, "beginYear", ECriteria.EQUAL, entranceYear.getBeginYear() +
                    semester.getStudyYear().getStudyYear() - 1);
            semesterDataQM.addWhere("semesterPeriod", ECriteria.EQUAL, semester.getSemesterPeriod().
                    getId());
            semesterData = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookupSingle(semesterDataQM);
        } catch (NoResultException e) {
            semesterData = createSemesterData(entranceYear, semester.getSemesterPeriod());
        } catch (Exception e) {
            e.printStackTrace();//TODO catch
        }
        return semesterData;
    }

    public static SEMESTER getSemesterBySemesterDataAndEntranceYear(SEMESTER_DATA semesterData,
                                                                    ENTRANCE_YEAR entranceYear) {
        SEMESTER semester = new SEMESTER();
        try {
            QueryModel<SEMESTER> semesterQM = new QueryModel<>(SEMESTER.class);
            semesterQM.addWhere("semesterPeriod", ECriteria.EQUAL, semesterData.getSemesterPeriod().getId());
            int year = semesterData.getYear().getBeginYear() - entranceYear.getBeginYear() + 1;
            semesterQM.addWhere("studyYear", ECriteria.EQUAL, ID.valueOf(year));
            semester = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(semesterQM);
        } catch (NoResultException e) {
            semester = null;
        } catch (Exception e) {
            e.printStackTrace();//TODO catch
        }
        return semester;
    }

    public static int getStudyYearByEntranceYear(ENTRANCE_YEAR entranceYear) {
        return getCurrentSemesterData().getYear().getBeginYear() - entranceYear.getBeginYear() + 1;
    }

    public static ENTRANCE_YEAR getEntranceYearByStudyYear(STUDY_YEAR studyYear) {
        ENTRANCE_YEAR entranceYear=new ENTRANCE_YEAR();
        try {
            QueryModel<ENTRANCE_YEAR> entranceYearQM = new QueryModel<>(ENTRANCE_YEAR.class);
            entranceYearQM.addWhere("beginYear", ECriteria.EQUAL,
                    CommonUtils.getCurrentSemesterData().getYear().getBeginYear() - (studyYear.getStudyYear() - 1));
            entranceYear = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(entranceYearQM);
        } catch (NoResultException e) {
            entranceYear = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return entranceYear;
    }
}
