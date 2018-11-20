package kz.halyqsoft.univercity.modules.customreports;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_DATA;
import kz.halyqsoft.univercity.modules.userarrival.subview.dialogs.PrintDialog;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.apache.commons.lang3.StringUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Assylkhan
 * on 09.11.2018
 * @project kz.halyqsoft.univercity
 */
public class CustomReportsView extends AbstractTaskView{

    public CustomReportsView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        getContent().addComponent(getForm34HL());
    }

    public HorizontalLayout getForm34HL(){
        HorizontalLayout formHL = new HorizontalLayout();
        formHL.setResponsive(true);

        ComboBox semesterDataCB = new ComboBox(getUILocaleUtil().getEntityLabel(SEMESTER_DATA.class));
        semesterDataCB.setTextInputAllowed(true);
        semesterDataCB.setNullSelectionAllowed(false);
        semesterDataCB.setFilteringMode(FilteringMode.CONTAINS);
        semesterDataCB.setImmediate(true);
        QueryModel<SEMESTER_DATA> semesterDataQM = new QueryModel<>(SEMESTER_DATA.class);
        semesterDataQM.addOrderDesc("id");
        ArrayList<SEMESTER_DATA> semesterDataList = new ArrayList<>();
        try {
            semesterDataList.addAll(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
                    .lookup(semesterDataQM));
        }catch (Exception e){
            e.printStackTrace();
        }
        BeanItemContainer bic = new BeanItemContainer(SEMESTER_DATA.class ,semesterDataList);
        semesterDataCB.setContainerDataSource(bic);
        semesterDataCB.setValue(CommonUtils.getCurrentSemesterData());
        Button exportBtn = new Button("form 34");
        exportBtn.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {

                if (semesterDataCB.getValue()==null){
                    Message.showError(getUILocaleUtil().getCaption("chooseARecord"));
                    return;
                }

                List<String> headerList = new ArrayList<String>();
                headerList.add("Мамандық коды");
                headerList.add("Мамандық аты");
                headerList.add("Форма");
                headerList.add("Курс");
                headerList.add("Тіл");
                headerList.add("Тип");
                headerList.add("Барлық студенттер");
                headerList.add("Академиялық демалыстағы студенттер");
                headerList.add("Тапсыру керек студенттер");
                headerList.add("Тапсыруға кірген студенттер");
                headerList.add("Құрметті себеп");
                headerList.add("Құрметсіз себеп");
                headerList.add("Тапсырды");
                headerList.add("A A- тапсырғандар");
                headerList.add("A A- B B+ B- тапсырғандар");
                headerList.add("A A- B B+ B- C C+ C- D+ D тапсырғандар");
                headerList.add("C C+ C- D+ D тапсырғандар");
                headerList.add("Тапсырмағандар");
                headerList.add("Тапсырмағандар бір F");
                headerList.add("Тапсырмағандар бірден көп F");
                headerList.add("Сапалық көрсеткіш");
                headerList.add("Абсолют көрсеткіш");
                headerList.add("Келесі курсқа көшкендер");
                headerList.add("Келесі курсқа академиялық қарызбен көшкендер");
                headerList.add("Қайта курста қалғандар");
                headerList.add("Шегерілгендер");
                List<List<String>>rowList = new ArrayList<List<String>>();
                String sql = StringUtils.join("SELECT\n" +
                                "  speciality_code,\n" +
                                "  speciality_name,\n" +
                                "  form,\n" +
                                "  course,\n" +
                                "  yaz,\n" +
                                "  type,\n" +
                                "  all_students,\n" +
                                "  all_students_in_academic_holiday,\n" +
                                "  must_to_pass,\n" +
                                "  entered_to_pass,\n" +
                                "  respectful_reason,\n" +
                                "  not_respectful_reason,\n" +
                                "  passed,\n" +
                                "  passedAAminus,\n" +
                                "  passedAAminusBplusBBminus,\n" +
                                "  passedAAminusBplusBBminusCplusCCminusDplusD,\n" +
                                "  passedCplusCCminusDplusD,\n" +
                                "  notPassed,\n" +
                                "  CASE WHEN notPassed = 1\n" +
                                "    THEN notPassed\n" +
                                "  ELSE 0 END                                  notPassedOneF,\n" +
                                "  CASE WHEN notPassed <> 1\n" +
                                "    THEN notPassed\n" +
                                "  ELSE 0 END                                  notPassedTwoOrMore,\n" +
                                "  CASE WHEN passedAAminusBplusBBminus <> 0\n" +
                                "    THEN passed / passedAAminusBplusBBminus\n" +
                                "  ELSE 0 END                                  qualityPerformance,\n" +
                                "  CASE WHEN passedAAminusBplusBBminus <> 0 OR passedAAminusBplusBBminusCplusCCminusDplusD <> 0\n" +
                                "    THEN passed / (passedAAminusBplusBBminus + passedAAminusBplusBBminusCplusCCminusDplusD)\n" +
                                "  ELSE 0 END                                  absolutePerformance,\n" +
                                "  passedAAminusBplusBBminusCplusCCminusDplusD movedToNextCourse,\n" +
                                "  0                                           movedToNextCourseWithAcademicIndebtedness,\n" +
                                "  stayInSameCourse,\n" +
                                "  deducted\n" +
                                "FROM (\n" +
                                "       SELECT\n" +
                                "         vs.speciality_code,\n" +
                                "         substring(vs.speciality_name, 12) speciality_name,\n" +
                                "         vs.study_year_id AS               course,\n" +
                                "         form.column1     AS               form,\n" +
                                "         yaz.column1      AS               yaz,\n" +
                                "         type.column1     AS               type,\n" +
                                "\n" +
                                "         -- FIRST COLUMN\n" +
                                "         CASE\n" +
                                "         --итого\n" +
                                "         -- OVERALL\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'итого' AND yaz.column1 = ' '\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'в том числе грант' AND yaz.column1 = ' ' AND\n" +
                                "              vs.education_type_id = 2\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'в том числе платное' AND yaz.column1 = ' ' AND\n" +
                                "              vs.education_type_id <> 2\n" +
                                "           THEN count(vs.id)\n" +
                                "         --на базе спо\n" +
                                "         -- KAZ\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.language_id = 1 AND vs.diploma_type_id IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 1 AND vs.diploma_type_id IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 1 AND vs.diploma_type_id IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- RUS\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.language_id = 2 AND vs.diploma_type_id IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 2 AND vs.diploma_type_id IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 2 AND vs.diploma_type_id IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- FOREIGN\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         --на базе высшего\n" +
                                "         -- KAZ\n",
                        "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.language_id = 1 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 1 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 1 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- RUS\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.language_id = 2 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 2 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 2 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- FOREIGN\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "\n" +
                                "         END                               all_students,\n" +
                                "\n" +
                                "         -- SECOND COLUMN\n" +
                                "         CASE\n" +
                                "         --итого\n" +
                                "         -- OVERALL\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'итого' AND yaz.column1 = ' ' AND vs.student_status_id = 5\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'в том числе грант' AND yaz.column1 = ' ' AND\n" +
                                "              vs.education_type_id = 2 AND vs.student_status_id = 5\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'в том числе платное' AND yaz.column1 = ' ' AND\n" +
                                "              vs.education_type_id <> 2 AND vs.student_status_id = 5\n" +
                                "           THEN count(vs.id)\n" +
                                "         --на базе спо\n" +
                                "         -- KAZ\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.language_id = 1 AND vs.student_status_id = 5 AND vs.diploma_type_id IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 1 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 5\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 1 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 5\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- RUS\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.language_id = 2 AND vs.student_status_id = 5 AND vs.diploma_type_id IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 2 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 5\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 2 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 5\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- FOREIGN\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.language_id NOT IN (1, 2) AND vs.student_status_id = 5 AND vs.diploma_type_id IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 5\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 5\n" +
                                "           THEN count(vs.id)\n" +
                                "         --на базе высшего\n" +
                                "         -- KAZ\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.language_id = 1 AND vs.student_status_id = 5 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 1 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 5\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 1 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 5\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- RUS\n",
                        "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.language_id = 2 AND vs.student_status_id = 5 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 2 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 5\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 2 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 5\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- FOREIGN\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.language_id NOT IN (1, 2) AND vs.student_status_id = 5 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 5\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 5\n" +
                                "           THEN count(vs.id)\n" +
                                "\n" +
                                "         END                               all_students_in_academic_holiday,\n" +
                                "\n" +
                                "         -- THIRD COLUMN\n" +
                                "         CASE\n" +
                                "         --итого\n" +
                                "         -- OVERALL\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'итого' AND yaz.column1 = ' ' AND vs.student_status_id = 1\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'в том числе грант' AND yaz.column1 = ' ' AND\n" +
                                "              vs.education_type_id = 2 AND vs.student_status_id = 1\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'в том числе платное' AND yaz.column1 = ' ' AND\n" +
                                "              vs.education_type_id <> 2 AND vs.student_status_id = 1\n" +
                                "           THEN count(vs.id)\n" +
                                "         --на базе спо\n" +
                                "         -- KAZ\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.language_id = 1 AND vs.student_status_id = 1 AND vs.diploma_type_id IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 1 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 1 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- RUS\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.language_id = 2 AND vs.student_status_id = 1 AND vs.diploma_type_id IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 2 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 2 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- FOREIGN\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.language_id NOT IN (1, 2) AND vs.student_status_id = 1 AND vs.diploma_type_id IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1\n" +
                                "           THEN count(vs.id)\n" +
                                "         --на базе высшего\n" +
                                "         -- KAZ\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.language_id = 1 AND vs.student_status_id = 1 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 1 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 1 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- RUS\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.language_id = 2 AND vs.student_status_id = 1 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 2 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 2 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- FOREIGN\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.language_id NOT IN (1, 2) AND vs.student_status_id = 1 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1\n" +
                                "           THEN count(vs.id)\n" +
                                "\n" +
                                "         END                               must_to_pass,\n",
                        "\n" +
                                "         -- FOURTH COLUMN\n" +
                                "         CASE\n" +
                                "         --итого\n" +
                                "         -- OVERALL\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'итого' AND yaz.column1 = ' ' AND vs.student_status_id = 1\n" +
                                "              AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'в том числе грант' AND yaz.column1 = ' ' AND\n" +
                                "              vs.education_type_id = 2 AND vs.student_status_id = 1 AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'в том числе платное' AND yaz.column1 = ' ' AND\n" +
                                "              vs.education_type_id <> 2 AND vs.student_status_id = 1 AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         --на базе спо\n" +
                                "         -- KAZ\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.language_id = 1 AND vs.student_status_id = 1 AND vs.diploma_type_id IN (5, 6) AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 1 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 1 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- RUS\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.language_id = 2 AND vs.student_status_id = 1 AND vs.diploma_type_id IN (5, 6) AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 2 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 2 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- FOREIGN\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.language_id NOT IN (1, 2) AND vs.student_status_id = 1 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         --на базе высшего\n" +
                                "         -- KAZ\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.language_id = 1 AND vs.student_status_id = 1 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 1 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 1 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- RUS\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.language_id = 2 AND vs.student_status_id = 1 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 2 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 2 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- FOREIGN\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.language_id NOT IN (1, 2) AND vs.student_status_id = 1 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "\n" +
                                "         END                               entered_to_pass,\n",
                        "\n" +
                                "\n" +
                                "         -- FIFTH COLUMN\n" +
                                "         CASE\n" +
                                "         --итого\n" +
                                "         -- OVERALL\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'итого' AND yaz.column1 = ' ' AND vs.student_status_id = 1\n" +
                                "              AND ser.final ISNULL AND nae.respectful_reason = TRUE\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'в том числе грант' AND yaz.column1 = ' ' AND\n" +
                                "              vs.education_type_id = 2 AND vs.student_status_id = 1 AND ser.final ISNULL AND\n" +
                                "              nae.respectful_reason = TRUE\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'в том числе платное' AND yaz.column1 = ' ' AND\n" +
                                "              vs.education_type_id <> 2 AND vs.student_status_id = 1 AND ser.final ISNULL AND\n" +
                                "              nae.respectful_reason = TRUE\n" +
                                "           THEN count(vs.id)\n" +
                                "         --на базе спо\n" +
                                "         -- KAZ\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.language_id = 1 AND vs.student_status_id = 1 AND vs.diploma_type_id IN (5, 6)\n" +
                                "              AND ser.final ISNULL AND nae.respectful_reason = TRUE\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 1 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final ISNULL AND nae.respectful_reason = TRUE\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 1 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final ISNULL AND nae.respectful_reason = TRUE\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- RUS\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.language_id = 2 AND vs.student_status_id = 1 AND vs.diploma_type_id IN (5, 6)\n" +
                                "              AND ser.final ISNULL AND nae.respectful_reason = TRUE\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 2 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final ISNULL AND nae.respectful_reason = TRUE\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 2 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final ISNULL AND nae.respectful_reason = TRUE\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- FOREIGN\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.language_id NOT IN (1, 2) AND vs.student_status_id = 1 AND vs.diploma_type_id IN (5, 6)\n" +
                                "              AND ser.final ISNULL AND nae.respectful_reason = TRUE\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final ISNULL AND nae.respectful_reason = TRUE\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final ISNULL AND nae.respectful_reason = TRUE\n" +
                                "           THEN count(vs.id)\n" +
                                "         --на базе высшего\n" +
                                "         -- KAZ\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.language_id = 1 AND vs.student_status_id = 1 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "              AND ser.final ISNULL AND nae.respectful_reason = TRUE\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 1 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final ISNULL AND nae.respectful_reason = TRUE\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 1 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final ISNULL AND nae.respectful_reason = TRUE\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- RUS\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.language_id = 2 AND vs.student_status_id = 1 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "              AND ser.final ISNULL AND nae.respectful_reason = TRUE\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 2 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final ISNULL AND nae.respectful_reason = TRUE\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 2 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final ISNULL AND nae.respectful_reason = TRUE\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- FOREIGN\n",
                        "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.language_id NOT IN (1, 2) AND vs.student_status_id = 1 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "              AND ser.final ISNULL AND nae.respectful_reason = TRUE\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final ISNULL AND nae.respectful_reason = TRUE\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final ISNULL AND nae.respectful_reason = TRUE\n" +
                                "           THEN count(vs.id)\n" +
                                "\n" +
                                "         END                               respectful_reason,\n" +
                                "\n" +
                                "\n" +
                                "         -- SIXTH COLUMN\n" +
                                "         CASE\n" +
                                "         --итого\n" +
                                "         -- OVERALL\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'итого' AND yaz.column1 = ' ' AND vs.student_status_id = 1\n" +
                                "              AND ser.final ISNULL AND nae.respectful_reason = FALSE\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'в том числе грант' AND yaz.column1 = ' ' AND\n" +
                                "              vs.education_type_id = 2 AND vs.student_status_id = 1 AND ser.final ISNULL AND\n" +
                                "              nae.respectful_reason = FALSE\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'в том числе платное' AND yaz.column1 = ' ' AND\n" +
                                "              vs.education_type_id <> 2 AND vs.student_status_id = 1 AND ser.final ISNULL AND\n" +
                                "              nae.respectful_reason = FALSE\n" +
                                "           THEN count(vs.id)\n" +
                                "         --на базе спо\n" +
                                "         -- KAZ\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.language_id = 1 AND vs.student_status_id = 1 AND vs.diploma_type_id IN (5, 6)\n" +
                                "              AND ser.final ISNULL AND nae.respectful_reason = FALSE\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 1 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final ISNULL AND nae.respectful_reason = FALSE\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 1 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final ISNULL AND nae.respectful_reason = FALSE\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- RUS\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.language_id = 2 AND vs.student_status_id = 1 AND vs.diploma_type_id IN (5, 6)\n" +
                                "              AND ser.final ISNULL AND nae.respectful_reason = FALSE\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 2 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final ISNULL AND nae.respectful_reason = FALSE\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 2 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final ISNULL AND nae.respectful_reason = FALSE\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- FOREIGN\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.language_id NOT IN (1, 2) AND vs.student_status_id = 1 AND vs.diploma_type_id IN (5, 6)\n" +
                                "              AND ser.final ISNULL AND nae.respectful_reason = FALSE\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final ISNULL AND nae.respectful_reason = FALSE\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final ISNULL AND nae.respectful_reason = FALSE\n" +
                                "           THEN count(vs.id)\n" +
                                "         --на базе высшего\n" +
                                "         -- KAZ\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.language_id = 1 AND vs.student_status_id = 1 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "              AND ser.final ISNULL AND nae.respectful_reason = FALSE\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 1 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final ISNULL AND nae.respectful_reason = FALSE\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 1 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final ISNULL AND nae.respectful_reason = FALSE\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- RUS\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.language_id = 2 AND vs.student_status_id = 1 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "              AND ser.final ISNULL AND nae.respectful_reason = FALSE\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 2 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final ISNULL AND nae.respectful_reason = FALSE\n" +
                                "           THEN count(vs.id)\n",
                        "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 2 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final ISNULL AND nae.respectful_reason = FALSE\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- FOREIGN\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.language_id NOT IN (1, 2) AND vs.student_status_id = 1 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "              AND ser.final ISNULL AND nae.respectful_reason = FALSE\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final ISNULL AND nae.respectful_reason = FALSE\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final ISNULL AND nae.respectful_reason = FALSE\n" +
                                "           THEN count(vs.id)\n" +
                                "\n" +
                                "         END                               not_respectful_reason,\n" +
                                "\n" +
                                "         -- SEVENTH COLUMN\n" +
                                "         CASE\n" +
                                "         --итого\n" +
                                "         -- OVERALL\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'итого' AND yaz.column1 = ' ' AND vs.student_status_id = 1\n" +
                                "              AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'в том числе грант' AND yaz.column1 = ' ' AND\n" +
                                "              vs.education_type_id = 2 AND vs.student_status_id = 1 AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'в том числе платное' AND yaz.column1 = ' ' AND\n" +
                                "              vs.education_type_id <> 2 AND vs.student_status_id = 1 AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         --на базе спо\n" +
                                "         -- KAZ\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.language_id = 1 AND vs.student_status_id = 1 AND vs.diploma_type_id IN (5, 6)\n" +
                                "              AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 1 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 1 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- RUS\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.language_id = 2 AND vs.student_status_id = 1 AND vs.diploma_type_id IN (5, 6)\n" +
                                "              AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 2 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 2 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- FOREIGN\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.language_id NOT IN (1, 2) AND vs.student_status_id = 1 AND vs.diploma_type_id IN (5, 6)\n" +
                                "              AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         --на базе высшего\n" +
                                "         -- KAZ\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.language_id = 1 AND vs.student_status_id = 1 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "              AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 1 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 1 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- RUS\n",
                        "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.language_id = 2 AND vs.student_status_id = 1 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "              AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 2 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 2 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- FOREIGN\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.language_id NOT IN (1, 2) AND vs.student_status_id = 1 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "              AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final NOTNULL\n" +
                                "           THEN count(vs.id)\n" +
                                "\n" +
                                "         END                               passed,\n" +
                                "\n" +
                                "\n" +
                                "         -- EIGHTH COLUMN\n" +
                                "         CASE\n" +
                                "         --итого\n" +
                                "         -- OVERALL\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'итого' AND yaz.column1 = ' ' AND vs.student_status_id = 1\n" +
                                "              AND ser.final >= 90\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'в том числе грант' AND yaz.column1 = ' ' AND\n" +
                                "              vs.education_type_id = 2 AND vs.student_status_id = 1 AND ser.final >= 90\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'в том числе платное' AND yaz.column1 = ' ' AND\n" +
                                "              vs.education_type_id <> 2 AND vs.student_status_id = 1 AND ser.final >= 90\n" +
                                "           THEN count(vs.id)\n" +
                                "         --на базе спо\n" +
                                "         -- KAZ\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.language_id = 1 AND vs.student_status_id = 1 AND vs.diploma_type_id IN (5, 6)\n" +
                                "              AND ser.final >= 90\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 1 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 90\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 1 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 90\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- RUS\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.language_id = 2 AND vs.student_status_id = 1 AND vs.diploma_type_id IN (5, 6)\n" +
                                "              AND ser.final >= 90\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 2 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 90\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 2 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 90\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- FOREIGN\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.language_id NOT IN (1, 2) AND vs.student_status_id = 1 AND vs.diploma_type_id IN (5, 6)\n" +
                                "              AND ser.final >= 90\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 90\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 90\n" +
                                "           THEN count(vs.id)\n" +
                                "         --на базе высшего\n" +
                                "         -- KAZ\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.language_id = 1 AND vs.student_status_id = 1 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "              AND ser.final >= 90\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 1 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 90\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 1 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 90\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- RUS\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.language_id = 2 AND vs.student_status_id = 1 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "              AND ser.final >= 90\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 2 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 90\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 2 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 90\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- FOREIGN\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.language_id NOT IN (1, 2) AND vs.student_status_id = 1 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "              AND ser.final >= 90\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 90\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 90\n" +
                                "           THEN count(vs.id)\n" +
                                "\n" +
                                "         END                               passedAAminus,\n" +
                                "\n",
                        "         -- NINETH COLUMN\n" +
                                "         CASE\n" +
                                "         --итого\n" +
                                "         -- OVERALL\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'итого' AND yaz.column1 = ' ' AND vs.student_status_id = 1\n" +
                                "              AND ser.final >= 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'в том числе грант' AND yaz.column1 = ' ' AND\n" +
                                "              vs.education_type_id = 2 AND vs.student_status_id = 1 AND ser.final >= 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'в том числе платное' AND yaz.column1 = ' ' AND\n" +
                                "              vs.education_type_id <> 2 AND vs.student_status_id = 1 AND ser.final >= 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         --на базе спо\n" +
                                "         -- KAZ\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.language_id = 1 AND vs.student_status_id = 1 AND vs.diploma_type_id IN (5, 6)\n" +
                                "              AND ser.final >= 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 1 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 1 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- RUS\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.language_id = 2 AND vs.student_status_id = 1 AND vs.diploma_type_id IN (5, 6)\n" +
                                "              AND ser.final >= 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 2 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 2 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- FOREIGN\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.language_id NOT IN (1, 2) AND vs.student_status_id = 1 AND vs.diploma_type_id IN (5, 6)\n" +
                                "              AND ser.final >= 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         --на базе высшего\n" +
                                "         -- KAZ\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.language_id = 1 AND vs.student_status_id = 1 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "              AND ser.final >= 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 1 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 1 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- RUS\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.language_id = 2 AND vs.student_status_id = 1 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "              AND ser.final >= 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 2 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 2 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- FOREIGN\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.language_id NOT IN (1, 2) AND vs.student_status_id = 1 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "              AND ser.final >= 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 75\n" +
                                "           THEN count(vs.id)\n" +
                                "\n" +
                                "         END                               passedAAminusBplusBBminus,\n",

                        "\n" +
                                "         -- TENTH COLUMN\n" +
                                "         CASE\n" +
                                "         --итого\n" +
                                "         -- OVERALL\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'итого' AND yaz.column1 = ' ' AND vs.student_status_id = 1\n" +
                                "              AND ser.final >= 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'в том числе грант' AND yaz.column1 = ' ' AND\n" +
                                "              vs.education_type_id = 2 AND vs.student_status_id = 1 AND ser.final >= 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'в том числе платное' AND yaz.column1 = ' ' AND\n" +
                                "              vs.education_type_id <> 2 AND vs.student_status_id = 1 AND ser.final >= 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         --на базе спо\n" +
                                "         -- KAZ\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.language_id = 1 AND vs.student_status_id = 1 AND vs.diploma_type_id IN (5, 6)\n" +
                                "              AND ser.final >= 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 1 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 1 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- RUS\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.language_id = 2 AND vs.student_status_id = 1 AND vs.diploma_type_id IN (5, 6)\n" +
                                "              AND ser.final >= 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 2 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 2 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- FOREIGN\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.language_id NOT IN (1, 2) AND vs.student_status_id = 1 AND vs.diploma_type_id IN (5, 6)\n" +
                                "              AND ser.final >= 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         --на базе высшего\n" +
                                "         -- KAZ\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.language_id = 1 AND vs.student_status_id = 1 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "              AND ser.final >= 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 1 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 1 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- RUS\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.language_id = 2 AND vs.student_status_id = 1 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "              AND ser.final >= 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 2 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 2 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- FOREIGN\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.language_id NOT IN (1, 2) AND vs.student_status_id = 1 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "              AND ser.final >= 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 50\n" +
                                "           THEN count(vs.id)\n" +
                                "\n" +
                                "         END                               passedAAminusBplusBBminusCplusCCminusDplusD,\n",
                        "\n" +
                                "         -- ELEVENTH COLUMN\n" +
                                "         CASE\n" +
                                "         --итого\n" +
                                "         -- OVERALL\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'итого' AND yaz.column1 = ' ' AND vs.student_status_id = 1\n" +
                                "              AND ser.final >= 50 AND ser.final < 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'в том числе грант' AND yaz.column1 = ' ' AND\n" +
                                "              vs.education_type_id = 2 AND vs.student_status_id = 1 AND ser.final >= 50 AND ser.final < 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'в том числе платное' AND yaz.column1 = ' ' AND\n" +
                                "              vs.education_type_id <> 2 AND vs.student_status_id = 1 AND ser.final >= 50 AND ser.final < 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         --на базе спо\n" +
                                "         -- KAZ\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.language_id = 1 AND vs.student_status_id = 1 AND vs.diploma_type_id IN (5, 6)\n" +
                                "              AND ser.final >= 50 AND ser.final < 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 1 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 50 AND ser.final < 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 1 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 50 AND ser.final < 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- RUS\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.language_id = 2 AND vs.student_status_id = 1 AND vs.diploma_type_id IN (5, 6)\n" +
                                "              AND ser.final >= 50 AND ser.final < 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 2 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 50 AND ser.final < 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 2 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 50 AND ser.final < 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- FOREIGN\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.language_id NOT IN (1, 2) AND vs.student_status_id = 1 AND vs.diploma_type_id IN (5, 6)\n" +
                                "              AND ser.final >= 50 AND ser.final < 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 50 AND ser.final < 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 50 AND ser.final < 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         --на базе высшего\n" +
                                "         -- KAZ\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.language_id = 1 AND vs.student_status_id = 1 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "              AND ser.final >= 50 AND ser.final < 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 1 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 50 AND ser.final < 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 1 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 50 AND ser.final < 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- RUS\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.language_id = 2 AND vs.student_status_id = 1 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "              AND ser.final >= 50 AND ser.final < 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 2 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 50 AND ser.final < 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 2 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 50 AND ser.final < 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- FOREIGN\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.language_id NOT IN (1, 2) AND vs.student_status_id = 1 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "              AND ser.final >= 50 AND ser.final < 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 50 AND ser.final < 75\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final >= 50 AND ser.final < 75\n" +
                                "           THEN count(vs.id)\n" +
                                "\n" +
                                "         END                               passedCplusCCminusDplusD,\n",
                        "\n" +
                                "         -- TWELFTH COLUMN\n" +
                                "         CASE\n" +
                                "         --итого\n" +
                                "         -- OVERALL\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'итого' AND yaz.column1 = ' ' AND vs.student_status_id = 1\n" +
                                "              AND ser.final < 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'в том числе грант' AND yaz.column1 = ' ' AND\n" +
                                "              vs.education_type_id = 2 AND vs.student_status_id = 1 AND ser.final < 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'в том числе платное' AND yaz.column1 = ' ' AND\n" +
                                "              vs.education_type_id <> 2 AND vs.student_status_id = 1 AND ser.final < 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         --на базе спо\n" +
                                "         -- KAZ\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.language_id = 1 AND vs.student_status_id = 1 AND vs.diploma_type_id IN (5, 6)\n" +
                                "              AND ser.final < 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 1 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final < 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 1 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final < 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- RUS\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.language_id = 2 AND vs.student_status_id = 1 AND vs.diploma_type_id IN (5, 6)\n" +
                                "              AND ser.final < 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 2 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final < 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 2 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final < 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- FOREIGN\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.language_id NOT IN (1, 2) AND vs.student_status_id = 1 AND vs.diploma_type_id IN (5, 6)\n" +
                                "              AND ser.final < 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final < 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final < 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         --на базе высшего\n" +
                                "         -- KAZ\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.language_id = 1 AND vs.student_status_id = 1 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "              AND ser.final < 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 1 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final < 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 1 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final < 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- RUS\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.language_id = 2 AND vs.student_status_id = 1 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "              AND ser.final < 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 2 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final < 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 2 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final < 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- FOREIGN\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.language_id NOT IN (1, 2) AND vs.student_status_id = 1 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "              AND ser.final < 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final < 50\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 1 AND ser.final < 50\n" +
                                "           THEN count(vs.id)\n" +
                                "\n" +
                                "         END                               notPassed,\n",
                        "\n" +
                                "         -- THIRTEENTH COLUMN\n" +
                                "         CASE\n" +
                                "         --итого\n" +
                                "         -- OVERALL\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'итого' AND yaz.column1 = ' ' AND vs.student_status_id = 11\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'в том числе грант' AND yaz.column1 = ' ' AND\n" +
                                "              vs.education_type_id = 2 AND vs.student_status_id = 11\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'в том числе платное' AND yaz.column1 = ' ' AND\n" +
                                "              vs.education_type_id <> 2 AND vs.student_status_id = 11\n" +
                                "           THEN count(vs.id)\n" +
                                "         --на базе спо\n" +
                                "         -- KAZ\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.language_id = 1 AND vs.student_status_id = 11 AND vs.diploma_type_id IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 1 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 11\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 1 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 11\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- RUS\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.language_id = 2 AND vs.student_status_id = 11 AND vs.diploma_type_id IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 2 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 11\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 2 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 11\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- FOREIGN\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.language_id NOT IN (1, 2) AND vs.student_status_id = 11 AND vs.diploma_type_id IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 11\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 11\n" +
                                "           THEN count(vs.id)\n" +
                                "         --на базе высшего\n" +
                                "         -- KAZ\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.language_id = 1 AND vs.student_status_id = 11 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 1 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 11\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 1 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 11\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- RUS\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.language_id = 2 AND vs.student_status_id = 11 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 2 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 11\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 2 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 11\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- FOREIGN\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.language_id NOT IN (1, 2) AND vs.student_status_id = 11 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "\n",
                        "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 11\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 11\n" +
                                "           THEN count(vs.id)\n" +
                                "\n" +
                                "         END                               stayInSameCourse,\n" +
                                "\n" +
                                "         -- THIRTEENTH COLUMN\n" +
                                "         CASE\n" +
                                "         --итого\n" +
                                "         -- OVERALL\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'итого' AND yaz.column1 = ' ' AND vs.student_status_id = 3\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'в том числе грант' AND yaz.column1 = ' ' AND\n" +
                                "              vs.education_type_id = 2 AND vs.student_status_id = 3\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'итого' AND type.column1 = 'в том числе платное' AND yaz.column1 = ' ' AND\n" +
                                "              vs.education_type_id <> 2 AND vs.student_status_id = 3\n" +
                                "           THEN count(vs.id)\n" +
                                "         --на базе спо\n" +
                                "         -- KAZ\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.language_id = 1 AND vs.student_status_id = 3 AND vs.diploma_type_id IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 1 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 3\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 1 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 3\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- RUS\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.language_id = 2 AND vs.student_status_id = 3 AND vs.diploma_type_id IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 2 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 3\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 2 AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 3\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- FOREIGN\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'всего' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.language_id NOT IN (1, 2) AND vs.student_status_id = 3 AND vs.diploma_type_id IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 3\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе спо' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id IN (5, 6) AND\n" +
                                "              vs.student_status_id = 3\n" +
                                "           THEN count(vs.id)\n" +
                                "         --на базе высшего\n" +
                                "         -- KAZ\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.language_id = 1 AND vs.student_status_id = 3 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 1 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 3\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'каз'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 1 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 3\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- RUS\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.language_id = 2 AND vs.student_status_id = 3 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id = 2 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 3\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'рус'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id = 2 AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 3\n" +
                                "           THEN count(vs.id)\n" +
                                "         -- FOREIGN\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'всего' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.language_id NOT IN (1, 2) AND vs.student_status_id = 3 AND vs.diploma_type_id NOT IN (5, 6)\n" +
                                "\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе грант' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id = 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 3\n" +
                                "           THEN count(vs.id)\n" +
                                "         WHEN form.column1 = 'на базе высшего' AND type.column1 = 'в том числе платное' AND yaz.column1 = 'иност'\n" +
                                "              AND vs.education_type_id <> 2 AND vs.language_id NOT IN (1, 2) AND vs.diploma_type_id NOT IN (5, 6) AND\n" +
                                "              vs.student_status_id = 3\n" +
                                "           THEN count(vs.id)\n" +
                                "\n" +
                                "         END                               deducted\n" +
                                "       FROM v_student vs\n" +
                                "         CROSS JOIN (VALUES ('итого'), ('на базе спо'), ('на базе высшего')) AS form\n" +
                                "         CROSS JOIN (VALUES (' '), ('каз'), ('рус'), ('иност')) AS yaz\n" +
                                "         CROSS JOIN (VALUES ('итого'), ('в том числе грант'), ('в том числе платное'), ('всего')) AS type\n" +
                                "         INNER JOIN student_education se ON vs.id = se.student_id\n" +
                                "         LEFT JOIN student_edu_rate ser ON ser.student_id = se.id\n" +
                                "         LEFT JOIN semester sem ON sem.id = ser.semester_id\n" +
                                "         LEFT JOIN non_admission_exam nae ON nae.student_education_id = se.id\n" +
                                "       WHERE vs.groups_id NOTNULL\n" +
                                "--              AND ser.subject_id = 1 \n" +
                                " AND ser.semester_id = "+ ((SEMESTER_DATA)semesterDataCB.getValue()).getId().getId().longValue() +"\n" +
                                "       GROUP BY vs.speciality_code, vs.speciality_name, form.column1, yaz.column1, type.column1, vs.study_year_id,\n" +
                                "         vs.education_type_id, vs.language_id, vs.diploma_type_id, vs.student_status_id, ser.final,\n" +
                                "         nae.respectful_reason\n" +
                                "       ORDER BY speciality_name, form.column1, yaz.column1 ASC, type.column1 DESC) AS form34;");


                try{
                    Map<Integer, Object> map = new HashMap<Integer,Object>();
                    List list = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql , map);
                    for (Object o : list){
                        Object oo[] = (Object[]) o;
                        ArrayList<String> valueList = new ArrayList<String>();
                        for(int i = 0 ; i < oo.length; i++){
                            valueList.add( oo[i] != null ? oo[i].toString() : "");
                        }
                        rowList.add(valueList);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                PrintDialog printDialog = new PrintDialog(headerList, rowList,"My Title", "document");
                printDialog.getPdfBtn().setVisible(false);

            }
        });
        formHL.setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
        formHL.addComponent(semesterDataCB);
        formHL.addComponent(exportBtn);
        return formHL;
    }
}
