package kz.halyqsoft.univercity.modules.schedule;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import kz.halyqsoft.univercity.entity.beans.univercity.SCHEDULE_DETAIL;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.AbstractWidgetModel;
import org.r3a.common.vaadin.widget.AbstractWidgetPanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Omarbek Dinassil
 * @created Jun 2, 2016 10:28:29 AM
 */
@SuppressWarnings("serial")
final class ScheduleWidget extends AbstractWidgetPanel {

    private final SEMESTER_DATA semesterData;
    private ROOM room;
    private List<WEEK_DAY> weekDays;
    private List<TIME> times = new ArrayList<>();
    private GridLayout matrixGL;
    private boolean addComponent = true;

    private int cols = 8;
    private int rows = 0;

    ScheduleWidget(SEMESTER_DATA semesterData) {
        super();
        this.semesterData = semesterData;

        QueryModel<LESSON_TIME> lessonTimeQM = new QueryModel<>(LESSON_TIME.class);
        lessonTimeQM.addOrder("shift");
        QueryModel<WEEK_DAY> weekDayQM = new QueryModel<>(WEEK_DAY.class);
        weekDayQM.addOrder("id");
        try {
            List<LESSON_TIME> lessonTimes = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(
                    lessonTimeQM);
            for (LESSON_TIME lessonTime : lessonTimes) {
                QueryModel<TIME> timeQM = new QueryModel<>(TIME.class);
                timeQM.addWhere("id", ECriteria.EQUAL, lessonTime.getBeginTime().getId());
                timeQM.addOrder("timeValue");
                TIME time = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(timeQM);
                times.add(time);
            }
            weekDays = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(weekDayQM);

            rows = times.size() + 1;

            matrixGL = new GridLayout();
            matrixGL.setHeight(100, Unit.PERCENTAGE);
            matrixGL.setWidth(78, Unit.PERCENTAGE);
            matrixGL.setColumns(cols);
            matrixGL.setRows(rows);

            int i = 1, j = 1;
            for (TIME time : times) {
                Label timeLabel = new Label(time.toString());
                timeLabel.setHeightUndefined();
                timeLabel.setWidth(35, Unit.PIXELS);
                timeLabel.addStyleName("day-time");
                timeLabel.addStyleName("bold");
                matrixGL.addComponent(timeLabel, 0, j);
                matrixGL.setComponentAlignment(timeLabel, Alignment.MIDDLE_RIGHT);
                j++;
            }

            for (WEEK_DAY wd : weekDays) {
                Label l = new Label(wd.getDayNameRU());
                l.setSizeUndefined();
                l.addStyleName("day-time");
                l.addStyleName("bold");
                matrixGL.addComponent(l, i, 0);
                matrixGL.setComponentAlignment(l, Alignment.MIDDLE_CENTER);
                matrixGL.setColumnExpandRatio(i, 1);
                i++;
            }

            getContent().addComponent(matrixGL);
            getContent().setComponentAlignment(matrixGL, Alignment.MIDDLE_LEFT);
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load week days", ex);
        }
    }

    @Override
    protected void initWidget() throws Exception {
    }

    public ROOM getRoom() {
        return room;
    }

    public void setRoom(ROOM room) {
        this.room = room;
    }

    @Override
    public void refresh() throws Exception {
        QueryModel<SCHEDULE_DETAIL> sdQM = new QueryModel<>(SCHEDULE_DETAIL.class);
        FromItem lessonTimeFI = sdQM.addJoin(EJoin.INNER_JOIN, "lessonTime", LESSON_TIME.class, "id");
        sdQM.addWhereAnd("semesterData", ECriteria.EQUAL, semesterData.getId());
        for (WEEK_DAY wd : weekDays) {
            sdQM.addWhere("weekDay", ECriteria.EQUAL, wd.getId());
            for (TIME st : times) {
                sdQM.addWhere(lessonTimeFI, "beginTime", ECriteria.EQUAL, st.getId());
                try {
                    SCHEDULE_DETAIL scheduleDetail = null;
                    List<SCHEDULE_DETAIL> list = SessionFacadeFactory.getSessionFacade(
                            CommonEntityFacadeBean.class).lookup(sdQM);
                    if (!list.isEmpty()) {
                        scheduleDetail = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                lookup(SCHEDULE_DETAIL.class, list.get(0).getId());
                    }

                    ScheduleCell sc = new ScheduleCell(scheduleDetail);
                    int col = wd.getId().getId().intValue();
                    int row = st.getId().getId().intValue();

                    matrixGL.removeComponent(col, row);
                    if (scheduleDetail != null && (scheduleDetail.getLessonTime().getEndTime().getTimeValue()
                            - scheduleDetail.getLessonTime().getBeginTime().getTimeValue() == 2)) {
                        matrixGL.removeComponent(col, row + 1);
                        matrixGL.addComponent(sc, col, row, col, row + 1);
                        addComponent = false;
                    } else if (addComponent) {
                        matrixGL.addComponent(sc, col, row);
                    } else {
                        addComponent = true;
                    }

//                    if (col < cols && row < rows) {
//                        matrixGL.removeComponent(col, row);
//                        if (scheduleDetail != null && (scheduleDetail.getLessonTime().getEndTime().getTimeValue()
//                                - scheduleDetail.getLessonTime().getBeginTime().getTimeValue() == 2)) {
//                            matrixGL.addComponent(sc, col, row, col, row + 1);
//                            addComponent = false;
//                        } else if (addComponent) {
//                            matrixGL.addComponent(sc, col, row);
//                        } else {
//                            addComponent = true;
//                        }
//                    }
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to load schedule detail", ex);
                }
            }
        }
    }

    @Override
    public void localeChanged(Locale newLocale) {
    }

    @Override
    public AbstractWidgetModel getWidgetModel() {
        return null;
    }
}
