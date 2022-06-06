package com.shopme.shopmebackend.admin.export;

import com.lowagie.text.*;

import com.lowagie.text.Font;


import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.shopme.shopmebackend.admin.entity.User;

import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class UserPDFExporter {
    private List<User> users;

    public UserPDFExporter(List<User> users){
        this.users = users;
    }

    private void writeTableHeader(PdfPTable table){
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.BLUE);
        cell.setPadding(5);

        Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.WHITE);

        cell.setPhrase(new Phrase("User Id", font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Email",font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("First Name",font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Last Name",font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Roles",font));
        table.addCell(cell);

        cell.setPhrase(new Phrase("Enabled",font));
        table.addCell(cell);

    }
    private void writeTableData(PdfPTable table) {
        for (User user : users) {
            table.addCell(String.valueOf(user.getId()));
            table.addCell(user.getEmail());
            table.addCell(user.getFirstName());
            table.addCell(user.getLastName());
            table.addCell(user.getRoles().toString());
            table.addCell(String.valueOf(user.getEnabled()));
        }
    }

    public void export(HttpServletResponse httpServletResponse) throws DocumentException, IOException {
        Document document = new Document(PageSize.LETTER);
        PdfWriter.getInstance(document, httpServletResponse.getOutputStream());

        document.open();
        Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(18);
        font.setColor(Color.BLUE);

        Paragraph p = new Paragraph("List of Users", font);
        p.setAlignment(Paragraph.ALIGN_CENTER);

        document.add(p);

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100f);
        table.setWidths(new float[] {1.5f, 4.0f, 2.5f, 2.5f, 2.5f, 2.0f});
        table.setSpacingBefore(10);

        writeTableHeader(table);
        writeTableData(table);

        document.add(table);

        document.close();

    }
}
