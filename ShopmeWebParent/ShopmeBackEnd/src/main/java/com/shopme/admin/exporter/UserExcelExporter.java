package com.shopme.admin.exporter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.shopme.shopmecommon.entity.Role;
import com.shopme.shopmecommon.entity.User;

public class UserExcelExporter {
	private XSSFWorkbook workbook;
	private XSSFSheet sheet;
	
	private List<User> users;
	
	public UserExcelExporter(List<User> users) {
		this.users = users;
		
		workbook = new XSSFWorkbook();
		sheet = workbook.createSheet("Users");
	}

	private void writeHeaderRow() {
		Row row = sheet.createRow(0);
		
		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		font.setFontHeight(14);
		style.setFont(font);
		style.setFillBackgroundColor(IndexedColors.LIGHT_BLUE.getIndex());;
		
		Cell cell = row.createCell(0);
		cell.setCellValue("User ID");
		cell.setCellStyle(style);
		
		cell = row.createCell(1);
		cell.setCellValue("Email");
		cell.setCellStyle(style);
		
		cell = row.createCell(2);
		cell.setCellValue("First Name");
		cell.setCellStyle(style);
		
		cell = row.createCell(3);
		cell.setCellValue("Last Name");
		cell.setCellStyle(style);
		
		cell = row.createCell(4);
		cell.setCellValue("Roles");
		cell.setCellStyle(style);
		
		cell = row.createCell(5);
		cell.setCellValue("Enabled");
		cell.setCellStyle(style);
	}
	
	private void writeDataRows() {
		int rowCount = 1;
		
		for(User user: users) {
			Row row = sheet.createRow(rowCount++);
			
			Cell cell = row.createCell(0);
			cell.setCellValue(user.getId());
			sheet.autoSizeColumn(0);
			
			cell = row.createCell(1);
			cell.setCellValue(user.getEmail());
			sheet.autoSizeColumn(1);
			
			cell = row.createCell(2);
			cell.setCellValue(user.getFirstName());
			sheet.autoSizeColumn(2);
			
			cell = row.createCell(3);
			cell.setCellValue(user.getLastName());
			sheet.autoSizeColumn(3);
			
			cell = row.createCell(4);
			List<String> roles = new ArrayList<>();
			for(Role role: user.getRoles()) {
				roles.add(role.getName());
			}
			String rolesString = String.join(", ", roles);
			cell.setCellValue(rolesString);
			sheet.autoSizeColumn(4);
			
			cell = row.createCell(5);
			cell.setCellValue(user.getEnabled());
			sheet.autoSizeColumn(5);
		}
		
		
	}
	
	public void export(HttpServletResponse response) throws IOException {
		response.setContentType("application/octet-stream");
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String currentDateTime = dateFormatter.format(new Date());
		String fileName = "users_" + currentDateTime + ".xlsx";
		
		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=" + fileName;
		
		response.setHeader(headerKey, headerValue);
		
		writeHeaderRow();
		writeDataRows();
		
		ServletOutputStream outputStream = response.getOutputStream();
		workbook.write(outputStream);
		workbook.close();
		outputStream.close();
	}

}
