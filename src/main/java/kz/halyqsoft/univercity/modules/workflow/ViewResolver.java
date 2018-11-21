package kz.halyqsoft.univercity.modules.workflow;

import kz.halyqsoft.univercity.modules.workflow.views.*;

public class ViewResolver {
    public BaseView getViewByTitle(String title) throws IllegalArgumentException{

        if(title.equals(WorkflowView.CREATE)){
            return new CreateView(title);
        }else if(title.equals(WorkflowView.MY_DOCUMENTS)){
            return new MyDocumentsView(title);
        }else if(title.equals(WorkflowView.I_ON_AGREE)){
            return new OutOnAgreeView(title);
        }else if(title.equals(WorkflowView.I_ON_SIGN)){
            return new OutOnSignView(title);
        }else if(title.equals(WorkflowView.O_ON_AGREE)){
            return new InOnAgreeView(title);
        }else if(title.equals(WorkflowView.O_ON_SIGN)){
            return new InOnSignView(title);
        }else{
            throw new IllegalArgumentException("No such view:" + title );
        }
    }
}
