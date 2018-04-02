package kz.halyqsoft.univercity.entity.beans.univercity.enumeration;

import org.r3a.common.vaadin.locale.UILocaleUtil;

/**
 * @author Omarbek
 * @created on 02.04.2018
 */
public enum Directions {
    UP {
        @Override
        public String getLabel(UILocaleUtil localeUtil) {
            return localeUtil.getCaption("direction.up");
        }
    },

    DOWN {
        @Override
        public String getLabel(UILocaleUtil localeUtil) {
            return localeUtil.getCaption("direction.down");
        }
    },

    RIGHT {
        @Override
        public String getLabel(UILocaleUtil localeUtil) {
            return localeUtil.getCaption("direction.right");
        }
    },

    LEFT {
        @Override
        public String getLabel(UILocaleUtil localeUtil) {
            return localeUtil.getCaption("direction.left");
        }
    };

    public String getLabel(UILocaleUtil localeUtil) {
        return null;
    }
}
