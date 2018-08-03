package kz.halyqsoft.univercity.modules.dorm;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.DORM_STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_EDUCATION;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DORM;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DORM_ROOM;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.Message;

import javax.persistence.NoResultException;
import java.util.Date;
import java.util.List;

/**
 * @author Dinassil Omarbek
 * @created May 24, 2017 9:14:06 AM
 */
public class StudentDormView extends AbstractTaskView {
    private ComboBox buildingCB, roomCB;
    private Label costL;
    private STUDENT student;
    private String faculty;
    private Label alreadyExistL, roomL, addressL, dormL;

    public StudentDormView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        FormLayout inFL = new FormLayout();
        //inFL.setSpacing(true);
        //inFL.setSizeFull();

        QueryModel<STUDENT_EDUCATION> studentQM = new QueryModel<STUDENT_EDUCATION>(STUDENT_EDUCATION.class);
        studentQM.addWhere("student", ECriteria.EQUAL, CommonUtils.getCurrentUser().getId());
        studentQM.addWhereNull("child");

        buildingCB = new ComboBox(getUILocaleUtil().getCaption("building"));
        buildingCB.setRequired(true);
        buildingCB.setTextInputAllowed(true);
        buildingCB.setFilteringMode(FilteringMode.STARTSWITH);
        buildingCB.setWidth(245, Unit.PIXELS);
        QueryModel<DORM> buildingQM = new QueryModel<>(DORM.class);
        buildingQM.addWhere("deleted", Boolean.FALSE);
        BeanItemContainer<DORM> buildingBIC = buildingBIC = new BeanItemContainer<DORM>(DORM.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(buildingQM));
        buildingCB.setContainerDataSource(buildingBIC);
        buildingCB.setNullSelectionAllowed(false);
        buildingCB.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if (event.getProperty().getValue() != null) {
                    roomCB.setReadOnly(false);
                    QueryModel<DORM_ROOM> roomQM = new QueryModel<DORM_ROOM>(DORM_ROOM.class);
                    roomQM.addWhere("dorm", ECriteria.EQUAL, ((DORM) event.getProperty().getValue()).getId());
                    roomQM.addWhere("deleted", Boolean.FALSE);
                    BeanItemContainer<DORM_ROOM> roomBIC = null;
                    try {
                        roomBIC = new BeanItemContainer<DORM_ROOM>(DORM_ROOM.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(roomQM));
                    } catch (IllegalArgumentException e) {
                        LOG.error("Unable to load room: IllegalArgumentException - ", e);
                    } catch (Exception ex) {
                        LOG.error("Unable to load room: ", ex);
                        Message.showError(ex.toString());
                    }
                    roomCB.setContainerDataSource(roomBIC);
                } else {
                    roomCB.setReadOnly(true);
                }
            }
        });
        inFL.addComponent(buildingCB);

        roomCB = new ComboBox(getUILocaleUtil().getCaption("room"));
        roomCB.setRequired(true);
        roomCB.setTextInputAllowed(true);
        roomCB.setFilteringMode(FilteringMode.STARTSWITH);
        roomCB.setWidth(245, Unit.PIXELS);
        roomCB.setNullSelectionAllowed(false);
        QueryModel<DORM_ROOM> dormRoomQM = new QueryModel<>(DORM_ROOM.class);
        dormRoomQM.addWhere("deleted", Boolean.FALSE);
        BeanItemContainer<DORM_ROOM> dormRoomBIC = new BeanItemContainer<DORM_ROOM>(DORM_ROOM.class, SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(dormRoomQM));
        roomCB.setContainerDataSource(dormRoomBIC);

        inFL.addComponent(roomCB);

        costL = new Label();
        costL.setCaption(getUILocaleUtil().getCaption("cost"));
        costL.setWidth(245, Unit.PIXELS);

        roomCB.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if (event != null && event.getProperty() != null && event.getProperty().getValue() != null) {
                    QueryModel<DORM_ROOM> roomQM = new QueryModel<>(DORM_ROOM.class);
                    FromItem fi = roomQM.addJoin(EJoin.INNER_JOIN, "dorm", DORM.class, "id");
                    roomQM.addWhere("deleted", Boolean.FALSE);
                    roomQM.addWhere(fi, "deleted", Boolean.FALSE);
                    roomQM.addWhere(fi, "name", ECriteria.EQUAL, buildingCB.getValue().toString());
                    roomQM.addWhere("roomNo", ECriteria.EQUAL, event.getProperty().getValue().toString());
                    List<DORM_ROOM> dormRooms = null;
                    try {
                        dormRooms = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(roomQM);
                        if (dormRooms != null) {
                            LOG.info("dormRooms requested. List size= " + dormRooms.size());
                        } else {
                            LOG.info("dormRooms requested. List empty !!! ");
                        }
                    } catch (NoResultException e) {
                        LOG.error("Unable to load room: ", e);
                    } catch (Exception ex) {
                        LOG.error("Unable to load room: ", ex);
                        Message.showError(ex.toString());
                    }
                    if (dormRooms != null && dormRooms.size() > 0 && dormRooms.get(0) != null &&
                            (dormRooms.get(0).getBedCount() - dormRooms.get(0).getBusyBedCount()) > 0) {
                        if ("Казахстанская морская академия".equals(faculty)) {
                            costL.setValue("0.00");
                        } else if (dormRooms.get(0).getCost() != null) {
                            costL.setValue(dormRooms.get(0).getCost().toString());
                        }
                    } else {
                        Message.showInfo(getUILocaleUtil().getMessage("no.free.beds"));
                        clearIn();
                    }
                }
            }
        });
        inFL.addComponent(costL);

        HorizontalLayout buttonsHL = new HorizontalLayout();
        buttonsHL.setSpacing(true);

        Button saveB = new Button(getUILocaleUtil().getCaption("saveB"));
        saveB.setCaption(getUILocaleUtil().getCaption("saveB"));
        Button cancelB = new Button(getUILocaleUtil().getCaption("clear"));
        buttonsHL.addComponent(saveB);
        buttonsHL.addComponent(cancelB);
        inFL.addComponent(buttonsHL);
        inFL.setComponentAlignment(buttonsHL, Alignment.MIDDLE_CENTER);
        saveB.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (roomCB.getValue() != null && costL.getValue() != null && !costL.getValue().equals("")) {
                    try {

                        QueryModel<DORM_ROOM> roomQM = new QueryModel<DORM_ROOM>(DORM_ROOM.class);
                        FromItem fi = roomQM.addJoin(EJoin.INNER_JOIN, "dorm", DORM.class, "id");
                        roomQM.addWhere("deleted", Boolean.FALSE);
                        roomQM.addWhere(fi, "deleted", Boolean.FALSE);
                        roomQM.addWhere(fi, "name", ECriteria.EQUAL, buildingCB.getValue().toString());
                        roomQM.addWhere("roomNo", ECriteria.EQUAL, roomCB.getValue().toString());

                        STUDENT_EDUCATION studentEducation = null;
                        DORM_ROOM dormRoom = null;
                        try {
                            studentEducation = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(studentQM);
                            dormRoom = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(roomQM);
                        } catch (NoResultException e) {
                            studentEducation = null;
                        }
                        DORM_STUDENT dormStudent = null;
                        if (studentEducation != null && dormRoom != null) {

                            dormStudent = new DORM_STUDENT();
                            dormStudent.setCost(Double.parseDouble(costL.getValue()));
                            dormStudent.setCreated(new Date());
                            dormStudent.setDeleted(false);
                            dormStudent.setStudent(studentEducation);
                            dormStudent.setRoom(dormRoom);

                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(dormStudent);
                            AbstractWebUI.getInstance().showNotificationInfo(getUILocaleUtil().getMessage("info.record.saved"));
                        }
                    } catch (Exception ex) {
                        LOG.error("Unable to create or update student dorm: ", ex);
                        Message.showError(ex.toString());
                    }
                } else {
                    Message.showError(getUILocaleUtil().getCaption("add.news.required"));
                }
            }
        });

        cancelB.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                clearIn();
            }
        });

        QueryModel<DORM_STUDENT> dormStudentQM = new QueryModel<DORM_STUDENT>(DORM_STUDENT.class);
        FromItem fi = dormStudentQM.addJoin(EJoin.INNER_JOIN, "student", STUDENT_EDUCATION.class, "id");
        dormStudentQM.addWhere(fi, "student", ECriteria.EQUAL, CommonUtils.getCurrentUser().getId());
        dormStudentQM.addWhere("checkOutDate",null);
        dormStudentQM.addWhere("deleted",false);
        dormStudentQM.addWhere("requestStatus",ECriteria.EQUAL,1);
        List<DORM_STUDENT> dormStudentId = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(dormStudentQM);
        boolean flag = true;
        for (DORM_STUDENT ds : dormStudentId) {
            flag = false;
            alreadyExistL = new Label();
            alreadyExistL.setCaption("<html><b>"+getUILocaleUtil().getCaption("alreadyExistL")+"</b>");
            alreadyExistL.setWidth(200, Unit.PIXELS);
            alreadyExistL.setCaptionAsHtml(true);

            addressL = new Label();
            addressL.setCaption(getUILocaleUtil().getCaption("addressL"));
            addressL.setWidth(200, Unit.PIXELS);
            addressL.setValue(ds.getRoom().getDorm().getAddress());

            dormL = new Label();
            dormL.setCaption(getUILocaleUtil().getCaption("dormL"));
            dormL.setWidth(200, Unit.PIXELS);
            dormL.setValue(ds.getRoom().getDorm().getName());

            roomL = new Label();
            roomL.setCaption(getUILocaleUtil().getCaption("roomL"));
            roomL.setWidth(200, Unit.PIXELS);
            roomL.setValue(ds.getRoom().getRoomNo());

            inFL.addComponent(alreadyExistL);
            inFL.addComponent(addressL);
            inFL.addComponent(dormL);
            inFL.addComponent(roomL);
        }

        if(flag) {
            getContent().addComponent(buildingCB);
            getContent().setComponentAlignment(buildingCB,Alignment.MIDDLE_CENTER);

            getContent().addComponent(roomCB);
            getContent().setComponentAlignment(roomCB,Alignment.MIDDLE_CENTER);

            getContent().addComponent(costL);
            getContent().setComponentAlignment(costL,Alignment.MIDDLE_CENTER);

            getContent().addComponent(buttonsHL);
            getContent().setComponentAlignment(buttonsHL,Alignment.MIDDLE_CENTER);
        }else{
            getContent().addComponent(alreadyExistL);
            getContent().setComponentAlignment(alreadyExistL,Alignment.MIDDLE_CENTER);

            getContent().addComponent(addressL);
            getContent().setComponentAlignment(addressL,Alignment.MIDDLE_CENTER);

            getContent().addComponent(dormL);
            getContent().setComponentAlignment(dormL,Alignment.MIDDLE_CENTER);

            getContent().addComponent(roomL);
            getContent().setComponentAlignment(roomL,Alignment.MIDDLE_CENTER);
        }
    }

    private void clearIn() {
        roomCB.clear();
        buildingCB.clear();
        costL.setValue("");
    }

}
