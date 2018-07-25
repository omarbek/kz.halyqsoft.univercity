package kz.halyqsoft.univercity.modules.graduateemployment;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.TabSheet;
import kz.halyqsoft.univercity.entity.beans.univercity.GRADUATE_EMPLOYMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VGraduateEmployment;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_STUDENT;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.EntityUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

    public class GraduateEmploymentView extends AbstractTaskView {
        private GridWidget graduateEmploymentGW;
        private GridWidget newGraduateEmploymentGW;
        private TabSheet tabSheet;

        public GridWidget getNewGraduateEmploymentGW() {
            return newGraduateEmploymentGW;
        }

        public void setNewGraduateEmploymentGW(GridWidget newGraduateEmploymentGW) {
            this.newGraduateEmploymentGW = newGraduateEmploymentGW;
        }

        public GraduateEmploymentView(AbstractTask task)throws Exception{
            super(task);
        }

        @Override
        public void initView(boolean b) throws Exception {

            newGraduateEmploymentGW = new GridWidget(GRADUATE_EMPLOYMENT.class);
            newGraduateEmploymentGW.setImmediate(true);
            newGraduateEmploymentGW.showToolbar(true);
            newGraduateEmploymentGW.setButtonVisible(IconToolbar.PREVIEW_BUTTON, false);
            newGraduateEmploymentGW.setButtonVisible(IconToolbar.REFRESH_BUTTON, false);
            newGraduateEmploymentGW.addEntityListener(new GraduateEmploymentEntity());

            DBGridModel newGraduateEmploymentGM = (DBGridModel) newGraduateEmploymentGW.getWidgetModel();
            newGraduateEmploymentGM.setEntities(getEntities());
            newGraduateEmploymentGM.setRowNumberVisible(true);
            newGraduateEmploymentGM.setMultiSelect(true);

            graduateEmploymentGW = new GridWidget(VGraduateEmployment.class);
            graduateEmploymentGW.setImmediate(true);
            graduateEmploymentGW.showToolbar(false);
            graduateEmploymentGW.setButtonVisible (AbstractToolbar.REFRESH_BUTTON, true);

            DBGridModel graduateEmploymentGM = (DBGridModel) graduateEmploymentGW.getWidgetModel();
            graduateEmploymentGM.setEntities(getList());
            graduateEmploymentGM.setRefreshType(ERefreshType.MANUAL);

            getContent().addComponent(newGraduateEmploymentGW);
            getContent().setComponentAlignment(newGraduateEmploymentGW,Alignment.MIDDLE_CENTER);

            getContent().addComponent(graduateEmploymentGW);
            getContent().setComponentAlignment(graduateEmploymentGW, Alignment.MIDDLE_CENTER);
        }

        public List<VGraduateEmployment> getList() {

            List<VGraduateEmployment> list = new ArrayList<>();
            Map<Integer, Object> params = new HashMap<>();
            String sql = "SELECT\n" +
                    "  spec.spec_name,\n" +
                    "  count(speciality_id),\n" +
                    "  count(nullif(employed = false, true)),\n" +
                    "  count(nullif(by_speciality = false, true)),\n" +
                    "  count(nullif(master = false, true)),\n" +
                    "  count(nullif(decree = false, true)),\n" +
                    "  count(nullif(army = false, true))\n" +
                    "  FROM graduate_employment\n" +
                    "INNER JOIN v_student s2 on graduate_employment.student_id = s2.id\n" +
                    "RIGHT JOIN speciality spec on s2.speciality_id = spec.id\n" +
                    "GROUP BY spec.spec_name;";

            try {
                List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
                if (!tmpList.isEmpty()) {
                    for (Object o : tmpList) {
                        Object[] oo = (Object[]) o;
                        VGraduateEmployment ve = new VGraduateEmployment();
                        ve.setSpecName((String) oo[0]);
                        ve.setGraduatedSum((long) oo[1]);
                        ve.setEmployedSum((long) oo[2]);
                        ve.setBySpecSum((long) oo[3]);
                        ve.setMasterSum((long) oo[4]);
                        ve.setDecreeSum((long) oo[5]);
                        ve.setArmySum((long) oo[6]);
                        list.add(ve);
                    }
                }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load teacher list", ex);
            }
            refreshList(list);
            return list;
        }

        public List<GRADUATE_EMPLOYMENT> getEntities() {

            List<GRADUATE_EMPLOYMENT> getList = new ArrayList<>();
                Map<Integer, Object> params = new HashMap<>();
                String sql = "SELECT * from graduate_employment ";

                try {
                    List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
                    if (!tmpList.isEmpty()) {
                        for (Object o : tmpList) {
                            Object[] oo = (Object[]) o;
                            GRADUATE_EMPLOYMENT ve = new GRADUATE_EMPLOYMENT();
                            ve.setId(ID.valueOf((long)oo[0]));
                            QueryModel<V_STUDENT> queryModel = new QueryModel<>(V_STUDENT.class);
                            queryModel.addWhere("id" , ECriteria.EQUAL, ID.valueOf((long)oo[1]));
                            V_STUDENT vStudent = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(queryModel);
                            ve.setStudent(vStudent);
                            ve.setEmployed((boolean) oo[2]);
                            ve.setBySpeciality((boolean) oo[3]);
                            ve.setMaster((boolean) oo[4]);
                            ve.setDecree((boolean) oo[5]);
                            ve.setArmy((boolean) oo[6]);
                            getList.add(ve);
                        }
                    }
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to load teacher list", ex);
            }
            refreshGWList(getList);
            return getList;
        }

        private void refreshList(List<VGraduateEmployment> list) {
            ((DBGridModel) graduateEmploymentGW.getWidgetModel()).setEntities(list);
            try {
                graduateEmploymentGW.refresh();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to refresh employment list", ex);
            }
        }

        private void refreshGWList (List<GRADUATE_EMPLOYMENT> getList) {
            ((DBGridModel) newGraduateEmploymentGW.getWidgetModel()).setEntities(getList);
            try {
                newGraduateEmploymentGW.refresh();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to refresh employment list", ex);
            }
        }

        public void refresh() {
            try {
                newGraduateEmploymentGW.refresh();
                graduateEmploymentGW.refresh();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


       private class GraduateEmploymentEntity  extends EntityUtils {

           @Override
           protected void init(Object source, Entity e, boolean isNew) throws Exception {
               GRADUATE_EMPLOYMENT graduateEmployment = (GRADUATE_EMPLOYMENT) e;
               new GraduateEmploymentEdit(graduateEmployment, isNew, GraduateEmploymentView.this);
           }

           @Override
           protected GridWidget getGridWidget() {
               return newGraduateEmploymentGW;
           }

           @Override
           protected String getModuleName() {
               return "Graduate Exam View";
           }

           @Override
           protected Class<? extends Entity> getEntityClass() {
               return GRADUATE_EMPLOYMENT.class;
           }

           @Override
           protected void removeChildrenEntity(List<Entity> delList) throws Exception {
           }

           @Override
           protected void refresh() throws Exception {
                   newGraduateEmploymentGW.refresh();
           }
       }
    }
