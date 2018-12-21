package kz.halyqsoft.univercity.modules.test;

import com.vaadin.ui.Button;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.ENTRANCE_YEAR;
import kz.halyqsoft.univercity.entity.beans.univercity.catalog.STUDY_YEAR;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.r3a.common.dblink.facade.CommonEntityFacadeBean;
import org.r3a.common.dblink.utils.SessionFacadeFactory;
import org.r3a.common.entity.ID;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.vaadin.view.AbstractTaskView;

public class TestView extends AbstractTaskView {

//    private TextField firstNameTF;
//    private TextField surnameTF;
//    private ComboBox rolesCB;
//
//    private final static String AUTH_KEY_FCM = "";//TODO
//    private final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";

    public TestView(AbstractTask task) throws Exception {
        super(task);
    }

    @Override
    public void initView(boolean b) throws Exception {
        try {
//            qwe(1);
//            qwe(2);
//            qwe(5);
//            qwe(9);
//            asd(1);
//            asd(2);
//            asd(3);
//            asd(4);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        GridWidget awardGW=new GridWidget(AWARD.class);
//        getContent().addComponent(awardGW);
        Button fixButton = new Button("fix");
        fixButton.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
//                try {
//                    QueryModel<M_USERS> usersQM = new QueryModel<>(M_USERS.class);
//                    usersQM.addOrder("id");
//                    List<M_USERS> users = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(usersQM);
//                    for (M_USERS user : users) {
////                        if (user.getFirstName().length() > 64) {
////                            user.setFirstName(user.getFirstName().substring(0, 64));
////                        }
////                        if (user.getLastName().length() > 64) {
////                            user.setLastName(user.getLastName().substring(0, 64));
////                        }
////                        if (user.getMiddleName().length() > 64) {
////                            user.setMiddleName(user.getMiddleName().substring(0, 64));
////                        }
////                        user.setFirstNameEN(transliterate(user.getFirstNameEN()));
////                        user.setLastNameEN(transliterate(user.getLastNameEN()));
////                        user.setMiddleNameEN(transliterate(user.getMiddleNameEN()));
////                        user.setPasswd("12345678");
////                        String code = CommonUtils.getCode("13");
////                        user.setCode(code);
////                        user.setLogin(code);
////
////                        SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).merge(user);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        });
//        mainOG.setValue(zxc);
//        mainOG.setItemEnabled(zxc,true);

        getContent().addComponent(fixButton);

    }

    public static String transliterate(String message) {
        char[] abcCyr = {' ', 'а', 'б', 'в', 'г', 'д', 'е', 'ё', 'ж', 'з', 'и', 'й', 'к', 'л', 'м', 'н', 'о', 'п', 'р', 'с', 'т', 'у', 'ф', 'х', 'ц', 'ч', 'ш', 'щ', 'ъ', 'ы', 'ь', 'э', 'ю', 'я', 'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
        String[] abcLat = {" ", "a", "b", "v", "g", "d", "e", "e", "zh", "z", "i", "y", "k", "l", "m", "n", "o", "p", "r", "s", "t", "u", "f", "h", "ts", "ch", "sh", "sch", "", "i", "", "e", "ju", "ja", "A", "B", "V", "G", "D", "E", "E", "Zh", "Z", "I", "Y", "K", "L", "M", "N", "O", "P", "R", "S", "T", "U", "F", "H", "Ts", "Ch", "Sh", "Sch", "", "I", "", "E", "Ju", "Ja", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < message.length(); i++) {
            for (int x = 0; x < abcCyr.length; x++) {
                if (message.charAt(i) == abcCyr[x]) {
                    builder.append(abcLat[x]);
                }
            }
        }
        return builder.toString();
    }
//    public static String sendPushNotification(String pushId)
//            throws IOException {
//        String result;
//        URL url = new URL(API_URL_FCM);
//        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//
//        conn.setUseCaches(false);
//        conn.setDoInput(true);
//        conn.setDoOutput(true);
//
//        conn.setRequestMethod("POST");
//        conn.setRequestProperty("Authorization", "key=" + AUTH_KEY_FCM);
//        conn.setRequestProperty("Content-Type", "application/json");
//
//        JSONObject json = new JSONObject();
//
//        json.put("to", pushId.trim());
//        JSONObject info = new JSONObject();
//        info.put("title", "notification title"); // Notification title
//        info.put("body", "message body"); // Notification
//        // body
//        json.put("notification", info);
//        try {
//            OutputStreamWriter wr = new OutputStreamWriter(
//                    conn.getOutputStream());
//            wr.write(json.toString());
//            wr.flush();
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(
//                    (conn.getInputStream())));
//
//            String output;
//            System.out.println("Output from Server .... \n");
//            while ((output = br.readLine()) != null) {
//                System.out.println(output);
//            }
//            result = "SUCCESS";
//        } catch (Exception e) {
//            e.printStackTrace();
//            result = "FAILURE";
//        }
//        System.out.println("GCM Notification is sent successfully");
//
//        return result;
//    }

    private static void qwe(int i) throws Exception {
        ENTRANCE_YEAR entranceYear = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookup(ENTRANCE_YEAR.class, ID.valueOf(i));
        System.out.println(entranceYear.toString() + ":" + CommonUtils.getStudyYearByEntranceYear(entranceYear));
    }
    private static void asd(int i) throws Exception {
        STUDY_YEAR studyYear = SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).
                lookup(STUDY_YEAR.class, ID.valueOf(i));
        System.out.println(studyYear.toString() + ":" + CommonUtils.getEntranceYearByStudyYear(studyYear));
    }

//    private static int getFirstMonday(int year, int month) {
//        Calendar cacheCalendar = Calendar.getInstance();
//        cacheCalendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
//        cacheCalendar.set(Calendar.DAY_OF_WEEK_IN_MONTH, 1);
//        cacheCalendar.set(Calendar.MONTH, month);
//        cacheCalendar.set(Calendar.YEAR, year);
//        return cacheCalendar.get(Calendar.DATE);
//    }

//    private static Integer getId(Set<Integer> ids, Integer id) {
//        if (ids.contains(id)) {
//            id = getId(ids, ++id);
//        } else {
//            ids.add(id);
//        }
//        return id;
//    }

//    private void ss() throws Exception {
//        firstNameTF = new TextField(getUILocaleUtil().getCaption("firstNameTF"));
//        firstNameTF.setImmediate(true);
//        firstNameTF.setRequired(true);
//
//        surnameTF = new TextField(getUILocaleUtil().getCaption("surnameTF"));
//        surnameTF.setImmediate(true);
//        surnameTF.setRequired(true);
//
//        HorizontalLayout mainHL = new HorizontalLayout();
//        mainHL.addComponent(firstNameTF);
//        mainHL.addComponent(surnameTF);
//        getContent().addComponent(mainHL);
//        getContent().setComponentAlignment(mainHL, Alignment.MIDDLE_CENTER);
//
//        rolesCB = new ComboBox(getUILocaleUtil().getCaption("rolesCB"));
//        rolesCB.setRequired(true);
//        QueryModel<USER_TYPE> typeQM = new QueryModel<>(USER_TYPE.class);
//        BeanItemContainer<USER_TYPE> typeB = new BeanItemContainer<>(USER_TYPE.class,
//                SessionFacadeFactory.getSessionFacade(CommonEntityFacadeBean.class).lookup(typeQM));
//        rolesCB.setContainerDataSource(typeB);
//
//        HorizontalLayout saveHL = new HorizontalLayout();
//        saveHL.addComponent(rolesCB);
//        getContent().addComponent(saveHL);
//        getContent().setComponentAlignment(saveHL, Alignment.MIDDLE_CENTER);
//
//        Button saveBtn = new Button(getUILocaleUtil().getCaption("saveBtn"));
//
//        saveBtn.addClickListener(new Button.ClickListener() {
//            @Override
//            public void buttonClick(Button.ClickEvent clickEvent) {
//                String firstName = firstNameTF.getValue();
//                String lastName = surnameTF.getValue();
//                USER_TYPE userType = (USER_TYPE) rolesCB.getValue();
//
//
//                if (firstName == null || firstName.isEmpty() || lastName == null || lastName.isEmpty()
//                        || userType == null) {
//                    Message.showError(getUILocaleUtil().getMessage("error.required.fields"));
//                } else {
//                    Message.showInfo(getUILocaleUtil().getCaption("firstNameTF") + ":" + firstName
//                            + "\n" + getUILocaleUtil().getCaption("surnameTF") + ":" + lastName
//                            + "\n" + getUILocaleUtil().getCaption("rolesCB") + ":" + userType);
//                }
//            }
//
//        });
//
//        Button clearBtn = new Button(getUILocaleUtil().getCaption("clearBtn"));
//
//        clearBtn.addClickListener(new Button.ClickListener() {
//            @Override
//            public void buttonClick(Button.ClickEvent clickEvent) {
//                firstNameTF.clear();
//                surnameTF.clear();
//                rolesCB.clear();
//            }
//
//        });
//
//
//        HorizontalLayout componentHL = new HorizontalLayout();
//        componentHL.addComponent(saveBtn);
//        componentHL.addComponent(clearBtn);
//        getContent().addComponent(componentHL);
//        getContent().setComponentAlignment(componentHL, Alignment.MIDDLE_CENTER);
//
//        Button usersToLower = new Button("to Lower");
//        usersToLower.addClickListener(new Button.ClickListener() {
//            @Override
//            public void buttonClick(Button.ClickEvent event) {
//                QueryModel<USERS> usersQM = new QueryModel<>(USERS.class);
////                usersQM.addWhere();
//            }
//        });
//        getContent().addComponent(usersToLower);
//    }
}
