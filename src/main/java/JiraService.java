import com.atlassian.httpclient.api.HttpClient;
import com.atlassian.httpclient.api.Response;
import com.atlassian.httpclient.api.ResponsePromise;
import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.domain.BasicIssue;
import com.atlassian.jira.rest.client.api.domain.IssueFieldId;
import com.atlassian.jira.rest.client.api.domain.User;
import com.atlassian.jira.rest.client.api.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.api.domain.input.ComponentInput;
import com.atlassian.jira.rest.client.api.domain.input.FieldInput;
import com.atlassian.jira.rest.client.api.domain.input.IssueInput;
import com.atlassian.jira.rest.client.auth.BasicHttpAuthenticationHandler;
import com.atlassian.jira.rest.client.internal.async.AsynchronousComponentRestClient;
import com.atlassian.jira.rest.client.internal.async.AsynchronousHttpClientFactory;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.jira.rest.client.internal.async.AsynchronousUserRestClient;
import com.sun.javafx.binding.StringFormatter;

import org.apache.http.HttpConnection;
import org.apache.http.HttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.json.Json;
import javax.json.stream.JsonParser;

import model.JiraIssueBean;
import model.JiraUserBean;

/**
 * TODO: write a brief summary fragment.
 * <p>
 * TODO: write a detailed description.
 * <p>
 * Created on 3/31/2016.
 * <p>
 *
 * @author Olga Lukashevich
 */
public class JiraService
{
   // private static final URI JIRA_SERVER_URI = URI.create("http://srv-jira-clone.softeq.com");
   // private static final String JIRA_SERVER_URI_ISSUE = "%s/rest/api/latest/issue/";
    private static final String JIRA_SERVER_URI_PART_LINK_REMOTE = "/remotelink";
    private static final String ISSUE_TYPE_TASK = "Task";
    private static final String ISSUE_TYPE_SUB_TASK = "Sub-task";
    private static final String ISSUE_SUB_TASK_PARENT = "parent";
    //private static final String ISSUE_CUSTOM_FIELD = "customfield_";
    //private static final String ISSUE_EPIC_LINK_ID = "11917";
    private static final String REQUEST_CONTENT_TYPE_HEADER = "Content-Type";
    private static final String REQUEST_JSON_TYPE = "application/json";
    private static String ISSUE_REMOTE_LINK_JSON = "{\"object\":{\"url\":\"%s\",\"title\":\"Link on wiki\"}}";
    private static String ISSUE_EPIC_LINK_JSON = "{\"fields\":{\"customfield_11917\":\"%s\"}}";
    //private static String issueCreatePath = "/rest/api/2/issue/";
    //private static String JIRA_SERVER_URI_SEARCH = "http://srv-jira-clone.softeq.com/rest/api/latest/search?jql=\\c\\f\\[11911\\]~\"%1$s\"%20AND%20project=%2$s%20AND%20issueType=Epic";//jql=assignee=olukashevich
    //private static String JIRA_SERVER_URI_SEARCH = "http://srv-jira-clone.softeq.com/rest/api/2/search?jql=summary\\~\"OlalalaEpic4\"%20AND%20project=FLEETMG%20AND%20issueType=Epic";
    //http://srv-jira-clone.softeq.com/rest/api/2/issue/createmeta?issuetypeNames=Bug&expand=projects.issuetypes.fields
    public void createAndPostIssueFromXlsFile(String jiraUrl, String filePath) throws IOException
    {
        final URI JIRA_SERVER_URI = URI.create(jiraUrl);
        final String JIRA_SERVER_URI_ISSUE = String.format("%s/rest/api/latest/issue/", jiraUrl);
        JiraUserBean jiraUser = XlsService.readJiraUserFromXlsFile(filePath);
        List<JiraIssueBean> jiraIssues = XlsService.readJiraIssuesFromXlsFile(filePath);
        if (jiraUser != null && jiraIssues != null && !jiraIssues.isEmpty())
        {
            final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
            final JiraRestClient jiraRestClient = factory
                    .createWithBasicHttpAuthentication(JIRA_SERVER_URI, jiraUser.getLogin(), jiraUser.getPassword());
            final HttpClient httpClient = new AsynchronousHttpClientFactory().
                    createClient(JIRA_SERVER_URI, new BasicHttpAuthenticationHandler(jiraUser.getLogin(), jiraUser.getPassword()));
            final IssueRestClient issueRestClient = jiraRestClient.getIssueClient();
            String parentKey = null;
            IssueInput newIssue = null;
            BasicIssue basicIssue = null;
            for (JiraIssueBean jiraIssue : jiraIssues)
            {
                if (ISSUE_TYPE_TASK.equals(jiraIssue.getType()))
                {
                    newIssue = createIssueInput(jiraUser.getProjectKey(), jiraIssue, null);
                    basicIssue = issueRestClient.createIssue(newIssue).claim();
                    parentKey = basicIssue.getKey();
                    System.out.println(basicIssue);
                }
                else if (ISSUE_TYPE_SUB_TASK.equals(jiraIssue.getType()))
                {
                    newIssue = createIssueInput(jiraUser.getProjectKey(), jiraIssue, parentKey);
                    basicIssue = issueRestClient.createIssue(newIssue).claim();
                    System.out.println(basicIssue);
                }
                if (jiraIssue.getLinkToWiki() != null)
                {
                    StringBuilder stringBuilder = new StringBuilder(JIRA_SERVER_URI_ISSUE);
                    stringBuilder.append(basicIssue.getKey()).append(JIRA_SERVER_URI_PART_LINK_REMOTE);
                    URI uriForRemoteLink = URI.create(stringBuilder.toString());
                    httpClient.newRequest(uriForRemoteLink)
                            .setEntity(String.format(ISSUE_REMOTE_LINK_JSON, jiraIssue.getLinkToWiki()))
                            .setHeader(REQUEST_CONTENT_TYPE_HEADER, REQUEST_JSON_TYPE)
                            .post();
                }
                if (jiraIssue.getEpicLink() != null && ISSUE_TYPE_TASK.equals(jiraIssue.getType()))
                {
                    StringBuilder stringBuilder = new StringBuilder(JIRA_SERVER_URI_ISSUE);
                    stringBuilder.append(basicIssue.getKey());
                    URI uriForEditIssue = URI.create(stringBuilder.toString());
                    httpClient.newRequest(uriForEditIssue)
                            .setEntity(String.format(ISSUE_EPIC_LINK_JSON, jiraIssue.getEpicLink()))
                            .setHeader(REQUEST_CONTENT_TYPE_HEADER, REQUEST_JSON_TYPE)
                            .put();
                }
            }
        }
    }

