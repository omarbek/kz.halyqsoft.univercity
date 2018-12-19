package kz.halyqsoft.univercity.modules.deletedata;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.USER_DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.USER_PASSPORT;
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
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
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
    private TextField iinTF;
    private TextField codeTF;

    public DeleteDataView(AbstractTask task) throws Exception {
        super(task);
        filterPanel = new UserFilterPanel(new FUserFilter());
        userType = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookup(USER_TYPE.class, ID.valueOf(UserType.STUDENT_INDEX));
    }

    @Override
    public void initView(boolean b) throws Exception {
        filterPanel.addFilterPanelListener(this);

        codeTF = new TextField();
        codeTF.setNullRepresentation("");
        codeTF.setNullSettingAllowed(true);
        filterPanel.addFilterComponent("code", codeTF);

        userTypeCB = new ComboBox();
        userTypeCB.setNullSelectionAllowed(false);
        userTypeCB.setTextInputAllowed(false);
        userTypeCB.setFilteringMode(FilteringMode.OFF);
        userTypeCB.setPageLength(0);
        QueryModel<USER_TYPE> typeQM = new QueryModel<>(USER_TYPE.class);
        BeanItemContainer<USER_TYPE> typeBIC = new BeanItemContainer<>(USER_TYPE.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(typeQM));
        userTypeCB.setContainerDataSource(typeBIC);
        filterPanel.addFilterComponent("userType", userTypeCB);
        userTypeCB.setValue(userType);

        iinTF = new TextField();
        iinTF.setNullRepresentation("");
        iinTF.setNullSettingAllowed(true);
        filterPanel.addFilterComponent("iin", iinTF);

        getContent().addComponent(filterPanel);
        getContent().setComponentAlignment(filterPanel, Alignment.TOP_CENTER);

        usersGW = new GridWidget(USERS.class);
        usersGW.addEntityListener(this);
        usersGW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, false);
        usersGW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
        usersGW.setButtonVisible(AbstractToolbar.ADD_BUTTON, false);
        usersGW.setButtonVisible(AbstractToolbar.EDIT_BUTTON, false);
        DBGridModel usersGM = (DBGridModel) usersGW.getWidgetModel();
        usersGM.getColumnModel("created").setInGrid(true);
        usersGM.setMultiSelect(true);
        usersGM.setRefreshType(ERefreshType.MANUAL);

        refresh(userType.getId());

        getContent().addComponent(usersGW);
        getContent().setComponentAlignment(usersGW, Alignment.MIDDLE_CENTER);
    }

    private void refresh(ID userTypeId) {
        List<ID> values = new ArrayList<>();
        values.add(ID.valueOf(1));
        values.add(ID.valueOf(2));
        QueryModel<USERS> usersQM = new QueryModel<>(USERS.class);
        String iin = iinTF.getValue();
        if (iin != null && !iin.isEmpty()) {
            FromItem userDocFI = usersQM.addJoin(EJoin.INNER_JOIN, "id", USER_DOCUMENT.class, "user");
            FromItem userPassportFI = userDocFI.addJoin(EJoin.INNER_JOIN, "id", USER_PASSPORT.class, "id");
            usersQM.addWhere(userPassportFI, "iin", ECriteria.EQUAL, iin);
        }
        usersQM.addWhereNotIn("id", values);
        if (userTypeId != null) {
            usersQM.addWhere("typeIndex", ECriteria.EQUAL, userTypeId);
        }
        String code = codeTF.getValue();
        if (code != null && !code.isEmpty()) {
            usersQM.addWhere("code", ECriteria.EQUAL, code);
        }
        List<USERS> list = null;
        try {
            list = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(usersQM);
        } catch (Exception e) {
            CommonUtils.showMessageAndWriteLog("Unable to get list", e);
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
                        deleteMainTable(user, "employee_work_hour", "EMPLOYEE_ID");
                        deleteMainTable(user, "child", "EMPLOYEE_ID");
                        deleteMainTable(user, "employee", "ID");
                        updateGroups(user, "groups", "CURATOR_ID");

                    } else {
                        deleteMainTable(user, "entrant_speciality", "STUDENT_ID");
                        deleteUntRates(user);
                        deleteUserDoc(user, "unt_certificate", 7);
                        deleteUserDoc(user, "grant_doc", 8);
                        deleteMainTable(user, "student_relative", "STUDENT_ID");
                        deleteUserDoc(user, "student_contract", 9);
                        deleteUserDoc(user, "order_doc", 10);
                        deleteMainTable(user, "graduation_project", "ID");
                        deleteNonAdmissionExam(user,"non_admission_exam");
                        deleteMainTable(user, "student_creative_exam", "STUDENT_ID");
                        deleteMainTable(user, "student_education", "STUDENT_ID");
                        deleteMainTable(user, "student", "ID");
                        deleteMainTable(user,"complaint","USER_ID");
                        deleteMainTable(user,"user_roles","USER_ID");
                        deleteMainTable(user,"user_address","USER_ID");
                        deleteMainTable(user,"lost_and_found","CREATED_BY");
                        deleteMainTable(user,"student_fin_debt","STUDENT_ID");
                        deleteMainTable(user,"student_payment","STUDENT_ID");
                        deleteMainTable(user,"student_subject","STUDENT_ID");
                        deleteMainTable(user,"student_edu_rate","STUDENT_ID");
                        deleteMainTable(user,"student_edu_task","STUDENT_ID");
                        deleteMainTable(user,"student_journal_rate","STUDENT_ID");
                        deleteMainTable(user,"student_relative","STUDENT_ID");
                        deleteMainTable(user,"student_schedule","STUDENT_ID");
                    }
                    deleteMainTable(user, "user_award", "USER_ID");
                    deleteMainTable(user, "user_social_category", "USER_ID");
                    deleteSomeUserDoc(user, "medical_checkup", 4);
                    deleteUserDoc(user, "preemptive_right", 11);
                    deleteMainTable(user, "user_language", "USER_ID");
                    deleteSomeUserDoc(user, "education_doc", 3);
                    deleteUserDoc(user, "user_passport", 1);
                    deleteUserDoc(user, "military_doc", 2);
                    deleteUserDoc(user, "disability_doc", 5);
                    deleteUserDoc(user, "repatriate_doc", 6);
                    deleteUserDocFiles(user);
                    deleteMainTable(user, "user_address", "USER_ID");
                    deleteMainTable(user, "user_photo", "USER_ID");
                    deleteMainTable(user, "user_document", "USER_ID");
                    deleteMainTable(user, "users", "id");
                    deleteMainTable(user,"user_arrival","USER_ID");
                    deleteMainTable(user,"student_attendance_log","USER_ID");

                }
            } catch (Exception ignored) {
            }
            refresh(userType.getId());
            return false;
        }
        return true;
    }

    private void deleteSomeUserDoc(USERS user, String table, int documentTypeId) throws Exception {
        try {
            String sql = "delete from " + table + " where id in " +
                    "(select id from user_document where user_id = ?1 and document_type_id = " + documentTypeId + ")";
            Map<Integer, Object> params = new HashMap<>();
            params.put(1, user.getId().getId());
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
        } catch (Exception ignored) {
        }
    }

    private void deleteUserDocFiles(USERS user) {
        try {
            String sql = "delete from user_document_file where USER_DOC_ID in" +
                    " (select id from user_document where user_id = ?1)";
            Map<Integer, Object> params = new HashMap<>();
            params.put(1, user.getId().getId());
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
        } catch (Exception ignored) {
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
        } catch (Exception ignored) {
        }
    }

    private void deleteMainTable(USERS user, String table, String field) {
        try {
            String sql = "delete from " + table + " where " + field + " = ?1";
            Map<Integer, Object> params = new HashMap<>();
            params.put(1, user.getId().getId());
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
        } catch (Exception ignored) {
        }
    }

    private void updateGroups(USERS user, String table, String field) {
        try {
            String sql = "UPDATE "+ table +" SET "+ field+ " = null WHERE "+ field+ " = ?1";
            Map<Integer, Object> params = new HashMap<>();
            params.put(1, user.getId().getId());
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
        } catch (Exception ignored) {
        }
    }

    private void deleteNonAdmissionExam(USERS user, String table) {
        try {
            String sql = "DELETE " + table + " \n" +
                    "where u.student_education_id = (SELECT id from student_education se where se.student_id=?1);";
            Map<Integer, Object> params = new HashMap<>();
            params.put(1, user.getId().getId());
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
        } catch (Exception ignored) {
        }
    }

    private void deleteUserDoc(USERS user, String table, int documentTypeId) {
        try {
            String sql = "delete from " + table + " where id = " +
                    "(select id from user_document where user_id = ?1 and document_type_id = " + documentTypeId + ")";
            Map<Integer, Object> params = new HashMap<>();
            params.put(1, user.getId().getId());
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
        } catch (Exception ignored) {
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
        } catch (Exception ignored) {
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
