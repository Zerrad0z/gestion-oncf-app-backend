package com.oncf.gare_app.controller;

import com.oncf.gare_app.dto.AntenneRequestDto;
import com.oncf.gare_app.dto.AntenneResponseDto;
import com.oncf.gare_app.service.AntenneService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/antennes")
@RequiredArgsConstructor
public class AntenneController {

    private final AntenneService antenneService;

    @PostMapping
    public ResponseEntity<AntenneResponseDto> createAntenne(@Valid @RequestBody AntenneRequestDto antenneDto) {
        AntenneResponseDto createdAntenne = antenneService.createAntenne(antenneDto);
        return new ResponseEntity<>(createdAntenne, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AntenneResponseDto>> getAllAntennes() {
        List<AntenneResponseDto> antennes = antenneService.getAllAntennes();
        return ResponseEntity.ok(antennes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AntenneResponseDto> getAntenneById(@PathVariable Long id) {
        AntenneResponseDto antenne = antenneService.getAntenneById(id);
        return ResponseEntity.ok(antenne);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AntenneResponseDto> updateAntenne(
            @PathVariable Long id,
            @Valid @RequestBody AntenneRequestDto antenneDto) {
        AntenneResponseDto updatedAntenne = antenneService.updateAntenne(id, antenneDto);
        return ResponseEntity.ok(updatedAntenne);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAntenne(@PathVariable Long id) {
        antenneService.deleteAntenne(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/bysection/{sectionId}")
    public ResponseEntity<List<AntenneResponseDto>> getAntennesBySection(@PathVariable Long sectionId) {
        List<AntenneResponseDto> antennes = antenneService.getAntennesBySection(sectionId);
        return ResponseEntity.ok(antennes);
    }

}