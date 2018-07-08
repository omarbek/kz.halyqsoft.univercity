package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.AbstractStatusEntity;

import javax.persistence.Entity;

@Entity
public class DOCUMENT_SIGNER_STATUS extends AbstractStatusEntity{
    public static String CREATED = "СОЗДАНО";
    public static String SIGNED = "ПОДПИСАНО";
    public static String REFUSED_FOR_CORRECTION = "ОТКАЗАНО НА ДОРАБОТКУ";
    public static String FINALLY_REFUSED = "ОТКАЗАНО ОКОНЧАТЕЛЬНО";

    public DOCUMENT_SIGNER_STATUS() {
    }
}
