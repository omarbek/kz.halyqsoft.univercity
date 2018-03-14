package kz.halyqsoft.univercity.modules.regusers;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.univercity.STUDENT;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ENTRANCE_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.LEVEL;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDENT_CATEGORY;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.filter.FilterableWidget;
import org.r3a.common.vaadin.widget.form.FormModel;
import org.r3a.common.vaadin.widget.form.GridFormWidget;
import org.r3a.common.vaadin.widget.form.field.fk.FKFieldModel;

import java.util.Calendar;

/**
 * @author Omarbek
 * @created on 13.03.2018
 */
public class RegisterUsersView extends AbstractTaskView implements EntityListener {

    public RegisterUsersView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        TabSheet mainForm = new TabSheet();

        Label mess = new Label("<br><strong>Для успешной регистрации на портале КБТУ Вам необходимо ОБЯЗАТЕЛЬНО ЗАПОЛНИТЬ следующие данные:</strong><br>" + "<i>1. Основные данные;</i><br><i>2. Ввести 4 специальности (Хотя бы одним из ВУЗов должен быть КБТУ);</i><br>" + "<i>3. Документ, удостоверящий личность (Удостоверение личности/паспорт);</i><br><i>4. Документ об образовании (Аттестат/диплом);</i><br>" + "<i>5. Сертификат ЕНТ/КТ.</i><br><br><hr>Поля, помеченные *, обязательны для заполнения<br><br>");
        mess.setContentMode(ContentMode.HTML);

        VerticalLayout selectFormVL = new VerticalLayout();
        selectFormVL.setSizeFull();
        selectFormVL.setSpacing(true);

        FormLayout selectFormFL = new FormLayout();
        selectFormVL.addComponent(selectFormFL);
        selectFormVL.setComponentAlignment(selectFormFL, Alignment.TOP_CENTER);

        Button buttonReg = new Button();
        buttonReg.setCaption(getUILocaleUtil().getCaption("sign.up"));
        buttonReg.addClickListener(new CreateApplicant());

        selectFormFL.addComponent(mess);
        selectFormFL.addComponent(buttonReg);
        selectFormFL.setComponentAlignment(buttonReg, Alignment.TOP_CENTER);
        mainForm.addTab(selectFormFL, getUILocaleUtil().getCaption("registration"));

        getContent().addComponent(mainForm);
    }
}
