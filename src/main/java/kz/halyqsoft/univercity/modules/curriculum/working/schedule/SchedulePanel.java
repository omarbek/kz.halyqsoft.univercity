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
import kz.halyqsoft.univercity.utils.excel.ExcelStyles;
import kz.halyqsoft.univercity.utils.excel.ExcelUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
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

    private List<WEEK> weekList;
    private Map<String, CURRICULUM_SCHEDULE_SYMBOL> symbolMap = new HashMap<String, CURRICULUM_SCHEDULE_SYMBOL>();

    public SchedulePanel(CurriculumView parentView) {
        super(parentView);
    }




    @Override
    public void initPanel() throws Exception {

        Button editButton = new Button("Edit");
        editButton.setCaption(getUILocaleUtil().getCaption("edit"));
        editButton.setWidth(120, Unit.PIXELS);
        editButton.setIcon(new ThemeResource("img/button/edit.png"));
        getContent().addComponent(editButton);

        editButton.addClickListener(
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent clickEvent) {

                       try{
                           if(grid.getSelectedRow()!=null)
                           {
                               STUDY_YEAR study_year = ((ScheduleBean)grid.getSelectedRow()).getStudyYear();
                               QueryModel<CURRICULUM_SCHEDULE> scheduleQueryModel = new QueryModel<>(CURRICULUM_SCHEDULE.class);
                               scheduleQueryModel.addWhere("studyYear" , ECriteria.EQUAL , study_year.getId());
                               scheduleQueryModel.addOrder("id");
                               List<CURRICULUM_SCHEDULE> scheduleList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(scheduleQueryModel);
                               SchedulePanelEdit schedulePanelEdit = new SchedulePanelEdit((scheduleList), SchedulePanel.this );

                           }else{
                               Message.showError("Choose one row");
                           }
                       }catch (Exception e){
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

            QueryModel<CURRICULUM_SCHEDULE_SYMBOL> qmSymbol = new QueryModel<CURRICULUM_SCHEDULE_SYMBOL>(CURRICULUM_SCHEDULE_SYMBOL.class);
            List<CURRICULUM_SCHEDULE_SYMBOL> symbolList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qmSymbol);
            Map<ID, CURRICULUM_SCHEDULE_SYMBOL> symbolMap = new HashMap<ID, CURRICULUM_SCHEDULE_SYMBOL>();
            for (CURRICULUM_SCHEDULE_SYMBOL css : symbolList) {
                symbolMap.put(css.getId(), css);
            }

            QueryModel<STUDY_YEAR> qmStudyYear = new QueryModel<STUDY_YEAR>(STUDY_YEAR.class);
            qmStudyYear.addWhere("studyYear", ECriteria.LESS_EQUAL, 4);
            qmStudyYear.addOrder("studyYear");
            List<STUDY_YEAR> studyYearList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qmStudyYear);

            QueryModel<WEEK> qmWeek = new QueryModel<WEEK>(WEEK.class);
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
        QueryModel<CURRICULUM_SCHEDULE> qm = new QueryModel<CURRICULUM_SCHEDULE>(CURRICULUM_SCHEDULE.class);
        qm.addWhere("curriculum", ECriteria.EQUAL, curriculum.getId());
        List<CURRICULUM_SCHEDULE> list = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qm);
        if (list.size() < 92) {
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

            QueryModel<CURRICULUM_SCHEDULE_SYMBOL> qmSymbol = new QueryModel<CURRICULUM_SCHEDULE_SYMBOL>(CURRICULUM_SCHEDULE_SYMBOL.class);
            List<CURRICULUM_SCHEDULE_SYMBOL> symbolList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qmSymbol);
            Map<ID, CURRICULUM_SCHEDULE_SYMBOL> symbolMap = new HashMap<ID, CURRICULUM_SCHEDULE_SYMBOL>();
            for (CURRICULUM_SCHEDULE_SYMBOL css : symbolList) {
                symbolMap.put(css.getId(), css);
            }

            QueryModel<STUDY_YEAR> qmStudyYear = new QueryModel<STUDY_YEAR>(STUDY_YEAR.class);
            qmStudyYear.addWhere("studyYear", ECriteria.LESS_EQUAL, 2);
            qmStudyYear.addOrder("studyYear");
            List<STUDY_YEAR> studyYearList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qmStudyYear);

            QueryModel<WEEK> qmWeek = new QueryModel<WEEK>(WEEK.class);
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

    public final void checkForConfirm() throws Exception {
        if (getRecordCount() == 0) {
            throw new Exception(getUILocaleUtil().getMessage("no.curriculum.schedule"));
        }
    }

    public void fillWorkbook(Workbook wb) throws Exception {
        Map<ExcelStyles, CellStyle> styles = ExcelUtil.createStyles(wb);

        Sheet sheet = wb.createSheet(getUILocaleUtil().getCaption("curriculum.schedule"));
        sheet.setDisplayGridlines(true);
        sheet.getPrintSetup().setLandscape(true);
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue(getUILocaleUtil().getCaption("curriculum.1"));
        cell.setCellStyle(styles.get(ExcelStyles.TITLE));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$A$1:$BA$1"));

        String specialityText = getParentView().getSpecialityText();
        row = sheet.createRow(1);
        cell = row.createCell(0);
        cell.setCellValue(specialityText);
        cell.setCellStyle(styles.get(ExcelStyles.SUBTITLE_CENTER));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$A$2:$BA$2"));

        LOG.info(specialityText);

        row = sheet.createRow(4);
        cell = row.createCell(1);
        cell.setCellValue(getParentView().getAcademicDegreeText());
        cell.setCellStyle(styles.get(ExcelStyles.SUBTITLE_LEFT));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$B$5:$BA$5"));

        row = sheet.createRow(5);
        cell = row.createCell(1);
        cell.setCellValue(getParentView().getStudyPeriodText());
        cell.setCellStyle(styles.get(ExcelStyles.SUBTITLE_LEFT));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$B$6:$BA$6"));

        row = sheet.createRow(6);
        cell = row.createCell(0);
        cell.setCellValue(getUILocaleUtil().getCaption("curriculum.schedule"));
        cell.setCellStyle(styles.get(ExcelStyles.SUBTITLE_CENTER));
        sheet.addMergedRegion(CellRangeAddress.valueOf("$A$7:$BA$7"));

        int startRow = 7;

        row = sheet.createRow(startRow);
        cell = row.createCell(0);
        cell.setCellValue(getUILocaleUtil().getEntityLabel(STUDY_YEAR.class));
        cell.setCellStyle(styles.get(ExcelStyles.HEADER_VERTICAL));

        cell = row.createCell(1);
        cell.setCellValue(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(MONTH.class, ID.valueOf(9)).getMonthNameRU());
        cell.setCellStyle(styles.get(ExcelStyles.HEADER));
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 1, 4));

        cell = row.createCell(6);
        cell.setCellValue(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(MONTH.class, ID.valueOf(10)).getMonthNameRU());
        cell.setCellStyle(styles.get(ExcelStyles.HEADER));
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 6, 9));

        cell = row.createCell(11);
        cell.setCellValue(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(MONTH.class, ID.valueOf(11)).getMonthNameRU());
        cell.setCellStyle(styles.get(ExcelStyles.HEADER));
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 11, 13));

        cell = row.createCell(15);
        cell.setCellValue(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(MONTH.class, ID.valueOf(12)).getMonthNameRU());
        cell.setCellStyle(styles.get(ExcelStyles.HEADER));
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 15, 17));

        cell = row.createCell(19);
        cell.setCellValue(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(MONTH.class, ID.valueOf(1)).getMonthNameRU());
        cell.setCellStyle(styles.get(ExcelStyles.HEADER));
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 19, 22));

        cell = row.createCell(24);
        cell.setCellValue(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(MONTH.class, ID.valueOf(2)).getMonthNameRU());
        cell.setCellStyle(styles.get(ExcelStyles.HEADER));
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 24, 26));

        cell = row.createCell(28);
        cell.setCellValue(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(MONTH.class, ID.valueOf(3)).getMonthNameRU());
        cell.setCellStyle(styles.get(ExcelStyles.HEADER));
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 28, 30));

        cell = row.createCell(32);
        cell.setCellValue(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(MONTH.class, ID.valueOf(4)).getMonthNameRU());
        cell.setCellStyle(styles.get(ExcelStyles.HEADER));
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 32, 35));

        cell = row.createCell(36);
        cell.setCellValue(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(MONTH.class, ID.valueOf(5)).getMonthNameRU());
        cell.setCellStyle(styles.get(ExcelStyles.HEADER));
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 36, 39));

        cell = row.createCell(41);
        cell.setCellValue(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(MONTH.class, ID.valueOf(6)).getMonthNameRU());
        cell.setCellStyle(styles.get(ExcelStyles.HEADER));
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 41, 43));

        cell = row.createCell(45);
        cell.setCellValue(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(MONTH.class, ID.valueOf(7)).getMonthNameRU());
        cell.setCellStyle(styles.get(ExcelStyles.HEADER));
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 45, 48));

        cell = row.createCell(49);
        cell.setCellValue(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(MONTH.class, ID.valueOf(8)).getMonthNameRU());
        cell.setCellStyle(styles.get(ExcelStyles.HEADER));
        sheet.addMergedRegion(new CellRangeAddress(startRow, startRow, 49, 52));

        QueryModel<WEEK> qmWeek = new QueryModel<WEEK>(WEEK.class);
        qmWeek.addOrder("weekCode");
        List<WEEK> weekList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qmWeek);

        startRow++;
        row = sheet.createRow(startRow);
        for (WEEK w : weekList) {
            cell = row.createCell(w.getWeekCode());
            cell.setCellValue(w.getWeekCode());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));
        }

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

        startRow++;
        for (ScheduleBean sb : sbList) {
            row = sheet.createRow(startRow);
            cell = row.createCell(0);
            cell.setCellValue(sb.getStudyYear().getStudyYear());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(1);
            cell.setCellValue(sb.getWeek01Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(2);
            cell.setCellValue(sb.getWeek02Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(3);
            cell.setCellValue(sb.getWeek03Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(4);
            cell.setCellValue(sb.getWeek04Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(5);
            cell.setCellValue(sb.getWeek05Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(6);
            cell.setCellValue(sb.getWeek06Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(7);
            cell.setCellValue(sb.getWeek07Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(8);
            cell.setCellValue(sb.getWeek08Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(9);
            cell.setCellValue(sb.getWeek09Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(10);
            cell.setCellValue(sb.getWeek10Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(11);
            cell.setCellValue(sb.getWeek11Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(12);
            cell.setCellValue(sb.getWeek12Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(13);
            cell.setCellValue(sb.getWeek13Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(14);
            cell.setCellValue(sb.getWeek14Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(15);
            cell.setCellValue(sb.getWeek15Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(16);
            cell.setCellValue(sb.getWeek16Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(17);
            cell.setCellValue(sb.getWeek17Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(18);
            cell.setCellValue(sb.getWeek18Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(19);
            cell.setCellValue(sb.getWeek19Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(20);
            cell.setCellValue(sb.getWeek20Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(21);
            cell.setCellValue(sb.getWeek21Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(22);
            cell.setCellValue(sb.getWeek22Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(23);
            cell.setCellValue(sb.getWeek23Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(24);
            cell.setCellValue(sb.getWeek24Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(25);
            cell.setCellValue(sb.getWeek25Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(26);
            cell.setCellValue(sb.getWeek26Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(27);
            cell.setCellValue(sb.getWeek27Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(28);
            cell.setCellValue(sb.getWeek28Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(29);
            cell.setCellValue(sb.getWeek29Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(30);
            cell.setCellValue(sb.getWeek30Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(31);
            cell.setCellValue(sb.getWeek31Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(32);
            cell.setCellValue(sb.getWeek32Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(33);
            cell.setCellValue(sb.getWeek33Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(34);
            cell.setCellValue(sb.getWeek34Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(35);
            cell.setCellValue(sb.getWeek35Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(36);
            cell.setCellValue(sb.getWeek36Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(37);
            cell.setCellValue(sb.getWeek37Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(38);
            cell.setCellValue(sb.getWeek38Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(39);
            cell.setCellValue(sb.getWeek39Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            cell = row.createCell(40);
            cell.setCellValue(sb.getWeek40Symbol());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

            if (sb.getStudyYear().getStudyYear() <= 3) {
                cell = row.createCell(41);
                cell.setCellValue(sb.getWeek41Symbol());
                cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

                cell = row.createCell(42);
                cell.setCellValue(sb.getWeek42Symbol());
                cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

                cell = row.createCell(43);
                cell.setCellValue(sb.getWeek43Symbol());
                cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

                cell = row.createCell(44);
                cell.setCellValue(sb.getWeek44Symbol());
                cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

                cell = row.createCell(45);
                cell.setCellValue(sb.getWeek45Symbol());
                cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

                cell = row.createCell(46);
                cell.setCellValue(sb.getWeek46Symbol());
                cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

                cell = row.createCell(47);
                cell.setCellValue(sb.getWeek47Symbol());
                cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

                cell = row.createCell(48);
                cell.setCellValue(sb.getWeek48Symbol());
                cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

                cell = row.createCell(49);
                cell.setCellValue(sb.getWeek49Symbol());
                cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

                cell = row.createCell(50);
                cell.setCellValue(sb.getWeek50Symbol());
                cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

                cell = row.createCell(51);
                cell.setCellValue(sb.getWeek51Symbol());
                cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));

                cell = row.createCell(52);
                cell.setCellValue(sb.getWeek52Symbol());
                cell.setCellStyle(styles.get(ExcelStyles.CONTENT_CENTER));
            }

            startRow++;
        }

        sheet.setColumnWidth(0, 3 * 256);
        sheet.setColumnWidth(1, 3 * 256);
        sheet.setColumnWidth(2, 3 * 256);
        sheet.setColumnWidth(3, 3 * 256);
        sheet.setColumnWidth(4, 3 * 256);
        sheet.setColumnWidth(5, 3 * 256);
        sheet.setColumnWidth(6, 3 * 256);
        sheet.setColumnWidth(7, 3 * 256);
        sheet.setColumnWidth(8, 3 * 256);
        sheet.setColumnWidth(9, 3 * 256);
        sheet.setColumnWidth(10, 3 * 256);
        sheet.setColumnWidth(11, 3 * 256);
        sheet.setColumnWidth(12, 3 * 256);
        sheet.setColumnWidth(13, 3 * 256);
        sheet.setColumnWidth(14, 3 * 256);
        sheet.setColumnWidth(15, 3 * 256);
        sheet.setColumnWidth(16, 3 * 256);
        sheet.setColumnWidth(17, 3 * 256);
        sheet.setColumnWidth(18, 3 * 256);
        sheet.setColumnWidth(19, 3 * 256);
        sheet.setColumnWidth(20, 3 * 256);
        sheet.setColumnWidth(21, 3 * 256);
        sheet.setColumnWidth(22, 3 * 256);
        sheet.setColumnWidth(23, 3 * 256);
        sheet.setColumnWidth(24, 3 * 256);
        sheet.setColumnWidth(25, 3 * 256);
        sheet.setColumnWidth(26, 3 * 256);
        sheet.setColumnWidth(27, 3 * 256);
        sheet.setColumnWidth(28, 3 * 256);
        sheet.setColumnWidth(29, 3 * 256);
        sheet.setColumnWidth(30, 3 * 256);
        sheet.setColumnWidth(31, 3 * 256);
        sheet.setColumnWidth(32, 3 * 256);
        sheet.setColumnWidth(33, 3 * 256);
        sheet.setColumnWidth(34, 3 * 256);
        sheet.setColumnWidth(35, 3 * 256);
        sheet.setColumnWidth(36, 3 * 256);
        sheet.setColumnWidth(37, 3 * 256);
        sheet.setColumnWidth(38, 3 * 256);
        sheet.setColumnWidth(39, 3 * 256);
        sheet.setColumnWidth(40, 3 * 256);
        sheet.setColumnWidth(41, 3 * 256);
        sheet.setColumnWidth(42, 3 * 256);
        sheet.setColumnWidth(43, 3 * 256);
        sheet.setColumnWidth(44, 3 * 256);
        sheet.setColumnWidth(45, 3 * 256);
        sheet.setColumnWidth(46, 3 * 256);
        sheet.setColumnWidth(47, 3 * 256);
        sheet.setColumnWidth(48, 3 * 256);
        sheet.setColumnWidth(49, 3 * 256);
        sheet.setColumnWidth(50, 3 * 256);
        sheet.setColumnWidth(51, 3 * 256);
        sheet.setColumnWidth(52, 3 * 256);

        startRow++;

        row = sheet.createRow(startRow);
        cell = row.createCell(1);
        cell.setCellValue(getUILocaleUtil().getCaption("designations"));
        cell.setCellStyle(styles.get(ExcelStyles.HEADER_LEFT));

        QueryModel<CURRICULUM_SCHEDULE_SYMBOL> qmCSS = new QueryModel<CURRICULUM_SCHEDULE_SYMBOL>(CURRICULUM_SCHEDULE_SYMBOL.class);
        qmCSS.addOrder("id");
        List<CURRICULUM_SCHEDULE_SYMBOL> cssList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qmCSS);

        startRow++;
        row = sheet.createRow(startRow);
        for (int i = 0; i < 4; i++) {
            if (i == 0) {
                cell = row.createCell(1);
            } else if (i == 1) {
                cell = row.createCell(18);
            } else if (i == 2) {
                cell = row.createCell(28);
            } else {
                cell = row.createCell(38);
            }

            CURRICULUM_SCHEDULE_SYMBOL css = cssList.get(i);
            cell.setCellValue(css.getSymbol() + " - " + css.getDescr());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_LEFT));
        }

        startRow++;
        row = sheet.createRow(startRow);
        for (int i = 4; i < 9; i++) {
            if (i == 4) {
                cell = row.createCell(1);
            } else if (i == 5) {
                cell = row.createCell(5);
            } else if (i == 6) {
                cell = row.createCell(17);
            } else if (i == 7) {
                cell = row.createCell(28);
            } else {
                cell = row.createCell(34);
            }

            CURRICULUM_SCHEDULE_SYMBOL css = cssList.get(i);
            cell.setCellValue(css.getSymbol() + " - " + css.getDescr());
            cell.setCellStyle(styles.get(ExcelStyles.CONTENT_LEFT));
        }
    }
}
