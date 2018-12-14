package kz.halyqsoft.univercity.modules.curriculum.working.schedule;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.*;
import com.vaadin.ui.Grid.HeaderCell;
import com.vaadin.ui.Grid.HeaderRow;
import kz.halyqsoft.univercity.entity.beans.univercity.CURRICULUM;
import kz.halyqsoft.univercity.entity.beans.univercity.CURRICULUM_SCHEDULE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.CURRICULUM_SCHEDULE_SYMBOL;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.MONTH;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDY_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.WEEK;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VCurriculumSchedule;
import kz.halyqsoft.univercity.modules.curriculum.working.main.AbstractCurriculumPanel;
import kz.halyqsoft.univercity.modules.curriculum.working.main.CurriculumView;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.widget.dialog.Message;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Omarbek Dinassil
 * @created Mar 1, 2016 1:30:03 PM
 */
@SuppressWarnings({"serial"})
public class SchedulePanel extends AbstractCurriculumPanel {

    static Button regButton;
    private VerticalLayout registerVL;
    private boolean removeAll = false;
    private Grid grid;
    private Button editButton;

    private List<WEEK> weekList;
    private Map<String, CURRICULUM_SCHEDULE_SYMBOL> symbolMap = new HashMap<String, CURRICULUM_SCHEDULE_SYMBOL>();

    public SchedulePanel(CurriculumView parentView) {
        super(parentView);
    }


