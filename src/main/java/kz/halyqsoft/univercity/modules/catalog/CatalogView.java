package kz.halyqsoft.univercity.modules.catalog;

import com.vaadin.data.HasValue;
import com.vaadin.ui.HorizontalSplitPanel;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
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
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.AbstractToolbar;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;
import org.r3a.common.vaadin.widget.tree.CommonTreeWidget;
import org.r3a.common.vaadin.widget.tree.LazyCommonTreeWidget;
import org.r3a.common.vaadin.widget.tree.model.UOTreeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Omarbek
 * @created on 12.03.2018
 */
public class CatalogView extends AbstractTaskView implements EntityListener {

    private HorizontalSplitPanel hsp;
    private CommonTreeWidget ctw;
    private AbstractSelectWidget asw;

    public CatalogView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        hsp = new HorizontalSplitPanel();
        hsp.setSizeFull();
        hsp.setSplitPosition(30);

        UOTreeModel treeModel = new UOTreeModel(CatalogList.getCatalogList());
        treeModel.setTitleVisible(false);
        ctw = new CommonTreeWidget(treeModel);//TODO NullPointerException
        ctw.showToolbar(false);
        ctw.addEntityListener(this);
        hsp.addComponent(ctw);
        getContent().addComponent(hsp);
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
        if (ev.getAction() == EntityEvent.SELECTED) {
            if (ev.getSource().equals(ctw)) {
                Class<? extends Entity> entityClass = ((CatalogEntity) ev.getEntities().get(0)).getEntityClass();
                if (asw != null) {
                    hsp.removeComponent(asw);
                }
                if (entityClass.equals(COUNTRY.class)) {
                    asw = new LazyCommonTreeWidget(COUNTRY.class);
                    asw.getContainerPanel().setHeight(490, Unit.PIXELS);
                    asw.addEntityListener(this);
                    asw.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
                    asw.setButtonVisible(IconToolbar.PREVIEW_BUTTON, true);
                    QueryModel qm = ((DBSelectModel) asw.getWidgetModel()).getQueryModel();
                    qm.addOrder("countryName");
                } else if (entityClass.equals(DEPARTMENT.class)) {
                    asw = new LazyCommonTreeWidget(DEPARTMENT.class);
                    asw.addEntityListener(this);
                    ((LazyCommonTreeWidget) asw).setCheckParents(false);

                    FormModel fm = ((DBSelectModel) asw.getWidgetModel()).getFormModel();
                    FKFieldModel typeFM = (FKFieldModel) fm.getFieldModel("type");
//                    QueryModel typeQM = typeFM.getQueryModel();
//                    typeQM.getFrom().getBaseItem().setSchema("KBTU");

                    QueryModel qm = ((DBSelectModel) asw.getWidgetModel()).getQueryModel();
                    qm.addWhere("deleted", Boolean.FALSE);
                    qm.addWhereNull("parent");
                    qm.addOrder("deptName");
                } else {
                    asw = new GridWidget(entityClass);
                    asw.addEntityListener(this);
                    DBGridModel tm = (DBGridModel) asw.getWidgetModel();
                    tm.setRowNumberWidth(16);
                    QueryModel qm = ((DBSelectModel) asw.getWidgetModel()).getQueryModel();
                    if (AbstractCategoryEntity.class.isAssignableFrom(entityClass)) {
                        asw.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
                        qm.addWhere("categoryName", null, null, true);
                    } else if (AbstractStatusEntity.class.isAssignableFrom(entityClass)) {
                        asw.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
                        qm.addWhere("statusName", null, null, true);
                    } else if (AbstractTypeEntity.class.isAssignableFrom(entityClass)) {
                        if (!entityClass.equals(SCHOOL_TYPE.class)) {
                            asw.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
                        } else {
                            asw.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
                        }
                        qm.addWhere("typeName", null, null, true);
                    } else if (entityClass.equals(AWARD.class)) {
                        asw.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
                        qm.addWhere("awardName", null, null, true);
                    } else if (entityClass.equals(DEGREE.class)) {
                        asw.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
                        qm.addWhere("degreeName", null, null, true);
                    } else if (entityClass.equals(LANGUAGE_LEVEL.class)) {
                        asw.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
                        qm.addWhere("levelName", null, null, true);
                    } else if (entityClass.equals(LANGUAGE.class)) {
                        qm.addWhere("langName", null, null, true);
                    } else if (entityClass.equals(NATIONALITY.class)) {
                        asw.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
                        qm.addWhere("nationName", null, null, true);
                    } else if (entityClass.equals(POST.class)) {
                        asw.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
                        qm.addWhere("postName", null, null, true);
                    } else if (entityClass.equals(UNIVERSITY.class)) {
                        asw.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
                        qm.addWhere("universityName", null, null, true);
                        qm.addWhere("useDefault", Boolean.FALSE, true);
                    /*} else if (entityClass.equals(T_UNT_SUBJECT.class)) {
                        asw.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
						qm.addWhere("subjectName", null, null, true);*/
                    }
//                    else if (entityClass.equals(VT_SPECIALITY.class)) {
//                        tm.setCrudEntityClass(T_SPECIALITY.class);
//                        FKFieldModel levelFM = (FKFieldModel) tm.getFormModel().getFieldModel("level");
//                        QueryModel levelQM = levelFM.getQueryModel();
//                        levelQM.getFrom().getBaseItem().setSchema("KBTU");
//                        levelQM.addOrder("levelName");
//
//                        FKFieldModel chairFM = (FKFieldModel) tm.getFormModel().getFieldModel("department");
//                        chairFM.setDialogWidth(400);
//                        chairFM.setDialogHeight(350);
//                        QueryModel qmFormDept = chairFM.getQueryModel();
//                        qmFormDept.getFrom().getBaseItem().setSchema("KBTU");
//                        qmFormDept.addWhere("deleted", Boolean.FALSE);
//                        qmFormDept.addOrder("deptName");
//                        qm.addWhere("specName", null, null, true);
//                        qm.addWhereAnd("code", null, null, true);
//                        qm.addWhereInAnd("department", null, true);
//                        QueryModel qmFilterDept = ((FKSetFilterItemModel) tm.getFilterModel().getFilterItemModel("department")).getQueryModel();
//                        qmFilterDept.addWhere("deleted", Boolean.FALSE);
//                        qmFilterDept.addOrder("deptName");
//
//                        FKFieldModel parentFM = (FKFieldModel) tm.getFormModel().getFieldModel("parent");
//                        QueryModel parentQM = parentFM.getQueryModel();
//                        parentQM.getFrom().getBaseItem().setSchema("KBTU");
//                        parentQM.addWhere("type", ECondition.EQUAL, T_SPECIALITY_TYPE.SPECIALITY_ID);
//                    } else if (entityClass.equals(VT_ORGANIZATION.class)) {
//                        tm.setCrudEntityClass(T_ORGANIZATION.class);
//                        FormModel fm = ((DBSelectModel) asw.getWidgetModel()).getFormModel();
//                        FKFieldModel organizationTypeFM = (FKFieldModel) fm.getFieldModel("organizationType");
//                        QueryModel organizationTypeQM = organizationTypeFM.getQueryModel();
//                        organizationTypeQM.getFrom().getBaseItem().setSchema("KBTU");
//                        organizationTypeQM.addOrder("typeName");
//
//                        FKFieldModel countryFM = (FKFieldModel) fm.getFieldModel("country");
//                        QueryModel countryQM = countryFM.getQueryModel();
//                        countryQM.getFrom().getBaseItem().setSchema("KBTU");
//                        countryQM.addWhereNull("parent");
//                        countryQM.addOrder("countryName");
//
//                        FKFieldModel regionFM = (FKFieldModel) fm.getFieldModel("region");
//                        QueryModel regionQM = regionFM.getQueryModel();
//                        regionQM.getFrom().getBaseItem().setSchema("KBTU");
//                        regionQM.addWhere("parent", ECondition.EQUAL, ID.valueOf(-1));
//                        regionQM.addOrder("countryName");
//
//                        countryFM.getListeners().add(new CountryChangeListener(null, regionFM));
//                    } else if (entityClass.equals(VT_ACADEMIC_DEGREE.class)) {
//                        tm.setCrudEntityClass(T_ACADEMIC_DEGREE.class);
//                        FormModel fm = ((DBSelectModel) asw.getWidgetModel()).getFormModel();
//
//                        FKFieldModel specialityFM = (FKFieldModel) fm.getFieldModel("speciality");
//                        QueryModel specialityQM = specialityFM.getQueryModel();
//                        specialityQM.getFrom().getBaseItem().setSchema("KBTU");
//                        specialityQM.addWhere("deleted", Boolean.FALSE);
//                    } else if (entityClass.equals(T_ENTRANCE_YEAR.class)) {
//                        asw.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
//                        tm.getColumnModel("beginYear").setFormat(NumberUtils.INTEGER_FORMAT);
//                        tm.getColumnModel("endYear").setFormat(NumberUtils.INTEGER_FORMAT);
//                    } else if (entityClass.equals(T_CORPUS.class)) {
//                        asw.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
//                    } else if (entityClass.equals(VT_SEMESTER_DATA.class)) {
//                        tm.setCrudEntityClass(T_SEMESTER_DATA.class);
//                        asw.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
//                        asw.setButtonVisible(AbstractToolbar.DELETE_BUTTON, false);
//                    }
                }
                hsp.addComponent(asw);
            }
        }
    }

    @Override
    public void onCreate(Object source, Entity e, int buttonId) {
        if (source.equals(asw)) {
            if (e instanceof CommonTree) {
                ((CommonTree) e).setParent((CommonTree) asw.getSelectedEntity());
            }
        }
    }

    @Override
    public boolean onEdit(Object source, Entity e, int buttonId) {
        if (e instanceof ORGANIZATION) {
            FormModel fm = ((DBSelectModel) asw.getWidgetModel()).getFormModel();
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
        if (source.equals(asw)) {
            if (e instanceof DEPARTMENT) {
                if (isNew) {
                    QueryModel<DEPARTMENT> qm = new QueryModel<>(DEPARTMENT.class);
                    qm.addSelect("id", EAggregate.COUNT);
                    try {
                        Integer count = (Integer) SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItems(qm);
                        count++;
                        String code = String.valueOf(count);
                        if (count < 10) {
                            code = "000" + code;
                        } else if (count < 100) {
                            code = "00" + code;
                        } else if (count < 1000) {
                            code = "0" + code;
                        }
                        ((DEPARTMENT) e).setCode(code);
                    } catch (Exception ex) {
                        LOG.error("Unable to generate a code for department: ", ex);
                        Message.showError(ex.getMessage());
                        return false;
                    }
                }
            }
        }

        return true;
    }

    @Override
    public boolean preDelete(Object source, List<Entity> entities, int buttonId) {
        if (source.equals(asw)) {
            boolean delete = true;
            for (Entity e : entities) {
                if (e instanceof DEPARTMENT) {
                    delete = false;
                    ((DEPARTMENT) e).setDeleted(true);
                    try {
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(e);
                    } catch (Exception ex) {
                        LOG.error("Unable to delete a department: ", ex);
                        Message.showError("Unable to delete a department");
                    }
                } else if (e instanceof SPECIALITY) {
                    delete = false;
                    try {
                        ((SPECIALITY) e).setDeleted(true);
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(e);
                    } catch (Exception ex) {
                        LOG.error("Unable to delete a speciality: ", ex);
                        Message.showError("Unable to delete a speciality");
                    }
                }
            }

            try {
                asw.refresh();
            } catch (Exception ex) {
                LOG.error("Unable to refresh entities: ", ex);
            }
            return delete;
        }
        return true;
    }

    private class CountryChangeListener implements HasValue.ValueChangeListener {

        private final COUNTRY region;
        private final FKFieldModel regionFM;

        public CountryChangeListener(COUNTRY region, FKFieldModel regionFM) {
            this.region = region;
            this.regionFM = regionFM;
        }

        @Override
        public void valueChange(HasValue.ValueChangeEvent ev) {
            Object value = ev.getValue();
            QueryModel qm = regionFM.getQueryModel();
            if (value != null) {
                qm.addWhere("parent", ECriteria.EQUAL, ((COUNTRY) value).getId());
            } else {
                qm.addWhere("parent", ECriteria.EQUAL, ID.valueOf(-1));
            }
            try {
                regionFM.refresh(region);
            } catch (Exception ex) {
                LOG.error("Unable to regions: ", ex);
            }
        }
    }


    @Override
    public boolean preCreate(Object source, int buttonId) {
        return true;
    }

    @Override
    public boolean onPreview(Object source, Entity e, int buttonId) {
        return true;
    }

    @Override
    public void beforeRefresh(Object source, int buttonId) {
    }

    @Override
    public void onRefresh(Object source, List<Entity> entities) {
    }

    @Override
    public void onFilter(Object source, QueryModel qm, int buttonId) {
    }

    @Override
    public void onAccept(Object source, List<Entity> entities, int buttonId) {
    }

    @Override
    public void onDelete(Object source, List<Entity> entities, int buttonId) {
    }

    @Override
    public void deferredCreate(Object source, Entity e) {
    }

    @Override
    public void deferredDelete(Object source, List<Entity> entities) {
    }

    @Override
    public void onException(Object source, Throwable ex) {
    }
}
