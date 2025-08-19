package com.alann616.consulter.web.rest;

import com.alann616.consulter.dto.DocumentSummaryDTO;
import com.alann616.consulter.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentResource {

    private final DocumentService documentService;

    public DocumentResource(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<DocumentSummaryDTO>> getAllDocuments() {
        List<DocumentSummaryDTO> allDocuments = documentService.getAllDocuments();
        return ResponseEntity.ok(allDocuments);
    }

    @GetMapping("/history")
    public ResponseEntity<List<DocumentSummaryDTO>> getPatientDocumentHistory(@RequestParam Long patientId) {
        List<DocumentSummaryDTO> history = documentService.getDocumentHistoryForPatient(patientId);
        return ResponseEntity.ok(history);
    }
}