package com.oncf.gare_app.controller;

import com.oncf.gare_app.dto.GareRequest;
import com.oncf.gare_app.dto.GareResponse;
import com.oncf.gare_app.service.GareService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/gares")
@RequiredArgsConstructor
public class GareController {

    private final GareService gareService;

    @GetMapping
    public ResponseEntity<List<GareResponse>> getAllGares() {
        return ResponseEntity.ok(gareService.getAllGares());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GareResponse> getGareById(@PathVariable Long id) {
        return ResponseEntity.ok(gareService.getGareById(id));
    }

    @PostMapping
    public ResponseEntity<GareResponse> createGare(@Valid @RequestBody GareRequest request) {
        return new ResponseEntity<>(gareService.createGare(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GareResponse> updateGare(@PathVariable Long id, @Valid @RequestBody GareRequest request) {
        return ResponseEntity.ok(gareService.updateGare(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGare(@PathVariable Long id) {
        gareService.deleteGare(id);
        return ResponseEntity.noContent().build();
    }
}