package com.oncf.gare_app.controller;


import com.oncf.gare_app.dto.SectionRequestDto;
import com.oncf.gare_app.dto.SectionResponseDto;
import com.oncf.gare_app.service.SectionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sections")
@RequiredArgsConstructor
public class SectionController {

    private final SectionService sectionService;

    @PostMapping
    public ResponseEntity<SectionResponseDto> createSection(@Valid @RequestBody SectionRequestDto sectionDto) {
        SectionResponseDto createdSection = sectionService.createSection(sectionDto);
        return new ResponseEntity<>(createdSection, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<SectionResponseDto>> getAllSections() {
        List<SectionResponseDto> sections = sectionService.getAllSections();
        return ResponseEntity.ok(sections);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SectionResponseDto> getSectionById(@PathVariable Long id) {
        SectionResponseDto section = sectionService.getSectionById(id);
        return ResponseEntity.ok(section);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SectionResponseDto> updateSection(
            @PathVariable Long id,
            @Valid @RequestBody SectionRequestDto sectionDto) {
        SectionResponseDto updatedSection = sectionService.updateSection(id, sectionDto);
        return ResponseEntity.ok(updatedSection);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSection(@PathVariable Long id) {
        sectionService.deleteSection(id);
        return ResponseEntity.noContent().build();
    }
}