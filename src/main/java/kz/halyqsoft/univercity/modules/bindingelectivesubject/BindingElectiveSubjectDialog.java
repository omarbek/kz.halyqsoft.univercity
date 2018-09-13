package kz.halyqsoft.univercity.modules.bindingelectivesubject;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.ELECTIVE_BINDED_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.PAIR_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.SEMESTER_SUBJECT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SEMESTER_DATA;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.SUBJECT;
import kz.halyqsoft.univercity.utils.CommonUtils;
import kz.halyqsoft.univercity.utils.WindowUtils;
import org.apache.commons.lang3.StringUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.dialog.Message;

/**
 * @author Omarbek
 * @created on 23.07.2018
 */
public class BindingElectiveSubjectDialog extends WindowUtils {

    private BindingElectiveSubjectEdit bindingElectiveSubjectEdit;
    private ELECTIVE_BINDED_SUBJECT electiveBindedSubject;
    private PAIR_SUBJECT pairSubject;
    private boolean isEdit;

    BindingElectiveSubjectDialog(ELECTIVE_BINDED_SUBJECT electiveBindedSubject,
                                 BindingElectiveSubjectEdit bindingElectiveSubjectEdit,
                                 PAIR_SUBJECT pairSubject, boolean isEdit) {
        this.bindingElectiveSubjectEdit = bindingElectiveSubjectEdit;
        this.electiveBindedSubject = electiveBindedSubject;
        this.pairSubject = pairSubject;
        this.isEdit = isEdit;
        init(400, 300);
    }

    @Override
    protected String createTitle() {
        return "BindingElectiveSubjectEdit";
    }

    @Override
    protected void refresh() throws Exception {
        bindingElectiveSubjectEdit.refresh(electiveBindedSubject);
    }

    @Override
    protected VerticalLayout getVerticalLayout() {
        VerticalLayout mainVL = new VerticalLayout();
        mainVL.setSpacing(true);
        try {
//            List<ID> chosenIDs = new ArrayList<>();
//            QueryModel<PAIR_SUBJECT> pairSubjectQM = new QueryModel<>(PAIR_SUBJECT.class);
//            pairSubjectQM.addWhere("electiveBindedSubject", ECriteria.EQUAL, electiveBindedSubject.getId());
//            List<PAIR_SUBJECT> pairSubjects = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
//                    lookup(pairSubjectQM);
//            for (PAIR_SUBJECT pairSubject : pairSubjects) {
//                ID subjectId = pairSubject.getSubject().getId();
//                if (!chosenIDs.contains(subjectId)) {
//                    chosenIDs.add(subjectId);
//                }
//            }

            ComboBox subjectCB = new ComboBox();
            subjectCB.setCaption(getUILocaleUtil().getEntityFieldLabel(PAIR_SUBJECT.class, "subject"));
            subjectCB.setNullSelectionAllowed(true);
            subjectCB.setTextInputAllowed(true);
            subjectCB.setFilteringMode(FilteringMode.CONTAINS);
            subjectCB.setPageLength(0);
            subjectCB.setWidth(300, Unit.PIXELS);
            QueryModel<SUBJECT> subjectQM = new QueryModel<>(SUBJECT.class);
            subjectQM.addWhereNotNull("subjectCycle");
            subjectQM.addWhereAnd("deleted", Boolean.FALSE);
            subjectQM.addWhereAnd("mandatory", Boolean.FALSE);
//            subjectQM.addWhereNotInAnd("id", chosenIDs);
            BeanItemContainer<SUBJECT> subjectBIC = new BeanItemContainer<>(SUBJECT.class,
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(subjectQM));
            subjectCB.setContainerDataSource(subjectBIC);
            mainVL.addComponent(subjectCB);
            mainVL.setComponentAlignment(subjectCB, Alignment.MIDDLE_CENTER);

            TextField pairTF = new TextField();
            pairTF.setCaption(getUILocaleUtil().getEntityFieldLabel(PAIR_SUBJECT.class, "pairNumber"));
            mainVL.addComponent(pairTF);
            mainVL.setComponentAlignment(pairTF, Alignment.MIDDLE_CENTER);

            TextField descriptionTF = new TextField();
            descriptionTF.setCaption(getUILocaleUtil().getEntityFieldLabel(PAIR_SUBJECT.class, "description"));
            mainVL.addComponent(descriptionTF);
            mainVL.setComponentAlignment(descriptionTF, Alignment.MIDDLE_CENTER);

            if (pairSubject != null) {
                subjectCB.setValue(pairSubject.getSubject());
                pairTF.setValue(pairSubject.getPairNumber().toString());
                descriptionTF.setValue(pairSubject.getDescription());
            }

            Button saveButton = CommonUtils.createSaveButton();
            saveButton.addClickListener(new Button.ClickListener() {
                @Override
                public void buttonClick(Button.ClickEvent event) {
                    try {
                        if (StringUtils.isNumeric(pairTF.getValue())) {
                            PAIR_SUBJECT modifiedPairSubject = new PAIR_SUBJECT();
                            modifiedPairSubject.setElectveBindedSubject(electiveBindedSubject);
                            modifiedPairSubject.setSubject((SUBJECT) subjectCB.getValue());
                            modifiedPairSubject.setPairNumber(Integer.parseInt(pairTF.getValue()));
                            modifiedPairSubject.setDescription(descriptionTF.getValue());
                            if (isEdit) {
                                modifiedPairSubject.setId(pairSubject.getId());
                                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(
                                        modifiedPairSubject);
                            } else {
                                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(
                                        modifiedPairSubject);
                                SEMESTER_SUBJECT semesterSubject = new SEMESTER_SUBJECT();
                                semesterSubject.setSubject(modifiedPairSubject.getSubject());
                                QueryModel<SEMESTER_DATA> semesterDataQM = new QueryModel<>(SEMESTER_DATA.class);
                                semesterDataQM.addWhere("year", ECriteria.EQUAL, electiveBindedSubject.getCatalogElectiveSubjects().getEntranceYear().getId());
                                semesterDataQM.addWhere("semesterPeriod", ECriteria.EQUAL, electiveBindedSubject.getSemester().getSemesterPeriod().getId());
                                SEMESTER_DATA semesterData = null;
                                try {
                                    semesterData = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(semesterDataQM);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (semesterData != null) {
                                    semesterSubject.setSemesterData(semesterData);
                                    try {
                                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(semesterSubject);
                                    } catch (Exception ignored) {
                                    }
                                }
                            }

                            close();
                            refresh();
                        } else {
                            Message.showError(getUILocaleUtil().getMessage("write.number"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();//TODO catch
                    }
                }
            });
            mainVL.addComponent(saveButton);
            mainVL.setComponentAlignment(saveButton, Alignment.MIDDLE_CENTER);
        } catch (Exception e) {
            e.printStackTrace();//TODO catch
        }
        return mainVL;
    }
}