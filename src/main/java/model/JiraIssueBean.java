package model;

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
public class JiraIssueBean
{
    private String type;

    private String summary;

    private String assignee;

    private String linkToWiki;

    private String epicLink;

    private String fixVersion;

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public String getSummary()
    {
        return summary;
    }

    public void setSummary(String summary)
    {
        this.summary = summary;
    }

    public String getAssignee()
    {
        return assignee;
    }

    public void setAssignee(String assignee)
    {
        this.assignee = assignee;
    }

    public String getLinkToWiki()
    {
        return linkToWiki;
    }

    public void setLinkToWiki(String linkToWiki)
    {
        this.linkToWiki = linkToWiki;
    }

    public String getEpicLink()
    {
        return epicLink;
    }

    public void setEpicLink(String epicLink)
    {
        this.epicLink = epicLink;
    }

    public String getFixVersion()
    {
        return fixVersion;
    }

    public void setFixVersion(String fixVersion)
    {
        this.fixVersion = fixVersion;
    }
}
