package kz.halyqsoft.univercity.utils.changelisteners;

import com.vaadin.data.Property;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.ComboBox;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.DEPARTMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import kz.halyqsoft.univercity.utils.ErrorUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.dialog.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Omarbek
 * @created on 03.04.2018
 */
public class FacultyChangeListener implements Property.ValueChangeListener {
    private final ComboBox specialtyCB;

    public FacultyChangeListener(ComboBox specialtyCB) {
        this.specialtyCB = specialtyCB;
    }

    @Override
    public void valueChange(Property.ValueChangeEvent ev) {

        DEPARTMENT faculty = (DEPARTMENT) ev.getProperty().getValue();
        if (faculty != null) {
            refreshSpecialities(faculty.getId());
        } else {
            refreshSpecialities(ID.valueOf(-1));
        }
    }

    public void refreshSpecialities(ID facultyID) {
        List<SPECIALITY> list = new ArrayList<>(1);
        QueryModel<SPECIALITY> specialtyQM = new QueryModel<>(SPECIALITY.class);
        FromItem fi = specialtyQM.addJoin(EJoin.INNER_JOIN, "department", DEPARTMENT.class, "id");
        specialtyQM.addWhere("deleted", Boolean.FALSE);
        specialtyQM.addWhereAnd(fi, "parent", ECriteria.EQUAL, facultyID);
        specialtyQM.addOrder("specName");
        try {
            list = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(specialtyQM);
        } catch (Exception ex) {
            ErrorUtils.LOG.error("Unable to load specialty list: ", ex);
            Message.showError(ex.toString());
        }
        BeanItemContainer<SPECIALITY> specialtyBIC = new BeanItemContainer<>(SPECIALITY.class, list);
        specialtyCB.setContainerDataSource(specialtyBIC);
    }
}
