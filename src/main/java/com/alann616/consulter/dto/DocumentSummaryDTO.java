package com.alann616.consulter.dto;

import com.alann616.consulter.enums.DocumentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentSummaryDTO {
    private Long documentId;
    private LocalDateTime timestamp;
    private DocumentType documentType;
    private String documentName;
    private String doctorName;
    private String patientName;
}
