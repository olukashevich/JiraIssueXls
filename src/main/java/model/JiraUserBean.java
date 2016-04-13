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
public class JiraUserBean
{
    private String login;

    private String password;

    private String projectKey;

    public String getLogin()
    {
        return login;
    }

    public void setLogin(String login)
    {
        this.login = login;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getProjectKey()
    {
        return projectKey;
    }

    public void setProjectKey(String projectKey)
    {
        this.projectKey = projectKey;
    }
}
