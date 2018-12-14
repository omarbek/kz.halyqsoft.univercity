package kz.halyqsoft.univercity.modules.teacherprofessionalcompositionload;

import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.LOAD_TO_TEACHER;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_EMPLOYEE;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.AbstractDialog;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.form.CommonFormWidget;
import org.r3a.common.vaadin.widget.form.FormModel;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Assylkhan
 * on 30.11.2018
 * @project kz.halyqsoft.univercity
 */
public class LoadToTeacherEditDialog extends AbstractDialog {

    private LOAD_TO_TEACHER loadToTeacher;
    private List<FormModel> formModels;
    private Map<String,Double> map;
    private LoadToTeacherView prevView;
    private static String fields[] = {
            "lcCount","prCount","lbCount","withTeacherCount",
            "ratingCount","examCount","controlCount","courseWorkCount",
            "diplomaCount","practiceCount","mek","protectDiplomaCount"
    };

    public LoadToTeacherEditDialog(LoadToTeacherView prevView ,LOAD_TO_TEACHER loadToTeacher){
        this.prevView = prevView;
        this.loadToTeacher = loadToTeacher;
        setWidth("90%");
        setHeight("90%");
        getContent().setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        VerticalLayout mainVL = new VerticalLayout();
        mainVL.setSpacing(true);
        mainVL.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        mainVL.setSizeUndefined();
        List<LOAD_TO_TEACHER>lttList = getLoadToTeachers();
        formModels = new ArrayList<>();
        map = new HashMap<String, Double>();
        for(String fieldName : fields){
            try{
                map.put(fieldName, 0.0);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        for(LOAD_TO_TEACHER ltt : lttList){
            for(String fieldName : fields){
                try{
                    Field field = ltt.getClass().getDeclaredField(fieldName);
                    field.setAccessible(true);
                    Double value = (Double) field.get(ltt);
                    map.replace(fieldName, map.get(fieldName) + value);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        GridLayout gridLayout = new GridLayout(4,3);
        gridLayout.setSpacing(true);
        gridLayout.setSizeUndefined();
        for(String  key : map.keySet()){
            Label row = new Label(getUILocaleUtil().getEntityFieldLabel(LOAD_TO_TEACHER.class,key) + ":  " + map.get(key));
            gridLayout.addComponent(row);
        }
        getContent().addComponent(gridLayout);
        getContent().setComponentAlignment(gridLayout,Alignment.MIDDLE_CENTER);
        GridLayout formsGL = new GridLayout(2 , lttList.size()%2==0 ? lttList.size()/2 : (lttList.size()+1)/2);
        for(LOAD_TO_TEACHER ltt : lttList){
            FormModel lttFM = new FormModel(LOAD_TO_TEACHER.class);
            lttFM.setRenderAsGrid(true);
            lttFM.setButtonsVisible(false);
            lttFM.getFieldModel("subject").setInEdit(false);
            lttFM.getFieldModel("curriculum").setInEdit(false);
            lttFM.getFieldModel("studyYear").setInEdit(false);
            lttFM.getFieldModel("stream").setInEdit(false);
            lttFM.getFieldModel("group").setInEdit(false);
            lttFM.getFieldModel("semester").setInEdit(false);
            lttFM.getFieldModel("studentNumber").setInEdit(false);
            formModels.add(lttFM);
            try{
                lttFM.loadEntity(ltt.getId());
                formsGL.addComponent(new CommonFormWidget(lttFM));
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        getContent().addComponent(formsGL);
        getContent().setComponentAlignment(formsGL,Alignment.MIDDLE_CENTER);
        Button saveBtn = new Button(getUILocaleUtil().getCaption("save"));
        saveBtn.setIcon(new ThemeResource("img/button/ok.png"),getUILocaleUtil().getCaption("save"));
        saveBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                Map<String, Double> tempMap = new HashMap<String, Double>();
                for(String fieldName : fields){
                    try{
                        tempMap.put(fieldName, 0.0);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                for(FormModel fm : formModels){
                    try{

                        for(String fieldName : fields){
                            if(fm.getFieldModel(fieldName).getValue()!=null) {
                                try{
                                    Double value = Double.valueOf((String)fm.getFieldModel(fieldName).getValue());
                                    tempMap.replace(fieldName, tempMap.get(fieldName) + value);
                                }catch (Exception ex){
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }

                for(String fieldName : fields){
                    if(!map.get(fieldName).equals(tempMap.get(fieldName))){
                        Message.showInfo(getUILocaleUtil().getCaption("invalid.values"));
                        return;
                    }
                }

                for(FormModel fm : formModels){
                    try{
                        if(fm.isModified()){
                            LOAD_TO_TEACHER ltt = (LOAD_TO_TEACHER) fm.getEntity();
                            for(String fieldName : fields){
                                if(fm.getFieldModel(fieldName).getValue()!=null) {
                                    try{
                                        Double value = Double.valueOf((String)fm.getFieldModel(fieldName).getValue());
                                        Field field = ltt.getClass().getDeclaredField(fieldName);
                                        field.setAccessible(true);
                                        field.set(ltt, value);
                                    }catch (Exception ex){
                                        ex.printStackTrace();
                                    }
                                }
                                V_EMPLOYEE teacher = (V_EMPLOYEE) fm.getFieldModel("teacher").getValue();
                                ltt.setTeacher(teacher);
                            }
                            CommonUtils.getQuery().merge(ltt);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                prevView.doFilter(prevView.getFilterPanel().getFilterBean());
                close();

            }
        });
        getContent().addComponent(mainVL);
        getContent().addComponent(saveBtn);
        AbstractWebUI.getInstance().addWindow(this);

    }

    public List<LOAD_TO_TEACHER> getLoadToTeachers(){
        QueryModel<LOAD_TO_TEACHER> loadToTeacherQM = new QueryModel<>(LOAD_TO_TEACHER.class);
        loadToTeacherQM.addWhere("semester" , ECriteria.EQUAL, loadToTeacher.getSemester().getId());
        loadToTeacherQM.addWhereAnd("curriculum" , ECriteria.EQUAL, loadToTeacher.getCurriculum().getId());
        loadToTeacherQM.addWhereAnd("subject" , ECriteria.EQUAL, loadToTeacher.getSubject().getId());
        loadToTeacherQM.addWhereAnd("studyYear" , ECriteria.EQUAL, loadToTeacher.getStudyYear().getId());
        loadToTeacherQM.addWhereAnd("studyYear" , ECriteria.EQUAL, loadToTeacher.getStudyYear().getId());
        if(loadToTeacher.getStream()!=null){
            loadToTeacherQM.addWhereAnd("stream" , ECriteria.EQUAL, loadToTeacher.getStream().getId());
        }else if(loadToTeacher.getGroup()!=null){
            loadToTeacherQM.addWhereAnd("group" , ECriteria.EQUAL, loadToTeacher.getGroup().getId());
        }
        try{
            return SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(loadToTeacherQM);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new ArrayList<LOAD_TO_TEACHER>();
    }

    @Override
    protected String createTitle() {
        return getUILocaleUtil().getEntityLabel(LOAD_TO_TEACHER.class);
    }
}
