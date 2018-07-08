package kz.halyqsoft.univercity.modules.workflow.views;

import com.vaadin.ui.Button;

public class CreateView extends BaseView{

    public CreateView(String title){
        super(title);
    }

    @Override
    public void initView(boolean b) throws Exception {
        super.initView(b);
        getContent().addComponent(new Button(getViewName()));
    }
}
