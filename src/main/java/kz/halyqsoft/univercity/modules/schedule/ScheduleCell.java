package kz.halyqsoft.univercity.modules.schedule;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE;
import kz.halyqsoft.univercity.entity.beans.univercity.SCHEDULE_DETAIL;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LESSON_TYPE;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.entity.ID;

/**
 * @author Omarbek Dinassil
 * @created Jun 2, 2016 10:25:56 AM
 */
@SuppressWarnings({"serial"})
final class ScheduleCell extends VerticalLayout {

    private final SCHEDULE_DETAIL scheduleDetail;
    private final Label text;

    public ScheduleCell(SCHEDULE_DETAIL scheduleDetail) {
        super();
        this.scheduleDetail = scheduleDetail;

        if (scheduleDetail != null && (scheduleDetail.getLessonTime().getEndTime().getTimeValue()
                - scheduleDetail.getLessonTime().getBeginTime().getTimeValue() == 2)) {
            setHeight(200, Unit.PIXELS);
        } else {
            setHeight(100, Unit.PIXELS);
        }
        addStyleName("bordered");
        addStyleName("schedule-cell");

        text = new Label();
        text.addStyleName("schedule-cell");
        text.setContentMode(ContentMode.HTML);
        addComponent(text);
        setComponentAlignment(text, Alignment.MIDDLE_CENTER);
        setExpandRatio(text, 1);

        refresh();
    }

    private void refresh() {
        StringBuilder sb = new StringBuilder();
        sb.append("<center>");
        if (scheduleDetail != null && scheduleDetail.getId() != null) {
            sb.append(scheduleDetail.getSubject().toString());
            sb.append(" ");
            LESSON_TYPE lt = scheduleDetail.getLessonType();
            if (lt.getId().equals(ID.valueOf(2))) {
                sb.append(lt.toString().charAt(0) + lt.toString().charAt(2));
            } else {
                sb.append(scheduleDetail.getLessonType().toString().charAt(0));
            }
            sb.append(" ");
            EMPLOYEE teacher = scheduleDetail.getTeacher();
            sb.append(teacher.getLastName());
            sb.append(" ");
            if (teacher.getFirstName() != null && !teacher.getFirstName().trim().isEmpty()) {
                sb.append(teacher.getFirstName().charAt(0));
                sb.append(". ");
            }
            if (teacher.getMiddleName() != null && !teacher.getMiddleName().trim().isEmpty()) {
                sb.append(teacher.getMiddleName().charAt(0));
                sb.append('.');
            }
            sb.append(scheduleDetail.getRoom().getRoomNo());
            addStyleName("schedule-cell-full");
        } else {
            sb.append(CommonUtils.getUILocaleUtil().getCaption("no.lesson"));
        }
        sb.append("</center>");
        text.setValue(sb.toString());
    }
}
