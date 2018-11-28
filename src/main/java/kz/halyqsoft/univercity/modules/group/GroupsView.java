package kz.halyqsoft.univercity.modules.group;

import com.vaadin.ui.TabSheet;
import kz.halyqsoft.univercity.modules.group.tab.AutoCreationTab;
import kz.halyqsoft.univercity.modules.group.tab.ManualCreationTab;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.vaadin.view.AbstractTaskView;

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
