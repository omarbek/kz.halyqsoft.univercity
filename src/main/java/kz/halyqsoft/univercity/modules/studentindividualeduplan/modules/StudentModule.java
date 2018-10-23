package kz.halyqsoft.univercity.modules.studentindividualeduplan.modules;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinService;
import com.vaadin.ui.Button;
import com.vaadin.ui.themes.ValoTheme;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SPECIALITY;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_COORDINATOR;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_STUDENT;
import kz.halyqsoft.univercity.modules.regapplicants.TableForm;
import kz.halyqsoft.univercity.modules.regapplicants.TableFormRus;
import kz.halyqsoft.univercity.modules.studentindividualeduplan.StudentPlan;
import kz.halyqsoft.univercity.modules.studentindividualeduplan.dialogs.LanguageDialog;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.dialog.Message;

import javax.persistence.NoResultException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

public class StudentModule extends BaseModule{

    private static String replaced;
    private static String inLettersEdu;
    private static String moneyForEducation;
    private static String answerEdu;


    private static final int FATHER = 1;
    private static final int MOTHER = 2;
    private static final int ADDRESS_FACT = 2;

    private Button downloadBtn;
    public StudentModule(BaseModule baseModule, StudentPlan studentPlan) {
        super(V_STUDENT.class, baseModule, studentPlan);
        getMainGW().setMultiSelect(true);
        getMainGM().getQueryModel().addWhere("group" , ECriteria.EQUAL,baseModule.getMainGW().getSelectedEntity().getId());

        downloadBtn = new Button(CommonUtils.getUILocaleUtil().getCaption("download"));
        downloadBtn.setStyleName(ValoTheme.BUTTON_LARGE);
        downloadBtn.setIcon(FontAwesome.DOWNLOAD);
        downloadBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                if(getMainGW().getSelectedEntities().size() > 0){
                    List<STUDENT> studentList = new ArrayList<>();
                    for(Entity vStudent : getMainGW().getSelectedEntities()){
                        try{
                            STUDENT student = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean .class)
                                    .lookup(STUDENT.class , vStudent.getId());

                            studentList.add(student);
                        }catch (Exception e){
                            e.printStackTrace();
                            return;
                        }
                    }
                    LanguageDialog languageDialog = new LanguageDialog(studentList);
                }else{
                    Message.showError(CommonUtils.getUILocaleUtil().getMessage("choose.field"));
                }
            }
        });
        getButtonsPanel().addComponent(downloadBtn);
    }

    @Override
    public void handleEntityEvent(EntityEvent entityEvent) {

    }
}
