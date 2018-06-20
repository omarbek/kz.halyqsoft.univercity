package kz.halyqsoft.univercity.modules.curriculum.working.semester;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import kz.halyqsoft.univercity.entity.beans.univercity.CURRICULUM;
import kz.halyqsoft.univercity.entity.beans.univercity.CURRICULUM_DETAIL;
import kz.halyqsoft.univercity.entity.beans.univercity.ELECTIVE_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.SEMESTER_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_CURRICULUM_DETAIL;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_ELECTIVE_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_ELECTIVE_SUBJECT_LABEL;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_SUBJECT_SELECT;
import kz.halyqsoft.univercity.modules.curriculum.working.AbstractCurriculumPanel;
import kz.halyqsoft.univercity.modules.curriculum.working.CurriculumView;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.dialog.AbstractYesButtonListener;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.FormWidgetDialog;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.footer.EColumnFooterType;
import org.r3a.common.vaadin.widget.grid.footer.IntegerColumnFooterModel;
import org.r3a.common.vaadin.widget.grid.footer.StringColumnFooterModel;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;

import javax.persistence.NoResultException;
import java.math.BigDecimal;
import java.util.*;

/**
 * @author Omarbek Dinassil
 * @created Feb 19, 2016 1:37:36 PM
 */
@SuppressWarnings({"serial", "unchecked"})
public final class SemesterDetailPanel extends AbstractCurriculumPanel implements EntityListener {

    private static final ID MULTI_CYCLE_ID = ID.valueOf(4);
    private final SEMESTER semester;
    private CURRICULUM curriculum;
    private GridWidget grid;
    private GridWidget electiveGrid;
    private SubjectSelectDialog subjectSelectDlg;
    private ElectiveSubjectSelectDialog electiveSubjectSelectDlg;
    private FormWidgetDialog addElectiveFWD;
    private CreditCycleSumPanel cycleSumPanel;

    public SemesterDetailPanel(CurriculumView parentView, SEMESTER semester) {
        super(parentView);
        this.semester = semester;
    }

    @Override
    public CURRICULUM getCurriculum() {
        return curriculum;
    }

    @Override
    public void setCurriculum(CURRICULUM curriculum) {
        this.curriculum = curriculum;
    }

    public CreditCycleSumPanel getCycleSumPanel() {
        return cycleSumPanel;
    }

    public void setCycleSumPanel(CreditCycleSumPanel cycleSumPanel) {
        this.cycleSumPanel = cycleSumPanel;
    }

    public SEMESTER getSemester() {
        return semester;
    }

    @Override
    public void initPanel() {
        String titleResource = "semester." + semester.getId().toString();

		/* Mandatory subjects */
        grid = new GridWidget(V_CURRICULUM_DETAIL.class);
        grid.setButtonVisible(AbstractToolbar.ADD_BUTTON, false);
        grid.setButtonVisible(AbstractToolbar.EDIT_BUTTON, false);
        grid.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
        grid.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, false);

        Button add = new Button();
        add.setWidth(120, Unit.PIXELS);
        add.setIcon(new ThemeResource("img/button/new.png"));
        add.addClickListener(new AddListener());
        int i = grid.addButtonFirst(add);
        grid.setButtonDescription(i, "new");

        Button addElective = new Button();
        addElective.setWidth(160, Unit.PIXELS);
        addElective.setIcon(new ThemeResource("img/button/add.png"));
        addElective.addClickListener(new AddElectiveListener());
        i = grid.addButtonLast(addElective);
        grid.setButtonDescription(i, "add.elective");
        grid.setButtonStyle(i, "add-elective");

        grid.addEntityListener(this);
        DBGridModel gm = (DBGridModel) grid.getWidgetModel();
        gm.setHeightMode(HeightMode.ROW);
        gm.setHeightByRows(8);
        gm.setTitleResource(titleResource);
        gm.setDeferredCreate(false);
        gm.setDeferredDelete(false);
        gm.setFooterVisible(true);

        StringColumnFooterModel nameFooter = new StringColumnFooterModel();
        nameFooter.setFooterType(EColumnFooterType.CAPTION);
        nameFooter.setColumnName("subjectName");
        nameFooter.setValue(getUILocaleUtil().getCaption("total"));
        gm.addFooterModel(nameFooter);

        IntegerColumnFooterModel creditFooter = new IntegerColumnFooterModel();
        creditFooter.setFooterType(EColumnFooterType.SUM);
        creditFooter.setColumnName("credit");
        gm.addFooterModel(creditFooter);

        ID curriculumId = (curriculum != null && curriculum.getId() != null) ? curriculum.getId() : ID.valueOf(-1);
        QueryModel qm = gm.getQueryModel();
        qm.addWhere("curriculum", ECriteria.EQUAL, curriculumId);
        qm.addWhereAnd("semester", ECriteria.EQUAL, semester.getId());

        getContent().addComponent(grid);

