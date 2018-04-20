package kz.halyqsoft.univercity.modules.employee.workhour;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Button;
import kz.halyqsoft.univercity.entity.beans.univercity.AbstractWorkHourEntity;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.WORK_HOUR_STATUS;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;

/**
 * @author Omarbek Dinassil
 * @created Jan 2, 2016 8:17:52 PM
 */
@SuppressWarnings("serial")
final class DayHourButton extends Button {

    private static final String ICON_PATH = "img/button/";
    private final AbstractWorkHourEntity awhe;
    private final AbstractWorkHourEntity clone;

    DayHourButton(AbstractWorkHourEntity awhe, boolean readOnly) throws Exception {
        super();
        this.awhe = awhe;
        this.clone = awhe.getClone();

        init(readOnly);
        refresh();
    }

    private void init(boolean readOnly) {
        setHeight(30, Unit.PIXELS);
        setWidth(30, Unit.PIXELS);
        String descr = awhe.getWeekDay().toString() + ". " + awhe.getDayHour().toString();
        setDescription(descr);
        setEnabled(!readOnly);
        addStyleName("icon-only");
        addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent ev) {
                WORK_HOUR_STATUS whs = awhe.getWorkHourStatus();
                try {
                    ID id = whs.getId();
                    if (id.equals(ID.valueOf(1))) {
                        id = ID.valueOf(2);
                    } else {
                        id = ID.valueOf(1);
                    }
                    whs = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                            lookup(WORK_HOUR_STATUS.class, id);
                    awhe.setWorkHourStatus(whs);
                    awhe.setChanged(true);
                    refresh();
                } catch (Exception ex) {
                    CommonUtils.showMessageAndWriteLog("Unable to change work hour status", ex);
                }
            }
        });
    }

    private void refresh() {
        String iconPath = ICON_PATH;
        if (awhe.getWorkHourStatus().getId().equals(ID.valueOf(1))) {
            iconPath += "active.png";
        } else {
            iconPath += "inactive.png";
        }

        setIcon(new ThemeResource(iconPath));
    }

    public void cancel() {
        if (awhe.isChanged()) {
            awhe.setWorkHourStatus(clone.getWorkHourStatus());
            awhe.setChanged(false);
            refresh();
        }
    }
}
