package kz.halyqsoft.univercity.modules.dorm;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.DORM_STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VDormStudent;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.view.AbstractCommonView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.form.AbstractFormWidgetView;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author Dinassil Omarbek
 * @created 25.05.2017.
 */
public class AcceptRequest extends AbstractFormWidgetView {

    private GridWidget dormStudentGW;

    AcceptRequest() {
        dormStudentGW = new GridWidget(VDormStudent.class);
        dormStudentGW.addEntityListener(this);

        dormStudentGW.showToolbar(false);

        DBGridModel dbGridModel = (DBGridModel) dormStudentGW.getWidgetModel();

        dbGridModel.setEntities(getList());
        dbGridModel.setMultiSelect(true);
        dbGridModel.setRefreshType(ERefreshType.MANUAL);

        getContent().addComponent(dormStudentGW);
        getContent().setComponentAlignment(dormStudentGW, Alignment.TOP_CENTER);

        HorizontalLayout buttonsHL = new HorizontalLayout();
        buttonsHL.setSpacing(true);

        Button acceptB = new Button(getUILocaleUtil().getCaption("acceptB"));
        Button refuseB = new Button(getUILocaleUtil().getCaption("refuseB"));
        buttonsHL.addComponent(acceptB);
        buttonsHL.addComponent(refuseB);
        getContent().addComponent(buttonsHL);
        getContent().setComponentAlignment(buttonsHL, Alignment.MIDDLE_CENTER);

        acceptB.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                try {
                    if (dormStudentGW.getSelectedEntity() != null) {
                        Collection<Entity> selectedEntities = dormStudentGW.getSelectedEntities();
                        for (Object object : selectedEntities) {

                            VDormStudent vDormStudent = (VDormStudent) object;
                            QueryModel<DORM_STUDENT> dr = new QueryModel<>(DORM_STUDENT.class);
                            dr.addWhere("id", ECriteria.EQUAL, vDormStudent.getId());

                            DORM_STUDENT dormStudent = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(dr);
                            dormStudent.setRequestStatus(1);
                            dormStudent.setCheckInDate(new Date());

                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(dormStudent);
                            AbstractWebUI.getInstance().showNotificationInfo(getUILocaleUtil().getMessage("info.record.saved"));
                            refreshList(getList());
                        }
                    } else {
                        Message.showError(getUILocaleUtil().getCaption("add.news.select"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        refuseB.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                try {
                    if (dormStudentGW.getSelectedEntity() != null) {
                        Collection<Entity> selectedEntities = dormStudentGW.getSelectedEntities();
                        for (Object object : selectedEntities) {
                            VDormStudent vDormStudent = (VDormStudent) object;
                            QueryModel<DORM_STUDENT> dr = new QueryModel<>(DORM_STUDENT.class);
                            dr.addWhere("id", ECriteria.EQUAL, vDormStudent.getId());
                            DORM_STUDENT dormStudent = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(dr);
                            dormStudent.setRequestStatus(2);

                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(dormStudent);
                            refreshList(getList());
                        }
                    } else {
                        Message.showError(getUILocaleUtil().getCaption("add.news.select"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void initView(boolean readOnly) throws Exception {
        super.initView(readOnly);
    }

    public List<VDormStudent> getList() {

        List<VDormStudent> list = new ArrayList<>();
        Map<Integer, Object> params = new HashMap<>();
        String sql = "select " +
                " ds.id," +
                " ds.room_id," +
                " trim(vs.LAST_NAME || ' ' || vs.FIRST_NAME || ' ' || coalesce(vs.MIDDLE_NAME, '')) FIO," +
                " ds.cost\n" +
                " from dorm_student ds\n" +
                " inner join student_education se on ds.student_id = se.id\n" +
                " inner join v_student vs on se.student_id = vs.id " +
                " where ds.request_status_id = 0 AND ds.deleted = false;";

        try {
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VDormStudent ve = new VDormStudent();
                    ve.setId(ID.valueOf((long) oo[0]));
                    ve.setRoomNo((long) oo[1]);
                    ve.setFio((String) oo[2]);
                    ve.setCost((BigDecimal) oo[3]);
                    list.add(ve);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load request list", ex);
        }
        refreshList(list);
        return list;
    }

    private void refreshList(List<VDormStudent> list) {
        ((DBGridModel) dormStudentGW.getWidgetModel()).setEntities(list);
        try {
            dormStudentGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh request list", ex);
        }
    }

    @Override
    protected AbstractCommonView getParentView() {
        return null;
    }

    @Override
    public String getViewName() {
        return "DormsEdit";
    }

    @Override
    protected String getViewTitle(Locale locale) {
        return getUILocaleUtil().getCaption("dorm");
    }


}
