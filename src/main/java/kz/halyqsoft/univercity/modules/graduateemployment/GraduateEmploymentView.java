package kz.halyqsoft.univercity.modules.graduateemployment;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.TabSheet;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VGraduate;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VGraduateEmployment;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.EntityUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
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
    private GridWidget sumOfGraduateGW;
    private GridWidget graduateEmploymentGW;
    private TabSheet tabSheet;

    public GridWidget getGraduateEmploymentGW() {
        return graduateEmploymentGW;
    }

    public void setGraduateEmploymentGW(GridWidget graduateEmploymentGW) {
        this.graduateEmploymentGW = graduateEmploymentGW;
    }

    public GraduateEmploymentView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {

        graduateEmploymentGW = new GridWidget(VGraduate.class);
        graduateEmploymentGW.addEntityListener(new GraduateEmploymentEntity());
        graduateEmploymentGW.setImmediate(true);
        graduateEmploymentGW.showToolbar(true);
        graduateEmploymentGW.setButtonVisible(IconToolbar.PREVIEW_BUTTON, false);
        graduateEmploymentGW.setButtonVisible(IconToolbar.REFRESH_BUTTON, false);

        DBGridModel graduateEmploymentGM = (DBGridModel) graduateEmploymentGW.getWidgetModel();
        graduateEmploymentGM.setRowNumberVisible(true);
        graduateEmploymentGM.setMultiSelect(true);
        graduateEmploymentGM.setRefreshType(ERefreshType.MANUAL);
        graduateEmploymentGM.setEntities(getEntities());

        sumOfGraduateGW = new GridWidget(VGraduateEmployment.class);
        sumOfGraduateGW.setImmediate(true);
        sumOfGraduateGW.showToolbar(false);
        sumOfGraduateGW.setButtonVisible(AbstractToolbar.REFRESH_BUTTON, true);

        DBGridModel sumOfGraduateGM = (DBGridModel) sumOfGraduateGW.getWidgetModel();
        sumOfGraduateGM.setEntities(getList());
        sumOfGraduateGM.setRefreshType(ERefreshType.MANUAL);

        getContent().addComponent(graduateEmploymentGW);
        getContent().setComponentAlignment(graduateEmploymentGW, Alignment.MIDDLE_CENTER);

        getContent().addComponent(sumOfGraduateGW);
        getContent().setComponentAlignment(sumOfGraduateGW, Alignment.MIDDLE_CENTER);
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

    public List<VGraduate> getEntities() {

        List<VGraduate> getList = new ArrayList<>();
        Map<Integer, Object> params = new HashMap<>();
        String sql = "SELECT ge.id,\n" +
                "                           trim(vs.LAST_NAME || ' ' || vs.FIRST_NAME || ' ' || coalesce(vs.MIDDLE_NAME, '')) FIO,\n" +
                "                          case when ge.employed = true then '+' else '-' end employed,\n" +
                "                          case when ge.by_speciality = true then '+' else '-' end bySpeciality,\n" +
                "                          case when ge.master = true then '+' else '-' end master,\n" +
                "                          case when ge.decree = true then '+' else '-' end decree,\n" +
                "                          case when ge.army = true then '+' else '-' end army\n" +
                "                        from graduate_employment ge\n" +
                " INNER JOIN v_student vs on vs.id=ge.student_id;";

        try {
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VGraduate ve = new VGraduate();
                    ve.setId(ID.valueOf((long) oo[0]));
                    ve.setFIO((String) oo[1]);
                    ve.setEmployed((String) oo[2]);
                    ve.setBySpeciality((String) oo[3]);
                    ve.setMaster((String) oo[4]);
                    ve.setDecree((String) oo[5]);
                    ve.setArmy((String) oo[6]);
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
        ((DBGridModel) sumOfGraduateGW.getWidgetModel()).setEntities(list);
        try {
            sumOfGraduateGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh employment list", ex);
        }
    }

    private void refreshGWList(List<VGraduate> getList) {
        ((DBGridModel) graduateEmploymentGW.getWidgetModel()).setEntities(getList);
        try {
            graduateEmploymentGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh employment list", ex);
        }
    }

    public void refresh() {
        try {
            graduateEmploymentGW.refresh();
            sumOfGraduateGW.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class GraduateEmploymentEntity extends EntityUtils {
        @Override
        protected void init(Object source, Entity e, boolean isNew) throws Exception {
            VGraduate graduateEmployment = (VGraduate) e;
            new GraduateEmploymentEdit(graduateEmployment, isNew, GraduateEmploymentView.this);
        }

        @Override
        protected GridWidget getGridWidget() {
            return graduateEmploymentGW;
        }

        @Override
        protected String getModuleName() {
            return "Graduate Exam View";
        }

        @Override
        protected Class<? extends Entity> getEntityClass() {
            return VGraduate.class;
        }

        @Override
        protected void removeChildrenEntity(List<Entity> delList) throws Exception {
        }

        @Override
        protected void refresh() throws Exception {
            graduateEmploymentGW.refresh();
        }
    }
}
