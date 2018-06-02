package kz.halyqsoft.univercity.modules.deletedata;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.USER_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.enumeration.UserType;
import kz.halyqsoft.univercity.filter.FUserFilter;
import kz.halyqsoft.univercity.filter.panel.UserFilterPanel;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Omarbek
 * @created on 31.05.2018
 */
public class DeleteDataView extends AbstractTaskView implements EntityListener, FilterPanelListener {

    private final UserFilterPanel filterPanel;
    private GridWidget usersGW;
    private ComboBox userTypeCB;

    private USER_TYPE userType;

    public DeleteDataView(AbstractTask task) throws Exception {
        super(task);
        filterPanel = new UserFilterPanel(new FUserFilter());
        userType = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookup(USER_TYPE.class, ID.valueOf(UserType.STUDENT_INDEX));
    }

    @Override
    public void initView(boolean b) throws Exception {
        filterPanel.addFilterPanelListener(this);

        userTypeCB = new ComboBox();
        userTypeCB.setNullSelectionAllowed(true);
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

        usersGW = new GridWidget(USERS.class);
        usersGW.addEntityListener(this);
        usersGW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, false);
        usersGW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
        usersGW.setButtonVisible(AbstractToolbar.ADD_BUTTON, false);
        usersGW.setButtonVisible(AbstractToolbar.EDIT_BUTTON, false);
        DBGridModel examGM = (DBGridModel) usersGW.getWidgetModel();
        examGM.setMultiSelect(true);
        examGM.setRefreshType(ERefreshType.MANUAL);

        refresh(null);

        getContent().addComponent(usersGW);
        getContent().setComponentAlignment(usersGW, Alignment.MIDDLE_CENTER);
    }

    private void refresh(ID userTypeId) {
        QueryModel<USERS> usersQM = new QueryModel<>(USERS.class);
        List<ID> values = new ArrayList<>();
        values.add(ID.valueOf(1));
        values.add(ID.valueOf(2));
        usersQM.addWhereNotIn("id", values);
        if (userTypeId != null) {
            usersQM.addWhere("typeIndex", ECriteria.EQUAL, userTypeId);
        }
        List<USERS> list = null;
        try {
            list = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(usersQM);
        } catch (Exception e) {
            e.printStackTrace();//TODO catch
        }
        ((DBGridModel) usersGW.getWidgetModel()).setEntities(list);
        try {
            usersGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh users", ex);
        }
    }

    @Override
    public boolean preDelete(Object source, List<Entity> entities, int buttonId) {
        if (source.equals(usersGW)) {
            try {
                for (Entity e : entities) {
                    USERS user = (USERS) e;
                    if (user.getTypeIndex() == 1) {
                        deleteUserDoc(user, "employee_degree", 12);
                        deleteEmplScient(user, "publication", 1);
                        deleteEmplScient(user, "scientific_activity", 2);
                        deleteEmplScient(user, "scientific_management", 3);
                        deleteMainTable(user, "previous_experience", "EMPLOYEE_ID");
                        deleteMainTable(user, "employee_dept", "EMPLOYEE_ID");
                        deleteMainTable(user, "employee_scientific", "EMPLOYEE_ID");
                        deleteMainTable(user, "employee", "ID");
                    } else {
                        deleteMainTable(user, "entrant_speciality", "STUDENT_ID");
                        deleteUntRates(user);
                        deleteUserDoc(user, "unt_certificate", 7);
                        deleteUserDoc(user, "grant_doc", 8);
                        deleteMainTable(user, "student_relative", "STUDENT_ID");
                        deleteUserDoc(user, "student_contract", 9);
                        deleteMainTable(user, "graduation_project", "ID");
                        deleteMainTable(user, "student_education", "student_id");
                        deleteMainTable(user, "student", "ID");
                    }
                    deleteMainTable(user, "user_award", "USER_ID");
                    deleteMainTable(user, "user_social_category", "USER_ID");
                    deleteUserDoc(user, "medical_checkup", 4);
                    deleteUserDoc(user, "preemptive_right", 11);
                    deleteMainTable(user, "user_language", "USER_ID");
                    deleteUserDoc(user, "education_doc", 3);
                    deleteUserDoc(user, "user_passport", 1);
                    deleteUserDoc(user, "military_doc", 2);
                    deleteUserDoc(user, "disability_doc", 5);
                    deleteUserDoc(user, "repatriate_doc", 6);
                    deleteUserDocFiles(user);
                    deleteMainTable(user, "user_address", "USER_ID");
                    deleteMainTable(user, "user_photo", "USER_ID");
                    deleteMainTable(user, "user_document", "USER_ID");
                    deleteMainTable(user, "users", "id");
                }
            } catch (Exception e1) {
                e1.printStackTrace();//TODO catch
            }
            refresh(((USER_TYPE) userTypeCB.getValue()).getId());
            return false;
        }
        return true;
    }

    private void deleteUserDocFiles(USERS user) {
        try {
            String sql = "delete from user_document_file where USER_DOC_ID in" +
                    " (select id from user_document where user_id = ?1)";
            Map<Integer, Object> params = new HashMap<>();
            params.put(1, user.getId().getId());
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteUntRates(USERS user) {
        try {
            String sql = "delete from unt_cert_subject where UNT_CERTIFICATE_ID = " +
                    "(select unt.id from unt_certificate unt" +
                    " inner join user_document usr_doc on usr_doc.id=unt.id" +
                    " where usr_doc.user_id = ?1)";
            Map<Integer, Object> params = new HashMap<>();
            params.put(1, user.getId().getId());
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteMainTable(USERS user, String table, String field) {
        try {
            String sql = "delete from " + table + " where " + field + " = ?1";
            Map<Integer, Object> params = new HashMap<>();
            params.put(1, user.getId().getId());
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteUserDoc(USERS user, String table, int documentTypeId) {
        try {
            String sql = "delete from " + table + " where id = " +
                    "(select id from user_document where user_id = ?1 and document_type_id = " + documentTypeId + ")";
            Map<Integer, Object> params = new HashMap<>();
            params.put(1, user.getId().getId());
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteEmplScient(USERS user, String table, int scientificTypeId) {
        try {
            String sql = "delete from " + table + " where id = " +
                    "(select id from employee_scientific where EMPLOYEE_ID = ?1 and scientific_type_id = ?2)";
            Map<Integer, Object> params = new HashMap<>();
            params.put(1, user.getId().getId());
            params.put(2, scientificTypeId);
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
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
        refresh(null);
    }
}
