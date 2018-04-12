package kz.halyqsoft.univercity.modules.catalog;

import com.vaadin.ui.HorizontalSplitPanel;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.changelisteners.CountryChangeListener;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.select.EAggregate;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.entity.tree.CommonTree;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.AbstractSelectWidget;
import org.r3a.common.vaadin.widget.DBSelectModel;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;
import org.r3a.common.vaadin.widget.tree.CommonTreeWidget;
import org.r3a.common.vaadin.widget.tree.LazyCommonTreeWidget;
import org.r3a.common.vaadin.widget.tree.model.UOTreeModel;

import java.util.List;

/**
 * @author Omarbek
 * @created on 12.03.2018
 */
public class CatalogView extends AbstractTaskView implements EntityListener {

    private HorizontalSplitPanel mainHSP;
    private CommonTreeWidget entitiesCTW;
    private AbstractSelectWidget classASW;

    public CatalogView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        mainHSP = new HorizontalSplitPanel();
        mainHSP.setSizeFull();
        mainHSP.setSplitPosition(30);

        UOTreeModel uoTreeModel = new UOTreeModel(CatalogList.getCatalogList());
        uoTreeModel.setTitleVisible(false);
        entitiesCTW = new CommonTreeWidget(uoTreeModel);
        entitiesCTW.showToolbar(false);
        entitiesCTW.addEntityListener(this);
        mainHSP.addComponent(entitiesCTW);
        getContent().addComponent(mainHSP);
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
        if (ev.getAction() == EntityEvent.SELECTED) {
            if (ev.getSource().equals(entitiesCTW)) {
                Class<? extends Entity> entityClass = ((CatalogEntity) ev.getEntities().get(0)).getEntityClass();
                if (classASW != null) {
                    mainHSP.removeComponent(classASW);
                }
                if (entityClass.equals(COUNTRY.class)) {
                    classASW = new LazyCommonTreeWidget(COUNTRY.class);
                    classASW.getContainerPanel().setHeight(490, Unit.PIXELS);
                    classASW.addEntityListener(this);
                    classASW.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
                    classASW.setButtonVisible(IconToolbar.PREVIEW_BUTTON, true);
                    QueryModel qm = ((DBSelectModel) classASW.getWidgetModel()).getQueryModel();
                    qm.addOrder("countryName");
                } else if (entityClass.equals(DEPARTMENT.class)) {
                    classASW = new LazyCommonTreeWidget(DEPARTMENT.class);
                    classASW.addEntityListener(this);
                    ((LazyCommonTreeWidget) classASW).setCheckParents(false);

                    FormModel fm = ((DBSelectModel) classASW.getWidgetModel()).getFormModel();
                    fm.getFieldModel("type");

                    QueryModel qm = ((DBSelectModel) classASW.getWidgetModel()).getQueryModel();
                    qm.addWhere("deleted", Boolean.FALSE);
                    qm.addWhereNull("parent");
                    qm.addOrder("deptName");
                } else {
                    classASW = new GridWidget(entityClass);
                    classASW.addEntityListener(this);
                    DBGridModel tm = (DBGridModel) classASW.getWidgetModel();
                    tm.setRowNumberWidth(16);
                    QueryModel qm = ((DBSelectModel) classASW.getWidgetModel()).getQueryModel();
                    if (AbstractCategoryEntity.class.isAssignableFrom(entityClass)) {
                        classASW.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
                        qm.addWhere("categoryName", null, null, true);
                    } else if (AbstractStatusEntity.class.isAssignableFrom(entityClass)) {
                        classASW.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
                        qm.addWhere("statusName", null, null, true);
                    } else if (AbstractTypeEntity.class.isAssignableFrom(entityClass)) {
                        if (!entityClass.equals(SCHOOL_TYPE.class)) {
                            classASW.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
                        } else {
                            classASW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
                        }
                        qm.addWhere("typeName", null, null, true);
                    } else if (entityClass.equals(AWARD.class)) {
                        classASW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
                        qm.addWhere("awardName", null, null, true);
                    } else if (entityClass.equals(DEGREE.class)) {
                        classASW.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
                        qm.addWhere("degreeName", null, null, true);
                    } else if (entityClass.equals(LANGUAGE_LEVEL.class)) {
                        classASW.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
                        qm.addWhere("levelName", null, null, true);
                    } else if (entityClass.equals(LANGUAGE.class)) {
                        qm.addWhere("langName", null, null, true);
                    } else if (entityClass.equals(NATIONALITY.class)) {
                        classASW.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
                        qm.addWhere("nationName", null, null, true);
                    } else if (entityClass.equals(POST.class)) {
                        classASW.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
                        qm.addWhere("postName", null, null, true);
                    } else if (entityClass.equals(UNIVERSITY.class)) {
                        classASW.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
                        qm.addWhere("universityName", null, null, true);
                        qm.addWhere("useDefault", Boolean.FALSE, true);
                    } else if (entityClass.equals(MARITAL_STATUS.class)) {
                        classASW.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
                        qm.addWhere("statusName", null, null, true);
                    } else if (entityClass.equals(UNT_SUBJECT.class)) {
                        classASW.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
                        qm.addWhere("subjectName", null, null, true);
                    } /*else if (entityClass.equals(ENTRANCE_YEAR.class)) {
                        classASW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
                        tm.getColumnModel("beginYear").setFormat(NumberUtils.INTEGER_FORMAT);
                        tm.getColumnModel("endYear").setFormat(NumberUtils.INTEGER_FORMAT);
                    } */ else if (entityClass.equals(CORPUS.class)) {
                        classASW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
                    } else if (entityClass.equals(LOCK_REASON.class)) {
                        qm.addWhere("reason", null, null, true);//for filter
                    } else if (entityClass.equals(SPECIALITY.class)) {
                        FormModel fm = ((DBSelectModel) classASW.getWidgetModel()).getFormModel();
                        QueryModel specQM = ((FKFieldModel) fm.getFieldModel("department")).getQueryModel();
                        specQM.addWhereNotNull("parent");
                    }
                }
                mainHSP.addComponent(classASW);
            }
        }
    }

    @Override
    public void onCreate(Object source, Entity e, int buttonId) {
        if (source.equals(classASW)) {
            if (e instanceof CommonTree) {//need for COUNTRY
                ((CommonTree) e).setParent((CommonTree) classASW.getSelectedEntity());
            }
        }
    }

    @Override
    public boolean onEdit(Object source, Entity e, int buttonId) {
        if (e instanceof ORGANIZATION) {//TODO check
            FormModel fm = ((DBSelectModel) classASW.getWidgetModel()).getFormModel();
            FKFieldModel countryFM = (FKFieldModel) fm.getFieldModel("country");

            COUNTRY country = ((ORGANIZATION) e).getCountry();

            FKFieldModel regionFM = (FKFieldModel) fm.getFieldModel("region");
            QueryModel regionQM = regionFM.getQueryModel();
            if (country == null) {
                regionQM.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));
            } else {
                regionQM.addWhere("parent", ECriteria.EQUAL, country.getId());
            }

            countryFM.getListeners().add(new CountryChangeListener(((ORGANIZATION) e).getRegion(), regionFM));
        }

        return true;
    }


    @Override
    public boolean preSave(Object source, Entity e, boolean isNew, int buttonId) {
        if (source.equals(classASW)) {
            if (e instanceof DEPARTMENT) {
                if (isNew) {
                    QueryModel<DEPARTMENT> qm = new QueryModel<>(DEPARTMENT.class);
                    qm.addSelect("id", EAggregate.COUNT);
                    try {
                        Integer count = ((Long) SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItems(qm)).intValue();
                        count++;
                        ((DEPARTMENT) e).setCode(CommonUtils.getCode(count));
                    } catch (Exception ex) {
                        CommonUtils.showMessageAndWriteLog("Unable to generate a code for department", ex);
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public boolean preDelete(Object source, List<Entity> entities, int buttonId) {
        if (source.equals(classASW)) {
            boolean delete = true;
            for (Entity e : entities) {
                if (e instanceof DEPARTMENT) {
                    delete = false;
                    ((DEPARTMENT) e).setDeleted(true);
                    try {
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(e);
                    } catch (Exception ex) {
                        CommonUtils.showMessageAndWriteLog("Unable to delete a department", ex);
                    }
                } else if (e instanceof SPECIALITY) {
                    delete = false;
                    try {
                        ((SPECIALITY) e).setDeleted(true);
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(e);
                    } catch (Exception ex) {
                        CommonUtils.showMessageAndWriteLog("Unable to delete a speciality", ex);
                    }
                }
            }

            try {
                classASW.refresh();
            } catch (Exception ex) {
                CommonUtils.showMessageAndWriteLog("Unable to refresh entities", ex);
            }
            return delete;
        }
        return true;
    }

}
