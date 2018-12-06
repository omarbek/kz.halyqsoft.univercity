package kz.halyqsoft.univercity.modules.workflowforemp;

import kz.halyqsoft.univercity.modules.workflow.views.BaseView;
import kz.halyqsoft.univercity.modules.workflowforemp.views.DocumentTypesView;
import kz.halyqsoft.univercity.modules.workflowforemp.views.EmployeesView;
import kz.halyqsoft.univercity.modules.workflowforemp.views.JournalView;
import kz.halyqsoft.univercity.modules.workflowforemp.views.MainView;

public class ViewResolver {
    public BaseView getViewByTitle(String title) throws IllegalArgumentException{

        if(title.equals(WorflowViewForEmp.WORK_FLOW)){
            return new MainView(title);
        }else if(title.equals(WorflowViewForEmp.JOURNAL)){
            return new JournalView(title);
        }else if(title.equals(WorflowViewForEmp.EMPLOYEES)){
            return new EmployeesView(title);
        }else if(title.equals(WorflowViewForEmp.DOCUMENT_TYPES)){
            return new DocumentTypesView(title);
        }else{
            throw new IllegalArgumentException("No such view:" + title );
        }
    }
}