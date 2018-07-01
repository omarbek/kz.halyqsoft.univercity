package kz.halyqsoft.univercity.modules.bindingspecialitytocorpus;

import com.vaadin.ui.Alignment;
import kz.halyqsoft.univercity.entity.beans.univercity.SPECIALITY_CORPUS;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Omarbek
 * @created on 26.06.2018
 */
public class
BindingSpecialityToCorpusView extends AbstractTaskView implements EntityListener {

    private GridWidget specAndCorpusGW;

    public BindingSpecialityToCorpusView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        specAndCorpusGW = new GridWidget(SPECIALITY_CORPUS.class);
        specAndCorpusGW.addEntityListener(this);
        specAndCorpusGW.setButtonVisible(IconToolbar.PREVIEW_BUTTON, false);
        specAndCorpusGW.setButtonVisible(IconToolbar.REFRESH_BUTTON, false);

        DBGridModel specAndCorpusGM = (DBGridModel) specAndCorpusGW.getWidgetModel();
        specAndCorpusGM.setRowNumberVisible(true);
        specAndCorpusGM.setTitleVisible(false);
        specAndCorpusGM.setMultiSelect(true);
        specAndCorpusGM.setRefreshType(ERefreshType.MANUAL);

        refresh();

        getContent().addComponent(specAndCorpusGW);
        getContent().setComponentAlignment(specAndCorpusGW, Alignment.MIDDLE_CENTER);
    }

    private void refresh() {
        try {
            List<SPECIALITY_CORPUS> specialityCorpuses = getSpecialityAndCorpuses();
            ((DBGridModel) specAndCorpusGW.getWidgetModel()).setEntities(specialityCorpuses);
            specAndCorpusGW.refresh();
        } catch (Exception ex) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh speciality and corpus grid", ex);
        }
    }

    private List<SPECIALITY_CORPUS> getSpecialityAndCorpuses() {
        QueryModel<SPECIALITY_CORPUS> specialityCorpusQM = new QueryModel<>(SPECIALITY_CORPUS.class);
        try {
            return SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                    lookup(specialityCorpusQM);
        } catch (Exception e) {
            CommonUtils.showMessageAndWriteLog("Unable to refresh get spec and corpuses list", e);
        }
        return new ArrayList<>();
    }

    @Override
    public void handleEntityEvent(EntityEvent ev) {
        if (ev.getAction() == EntityEvent.CREATED || ev.getAction() == EntityEvent.MERGED
                || ev.getAction() == EntityEvent.REMOVED) {
            refresh();
        }
    }

    @Override
    public boolean preSave(Object source, Entity e, boolean isNew, int buttonId) {
        List<SPECIALITY_CORPUS> specialityCorpuses = getSpecialityAndCorpuses();
        for (SPECIALITY_CORPUS specCorpus : specialityCorpuses) {
            SPECIALITY_CORPUS specialityCorpus = (SPECIALITY_CORPUS) e;
            if (specCorpus.getSpeciality().equals(specialityCorpus.getSpeciality())) {
                Message.showInfo(getUILocaleUtil().getMessage("this.spec.is.already.binded"));
                return false;
            }
        }
        return true;
    }
}
