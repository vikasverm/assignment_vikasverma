package com.example.pdfgenerator.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.example.pdfgenerator.entity.Invoice;
import com.example.pdfgenerator.service.PdfGeneratorService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
public class PdfController {
    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    private static final String PDF_DIRECTORY = "generated_pdfs/";

    @PostMapping("/generate-pdf")
    public ResponseEntity<byte[]> generatePdf(@RequestBody Invoice invoice) {
        if (invoice == null || invoice.getItems() == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            // Check if PDF already exists
            Path pdfPath = Paths.get(PDF_DIRECTORY + invoice.getSeller() + "_" + invoice.getBuyer() + ".pdf");
            if (Files.exists(pdfPath)) {
                byte[] pdfBytes = Files.readAllBytes(pdfPath);
                return createPdfResponse(pdfBytes, "invoice.pdf");
            }

            // Generate PDF and save to local storage
            byte[] pdfBytes = pdfGeneratorService.generatePdf(invoice);
            Files.createDirectories(pdfPath.getParent());
            Files.write(pdfPath, pdfBytes);

            return createPdfResponse(pdfBytes, "invoice.pdf");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ResponseEntity<byte[]> createPdfResponse(byte[] pdfBytes, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment").filename(filename).build());
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
