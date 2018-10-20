package kz.halyqsoft.univercity.modules.group;

import com.vaadin.data.Validator;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.validator.IntegerRangeValidator;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.MouseEvents;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import com.vaadin.ui.declarative.FieldBinder;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VAccountants;
import kz.halyqsoft.univercity.filter.FAccountantFilter;
import kz.halyqsoft.univercity.filter.FGroupFilter;
import kz.halyqsoft.univercity.filter.panel.GroupFilterPanel;
import kz.halyqsoft.univercity.modules.group.tab.AutoCreationTab;
import kz.halyqsoft.univercity.modules.group.tab.ManualCreationTab;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.FormWidgetDialog;
import org.r3a.common.vaadin.widget.form.field.validator.IntegerValidator;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;
import java.util.Calendar;

import static kz.halyqsoft.univercity.utils.CommonUtils.getUILocaleUtil;

public class GroupsView extends AbstractTaskView{


    private TabSheet mainTS;

    public GroupsView(AbstractTask task) throws Exception {
        super(task);
    }






    @Override
    public void initView(boolean b) throws Exception {
        mainTS = new TabSheet();
        mainTS.setImmediate(true);
        String autoGenStr = getUILocaleUtil().getCaption("auto.generation");
        String manualGenStr = getUILocaleUtil().getCaption("manual.generation");
        AutoCreationTab autoCreationTab = new AutoCreationTab(autoGenStr);
        TabSheet.Tab autoTab = mainTS.addTab(autoCreationTab.getMainVL() , getUILocaleUtil().getCaption("auto.generation") );

        ManualCreationTab manualCreationTab = new ManualCreationTab(getUILocaleUtil().getCaption("manual.generation"));
        TabSheet.Tab manualTab = mainTS.addTab(manualCreationTab.getMainVL() , manualGenStr);

        mainTS.addSelectedTabChangeListener(new TabSheet.SelectedTabChangeListener() {
            @Override
            public void selectedTabChange(TabSheet.SelectedTabChangeEvent selectedTabChangeEvent) {
                if(mainTS.getSelectedTab().equals(autoCreationTab.getMainVL())){
                    autoCreationTab.doFilter(autoCreationTab.getGroupFilterPanel().getFilterBean());
                }else if(mainTS.getSelectedTab().equals(manualCreationTab.getMainVL())){
                    manualCreationTab.doFilter(manualCreationTab.getFilterPanel().getFilterBean());
                }
            }
        });

        getContent().addComponent(mainTS);

    }

}
