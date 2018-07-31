package kz.halyqsoft.univercity.utils;

import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_IMPORTANCE;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_SIGNER_STATUS;
import kz.halyqsoft.univercity.entity.beans.univercity.DOCUMENT_STATUS;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.AbstractSecureWebUI;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.widget.dialog.Message;

import java.math.BigInteger;

public class WorkflowCommonUtils {

    public static BigInteger getCurrentUserId() {
        return getCurrentUser().getId().getId();
    }

    public static USERS getCurrentUser(){
        if (AbstractWebUI.getInstance() instanceof AbstractSecureWebUI) {
            QueryModel<USERS> qm = new QueryModel<>(USERS.class);
            qm.addWhere("login" , ECriteria.EQUAL ,AbstractSecureWebUI.getInstance().getUsername());
            USERS u = null;
            try {
                u = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(qm);
                return u;
            }catch (Exception e)
            {
                Message.showError(e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }



    public static DOCUMENT_STATUS getDocumentStatusByName(String name){
        DOCUMENT_STATUS documentStatus = null;
        try{
            QueryModel<DOCUMENT_STATUS> documentStatusQM = new QueryModel<>(DOCUMENT_STATUS.class);
            documentStatusQM.addWhere("statusName" ,ECriteria.EQUAL , name);
            documentStatus = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(documentStatusQM);
        }catch (Exception e){
            e.printStackTrace();
        }
        return documentStatus;
    }

    public static DOCUMENT_SIGNER_STATUS getDocumentSignerStatusByName(String name){
        DOCUMENT_SIGNER_STATUS documentSignerStatus= null;
        try{
            QueryModel<DOCUMENT_SIGNER_STATUS> documentSignerStatusQM = new QueryModel<>(DOCUMENT_SIGNER_STATUS.class);
            documentSignerStatusQM.addWhere("statusName" ,ECriteria.EQUAL , name);
            documentSignerStatus = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(documentSignerStatusQM);
        }catch (Exception e){
            e.printStackTrace();
        }
        return documentSignerStatus;
    }

    public static DOCUMENT_IMPORTANCE getDocumentImportanceByValue(Integer value){
        DOCUMENT_IMPORTANCE documentImportance= null;
        try{
            QueryModel<DOCUMENT_IMPORTANCE> documentImportanceQM = new QueryModel<>(DOCUMENT_IMPORTANCE.class);
            documentImportanceQM.addWhere("importanceValue" ,ECriteria.EQUAL , value);
            documentImportance = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(documentImportanceQM);
        }catch (Exception e){
            e.printStackTrace();
        }
        return documentImportance;
    }

}
