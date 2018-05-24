package kz.halyqsoft.univercity.filter;

import kz.halyqsoft.univercity.entity.beans.univercity.CONTRACT_PAYMENT_TYPE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LEVEL;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDENT_DIPLOMA_TYPE;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;

public class FAccountantFilter extends AbstractFilterBean{

    private STUDENT_DIPLOMA_TYPE diplomaType;
    private LEVEL level;
    private CONTRACT_PAYMENT_TYPE contractPaymentType;

    public FAccountantFilter() {
    }

    public STUDENT_DIPLOMA_TYPE getDiplomaType() {
        return diplomaType;
    }

    public void setDiplomaType(STUDENT_DIPLOMA_TYPE diplomaType) {
        this.diplomaType = diplomaType;
    }

    public LEVEL getLevel() {
        return level;
    }

    public void setLevel(LEVEL level) {
        this.level = level;
    }

    public CONTRACT_PAYMENT_TYPE getContractPaymentType() {
        return contractPaymentType;
    }

    public void setContractPaymentType(CONTRACT_PAYMENT_TYPE contractPaymentType) {
        this.contractPaymentType = contractPaymentType;
    }


    @Override
    public boolean hasFilter() {
        return !(diplomaType == null && level == null && contractPaymentType == null);
    }
}
