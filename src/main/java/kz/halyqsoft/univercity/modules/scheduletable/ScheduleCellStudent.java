package kz.halyqsoft.univercity.modules.scheduletable;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE;
import kz.halyqsoft.univercity.entity.beans.univercity.GROUPS;
import kz.halyqsoft.univercity.entity.beans.univercity.SCHEDULE_DETAIL;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LESSON_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ROOM;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.entity.ID;

import java.util.List;


@SuppressWarnings({"serial"})
final class ScheduleCellStudent extends VerticalLayout {

    private final List<SCHEDULE_DETAIL> scheduleDetails;
    private final Label text;

    ScheduleCellStudent(List<SCHEDULE_DETAIL> scheduleDetails) {
        super();
        this.scheduleDetails = scheduleDetails;

        if (scheduleDetails.size() > 0 &&scheduleDetails != null && (scheduleDetails.get(0).getLessonTime().getEndTime().getTimeValue()
                - scheduleDetails.get(0).getLessonTime().getBeginTime().getTimeValue() == 2)) {
            setHeight("100%");
        } else {
            setHeight("100%");
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

        if (scheduleDetails.size() > 0) {
            boolean flag = true;
            for(SCHEDULE_DETAIL scheduleDetail : scheduleDetails){

                if(flag){
                    sb.append(scheduleDetail.getSubject().getNameKZ());
                    flag = false;
                    sb.append("<br>");
                }

                LESSON_TYPE lt = scheduleDetail.getLessonType();
                sb.append(lt.getTypeShortName());
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
                sb.append(" ");
                sb.append(CommonUtils.getUILocaleUtil().getEntityLabel(ROOM.class) + " "+  scheduleDetail.getRoom().getRoomNo());
                sb.append("<hr>");

            }
            addStyleName("schedule-cell-full");
        } else {
            sb.append(CommonUtils.getUILocaleUtil().getCaption("no.lesson"));
        }

        sb.append("</center>");
        text.setValue(sb.toString());
    }
}