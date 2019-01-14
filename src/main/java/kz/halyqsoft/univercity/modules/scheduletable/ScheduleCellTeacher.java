package kz.halyqsoft.univercity.modules.scheduletable;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.GROUPS;
import kz.halyqsoft.univercity.entity.beans.univercity.SCHEDULE_DETAIL;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LESSON_TYPE;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.entity.ID;

import java.util.ArrayList;
import java.util.List;


@SuppressWarnings({"serial"})
final class ScheduleCellTeacher extends VerticalLayout {

    private final List<SCHEDULE_DETAIL> scheduleDetails = new ArrayList<>();
    private final Label text;

    ScheduleCellTeacher(List<SCHEDULE_DETAIL> scheduleDetails) {
        super();
        this.scheduleDetails.addAll(scheduleDetails);

        if (scheduleDetails.size() > 0 && (scheduleDetails.get(0).getLessonTime().getEndTime().getTimeValue()
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
                    sb.append(scheduleDetail.getSubject().toString());
                    sb.append(" ");
                    LESSON_TYPE lt = scheduleDetail.getLessonType();
                    if (lt.getId().equals(ID.valueOf(2))) {
                        sb.append(lt.toString().charAt(0) + lt.toString().charAt(2));
                    } else {
                        sb.append(scheduleDetail.getLessonType().toString().charAt(0));
                    }
                    sb.append(" ");
                    sb.append(scheduleDetail.getRoom().getRoomNo());
                    flag = false;
                }
                sb.append(" ");
                GROUPS group = scheduleDetail.getGroup();
                sb.append(group.getName());
            }
            addStyleName("schedule-cell-full");
        } else {
            sb.append(CommonUtils.getUILocaleUtil().getCaption("no.lesson"));
        }
        sb.append("</center>");
        text.setValue(sb.toString());
    }
}