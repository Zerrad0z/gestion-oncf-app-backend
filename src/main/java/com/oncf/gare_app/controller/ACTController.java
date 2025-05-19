package com.oncf.gare_app.controller;

import com.oncf.gare_app.dto.ACTRequest;
import com.oncf.gare_app.dto.ACTResponse;
import com.oncf.gare_app.service.ACTService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/acts")
@RequiredArgsConstructor
public class ACTController {

    private final ACTService actService;

    @GetMapping
    public ResponseEntity<List<ACTResponse>> getAllACTs() {
        return ResponseEntity.ok(actService.getAllACTs());
    }

    @GetMapping("/antenne/{antenneId}")
    public ResponseEntity<List<ACTResponse>> getACTsByAntenneId(@PathVariable Long antenneId) {
        return ResponseEntity.ok(actService.getACTsByAntenneId(antenneId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ACTResponse> getACTById(@PathVariable Long id) {
        return ResponseEntity.ok(actService.getACTById(id));
    }

    @GetMapping("/matricule/{matricule}")
    public ResponseEntity<ACTResponse> getACTByMatricule(@PathVariable String matricule) {
        return ResponseEntity.ok(actService.getACTByMatricule(matricule));
    }

    @PostMapping
    public ResponseEntity<ACTResponse> createACT(@Valid @RequestBody ACTRequest request) {
        return new ResponseEntity<>(actService.createACT(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ACTResponse> updateACT(@PathVariable Long id, @Valid @RequestBody ACTRequest request) {
        return ResponseEntity.ok(actService.updateACT(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteACT(@PathVariable Long id) {
        actService.deleteACT(id);
        return ResponseEntity.noContent().build();
    }
}