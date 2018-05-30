package kz.halyqsoft.univercity.modules.creativeexams;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.TextField;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_CREATIVE_EXAM;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_CREATIVE_EXAM_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VCreativeExam;
import kz.halyqsoft.univercity.filter.FStudentFilter;
import kz.halyqsoft.univercity.filter.panel.StudentFilterPanel;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.EntityUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Omarbek
 * @created on 23.04.2018
 */
public class CreativeExamView extends AbstractTaskView implements FilterPanelListener {

    private final StudentFilterPanel filterPanel;
    private GridWidget examGW;

    public CreativeExamView(AbstractTask task) throws Exception {
        super(task);
        filterPanel = new StudentFilterPanel(new FStudentFilter());
    }

    @Override
    public void initView(boolean b) throws Exception {
        filterPanel.addFilterPanelListener(this);
        TextField tf = new TextField();
        tf.setNullRepresentation("");
        tf.setNullSettingAllowed(true);
        filterPanel.addFilterComponent("code", tf);

        tf = new TextField();
        tf.setNullRepresentation("");
        tf.setNullSettingAllowed(true);
        filterPanel.addFilterComponent("firstname", tf);

        tf = new TextField();
        tf.setNullRepresentation("");
        tf.setNullSettingAllowed(true);
        filterPanel.addFilterComponent("lastname", tf);

        getContent().addComponent(filterPanel);
        getContent().setComponentAlignment(filterPanel, Alignment.TOP_CENTER);

        examGW = new GridWidget(VCreativeExam.class);
        examGW.addEntityListener(new CreateExamEntity());
        examGW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, false);
        examGW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
        DBGridModel examGM = (DBGridModel) examGW.getWidgetModel();
        examGM.setMultiSelect(true);
        examGM.setRefreshType(ERefreshType.MANUAL);

        refresh();

