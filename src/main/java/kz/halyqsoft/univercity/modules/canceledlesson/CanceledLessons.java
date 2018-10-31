package kz.halyqsoft.univercity.modules.canceledlesson;

import kz.halyqsoft.univercity.entity.beans.univercity.LESSON;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VCanceledLessons;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;

import java.util.*;

public class CanceledLessons extends AbstractTaskView implements EntityListener {
    private GridWidget lessonGW;

    public CanceledLessons(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        lessonGW = new GridWidget(VCanceledLessons.class);
        lessonGW.addEntityListener(this);
        lessonGW.setButtonVisible(AbstractToolbar.ADD_BUTTON, false);
        lessonGW.setButtonVisible(AbstractToolbar.DELETE_BUTTON, false);


        DBGridModel lessonGM = (DBGridModel) lessonGW.getWidgetModel();
        lessonGM.setRefreshType(ERefreshType.MANUAL);
        lessonGM.setCrudEntityClass(LESSON.class);

        refreshLessonList(getLessonList());

        FormModel lessonFM = lessonGM.getFormModel();
        lessonFM.getFieldModel("scheduleDetail").setInView(false);
        lessonFM.getFieldModel("scheduleDetail").setInEdit(false);

        lessonFM.getFieldModel("lessonDate").setInView(false);
        lessonFM.getFieldModel("lessonDate").setInEdit(false);

        lessonFM.getFieldModel("beginDate").setInView(false);
        lessonFM.getFieldModel("beginDate").setInEdit(false);

        lessonFM.getFieldModel("finishDate").setInView(false);
        lessonFM.getFieldModel("finishDate").setInEdit(false);

        lessonFM.getFieldModel("cancelReason").setInView(false);
        lessonFM.getFieldModel("cancelReason").setInEdit(false);

        lessonFM.getFieldModel("canceled").setRequired(false);

        getContent().addComponent(lessonGW);
    }

    public List<VCanceledLessons> getLessonList() {
        List<VCanceledLessons> lessonList = new ArrayList<>();

        Map<Integer, Object> pm = new HashMap<>();
        String sql = "SELECT " +
                "        ls.id," +
                "        s2.name_" + CommonUtils.getLanguage() + ","+
                "        trim(u.first_name||' '||u.last_name||' '||u.middle_name),\n" +
                "        ls.lesson_date,\n" +
                "        (ls.begin_date::time)::text ,\n" +
                "        (ls.finish_date::time)::text,\n" +
                "        ls.cancel_reason\n" +
                "            from lesson ls\n" +
                "              INNER JOIN schedule_detail sd on ls.schedule_detail_id = sd.id\n" +
                "              INNER JOIN subject s2 on sd.subject_id = s2.id\n" +
                "              INNER JOIN employee e on sd.teacher_id = e.id\n" +
                "              INNER JOIN users u on e.id = u.id\n" +
                "WHERE canceled = true";

        try {
            List<Object> emplBDList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, pm);
            if (!emplBDList.isEmpty()) {
                for (Object o : emplBDList) {
                    Object[] oo = (Object[]) o;
                    VCanceledLessons lesson = new VCanceledLessons();
                    lesson.setId(ID.valueOf((long) oo[0]));
                    lesson.setSubjectName((String) oo[1]);
                    lesson.setTeacherFIO((String) oo[2]);
                    lesson.setLessonDate((Date) oo[3]);
                    lesson.setBeginDate((String) oo[4]);
                    lesson.setFinishDate((String) oo[5]);
                    lesson.setCancelReason((String) oo[6]);
                    lessonList.add(lesson);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load employee list", ex);
        }
        refreshLessonList(lessonList);
        return lessonList;
    }

    private void refreshLessonList(List<VCanceledLessons> lessonsList) {
        ((DBGridModel) lessonGW.getWidgetModel()).setEntities(lessonsList);
        try {
            lessonGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh lesson list", ex);
        }
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
        if (ev.getAction() == EntityEvent.MERGED || ev.getAction() == EntityEvent.REMOVED) {
            refreshLessonList(getLessonList());
        }
    }
}
