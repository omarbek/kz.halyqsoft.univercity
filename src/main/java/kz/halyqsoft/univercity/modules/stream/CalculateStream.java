package kz.halyqsoft.univercity.modules.stream;

import kz.halyqsoft.univercity.entity.beans.univercity.GROUPS;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.CORPUS;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_GROUPS_CREATION_NEEDED;
import org.r3a.common.entity.AbstractEntity;
import org.r3a.common.entity.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalculateStream {

    List<Map<Entity, List<V_GROUPS_CREATION_NEEDED>>> sortedLanguageFromCorpus ;
    public List<Map<Entity, List<V_GROUPS_CREATION_NEEDED>>> sortedEntranceYearFromLanguage ;

    List<V_GROUPS_CREATION_NEEDED> list;

    public CalculateStream(List<V_GROUPS_CREATION_NEEDED> list){
        sortedLanguageFromCorpus = new ArrayList<>();
        sortedEntranceYearFromLanguage = new ArrayList<>();
        this.list = list;
        getSortedGroupsByCorpus(list);

        for(Map<Entity, List<V_GROUPS_CREATION_NEEDED>> value : sortedLanguageFromCorpus)
        {
            for(Entity key : value.keySet())
            {
                getSortedGroupsByEntranceYear(value.get(key));
            }
        }
    }

    private Map<Entity, List<V_GROUPS_CREATION_NEEDED>> getSortedGroupsByCorpus(List<V_GROUPS_CREATION_NEEDED> list){
        Map<Entity, List<V_GROUPS_CREATION_NEEDED>> sortedGroupsByCorpus = new HashMap();
            for(V_GROUPS_CREATION_NEEDED group : list){
                if(sortedGroupsByCorpus.get(group.getCorpus())!=null)
                {
                    sortedGroupsByCorpus.get(group.getCorpus()).add(group);
                }else{
                    sortedGroupsByCorpus.put(group.getCorpus(), new ArrayList<V_GROUPS_CREATION_NEEDED>());
                    sortedGroupsByCorpus.get(group.getCorpus()).add(group);
                }
            }
            for(Entity key : sortedGroupsByCorpus.keySet())
            {
                getSortedGroupsByLanguage(sortedGroupsByCorpus.get(key));
            }
        return sortedGroupsByCorpus;
    }

    private void getSortedGroupsByLanguage(List<V_GROUPS_CREATION_NEEDED> list){
        Map<Entity, List<V_GROUPS_CREATION_NEEDED>> sortedGroupsByCorpus = new HashMap();

        for(V_GROUPS_CREATION_NEEDED group : list){
            if(sortedGroupsByCorpus.get(group.getLanguage())!=null){
                sortedGroupsByCorpus.get(group.getLanguage()).add(group);
            }else{
                sortedGroupsByCorpus.put(group.getLanguage(), new ArrayList<V_GROUPS_CREATION_NEEDED>());
                sortedGroupsByCorpus.get(group.getLanguage()).add(group);
            }
        }
        sortedLanguageFromCorpus.add(sortedGroupsByCorpus);
    }

    private void getSortedGroupsByEntranceYear(List<V_GROUPS_CREATION_NEEDED> list){
        Map<Entity, List<V_GROUPS_CREATION_NEEDED>> sortedGroupsByEntranceYear = new HashMap();
        for(V_GROUPS_CREATION_NEEDED group : list){
            if(sortedGroupsByEntranceYear.get(group.getEntranceYear())!=null)
            {
                sortedGroupsByEntranceYear.get(group.getEntranceYear()).add(group);
            }else{
                sortedGroupsByEntranceYear.put(group.getEntranceYear(), new ArrayList<V_GROUPS_CREATION_NEEDED>());
                sortedGroupsByEntranceYear.get(group.getEntranceYear()).add(group);
            }
        }
        sortedEntranceYearFromLanguage.add(sortedGroupsByEntranceYear);

    }


}
