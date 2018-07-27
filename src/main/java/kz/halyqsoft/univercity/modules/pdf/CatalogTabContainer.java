package kz.halyqsoft.univercity.modules.pdf;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.CATALOG;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_DOCUMENT;
import kz.halyqsoft.univercity.entity.beans.univercity.PDF_PROPERTY;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.apache.commons.io.FileUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractCommonView;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Set;


public class CatalogTabContainer extends AbstractCommonView {

    public CatalogTabContainer() {

        GridWidget catalogGW = new GridWidget(CATALOG.class);
        catalogGW.setButtonVisible(IconToolbar.PREVIEW_BUTTON, false);
        catalogGW.setButtonVisible(IconToolbar.ADD_BUTTON, false);
        catalogGW.setButtonVisible(IconToolbar.EDIT_BUTTON, false);

        catalogGW.setSizeFull();
        catalogGW.setImmediate(true);

        getContent().addComponent(catalogGW);
    }


    @Override
    public String getViewName() {
        return getUILocaleUtil().getEntityLabel(CATALOG.class);
    }

    @Override
    protected String getViewTitle(Locale locale) {
        return getUILocaleUtil().getEntityLabel(CATALOG.class);
    }

    @Override
    public void initView(boolean b) throws Exception {

    }

}