        getContent().addComponent(examGW);
        getContent().setComponentAlignment(examGW, Alignment.MIDDLE_CENTER);
    }

    @Override
    public void doFilter(AbstractFilterBean abstractFilterBean) {
        FStudentFilter sf = (FStudentFilter) abstractFilterBean;
        Map<Integer, Object> params = new HashMap<>();
        StringBuilder sb = new StringBuilder();
        if (sf.getCode() != null && sf.getCode().trim().length() >= 2) {
            sb.append(" and lower(usr.CODE) like '");
            sb.append(sf.getCode().trim().toLowerCase());
            sb.append("%'");
        }
//        if (sf.getFirstname() != null && sf.getFirstname().trim().length() >= 3) {
//            sb.append(" and lower(usr.FIRST_NAME) like '");
//            sb.append(sf.getFirstname().trim().toLowerCase());
//            sb.append("%'");
//        }
//        if (sf.getLastname() != null && sf.getLastname().trim().length() >= 3) {
//            sb.append(" and lower(usr.LAST_NAME) like '");
//            sb.append(sf.getLastname().trim().toLowerCase());
//            sb.append("%'");
//        }

        List<VCreativeExam> list = new ArrayList<>();
        if (sb.length() > 0) {
            sb.insert(0, " where usr.deleted = FALSE ");
            String sql = "SELECT " +
                    " st_creat_exam.id," +
                    " usr.code," +
                    "  trim(usr.LAST_NAME || ' ' || usr.FIRST_NAME || ' ' || coalesce(usr.MIDDLE_NAME, '')) FIO, " +
                    "  trim(first_subj.subject_name || '-' || first_subj.rate)                              firstSubject, " +
                    "  trim(second_subj.subject_name || '-' || second_subj.rate)                            secondSubject, " +
                    "  st_creat_exam.rate                                                                   total, " +
                    "  st_creat_exam.place " +
                    "FROM STUDENT stu INNER JOIN USERS usr ON stu.ID = usr.ID " +
                    "  INNER JOIN student_creative_exam st_creat_exam ON stu.id = st_creat_exam.student_id " +
                    "  LEFT JOIN ( " +
                    "              SELECT " +
                    "                st_creat_exam_subj.student_creative_exam_id, " +
                    "                creat_exam_subj.subject_name, " +
                    "                st_creat_exam_subj.rate " +
                    "              FROM student_creative_exam_subject st_creat_exam_subj " +
                    "                INNER JOIN creative_exam_subject creat_exam_subj " +
                    "                  ON st_creat_exam_subj.creative_exam_subject_id = creat_exam_subj.id " +
                    "              WHERE creat_exam_subj.id = 1 " +
                    "            ) first_subj ON first_subj.student_creative_exam_id = st_creat_exam.id " +
                    "  LEFT JOIN ( " +
                    "              SELECT " +
                    "                st_creat_exam_subj.student_creative_exam_id, " +
                    "                creat_exam_subj.subject_name, " +
                    "                st_creat_exam_subj.rate " +
                    "              FROM student_creative_exam_subject st_creat_exam_subj " +
                    "                INNER JOIN creative_exam_subject creat_exam_subj " +
                    "                  ON st_creat_exam_subj.creative_exam_subject_id = creat_exam_subj.id " +
                    "              WHERE creat_exam_subj.id = 2 " +
                    "            ) second_subj ON second_subj.student_creative_exam_id = st_creat_exam.id " +
                    sb.toString() +
                    " ORDER BY FIO, TOTAL";
            try {
                List tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
                if (!tmpList.isEmpty()) {
                    for (Object o : tmpList) {
                        Object[] oo = (Object[]) o;
                        VCreativeExam vs = new VCreativeExam();
                        vs.setId(ID.valueOf((long) oo[0]));
                        vs.setCode((String) oo[1]);
                        vs.setFio((String) oo[2]);
                        vs.setFirstSubject((String) oo[3]);
                        vs.setSecondSubject((String) oo[4]);
                        vs.setTotal(((BigDecimal) oo[5]).intValue());
                        vs.setPlace((String) oo[6]);
                        list.add(vs);
                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load student's creative exam list", ex);
            }
        } else {
            Message.showInfo(getUILocaleUtil().getMessage("select.1.search.condition"));
        }

        refresh(list);
    }

    @Override
    public void clearFilter() {
        refresh(new ArrayList<>());
    }

    private void refresh(List<VCreativeExam> list) {
        ((DBGridModel) examGW.getWidgetModel()).setEntities(list);
        try {
            examGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh student's creative exam list", ex);
        }
    }

    public void refresh() throws Exception {
        FStudentFilter ef = (FStudentFilter) filterPanel.getFilterBean();
        if (ef.hasFilter()) {
            doFilter(ef);
        }
    }

    private class CreateExamEntity extends EntityUtils {

        @Override
        public void init(Object source, Entity e, boolean isNew) throws Exception {
            VCreativeExam creativeExam = (VCreativeExam) e;
            new CreativeExamEdit(creativeExam, isNew, CreativeExamView.this);

        }

        @Override
        public GridWidget getGridWidget() {
            return examGW;
        }

        @Override
        public String getModuleName() {
            return "creative exam edit";
        }

        @Override
        public Class<? extends Entity> getEntityClass() {
            return STUDENT_CREATIVE_EXAM.class;
        }

        @Override
        protected void refresh() throws Exception {
            CreativeExamView.this.refresh();
        }

        @Override
        protected void removeChildrenEntity(List<Entity> delList) throws Exception {
            for (Entity entity : delList) {
                QueryModel<STUDENT_CREATIVE_EXAM_SUBJECT> subjectQM = new QueryModel<>(STUDENT_CREATIVE_EXAM_SUBJECT.class);
                subjectQM.addWhere("studentCreativeExam", ECriteria.EQUAL, entity.getId());
                List<STUDENT_CREATIVE_EXAM_SUBJECT> subjects = SessionFacadeFactory.getSessionFacade(
                        CommonEntityFacadeBean.class).lookup(subjectQM);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(subjects);
            }
        }
    }
}
