package kz.halyqsoft.univercity.modules.student;

import com.vaadin.server.Sizeable;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.TextField;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.*;
import kz.halyqsoft.univercity.entity.beans.univercity.view.VStudent;
import kz.halyqsoft.univercity.filter.FStudentFilter;
import kz.halyqsoft.univercity.filter.panel.StudentFilterPanel;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.ERefreshType;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.filter2.AbstractFilterBean;
import org.r3a.common.vaadin.widget.filter2.FilterPanelListener;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.grid.GridWidget;
import org.r3a.common.vaadin.widget.grid.model.DBGridModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Omarbek
 * @created on 02.04.2018
 */
public class StudentView extends AbstractTaskView {

    private VerticalLayout mainVL;
    private HorizontalLayout buttonsHL;

    public StudentView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        mainVL = new VerticalLayout();
        buttonsHL = CommonUtils.createButtonPanel();
        setStudentOrApplicant(3, "students");
        setStudentOrApplicant(1, "applicants");
        getContent().addComponent(buttonsHL);
        getContent().setComponentAlignment(buttonsHL, Alignment.MIDDLE_CENTER);
        getContent().addComponent(mainVL);
    }

    private void setStudentOrApplicant(int categoryType, String caption) {
        Button openButton = new Button();
        openButton.setCaption(getUILocaleUtil().getCaption(caption));
        openButton.setWidth(120.0F, Sizeable.Unit.PIXELS);
        openButton.setIcon(new ThemeResource("img/button/users.png"));
        openButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    mainVL.removeAllComponents();
                    mainVL.addComponent(new StudentOrApplicantView(categoryType,buttonsHL));
                } catch (Exception e) {
                    e.printStackTrace();//TODO catch
                }
            }
        });
        buttonsHL.addComponent(openButton);
    }
}