package kz.halyqsoft.univercity.entity.beans.univercity.view;

/**
 * @author Dmitry Dobrin.
 * @created 26.04.2017.
 */
public class V_UNT_CERTIFICATE extends V_USER_DOCUMENT {

    private final String caption;

    public V_UNT_CERTIFICATE(String caption) {
        this.caption = caption;
    }

    public String getCaption() {
        return caption;
    }
}