    @Override
    public void initPanel() throws Exception {

        editButton = new Button("Edit");
        editButton.setCaption(getUILocaleUtil().getCaption("edit"));
        editButton.setWidth(120, Unit.PIXELS);
        editButton.setIcon(new ThemeResource("img/button/edit.png"));
        getContent().addComponent(editButton);

        editButton.addClickListener(
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {

                        try {
                            if (grid.getSelectedRow() != null) {
                                STUDY_YEAR study_year = ((ScheduleBean) grid.getSelectedRow()).getStudyYear();
                                QueryModel<CURRICULUM_SCHEDULE> scheduleQueryModel = new QueryModel<>(CURRICULUM_SCHEDULE.class);
                                scheduleQueryModel.addWhere("studyYear", ECriteria.EQUAL, study_year.getId());
                                scheduleQueryModel.addOrder("id");
                                List<CURRICULUM_SCHEDULE> scheduleList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(scheduleQueryModel);
                                SchedulePanelEdit schedulePanelEdit = new SchedulePanelEdit((scheduleList), SchedulePanel.this);

                            } else {
                                Message.showError("Choose one row");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
        );


        QueryModel<WEEK> qmWeek = new QueryModel<WEEK>(WEEK.class);
        qmWeek.addOrder("id");
        weekList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qmWeek);

        QueryModel<CURRICULUM_SCHEDULE_SYMBOL> qmSymbol = new QueryModel<CURRICULUM_SCHEDULE_SYMBOL>(CURRICULUM_SCHEDULE_SYMBOL.class);
        List<CURRICULUM_SCHEDULE_SYMBOL> symbolList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qmSymbol);
        for (CURRICULUM_SCHEDULE_SYMBOL css : symbolList) {
            symbolMap.put(css.getSymbol(), css);
        }

        grid = new Grid();
        grid.setSizeFull();

        grid.addStyleName("curriculum-schedule");
        grid.addColumn("studyYear").setHeaderCaption(getUILocaleUtil().getEntityLabel(STUDY_YEAR.class)).
                setHidable(false);
        for (int i = 0; i < 52; i++) {
            addColumns(i + 1);
        }
        grid.setHeightMode(HeightMode.ROW);
        grid.setHeightByRows(5);
        grid.setFrozenColumnCount(1);

        HeaderRow hr = grid.prependHeaderRow();

        HeaderCell hc = hr.join(hr.getCell("week01Symbol"), hr.getCell("week02Symbol"), hr.getCell("week03Symbol"), hr.getCell("week04Symbol"));
        hc.setText(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(MONTH.class, ID.valueOf(9)).getMonthNameRU());

        hc = hr.join(hr.getCell("week06Symbol"), hr.getCell("week07Symbol"), hr.getCell("week08Symbol"), hr.getCell("week09Symbol"));
        hc.setText(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(MONTH.class, ID.valueOf(10)).getMonthNameRU());

        hc = hr.join(hr.getCell("week11Symbol"), hr.getCell("week12Symbol"), hr.getCell("week13Symbol"));
        hc.setText(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(MONTH.class, ID.valueOf(11)).getMonthNameRU());

        hc = hr.join(hr.getCell("week15Symbol"), hr.getCell("week16Symbol"), hr.getCell("week17Symbol"));
        hc.setText(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(MONTH.class, ID.valueOf(12)).getMonthNameRU());

        hc = hr.join(hr.getCell("week19Symbol"), hr.getCell("week20Symbol"), hr.getCell("week21Symbol"), hr.getCell("week22Symbol"));
        hc.setText(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(MONTH.class, ID.valueOf(1)).getMonthNameRU());

        hc = hr.join(hr.getCell("week24Symbol"), hr.getCell("week25Symbol"), hr.getCell("week26Symbol"));
        hc.setText(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(MONTH.class, ID.valueOf(2)).getMonthNameRU());

        hc = hr.join(hr.getCell("week28Symbol"), hr.getCell("week29Symbol"), hr.getCell("week30Symbol"));
        hc.setText(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(MONTH.class, ID.valueOf(3)).getMonthNameRU());

        hc = hr.join(hr.getCell("week32Symbol"), hr.getCell("week33Symbol"), hr.getCell("week34Symbol"), hr.getCell("week35Symbol"));
        hc.setText(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(MONTH.class, ID.valueOf(4)).getMonthNameRU());

        hc = hr.join(hr.getCell("week36Symbol"), hr.getCell("week37Symbol"), hr.getCell("week38Symbol"), hr.getCell("week39Symbol"));
        hc.setText(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(MONTH.class, ID.valueOf(5)).getMonthNameRU());

        hc = hr.join(hr.getCell("week41Symbol"), hr.getCell("week42Symbol"), hr.getCell("week43Symbol"));
        hc.setText(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(MONTH.class, ID.valueOf(6)).getMonthNameRU());

        hc = hr.join(hr.getCell("week45Symbol"), hr.getCell("week46Symbol"), hr.getCell("week47Symbol"), hr.getCell("week48Symbol"));
        hc.setText(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(MONTH.class, ID.valueOf(7)).getMonthNameRU());

        hc = hr.join(hr.getCell("week49Symbol"), hr.getCell("week50Symbol"), hr.getCell("week51Symbol"), hr.getCell("week52Symbol"));
        hc.setText(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(MONTH.class, ID.valueOf(8)).getMonthNameRU());

        getContent().addComponent(grid);

        QueryModel<CURRICULUM_SCHEDULE_SYMBOL> qmCSS = new QueryModel<CURRICULUM_SCHEDULE_SYMBOL>(CURRICULUM_SCHEDULE_SYMBOL.class);
        qmCSS.addOrder("id");
        List<CURRICULUM_SCHEDULE_SYMBOL> cssList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qmCSS);

        HorizontalLayout hl = new HorizontalLayout();
        hl.setCaption(getUILocaleUtil().getCaption("designations"));
        hl.setSpacing(true);
        for (int i = 0; i < 4; i++) {
            Label l = new Label();
            l.setWidthUndefined();
            CURRICULUM_SCHEDULE_SYMBOL css = cssList.get(i);
            l.setValue(css.getSymbol() + " - " + css.getDescr());
            hl.addComponent(l);
        }
        getContent().addComponent(hl);

        hl = new HorizontalLayout();
        hl.setSpacing(true);
        for (int i = 4; i < 9; i++) {
            Label l = new Label();
            l.setWidthUndefined();
            CURRICULUM_SCHEDULE_SYMBOL css = cssList.get(i);
            l.setValue(css.getSymbol() + " - " + css.getDescr());
            hl.addComponent(l);
        }
        getContent().addComponent(hl);

        refresh();

        super.initPanel();
    }

    private void addColumns(int i) {
        if (i < 10) {
            grid.addColumn("week0" + i + "Symbol").setHeaderCaption(i + "").setHidable(false).setWidth(50);
        } else {
            grid.addColumn("week" + i + "Symbol").setHeaderCaption(i + "").setHidable(false).setWidth(50);
        }


    }

