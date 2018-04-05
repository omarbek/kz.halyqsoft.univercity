package kz.halyqsoft.univercity.utils;

import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT_RELATIVE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.COUNTRY;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.form.FormModel;

import javax.persistence.NoResultException;

/**
 * @author Omarbek
 * @created on 05.04.2018
 */
public class AddressUtils {

    private COUNTRY region;
    private COUNTRY city;
    private COUNTRY village;

    public AddressUtils(int parent_number, FormModel parentFM, boolean readOnly, ID studentId) throws Exception {
        QueryModel<STUDENT_RELATIVE> relativeQM = new QueryModel<>(STUDENT_RELATIVE.class);
        relativeQM.addWhere("student", ECriteria.EQUAL, studentId);
        relativeQM.addWhereAnd("relativeType", ECriteria.EQUAL, ID.valueOf(parent_number));
        try {
            STUDENT_RELATIVE studentRelative = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(relativeQM);
            if (studentRelative != null) {
                parentFM.loadEntity(studentRelative.getId());
                studentRelative = (STUDENT_RELATIVE) parentFM.getEntity();
                region = studentRelative.getRegion();
                city = studentRelative.getCity();
                village = studentRelative.getVillage();
            }
        } catch (NoResultException ex) {
            if (readOnly) {
                parentFM.loadEntity(ID.valueOf(-1));
            } else {
                parentFM.createNew();
            }
        }
    }

    public COUNTRY getRegion() {
        return region;
    }

    public COUNTRY getCity() {
        return city;
    }

    public COUNTRY getVillage() {
        return village;
    }
}
