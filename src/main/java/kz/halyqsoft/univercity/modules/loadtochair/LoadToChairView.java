package kz.halyqsoft.univercity.modules.loadtochair;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Grid;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VStudent;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.vaadin.view.AbstractTaskView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Omarbek
 * @created on 29.06.2018
 */
public class LoadToChairView extends AbstractTaskView {

    private Grid studentGrid;

    public LoadToChairView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        studentGrid = new Grid();
        studentGrid.setCaption("load to chair");//TODO
        studentGrid.setSizeFull();
//        studentGrid.addStyleName("lesson-detail");//TODO
        studentGrid.setSelectionMode(Grid.SelectionMode.NONE);
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
}