    @Override
    public void refresh() throws Exception {
        QueryModel<STUDY_YEAR> qmStudyYear = new QueryModel<STUDY_YEAR>(STUDY_YEAR.class);
        qmStudyYear.addWhere("studyYear", ECriteria.LESS_EQUAL, 4);
        qmStudyYear.addOrder("studyYear");
        List<STUDY_YEAR> syList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qmStudyYear);

        long curriculumId = (getCurriculum() != null && getCurriculum().getId() != null) ? getCurriculum().getId().getId().longValue() : -1;

        String sql = "select a.ID, a.STUDY_YEAR_ID, b.WEEK_CODE, c.SYMBOL from CURRICULUM_SCHEDULE a inner join WEEK b on a.WEEK_ID = b.ID inner join CURRICULUM_SCHEDULE_SYMBOL c on a.SYMBOL_ID = c.ID where a.CURRICULUM_ID = ?1";
        Map<Integer, Object> params = new HashMap<Integer, Object>(1);
        params.put(1, curriculumId);

        List tempList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupItemsList(sql, params);

        List<ScheduleBean> sbList = new ArrayList<ScheduleBean>(4);
        for (STUDY_YEAR sy : syList) {
            ScheduleBean sb = new ScheduleBean();
            sb.setStudyYear(sy);
            for (Object o : tempList) {
                Object[] oo = (Object[]) o;
                long syId = (long) oo[1];
                if (syId == sy.getId().getId().longValue()) {
                    VCurriculumSchedule vcs = new VCurriculumSchedule();
                    vcs.setId(ID.valueOf((long) oo[0]));
                    vcs.setWeekCode(((BigDecimal) oo[2]).intValue());
                    vcs.setSymbolCode((String) oo[3]);
                    sb.add(vcs);
                }
            }

            sbList.add(sb);
        }

