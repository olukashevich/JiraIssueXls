import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

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
public class XlsService
{
    private static final int ROW_NUMBER_USER_LOGIN = 0;
    private static final int ROW_NUMBER_USER_PASSWORD = 1;
    private static final int ROW_NUMBER_USER_PROJECT_KEY = 2;
    private static final int COLUMN_INDEX_USER_INFO = 1;
    private static final int ROW_NUMBER_ISSUES = 5;

    public static List<JiraIssueBean> readJiraIssuesFromXlsFile(String excelFilePath) throws IOException
    {
        List<JiraIssueBean> jiraIssues = new LinkedList<JiraIssueBean>();
        JiraIssueBean jiraIssue;
        Workbook workbook = new XSSFWorkbook(excelFilePath); //HSSFWorkbook for Excel 2003 and XSSFWorkbook for Excel 2007
        Sheet firstSheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = firstSheet.iterator();

        while (iterator.hasNext()) {
            Row nextRow = iterator.next();
            jiraIssue = null;
            if (nextRow.getRowNum() >= ROW_NUMBER_ISSUES)
            {
                Iterator<Cell> cellIterator = nextRow.cellIterator();
                jiraIssue = new JiraIssueBean();
                while (cellIterator.hasNext())
                {
                    Cell nextCell = cellIterator.next();
                    int columnIndex = nextCell.getColumnIndex();
                    switch (columnIndex)
                    {
                        case 0:
                            jiraIssue.setType((String) getCellValue(nextCell));
                            break;
                        case 1:
                            jiraIssue.setSummary((String) getCellValue(nextCell));
                            break;
                        case 2:
                            jiraIssue.setAssignee((String) getCellValue(nextCell));
                            break;
                        case 3:
                            jiraIssue.setLinkToWiki((String) getCellValue(nextCell));
                            break;
                        case 4:
                            jiraIssue.setEpicLink((String) getCellValue(nextCell));
                            break;
                        case 5:
                            jiraIssue.setFixVersion((String) getCellValue(nextCell));
                            break;
                    }
                }
                if (jiraIssue != null)
                {
                    jiraIssues.add(jiraIssue);
                }
            }
        }
        workbook.close();
        return jiraIssues;
    }

    public static JiraUserBean readJiraUserFromXlsFile(String excelFilePath) throws IOException
    {
        JiraUserBean jiraUser = new JiraUserBean();
        Workbook workbook = new XSSFWorkbook(excelFilePath); //HSSFWorkbook for Excel 2003 and XSSFWorkbook for Excel 2007
        Sheet firstSheet = workbook.getSheetAt(0);
        Iterator<Row> iterator = firstSheet.iterator();

        while (iterator.hasNext()) {
            Row nextRow = iterator.next();
            if (nextRow.getRowNum() > ROW_NUMBER_USER_PROJECT_KEY)
            {
                break;
            }
            switch (nextRow.getRowNum())
            {
                case ROW_NUMBER_USER_LOGIN :
                    jiraUser.setLogin((String) getCellValueFromRowByIndex(nextRow, COLUMN_INDEX_USER_INFO));
                    break;
                case ROW_NUMBER_USER_PASSWORD :
                    jiraUser.setPassword((String) getCellValueFromRowByIndex(nextRow, COLUMN_INDEX_USER_INFO));
                    break;
                case ROW_NUMBER_USER_PROJECT_KEY :
                    jiraUser.setProjectKey((String) getCellValueFromRowByIndex(nextRow, COLUMN_INDEX_USER_INFO));
                    break;
            }
        }
        workbook.close();
        return jiraUser;
    }

    public static Object getCellValue(Cell cell)
    {
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_STRING:
                return cell.getStringCellValue();

            case Cell.CELL_TYPE_BOOLEAN:
                return cell.getBooleanCellValue();

            case Cell.CELL_TYPE_NUMERIC:
                return cell.getNumericCellValue();
        }
        return null;
    }

    public static Object getCellValueFromRowByIndex(Row row, int columnIndex)
    {
        Iterator<Cell> cellIterator = row.cellIterator();
        while (cellIterator.hasNext())
        {
            Cell nextCell = cellIterator.next();
            if (nextCell.getColumnIndex() == columnIndex)
            {
                return getCellValue(nextCell);
            }
        }
        return null;
    }

}
