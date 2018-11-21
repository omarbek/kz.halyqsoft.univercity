package kz.halyqsoft.univercity.modules.studentindividualeduplan.modules;

import com.vaadin.server.FontAwesome;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import kz.halyqsoft.univercity.modules.studentindividualeduplan.StudentPlan;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;

import java.util.List;

public abstract class BaseModule extends VerticalLayout implements EntityListener{
    private BaseModule prevPage;
    private GridWidget mainGW;
    private DBGridModel mainGM;
    protected StudentPlan studentPlan;
    private Button backBtn;
    private HorizontalLayout buttonsPanel;
    public BaseModule(Class<? extends Entity> myclass, BaseModule baseModule , StudentPlan studentPlan){
        mainGW = new GridWidget(myclass);
        mainGW.showToolbar(false);
        mainGM = (DBGridModel) mainGW.getWidgetModel();
        mainGW.addEntityListener(this);
        this.prevPage = baseModule;
        this.studentPlan = studentPlan;
        backBtn = new Button(CommonUtils.getUILocaleUtil().getCaption("back"));
        backBtn.setIcon(FontAwesome.CARET_SQUARE_O_LEFT);
        backBtn.setStyleName(ValoTheme.BUTTON_HUGE);
        buttonsPanel = CommonUtils.createButtonPanel();
        if(prevPageExists()){
            backBtn.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent clickEvent) {
                    studentPlan.getMainVL().removeAllComponents();
                    studentPlan.getMainVL().addComponent(baseModule);
                }
            });
            buttonsPanel.addComponent(backBtn);
        }
        setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        addComponent(buttonsPanel);
        addComponent(mainGW);
        setImmediate(true);
        setSizeFull();
    }

    public HorizontalLayout getButtonsPanel() {
        return buttonsPanel;
    }


    public boolean prevPageExists(){
        return getPrevPage() != null;
    }

    public BaseModule getPrevPage() {
        return prevPage;
    }

    public GridWidget getMainGW() {
        return mainGW;
    }

    public void setMainGW(GridWidget mainGW) {
        this.mainGW = mainGW;
    }

    public DBGridModel getMainGM() {
        return mainGM;
    }

    public void setMainGM(DBGridModel mainGM) {
        this.mainGM = mainGM;
    }

    @Override
    public boolean preCreate(Object o, int i) {
        return true;
    }

    @Override
    public void onCreate(Object o, Entity entity, int i) {

    }

    @Override
    public boolean onEdit(Object o, Entity entity, int i) {
        return true;
    }

    @Override
    public boolean onPreview(Object o, Entity entity, int i) {
        return true;
    }

    @Override
    public void beforeRefresh(Object o, int i) {

    }

    @Override
    public void onRefresh(Object o, List<Entity> list) {

    }

    @Override
    public void onFilter(Object o, QueryModel queryModel, int i) {

    }

    @Override
    public void onAccept(Object o, List<Entity> list, int i) {

    }

    @Override
    public boolean preSave(Object o, Entity entity, boolean b, int i) throws Exception {
        return true;
    }

    @Override
    public boolean preDelete(Object o, List<Entity> list, int i) {
        return true;
    }

    @Override
    public void onDelete(Object o, List<Entity> list, int i) {

    }

    @Override
    public void deferredCreate(Object o, Entity entity) {

    }

    @Override
    public void deferredDelete(Object o, List<Entity> list) {

    }

    @Override
    public void onException(Object o, Throwable throwable) {

    }
}
