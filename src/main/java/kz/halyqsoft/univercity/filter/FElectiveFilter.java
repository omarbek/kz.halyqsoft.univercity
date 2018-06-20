package kz.halyqsoft.univercity.filter;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;

public class FElectiveFilter extends AbstractFilterBean {

    private SUBJECT firstSubject;
    private SUBJECT secondSubject;

    public SUBJECT getFirstSubject() {
        return firstSubject;
    }

    public void setFirstSubject(SUBJECT firstSubject) {
        this.firstSubject = firstSubject;
    }

    public SUBJECT getSecondSubject() {
        return secondSubject;
    }

    public void setSecondSubject(SUBJECT secondSubject) {
        this.secondSubject = secondSubject;
    }

    @Override
    public boolean hasFilter() {
        return (!(firstSubject == null && secondSubject == null));
    }
}
