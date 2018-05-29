package kz.halyqsoft.univercity.modules.chat;

import com.vaadin.data.Property;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.MouseEvents;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.CHAT;
import kz.halyqsoft.univercity.entity.beans.univercity.MESSAGE;
import kz.halyqsoft.univercity.entity.beans.univercity.view.V_STUDENT;
import kz.halyqsoft.univercity.modules.reports.MenuColumn;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.select.EAggregate;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.entity.tree.CommonTree;
import org.r3a.common.vaadin.AbstractSecureWebUI;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;
import org.r3a.common.vaadin.widget.tree.CommonTreeWidget;
import sun.jvm.hotspot.HotSpotAgent;

import javax.persistence.NoResultException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class ChatView extends AbstractTaskView implements EntityListener  {

    private HorizontalSplitPanel mainHSP;

    private CommonTreeWidget usersCTW;
    private AbstractLayout mainLayout;
    private CHAT mainChat;
    private  TextArea chatBodyTextArea;
    public ChatView(AbstractTask task) throws Exception{
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        mainHSP = new HorizontalSplitPanel();
        mainHSP.setSplitPosition(25);
        mainHSP.isResponsive();


        final TreeTable menuTT = new TreeTable();

        HierarchicalContainer hierarchicalContainer = new HierarchicalContainer();
        hierarchicalContainer.addItem("Друзья");
        hierarchicalContainer.addItem("Исходящие запросы");
        hierarchicalContainer.addItem("Входящие запросы");
        menuTT.setContainerDataSource(hierarchicalContainer);
        menuTT.setContainerDataSource(hierarchicalContainer);
        menuTT.setSizeFull();
        menuTT.addStyleName("schedule");
        menuTT.setSelectable(true);
        menuTT.setMultiSelect(false);
        menuTT.setNullSelectionAllowed(false);
        menuTT.setImmediate(true);
        menuTT.setColumnReorderingAllowed(false);
        menuTT.setPageLength(20);
        MenuColumn menuColumn = new MenuColumn();
        menuTT.addGeneratedColumn("users", menuColumn);
        menuTT.setColumnHeader("users", "Пользователи");

        menuTT.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                try {
                    if (event != null && event.getProperty() != null && event.getProperty().getValue() != null) {
                        if ("Студенты".equals(event.getProperty().getValue().toString())) {
                            setStudents();
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            private void setStudents() throws Exception {
                QueryModel<V_STUDENT> studentQM = new QueryModel<>(V_STUDENT.class);
                studentQM.addSelect("userCode", EAggregate.COUNT);
                studentQM.addWhere("deleted", Boolean.FALSE);
                Long allStudents = (Long) SessionFacadeFactory.
                        getSessionFacade(CommonEntityFacadeBean.class).lookupItems(studentQM);

                String sql = "SELECT count(1) " +
                        "FROM user_arrival arriv INNER JOIN v_student stu ON stu.id = arriv.user_id " +
                        "WHERE date_trunc('day', arriv.created) = date_trunc('day', now()) " +
                        "      AND arriv.created = (SELECT max(max_arriv.created) " +
                        "                           FROM user_arrival max_arriv " +
                        "                           WHERE max_arriv.user_id = arriv.user_id) " +
                        "      AND come_in = TRUE";
                Long inStudents = (Long) SessionFacadeFactory.getSessionFacade(
                        CommonEntityFacadeBean.class).lookupSingle(sql, new HashMap<>());
                long inPercent = inStudents * 100 / allStudents;

                Panel studentsPanel = new Panel("<b>Учащиеся - " + allStudents + "</b><br>" +
                        "Присутс. - " + inStudents + " (" + inPercent + "%)<br>" +
                        "Отсутс. - " + (allStudents - inStudents) + " (" + (100 - inPercent) + "%)" +
                        "<br>");
                studentsPanel.setIcon(new ThemeResource("img/student.png"));
                studentsPanel.addClickListener(new MouseEvents.ClickListener() {
                    @Override
                    public void click(MouseEvents.ClickEvent event) {
                        Message.showInfo("Students");
                    }
                });
            }
        });



        //usersCTW = new CommonTreeWidget(AbstractSecureWebUI.getInstance().getUserClass().asSubclass(CommonTree.class));
//        usersCTW = new CommonTreeWidget();
//
//        usersCTW.setButtonVisible(IconToolbar.REFRESH_BUTTON , true);
//        usersCTW.setButtonVisible(IconToolbar.EDIT_BUTTON , false);
//        usersCTW.setButtonVisible(IconToolbar.DELETE_BUTTON , false);
//        usersCTW.setButtonVisible(IconToolbar.ADD_BUTTON , false);
//        usersCTW.setButtonVisible(IconToolbar.FILTER_BUTTON , true);
//
//        usersCTW.addEntityListener(this);
//
//        mainHSP.addComponent(usersCTW);
        mainHSP.addComponent(menuTT);
        getContent().addComponent(mainHSP);
    }

    private VerticalLayout initChatAcceptRequestView(CHAT chat){

        VerticalLayout chatAcceptRequestHL = new VerticalLayout();

        Label label = new Label();
        label.setValue("Someone sending you a request to have a conversation!");
        Button button = new Button("ACCEPT");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                chat.setAccepted(true);
                try {
                    SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(chat);
                } catch (Exception e) {
                    Message.showError(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        chatAcceptRequestHL.removeAllComponents();
        chatAcceptRequestHL.addComponent(label);
        chatAcceptRequestHL.setComponentAlignment(label,Alignment.MIDDLE_CENTER);

        chatAcceptRequestHL.addComponent(button);
        chatAcceptRequestHL.setComponentAlignment(button,Alignment.MIDDLE_CENTER);

        return chatAcceptRequestHL;
    }

    private VerticalLayout initWaitingForResponseView(){
        VerticalLayout waitingForResultVL = new VerticalLayout();

        Label label = new Label("You've already sent a request for having a conversation!");
        waitingForResultVL.addComponent(label);
        waitingForResultVL.setComponentAlignment(label , Alignment.MIDDLE_CENTER);

        return waitingForResultVL;
    }

    private VerticalLayout initChatSendRequestView(){
        VerticalLayout chatSendRequestVL = new VerticalLayout();

        Button send = new Button("Send a request");

        send.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                CHAT chat = new CHAT();
                USERS first = new USERS();
                first.setId(new ID(getCurrentUserId()));

                chat.setFirst_user(first);
                chat.setSecond_user((USERS) usersCTW.getSelectedEntity());
                chat.setAccepted(false);

                if(!isChatExists(chat.getFirst_user() , chat.getSecond_user())){
                    try {
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(chat);
                    } catch (Exception e) {
                        Message.showError(e.getMessage());
                        e.printStackTrace();
                    }
                }else{
                    Message.showError("You've already had a chat");
                }
            }
        });
        send.setSizeFull();

        chatSendRequestVL.addComponent(send);
        chatSendRequestVL.setComponentAlignment(send, Alignment.MIDDLE_CENTER);
        return  chatSendRequestVL;
    }

    private VerticalLayout initChatView(CHAT chat){
        VerticalLayout chatVL = new VerticalLayout();
        VerticalLayout contentVerticalLayout = new VerticalLayout();
        VerticalLayout upperSideLayout = new VerticalLayout();
        HorizontalLayout lowerSideLayout = new HorizontalLayout();

        chatBodyTextArea.setImmediate(true);
        chatBodyTextArea.setSizeFull();
        chatBodyTextArea.setHeight(50 , Unit.PERCENTAGE);
        mainChat = chat;

        chatBodyTextArea.setReadOnly(false);
        String result = reloadChatBox(mainChat);
        chatBodyTextArea.clear();
        chatBodyTextArea.setValue(result);
        chatBodyTextArea.setReadOnly(true);




        upperSideLayout.addComponent(chatBodyTextArea);
        upperSideLayout.setComponentAlignment(chatBodyTextArea, Alignment.MIDDLE_CENTER);

        TextArea textArea = new TextArea();
        textArea.setSizeFull();
        textArea.setWordwrap(true);
        textArea.setImmediate(true);

        Button sendMessage = new Button("Send");
        sendMessage.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                String content = textArea.getValue();
                if(!content.trim().equals(""))
                {
                    MESSAGE message = new MESSAGE();
                    message.setChat(chat);
                    message.setContent(content);

                    if(chat.getFirst_user().getLogin().equals(getCurrentUser().getLogin()))
                    {
                        message.setFromFirst(true);
                    }else{
                        message.setFromFirst(false);
                    }
                    java.util.Date utilDate = new java.util.Date();
                    message.setCreated(new java.sql.Date(utilDate.getTime()));

                    try{
                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).create(message);

                    }catch (Exception e)
                    {
                        Message.showError(e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
        });
        sendMessage.setImmediate(true);

        lowerSideLayout.addComponent(textArea);
        lowerSideLayout.setComponentAlignment(textArea , Alignment.MIDDLE_CENTER);


        lowerSideLayout.addComponent(sendMessage);
        lowerSideLayout.setComponentAlignment(sendMessage , Alignment.MIDDLE_CENTER);

        contentVerticalLayout.addComponent(upperSideLayout);
        contentVerticalLayout.addComponent(lowerSideLayout);

        chatVL.addComponent(contentVerticalLayout);

        return chatVL;
    }

    private String reloadChatBox(CHAT chat){
        String result = chatBodyTextArea.getValue();

        try{
            QueryModel<MESSAGE> qm = new QueryModel<>(MESSAGE.class);
            qm.addWhere("chat" , ECriteria.EQUAL , chat.getId());
            List<MESSAGE> messageList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qm);
            Collections.reverse(messageList);
            for(MESSAGE message : messageList)
            {
                String owner = "";
                if(message.getFromFirst())
                {
                    owner = message.getChat().getFirst_user().getFirstName() + " " +  message.getChat().getFirst_user().getLastName();
                }else{
                    owner = message.getChat().getSecond_user().getFirstName() + " " +  message.getChat().getSecond_user().getLastName();
                }
                owner =  owner + ": ";
                result  = (result + owner +  message.getCreated().toString()+"\n" + message.getContent() + "\n");
            }
        }catch (NoResultException e)
        {
            chatBodyTextArea.setValue("Still no messages!");
        }catch (Exception e)
        {
            e.printStackTrace();
            Message.showError(e.getMessage());
        }

        return  result;
    }

    @Override
    public void handleEntityEvent(EntityEvent ev){
        super.handleEntityEvent(ev);
        if(ev.getAction()==EntityEvent.SELECTED) {

            if (ev.getSource().equals(usersCTW)) {
                if (mainLayout != null) {
                    mainLayout.removeAllComponents();
                    mainHSP.removeComponent(mainLayout);
                }
                if (usersCTW.getSelectedEntity() instanceof USERS) {

                    CHAT chat = new CHAT();
                    chat.setFirst_user(getCurrentUser());
                    chat.setSecond_user((USERS) usersCTW.getSelectedEntity());
                    chat = searchForChat(chat);
                    if(chat!= null)
                    {
                        if(chat.getAccepted())
                        {
                            chatBodyTextArea = new TextArea();
                            chatBodyTextArea.setValue("");
                            mainLayout = initChatView(chat);
                        }else if(chat.getFirst_user().getLogin().equals(getCurrentUser().getLogin())) {
                            mainLayout = initWaitingForResponseView();
                        }else{
                            mainLayout = initChatAcceptRequestView(chat);
                        }
                    }else{
                        mainLayout = initChatSendRequestView();
                    }

                    mainHSP.addComponent(mainLayout);
                }
            }
        }
    }

    private CHAT searchForChat(CHAT chat){
        List<ID> usersList = new ArrayList<>();
        usersList.add(chat.getFirst_user().getId());
        usersList.add(chat.getSecond_user().getId());
        QueryModel<CHAT> qm = new QueryModel<>(CHAT.class);
        qm.addWhereInOr("second_user" , usersList);
        qm.addWhereIn("first_user" , usersList);

        try {

            CHAT newChat = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(qm);
            if(newChat!=null)
                return newChat;
        }catch (NoResultException e)
        {
            Message.showError("You still don't started conversation with this user!");
        }
        catch (Exception e)
        {
            Message.showError(e.getMessage());
            e.printStackTrace();
        };
        return null;
    }

    public BigInteger getCurrentUserId() {
        if (AbstractWebUI.getInstance() instanceof AbstractSecureWebUI) {
            QueryModel<USERS> qm = new QueryModel<>(USERS.class);
            qm.addWhere("login" , ECriteria.EQUAL ,AbstractSecureWebUI.getInstance().getUsername());
            USERS u = null;
            try {
                u = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(qm);
                return u.getId().getId();
            }catch (NoResultException e)
            {
                Message.showError("No user!");
            }
            catch (Exception e)
            {
                Message.showError(e.getMessage());
                e.printStackTrace();
            };
        }
        return null;
    }

    public USERS getCurrentUser(){
        if (AbstractWebUI.getInstance() instanceof AbstractSecureWebUI) {
            QueryModel<USERS> qm = new QueryModel<>(USERS.class);
            qm.addWhere("login" , ECriteria.EQUAL ,AbstractSecureWebUI.getInstance().getUsername());
            USERS u = null;
            try {
                u = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(qm);
                return u;
            }catch (Exception e)
            {
                Message.showError(e.getMessage());
                e.printStackTrace();
            };
        }
        return null;
    }

    public boolean isChatExists(USERS first  , USERS second){
        List<ID> usersList = new ArrayList<>();
        usersList.add(first.getId());
        usersList.add(second.getId());
        QueryModel<CHAT> qm = new QueryModel<>(CHAT.class);
        qm.addWhereInOr("second_user" , usersList);
        qm.addWhereIn("first_user" , usersList);

        try {

            CHAT chat = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(qm);
            if(chat!=null)
                return true;
        }catch (Exception e)
        {
            Message.showError(e.getMessage());
            e.printStackTrace();
        };

        return false;
    }

}
