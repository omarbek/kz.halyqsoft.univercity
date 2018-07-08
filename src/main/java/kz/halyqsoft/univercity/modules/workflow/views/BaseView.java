package kz.halyqsoft.univercity.modules.workflow.views;

import org.r3a.common.vaadin.view.AbstractCommonView;

import java.util.Locale;

public class BaseView extends AbstractCommonView {
    private String title;
    public BaseView(String title){
        this.title = title;
        setSizeFull();
        try{
            initView(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public String getViewName() {
        return this.title;
    }

    @Override
    protected String getViewTitle(Locale locale) {
        return this.title;
    }

    @Override
    public void initView(boolean b) throws Exception {

    }
}
