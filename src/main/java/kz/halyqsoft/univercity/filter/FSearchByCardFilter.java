package kz.halyqsoft.univercity.filter;


import kz.halyqsoft.univercity.entity.beans.univercity.catalog.CARD;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;

public class FSearchByCardFilter extends AbstractFilterBean {

    private CARD card;

    public CARD getCard() {
        return card;
    }

    public void setCard(CARD card) {
        this.card = card;
    }

    //@Override
    public boolean hasFilter() {
        return (!(card == null));
    }
}
