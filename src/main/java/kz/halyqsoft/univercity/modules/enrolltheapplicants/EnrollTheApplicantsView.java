package kz.halyqsoft.univercity.modules.enrolltheapplicants;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDENT_CATEGORY;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VStudent;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;

import java.util.*;

/**
 * @author Omarbek
 * @created on 04.05.2018
 */
public class EnrollTheApplicantsView extends AbstractTaskView {

    private Grid studentGrid;

    public EnrollTheApplicantsView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        initButtons();
        initGrid();
    }

    private void initButtons() {
        HorizontalLayout buttonsHL = CommonUtils.createButtonPanel();

        Button enrollButton = new Button();
        enrollButton.setWidth(120.0F, Unit.PIXELS);
        enrollButton.setIcon(new ThemeResource("img/button/ok.png"));
        enrollButton.setCaption(getUILocaleUtil().getCaption("enroll"));
        enrollButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    Collection<Object> selectedRows = studentGrid.getSelectedRows();
                    for (Object object : selectedRows) {
                        VStudent vStudent = (VStudent) object;
                        STUDENT student = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                lookup(STUDENT.class, vStudent.getId());
                        student.setCategory(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                lookup(STUDENT_CATEGORY.class, ID.valueOf(3)));
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(student);
                    }
                    CommonUtils.showSavedNotification();
                    refresh();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        buttonsHL.addComponent(enrollButton);
        buttonsHL.setComponentAlignment(enrollButton, Alignment.MIDDLE_CENTER);

        Button deleteButton = new Button();
        deleteButton.setWidth(120.0F, Unit.PIXELS);
        deleteButton.setIcon(new ThemeResource("img/button/delete.png"));
        deleteButton.setCaption(getUILocaleUtil().getCaption("delete"));
        deleteButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                        AbstractWebUI.getInstance().addWindow(new reasonDeleted());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        buttonsHL.addComponent(deleteButton);
        buttonsHL.setComponentAlignment(deleteButton, Alignment.MIDDLE_CENTER);

        getContent().addComponent(buttonsHL);
        getContent().setComponentAlignment(buttonsHL, Alignment.MIDDLE_LEFT);
    }

    private void initGrid() {
        studentGrid = new Grid();
        studentGrid.setCaption(getUILocaleUtil().getCaption("applicants"));
        studentGrid.setSizeFull();
        studentGrid.addStyleName("lesson-detail");
        studentGrid.setSelectionMode(Grid.SelectionMode.MULTI);
        studentGrid.addColumn("code").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(VStudent.class,
                "code")).setHidable(false).setWidth(120);
        studentGrid.addColumn("fio").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(VStudent.class,
                "fio")).setHidable(false);
        studentGrid.addColumn("category").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(VStudent.class,
                "category")).setHidable(false);
        studentGrid.addColumn("status").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(VStudent.class,
                "status")).setHidable(false);
        studentGrid.addColumn("faculty").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(VStudent.class,
                "faculty")).setHidable(false);
        studentGrid.addColumn("specialty").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(VStudent.class,
                "specialty")).setHidable(false);
        studentGrid.addColumn("studyYear").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(VStudent.class,
                "studyYear")).setHidable(false);
        studentGrid.addColumn("languageName").setHeaderCaption(getUILocaleUtil().getEntityFieldLabel(VStudent.class,
                "languageName")).setHidable(false);

        refresh();

        getContent().addComponent(studentGrid);
        getContent().setComponentAlignment(studentGrid, Alignment.MIDDLE_CENTER);
    }

    public void refresh() {

        List<VStudent> list = new ArrayList<>();
        String sql = "SELECT " +
                "  stu.ID, " +
                "  stu.user_code                                                                        code, " +
                "  trim(stu.LAST_NAME || ' ' || stu.FIRST_NAME || ' ' || coalesce(stu.MIDDLE_NAME, '')) FIO, " +
                "  stu.student_status_name                                                              STATUS_NAME, " +
                "  stu.faculty_short_name                                                               FACULTY, " +
                "  stu.speciality_name " +
                "FROM V_STUDENT stu " +
                "WHERE stu.category_id = 1 " +
                "ORDER BY FIO";
        try {
            List<Object> tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, new HashMap<>());
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;
                    VStudent vs = new VStudent();
                    vs.setId(ID.valueOf((long) oo[0]));
                    vs.setCode((String) oo[1]);
                    vs.setFio((String) oo[2]);
                    vs.setStatus((String) oo[3]);
                    vs.setFaculty((String) oo[4]);
                    vs.setSpecialty((String) oo[5]);
                    list.add(vs);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load student list", ex);
        }

        final BeanItemContainer<VStudent> bic = new BeanItemContainer<>(VStudent.class, list);
        studentGrid.setContainerDataSource(bic);
    }

    private class reasonDeleted extends AbstractDialog{

        reasonDeleted(){

            setWidth(500, Unit.PIXELS);
            setHeight(300, Unit.PIXELS);
            center();

            TextArea ta = new TextArea();
            ta.setCaption("<html><b>" + getUILocaleUtil().getCaption("reason") + "</b>");
            ta.setWidth(100, Unit.PERCENTAGE);
            ta.setCaptionAsHtml(true);
            getContent().addComponent(ta);

            Button deleteButton = new Button();
            deleteButton.setWidth(120.0F, Unit.PIXELS);
            deleteButton.setIcon(new ThemeResource("img/button/delete.png"));
            deleteButton.setCaption(getUILocaleUtil().getCaption("delete"));
            deleteButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                    Collection<Object> selectedRows = studentGrid.getSelectedRows();
                    for (Object object : selectedRows) {
                        VStudent vStudent = (VStudent) object;
                        USERS user = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                lookup(USERS.class, vStudent.getId());
                        user.setDeleted(true);
                        user.setUpdated(new Date());
                        user.setUpdatedBy(CommonUtils.getCurrentUserLogin());
                        user.setReason(ta.getValue());
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(user);
                    }
                    refresh();
                    close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            getContent().addComponent(deleteButton);
            getContent().setComponentAlignment(deleteButton, Alignment.MIDDLE_CENTER);
        }

        @Override
        protected String createTitle() {
            return null;
        }
    }
}
