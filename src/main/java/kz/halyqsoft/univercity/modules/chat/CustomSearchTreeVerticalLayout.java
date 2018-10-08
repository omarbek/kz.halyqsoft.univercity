package kz.halyqsoft.univercity.modules.chat;

import com.vaadin.ui.*;
import org.r3a.common.entity.Entity;
import org.r3a.common.vaadin.widget.toolbar.IconToolbar;
import org.r3a.common.vaadin.widget.tree.CommonTreeWidget;
import org.r3a.common.vaadin.widget.tree.model.UOTreeModel;

import java.util.ArrayList;

public class CustomSearchTreeVerticalLayout extends VerticalLayout{
    private TreeTable treeTable;
    private HorizontalLayout horizontalLayout;
    private TextField searchTextField;
    private CommonTreeWidget usersCTW;
    private Button searchButton;
    private Label loginLabel;
    private void init(){
        horizontalLayout.addComponent(loginLabel);
        horizontalLayout.addComponent(searchTextField);
        horizontalLayout.addComponent(searchButton);

        addComponent(horizontalLayout);

        addComponent(usersCTW);
        usersCTW.setImmediate(true);
        usersCTW.setButtonVisible(IconToolbar.REFRESH_BUTTON, false);
        usersCTW.setButtonVisible(IconToolbar.DELETE_BUTTON, false);


        addComponent(treeTable);

        horizontalLayout.setImmediate(true);
        searchTextField.setImmediate(true);
        searchButton.setImmediate(true);
        treeTable.setImmediate(true);
        this.setImmediate(true);
    }

    public CustomSearchTreeVerticalLayout(TreeTable treeTable){
        horizontalLayout = new HorizontalLayout();
        this.treeTable = treeTable;
        searchButton = new Button();
        searchTextField = new TextField();
        loginLabel = new Label();

    }

    public CustomSearchTreeVerticalLayout(){
        horizontalLayout = new HorizontalLayout();

        treeTable = new TreeTable();
        searchButton = new Button();
        searchTextField = new TextField();
        loginLabel = new Label();
        init();
    }

    public CommonTreeWidget getUsersCTW() {
        return usersCTW;
    }

    public void setUsersCTW(CommonTreeWidget usersCTW) {
        this.replaceComponent(this.usersCTW , usersCTW);
        this.usersCTW = usersCTW;

        init();
    }

    public Label getLoginLabel() {
        return loginLabel;
    }

    public void setLoginLabel(Label loginLabel) {
        this.loginLabel = loginLabel;
    }

    public TreeTable getTreeTable() {
        return treeTable;
    }

    public void setTreeTable(TreeTable treeTable) {
        this.treeTable = treeTable;
    }

    public HorizontalLayout getHorizontalLayout() {
        return horizontalLayout;
    }

    public void setHorizontalLayout(HorizontalLayout horizontalLayout) {
        this.horizontalLayout = horizontalLayout;
    }

    public TextField getSearchTextField() {
        return searchTextField;
    }

    public void setSearchTextField(TextField searchTextField) {
        this.searchTextField = searchTextField;
    }

    public Button getSearchButton() {
        return searchButton;
    }

    public void setSearchButton(Button searchButton) {
        this.searchButton = searchButton;
    }
}
