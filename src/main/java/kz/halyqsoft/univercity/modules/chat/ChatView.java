package kz.halyqsoft.univercity.modules.chat;

import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.univercity.CHAT;
import kz.halyqsoft.univercity.entity.beans.univercity.MESSAGE;
import kz.halyqsoft.univercity.modules.reports.MenuColumn;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.Entity;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.event.EntityEvent;
import org.r3a.common.entity.event.EntityListener;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.entity.tree.CommonTree;
import org.r3a.common.vaadin.AbstractSecureWebUI;
import org.r3a.common.vaadin.AbstractWebUI;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.Message;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;
import org.r3a.common.vaadin.widget.tree.CommonTreeWidget;
import org.r3a.common.vaadin.widget.tree.model.UOTreeModel;

import javax.persistence.NoResultException;
import java.math.BigInteger;
import java.util.*;

public class ChatView extends AbstractTaskView implements EntityListener  {

    private static String FRIENDS = "Друзья";
    private static String OUTCOMING_REQUEST = "Исходящие запросы";
    private static String INCOMING_REQUESTS = "Входящие запросы";

    private HorizontalSplitPanel mainHSP;

    private CommonTreeWidget usersCTW;
    private AbstractLayout mainLayout;
    private CHAT mainChat;
    private  TextArea chatBodyTextArea;
    private CustomSearchTreeVerticalLayout customSearchTreeVerticalLayout;
    private HierarchicalContainer hierarchicalContainer;
    public ChatView(AbstractTask task) throws Exception{
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        mainHSP = new HorizontalSplitPanel();

        mainHSP.setSplitPosition(25);
        mainHSP.isResponsive();


        final TreeTable menuTT = new TreeTable();

        hierarchicalContainer = new HierarchicalContainer();


        hierarchicalContainer.addItem(FRIENDS);
        hierarchicalContainer.setChildrenAllowed(FRIENDS, true);

        hierarchicalContainer.addItem(INCOMING_REQUESTS);
        hierarchicalContainer.setChildrenAllowed(INCOMING_REQUESTS, true);

        hierarchicalContainer.addItem(OUTCOMING_REQUEST);
        hierarchicalContainer.setChildrenAllowed(OUTCOMING_REQUEST, true);

        List<CHAT> chats = getCurrentUserChats();

        if(chats!=null)
        {

            for(CHAT singleChat : chats)
            {
                USERS anotherUser = getAnotherUserFromChat(singleChat);
                hierarchicalContainer.addItem(anotherUser.getLogin());
                hierarchicalContainer.setChildrenAllowed(anotherUser.getLogin(), false);

                if(singleChat.getAccepted()) {
                    hierarchicalContainer.setParent(anotherUser.getLogin(), FRIENDS);
                }else if(singleChat.getFirst_user().getLogin().equals(getCurrentUser().getLogin())){
                    hierarchicalContainer.setParent(anotherUser.getLogin(), OUTCOMING_REQUEST);
                }else{
                    Message.showInfo("INCOMING REQUESTS");
                    hierarchicalContainer.setParent(anotherUser.getLogin(), INCOMING_REQUESTS);
                }
            }
        }


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

        menuTT.addItemClickListener(new ItemClickEvent.ItemClickListener() {
            @Override
            public void itemClick(ItemClickEvent event) {
                if(event.getItem()!=null)
                {
                    if(!event.getItemId().equals(FRIENDS) && !event.getItemId().equals(OUTCOMING_REQUEST) && !event.getItemId().equals(INCOMING_REQUESTS))
                    {

                            if (mainLayout != null) {
                                mainLayout.removeAllComponents();
                                mainHSP.removeComponent(mainLayout);
                            }
                            if (getUserByLogin((String)event.getItemId())!=null) {

                                CHAT chat = new CHAT();
                                chat.setFirst_user(getCurrentUser());
                                chat.setSecond_user((USERS) getUserByLogin((String)event.getItemId()));
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
        });

        customSearchTreeVerticalLayout = new CustomSearchTreeVerticalLayout(menuTT);
        customSearchTreeVerticalLayout.setResponsive(true);
        customSearchTreeVerticalLayout.setImmediate(true);
        customSearchTreeVerticalLayout.getSearchButton().setCaption("Search");
        customSearchTreeVerticalLayout.getLoginLabel().setValue("Login:");
        customSearchTreeVerticalLayout.getSearchButton().addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent clickEvent) {
                String login = customSearchTreeVerticalLayout.getSearchTextField().getValue();
                if(!login.trim().equals("")){

                    USERS user = getUserByLogin(login);
                    ArrayList<Entity> usersArrayList = new ArrayList<>();
                    if(user!=null) {
                        usersArrayList.add(user);
                    }
                    UOTreeModel uoTreeModel=new UOTreeModel(usersArrayList);
                    usersCTW = new CommonTreeWidget(uoTreeModel);
                    usersCTW.setButtonVisible(IconToolbar.REFRESH_BUTTON , false);
                    usersCTW.setButtonVisible(IconToolbar.EDIT_BUTTON , false);
                    usersCTW.setButtonVisible(IconToolbar.DELETE_BUTTON , false);
                    usersCTW.setButtonVisible(IconToolbar.ADD_BUTTON , false);
                    usersCTW.setButtonVisible(IconToolbar.FILTER_BUTTON , false);
                    usersCTW.addEntityListener(new EntityListener() {
                        @Override
                        public void handleEntityEvent(EntityEvent ev){
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


                        @Override
                        public boolean preCreate(Object o, int i) {
                            return false;
                        }

                        @Override
                        public void onCreate(Object o, Entity entity, int i) {

                        }

                        @Override
                        public boolean onEdit(Object o, Entity entity, int i) {
                            return false;
                        }

                        @Override
                        public boolean onPreview(Object o, Entity entity, int i) {
                            return false;
                        }

                        @Override
                        public void beforeRefresh(Object o, int i) {

                        }

                        @Override
                        public void onRefresh(Object o, List<Entity> list) {

                        }

                        @Override
                        public void onFilter(Object o, QueryModel queryModel, int i) {

                        }

                        @Override
                        public void onAccept(Object o, List<Entity> list, int i) {

                        }

                        @Override
                        public boolean preSave(Object o, Entity entity, boolean b, int i) throws Exception {
                            return false;
                        }

                        @Override
                        public boolean preDelete(Object o, List<Entity> list, int i) {
                            return false;
                        }

                        @Override
                        public void onDelete(Object o, List<Entity> list, int i) {

                        }

                        @Override
                        public void deferredCreate(Object o, Entity entity) {

                        }

                        @Override
                        public void deferredDelete(Object o, List<Entity> list) {

                        }

                        @Override
                        public void onException(Object o, Throwable throwable) {

                        }
                    });


                    customSearchTreeVerticalLayout.setUsersCTW(usersCTW);
                }
            }
        });
        mainHSP.addComponent(customSearchTreeVerticalLayout);

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
        USERS anotherUser = getAnotherUserFromChat(chat);
        Label label = new Label(anotherUser.getFirstName() + " " + anotherUser.getLastName());


        upperSideLayout.addComponent(label);

        upperSideLayout.addComponent(chatBodyTextArea);
        upperSideLayout.setComponentAlignment(chatBodyTextArea, Alignment.TOP_LEFT);

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
                    Date utilDate = new Date();
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

    private BigInteger getCurrentUserId() {
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

    private USERS getCurrentUser(){
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

    private USERS getUserByLogin(String login){
            QueryModel<USERS> qm = new QueryModel<>(USERS.class);
            qm.addWhere("login" , ECriteria.EQUAL ,login);
            USERS u = null;
            try {
                u = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookupSingle(qm);
                return u;
            }catch (NoResultException e)
            {
                Message.showError("Not found!");
            }catch (Exception e)
            {
                Message.showError(e.getMessage());
                e.printStackTrace();
            };

        return null;
    }

    private boolean isChatExists(USERS first  , USERS second){
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

    private List<CHAT> getCurrentUserChats(){
        QueryModel<CHAT> qm = new QueryModel<>(CHAT.class);
        qm.addWhere("second_user" , ECriteria.EQUAL , getCurrentUser().getId());
        qm.addWhereOr("first_user" , ECriteria.EQUAL , getCurrentUser().getId());
        List<CHAT> chatList = null;
        try {

            chatList = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(qm);

        }catch (NoResultException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        };
        return chatList;
    }

    private USERS getAnotherUserFromChat(CHAT chat){
        if(chat.getFirst_user().getLogin().equals(getCurrentUser().getLogin()))
        {
            return chat.getSecond_user();
        }else{
            return  chat.getFirst_user();
        }
    }
}
