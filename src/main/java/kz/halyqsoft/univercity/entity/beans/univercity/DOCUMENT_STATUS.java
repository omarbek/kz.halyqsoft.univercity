package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.AbstractStatusEntity;

import javax.persistence.Entity;

@Entity
public class DOCUMENT_STATUS extends AbstractStatusEntity{
    public static String CREATED = "СОЗДАНО";
    public static String IN_PROCESS = "В ПРОЦЕССЕ";
    public static String REFUSED_FOR_CORRECTION = "ОТКАЗАНО НА ДОРАБОТКУ";
    public static String FINALLY_REFUSED = "ОТКАЗАНО ОКОНЧАТЕЛЬНО";
    public static String ACCEPTED = "ПРИНЯТО";

    public DOCUMENT_STATUS() {
    }
}
