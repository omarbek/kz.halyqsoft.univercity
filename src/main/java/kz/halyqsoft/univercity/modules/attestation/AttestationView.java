package kz.halyqsoft.univercity.modules.attestation;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalLayout;
import kz.halyqsoft.univercity.entity.beans.univercity.ATTESTATION;
import kz.halyqsoft.univercity.entity.beans.univercity.ATTESTATION_DETAIL;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_DATA;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AttestationView extends AbstractTaskView{

    private GridWidget semesterDataGW;
    private HorizontalSplitPanel mainHL;

    public AttestationView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        mainHL = new HorizontalSplitPanel();
        mainHL.setSizeFull();
        mainHL.setResponsive(true);

        semesterDataGW = new GridWidget(SEMESTER_DATA.class);
        semesterDataGW.setImmediate(true);
        semesterDataGW.setButtonVisible(IconToolbar.EDIT_BUTTON,false);
        semesterDataGW.addEntityListener(this);
        mainHL.setFirstComponent(semesterDataGW);


        getContent().addComponent(mainHL);
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
        if(ev.getSource().equals(semesterDataGW)){
            if(ev.getAction()==EntityEvent.SELECTED){
                if(mainHL.getSecondComponent()!=null){
                    mainHL.removeComponent(mainHL.getSecondComponent());
                }
                if(semesterDataGW.getSelectedEntity()!=null){
                    VerticalLayout innerVL = new VerticalLayout();
                    innerVL.setSpacing(true);
                    innerVL.setSizeFull();
                    innerVL.setImmediate(true);
                    innerVL.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);


                    GridWidget attestationsGW = new GridWidget(ATTESTATION.class);
                    attestationsGW.addEntityListener(new EntityListener() {
                        @Override
                        public void handleEntityEvent(EntityEvent entityEvent) {

                        }

                        @Override
                        public boolean preCreate(Object o, int i) {
                            return true;
                        }

                        @Override
                        public boolean onEdit(Object o, Entity entity, int i) {
                            return true;
                        }

                        @Override
                        public void onCreate(Object o, Entity entity, int i) {

                        }

                        @Override
                        public boolean onPreview(Object o, Entity entity, int i) {
                            return true;
                        }

                        @Override
                        public void beforeRefresh(Object o, int i) {

                        }

                        @Override
                        public void onRefresh(Object o, List<Entity> list) {

                        }

                        @Override
                        public void onFilter(Object o, QueryModel queryModel, int i) {

                        }

                        @Override
                        public void onAccept(Object o, List<Entity> list, int i) {

                        }

                        @Override
                        public boolean preSave(Object o, Entity entity, boolean b, int i) {
                            ATTESTATION attestation = (ATTESTATION) entity;
                            attestation.setSemesterData((SEMESTER_DATA) semesterDataGW.getSelectedEntity());
                            return true;
                        }

                        @Override
                        public boolean preDelete(Object o, List<Entity> list, int i) {
                            return true;
                        }

                        @Override
                        public void onDelete(Object o, List<Entity> list, int i) {

                        }

                        @Override
                        public void deferredCreate(Object o, Entity entity) {

                        }

                        @Override
                        public void deferredDelete(Object o, List<Entity> list) {

                        }

                        @Override
                        public void onException(Object o, Throwable throwable) {

                        }
                    });
                    DBGridModel attestationsGM = (DBGridModel) attestationsGW.getWidgetModel();
                    attestationsGM.getQueryModel().addWhere("semesterData" , ECriteria.EQUAL , semesterDataGW.getSelectedEntity().getId());
                    attestationsGM.getQueryModel().addOrderDesc("id");

                    Button generateSem = new Button("Generate attestation");
                    innerVL.addComponent(generateSem);
                    generateSem.addClickListener(new Button.ClickListener() {
                        @Override
                        public void buttonClick(Button.ClickEvent clickEvent) {
                            SEMESTER_DATA semesterData = (SEMESTER_DATA) semesterDataGW.getSelectedEntity();
                            List<ATTESTATION> attestations = new ArrayList<>();
                            Calendar cal = Calendar.getInstance();
                            cal.setTime(semesterData.getBeginDate());

                            Calendar lastCal = Calendar.getInstance();
                            lastCal.setTime(semesterData.getEndDate());

                            if(!semesterData.getBeginDate().after(semesterData.getEndDate())){
                                while (true){
                                    cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DAY_OF_MONTH)-7);
                                    ATTESTATION attestation = new ATTESTATION();
                                    attestation.setSemesterData(semesterData);
                                    attestation.setBeginDate(cal.getTime());
                                    cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
                                    attestation.setFinishDate(cal.getTime());
                                    attestations.add(attestation);
                                    if(cal.get(Calendar.MONTH) >= lastCal.get(Calendar.MONTH)){
                                        break;
                                    }
                                    cal.add(Calendar.MONTH,1);
                                }
                                try{
                                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(attestations);
                                    attestationsGW.refresh();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }

                            }else{
                                Message.showError(getUILocaleUtil().getMessage("dateFormat.exception"));
                            }
                        }
                    });
                    innerVL.addComponent(attestationsGW);
                    mainHL.setSecondComponent(innerVL);
                }
            }
        }
    }

    @Override
    public boolean preCreate(Object source, int buttonId) {
        return true;
    }

    @Override
    public boolean onEdit(Object source, Entity e, int buttonId) {
        return true;
    }

    @Override
    public boolean preDelete(Object source, List<Entity> entities, int buttonId) {
        return true;
    }

    @Override
    public boolean onPreview(Object source, Entity e, int buttonId) {
        return true;
    }

    @Override
    public boolean preSave(Object source, Entity e, boolean isNew, int buttonId) {
        return true;
    }
}
