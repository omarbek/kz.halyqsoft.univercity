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

    public StudentDormView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        FormLayout inFL = new FormLayout();
        inFL.setSpacing(true);
        inFL.setSizeFull();

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

//        List<DORM_ROOM> dr = new ArrayList<>();
//        QueryModel<DORM_ROOM> dormRQM = new QueryModel<DORM_ROOM>(DORM_ROOM.class);
//        dormRQM.addWhere("roomNo",ECriteria.EQUAL,roomCB.getValue());
//        try {
//            dr = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(dormRQM);
//        } catch (NoResultException e) {
//            dr = null;
//        }
//        if (dr != null) {
//            for(DORM_ROOM dormRoom : dr) {
//                costL.setValue(dormRoom.getCost().toString());
//            }
//        }

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
                        if (dormRooms.get(0).getCost() != null) {
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
        saveB.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (roomCB.getValue() != null && costL.getValue() != null && !costL.getValue().equals("")) {
//                    DORM_STUDENT dormStudent = new DORM_STUDENT();
//                    DORM_ROOM dormR = (DORM_ROOM) roomCB.getValue();
//                    dormStudent.setRoom(dormR);
//                    dormStudent.setCost();
                    try {
                        QueryModel<STUDENT_EDUCATION> studentQM = new QueryModel<STUDENT_EDUCATION>(STUDENT_EDUCATION.class);
                        studentQM.addWhere("student", ECriteria.EQUAL, CommonUtils.getCurrentUser().getId());
                        studentQM.addWhereNull("child");

                        QueryModel<DORM_ROOM> roomQM = new QueryModel<DORM_ROOM>(DORM_ROOM.class);
                        FromItem fi = roomQM.addJoin(EJoin.INNER_JOIN, "dorm", DORM.class, "id");
                        roomQM.addWhere("deleted", Boolean.FALSE);
                        roomQM.addWhere(fi, "deleted", Boolean.FALSE);
                        roomQM.addWhere(fi, "name", ECriteria.EQUAL, buildingCB.getValue().toString());
                        roomQM.addWhere("roomNo", ECriteria.EQUAL, roomCB.getValue().toString());

                        STUDENT_EDUCATION STUDENT_EDUCATION = null;
                        DORM_ROOM dormRoom = null;
                        try {
                            STUDENT_EDUCATION = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(studentQM);
                            dormRoom = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(roomQM);
                        } catch (NoResultException e) {
                            STUDENT_EDUCATION = null;
                        }
                        if (STUDENT_EDUCATION != null && dormRoom != null) {
                            DORM_STUDENT dormStudent = null;

                            dormStudent = new DORM_STUDENT();
                            dormStudent.setCost(Double.parseDouble(costL.getValue()));
                            dormStudent.setCreated(new Date());
                            dormStudent.setDeleted(false);
                            dormStudent.setStudent(STUDENT_EDUCATION);
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

        getContent().addComponent(inFL);

    }

    private void clearIn() {
        roomCB.clear();
        buildingCB.clear();
        costL.setValue("");
    }

}
