package com.sam.BankingWebApplication1.Controllers;

import com.sam.BankingWebApplication1.Entities.Customer;
import com.sam.BankingWebApplication1.Enums.KycDocumentType;
import com.sam.BankingWebApplication1.Enums.KycStatus;
import com.sam.BankingWebApplication1.Exceptions.ResourceNotFoundException;
import com.sam.BankingWebApplication1.Repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/smartBank/admin/kyc")
@PreAuthorize("hasRole('ADMIN')")
public class AdminKycDocumentController {

    @Autowired
    private CustomerRepository customerRepo;

    @GetMapping("/{customerId}/document/{type}")
    public ResponseEntity<Resource> viewDocument(
            @PathVariable Long customerId,
            @PathVariable KycDocumentType type
    ) throws IOException {

        Customer customer = customerRepo.findById(customerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer", "id", customerId));

        if (customer.getKycStatus() != KycStatus.SUBMITTED &&
                customer.getKycStatus() != KycStatus.VERIFIED) {
            throw new IllegalStateException("Documents not available");
        }

        String pathStr = switch (type) {
            case AADHAAR_FRONT -> customer.getAadhaarFrontPath();
            case AADHAAR_BACK  -> customer.getAadhaarBackPath();
            case PAN           -> customer.getPanPath();
        };

        if (pathStr == null || pathStr.isBlank()) {
            throw new ResourceNotFoundException("Document", "type", type.name());
        }

        Path filePath = Paths.get(pathStr);

        if (!Files.exists(filePath)) {
            throw new ResourceNotFoundException("Document", "type", type.name());
        }

        Resource resource = new UrlResource(filePath.toUri());

        String contentType = Files.probeContentType(filePath);
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + filePath.getFileName() + "\""
                )
                .body(resource);
    }


}

