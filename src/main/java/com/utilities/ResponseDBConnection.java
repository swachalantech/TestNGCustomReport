package com.utilities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import com.log.reporter.LogReporter;

public class ResponseDBConnection {

	LogReporter objLogReporter = null;
	public ResponseDBConnection(LogReporter objLogReporter){
		this.objLogReporter= objLogReporter;
	}
	public ResultSet executeQueryAndGetResultSet(String envUrl, String query, boolean... printQuery) {
		boolean print = (printQuery.length > 0) ? printQuery[0] : true;
		ResultSet rs = null;
		int totalRows = 0;
		try {
			// create connection
			Connection conn = DriverManager.getConnection("jdbc:postgresql://10.24.3.8:5033/CLIENT_esb01", "od", "5a1mi");
			Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			//PreparedStatement preparedStatement = conn.prepareStatement(query);
	        rs = stmt.executeQuery(query);
			List<String> listOfColumnNames = new ArrayList();

			// execute query
			//rs = stmt.executeQuery(query);
			objLogReporter.logInfo("Query = " + query,false);

			if (print) {
				// get total number of rows
				totalRows = getRowCount(rs);

				if (totalRows == 0) {
					objLogReporter.logInfo("No Result is returned by Query",false);
				} else {
					// get column names
					ResultSetMetaData rsmd = rs.getMetaData();
					int columnCount = rsmd.getColumnCount();
					for (int i = 1; i <= columnCount; i++) {
						listOfColumnNames.add(rsmd.getColumnName(i));
					}

					// store data in records array
					Object[][] records = new Object[totalRows][columnCount];
					int counter = 0;
					int flag;
					int maxListSize = listOfColumnNames.size();
					while (rs.next()) {
						flag = 0;
						for (int i = 0; i < maxListSize; i++) {
							if (flag > maxListSize) {
								break;
							} else {
								Object value = rs.getObject(listOfColumnNames.get(flag));
								if (value instanceof Boolean) {
									records[counter][flag] = Boolean.valueOf(value.toString()) == false ? "0" : "1";
								} else {
									records[counter][flag] = value;
								}
								flag++;
							}
						}
						counter++;
					}
					rs.beforeFirst();
					StringBuilder sqlTable = new StringBuilder("<table>");

					sqlTable.append("<tr>");
					for (String columnName : listOfColumnNames) {
						sqlTable.append("<th>" + columnName + "</th>");
					}
					sqlTable.append("</tr>");

					for (int i = 0; i < totalRows; i++) {
						sqlTable.append("<tr>");
						for (int j = 0; j < columnCount; j++) {
							sqlTable.append("<td>" + records[i][j] + "</td>");
						}
						sqlTable.append("</tr>");
					}
					sqlTable.append("</table>");

					objLogReporter.logInfo(sqlTable.toString(),false);
				}
			}else {
				objLogReporter.logInfo("Result set can not be show as meta data parsing do not support the content returned",false);
			}
		} catch (Exception ex) {
				ex.printStackTrace();
		}
		return rs;
	}
	
	public int getRowCount(ResultSet resultSet) {
		if (resultSet == null) {
			return 0;
		}
		try {
			resultSet.last();
			return resultSet.getRow();
		} catch (SQLException exp) {
			exp.printStackTrace();
		} finally {
			try {
				resultSet.beforeFirst();
			} catch (SQLException exp) {
				exp.printStackTrace();
			}
		}
		return 0;
	}
}
