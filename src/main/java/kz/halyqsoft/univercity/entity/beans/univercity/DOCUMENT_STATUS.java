package kz.halyqsoft.univercity.entity.beans.univercity;

import kz.halyqsoft.univercity.entity.beans.univercity.catalog.AbstractStatusEntity;
import org.r3a.common.entity.ID;

import javax.persistence.Entity;

@Entity
public class DOCUMENT_STATUS extends AbstractStatusEntity{
    public static String CREATED = "СОЗДАНО";
    public static String IN_PROCESS = "В ПРОЦЕССЕ";
    public static String REFUSED_FOR_CORRECTION = "ОТКАЗАНО НА ДОРАБОТКУ";
    public static String FINALLY_REFUSED = "ОТКАЗАНО ОКОНЧАТЕЛЬНО";
    public static String ACCEPTED = "ПРИНЯТО";

    public static ID CREATED_ID = ID.valueOf(1);
    public static ID IN_PROCESS_ID = ID.valueOf(2);
    public static ID REFUSED_FOR_CORRECTION_ID = ID.valueOf(3);
    public static ID FINALLY_REFUSED_ID = ID.valueOf(4);
    public static ID ACCEPTED_ID = ID.valueOf(5);


    public DOCUMENT_STATUS() {
    }
}
