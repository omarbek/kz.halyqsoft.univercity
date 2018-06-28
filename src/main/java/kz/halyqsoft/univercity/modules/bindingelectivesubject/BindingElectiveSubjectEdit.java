package kz.halyqsoft.univercity.modules.bindingelectivesubject;

import kz.halyqsoft.univercity.entity.beans.univercity.CATALOG_ELECTIVE_SUBJECTS;
import kz.halyqsoft.univercity.entity.beans.univercity.ELECTIVE_BINDED_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ENTRANCE_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.form.CommonFormWidget;
import org.r3a.common.vaadin.widget.table.TableWidget;

import javax.persistence.NoResultException;
import java.util.Date;
import java.util.List;

public class BindingElectiveSubjectEdit extends AbstractDialog {

    private CommonFormWidget selectSemestrCFW;
    private TableWidget selectSubjectTW;

    private final boolean isNew;

    BindingElectiveSubjectEdit(ELECTIVE_BINDED_SUBJECT electiveBinded,boolean isNew,BindingElectiveSubjectView bindingElectiveSubjectView)throws Exception{
        this.isNew = isNew;
    }

    private CommonFormWidget selectSemester() throws Exception{
        return null;
    }

    private TableWidget selectSubject() throws Exception{
        return null;
    }

    private void refreshSemestr(TableWidget selectSubjectTW) throws Exception{

    }

    private class SelectSemesterListener implements EntityListener{

        @Override
        public boolean preSave(Object source, Entity entity, boolean isNew, int buttonId) {
//            if (source.equals(electiveSubjectsGW)) {
//                try {
//                    QueryModel<CATALOG_ELECTIVE_SUBJECTS> catQM = new QueryModel<>(CATALOG_ELECTIVE_SUBJECTS.class);
//                    SPECIALITY spec = (SPECIALITY) specCB.getValue();
//                    ENTRANCE_YEAR year = (ENTRANCE_YEAR) yearCB.getValue();
//                    catQM.addWhere("speciality", ECriteria.EQUAL, spec.getId());
//                    catQM.addWhere("entranceYear", ECriteria.EQUAL, year.getId());
//                    CATALOG_ELECTIVE_SUBJECTS cat = getCat(catQM, spec, year);
//                    ELECTIVE_BINDED_SUBJECT electiveBindedSubject = (ELECTIVE_BINDED_SUBJECT) entity;
//                    electiveBindedSubject.setCatalogElectiveSubjects(cat);
//                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(electiveBindedSubject);
//                } catch (Exception e) {
//                    e.printStackTrace();//TODO catch
//                }
//            }
            return false;
        }

        private CATALOG_ELECTIVE_SUBJECTS getCat(QueryModel<CATALOG_ELECTIVE_SUBJECTS> catQM,
                                                 SPECIALITY spec, ENTRANCE_YEAR year) throws Exception {
            CATALOG_ELECTIVE_SUBJECTS cat;
            try {
                cat = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(catQM);
            } catch (NoResultException ex) {
                cat = new CATALOG_ELECTIVE_SUBJECTS();
                cat.setCreated(new Date());
                cat.setDeleted(Boolean.FALSE);
                cat.setEntranceYear(year);
                cat.setSpeciality(spec);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(cat);
            }
            return cat;
        }

        @Override
        public void handleEntityEvent(EntityEvent entityEvent) {
        }

        @Override
        public boolean preCreate(Object o, int i) {
            return false;
        }

        @Override
        public void onCreate(Object o, Entity entity, int i) {
        }

        @Override
        public boolean onEdit(Object o, Entity entity, int i) {
            return false;
        }

        @Override
        public boolean onPreview(Object o, Entity entity, int i) {
            return false;
        }

        @Override
        public void beforeRefresh(Object o, int i) {
        }

        @Override
        public void onRefresh(Object o, List<Entity> list) {
        }

        @Override
        public void onFilter(Object o, QueryModel queryModel, int i) {
        }

        @Override
        public void onAccept(Object o, List<Entity> list, int i) {
        }

        @Override
        public boolean preDelete(Object o, List<Entity> list, int i) {
            return false;
        }

        @Override
        public void onDelete(Object o, List<Entity> list, int i) {
        }

        @Override
        public void deferredCreate(Object o, Entity entity) {
        }

        @Override
        public void deferredDelete(Object o, List<Entity> list) {
        }

        @Override
        public void onException(Object o, Throwable throwable) {
        }
    }

    private class SelectSubjectListener implements EntityListener{
        @Override
        public boolean preSave(Object source, Entity entity, boolean isNew, int buttonId) {
//            if (source.equals(electiveSubjectsGW)) {
//                try {
//                    QueryModel<CATALOG_ELECTIVE_SUBJECTS> catQM = new QueryModel<>(CATALOG_ELECTIVE_SUBJECTS.class);
//                    SPECIALITY spec = (SPECIALITY) specCB.getValue();
//                    ENTRANCE_YEAR year = (ENTRANCE_YEAR) yearCB.getValue();
//                    catQM.addWhere("speciality", ECriteria.EQUAL, spec.getId());
//                    catQM.addWhere("entranceYear", ECriteria.EQUAL, year.getId());
//                    CATALOG_ELECTIVE_SUBJECTS cat = getCat(catQM, spec, year);
//                    ELECTIVE_BINDED_SUBJECT electiveBindedSubject = (ELECTIVE_BINDED_SUBJECT) entity;
//                    electiveBindedSubject.setCatalogElectiveSubjects(cat);
//                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(electiveBindedSubject);
//                } catch (Exception e) {
//                    e.printStackTrace();//TODO catch
//                }
//            }
            return false;
        }

        private CATALOG_ELECTIVE_SUBJECTS getCat(QueryModel<CATALOG_ELECTIVE_SUBJECTS> catQM,
                                                 SPECIALITY spec, ENTRANCE_YEAR year) throws Exception {
            CATALOG_ELECTIVE_SUBJECTS cat;
            try {
                cat = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(catQM);
            } catch (NoResultException ex) {
                cat = new CATALOG_ELECTIVE_SUBJECTS();
                cat.setCreated(new Date());
                cat.setDeleted(Boolean.FALSE);
                cat.setEntranceYear(year);
                cat.setSpeciality(spec);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(cat);
            }
            return cat;
        }

        @Override
        public void handleEntityEvent(EntityEvent entityEvent) {
        }

        @Override
        public boolean preCreate(Object o, int i) {
            return false;
        }

        @Override
        public void onCreate(Object o, Entity entity, int i) {
        }

        @Override
        public boolean onEdit(Object o, Entity entity, int i) {
            return false;
        }

        @Override
        public boolean onPreview(Object o, Entity entity, int i) {
            return false;
        }

        @Override
        public void beforeRefresh(Object o, int i) {
        }

        @Override
        public void onRefresh(Object o, List<Entity> list) {
        }

        @Override
        public void onFilter(Object o, QueryModel queryModel, int i) {
        }

        @Override
        public void onAccept(Object o, List<Entity> list, int i) {
        }

        @Override
        public boolean preDelete(Object o, List<Entity> list, int i) {
            return false;
        }

        @Override
        public void onDelete(Object o, List<Entity> list, int i) {
        }

        @Override
        public void deferredCreate(Object o, Entity entity) {
        }

        @Override
        public void deferredDelete(Object o, List<Entity> list) {
        }

        @Override
        public void onException(Object o, Throwable throwable) {
        }
    }

    protected String createTitle() {
        return "Binding elective";//TODO
    }
}
