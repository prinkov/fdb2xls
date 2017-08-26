package xyz.prinkov.selex;

import com.sun.xml.internal.ws.api.pipe.FiberContextSwitchInterceptor;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import javax.swing.*;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.*;
import java.util.Properties;

public class Selex {

    String output;
    String input;
    Statement stmt;

    public Selex(String input, String output) {
        try {
            Class.forName("org.firebirdsql.jdbc.FBDriver");
            Properties props = new Properties();
            props.setProperty("user", Workspace.username);
            props.setProperty("password", Workspace.password);
            props.setProperty("encoding", "UNICODE_FSS");
            this.input = input;
            this.output = output;
            Connection connection = DriverManager.getConnection(
                    "jdbc:firebirdsql:" + Workspace.host + ":" + input,
                    props);
            this.stmt = connection.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
//            todo отловить ошибки аутентификации
            try {
                Desktop.getDesktop().browse(new URI("http://www.firebirdsql.org/en/firebird-2-5/"));
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (URISyntaxException e1) {
                e1.printStackTrace();
            }
            JOptionPane.showMessageDialog(null,  "Для работы необходимо установить Firebird по ссылке http://www.firebirdsql.org/en/firebird-2-5/", "Ошибка!", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void exec(Param param) {
        try {
            int cellCounter = 0;
            int rowCounter = 0;
            ResultSet res = stmt.executeQuery(param.query);
            ResultSetMetaData types = res.getMetaData();
            Workbook book = new HSSFWorkbook();
            CellStyle dateStyle = book.createCellStyle();
            CreationHelper createHelper = book.getCreationHelper();
            dateStyle.setDataFormat(
                    createHelper.createDataFormat().getFormat("m/d/yy"));
            Sheet sheet = book.createSheet("Sheet1");
            Row row = sheet.createRow(rowCounter++);
            for(String key : param.view.keySet()) {
                row.createCell(cellCounter++).setCellValue(key);
            }
            while(res.next()) {

                cellCounter = 0;
                row = sheet.createRow(rowCounter++);
                for(String val : param.view.values()) {
                    switch (types.getColumnType(cellCounter+1)) {
                        case Types.DOUBLE:
                            if(res.getString(val) == null)
                                row.createCell(cellCounter++).setCellValue(res.getString(val));
                            else
                                row.createCell(cellCounter++).setCellValue(Double.parseDouble(res.getString(val)));
                            break;
                        case Types.INTEGER:
                            if(res.getString(val) == null)
                                row.createCell(cellCounter++).setCellValue(res.getString(val));
                            else
                                row.createCell(cellCounter++).setCellValue(Integer.parseInt(res.getString(val)));
                            break;
                        case Types.DATE:
                            if(res.getString(val) == null)
                                row.createCell(cellCounter++).setCellValue(res.getString(val));
                            else {
                                Cell cell = row.createCell(cellCounter++);
                                cell.setCellValue(res.getDate(val));
                                cell.setCellStyle(dateStyle);
                            }
                            break;
                        default:
                            row.createCell(cellCounter++).setCellValue(res.getString(val));
                            break;
                    }
                }
            }
            book.write(new FileOutputStream(output + "/" + param.name + ".xls"));
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,  "Ошибка выполнения запроса", "Ошибка!", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null,  "Ошибка записи/чтения файла", "Ошибка!", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,  "Ошибка записи/чтения файла", "Ошибка!", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}