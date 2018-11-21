package kz.halyqsoft.univercity.modules.stream.generate;

import com.vaadin.ui.ProgressBar;
import kz.halyqsoft.univercity.entity.beans.univercity.GROUPS;
import kz.halyqsoft.univercity.entity.beans.univercity.SPECIALITY_CORPUS;
import kz.halyqsoft.univercity.entity.beans.univercity.STREAM;
import kz.halyqsoft.univercity.entity.beans.univercity.STREAM_GROUP;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_GROUP;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.facade.CommonIDFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author Omarbek
 * @created on 08.11.2018
 */
public class GenerateSpecStreams extends Generate {

    public GenerateSpecStreams(ProgressBar samplePB) {
        super(samplePB);
    }

    @Override
    protected void distribute(Map<Long, Integer> corpusAndCapacityMap, List<LANGUAGE> languages,
                              STUDY_YEAR studyYear, CORPUS corpus, LEVEL level) throws Exception {
        QueryModel<SPECIALITY> specQM = new QueryModel<>(SPECIALITY.class);
        FromItem specCorpusFI = specQM.addJoin(EJoin.INNER_JOIN, "id", SPECIALITY_CORPUS.class,
                "speciality");
        specQM.addWhere("deleted", false);
        specQM.addWhere("level", ECriteria.EQUAL, level.getId());
        specQM.addWhere(specCorpusFI, "corpus", ECriteria.EQUAL, corpus.getId());
        specQM.addOrder("specName");
        List<SPECIALITY> specialities = SessionFacadeFactory.getSessionFacade(
                CommonEntityFacadeBean.class).lookup(specQM);

        for (SPECIALITY speciality : specialities) {
            for (LANGUAGE language : languages) {
                QueryModel<V_GROUP> groupQM = new QueryModel<>(V_GROUP.class);
                groupQM.addWhere("speciality", ECriteria.EQUAL, speciality.getId());
                groupQM.addWhere("studyYear", ECriteria.EQUAL, studyYear.getId());
                groupQM.addWhere("language", ECriteria.EQUAL, language.getId());
                List<V_GROUP> groups = SessionFacadeFactory.getSessionFacade(
                        CommonEntityFacadeBean.class).lookup(groupQM);

                int numberOfStudents = 0;
                for (V_GROUP group : groups) {
                    numberOfStudents += group.getStudentCount();
                }
                int numberOfGroups = groups.size();
                if (numberOfGroups > 0) {
                    if (numberOfStudents > corpusAndCapacityMap.get(
                            corpus.getId().getId().longValue())) {
                        List<V_GROUP> firstHalf = new ArrayList<>();
                        for (int i = 0; i < numberOfGroups / 2; i++) {
                            firstHalf.add(groups.get(i));
                        }
                        createStream(firstHalf);
                        groups.removeAll(firstHalf);
                        createStream(groups);
                    } else {
                        createStream(groups);
                    }
                }
            }
        }
    }

    @Override
    protected void createStream(List<V_GROUP> groups) throws Exception {
        STREAM stream = new STREAM();
        stream.setCreated(new Date());
        ID seqenceOfStream = SessionFacadeFactory.getSessionFacade(CommonIDFacadeBean.class).getID("s_stream");
        stream.setName(seqenceOfStream + "S");
        stream.setDeleted(false);
        stream.setLanguage(groups.get(0).getLanguage());
        stream.setStreamType(SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class)
                .lookup(STREAM_TYPE.class, STREAM_TYPE.SPEC));
        stream.setId(seqenceOfStream);
        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).createNoID(stream);

        for (V_GROUP group : groups) {
            STREAM_GROUP streamGroup = new STREAM_GROUP();
            streamGroup.setStream(stream);
            streamGroup.setGroup(SessionFacadeFactory.getSessionFacade(
                    CommonEntityFacadeBean.class).lookup(GROUPS.class, group.getId()));
            SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(
                    streamGroup);
        }
    }
}