    public IssueInput createIssueInput(String projectKey, JiraIssueBean jiraIssue, String parentKey)
    {
        List<FieldInput> fieldInputList = new ArrayList<FieldInput>();
        FieldInput[] fieldInputArray = new FieldInput[0];
        fieldInputList.add(new FieldInput(IssueFieldId.PROJECT_FIELD, ComplexIssueInputFieldValue.with("key", projectKey)));
        fieldInputList.add(new FieldInput(IssueFieldId.ISSUE_TYPE_FIELD, ComplexIssueInputFieldValue.with("name", jiraIssue.getType())));
        if (ISSUE_TYPE_SUB_TASK.equals(jiraIssue.getType()))
        {
            fieldInputList.add(new FieldInput(ISSUE_SUB_TASK_PARENT, ComplexIssueInputFieldValue.with("key", parentKey)));
        }
        fieldInputList.add(new FieldInput(IssueFieldId.SUMMARY_FIELD, jiraIssue.getSummary()));
        fieldInputList.add(new FieldInput(IssueFieldId.ASSIGNEE_FIELD, ComplexIssueInputFieldValue.with("name", jiraIssue.getAssignee())));
        if (jiraIssue.getFixVersion() != null)
        {
            List<Object> fixVersions = new ArrayList<Object>();
            fixVersions.add(ComplexIssueInputFieldValue.with("name", jiraIssue.getFixVersion()));
            fieldInputList.add(new FieldInput(IssueFieldId.FIX_VERSIONS_FIELD, fixVersions));
        }
        IssueInput issueInput = IssueInput.createWithFields(fieldInputList.toArray(fieldInputArray));
        return issueInput;
    }

    /*private String parseIssueKeyFromJson(String issueJsonAsString)
    {

        JsonParser parser = Json.createParser(new BufferedReader(new StringReader(issueJsonAsString)));
        JsonParser.Event event;
        do
        {
            event = parser.next();
        }
        while (!(JsonParser.Event.KEY_NAME.equals(event) && "issues".equals(parser.getString())));
        event = parser.next(); // START_ARRAY
        event = parser.next(); //START_OBJECT
        do
        {
            event = parser.next();
        }
        while (!(JsonParser.Event.KEY_NAME.equals(event) && "key".equals(parser.getString())));
        event = parser.next(); //key value
        return event.toString();
    }*/


}