        BeanItemContainer<ScheduleBean> bic = new BeanItemContainer<ScheduleBean>(ScheduleBean.class, sbList);
        grid.setContainerDataSource(bic);
    }

    public int getRecordCount() {
        return grid.getContainerDataSource().size();
    }

    public void generateSchedule() throws Exception {
        if (getParentView().isBachelorCurriculum()) {
            generateBachelorSchedule(getCurriculum());
        } else {
            generateMasterSchedule(getCurriculum());
        }
    }

    private void generateBachelorSchedule(CURRICULUM curriculum) throws Exception {
        QueryModel<CURRICULUM_SCHEDULE> qm = new QueryModel<CURRICULUM_SCHEDULE>(CURRICULUM_SCHEDULE.class);
        qm.addWhere("curriculum", ECriteria.EQUAL, curriculum.getId());
        List<CURRICULUM_SCHEDULE> list = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qm);
        if (list.size() < 196) {
            if (!list.isEmpty()) {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(list);
            }

            List<CURRICULUM_SCHEDULE> newList = new ArrayList<CURRICULUM_SCHEDULE>();

            QueryModel<MONTH> qmMonth = new QueryModel<MONTH>(MONTH.class);
            List<MONTH> monthList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qmMonth);
            Map<ID, MONTH> monthMap = new HashMap<ID, MONTH>();
            for (MONTH m : monthList) {
                monthMap.put(m.getId(), m);
            }

            QueryModel<CURRICULUM_SCHEDULE_SYMBOL> qmSymbol = new QueryModel<>(CURRICULUM_SCHEDULE_SYMBOL.class);
            List<CURRICULUM_SCHEDULE_SYMBOL> symbolList = SessionFacadeFactory.getSessionFacade(
                    CommonEntityFacadeBean.class).lookup(qmSymbol);
            Map<ID, CURRICULUM_SCHEDULE_SYMBOL> symbolMap = new HashMap<>();
            for (CURRICULUM_SCHEDULE_SYMBOL css : symbolList) {
                symbolMap.put(css.getId(), css);
            }

            QueryModel<STUDY_YEAR> qmStudyYear = new QueryModel<STUDY_YEAR>(STUDY_YEAR.class);
            qmStudyYear.addWhere("studyYear", ECriteria.LESS_EQUAL, 4);
            qmStudyYear.addOrder("studyYear");
            List<STUDY_YEAR> studyYearList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookup(qmStudyYear);

            QueryModel<WEEK> qmWeek = new QueryModel<>(WEEK.class);
            qmWeek.addOrder("id");
            List<WEEK> weekList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qmWeek);

            for (STUDY_YEAR sy : studyYearList) {
                for (WEEK w : weekList) {
                    if (sy.getStudyYear() <= 3) {
                        CURRICULUM_SCHEDULE cs = new CURRICULUM_SCHEDULE();
                        cs.setCurriculum(curriculum);
                        cs.setStudyYear(sy);
                        cs.setWeek(w);
                        if (w.getWeekCode() <= 15) {
                            cs.setSymbol(symbolMap.get(ID.valueOf(1)));
                            if (w.getWeekCode() <= 4) {
                                cs.setMonth(monthMap.get(ID.valueOf(9)));
                            } else if (w.getWeekCode() >= 6 && w.getWeekCode() <= 9) {
                                cs.setMonth(monthMap.get(ID.valueOf(10)));
                            } else if (w.getWeekCode() >= 11 && w.getWeekCode() <= 13) {
                                cs.setMonth(monthMap.get(ID.valueOf(11)));
                            } else if (w.getWeekCode() == 15) {
                                cs.setMonth(monthMap.get(ID.valueOf(12)));
                            }
                        } else if (w.getWeekCode() <= 17) {
                            cs.setSymbol(symbolMap.get(ID.valueOf(2)));
                            cs.setMonth(monthMap.get(ID.valueOf(12)));
                        } else if (w.getWeekCode() <= 19) {
                            cs.setSymbol(symbolMap.get(ID.valueOf(8)));
                            if (w.getWeekCode() == 19) {
                                cs.setMonth(monthMap.get(ID.valueOf(1)));
                            }
                        } else if (w.getWeekCode() <= 34) {
                            cs.setSymbol(symbolMap.get(ID.valueOf(1)));
                            if (w.getWeekCode() <= 22) {
                                cs.setMonth(monthMap.get(ID.valueOf(1)));
                            } else if (w.getWeekCode() >= 24 && w.getWeekCode() <= 26) {
                                cs.setMonth(monthMap.get(ID.valueOf(2)));
                            } else if (w.getWeekCode() >= 28 && w.getWeekCode() <= 30) {
                                cs.setMonth(monthMap.get(ID.valueOf(3)));
                            } else if (w.getWeekCode() >= 32) {
                                cs.setMonth(monthMap.get(ID.valueOf(4)));
                            }
                        } else if (w.getWeekCode() <= 36) {
                            cs.setSymbol(symbolMap.get(ID.valueOf(2)));
                            if (w.getWeekCode() == 35) {
                                cs.setMonth(monthMap.get(ID.valueOf(4)));
                            } else {
                                cs.setMonth(monthMap.get(ID.valueOf(5)));
                            }
                        } else {
                            if (w.getWeekCode() <= 38) {
                                cs.setMonth(monthMap.get(ID.valueOf(5)));
                                if (sy.getStudyYear() == 1) {
                                    cs.setSymbol(symbolMap.get(ID.valueOf(3)));
                                } else {
                                    cs.setSymbol(symbolMap.get(ID.valueOf(4)));
                                }
                            } else if (w.getWeekCode() == 39) {
                                cs.setMonth(monthMap.get(ID.valueOf(5)));
                                if (sy.getStudyYear() == 1) {
                                    cs.setSymbol(symbolMap.get(ID.valueOf(8)));
                                } else if (sy.getStudyYear() == 2) {
                                    cs.setSymbol(symbolMap.get(ID.valueOf(5)));
                                } else {
                                    cs.setSymbol(symbolMap.get(ID.valueOf(4)));
                                }
                            } else if (w.getWeekCode() == 40) {
                                if (sy.getStudyYear() == 3) {
                                    cs.setSymbol(symbolMap.get(ID.valueOf(4)));
                                } else {
                                    cs.setSymbol(symbolMap.get(ID.valueOf(8)));
                                }
                            } else {
                                cs.setSymbol(symbolMap.get(ID.valueOf(8)));
                                if (w.getWeekCode() <= 43) {
                                    cs.setMonth(monthMap.get(ID.valueOf(6)));
                                } else if (w.getWeekCode() >= 45 && w.getWeekCode() <= 48) {
                                    cs.setMonth(monthMap.get(ID.valueOf(7)));
                                } else if (w.getWeekCode() >= 49) {
                                    cs.setMonth(monthMap.get(ID.valueOf(8)));
                                }
                            }
                        }

                        newList.add(cs);
                    } else {
                        if (w.getWeekCode() <= 40) {
                            CURRICULUM_SCHEDULE cs = new CURRICULUM_SCHEDULE();
                            cs.setCurriculum(curriculum);
                            cs.setStudyYear(sy);
                            cs.setWeek(w);
                            if (w.getWeekCode() <= 15) {
                                cs.setSymbol(symbolMap.get(ID.valueOf(1)));
                                if (w.getWeekCode() <= 4) {
                                    cs.setMonth(monthMap.get(ID.valueOf(9)));
                                } else if (w.getWeekCode() >= 6 && w.getWeekCode() <= 9) {
                                    cs.setMonth(monthMap.get(ID.valueOf(10)));
                                } else if (w.getWeekCode() >= 11 && w.getWeekCode() <= 13) {
                                    cs.setMonth(monthMap.get(ID.valueOf(11)));
                                } else if (w.getWeekCode() == 15) {
                                    cs.setMonth(monthMap.get(ID.valueOf(12)));
                                }
                            } else if (w.getWeekCode() <= 17) {
                                cs.setMonth(monthMap.get(ID.valueOf(12)));
                                cs.setSymbol(symbolMap.get(ID.valueOf(2)));
                            } else if (w.getWeekCode() <= 19) {
                                cs.setSymbol(symbolMap.get(ID.valueOf(8)));
                                if (w.getWeekCode() == 19) {
                                    cs.setMonth(monthMap.get(ID.valueOf(1)));
                                }
                            } else if (w.getWeekCode() <= 23) {
                                cs.setSymbol(symbolMap.get(ID.valueOf(4)));
                                if (w.getWeekCode() <= 22) {
                                    cs.setMonth(monthMap.get(ID.valueOf(1)));
                                }
                            } else if (w.getWeekCode() <= 36) {
                                cs.setSymbol(symbolMap.get(ID.valueOf(6)));
                                if (w.getWeekCode() <= 26) {
                                    cs.setMonth(monthMap.get(ID.valueOf(2)));
                                } else if (w.getWeekCode() >= 28 && w.getWeekCode() <= 30) {
                                    cs.setMonth(monthMap.get(ID.valueOf(3)));
                                } else if (w.getWeekCode() >= 32 && w.getWeekCode() <= 35) {
                                    cs.setMonth(monthMap.get(ID.valueOf(4)));
                                } else if (w.getWeekCode() == 36) {
                                    cs.setMonth(monthMap.get(ID.valueOf(5)));
                                }
                            } else if (w.getWeekCode() <= 38) {
                                cs.setSymbol(symbolMap.get(ID.valueOf(7)));
                                cs.setMonth(monthMap.get(ID.valueOf(5)));
                            } else {
                                cs.setSymbol(symbolMap.get(ID.valueOf(9)));
                                if (w.getWeekCode() == 39) {
                                    cs.setMonth(monthMap.get(ID.valueOf(5)));
                                }
                            }

                            newList.add(cs);
                        }
                    }
                }
            }

            if (!newList.isEmpty()) {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(newList);
            }
        }
    }

    private void generateMasterSchedule(CURRICULUM curriculum) throws Exception {
        QueryModel<CURRICULUM_SCHEDULE> qm = new QueryModel<>(CURRICULUM_SCHEDULE.class);
        qm.addWhere("curriculum", ECriteria.EQUAL, curriculum.getId());
        List<CURRICULUM_SCHEDULE> list = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qm);
        if (list.size() < 92) {
            if (!list.isEmpty()) {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).delete(list);
            }

            List<CURRICULUM_SCHEDULE> newList = new ArrayList<>();

            QueryModel<MONTH> qmMonth = new QueryModel<>(MONTH.class);
            List<MONTH> monthList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qmMonth);
            Map<ID, MONTH> monthMap = new HashMap<>();
            for (MONTH m : monthList) {
                monthMap.put(m.getId(), m);
            }

            QueryModel<CURRICULUM_SCHEDULE_SYMBOL> qmSymbol = new QueryModel<>(CURRICULUM_SCHEDULE_SYMBOL.class);
            List<CURRICULUM_SCHEDULE_SYMBOL> symbolList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qmSymbol);
            Map<ID, CURRICULUM_SCHEDULE_SYMBOL> symbolMap = new HashMap<>();
            for (CURRICULUM_SCHEDULE_SYMBOL css : symbolList) {
                symbolMap.put(css.getId(), css);
            }

            QueryModel<STUDY_YEAR> qmStudyYear = new QueryModel<>(STUDY_YEAR.class);
            qmStudyYear.addWhere("studyYear", ECriteria.LESS_EQUAL, 2);
            qmStudyYear.addOrder("studyYear");
            List<STUDY_YEAR> studyYearList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookup(qmStudyYear);

            QueryModel<WEEK> qmWeek = new QueryModel<>(WEEK.class);
            qmWeek.addOrder("id");
            List<WEEK> weekList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qmWeek);

            for (STUDY_YEAR sy : studyYearList) {
                for (WEEK w : weekList) {
                    if (sy.getStudyYear() == 1) {
                        CURRICULUM_SCHEDULE cs = new CURRICULUM_SCHEDULE();
                        cs.setCurriculum(curriculum);
                        cs.setStudyYear(sy);
                        cs.setWeek(w);
                        if (w.getWeekCode() <= 15) {
                            cs.setSymbol(symbolMap.get(ID.valueOf(1)));
                            if (w.getWeekCode() <= 4) {
                                cs.setMonth(monthMap.get(ID.valueOf(9)));
                            } else if (w.getWeekCode() >= 6 && w.getWeekCode() <= 9) {
                                cs.setMonth(monthMap.get(ID.valueOf(10)));
                            } else if (w.getWeekCode() >= 11 && w.getWeekCode() <= 13) {
                                cs.setMonth(monthMap.get(ID.valueOf(11)));
                            } else if (w.getWeekCode() == 15) {
                                cs.setMonth(monthMap.get(ID.valueOf(12)));
                            }
                        } else if (w.getWeekCode() <= 17) {
                            cs.setSymbol(symbolMap.get(ID.valueOf(2)));
                            cs.setMonth(monthMap.get(ID.valueOf(12)));
                        } else if (w.getWeekCode() <= 19) {
                            cs.setSymbol(symbolMap.get(ID.valueOf(8)));
                            if (w.getWeekCode() == 19) {
                                cs.setMonth(monthMap.get(ID.valueOf(1)));
                            }
                        } else if (w.getWeekCode() <= 34) {
                            cs.setSymbol(symbolMap.get(ID.valueOf(1)));
                            if (w.getWeekCode() <= 22) {
                                cs.setMonth(monthMap.get(ID.valueOf(1)));
                            } else if (w.getWeekCode() >= 24 && w.getWeekCode() <= 26) {
                                cs.setMonth(monthMap.get(ID.valueOf(2)));
                            } else if (w.getWeekCode() >= 28 && w.getWeekCode() <= 30) {
                                cs.setMonth(monthMap.get(ID.valueOf(3)));
                            } else if (w.getWeekCode() >= 32) {
                                cs.setMonth(monthMap.get(ID.valueOf(4)));
                            }
                        } else if (w.getWeekCode() <= 36) {
                            cs.setSymbol(symbolMap.get(ID.valueOf(2)));
                            if (w.getWeekCode() == 35) {
                                cs.setMonth(monthMap.get(ID.valueOf(4)));
                            } else {
                                cs.setMonth(monthMap.get(ID.valueOf(5)));
                            }
                        } else {
                            if (w.getWeekCode() <= 38) {
                                cs.setMonth(monthMap.get(ID.valueOf(5)));
                                if (sy.getStudyYear() == 1) {
                                    cs.setSymbol(symbolMap.get(ID.valueOf(3)));
                                } else {
                                    cs.setSymbol(symbolMap.get(ID.valueOf(4)));
                                }
                            } else if (w.getWeekCode() == 39) {
                                cs.setMonth(monthMap.get(ID.valueOf(5)));
                                if (sy.getStudyYear() == 1) {
                                    cs.setSymbol(symbolMap.get(ID.valueOf(8)));
                                } else if (sy.getStudyYear() == 2) {
                                    cs.setSymbol(symbolMap.get(ID.valueOf(5)));
                                } else {
                                    cs.setSymbol(symbolMap.get(ID.valueOf(4)));
                                }
                            } else if (w.getWeekCode() == 40) {
                                if (sy.getStudyYear() == 3) {
                                    cs.setSymbol(symbolMap.get(ID.valueOf(4)));
                                } else {
                                    cs.setSymbol(symbolMap.get(ID.valueOf(8)));
                                }
                            } else {
                                cs.setSymbol(symbolMap.get(ID.valueOf(8)));
                                if (w.getWeekCode() <= 43) {
                                    cs.setMonth(monthMap.get(ID.valueOf(6)));
                                } else if (w.getWeekCode() >= 45 && w.getWeekCode() <= 48) {
                                    cs.setMonth(monthMap.get(ID.valueOf(7)));
                                } else if (w.getWeekCode() >= 49) {
                                    cs.setMonth(monthMap.get(ID.valueOf(8)));
                                }
                            }
                        }

                        newList.add(cs);
                    } else {
                        if (w.getWeekCode() <= 40) {
                            CURRICULUM_SCHEDULE cs = new CURRICULUM_SCHEDULE();
                            cs.setCurriculum(curriculum);
                            cs.setStudyYear(sy);
                            cs.setWeek(w);
                            if (w.getWeekCode() <= 15) {
                                cs.setSymbol(symbolMap.get(ID.valueOf(1)));
                                if (w.getWeekCode() <= 4) {
                                    cs.setMonth(monthMap.get(ID.valueOf(9)));
                                } else if (w.getWeekCode() >= 6 && w.getWeekCode() <= 9) {
                                    cs.setMonth(monthMap.get(ID.valueOf(10)));
                                } else if (w.getWeekCode() >= 11 && w.getWeekCode() <= 13) {
                                    cs.setMonth(monthMap.get(ID.valueOf(11)));
                                } else if (w.getWeekCode() == 15) {
                                    cs.setMonth(monthMap.get(ID.valueOf(12)));
                                }
                            } else if (w.getWeekCode() <= 17) {
                                cs.setMonth(monthMap.get(ID.valueOf(12)));
                                cs.setSymbol(symbolMap.get(ID.valueOf(2)));
                            } else if (w.getWeekCode() <= 19) {
                                cs.setSymbol(symbolMap.get(ID.valueOf(8)));
                                if (w.getWeekCode() == 19) {
                                    cs.setMonth(monthMap.get(ID.valueOf(1)));
                                }
                            } else if (w.getWeekCode() <= 23) {
                                cs.setSymbol(symbolMap.get(ID.valueOf(4)));
                                if (w.getWeekCode() <= 22) {
                                    cs.setMonth(monthMap.get(ID.valueOf(1)));
                                }
                            } else if (w.getWeekCode() <= 36) {
                                cs.setSymbol(symbolMap.get(ID.valueOf(6)));
                                if (w.getWeekCode() <= 26) {
                                    cs.setMonth(monthMap.get(ID.valueOf(2)));
                                } else if (w.getWeekCode() >= 28 && w.getWeekCode() <= 30) {
                                    cs.setMonth(monthMap.get(ID.valueOf(3)));
                                } else if (w.getWeekCode() >= 32 && w.getWeekCode() <= 35) {
                                    cs.setMonth(monthMap.get(ID.valueOf(4)));
                                } else if (w.getWeekCode() == 36) {
                                    cs.setMonth(monthMap.get(ID.valueOf(5)));
                                }
                            } else if (w.getWeekCode() <= 38) {
                                cs.setSymbol(symbolMap.get(ID.valueOf(7)));
                                cs.setMonth(monthMap.get(ID.valueOf(5)));
                            } else {
                                cs.setSymbol(symbolMap.get(ID.valueOf(9)));
                                if (w.getWeekCode() == 39) {
                                    cs.setMonth(monthMap.get(ID.valueOf(5)));
                                }
                            }

                            newList.add(cs);
                        }
                    }
                }
            }

            if (!newList.isEmpty()) {
                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(newList);
            }
        }
    }

    public final void checkForConform() throws Exception {
        if (getRecordCount() == 0) {
            throw new Exception(getUILocaleUtil().getMessage("no.curriculum.schedule"));
        }
    }

    public Button getEditButton() {
        return editButton;
    }
}
