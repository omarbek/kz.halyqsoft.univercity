package kz.halyqsoft.univercity.modules.checkstudents;

import kz.halyqsoft.univercity.entity.beans.USERS;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CheckStudentsView extends AbstractTaskView implements EntityListener {

    private GridWidget gridWidget;
    private DBGridModel dbGridModel;

    public CheckStudentsView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        gridWidget = new GridWidget(USERS.class);
        gridWidget.setImmediate(true);
        gridWidget.addEntityListener(this);
        gridWidget.setButtonVisible(IconToolbar.ADD_BUTTON, false);
        gridWidget.setButtonVisible(IconToolbar.EDIT_BUTTON, false);
        gridWidget.setMultiSelect(true);

        dbGridModel = (DBGridModel) gridWidget.getWidgetModel();
        dbGridModel.setRefreshType(ERefreshType.MANUAL);
        dbGridModel.setEntities(getStudents());
        dbGridModel.setDeferredDelete(true);
        getContent().addComponent(gridWidget);
    }

    public class EntityStore {
        public String fieldName;
        public Class<? extends Entity> className;

        public EntityStore(Class<? extends Entity> className, String fieldName) {
            this.fieldName = fieldName;
            this.className = className;
        }
    }

    @Override
    public void onDelete(Object source, List<Entity> entities, int buttonId) {
//        ArrayList<EntityStore> entityStores = new ArrayList<>();
//        entityStores.add(new EntityStore(ENTRANT_SPECIALITY.class , "student"));
//        entityStores.add(new EntityStore(STUDENT_CREATIVE_EXAM.class , "student"));
//        entityStores.add(new EntityStore(STUDENT.class , "id"));
//        entityStores.add(new EntityStore(USERS.class , "id"));
//
//        for(Entity entity:entities)
//        {
//            for(EntityStore entityStore: entityStores)
//            {
//                Entity result  = getFromDB(entity , entityStore.className , entityStore.fieldName);
//
//                if(result!=null)
//                {
//                    if(entityStore.className.equals(STUDENT_CREATIVE_EXAM.class))
//                    {
//                        List<Entity> moreEnt = getAllFromDB(result , STUDENT_CREATIVE_EXAM_SUBJECT.class ,"studentCreativeExam" );
//                        for(Entity entity1 : moreEnt)
//                        {
//                            delete(entity1);
//                        }
//                    }else if(){
//
//                    }
//                    delete(result);
//                }
//            }
//        }

        try {
            for (Entity e : entities) {
                USERS user = (USERS) e;

                deleteMainTable(user, "entrant_speciality", "STUDENT_ID");
                deleteUntRates(user);
                deleteUserDoc(user, "unt_certificate", 7);
                deleteUserDoc(user, "grant_doc", 8);
                deleteMainTable(user, "student_relative", "STUDENT_ID");
                deleteUserDoc(user, "student_contract", 9);
                deleteMainTable(user, "graduation_project", "ID");
                deleteMainTable(user, "student_education", "student_id");
                deleteMainTable(user, "student", "ID");

                deleteMainTable(user, "user_award", "USER_ID");
                deleteMainTable(user, "user_social_category", "USER_ID");
                deleteUserDoc(user, "medical_checkup", 4);
                deleteUserDoc(user, "preemptive_right", 11);
                deleteMainTable(user, "user_language", "USER_ID");

                String sql = "delete from education_doc where id in " +
                        "(select id from user_document where user_id = ?1 and document_type_id = 3)";
                Map<Integer, Object> params = new HashMap<>();
                params.put(1, user.getId().getId());
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);

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
        } catch (Exception ignored) {
        }
        refresh();
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

    public Entity getFromDB(Entity entity, Class<? extends Entity> className, String fieldName) {
        QueryModel queryModel = new QueryModel(className);
        queryModel.addWhere(fieldName, ECriteria.EQUAL, entity.getId());
        try {
            return SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(queryModel);
        } catch (NoResultException noe) {
            System.out.println("No result : " + noe.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Entity> getAllFromDB(Entity entity, Class<? extends Entity> className, String fieldName) {
        QueryModel queryModel = new QueryModel(className);
        queryModel.addWhere(fieldName, ECriteria.EQUAL, entity.getId());
        try {
            return SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(queryModel);
        } catch (NoResultException noe) {
            System.out.println("No result : " + noe.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void delete(Entity entity) {
        try {
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(entity);
        } catch (Exception e) {
            Message.showError(e.getMessage());
            e.printStackTrace();
        }
    }


    public void refresh() {
        try {
            dbGridModel.setEntities(getStudents());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRefresh(Object source, List<Entity> entities) {
        dbGridModel.setEntities(getStudents());

    }


    private List<USERS> getStudents() {

    String sql = "select usr.* from users usr " +
            "INNER JOIN student stu on stu.id=usr.id " +
            "where usr.id not in (select id from v_student) and usr.user_type_id=2 " +
            "and stu.category_id=1 and usr.deleted=false";
        List<USERS> list = new ArrayList<>();
        try {
            Map<Integer, Object> params = new HashMap<>();
            list = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(sql, params, USERS.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
}
