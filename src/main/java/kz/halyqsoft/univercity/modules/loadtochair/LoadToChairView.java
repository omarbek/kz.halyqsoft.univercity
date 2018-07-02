package kz.halyqsoft.univercity.modules.loadtochair;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import kz.halyqsoft.univercity.entity.beans.univercity.CURRICULUM;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_LOAD_TO_CHAIR;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_LOAD_TO_CHAIR_COUNT;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_LOAD_TO_CHAIR_COUNT_ALL;
import kz.halyqsoft.univercity.filter.FChairFilter;
import kz.halyqsoft.univercity.filter.panel.ChairFilterPanel;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Omarbek
 * @created on 29.06.2018
 */
public class LoadToChairView extends AbstractTaskView implements FilterPanelListener {

    private ChairFilterPanel filterPanel;
    private ENTRANCE_YEAR currentYear;

    private GridWidget loadGW;
    private GridWidget countGW;
    private GridWidget totalCountGW;

    public LoadToChairView(AbstractTask task) throws Exception {
        super(task);
        filterPanel = new ChairFilterPanel(new FChairFilter());
    }

    @Override
    public void initView(boolean b) throws Exception {
        filterPanel.addFilterPanelListener(this);

        SEMESTER_DATA semesterData = CommonUtils.getCurrentSemesterData();
        if (semesterData != null) {
            currentYear = semesterData.getYear();

            Label yearLabel = new Label();
            yearLabel.setWidthUndefined();
            yearLabel.setValue(currentYear.toString());
            getContent().addComponent(yearLabel);
            getContent().setComponentAlignment(yearLabel, Alignment.TOP_CENTER);

            QueryModel<DEPARTMENT> chairQM = new QueryModel<>(DEPARTMENT.class);
            chairQM.addWhereNotNull("parent");
            chairQM.addWhereAnd("deleted", Boolean.FALSE);
            chairQM.addOrder("deptName");
            BeanItemContainer<DEPARTMENT> chairBIC = new BeanItemContainer<>(DEPARTMENT.class,
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(chairQM));
            ComboBox chairCB = new ComboBox();
            chairCB.setContainerDataSource(chairBIC);
            chairCB.setImmediate(true);
            chairCB.setNullSelectionAllowed(true);
            chairCB.setTextInputAllowed(true);
            chairCB.setFilteringMode(FilteringMode.CONTAINS);
            chairCB.setWidth(400, Unit.PIXELS);
            chairCB.setPageLength(0);
            filterPanel.addFilterComponent("chair", chairCB);

            QueryModel<STUDENT_DIPLOMA_TYPE> diplomaTypeQM = new QueryModel<>(STUDENT_DIPLOMA_TYPE.class);
            BeanItemContainer<STUDENT_DIPLOMA_TYPE> diplomaTypeBIC = new BeanItemContainer<>(
                    STUDENT_DIPLOMA_TYPE.class, SessionFacadeFactory.getSessionFacade(
                    CommonEntityFacadeBean.class).lookup(diplomaTypeQM));
            ComboBox diplomaTypeCB = new ComboBox();
            diplomaTypeCB.setContainerDataSource(diplomaTypeBIC);
            diplomaTypeCB.setImmediate(true);
            diplomaTypeCB.setNullSelectionAllowed(true);
            diplomaTypeCB.setTextInputAllowed(true);
            diplomaTypeCB.setFilteringMode(FilteringMode.STARTSWITH);
            diplomaTypeCB.setWidth(200, Unit.PIXELS);
            diplomaTypeCB.setPageLength(0);
            filterPanel.addFilterComponent("studentDiplomaType", diplomaTypeCB);

            QueryModel<STUDY_YEAR> studyYearQM = new QueryModel<>(STUDY_YEAR.class);
            BeanItemContainer<STUDY_YEAR> studyYearBIC = new BeanItemContainer<>(
                    STUDY_YEAR.class, SessionFacadeFactory.getSessionFacade(
                    CommonEntityFacadeBean.class).lookup(studyYearQM));
            ComboBox studyYearCB = new ComboBox();
            studyYearCB.setContainerDataSource(studyYearBIC);
            studyYearCB.setImmediate(true);
            studyYearCB.setNullSelectionAllowed(true);
            studyYearCB.setTextInputAllowed(true);
            studyYearCB.setFilteringMode(FilteringMode.STARTSWITH);
            studyYearCB.setWidth(200, Unit.PIXELS);
            studyYearCB.setPageLength(0);
            filterPanel.addFilterComponent("studyYear", studyYearCB);

            getContent().addComponent(filterPanel);
            getContent().setComponentAlignment(filterPanel, Alignment.TOP_CENTER);

            loadGW = new GridWidget(V_LOAD_TO_CHAIR.class);
//            loadGW.addEntityListener(this);
            loadGW.showToolbar(false);

            DBGridModel loadGM = (DBGridModel) loadGW.getWidgetModel();
            loadGM.setTitleVisible(false);
            loadGM.setMultiSelect(false);
            loadGM.setRefreshType(ERefreshType.MANUAL);

            countGW = new GridWidget(V_LOAD_TO_CHAIR_COUNT.class);
            countGW.showToolbar(false);

            DBGridModel countGM = (DBGridModel) countGW.getWidgetModel();
            countGM.setMultiSelect(false);
            countGM.setHeightByRows(1);
            countGM.setRefreshType(ERefreshType.MANUAL);

            totalCountGW = new GridWidget(V_LOAD_TO_CHAIR_COUNT_ALL.class);
            totalCountGW.showToolbar(false);

            DBGridModel totalCountGM = (DBGridModel) totalCountGW.getWidgetModel();
            totalCountGM.setRowNumberVisible(true);
            totalCountGM.setHeightByRows(1);
            totalCountGM.setTitleResource("total");
            totalCountGM.setMultiSelect(false);
            totalCountGM.setRefreshType(ERefreshType.MANUAL);

            FChairFilter chairFilter = (FChairFilter) filterPanel.getFilterBean();
            if (chairFilter.hasFilter()) {
                doFilter(chairFilter);
            }

            getContent().addComponent(loadGW);
            getContent().setComponentAlignment(loadGW, Alignment.MIDDLE_CENTER);

            getContent().addComponent(countGW);
            getContent().setComponentAlignment(countGW, Alignment.MIDDLE_CENTER);

            getContent().addComponent(totalCountGW);
            getContent().setComponentAlignment(totalCountGW, Alignment.MIDDLE_CENTER);
        } else {
            Label yearLabel = new Label();
            yearLabel.setCaption(getUILocaleUtil().getMessage("semester.not.going.now"));
            yearLabel.setWidthUndefined();
            getContent().addComponent(yearLabel);
            getContent().setComponentAlignment(yearLabel, Alignment.MIDDLE_CENTER);
        }
    }

