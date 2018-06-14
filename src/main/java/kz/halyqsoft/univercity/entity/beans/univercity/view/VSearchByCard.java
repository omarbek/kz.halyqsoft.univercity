package kz.halyqsoft.univercity.entity.beans.univercity.view;

import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.EFieldType;
import org.r3a.common.entity.FieldInfo;
import org.r3a.common.entity.ID;

public class VSearchByCard extends AbstractEntity {

    @FieldInfo(type = EFieldType.TEXT, order = 2)
    private String code;

    @FieldInfo(type = EFieldType.TEXT, order = 3)
    private String fio;

    @FieldInfo(type = EFieldType.TEXT, order = 4)
    private String typeName;

    @FieldInfo(type = EFieldType.TEXT, order = 5)
    private String cardName;

    public VSearchByCard() {

    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }
}
