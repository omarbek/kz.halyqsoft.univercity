package kz.halyqsoft.univercity.modules.reports;


import com.vaadin.data.Property;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.ui.*;
import kz.halyqsoft.univercity.utils.CommonUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.r3a.common.entity.beans.AbstractTask;
import org.r3a.common.vaadin.view.AbstractTaskView;
import org.r3a.common.vaadin.widget.dialog.Message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Omarbek
 * @created on 18.04.2018
 */
public class ReportView extends AbstractTaskView {

    private VerticalLayout filesVL;
    private VerticalLayout paramsVL;
    private Button printB;
    private FileDownloader fileDownloader;
    private boolean showMessage;

    private List<Param> paramList;
    private Map<Param, Component> componentMap;
    private Map<String, Map<String, Object>> foldersAndFilesMap;
    private Map<String, String> namesMap = new HashMap<>();

    private static final String REPORT_NAME = "report.xlsx";
    private static final String ANY_STRING = "string";

    private static final String INTEGER = "class java.lang.Integer";
    private static final String STRING = "class java.lang.String";
    private static final String BOOLEAN = "class java.lang.Boolean";
    private static final String DATE = "Date";

    private static final String RESULT = "result";
    private static final String MESSAGE = "message";

    private static final String SELECTION_COMMITTEE = "selection_committee";
    private static final String B = "b";

    private static final String JASPER_REPORT_URL = "http://78.40.108.39:8080/jasperreport/GenerateReport";
//    private static final String JASPER_REPORT_URL = "http://localhost:8080/jasperreport/GenerateReport";

    public ReportView(AbstractTask task) throws Exception {
        super(task);

        namesMap.put(SELECTION_COMMITTEE, "Приемная коммиссия");
        namesMap.put(B, "Другие");
    }

    @Override
    public void initView(boolean readOnly) throws Exception {
        HorizontalLayout mainHL = new HorizontalLayout();
        mainHL.setSizeFull();
        mainHL.setSpacing(true);

        filesVL = new VerticalLayout();
        filesVL.setSizeFull();

        paramsVL = new VerticalLayout();
        paramsVL.setSizeFull();

        initFoldersAndFilesMenu();

        mainHL.addComponent(filesVL);
        mainHL.setExpandRatio(filesVL, 0.35f);

        mainHL.addComponent(paramsVL);
        mainHL.setExpandRatio(paramsVL, 0.65f);
        mainHL.setComponentAlignment(paramsVL, Alignment.TOP_CENTER);

        getContent().addComponent(mainHL);
    }

    private void initFoldersAndFilesMenu() throws IOException, JSONException {
        foldersAndFilesMap = new HashMap<>();

        JSONObject treeJson = JsonFromUrl.readJsonFromUrl(JASPER_REPORT_URL + "?type=get_tree");
        Iterator keys = treeJson.keys();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            if (!(RESULT.equals(key) || MESSAGE.equals(key))) {
                JSONObject treeJsonObject = treeJson.getJSONObject(key);
                if (treeJsonObject != null) {
                    Map<String, Object> valueMap = JsonFromUrl.jsonToMap(treeJsonObject);
                    foldersAndFilesMap.put(key, valueMap);
                }
            }
        }

        final TreeTable foldersAndFilesTT = new TreeTable();

        HierarchicalContainer hierarchicalContainer = new HierarchicalContainer();

        for (Map.Entry<String, Map<String, Object>> entry : foldersAndFilesMap.entrySet()) {
            String dirName = namesMap.get(entry.getKey());
            hierarchicalContainer.setParent(dirName, null);
            hierarchicalContainer.setChildrenAllowed(dirName, true);
            hierarchicalContainer.addItem(dirName);

            for (Map.Entry<String, Object> value : entry.getValue().entrySet()) {
                String fileName = value.getKey();
                hierarchicalContainer.addItem(fileName);
                hierarchicalContainer.setParent(fileName, dirName);
                hierarchicalContainer.setChildrenAllowed(fileName, false);
            }
        }
        foldersAndFilesTT.setContainerDataSource(hierarchicalContainer);