    private void refresh(List<V_LOAD_TO_CHAIR> loads, List<V_LOAD_TO_CHAIR_COUNT> loadCounts,
                         List<V_LOAD_TO_CHAIR_COUNT_ALL> loadAllCounts) {
        try {
            ((DBGridModel) loadGW.getWidgetModel()).setEntities(loads);
            loadGW.refresh();

            ((DBGridModel) countGW.getWidgetModel()).setEntities(loadCounts);
            countGW.refresh();

            ((DBGridModel) totalCountGW.getWidgetModel()).setEntities(loadAllCounts);
            totalCountGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh loading to chair", ex);
        }
    }

    private List<V_LOAD_TO_CHAIR> getLoads(ENTRANCE_YEAR currentYear, DEPARTMENT chair, STUDENT_DIPLOMA_TYPE studentDiplomaType,
                                           STUDY_YEAR studyYear) {
        QueryModel<V_LOAD_TO_CHAIR> loadQM = new QueryModel<>(V_LOAD_TO_CHAIR.class);
        FromItem curriculumFI = loadQM.addJoin(EJoin.INNER_JOIN, "curriculum", CURRICULUM.class, "id");
        FromItem specFI = curriculumFI.addJoin(EJoin.INNER_JOIN, "speciality", SPECIALITY.class, "id");
        loadQM.addWhere(specFI, "department", ECriteria.EQUAL, chair.getId());
        loadQM.addWhere(curriculumFI, "entranceYear", ECriteria.EQUAL, currentYear.getId());
        loadQM.addWhere(curriculumFI, "diplomaType", ECriteria.EQUAL, studentDiplomaType.getId());
        loadQM.addWhere("studyYear", ECriteria.EQUAL, studyYear.getId());
        try {
            return SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookup(loadQM);
        } catch (Exception e) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh loading to chair", e);
        }
        return new ArrayList<>();
    }

    private List<V_LOAD_TO_CHAIR_COUNT> getLoadCount(ENTRANCE_YEAR currentYear, DEPARTMENT chair,
                                                     STUDENT_DIPLOMA_TYPE studentDiplomaType, STUDY_YEAR studyYear) {
        QueryModel<V_LOAD_TO_CHAIR_COUNT> loadQM = new QueryModel<>(V_LOAD_TO_CHAIR_COUNT.class);
        FromItem curriculumFI = loadQM.addJoin(EJoin.INNER_JOIN, "curriculum", CURRICULUM.class, "id");
        FromItem specFI = curriculumFI.addJoin(EJoin.INNER_JOIN, "speciality", SPECIALITY.class, "id");
        loadQM.addWhere(specFI, "department", ECriteria.EQUAL, chair.getId());
        loadQM.addWhere(curriculumFI, "diplomaType", ECriteria.EQUAL, studentDiplomaType.getId());
        loadQM.addWhere(curriculumFI, "entranceYear", ECriteria.EQUAL, currentYear.getId());
        loadQM.addWhere("studyYear", ECriteria.EQUAL, studyYear.getId());
        try {
            return SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookup(loadQM);
        } catch (Exception e) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh count of loading to chair", e);
        }
        return new ArrayList<>();
    }

    @Override
    public void doFilter(AbstractFilterBean abstractFilterBean) {
        FChairFilter chairFilter = (FChairFilter) abstractFilterBean;
        if (chairFilter.getChair() != null && chairFilter.getStudentDiplomaType() != null
                && chairFilter.getStudyYear() != null) {
            List<V_LOAD_TO_CHAIR> loads = getLoads(currentYear, chairFilter.getChair(), chairFilter.getStudentDiplomaType(),
                    chairFilter.getStudyYear());
            List<V_LOAD_TO_CHAIR_COUNT> loadCounts = getLoadCount(currentYear, chairFilter.getChair(),
                    chairFilter.getStudentDiplomaType(), chairFilter.getStudyYear());
            List<V_LOAD_TO_CHAIR_COUNT_ALL> loadAllCounts = getLoadAllCount(currentYear, chairFilter.getChair(),
                    chairFilter.getStudentDiplomaType());
            refresh(loads, loadCounts, loadAllCounts);
        }
    }

    private List<V_LOAD_TO_CHAIR_COUNT_ALL> getLoadAllCount(ENTRANCE_YEAR currentYear, DEPARTMENT chair,
                                                            STUDENT_DIPLOMA_TYPE studentDiplomaType) {
        QueryModel<V_LOAD_TO_CHAIR_COUNT_ALL> loadQM = new QueryModel<>(V_LOAD_TO_CHAIR_COUNT_ALL.class);
        FromItem curriculumFI = loadQM.addJoin(EJoin.INNER_JOIN, "curriculum", CURRICULUM.class, "id");
        FromItem specFI = curriculumFI.addJoin(EJoin.INNER_JOIN, "speciality", SPECIALITY.class, "id");
        loadQM.addWhere(specFI, "department", ECriteria.EQUAL, chair.getId());
        loadQM.addWhere(curriculumFI, "diplomaType", ECriteria.EQUAL, studentDiplomaType.getId());
        loadQM.addWhere(curriculumFI, "entranceYear", ECriteria.EQUAL, currentYear.getId());
        try {
            return SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookup(loadQM);
        } catch (Exception e) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh all count of loading to chair", e);
        }
        return new ArrayList<>();
    }

    @Override
    public void clearFilter() {
        refresh(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }
}
