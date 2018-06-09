package kz.halyqsoft.univercity.modules.academiccalendar.layout;

import com.vaadin.ui.AbstractLayout;
import kz.halyqsoft.univercity.modules.academiccalendar.item.AbstractACItem;

import java.util.List;

/**
 * @author Omarbek Dinassil
 * @created Oct 26, 2016 9:38:38 AM
 */
public interface AcademicCalendarLayout {

    void initLayout() throws Exception;

    void save() throws Exception;

    AbstractLayout getLayout();

    List<AbstractACItem> getItems();
}
