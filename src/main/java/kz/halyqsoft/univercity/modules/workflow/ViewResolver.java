package kz.halyqsoft.univercity.modules.workflow;

import kz.halyqsoft.univercity.modules.workflow.views.*;

import static kz.halyqsoft.univercity.utils.CommonUtils.getUILocaleUtil;

public class ViewResolver {

    public static String MY_DOCUMENTS = getUILocaleUtil().getCaption("my_documents");
    public static String CREATE = getUILocaleUtil().getCaption("create");
    public static String INCOMING = getUILocaleUtil().getCaption("incoming");
    public static String OUTCOMING = getUILocaleUtil().getCaption("outcoming");
    public static String O_ON_AGREE = getUILocaleUtil().getCaption("on_agree");
    public static String O_ON_SIGN = getUILocaleUtil().getCaption("on_sign");
    public static String I_ON_AGREE = getUILocaleUtil().getCaption("on_agree")+" ";
    public static String I_ON_SIGN = getUILocaleUtil().getCaption("on_sign")+" ";

    public BaseView getViewByTitle(String title) throws IllegalArgumentException{



        if(title.equals(CREATE)){
            return new CreateView(title);
        }else if(title.equals(MY_DOCUMENTS)){
            return new MyDocumentsView(title);
        }else if(title.equals(I_ON_AGREE)){
            return new OutOnAgreeView(title);
        }else if(title.equals(I_ON_SIGN)){
            return new OutOnSignView(title);
        }else if(title.equals(O_ON_AGREE)){
            return new InOnAgreeView(title);
        }else if(title.equals(O_ON_SIGN)){
            return new InOnSignView(title);
        }else{
            throw new IllegalArgumentException("No such view:" + title );
        }
    }
}
