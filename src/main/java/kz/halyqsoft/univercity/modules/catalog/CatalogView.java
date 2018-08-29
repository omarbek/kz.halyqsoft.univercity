package kz.halyqsoft.univercity.modules.catalog;

import com.vaadin.ui.HorizontalSplitPanel;
import kz.halyqsoft.univercity.entity.beans.ROLES;
import kz.halyqsoft.univercity.entity.beans.ROLE_TASKS;
import kz.halyqsoft.univercity.entity.beans.TASKS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.TimeUtils;
import kz.halyqsoft.univercity.utils.changelisteners.CountryChangeListener;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
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

import java.math.BigDecimal;
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
                Class<? extends Entity> entityClass = ((CatalogEntity) ev.getEntities().get(0)).
                        getEntityClass();
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
                } else if (entityClass.equals(TASKS.class)) {
                    classASW = new LazyCommonTreeWidget(TASKS.class);

                    classASW.setButtonVisible(IconToolbar.PREVIEW_BUTTON, true);

                    classASW.addEntityListener(this);
                    FormModel taskFM = ((DBSelectModel) classASW.getWidgetModel()).getFormModel();

                    taskFM.getFieldModel("taskOrder").setRequired(false);
                    (taskFM.getFieldModel("taskOrder")).setInEdit(false);

                    taskFM.getFieldModel("visible").setRequired(false);
                    (taskFM.getFieldModel("visible")).setInEdit(false);

                    taskFM.getFieldModel("taskType").setRequired(false);
                    (taskFM.getFieldModel("taskType")).setInEdit(false);

                    taskFM.getFieldModel("iconPath").setRequired(false);
                    (taskFM.getFieldModel("iconPath")).setInEdit(false);


                    QueryModel<TASKS> tasksQM = new QueryModel<>(TASKS.class);
                    TASKS tasks = new TASKS();
                    ((LazyCommonTreeWidget) classASW).setCheckParents(false);
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
                        if (entityClass.equals(MARITAL_STATUS.class)) {
                            classASW.setButtonVisible(IconToolbar.DELETE_BUTTON, true);
                        } else {
                            classASW.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
                        }
                        qm.addWhere("statusName", null, null, true);
                    } else if (AbstractTypeEntity.class.isAssignableFrom(entityClass)) {
                        if (!(entityClass.equals(SCHOOL_TYPE.class)
                                || entityClass.equals(MEDICAL_CHECKUP_TYPE.class)
                                || entityClass.equals(CONTRACT_TYPE.class)
                                || entityClass.equals(EDUCATION_MODULE_TYPE.class))) {
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
                    } else if (entityClass.equals(UNT_SUBJECT.class)) {
                        classASW.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
                        qm.addWhere("subjectName", null, null, true);
                    } else if (entityClass.equals(CORPUS.class)) {
                        classASW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
                    } else if (entityClass.equals(LOCK_REASON.class)) {
                        qm.addWhere("reason", null, null, true);//for filter button
                    } else if (entityClass.equals(SPECIALITY.class)) {
                        qm.addWhere("deleted", Boolean.FALSE);
                        FromItem departmentFI = qm.addJoin(EJoin.INNER_JOIN, "department", DEPARTMENT.class,
                                "id");
                        qm.addWhere(departmentFI, "deleted", Boolean.FALSE);
                        qm.addWhereNotNull(departmentFI, "parent");

                        FormModel specFM = ((DBSelectModel) classASW.getWidgetModel()).getFormModel();
                        QueryModel specQM = ((FKFieldModel) specFM.getFieldModel("department")).getQueryModel();
                        specQM.addWhere("deleted", Boolean.FALSE);
                        specQM.addWhereNotNull("parent");
                    } else if (entityClass.equals(CREATIVE_EXAM_SUBJECT.class)) {
                        classASW.setButtonVisible(IconToolbar.DELETE_BUTTON, false);
                        classASW.setButtonVisible(IconToolbar.ADD_BUTTON, false);
                        qm.addWhere("subjectName", null, null, true);
                    }
                    /*else if (entityClass.equals(ACADEMIC_DEGREE.class)) {
                        FormModel fm = ((DBSelectModel) classASW.getWidgetModel()).getFormModel();
                        ((FKFieldModel) fm.getFieldModel("speciality")).setDialogWidth(500);
//                        (fm.getFieldModel("speciality")).setWidth(500);
                    } else if (entityClass.equals(ENTRANCE_YEAR.class)) {
                        classASW.setButtonVisible(AbstractToolbar.PREVIEW_BUTTON, false);
                        tm.getColumnModel("beginYear").setFormat(NumberUtils.INTEGER_FORMAT);
                        tm.getColumnModel("endYear").setFormat(NumberUtils.INTEGER_FORMAT);
                    }*/
                    else if (entityClass.equals(ROLES.class)) {
                        classASW.setButtonVisible(AbstractToolbar.FILTER_BUTTON, true);
                    }
                    else if (entityClass.equals(NON_ADMISSION_CAUSE.class)) {
                        classASW.setButtonVisible(AbstractToolbar.DELETE_BUTTON, false);
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
            } else if (e instanceof ACADEMIC_FORMULA) {
                ACADEMIC_FORMULA academicFormula = (ACADEMIC_FORMULA) e;
                academicFormula.setLcCount(0);
                academicFormula.setPrCount(0);
                academicFormula.setLbCount(0);
            }
        }
    }


    @Override
    public boolean onEdit(Object source, Entity e, int buttonId) {
        if (e instanceof ORGANIZATION) {
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
        } else if (e instanceof ORDER_TYPE) {
            Integer[] ids = {3, 4, 5, 7, 8};
            return !onEdit(e, ids);

        } else if (e instanceof STUDENT_CATEGORY) {
            Integer[] ids = {1, 2, 3};
            return !onEdit(e, ids);
        } else if (e instanceof STUDENT_STATUS) {
            Integer[] ids = {1, 2, 3, 4, 5, 6, 7, 8};
            return !onEdit(e, ids);
        } else if (e instanceof STUDY_DIRECT) {
            Integer[] ids = {9};
            return !onEdit(e, ids);
        } else if (e instanceof EQUIPMENT) {
            Integer[] ids = {3};
            return !onEdit(e, ids);
        }

        return true;
    }

    private boolean onEdit(Entity e, Integer[] ids) {
        ID entityId = e.getId();
        for (Integer id : ids) {
            if (entityId.equals(ID.valueOf(id))) {
                Message.showInfo(getUILocaleUtil().getMessage("cannot.edit"));
                return true;
            }
        }
        return false;
    }


    @Override
    public boolean preSave(Object source, Entity e, boolean isNew, int buttonId) {
        if (source.equals(classASW)) {
            if (e instanceof DEPARTMENT) {
                if (isNew) {
                    QueryModel<DEPARTMENT> qm = new QueryModel<>(DEPARTMENT.class);
                    qm.addSelect("id", EAggregate.COUNT);
                    try {
                        Integer count = ((Long) SessionFacadeFactory.getSessionFacade(
                                CommonEntityFacadeBean.class).lookupItems(qm)).intValue();
                        count++;
                        ((DEPARTMENT) e).setCode(CommonUtils.getCode(count));
                    } catch (Exception ex) {
                        CommonUtils.showMessageAndWriteLog("Unable to generate a code for department", ex);
                        return false;
                    }
                }
            } else if (e instanceof TASKS) {
                TASKS parent = ((TASKS) e).getParent();
                if (parent != null) {
                    Integer max = parent.getTaskOrder();
                    List<TASKS> children = parent.getChildren();
                    if (children.size() > 0) {
                        for (TASKS task : children) {
                            if (task.getTaskOrder() > max) {
                                max = task.getTaskOrder();
                            }
                        }
                    }
                    try {
                        max++;
                        ((TASKS) e).setTaskOrder(max);

                    } catch (Exception ex) {
                        CommonUtils.showMessageAndWriteLog("Unable to generate a taskOrder for TASKS", ex);
                        return false;
                    }

                } else {
                    QueryModel<TASKS> qm = new QueryModel<>(TASKS.class);
                    qm.addSelect("TASK_ORDER", EAggregate.MAX);

                    try {
                        BigDecimal bigDecimalMax = (BigDecimal) (SessionFacadeFactory.getSessionFacade(
                                CommonEntityFacadeBean.class).lookupItems(qm));
                        Integer max = bigDecimalMax.intValue();
                        max = max / 100;
                        max++;
                        max = max * 100;
                        ((TASKS) e).setTaskOrder(max);
                    } catch (Exception ex) {
                        CommonUtils.showMessageAndWriteLog("Unable to generate a taskOrder for TASKS", ex);
                        return false;
                    }

                }
                ((TASKS) e).setVisible(true);

            } else if (e instanceof ACADEMIC_FORMULA) {
                ACADEMIC_FORMULA academicFormula = (ACADEMIC_FORMULA) e;
                Integer sum = academicFormula.getLcCount() + academicFormula.getPrCount()
                        + academicFormula.getLbCount();
                if (!sum.equals(academicFormula.getCreditability().getCredit())) {
                    Message.showInfo(getUILocaleUtil().getCaption("sum.not.equal.credit"));
                    return false;
                }
                academicFormula.setFormula(academicFormula.getLcCount() + "/" +
                        academicFormula.getLbCount() + "/" + academicFormula.getPrCount());
            } else if (e instanceof TIME) {
                TIME time = (TIME) e;
                TimeUtils clock = new TimeUtils(time);
                if (clock.isError()) return false;
                String hours = clock.getHours();
                String minutes = clock.getMinutes();
                Double hoursInDouble = Double.valueOf(hours);
                if ("00".equals(minutes)) {
                    time.setTimeValue(hoursInDouble);
                } else if ("10".equals(minutes)) {
                    time.setTimeValue(hoursInDouble + 0.17);
                } else if ("20".equals(minutes)) {
                    time.setTimeValue(hoursInDouble + 0.33);
                } else if ("30".equals(minutes)) {
                    time.setTimeValue(hoursInDouble + 0.5);
                } else if ("40".equals(minutes)) {
                    time.setTimeValue(hoursInDouble + 0.67);
                } else if ("50".equals(minutes)) {
                    time.setTimeValue(hoursInDouble + 0.83);
                } else {
                    Message.showError("you should choose minutes with interval of 10 mins");//TODO
                    return false;
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
                    DEPARTMENT faculty = (DEPARTMENT) e;
                    faculty.setDeleted(true);
                    try {
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(faculty);
                        deleteSpecialities(faculty);

                        QueryModel<DEPARTMENT> chairQM = new QueryModel<>(DEPARTMENT.class);
                        chairQM.addWhere("parent", ECriteria.EQUAL, faculty.getId());
                        List<DEPARTMENT> chairs = SessionFacadeFactory.getSessionFacade(
                                CommonEntityFacadeBean.class).
                                lookup(chairQM);
                        for (DEPARTMENT chair : chairs) {
                            chair.setDeleted(true);
                            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(chair);
                            deleteSpecialities(chair);
                        }
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
                } else if (e instanceof TASKS) {
                    try {
                        //1 role tasks
                        QueryModel<ROLE_TASKS> queryModel = new QueryModel<>(ROLE_TASKS.class);
                        queryModel.addWhere("task", ECriteria.EQUAL, e.getId());
                        List<ROLE_TASKS> roleTasks = SessionFacadeFactory.getSessionFacade(
                                CommonEntityFacadeBean.class).lookup(queryModel);
                        if (roleTasks != null) {
                            if (roleTasks.size() > 0) {
                                Message.showError(getUILocaleUtil().getMessage("error.binded"));
                                return false;
                            }
                        }
                        //2
                        TASKS task = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                                lookup(TASKS.class, e.getId());
                        if (task != null) {
                            if (task.getChildren() != null) {
                                if (task.getChildren().size() > 0) {
                                    Message.showError(getUILocaleUtil().getMessage("error.children"));
                                    return false;
                                }
                            }
                        }

                    } catch (Exception ex) {
                        Message.showError(ex.getMessage());
                    }
                } else if (e instanceof STUDY_DIRECT) {
                    ID entityId = e.getId();
                    if (entityId.equals(ID.valueOf(9))) {
                        Message.showInfo(getUILocaleUtil().getMessage("cannot.delete"));
                        return true;
                    }
                } else if (e instanceof EQUIPMENT) {
                    ID entityId = e.getId();
                    if (entityId.equals(ID.valueOf(3))) {
                        Message.showInfo(getUILocaleUtil().getMessage("cannot.delete"));
                        return true;
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

    private void deleteSpecialities(DEPARTMENT chair) throws Exception {
        QueryModel<SPECIALITY> specQM = new QueryModel<>(SPECIALITY.class);
        specQM.addWhere("department", ECriteria.EQUAL, chair.getId());
        List<SPECIALITY> specialities = SessionFacadeFactory.getSessionFacade(
                CommonEntityFacadeBean.class).lookup(specQM);
        for (SPECIALITY speciality : specialities) {
            speciality.setDeleted(true);
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(speciality);
        }
    }
}
