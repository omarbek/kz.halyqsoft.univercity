package kz.halyqsoft.univercity.modules.studentattendancelog;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_ATTENDANCE_LOG;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.USER_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.enumeration.UserType;
import kz.halyqsoft.univercity.filter.FUserFilter;
import kz.halyqsoft.univercity.filter.panel.UserFilterPanel;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Omarbek
 * @created on 06.09.2018
 */
public class StudentAttendanceLogView extends AbstractTaskView implements FilterPanelListener {

    private GridWidget studentAttendanceLogGW;
    private final UserFilterPanel filterPanel;
    private USER_TYPE userType;

    public StudentAttendanceLogView(AbstractTask task) throws Exception {
        super(task);
        filterPanel = new UserFilterPanel(new FUserFilter());
        userType = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookup(USER_TYPE.class, ID.valueOf(UserType.STUDENT_INDEX));
    }

    @Override
    public void initView(boolean b) throws Exception {
        filterPanel.addFilterPanelListener(this);

        ComboBox userTypeCB = new ComboBox();
        userTypeCB.setNullSelectionAllowed(false);
        userTypeCB.setTextInputAllowed(false);
        userTypeCB.setFilteringMode(FilteringMode.OFF);
        userTypeCB.setPageLength(0);
        QueryModel<USER_TYPE> typeQM = new QueryModel<>(USER_TYPE.class);
        BeanItemContainer<USER_TYPE> typeBIC = new BeanItemContainer<>(USER_TYPE.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(typeQM));
        userTypeCB.setContainerDataSource(typeBIC);
        filterPanel.addFilterComponent("userType", userTypeCB);
        getContent().addComponent(filterPanel);
        getContent().setComponentAlignment(filterPanel, Alignment.TOP_CENTER);

        userTypeCB.setValue(userType);
        studentAttendanceLogGW = new GridWidget(STUDENT_ATTENDANCE_LOG.class);
        studentAttendanceLogGW.showToolbar(false);

        DBGridModel studentAttendanceLogGM = (DBGridModel) studentAttendanceLogGW.getWidgetModel();
        studentAttendanceLogGM.setRowNumberVisible(true);
        studentAttendanceLogGM.setMultiSelect(true);
        studentAttendanceLogGM.setRefreshType(ERefreshType.MANUAL);

        refresh(userType.getId());

        getContent().addComponent(studentAttendanceLogGW);
    }

    private void refresh(ID userTypeId) {
        try {
            String sql = "SELECT log.* " +
                    "FROM student_attendance_log log " +
                    "  INNER JOIN users usr ON usr.id = log.user_id " +
                    "WHERE usr.deleted = FALSE AND usr.locked = FALSE AND usr.user_type_id = ?1";
            Map<Integer, Object> params = new HashMap<>();
            params.put(1, userTypeId.getId());
            List<STUDENT_ATTENDANCE_LOG> studentAttendanceLogs = SessionFacadeFactory.getSessionFacade(
                    CommonEntityFacadeBean.class).lookup(sql, params, STUDENT_ATTENDANCE_LOG.class);
            ((DBGridModel) studentAttendanceLogGW.getWidgetModel()).setEntities(studentAttendanceLogs);
            studentAttendanceLogGW.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doFilter(AbstractFilterBean abstractFilterBean) {
        FUserFilter fUserFilter = (FUserFilter) abstractFilterBean;
        refresh(fUserFilter.getUserType().getId());
    }

    @Override
    public void clearFilter() {
        refresh(userType.getId());
    }
}