		/* Elective subjects */
        electiveGrid = new GridWidget(V_ELECTIVE_SUBJECT.class);
        electiveGrid.setButtonVisible(AbstractToolbar.ADD_BUTTON, false);
        electiveGrid.setButtonVisible(AbstractToolbar.EDIT_BUTTON, false);
        electiveGrid.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
        electiveGrid.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, false);

        Button addElectiveSubject = new Button();
        addElectiveSubject.setWidth(120, Unit.PIXELS);
        addElectiveSubject.setIcon(new ThemeResource("img/button/new.png"));
        addElectiveSubject.addClickListener(new AddElectiveSubjectListener());
        i = electiveGrid.addButtonFirst(addElectiveSubject);
        electiveGrid.setButtonDescription(i, "new");

        electiveGrid.addEntityListener(this);
        DBGridModel electiveGridGM = (DBGridModel) electiveGrid.getWidgetModel();
        electiveGridGM.setHeightMode(HeightMode.ROW);
        electiveGridGM.setHeightByRows(8);
        electiveGridGM.setDeferredCreate(false);
        electiveGridGM.setDeferredDelete(false);
        electiveGridGM.setFooterVisible(true);

        StringColumnFooterModel electiveGridNameFooter = new StringColumnFooterModel();
        electiveGridNameFooter.setFooterType(EColumnFooterType.CAPTION);
        electiveGridNameFooter.setColumnName("subjectName");
        electiveGridNameFooter.setValue(getUILocaleUtil().getCaption("total"));
        electiveGridGM.addFooterModel(electiveGridNameFooter);

        IntegerColumnFooterModel electiveGridCreditFooter = new IntegerColumnFooterModel();
        electiveGridCreditFooter.setFooterType(EColumnFooterType.SUM);
        electiveGridCreditFooter.setColumnName("credit");
        electiveGridGM.addFooterModel(electiveGridCreditFooter);

        QueryModel electiveGridQM = electiveGridGM.getQueryModel();
        electiveGridQM.addWhere("curriculum", ECriteria.EQUAL, curriculumId);
        electiveGridQM.addWhereAnd("semester", ECriteria.EQUAL, semester.getId());

        getContent().addComponent(electiveGrid);
    }

    @Override
    public void refresh() throws Exception {
        refreshMandatory();
        refreshElective();
    }

    private void refreshMandatory() throws Exception {
        ID curriculumId = (curriculum != null && curriculum.getId() != null) ? curriculum.getId() : ID.valueOf(-1);
        QueryModel qm = ((DBGridModel) grid.getWidgetModel()).getQueryModel();
        qm.addWhere("curriculum", ECriteria.EQUAL, curriculumId);
        qm.addWhere("deleted", Boolean.FALSE);

        grid.getWidgetModel().setReadOnly(curriculum.getCurriculumStatus().getId().equals(ID.valueOf(3)));
        grid.refresh();
        getParentView().showErrorLabel(curriculumId.equals(ID.valueOf(-1)));
        getParentView().setTotalCreditSum();
    }

    private void refreshElective() throws Exception {
        ID curriculumId = (curriculum != null && curriculum.getId() != null) ? curriculum.getId() : ID.valueOf(-1);
        QueryModel qm = ((DBGridModel) electiveGrid.getWidgetModel()).getQueryModel();
        qm.addWhere("curriculum", ECriteria.EQUAL, curriculumId);
        qm.addWhere("deleted", Boolean.FALSE);

        //		if (curriculum.getCurriculumStatus().getId().equals(ID.valueOf(3))) {
        //			ENTRANCE_YEAR studyYear = getStudyYear();
        //			SEMESTER_PERIOD sp = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(SEMESTER.class, semester.getId()).getSemesterPeriod();
        //			ID itemId = null;
        //			ID acfID = ID.valueOf(3);
        //			T_DEPARTMENT faculty = curriculum.getSpeciality().getDepartment().getParent();
        //			if (faculty.getId().equals(ID.valueOf(10))) {
        //				acfID = ID.valueOf(1);
        //				if (sp.getId().equals(ID.valueOf(1))) {
        //					itemId = ID.valueOf(157);
        //				} else {
        //					itemId = ID.valueOf(179);
        //				}
        //			} else if (faculty.getId().equals(ID.valueOf(14))) {
        //				acfID = ID.valueOf(2);
        //				if (sp.getId().equals(ID.valueOf(1))) {
        //					itemId = ID.valueOf(144);
        //				} else {
        //					itemId = ID.valueOf(121);
        //				}
        //			} else {
        //				if (sp.getId().equals(ID.valueOf(1))) {
        //					itemId = ID.valueOf(38);
        //				} else {
        //					itemId = ID.valueOf(74);
        //				}
        //			}
        //
        //			Map<Integer, Object> params = new HashMap<Integer, Object>();
        //			params.put(1, studyYear.getId().getId());
        //			params.put(2, acfID.getId());
        //			params.put(3, itemId.getId());
        //			String sql = "select a.DATE1 from ACADEMIC_CALENDAR_DETAIL a inner join ACADEMIC_CALENDAR b on a.ACADEMIC_CALENDAR_ID = b.ID where b.YEAR_ID = ?1 and b.FACULTY_ID = ?2 and a.ITEM_ID = ?3";
        //
        //			try {
        //				Object o = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(sql, params);
        //				Date regDate = (Date)o;
        //				Date today = new Date();
        //				if (today.before(regDate)) {
        //					electiveGrid.setButtonVisible(AbstractToolbar.DELETE_BUTTON, false);
        //				} else {
        //					electiveGrid.getWidgetModel().setReadOnly(true);
        //				}
        //			} catch (NoResultException nrex) {
        //				electiveGrid.setButtonVisible(AbstractToolbar.DELETE_BUTTON, false);
        //			}
        //		}
        electiveGrid.getWidgetModel().setReadOnly(curriculum.getCurriculumStatus().getId().equals(ID.valueOf(3)));
        electiveGrid.refresh();
        getParentView().showErrorLabel(curriculumId.equals(ID.valueOf(-1)));
        getParentView().setTotalCreditSum();
    }

    public void setReadOnlyDetail(boolean readOnly) {
        grid.getWidgetModel().setReadOnly(readOnly);
        electiveGrid.getWidgetModel().setReadOnly(readOnly);
        try {
            refresh();
        } catch (Exception ex) {
            LOG.error("Unable to set read only status: ", ex);
            Message.showError(ex.toString());
        }
    }

    public void checkForConform() throws Exception {
        if (isCheckSemester()) {
            if (getTotalCredit() == 0) {
                String message = String.format(
                        getUILocaleUtil().getMessage("no.subject.for.semester"), getSemester().getId().toString());
                throw new Exception(message);
            }
        }
    }

    public void checkForApprove() throws Exception {
        if (isCheckSemester()) {
            int totalApproved = getTotalApprovedElectiveCredit();
            int totalSelected = getTotalSelectedElectiveCredit();
            if ((totalApproved - totalSelected) != 0) {
                throw new Exception(String.format(getUILocaleUtil()
                        .getMessage("elective.subject.configured.incorrectly"), semester.getId().toString()));
            }
        }
    }

    private boolean isCheckSemester() {
        return semester.getId().getId().intValue() < 8;
    }

    public void approve() throws Exception {
        SEMESTER_DATA sd = getOrCreateSemesterData();

		/* Mandatory subjects only. Approve only unsaved subjects */
        String sql = "select a.* from SUBJECT a inner join CURRICULUM_DETAIL b on a.ID = b.SUBJECT_ID and b.CURRICULUM_ID = ?1 and b.SEMESTER_ID = ?2 and b.DELETED = ?3 where not exists (select 1 from SEMESTER_SUBJECT c where a.ID = c.SUBJECT_ID and c.SEMESTER_DATA_ID = ?4)";
        Map<Integer, Object> params = new HashMap<Integer, Object>(4);
        params.put(1, curriculum.getId().getId());
        params.put(2, semester.getId().getId());
        params.put(3, Boolean.FALSE);
        params.put(4, sd.getId().getId());
        List<SUBJECT> subjectList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
                .lookup(sql, params, SUBJECT.class);
        if (!subjectList.isEmpty()) {
            List<SEMESTER_SUBJECT> newList = new ArrayList<SEMESTER_SUBJECT>(subjectList.size());
            for (SUBJECT s : subjectList) {
                SEMESTER_SUBJECT ss = new SEMESTER_SUBJECT();
                ss.setSemesterData(sd);
                ss.setSubject(s);
                newList.add(ss);
            }

            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(newList);
        }
    }

    private SEMESTER_DATA getOrCreateSemesterData() throws Exception {
        SEMESTER_DATA sd = null;
        ENTRANCE_YEAR studyYear = null;
        try {
            studyYear = getStudyYear();
        } catch (NoResultException nrex) {
            if (!semester.getId().equals(ID.valueOf(1)) && !semester.getId().equals(ID.valueOf(2))) {
                int beginYear = curriculum.getEntranceYear().getBeginYear();
                int endYear = curriculum.getEntranceYear().getEndYear();
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

                studyYear = new ENTRANCE_YEAR();
                studyYear.setBeginYear(beginYear);
                studyYear.setEndYear(endYear);
                studyYear.setEntranceYear(beginYear + "-" + endYear);

                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(studyYear);
            }
        }

        SEMESTER_PERIOD sp = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
                .lookup(SEMESTER.class, semester.getId()).getSemesterPeriod();

        QueryModel<SEMESTER_DATA> sdQM = new QueryModel<SEMESTER_DATA>(SEMESTER_DATA.class);
        sdQM.addWhere("year", ECriteria.EQUAL, studyYear.getId());
        sdQM.addWhereAnd("semesterPeriod", ECriteria.EQUAL, sp.getId());

        try {
            sd = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(sdQM);
        } catch (NoResultException nrex) {
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

            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(sd);
        }

        return sd;
    }

    private ENTRANCE_YEAR getStudyYear() throws Exception {
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

            studyYear = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(syQM);
        }

        return studyYear;
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
    }

    @Override
    public boolean preCreate(Object source, int buttonId) {
        if (source.equals(grid)) {
            if (curriculum == null) {
                Message.showError(getUILocaleUtil().getMessage("error.emptyvalue"));
                return false;
            }
        }

        return true;
    }

    @Override
    public void onCreate(Object source, Entity e, int buttonId) {
    }

    @Override
    public boolean onEdit(Object source, Entity e, int buttonId) {
        return true;
    }

    @Override
    public boolean onPreview(Object source, Entity e, int buttonId) {
        return true;
    }

    @Override
    public void beforeRefresh(Object source, int buttonId) {
    }

    @Override
    public void onRefresh(Object source, List<Entity> entities) {
        if (source.equals(grid) && !entities.isEmpty()) {
            StringBuilder sql = new StringBuilder();
            sql.append("select b.ID, c.CODE from SUBJECT_REQUISITE a inner join CURRICULUM_DETAIL b on a.SUBJECT_ID = b.SUBJECT_ID inner join SUBJECT c on a.REQUISITE_ID = c.ID where a.PRE_REQUISITE = ?1 and b.DELETED = ?2 and b.ID in (");
            boolean first = true;
            for (Entity e : entities) {
                if (!first) {
                    sql.append(", ");
                }

                sql.append(e.getId().getId());
                first = false;
            }
            sql.append(')');

            Map<Integer, Object> params = new HashMap<Integer, Object>(2);
            params.put(1, Boolean.TRUE);
            params.put(2, Boolean.FALSE);

            try {
                List tempList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
                        .lookupItemsList(sql.toString(), params);
                if (!tempList.isEmpty()) {
                    for (Entity e : entities) {
                        V_CURRICULUM_DETAIL vcd = (V_CURRICULUM_DETAIL) e;
                        StringBuilder sb = new StringBuilder();
                        first = true;
                        for (Object o : tempList) {
                            Object[] oo = (Object[]) o;
                            if (vcd.getId().getId().longValue() == ((long) oo[0])) {
                                if (!first) {
                                    sb.append(", ");
                                }
                                sb.append(oo[1]);
                                first = false;
                            }
                        }

                        if (sb.length() > 0) {
                            vcd.setSubjectPrerequisiteCode(sb.toString());
                        }
                    }
                }
            } catch (Exception ex) {
                LOG.error("Unable to refresh mandatory subjects grid: ", ex);
                Message.showError(ex.toString());
            }
        }
    }

    @Override
    public void onFilter(Object source, QueryModel qm, int buttonId) {
    }

    @Override
    public void onAccept(Object source, List<Entity> entities, int buttonId) {
    }

    @Override
    public boolean preSave(Object source, Entity e, boolean isNew, int buttonId) {
        if (source.equals(addElectiveFWD.getFormWidget())) {
            V_CURRICULUM_DETAIL vcd = (V_CURRICULUM_DETAIL) e;
            CURRICULUM_DETAIL cd = new CURRICULUM_DETAIL();
            cd.setCurriculum(curriculum);
            cd.setSemester(semester);
            try {
                cd.setElectiveSubject(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
                        .lookup(ELECTIVE_SUBJECT_LABEL.class, ID.valueOf(999999)));
                cd.setElectiveSubjectCycle(vcd.getSubjectCycle());
                cd.setElectiveSubjectCredit(vcd.getCreditability().getCredit());
                cd.setCreated(new Date());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(cd);
                refreshMandatory();
            } catch (Exception ex) {
                LOG.error("Unable to add elective subject: ", ex);
                Message.showError(ex.toString());
            }
        }

        return false;
    }

    @Override
    public boolean preDelete(Object source, List<Entity> entities, int buttonId) {
        List<SEMESTER_SUBJECT> ssDelList = new ArrayList<SEMESTER_SUBJECT>();
        List<String> notDelList = new ArrayList<String>();
        if (source.equals(grid)) {
            List<CURRICULUM_DETAIL> delList = new ArrayList<CURRICULUM_DETAIL>();
            try {
                CommonEntityFacadeBean session = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class);
                SEMESTER_DATA sd = getOrCreateSemesterData();
                QueryModel<SEMESTER_SUBJECT> ssQM = new QueryModel<SEMESTER_SUBJECT>(SEMESTER_SUBJECT.class);
                ssQM.addWhere("semesterData", ECriteria.EQUAL, sd.getId());
                for (Entity e : entities) {
                    CURRICULUM_DETAIL cd = session.lookup(CURRICULUM_DETAIL.class, e.getId());
                    if (cd.getSubject() != null) {
                        ssQM.addWhere("subject", ECriteria.EQUAL, cd.getSubject().getId());
                        SEMESTER_SUBJECT ss = null;
                        try {
                            ss = session.lookupSingle(ssQM);
                        } catch (NoResultException nrex) {
                        }

                        if (ss != null) {
                            String sql = "select sum(coalesce(t1.CNT, 0)) from (select count(a1.ID) CNT from CURRICULUM_DETAIL a1 inner join CURRICULUM b1 on a1.CURRICULUM_ID = b1.ID where a1.CURRICULUM_ID != ?1 and a1.SEMESTER_DATA_ID = ?2 and a1.SUBJECT_ID = ?3 and b1.STATUS_ID = ?4 union select count(a2.ID) CNT from ELECTIVE_SUBJECT a2 inner join CURRICULUM b2 on a2.CURRICULUM_ID = b2.ID where a2.CURRICULUM_ID != ?5 and a2.SEMESTER_DATA_ID = ?6 and a2.SUBJECT_ID = ?7 and b2.STATUS_ID = ?8) t1";
                            Map<Integer, Object> params = new HashMap<Integer, Object>(8);
                            params.put(1, curriculum.getId().getId());
                            params.put(2, sd.getId().getId());
                            params.put(3, cd.getSubject().getId().getId());
                            params.put(4, 3);
                            params.put(5, curriculum.getId().getId());
                            params.put(6, sd.getId().getId());
                            params.put(7, cd.getSubject().getId().getId());
                            params.put(8, 3);
                            Integer sum = null;
                            try {
                                sum = (Integer) session.lookupSingle(sql, params);
                            } catch (NoResultException nrex) {
                            }
                            if (sum != null && sum > 0) {
                                delList.add(cd);
                            } else {
                                sql = "select sum(coalesce(t1.CNT, 0)) from (select count(a1.ID) CNT from EXAM_SCHEDULE a1 where a1.SUBJECT_ID = ?1 and a1.DELETED = ?2 union select count(a2.ID) CNT from SCHEDULE_DETAIL a2 where a2.SUBJECT_ID = ?3 union select count(a3.SUBJECT_ID) CNT from STUDENT_SUBJECT a3 where a3.SUBJECT_ID = ?4) t1";
                                params = new HashMap<Integer, Object>(4);
                                params.put(1, ss.getId().getId());
                                params.put(2, Boolean.FALSE);
                                params.put(3, ss.getId().getId());
                                params.put(4, ss.getId().getId());
                                try {
                                    sum = (Integer) session.lookupSingle(sql, params);
                                } catch (NoResultException nrex) {
                                }
                                if (sum != null && sum > 0) {
                                    notDelList.add(cd.getSubject().getCode() + " " + cd.getSubject().getNameRU());
                                } else {
                                    ssDelList.add(ss);
                                    delList.add(cd);
                                }
                            }
                        } else {
                            delList.add(cd);
                        }
                    } else {
                        List<Entity> list = electiveGrid.getAllEntities();
                        boolean found = false;
                        for (Entity e1 : list) {
                            V_ELECTIVE_SUBJECT ves = (V_ELECTIVE_SUBJECT) e1;
                            if (ves.getCycleShortName().equals(cd.getElectiveSubjectCycle().getCycleShortName())) {
                                found = true;
                                break;
                            }
                        }

                        if (found) {
                            notDelList.add(cd.getElectiveSubject().getNameRU());
                        } else {
                            delList.add(cd);
                        }
                    }
                }

                if (!delList.isEmpty()) {
                    if (!ssDelList.isEmpty()) {
                        session.delete(ssDelList);
                    }
                    session.delete(delList);
                    refreshMandatory();
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
        } else if (source.equals(electiveGrid)) {
            List<ELECTIVE_SUBJECT> delList = new ArrayList<ELECTIVE_SUBJECT>();
            try {
                SEMESTER_DATA sd = getOrCreateSemesterData();
                CommonEntityFacadeBean session = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class);
                QueryModel<SEMESTER_SUBJECT> ssQM = new QueryModel<SEMESTER_SUBJECT>(SEMESTER_SUBJECT.class);
                ssQM.addWhere("semesterData", ECriteria.EQUAL, sd.getId());
                for (Entity e : entities) {
                    ELECTIVE_SUBJECT es = session.lookup(ELECTIVE_SUBJECT.class, e.getId());
                    ssQM.addWhere("subject", ECriteria.EQUAL, es.getSubject().getId());
                    SEMESTER_SUBJECT ss = null;
                    try {
                        ss = session.lookupSingle(ssQM);
                    } catch (NoResultException nrex) {
                    }

                    if (ss != null) {
                        String sql = "select sum(coalesce(t1.CNT, 0)) from (select count(a1.ID) CNT from CURRICULUM_DETAIL a1 inner join CURRICULUM b1 on a1.CURRICULUM_ID = b1.ID where a1.CURRICULUM_ID != ?1 and a1.SEMESTER_DATA_ID = ?2 and a1.SUBJECT_ID = ?3 and b1.STATUS_ID = ?4 union select count(a2.ID) CNT from ELECTIVE_SUBJECT a2 inner join CURRICULUM b2 on a2.CURRICULUM_ID = b2.ID where a2.CURRICULUM_ID != ?5 and a2.SEMESTER_DATA_ID = ?6 and a2.SUBJECT_ID = ?7 and b2.STATUS_ID = ?8) t1";
                        Map<Integer, Object> params = new HashMap<Integer, Object>(8);
                        params.put(1, curriculum.getId().getId());
                        params.put(2, sd.getId().getId());
                        params.put(3, es.getSubject().getId().getId());
                        params.put(4, 3);
                        params.put(5, curriculum.getId().getId());
                        params.put(6, sd.getId().getId());
                        params.put(7, es.getSubject().getId().getId());
                        params.put(8, 3);
                        Integer sum = null;
                        try {
                            sum = ((BigDecimal) session.lookupSingle(sql, params)).intValue();
                        } catch (NoResultException nrex) {
                        }
                        if (sum != null && sum > 0) {
                            delList.add(es);
                        } else {
                            sql = "select sum(coalesce(t1.CNT, 0)) from (select count(a1.ID) CNT from EXAM_SCHEDULE a1 where a1.SUBJECT_ID = ?1 and a1.DELETED = ?2 union select count(a2.ID) CNT from SCHEDULE_DETAIL a2 where a2.SUBJECT_ID = ?3 union select count(a3.SUBJECT_ID) CNT from STUDENT_SUBJECT a3 where a3.SUBJECT_ID = ?4) t1";
                            params = new HashMap<Integer, Object>(4);
                            params.put(1, ss.getId().getId());
                            params.put(2, Boolean.FALSE);
                            params.put(3, ss.getId().getId());
                            params.put(4, ss.getId().getId());
                            try {
                                sum = ((BigDecimal) session.lookupSingle(sql, params)).intValue();
                            } catch (NoResultException nrex) {
                            }
                            if (sum != null && sum > 0) {
                                notDelList.add(es.getSubject().getCode() + " " + es.getSubject().getNameRU());
                            } else {
                                ssDelList.add(ss);
                                delList.add(es);
                            }
                        }
                    } else {
                        delList.add(es);
                    }
                }

                if (!delList.isEmpty()) {
                    if (!ssDelList.isEmpty()) {
                        session.delete(ssDelList);
                    }
                    session.delete(delList);
                    refreshElective();
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
                LOG.error("Unable to delete elective subject: ", ex);
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

    @Override
    public void save() throws Exception {
        if (curriculum != null && !curriculum.getCurriculumStatus().getId().equals(ID.valueOf(3))) {
            List<CURRICULUM_DETAIL> cdList = new ArrayList<CURRICULUM_DETAIL>();
            List<Entity> list = grid.getAllEntities();
            try {
                for (Entity item : list) {
                    V_CURRICULUM_DETAIL vcd = (V_CURRICULUM_DETAIL) item;
                    CURRICULUM_DETAIL cd = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
                            .lookup(CURRICULUM_DETAIL.class, vcd.getId());
                    if (cd != null && cd.getSubject() != null && vcd.isConsiderCredit() != cd.isConsiderCredit()) {
                        cd.setConsiderCredit(vcd.isConsiderCredit());
                        cdList.add(cd);
                    }
                }

                if (!cdList.isEmpty()) {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(cdList);
                    refreshMandatory();
                }
            } catch (Exception ex) {
                LOG.error("Unable to save changes: ", ex);
                Message.showError(ex.toString());
            }
        }
    }

    @Override
    protected void cancel() {
    }

    public Integer getTotalCredit() {
        Integer totalCredit = (Integer) ((DBGridModel) grid.getWidgetModel()).getFooterValue("credit");
        List<Entity> list = grid.getAllEntities();
        for (Entity item : list) {
            V_CURRICULUM_DETAIL vcd = (V_CURRICULUM_DETAIL) item;
            if (vcd.getSubjectCode() != null && !vcd.isConsiderCredit()) {
                totalCredit = totalCredit - vcd.getCredit();
            }
        }
        grid.setFooterValue("credit", totalCredit);

        Integer electiveTotalCredit = (Integer) ((DBGridModel) electiveGrid.getWidgetModel()).
                getFooterValue("credit");
        List<Entity> electives = electiveGrid.getAllEntities();
        for (Entity entity : electives) {
            V_ELECTIVE_SUBJECT electiveSubject = (V_ELECTIVE_SUBJECT) entity;
            if (electiveSubject.getSubjectCode() != null && !electiveSubject.isConsiderCredit()) {
                electiveTotalCredit -= electiveSubject.getCredit();
            }
        }
        return totalCredit + electiveTotalCredit;
    }

    private List<CURRICULUM_DETAIL> getElectiveCurriculumDetailList() throws Exception {
        QueryModel<CURRICULUM_DETAIL> cdQM = new QueryModel<CURRICULUM_DETAIL>(CURRICULUM_DETAIL.class);
        cdQM.addWhere("curriculum", ECriteria.EQUAL, curriculum.getId());
        cdQM.addWhereAnd("semester", ECriteria.EQUAL, semester.getId());
        cdQM.addWhereNotNullAnd("electiveSubject");
        cdQM.addWhereAnd("deleted", Boolean.FALSE);

        return SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(cdQM);
    }

    private int getTotalApprovedElectiveCredit() throws Exception {
        int total = 0;
        List<CURRICULUM_DETAIL> cdList = getElectiveCurriculumDetailList();
        for (CURRICULUM_DETAIL cd : cdList) {
            if (cd.getElectiveSubjectCredit() != null) {
                total += cd.getElectiveSubjectCredit();
            }
        }

        return total;
    }

    private List<SUBJECT_CYCLE> getApprovedElectiveCycleList() throws Exception {
        List<SUBJECT_CYCLE> scList = new ArrayList<SUBJECT_CYCLE>();
        List<CURRICULUM_DETAIL> cdList = getElectiveCurriculumDetailList();
        for (CURRICULUM_DETAIL cd : cdList) {
            scList.add(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
                    .lookup(CURRICULUM_DETAIL.class, cd.getId()).getElectiveSubjectCycle());
        }

        return scList;
    }

    private int getTotalSelectedElectiveCredit() throws Exception {
        int total = 0;
        String sql = "select sum(c.CREDIT) from ELECTIVE_SUBJECT a inner join SUBJECT b on a.SUBJECT_ID = b.ID inner join CREDITABILITY c on b.CREDITABILITY_ID = c.ID where a.DELETED = FALSE and a.CURRICULUM_ID = ?1 and a.SEMESTER_ID = ?2";
        Map<Integer, Object> params = new HashMap<Integer, Object>();
        params.put(1, curriculum.getId().getId().intValue());
        params.put(2, semester.getId().getId().intValue());

        Object o = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(sql, params);
        if (o != null) {
            total = Integer.valueOf(o.toString());
        }

        return total;
    }

    private class AddListener implements ClickListener {

        @Override
        public void buttonClick(ClickEvent ev) {
            try {
                getParentView().save();
                QueryModel<DEPARTMENT> chairQM = new QueryModel<DEPARTMENT>(DEPARTMENT.class);
                chairQM.addWhereNotNull("parent");
                chairQM.addWhereAnd("deleted", Boolean.FALSE);
                chairQM.addOrder("deptName");
                BeanItemContainer<DEPARTMENT> chairBIC = new BeanItemContainer<DEPARTMENT>(DEPARTMENT.class,
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(chairQM));
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
                BeanItemContainer<LEVEL> levelBIC = new BeanItemContainer<LEVEL>(LEVEL.class,
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(levelQM));
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
                BeanItemContainer<SUBJECT_CYCLE> subjectCycleBIC = new BeanItemContainer<SUBJECT_CYCLE>(
                        SUBJECT_CYCLE.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
                        .lookup(subjectCycleQM));
                ComboBox subjectCycleCB = new ComboBox();
                subjectCycleCB.setContainerDataSource(subjectCycleBIC);
                subjectCycleCB.setImmediate(true);
                subjectCycleCB.setNullSelectionAllowed(true);
                subjectCycleCB.setTextInputAllowed(false);
                subjectCycleCB.setFilteringMode(FilteringMode.OFF);
                subjectCycleCB.setPageLength(0);

                QueryModel<CREDITABILITY> creditabilityQM = new QueryModel<CREDITABILITY>(CREDITABILITY.class);
                creditabilityQM.addOrder("credit");
                BeanItemContainer<CREDITABILITY> creditabilityBIC = new BeanItemContainer<CREDITABILITY>(
                        CREDITABILITY.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
                        .lookup(creditabilityQM));
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
                qm.addWhere("mandatory", Boolean.TRUE);
                qm.addWhere("subjectCycle",ECriteria.NOT_EQUAL,ID.valueOf(4));
                subjectSelectDlg.setDialogWidth(600);
                subjectSelectDlg.setDialogHeight(300);
                subjectSelectDlg.getFilterModel().addFilter("chair", chairCB);
                subjectSelectDlg.getFilterModel().addFilter("level", levelCB);
//                subjectSelectDlg.getFilterModel().addFilter("subjectCycle", subjectCycleCB);
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

    private class AddElectiveSubjectListener implements ClickListener {

        @Override
        public void buttonClick(ClickEvent ev) {
            try {
                String message = String.format(getUILocaleUtil().getMessage("no.elective.subject.for.semester"),
                        semester.getId().toString());
                if (curriculum == null || curriculum.getId() == null) {
                    Message.showInfo(message);
                    return;
                }

                QueryModel<DEPARTMENT> chairQM = new QueryModel<DEPARTMENT>(DEPARTMENT.class);
                chairQM.addWhereNotNull("parent");
                chairQM.addWhereAnd("deleted", Boolean.FALSE);
                chairQM.addOrder("deptName");
                BeanItemContainer<DEPARTMENT> chairBIC = new BeanItemContainer<DEPARTMENT>(DEPARTMENT.class,
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(chairQM));
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
                BeanItemContainer<LEVEL> levelBIC = new BeanItemContainer<LEVEL>(LEVEL.class,
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(levelQM));
                ComboBox levelCB = new ComboBox();
                levelCB.setContainerDataSource(levelBIC);
                levelCB.setImmediate(true);
                levelCB.setNullSelectionAllowed(true);
                levelCB.setTextInputAllowed(false);
                levelCB.setFilteringMode(FilteringMode.OFF);
                levelCB.setPageLength(0);

                QueryModel<SUBJECT_CYCLE> subjectCycleQM =
                        new QueryModel<SUBJECT_CYCLE>(SUBJECT_CYCLE.class);
                subjectCycleQM.addWhere("id", ECriteria.LESS, ID.valueOf(4));
                subjectCycleQM.addOrder("cycleShortName");
                BeanItemContainer<SUBJECT_CYCLE> subjectCycleBIC = new BeanItemContainer<SUBJECT_CYCLE>(
                        SUBJECT_CYCLE.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
                        .lookup(subjectCycleQM));
                ComboBox subjectCycleCB = new ComboBox();
                subjectCycleCB.setContainerDataSource(subjectCycleBIC);
                subjectCycleCB.setImmediate(true);
                subjectCycleCB.setNullSelectionAllowed(true);
                subjectCycleCB.setTextInputAllowed(false);
                subjectCycleCB.setFilteringMode(FilteringMode.OFF);
                subjectCycleCB.setPageLength(0);

                QueryModel<CREDITABILITY> creditabilityQM = new QueryModel<CREDITABILITY>(CREDITABILITY.class);
                creditabilityQM.addOrder("credit");
                BeanItemContainer<CREDITABILITY> creditabilityBIC = new BeanItemContainer<CREDITABILITY>(
                        CREDITABILITY.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
                        .lookup(creditabilityQM));
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

                electiveSubjectSelectDlg = new ElectiveSubjectSelectDialog(
                        new AddNewElectiveSubjectListener(), V_SUBJECT_SELECT.class);
                QueryModel qm = ((DBGridModel) electiveSubjectSelectDlg.getSelectModel()).getQueryModel();
                qm.addWhere("chair", ECriteria.EQUAL, ID.valueOf(-1));
                qm.addWhere("mandatory", Boolean.FALSE);
                qm.addWhere("subjectCycle",ECriteria.NOT_EQUAL,ID.valueOf(4));
                electiveSubjectSelectDlg.setDialogWidth(600);
                electiveSubjectSelectDlg.setDialogHeight(400);
                electiveSubjectSelectDlg.getFilterModel().addFilter("chair", chairCB);
                electiveSubjectSelectDlg.getFilterModel().addFilter("level", levelCB);
//                electiveSubjectSelectDlg.getFilterModel().addFilter("subjectCycle", subjectCycleCB);
                electiveSubjectSelectDlg.getFilterModel().addFilter("creditability", creditabilityCB);
                electiveSubjectSelectDlg.getFilterModel().addFilter("code", subjectCodeTF);
                electiveSubjectSelectDlg.setFilterRequired(true);
                electiveSubjectSelectDlg.initFilter();
                electiveSubjectSelectDlg.open();
            } catch (Exception ex) {
                LOG.error("Unable to open elective subject select dialog: ", ex);
                Message.showError(ex.getMessage());
            }
        }
    }

    private class AddElectiveListener implements ClickListener {

        @Override
        public void buttonClick(ClickEvent ev) {
            try {
                getParentView().save();
                FormModel fm = new FormModel(V_CURRICULUM_DETAIL.class, true);
                fm.setTitleResource("semester." + semester.getId().toString());
                fm.setDeferredCreate(false);

                fm.getFieldModel("academicFormula").setInEdit(false);

                FKFieldModel subjectFM = (FKFieldModel) fm.getFieldModel("subject");
                subjectFM.setReadOnly(true);
                QueryModel subjectQM = subjectFM.getQueryModel();
                subjectQM.addWhere("deleted", Boolean.FALSE);
                subjectQM.addWhereAnd("elective", Boolean.TRUE);

                FKFieldModel subjectCycleFM = (FKFieldModel) fm.getFieldModel("subjectCycle");
                subjectCycleFM.setReadOnly(false);
                subjectCycleFM.setRequired(true);
                QueryModel subjectCycleQM = subjectCycleFM.getQueryModel();
                subjectCycleQM.addWhere("id", ECriteria.LESS, ID.valueOf(4));

                FKFieldModel creditabilityFM = (FKFieldModel) fm.getFieldModel("creditability");
                QueryModel creditabilityQM = creditabilityFM.getQueryModel();
                creditabilityQM.addOrder("credit");

                V_CURRICULUM_DETAIL vcd = (V_CURRICULUM_DETAIL) fm.getEntity();
                vcd.setSubject(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
                        .lookup(SUBJECT.class, ID.valueOf(999999)));

                addElectiveFWD = new FormWidgetDialog(fm);
                addElectiveFWD.getFormWidget().addEntityListener(SemesterDetailPanel.this);
                addElectiveFWD.open();
            } catch (Exception ex) {
                LOG.error("Unable to add elective subject: ", ex);
                Message.showError(ex.toString());
            }
        }
    }

    private class AddNewSubjectListener extends AbstractYesButtonListener {

        private boolean can = false;

        @Override
        public void buttonClick(ClickEvent ev) {
            List<Entity> selectedList = subjectSelectDlg.getSelectedEntities();
            if (!selectedList.isEmpty()) {
                List<CURRICULUM_DETAIL> newList = new ArrayList<CURRICULUM_DETAIL>();
                try {
                    SEMESTER_DATA sd = getOrCreateSemesterData();
                    CommonEntityFacadeBean session = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class);
                    Date created = new Date();
                    for (Entity e : selectedList) {
                        CURRICULUM_DETAIL cd = new CURRICULUM_DETAIL();
                        cd.setCurriculum(curriculum);
                        cd.setSemester(semester);
                        cd.setSemesterData(sd);
                        cd.setSubject(session.lookup(SUBJECT.class, e.getId()));
                        if (cd.getSubject().getSubjectCycle().getId().equals(MULTI_CYCLE_ID)) {
                            cd.setSubjectCycle(subjectSelectDlg.getSubjectCycle());
                        }
                        cd.setConsiderCredit(subjectSelectDlg.isConsiderCredit());
                        cd.setCreated(created);
                        newList.add(cd);
                    }

                    if (!newList.isEmpty()) {
                        session.create(newList);
                        refreshMandatory();
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

                Map<Integer, Object> params = new HashMap<Integer, Object>(3);
                params.put(1, curriculum.getId().getId());
                params.put(2, semester.getId().getId());
                params.put(3, Boolean.FALSE);
                String sql = "select count(a.SUBJECT_ID) from CURRICULUM_DETAIL a where a.CURRICULUM_ID = ?1 and a.SEMESTER_ID = ?2 and a.DELETED = ?3 and a.SUBJECT_ID in (";
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
                    List<SUBJECT_CYCLE> scList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
                            .lookup(scQM);
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

                    long count = (long) SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
                            .lookupSingle(sql, params);
                    can = (count == 0);
                    if (!can) {
                        Message.showError(getUILocaleUtil().getMessage("selected.subjects.already.exists"));
                    } else {
                        sql = "select count(a.SUBJECT_ID) from ELECTIVE_SUBJECT a where a.CURRICULUM_ID = ?1 and a.SEMESTER_ID = ?2 and a.DELETED = ?3 and a.SUBJECT_ID in (";
                        sb = new StringBuilder();
                        first = true;
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

                        count = (long) SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
                                .lookupSingle(sql, params);
                        can = (count == 0);
                        if (!can) {
                            Message.showError(getUILocaleUtil().getMessage("selected.subjects.already.exists"));
                        }
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

    private class AddNewElectiveSubjectListener extends AbstractYesButtonListener {

        private boolean can = false;

        @Override
        public void buttonClick(ClickEvent ev) {
            List<Entity> selectedList = electiveSubjectSelectDlg.getSelectedEntities();
            if (!selectedList.isEmpty()) {
                List<Entity> newList = new ArrayList<Entity>();
                try {
                    SEMESTER_DATA sd = getOrCreateSemesterData();
                    CommonEntityFacadeBean session = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class);
                    Date created = new Date();
                    QueryModel<SEMESTER_SUBJECT> ssQM = new QueryModel<SEMESTER_SUBJECT>(SEMESTER_SUBJECT.class);
                    ssQM.addWhere("semesterData", ECriteria.EQUAL, sd.getId());
                    for (Entity e : selectedList) {
                        ELECTIVE_SUBJECT es = new ELECTIVE_SUBJECT();
                        es.setCurriculum(curriculum);
                        es.setSemester(semester);
                        es.setSemesterData(sd);
                        es.setSubject(session.lookup(SUBJECT.class, e.getId()));
                        if (es.getSubject().getSubjectCycle().getId().equals(MULTI_CYCLE_ID)) {
                            es.setSubjectCycle(electiveSubjectSelectDlg.getSubjectCycle());
                        }
                        es.setConsiderCredit(electiveSubjectSelectDlg.isConsiderCredit());
                        es.setCreated(created);
                        newList.add(es);

                        // Create semester subject if not exists
                        ssQM.addWhere("subject", ECriteria.EQUAL, es.getSubject().getId());
                        List<SEMESTER_SUBJECT> ssList = session.lookup(ssQM);
                        if (ssList.isEmpty()) {
                            SEMESTER_SUBJECT ss = new SEMESTER_SUBJECT();
                            ss.setSemesterData(sd);
                            ss.setSubject(es.getSubject());
                            newList.add(ss);
                        }
                    }

                    if (!newList.isEmpty()) {
                        session.create(newList);
                        refreshElective();
                    }
                } catch (Exception ex) {
                    LOG.error("Unable to create new elective subject: ", ex);
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
            List<Entity> selectedList = electiveSubjectSelectDlg.getSelectedEntities();
            can = !selectedList.isEmpty();
            if (can) {
                SUBJECT_CYCLE sc = electiveSubjectSelectDlg.getSubjectCycle();
                boolean multi = false;
                QueryModel<SUBJECT_CYCLE> scQM = new QueryModel<SUBJECT_CYCLE>(SUBJECT_CYCLE.class);
                FromItem scFI = scQM.addJoin(EJoin.INNER_JOIN, "id", SUBJECT.class, "subjectCycle");
                List<ID> idList = new ArrayList<ID>(selectedList.size());

                try {
                    //					int totalApproved = getTotalApprovedElectiveCredit();
                    //					int totalSelected = 0;
                    //					for (Entity e : selectedList) {
                    //						Integer credit = ((V_SUBJECT_SELECT)e).getCredit();
                    //						if (credit != null) {
                    //							totalSelected += credit;
                    //						}
                    //					}
                    //
                    //					totalSelected += getTotalSelectedElectiveCredit();
                    //
                    //					can = (totalApproved >= totalSelected);
                    //					if (!can) {
                    //						String message = String.format(getUILocaleUtil().getMessage("elective.subject.credit.exceed"), semester.getId().toString(), totalApproved);
                    //						Message.showInfo(message);
                    //					}

//                    if (can) {
//                        List<SUBJECT_CYCLE> scList = getApprovedElectiveCycleList();
//                        for (Entity e : selectedList) {
//                            V_SUBJECT_SELECT vss = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
//                                    .lookup(V_SUBJECT_SELECT.class, e.getId());
//                            if (!vss.getSubjectCycle().getId().equals(MULTI_CYCLE_ID)) {
//                                can = scList.contains(vss.getSubjectCycle());
//                                if (!can) {
//                                    Message.showInfo(getUILocaleUtil()
//                                            .getMessage("incorrect.elective.subject.component"));
//                                    break;
//                                }
//                            }
//                        }
//                    }
//
//                    if (can) {
                    Map<Integer, Object> params = new HashMap<Integer, Object>(3);
                    params.put(1, curriculum.getId().getId());
                    params.put(2, semester.getId().getId());
                    params.put(3, Boolean.FALSE);
                    String sql = "select count(a.SUBJECT_ID) from ELECTIVE_SUBJECT a where a.CURRICULUM_ID = ?1 and a.SEMESTER_ID = ?2 and a.DELETED = ?3 and a.SUBJECT_ID in (";
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
                        List<SUBJECT_CYCLE> scList = SessionFacadeFactory
                                .getSessionFacade(CommonEntityFacadeBean.class).lookup(scQM);
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

                        long count = (long) SessionFacadeFactory
                                .getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(sql, params);
                        can = (count == 0);
                        if (!can) {
                            Message.showError(getUILocaleUtil().getMessage("selected.subjects.already.exists"));
                        } else {
                            sql = "select count(a.SUBJECT_ID) from CURRICULUM_DETAIL a where a.CURRICULUM_ID = ?1 and a.SEMESTER_ID = ?2 and a.DELETED = ?3 and a.SUBJECT_ID in (";
                            sb = new StringBuilder();
                            first = true;
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

                            count = (long) SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
                                    .lookupSingle(sql, params);
                            can = (count == 0);
                            if (!can) {
                                Message.showError(getUILocaleUtil().getMessage("selected.subjects.already.exists"));
                            }
                        }
                    } catch (Exception ex) {
                        can = false;
                        LOG.error("Unable to check subjects: ", ex);
                        Message.showError(ex.toString());
//                        }
                    }
                } catch (Exception ex) {
                    can = false;
                    LOG.error("Unable to calculate total credit: ", ex);
                    Message.showError(ex.toString());
                }
            }

            return can;
        }
    }
}
