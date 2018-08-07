package kz.halyqsoft.univercity.modules.practice;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE;
import kz.halyqsoft.univercity.entity.beans.univercity.GROUPS;
import kz.halyqsoft.univercity.entity.beans.univercity.PRACTICE_INFORMATION;
import kz.halyqsoft.univercity.entity.beans.univercity.PRACTICE_STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDY_YEAR;
import kz.halyqsoft.univercity.filter.FGroupFilter;
import kz.halyqsoft.univercity.filter.panel.InformationPracticeFilterPanel;
import kz.halyqsoft.univercity.filter.FInformationPracticeFilter;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.EmployeePdfCreator;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;

import java.util.*;

public class PracticeView extends AbstractTaskView implements FilterPanelListener, EntityListener{

    private InformationPracticeFilterPanel informationPracticeFP;
    private VerticalLayout mainVL;
    private GridWidget informationPracticeGW;
    private DBGridModel informationPracticeGM;

    public PracticeView(AbstractTask task) throws Exception {
        super(task);

        mainVL = new VerticalLayout();
        mainVL.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);

    }

    @Override
    public void initView(boolean b) throws Exception {

        initGridWidget();
        initFilter();
        mainVL.addComponent(informationPracticeFP);
        mainVL.addComponent(informationPracticeGW);
        getContent().addComponent(mainVL);
    }

    private void initGridWidget(){
        informationPracticeGW = new GridWidget(PRACTICE_INFORMATION.class);
        informationPracticeGW.setImmediate(true);
        informationPracticeGW.setSizeFull();
        informationPracticeGW.removeEntityListener(informationPracticeGW);
        informationPracticeGW.showToolbar(true);
        informationPracticeGW.addEntityListener(this);
        informationPracticeGW.setMultiSelect(true);

        informationPracticeGM = (DBGridModel)informationPracticeGW.getWidgetModel();
        informationPracticeGM.setRefreshType(ERefreshType.AUTO);
        informationPracticeGM.setMultiSelect(true);
        informationPracticeGM.setRowNumberVisible(true);
        informationPracticeGM.setRowNumberWidth(50);
    }

    private void initFilter() throws Exception{

        informationPracticeFP = new InformationPracticeFilterPanel(new FInformationPracticeFilter());
        informationPracticeFP.addFilterPanelListener(this);
        informationPracticeFP.setImmediate(true);

        ComboBox groupsCB = new ComboBox();
        groupsCB.setNullSelectionAllowed(true);
        groupsCB.setTextInputAllowed(true);
        groupsCB.setFilteringMode(FilteringMode.CONTAINS);
        groupsCB.setWidth(300, Unit.PIXELS);
        QueryModel<GROUPS> specialityQM = new QueryModel<>(GROUPS.class);
        BeanItemContainer<GROUPS> groupsBIC = new BeanItemContainer<>(GROUPS.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(specialityQM));
        groupsCB.setContainerDataSource(groupsBIC);
        informationPracticeFP.addFilterComponent("groups", groupsCB);

        ComboBox employeeCB = new ComboBox();
        employeeCB.setNullSelectionAllowed(true);
        employeeCB.setTextInputAllowed(true);
        employeeCB.setFilteringMode(FilteringMode.CONTAINS);
        employeeCB.setWidth(300, Unit.PIXELS);
        QueryModel<USERS> employeeQM = new QueryModel<>(USERS.class);
        employeeQM.addJoin(EJoin.INNER_JOIN , "id" ,EMPLOYEE.class , "id");

        BeanItemContainer<USERS> employeeBIC = new BeanItemContainer<>(USERS.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(employeeQM));
        employeeCB.setContainerDataSource(employeeBIC);
        informationPracticeFP.addFilterComponent("employee", employeeCB);


        ComboBox studyYearCB = new ComboBox();
        studyYearCB.setNullSelectionAllowed(true);
        studyYearCB.setTextInputAllowed(true);
        studyYearCB.setFilteringMode(FilteringMode.CONTAINS);
        studyYearCB.setWidth(300, Unit.PIXELS);
        QueryModel<STUDY_YEAR> studyYearQM = new QueryModel<>(STUDY_YEAR.class);

        BeanItemContainer<STUDY_YEAR> studyYearBIC = new BeanItemContainer<>(STUDY_YEAR.class,
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(studyYearQM));
        studyYearCB.setContainerDataSource(studyYearBIC);
        informationPracticeFP.addFilterComponent("studyYear", studyYearCB);

        DateField createdDF = new DateField();
        informationPracticeFP.addFilterComponent("created", createdDF);

    }

    @Override
    public void doFilter(AbstractFilterBean abstractFilterBean) {
        FInformationPracticeFilter informationPracticeFilter = (FInformationPracticeFilter) abstractFilterBean;
        Map<Integer, Object> params = new HashMap<>();
        int i = 1;
        StringBuilder sb = new StringBuilder();

        if (informationPracticeFilter.getEmployee() != null) {

            if(i!=1){
                sb.append(" and ");
            }
            params.put(i, informationPracticeFilter.getEmployee().getId().getId());
            sb.append("pi.teacher_id = ?" + i++);

        }

        if (informationPracticeFilter.getGroups()!=null) {

            if(i!=1){
                sb.append(" and ");
            }
            params.put(i, informationPracticeFilter.getGroups().getId().getId());
            sb.append("g.id = ?" + i++);

        }

        if (informationPracticeFilter.getStudyYear() != null) {

            if(i!=1){
                sb.append(" and ");
            }
            params.put(i, informationPracticeFilter.getStudyYear().getId().getId());
            sb.append(" g.study_year_id = ?" + i++);

        }

        if (informationPracticeFilter.getCreated() != null) {

            if(i!=1){
                sb.append(" and ");
            }
            i++;
            sb.append(" date_trunc ('day' ,  pi.created ) = date_trunc('day' , TIMESTAMP  '"+ CommonUtils.getFormattedDate(informationPracticeFilter.getCreated())+ "') ");

        }

        List<PRACTICE_INFORMATION> list = new ArrayList<>();

        if(i > 1){
            sb.insert(0, " WHERE  ");
        }
        String sql = "select pi.* from practice_information pi\n" +
                "  INNER JOIN groups g\n" +
                "    ON pi.groups_id = g.id\n"
                + sb.toString();
        try {

            List tmpList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);
            if (!tmpList.isEmpty()) {
                for (Object o : tmpList) {
                    Object[] oo = (Object[]) o;

                    PRACTICE_INFORMATION practiceInformation = new PRACTICE_INFORMATION();
                    practiceInformation.setId(ID.valueOf((Long)oo[0]));

                    try{
                        practiceInformation.setGroups(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(GROUPS.class , ID.valueOf((Long)oo[1])));
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    try{
                        practiceInformation.setEmployee(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USERS.class , ID.valueOf((Long)oo[2])));
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    practiceInformation.setCreated((Date)oo[3]);

                    list.add(practiceInformation);
                }
            }
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to load accounts list", ex);
        }
        refresh(list);
    }

    @Override
    public void clearFilter() {

    }

    private void refresh(List<PRACTICE_INFORMATION> list) {

        informationPracticeGM.setEntities(list);
        try {
            informationPracticeGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh practice information list", ex);
        }
    }
}
