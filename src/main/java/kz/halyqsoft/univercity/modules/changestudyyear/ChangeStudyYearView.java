package kz.halyqsoft.univercity.modules.changestudyyear;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.GROUPS;
import kz.halyqsoft.univercity.entity.beans.univercity.PRACTICE_INFORMATION;
import kz.halyqsoft.univercity.entity.beans.univercity.PRACTICE_STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_EDUCATION;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDENT_CATEGORY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDENT_STATUS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDY_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VChangeToNextYearStudent;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VStudent;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_GROUP;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_STUDENT;
import kz.halyqsoft.univercity.filter.FChangeToNextYearGroupFilter;
import kz.halyqsoft.univercity.filter.FGroupFilter;
import kz.halyqsoft.univercity.filter.panel.ChangeToNextYearGroupFilterPanel;
import kz.halyqsoft.univercity.filter.panel.GroupFilterPanel;
import kz.halyqsoft.univercity.modules.changestudyyear.view.OrdinaryStudentsView;
import kz.halyqsoft.univercity.modules.changestudyyear.view.ProblemStudentsView;
import kz.halyqsoft.univercity.modules.reports.MenuColumn;
import kz.halyqsoft.univercity.modules.workflow.MyItem;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;

import java.util.*;

public class ChangeStudyYearView extends AbstractTaskView {

    private VerticalLayout mainVL;
    private HorizontalSplitPanel mainHSP;
    private static String FIRST_ROW = getUILocaleUtil().getCaption("ordinary.students");
    private static String SECOND_ROW = getUILocaleUtil().getCaption("problem.students");

    private OrdinaryStudentsView ordinaryStudentsView;
    private ProblemStudentsView problemStudentsView;

    public ChangeStudyYearView(AbstractTask task) throws Exception {
        super(task);
        mainVL = new VerticalLayout();
        mainVL.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        mainHSP = new HorizontalSplitPanel();
        mainHSP.setImmediate(true);
        mainHSP.setSizeFull();
        mainHSP.setSplitPosition(15);

        ordinaryStudentsView = new OrdinaryStudentsView();
        problemStudentsView = new ProblemStudentsView();
    }

    @Override
    public void initView(boolean b) throws Exception {

        HierarchicalContainer optionHC = new HierarchicalContainer();
        ArrayList<MyItem> myItems = new ArrayList<>();
        myItems.add(new MyItem(optionHC.addItem(FIRST_ROW) ,FIRST_ROW, null));
        myItems.add(new MyItem(optionHC.addItem(SECOND_ROW) ,SECOND_ROW, null));

        TreeTable sideMenu = new TreeTable();
        sideMenu.setContainerDataSource(optionHC);
        sideMenu.setColumnReorderingAllowed(false);
        sideMenu.setMultiSelect(false);
        sideMenu.setNullSelectionAllowed(false);
        sideMenu.setSizeFull();
        sideMenu.setImmediate(true);
        sideMenu.setSelectable(true);

        sideMenu.setChildrenAllowed(FIRST_ROW, false);
        sideMenu.setChildrenAllowed(SECOND_ROW, false);

        MenuColumn firstColumn = new MenuColumn();
        sideMenu.addGeneratedColumn("first" , firstColumn);
        sideMenu.setColumnHeader("first" , getUILocaleUtil().getCaption("menu"));
        sideMenu.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent valueChangeEvent) {
                mainVL.removeAllComponents();

                if(valueChangeEvent.getProperty().getValue().toString().equals(FIRST_ROW)){
                    mainVL.addComponent(ordinaryStudentsView.getContent());
                    ordinaryStudentsView.doFilter(ordinaryStudentsView.changeToNextYearGroupFilterPanel.getFilterBean());
                }else if(valueChangeEvent.getProperty().getValue().toString().equals(SECOND_ROW)){
                    mainVL.addComponent(problemStudentsView.getContent());
                    ordinaryStudentsView.doFilter(ordinaryStudentsView.changeToNextYearGroupFilterPanel.getFilterBean());
                    problemStudentsView.doFilter(problemStudentsView.changeToNextYearGroupFilterPanel.getFilterBean());
                }
            }
        });
        mainHSP.setFirstComponent(sideMenu);
        mainHSP.setSecondComponent(mainVL);
        getContent().addComponent(mainHSP);
    }

}
