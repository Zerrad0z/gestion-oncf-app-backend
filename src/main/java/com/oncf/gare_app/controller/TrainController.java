package com.oncf.gare_app.controller;

import com.oncf.gare_app.dto.TrainRequest;
import com.oncf.gare_app.dto.TrainResponse;
import com.oncf.gare_app.service.TrainService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/trains")
@RequiredArgsConstructor
public class TrainController {

    private final TrainService trainService;

    @GetMapping
    public ResponseEntity<List<TrainResponse>> getAllTrains() {
        return ResponseEntity.ok(trainService.getAllTrains());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrainResponse> getTrainById(@PathVariable Long id) {
        return ResponseEntity.ok(trainService.getTrainById(id));
    }

    @PostMapping
    public ResponseEntity<TrainResponse> createTrain(@Valid @RequestBody TrainRequest request) {
        return new ResponseEntity<>(trainService.createTrain(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrainResponse> updateTrain(@PathVariable Long id, @Valid @RequestBody TrainRequest request) {
        return ResponseEntity.ok(trainService.updateTrain(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrain(@PathVariable Long id) {
        trainService.deleteTrain(id);
        return ResponseEntity.noContent().build();
    }
}