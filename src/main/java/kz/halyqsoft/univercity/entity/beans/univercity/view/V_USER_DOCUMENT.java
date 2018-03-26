package kz.halyqsoft.univercity.entity.beans.univercity.view;

import java.util.Date;

/**
 * @author Omarbek
 * @created 26.04.2017.
 */
public class V_USER_DOCUMENT {

    private long id;

    private String documentNumber;

    private Date issueDate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }
}
