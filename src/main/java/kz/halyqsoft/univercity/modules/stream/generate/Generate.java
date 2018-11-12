package kz.halyqsoft.univercity.modules.stream.generate;

import com.vaadin.ui.Button;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.CORPUS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LANGUAGE;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LEVEL;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDY_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_GROUP;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.query.QueryModel;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Omarbek
 * @created on 09.11.2018
 */
abstract class Generate implements Button.ClickListener {

    private ProgressBar samplePB;

    public Generate(ProgressBar samplePB) {
        this.samplePB = samplePB;
    }

    private void updateProgressBar(UI ui, int progress, int maxProgress) {
        ui.access(() -> {
            final float newValue;
            if (progress == maxProgress) {
                ui.setPollInterval(-1);
                newValue = 0f;
                samplePB.setVisible(!samplePB.isIndeterminate());
            } else {
                newValue = (float) progress / maxProgress;
            }
            samplePB.setValue(newValue);
//            Notification.show("Value changed:", Float.toString(newValue), Notification.Type.TRAY_NOTIFICATION);
        });
    }

//    private void launchProgressUpdater(UI ui) {
//        new Thread(() -> {
//            for (int progress = 1; progress <= maxProgress; progress++) {
//                try {
//                    Thread.sleep(1000);
//                } catch (final InterruptedException e) {
//                    throw new RuntimeException("Unexpected interruption", e);
//                }
//                updateProgressBar(ui, progress, maxProgress);
//            }
//        }).start();
//    }


    @Override
    public void buttonClick(Button.ClickEvent event) {
        generate();
    }

    private void generate() {
        samplePB.setValue(0f);
        samplePB.setVisible(true);
        UI.getCurrent().setPollInterval(500);

        new Thread(() -> {
//        launchProgressUpdater(UI.getCurrent());
            try {
                String sql = "SELECT DISTINCT " +
                        "  corpus_id, " +
                        "  capacity " +
                        "FROM room " +
                        "WHERE capacity = (SELECT max(max_room.capacity) " +
                        "                  FROM room max_room " +
                        "                  WHERE max_room.corpus_id = room.corpus_id " +
                        "                        AND max_room.deleted = FALSE) " +
                        "      AND deleted = FALSE;";
                List list = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
                        .lookupItemsList(sql, new HashMap<>());
                Map<Long, Integer> corpusAndCapacityMap = new HashMap<>();
                for (Object object : list) {
                    Object[] objects = (Object[]) object;
                    Long corpusId = (Long) objects[0];
                    Integer capacity = ((BigDecimal) objects[1]).intValue();
                    corpusAndCapacityMap.put(corpusId, capacity);
                }

                QueryModel<STUDY_YEAR> studyYearQM = new QueryModel<>(STUDY_YEAR.class);
                List<STUDY_YEAR> studyYears = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
                        .lookup(studyYearQM);

                QueryModel<LEVEL> levelQM = new QueryModel<>(LEVEL.class);
                List<LEVEL> levels = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
                        .lookup(levelQM);

                QueryModel<LANGUAGE> langQM = new QueryModel<>(LANGUAGE.class);
                List<LANGUAGE> languages = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
                        .lookup(langQM);

                QueryModel<CORPUS> corpusQM = new QueryModel<>(CORPUS.class);
                List<CORPUS> corpuses = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                        lookup(corpusQM);

                int numberOfStream = 0;
                int currentLoop = 0;
                for (STUDY_YEAR studyYear : studyYears) {
                    currentLoop++;
                    for (CORPUS corpus : corpuses) {
                        currentLoop++;
                        for (LEVEL level : levels) {
                            currentLoop++;
                            distribute(corpusAndCapacityMap, languages, studyYear, corpus, level);
                            try {
                                Thread.sleep(1000);
                            } catch (final InterruptedException e) {
                                throw new RuntimeException("Unexpected interruption", e);
                            }
                            int maxProgress = studyYears.size() * corpuses.size() * levels.size();
                            updateProgressBar(UI.getCurrent(), currentLoop, maxProgress);
                        }
                    }
                }
                samplePB.setValue(0f);
                CommonUtils.showSavedNotification();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    protected abstract void distribute(Map<Long, Integer> corpusAndCapacityMap, List<LANGUAGE> languages,
                                       STUDY_YEAR studyYear, CORPUS corpus, LEVEL level) throws Exception;

    protected abstract void createStream(List<V_GROUP> groups) throws Exception;
}