        foldersAndFilesTT.setSizeFull();//
        foldersAndFilesTT.addStyleName("schedule");
        foldersAndFilesTT.setSelectable(true);
        foldersAndFilesTT.setMultiSelect(false);
        foldersAndFilesTT.setNullSelectionAllowed(false);
        foldersAndFilesTT.setImmediate(true);
        foldersAndFilesTT.setColumnReorderingAllowed(false);
        foldersAndFilesTT.setPageLength(27);
        MenuColumn menuColumn = new MenuColumn();
        foldersAndFilesTT.addGeneratedColumn("report", menuColumn);
        foldersAndFilesTT.setColumnHeader("report", getUILocaleUtil().getCaption("template.report"));//TODO kk,ru
        filesVL.addComponent(foldersAndFilesTT);
        foldersAndFilesTT.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if (event != null && event.getProperty() != null && event.getProperty().getValue() != null) {
                    String eventName = event.getProperty().getValue().toString();
                    boolean showReports = false;
                    String reportName = "";
                    for (Map.Entry<String, Map<String, Object>> entry : foldersAndFilesMap.entrySet()) {
                        if (entry.getValue().containsKey(eventName)) {
                            reportName = entry.getValue().get(eventName).toString();
                            showReports = true;
                            break;
                        }
                    }
                    if (showReports) {
                        try {
                            paramList = new ArrayList<>();
                            componentMap = new HashMap<>();
                            showMessage = true;

                            JSONObject paramsJson = JsonFromUrl.readJsonFromUrl(JASPER_REPORT_URL + "?type=get_params&report_name=" + reportName);
                            Iterator keys = paramsJson.keys();

                            List<JSONObject> valueList = new ArrayList<>();
                            while (keys.hasNext()) {
                                String key = (String) keys.next();
                                if (!(RESULT.equals(key) || MESSAGE.equals(key))) {
                                    JSONArray paramsJsonArray = paramsJson.getJSONArray(key);
                                    if (paramsJsonArray != null) {
                                        for (int i = 0; i < paramsJsonArray.length(); i++) {
                                            valueList.add(paramsJsonArray.getJSONObject(i));
                                        }
                                    }
                                }
                            }
                            for (JSONObject jsonValue : valueList) {
                                Param param = new Param();
                                param.setDescription(jsonValue.getString("description"));
                                param.setName(jsonValue.getString("name"));
                                param.setType(jsonValue.getString("type"));
                                param.setReportName(reportName);

                                JSONArray dataArray = jsonValue.getJSONArray("dataList");
                                List<Object> dataList = new ArrayList<>();
                                for (int i = 0; i < dataArray.length(); i++) {
                                    dataList.add(dataArray.get(i).toString());
                                }
                                param.setDataList(dataList);
                                paramList.add(param);
                            }

                            initParams();

                            extend(reportName);
                        } catch (Exception e) {
                            CommonUtils.showMessageAndWriteLog("Unable to get params from jasper report", e);
                        }
                    }
                }
            }
        });
    }

    private void initParams() throws Exception {
        paramsVL.removeAllComponents();

        paramsVL.setSpacing(true);
        paramsVL.setWidthUndefined();
        paramsVL.addStyleName("report-params");

        paramsVL.addComponent(getParams());

        HorizontalLayout buttonsHL = new HorizontalLayout();
        buttonsHL.setSpacing(true);

        printB = new Button();
        printB.setCaption(getUILocaleUtil().getCaption("print"));
        printB.setWidth(120, Unit.PIXELS);
        printB.setIcon(new ThemeResource("img/button/printer.png"));
        printB.addStyleName("print");
        printB.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                if (showMessage) {
                    Message.showError(getUILocaleUtil().getMessage("fill.all.fields"));//TODO kk,ru
                }
            }
        });
        buttonsHL.addComponent(printB);

        Button clearB = new Button();
        clearB.setCaption(getUILocaleUtil().getCaption("clear"));
        clearB.setIcon(new ThemeResource("img/button/erase.png"));
        clearB.addStyleName("clear");
        clearB.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(Button.ClickEvent event) {
                try {
                    initParams();
                    showMessage = true;
                } catch (Exception e) {
                    CommonUtils.showMessageAndWriteLog("Unable to clear params", e);
                }
            }
        });
        buttonsHL.addComponent(clearB);

        paramsVL.addComponent(buttonsHL);
        paramsVL.setComponentAlignment(buttonsHL, Alignment.MIDDLE_CENTER);
    }

    private StreamResource getStreamResource(final String reportName) {
        return new StreamResource(new StreamResource.StreamSource() {
            @Override
            public InputStream getStream() {
                byte[] bytes = new byte[0];
                try {
                    Map<String, Object> reportParamMap = fillReportParamMap(reportName);
                    HttpClient client = HttpClientBuilder.create().build();
                    JSONObject sendingJson = new JSONObject();
                    HttpPost post = new HttpPost(JASPER_REPORT_URL);
                    for (Map.Entry<String, Object> entry : reportParamMap.entrySet()) {
                        sendingJson.put(entry.getKey(), entry.getValue());
                    }
                    StringEntity se = new StringEntity(sendingJson.toString(), "UTF-8");
                    se.setContentType(new BasicHeader(HTTP.CONTENT_TYPE, "application/sendingJson"));
                    post.setEntity(se);
                    HttpResponse response = client.execute(post);
                    HttpEntity entity = response.getEntity();

                    String sendingJsonByString = EntityUtils.toString(entity);
                    JSONObject jsonObject = new JSONObject(sendingJsonByString);
                    bytes = Base64.decodeBase64(jsonObject.get("byteArrayOutputStream").toString());

                } catch (Exception e) {
                    CommonUtils.showMessageAndWriteLog("Unable send params or get bytes", e);
                }
                return new ByteArrayInputStream(bytes);
            }
        }, REPORT_NAME);
    }

    private FormLayout getParams() throws Exception {
        FormLayout formLayout = new FormLayout();
        formLayout.setSpacing(true);
        formLayout.setWidthUndefined();

        Collections.sort(paramList, new Comparator<Param>() {
            @Override
            public int compare(Param o1, Param o2) {
                return (o1.getName()).compareTo(o2.getName());
            }
        });
        for (final Param param : paramList) {
            String caption = param.getDescription();
            String classType = param.getType();
            if (param.getDataList() != null && !param.getDataList().isEmpty()) {
                final ComboBox paramCB = new ComboBox();
                paramCB.setCaption(caption);
                paramCB.setNullSelectionAllowed(false);
                List list = param.getDataList();
                for (Object object : list) {
                    paramCB.addItem(object);
                }
                formLayout.addComponent(paramCB);

                paramCB.addValueChangeListener(initValueChangeListener(param, paramCB));
                componentMap.put(param, paramCB);
            } else {
                switch (classType) {
                    case BOOLEAN:
                        final CheckBox paramChB = new CheckBox();
                        paramChB.setCaption(caption);
                        formLayout.addComponent(paramChB);

                        paramChB.addValueChangeListener(initValueChangeListener(param, paramChB));
                        componentMap.put(param, paramChB);
                        break;
                    case DATE:
                        final DateField paramDF = new DateField();
                        paramDF.setCaption(caption);
                        paramDF.setValue(new Date());
                        formLayout.addComponent(paramDF);

                        paramDF.addValueChangeListener(initValueChangeListener(param, paramDF));
                        componentMap.put(param, paramDF);
                        break;
                    default:
                        final TextField paramTF = new TextField();
                        paramTF.setCaption(caption);
                        formLayout.addComponent(paramTF);

                        paramTF.addValueChangeListener(initValueChangeListener(param, paramTF));
                        componentMap.put(param, paramTF);
                        break;
                }
            }
        }

        return formLayout;
    }

    private Property.ValueChangeListener initValueChangeListener(final Param param, final Component paramC) {
        return new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if (printB.getExtensions().size() > 0)
                    printB.removeExtension(fileDownloader);

                if (event != null && event.getProperty() != null) {
                    String value = event.getProperty().getValue() != null ? event.getProperty().getValue().toString() : "";
                    boolean boolValue = Boolean.valueOf(value);
                    Date dateValue = new Date();

                    if (INTEGER.equals(param.getType())) {
                        if (!NumberUtils.isNumber(value)) {
                            Message.showError(getUILocaleUtil().getMessage("write.number"));//TODO kk,ru
                            return;
                        }
                    } else if (DATE.equals(param.getType())) {
                        dateValue = (Date) event.getProperty().getValue();
                    }

                    if (param.getDataList() != null && !param.getDataList().isEmpty()) {
                        ((ComboBox) paramC).setValue(value);
                    } else {
                        switch (param.getType()) {
                            case BOOLEAN:
                                ((CheckBox) paramC).setValue(boolValue);
                                break;
                            case DATE:
                                ((DateField) paramC).setValue(dateValue);
                                break;
                            default:
                                ((TextField) paramC).setValue(value);
                                break;
                        }
                    }

                    int filledParams = 0;
                    for (Map.Entry<Param, Component> componentEntry : componentMap.entrySet()) {
                        String paramValue = "";
                        if (componentEntry.getKey().getDataList() != null && !componentEntry.getKey().getDataList().isEmpty()) {
                            if (((ComboBox) componentEntry.getValue()).getValue() != null) {
                                paramValue = ((ComboBox) componentEntry.getValue()).getValue().toString();
                            }
                        } else {
                            switch (componentEntry.getKey().getType()) {
                                case DATE:
                                    Date date = ((DateField) componentEntry.getValue()).getValue();
                                    if (date != null) {
                                        paramValue = ANY_STRING;
                                    } else {
                                        showMessage = true;
                                    }
                                    break;
                                case BOOLEAN:
                                    paramValue = ANY_STRING;
                                    break;
                                default:
                                    paramValue = ((TextField) componentEntry.getValue()).getValue();
                                    break;
                            }
                        }
                        if (!paramValue.isEmpty()) {
                            filledParams++;
                        }
                    }

                    if (filledParams == componentMap.size()) {
                        extend(param.getReportName());
                    }
                }
            }
        };
    }

    private void extend(String reportName) {
        StreamResource res = getStreamResource(reportName);
        fileDownloader = new FileDownloader(res);
        fileDownloader.extend(printB);
        showMessage = false;
    }

    private Map<String, Object> fillReportParamMap(String reportName) {
        Map<String, Object> reportParamMap = new HashMap<>();
        for (Param param : paramList) {
            String key = param.getName();
            String value = "";
            boolean boolValue = false;
            if (param.getDataList() != null && !param.getDataList().isEmpty()) {
                value = ((ComboBox) componentMap.get(param)).getValue().toString();
            } else {
                switch (param.getType()) {
                    case BOOLEAN:
                        boolValue = ((CheckBox) componentMap.get(param)).getValue();
                        break;
                    case DATE:
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                        Date dateValue = ((DateField) componentMap.get(param)).getValue();
                        value = df.format(dateValue);
                        break;
                    default:
                        value = ((TextField) componentMap.get(param)).getValue();
                        break;
                }
            }

            Object objectValue = new Object();

            switch (param.getType()) {
                case INTEGER:
                    objectValue = Integer.valueOf(value);
                    break;
                case STRING:
                case DATE:
                    objectValue = value;
                    break;
                case BOOLEAN:
                    objectValue = boolValue;
                    break;
            }
            reportParamMap.put(key, objectValue);
        }
        reportParamMap.put("reportName", reportName);
        return reportParamMap;
    }

    public class Param {

        private String name;
        private String description;
        private String type;
        private List<Object> dataList;
        private String reportName;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public List<Object> getDataList() {
            return dataList;
        }

        public void setDataList(List<Object> dataList) {
            this.dataList = dataList;
        }

        public String getReportName() {
            return reportName;
        }

        public void setReportName(String reportName) {
            this.reportName = reportName;
        }
    }

}
