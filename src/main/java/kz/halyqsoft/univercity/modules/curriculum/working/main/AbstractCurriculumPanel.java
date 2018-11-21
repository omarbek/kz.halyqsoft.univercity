package kz.halyqsoft.univercity.modules.curriculum.working.main;

import kz.halyqsoft.univercity.entity.beans.univercity.CURRICULUM;
import org.r3a.common.vaadin.widget.AbstractCommonPanel;

/**
 * @author Omarbek Dinassil
 * @created Feb 24, 2016 11:36:51 AM
 */
@SuppressWarnings("serial")
public abstract class AbstractCurriculumPanel extends AbstractCommonPanel {

    private final CurriculumView parentView;
    private CURRICULUM curriculum;

    protected AbstractCurriculumPanel(CurriculumView parentView) {
        super();
        this.parentView = parentView;
    }

    public void initPanel() throws Exception {
    }

    public CURRICULUM getCurriculum() {
        return curriculum;
    }

    public void setCurriculum(CURRICULUM curriculum) {
        this.curriculum = curriculum;
    }

    protected final CurriculumView getParentView() {
        return parentView;
    }

    public abstract void refresh() throws Exception;
}
