package kz.halyqsoft.univercity.utils;

import com.vaadin.ui.Button;
import kz.halyqsoft.univercity.entity.beans.univercity.USER_DOCUMENT_FILE;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.file.FileBean;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.AbstractSecureWebUI;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.locale.UILocaleUtil;
import org.r3a.common.vaadin.widget.form.field.filelist.FileListFieldModel;

import java.util.List;

/**
 * @author Omarbek
 * @created on 15.03.2018
 */
public class CommonUtils {

    public static String getCurrentUserLogin() {
        return AbstractSecureWebUI.getInstance().getUsername();
    }

    public static String getCode(Integer count) {
        String code = String.valueOf(count);
        if (count < 10) {
            code = "000" + code;
        } else if (count < 100) {
            code = "00" + code;
        } else if (count < 1000) {
            code = "0" + code;
        }
        return code;
    }

    private static String getCodeBuilder(Integer count) {
        String code = String.valueOf(count);
        StringBuilder codeSB = new StringBuilder();
        if (count < 10) {
            codeSB.append("000");
        } else if (count < 100) {
            codeSB.append("00");
        } else if (count < 1000) {
            codeSB.append("0");
        }
        codeSB.append(code);
        return codeSB.toString();
    }

    public static void main(String[] args) {
        int a = 1;
        String code = getCode(a);
        String codeBuilder = getCodeBuilder(a);
        System.out.println("code: " + code);
        System.out.println("builder: " + codeBuilder);
    }

    public static void addFiles(QueryModel<USER_DOCUMENT_FILE> udfQM, FileListFieldModel medicalCheckupFLFM) {
        try {
            List udfList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(udfQM);
            if (!udfList.isEmpty()) {
                for (Object o : udfList) {
                    Object[] oo = (Object[]) o;
                    FileBean fe = new FileBean(USER_DOCUMENT_FILE.class);
                    fe.setId(ID.valueOf((Long) oo[0]));
                    fe.setFileName((String) oo[1]);
                    fe.setNewFile(false);
                    medicalCheckupFLFM.getFileList().add(fe);
                }
            }
        } catch (Exception ex) {
            ErrorUtils.LOG.error("Unable to load education document copies: ", ex);
        }
    }

    public static void deleteFiles(FileListFieldModel flfm) {
        for (FileBean fe : flfm.getDeleteList()) {
            try {
                USER_DOCUMENT_FILE udf = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(USER_DOCUMENT_FILE.class, fe.getId());
                udf.setDeleted(true);
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(udf);
            } catch (Exception ex) {
                ErrorUtils.LOG.error("Unable to delete repatriate doc copy: ", ex);
            }
        }
    }

    public static UILocaleUtil getUILocaleUtil() {
        return AbstractWebUI.getInstance().getUILocaleUtil();
    }

    public static void showSavedNotification() {
        AbstractWebUI.getInstance().showNotificationInfo(getUILocaleUtil().getMessage("info.record.saved"));
    }
}
