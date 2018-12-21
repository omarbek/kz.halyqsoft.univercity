package kz.halyqsoft.univercity.modules.messagecenter;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.entity.beans.USERS;
import kz.halyqsoft.univercity.entity.beans.USER_ROLES;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.entity.query.QueryModel;
import org.r3a.common.entity.query.from.EJoin;
import org.r3a.common.entity.query.from.FromItem;
import org.r3a.common.entity.query.where.ECriteria;
import org.r3a.common.vaadin.view.AbstractTaskView;
import kz.halyqsoft.univercity.entity.beans.ROLES;

import java.util.*;

/**
 * @author Assylkhan
 * on 20.12.2018
 * @project kz.halyqsoft.univercity
 */
public class MessageCenterView extends AbstractTaskView{
    private TextArea messageBody;
    private TextField messageTitle;
    private ComboBox rolesCB;
    private static final String HOST ="https://fcm.googleapis.com/fcm/send";
    private static final String AUTH ="key=AIzaSyAQ81fNK1p4eU58BdJ0Ym9ZkDHux3X1jxE";
    public MessageCenterView(AbstractTask task) throws Exception {
        super(task);
        getContent().setSpacing(true);
        getContent().setSizeFull();
        getContent().setDefaultComponentAlignment(Alignment.MIDDLE_CENTER);
    }

    @Override
    public void initView(boolean b) throws Exception {
        rolesCB = new ComboBox();
        rolesCB.setCaption(getUILocaleUtil().getEntityLabel(ROLES.class));
        QueryModel<ROLES> rolesQueryModel = new QueryModel<ROLES>(ROLES.class);
        BeanItemContainer bic = new BeanItemContainer(ROLES.class , CommonUtils.getQuery().lookup(rolesQueryModel));
        rolesCB.setContainerDataSource(bic);
        VerticalLayout mainVL = new VerticalLayout();
        mainVL.setSizeFull();
        mainVL.setSpacing(true);
        mainVL.setMargin(true);

        Button sendMessage = new Button(getUILocaleUtil().getCaption("send"));
        sendMessage.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {

                try{
                    QueryModel<USERS> usersQM = new QueryModel<>(USERS.class);
                    usersQM.addWhereNotNull("pushId");
                    if(rolesCB.getValue()!=null){
                        FromItem fi = usersQM.addJoin(EJoin.LEFT_JOIN , "id" , USER_ROLES.class, "user");
                        usersQM.addWhereAnd(fi , "role" , ECriteria.EQUAL , ((ROLES)rolesCB.getValue()).getId());
                    }

                    List<USERS> users = CommonUtils.getQuery().lookup(usersQM);
                    Set<USERS> usersSet = new HashSet<>(users);
                    for(USERS user: usersSet){
                        if(!user.getPushId().isEmpty()){
                            JSONObject json = new JSONObject();
                            json.put("to" , user.getPushId());
                            Map<String,String> map = new HashMap<>();
                            map.put("title" , messageTitle.getValue());
                            map.put("message" , messageBody.getValue());
                            json.put("notification" , map);
                            CloseableHttpClient httpClient = HttpClientBuilder.create().build();
                            HttpPost request = new HttpPost(HOST);
                            StringEntity params = new StringEntity(json.toString(),"UTF-8");
                            request.addHeader("content-type", "application/json; charset=UTF-8");
                            request.addHeader("Authorization", AUTH);
                            request.addHeader("Accept-Charset", "UTF-8");
                            request.addHeader("charset", "UTF-8");
                            request.setEntity(params);
                            ResponseHandler<String> responseHandler=new BasicResponseHandler();
                            String responseBody = httpClient.execute(request, responseHandler);
                            JSONObject response=new JSONObject(responseBody);
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        messageBody = new TextArea();
        messageBody.setCaption(getUILocaleUtil().getCaption("message"));
        messageBody.setSizeFull();
        messageBody.setRows(10);
        messageBody.setWordwrap(true);
        messageBody.setRequired(true);
        messageTitle = new TextField();
        messageTitle.setCaption(getUILocaleUtil().getCaption("title"));
        messageTitle.setNullRepresentation("");
        messageTitle.setRequired(true);
        messageTitle.setWidth("100%");
        HorizontalLayout mainHL = new HorizontalLayout();
        mainHL.setSpacing(true);
        mainHL.setSizeFull();
        mainHL.addComponent(messageTitle);
        mainHL.addComponent(rolesCB);
        mainVL.addComponent(mainHL);
        mainVL.addComponent(messageBody);
        mainVL.addComponent(sendMessage);

        Panel panel = new Panel();
        panel.setContent(mainVL);
        panel.setWidth("70%");

        getContent().addComponent(panel);

    }
}
