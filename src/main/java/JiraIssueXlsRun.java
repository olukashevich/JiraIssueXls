/*
 * Copyright (c) 2016, onlinedruck.
 * Developed by Softeq Development Corp. for BC MEDIA AG.
 */

import java.io.Console;
import java.util.Scanner;

/**
 * TODO: write a brief summary fragment.
 * <p/>
 * TODO: write a detailed description.
 * <p/>
 * Created on 3/30/2016.
 * <p/>
 *
 * @author Olga Lukashevich
 */
public class JiraIssueXlsRun
{
    public static void main(String[] args) throws Exception
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter JIRA URL (like \'http://srv-jira-clone.softeq.com\')");
        String jiraUrl = scanner.nextLine();
        System.out.println("Enter FILE PATH (like \'f://jira-task-source.xlsx\')");
        String jiraFileSource = scanner.nextLine();
        new JiraService().createAndPostIssueFromXlsFile(jiraUrl, jiraFileSource);

    }

}
