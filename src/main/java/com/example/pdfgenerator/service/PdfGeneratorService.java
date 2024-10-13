package com.example.pdfgenerator.service;

import com.example.pdfgenerator.entity.Invoice;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class PdfGeneratorService {

    public byte[] generatePdf(Invoice invoice) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            PdfFont font = PdfFontFactory.createFont("Helvetica");
            Document doc = new Document(pdfDoc).setFont(font);

            // Create header table
            float[] pointColumnWidths = { 280F, 280F };
            Table header = new Table(pointColumnWidths);
            header.addCell(new Cell().add(new Paragraph("Seller: " + invoice.getSeller() + "\n" +
                    invoice.getSellerAddress() + "\n GSTIN: " + invoice.getSellerGstin())).setPadding(30));
            header.addCell(new Cell().add(new Paragraph("Buyer: " + invoice.getBuyer() + "\n" +
                    invoice.getBuyerAddress() + "\n GSTIN: " + invoice.getBuyerGstin())).setPadding(30));

            // Create product info table
            float[] productInfoColumnWidths = { 140, 140, 140, 140 };
            Table productInfoTable = new Table(productInfoColumnWidths);
            productInfoTable.setTextAlignment(TextAlignment.CENTER);
            productInfoTable.addCell(new Cell().add(new Paragraph("Item")));
            productInfoTable.addCell(new Cell().add(new Paragraph("Quantity")));
            productInfoTable.addCell(new Cell().add(new Paragraph("Rate")));
            productInfoTable.addCell(new Cell().add(new Paragraph("Amount")));

            List<com.example.pdfgenerator.entity.Item> items = invoice.getItems();
            for (com.example.pdfgenerator.entity.Item item : items) {
                productInfoTable.addCell(new Cell().add(new Paragraph(item.getName())));
                productInfoTable.addCell(new Cell().add(new Paragraph(item.getQuantity())));
                productInfoTable.addCell(new Cell().add(new Paragraph(String.valueOf(item.getRate()))));
                productInfoTable.addCell(new Cell().add(new Paragraph(String.valueOf(item.getAmount()))));
            }

            // Add tables to document
            doc.add(header);
            doc.add(productInfoTable);
            doc.close();
            pdfDoc.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray();
    }

    public Object generateByteArray(Invoice any) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateByteArray'");
    }
}
