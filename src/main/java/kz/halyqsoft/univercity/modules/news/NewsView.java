package kz.halyqsoft.univercity.modules.news;

import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.EMPLOYEE_DEPT;
import kz.halyqsoft.univercity.entity.beans.univercity.NEWS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @author Omarbek
 * @created on 06.09.2018
 */
public class NewsView extends AbstractTaskView implements EntityListener {

    private GridWidget newsGW;

    public NewsView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        newsGW = new GridWidget(NEWS.class);
        newsGW.addEntityListener(this);

        DBGridModel newsGM = (DBGridModel) newsGW.getWidgetModel();
        newsGM.setRowNumberVisible(true);
        newsGM.setMultiSelect(true);
        newsGM.setRefreshType(ERefreshType.MANUAL);

        refresh();

        getContent().addComponent(newsGW);
    }

    private void refresh() {
        try {
            List<NEWS> newsList = getNews();
            ((DBGridModel) newsGW.getWidgetModel()).setEntities(newsList);
            newsGW.refresh();
        } catch (Exception e) {
            e.printStackTrace();//TODO catch
        }
    }

    private List<NEWS> getNews() throws Exception {
        StringBuilder sqlSB = new StringBuilder("select * from news where deleted = false ");
        if (!CommonUtils.isAdmin()) {
            sqlSB.append(" and EXPIRE_DATE > now() ");
        }
        sqlSB.append("order by created desc");
        List<NEWS> newsList = SessionFacadeFactory.getSessionFacade(
                CommonEntityFacadeBean.class).lookup(sqlSB.toString(), new HashMap<>(), NEWS.class);
        List<NEWS> filteredNews = new ArrayList<>();
        if (!CommonUtils.isAdmin()) {
            for (NEWS news : newsList) {
                if (news.getDepartment() != null && news.getDepartment().equals(getDepartmentByUser(
                        CommonUtils.getCurrentUser()))) {
                    filteredNews.add(news);
                } else {
                    if (news.isGlobalNews()) {
                        filteredNews.add(news);
                    }
                }
            }
        } else {
            filteredNews.addAll(newsList);
        }

        return filteredNews;
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
        if (ev.getAction() == EntityEvent.CREATED || ev.getAction() == EntityEvent.MERGED) {
            refresh();
        }
    }

    @Override
    public boolean preSave(Object source, Entity e, boolean isNew, int buttonId) {
        try {
            NEWS news = (NEWS) e;
            if (!isNew) {
                news.setUpdated(new Date());
            } else {
                USERS user = CommonUtils.getCurrentUser();
                news.setDeleted(false);
                news.setUser(user);
                news.setDepartment(getDepartmentByUser(user));
                news.setCreated(new Date());
            }
        } catch (Exception e1) {
            e1.printStackTrace();//TODO catch
        }
        return true;
    }

    private DEPARTMENT getDepartmentByUser(USERS user) throws Exception {
        QueryModel<DEPARTMENT> departmentQM = new QueryModel<>(DEPARTMENT.class);
        FromItem emplDeptFI = departmentQM.addJoin(EJoin.INNER_JOIN, "id", EMPLOYEE_DEPT.class, "department");
        departmentQM.addWhere(emplDeptFI, "employee", ECriteria.EQUAL, user.getId());
        departmentQM.addWhere("deleted", false);
        List<DEPARTMENT> departments = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookup(departmentQM);
        if (departments.isEmpty()) {
            return null;
        }
        return departments.get(0);
    }

    @Override
    public boolean onEdit(Object source, Entity e, int buttonId) {
        NEWS news = (NEWS) e;
        if (CommonUtils.isAdmin()) {
            return true;
        }
        if (news.getUser().equals(CommonUtils.getCurrentUser())) {
            return true;
        }
        Message.showInfo(getUILocaleUtil().getMessage("cannot.edit"));
        return false;
    }

    @Override
    public boolean preDelete(Object source, List<Entity> entities, int buttonId) {
        List<NEWS> newsList = new ArrayList<>();
        for (Entity entity : entities) {
            NEWS news = (NEWS) entity;
            if (!(news.getUser().equals(CommonUtils.getCurrentUser()) || CommonUtils.isAdmin())) {
                Message.showInfo(getUILocaleUtil().getMessage("cannot.delete"));
                return false;
            }
            news.setDeleted(true);
            news.setUpdated(new Date());
            newsList.add(news);
        }
        try {
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(newsList);
            refresh();
        } catch (Exception e) {
            e.printStackTrace();//TODO catch
        }
        return false;
    }
}
