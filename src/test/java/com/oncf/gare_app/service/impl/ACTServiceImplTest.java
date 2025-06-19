//package com.oncf.gare_app.service.impl;
//
//import com.oncf.gare_app.dto.ACTRequest;
//import com.oncf.gare_app.dto.ACTResponse;
//import com.oncf.gare_app.dto.AntenneResponseDto;
//import com.oncf.gare_app.entity.ACT;
//import com.oncf.gare_app.entity.Antenne;
//import com.oncf.gare_app.exception.ResourceNotFoundException;
//import com.oncf.gare_app.exception.UniqueConstraintViolationException;
//import com.oncf.gare_app.mapper.ACTMapper;
//import com.oncf.gare_app.repository.ACTRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.Assertions.assertThatThrownBy;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class ACTServiceImplTest {
//
//    @Mock
//    private ACTRepository actRepository;
//
//    @Mock
//    private ACTMapper actMapper;
//
//    @InjectMocks
//    private ACTServiceImpl actService;
//
//    private ACT act1;
//    private ACT act2;
//    private ACTRequest actRequest;
//    private ACTResponse actResponse1;
//    private ACTResponse actResponse2;
//    private Antenne antenne;
//    private AntenneResponseDto antenneResponseDto;
//
//    @BeforeEach
//    void setUp() {
//        // Setup Antenne and AntenneResponseDto
//        antenne = new Antenne();
//        antenne.setId(100L);
//        antenne.setNom("Antenne Test");
//
//        antenneResponseDto = new AntenneResponseDto();
//        antenneResponseDto.setId(100L);
//        antenneResponseDto.setNom("Antenne Test");
//
//        // Setup test data
//        act1 = new ACT();
//        act1.setId(1L);
//        act1.setMatricule("ACT001");
//        act1.setNomPrenom("Test User 1");
//        act1.setAntenne(antenne);
//
//        act2 = new ACT();
//        act2.setId(2L);
//        act2.setMatricule("ACT002");
//        act2.setNomPrenom("Test User 2");
//        act2.setAntenne(antenne);
//
//        actRequest = new ACTRequest();
//        actRequest.setMatricule("ACT001");
//        actRequest.setNomPrenom("Test User 1");
//        // Assuming ACTRequest has antenneId field
//
//        actResponse1 = new ACTResponse();
//        actResponse1.setId(1L);
//        actResponse1.setMatricule("ACT001");
//        actResponse1.setNomPrenom("Test User 1");
//        actResponse1.setAntenne(antenneResponseDto);
//
//        actResponse2 = new ACTResponse();
//        actResponse2.setId(2L);
//        actResponse2.setMatricule("ACT002");
//        actResponse2.setNomPrenom("Test User 2");
//        actResponse2.setAntenne(antenneResponseDto);
//    }
//
//    @Test
//    void getAllACTs_ShouldReturnListOfACTResponses_WhenACTsExist() {
//        // Given
//        List<ACT> acts = Arrays.asList(act1, act2);
//        when(actRepository.findAll()).thenReturn(acts);
//        when(actMapper.toDto(act1)).thenReturn(actResponse1);
//        when(actMapper.toDto(act2)).thenReturn(actResponse2);
//
//        // When
//        List<ACTResponse> result = actService.getAllACTs();
//
//        // Then
//        assertThat(result).hasSize(2);
//        assertThat(result).containsExactly(actResponse1, actResponse2);
//        verify(actRepository).findAll();
//        verify(actMapper).toDto(act1);
//        verify(actMapper).toDto(act2);
//    }
//
//    @Test
//    void getAllACTs_ShouldReturnEmptyList_WhenNoACTsExist() {
//        // Given
//        when(actRepository.findAll()).thenReturn(Collections.emptyList());
//
//        // When
//        List<ACTResponse> result = actService.getAllACTs();
//
//        // Then
//        assertThat(result).isEmpty();
//        verify(actRepository).findAll();
//        verifyNoInteractions(actMapper);
//    }
//
//    @Test
//    void getACTsByAntenneId_ShouldReturnFilteredACTs_WhenACTsExistForAntenne() {
//        // Given
//        Long antenneId = 100L;
//        List<ACT> acts = Arrays.asList(act1, act2);
//        when(actRepository.findByAntenneId(antenneId)).thenReturn(acts);
//        when(actMapper.toDto(act1)).thenReturn(actResponse1);
//        when(actMapper.toDto(act2)).thenReturn(actResponse2);
//
//        // When
//        List<ACTResponse> result = actService.getACTsByAntenneId(antenneId);
//
//        // Then
//        assertThat(result).hasSize(2);
//        assertThat(result).containsExactly(actResponse1, actResponse2);
//        verify(actRepository).findByAntenneId(antenneId);
//        verify(actMapper).toDto(act1);
//        verify(actMapper).toDto(act2);
//    }
//
//    @Test
//    void getACTsByAntenneId_ShouldReturnEmptyList_WhenNoACTsExistForAntenne() {
//        // Given
//        Long antenneId = 999L;
//        when(actRepository.findByAntenneId(antenneId)).thenReturn(Collections.emptyList());
//
//        // When
//        List<ACTResponse> result = actService.getACTsByAntenneId(antenneId);
//
//        // Then
//        assertThat(result).isEmpty();
//        verify(actRepository).findByAntenneId(antenneId);
//        verifyNoInteractions(actMapper);
//    }
//
//    @Test
//    void getACTById_ShouldReturnACTResponse_WhenACTExists() {
//        // Given
//        Long id = 1L;
//        when(actRepository.findById(id)).thenReturn(Optional.of(act1));
//        when(actMapper.toDto(act1)).thenReturn(actResponse1);
//
//        // When
//        ACTResponse result = actService.getACTById(id);
//
//        // Then
//        assertThat(result).isEqualTo(actResponse1);
//        verify(actRepository).findById(id);
//        verify(actMapper).toDto(act1);
//    }
//
//    @Test
//    void getACTById_ShouldThrowResourceNotFoundException_WhenACTNotFound() {
//        // Given
//        Long id = 999L;
//        when(actRepository.findById(id)).thenReturn(Optional.empty());
//
//        // When & Then
//        assertThatThrownBy(() -> actService.getACTById(id))
//                .isInstanceOf(ResourceNotFoundException.class)
//                .hasMessage("ACT non trouvé avec l'id: " + id);
//
//        verify(actRepository).findById(id);
//        verifyNoInteractions(actMapper);
//    }
//
//    @Test
//    void getACTByMatricule_ShouldReturnACTResponse_WhenACTExists() {
//        // Given
//        String matricule = "ACT001";
//        when(actRepository.findByMatricule(matricule)).thenReturn(Optional.of(act1));
//        when(actMapper.toDto(act1)).thenReturn(actResponse1);
//
//        // When
//        ACTResponse result = actService.getACTByMatricule(matricule);
//
//        // Then
//        assertThat(result).isEqualTo(actResponse1);
//        verify(actRepository).findByMatricule(matricule);
//        verify(actMapper).toDto(act1);
//    }
//
//    @Test
//    void getACTByMatricule_ShouldThrowResourceNotFoundException_WhenACTNotFound() {
//        // Given
//        String matricule = "NOTFOUND";
//        when(actRepository.findByMatricule(matricule)).thenReturn(Optional.empty());
//
//        // When & Then
//        assertThatThrownBy(() -> actService.getACTByMatricule(matricule))
//                .isInstanceOf(ResourceNotFoundException.class)
//                .hasMessage("ACT non trouvé avec le matricule: " + matricule);
//
//        verify(actRepository).findByMatricule(matricule);
//        verifyNoInteractions(actMapper);
//    }
//
//    @Test
//    void createACT_ShouldCreateAndReturnACT_WhenMatriculeIsUnique() {
//        // Given
//        when(actRepository.existsByMatricule(actRequest.getMatricule())).thenReturn(false);
//        when(actMapper.toEntity(actRequest)).thenReturn(act1);
//        when(actRepository.save(act1)).thenReturn(act1);
//        when(actMapper.toDto(act1)).thenReturn(actResponse1);
//
//        // When
//        ACTResponse result = actService.createACT(actRequest);
//
//        // Then
//        assertThat(result).isEqualTo(actResponse1);
//        verify(actRepository).existsByMatricule(actRequest.getMatricule());
//        verify(actMapper).toEntity(actRequest);
//        verify(actRepository).save(act1);
//        verify(actMapper).toDto(act1);
//    }
//
//    @Test
//    void createACT_ShouldThrowUniqueConstraintViolationException_WhenMatriculeAlreadyExists() {
//        // Given
//        when(actRepository.existsByMatricule(actRequest.getMatricule())).thenReturn(true);
//
//        // When & Then
//        assertThatThrownBy(() -> actService.createACT(actRequest))
//                .isInstanceOf(UniqueConstraintViolationException.class)
//                .hasMessage("Un ACT avec le matricule " + actRequest.getMatricule() + " existe déjà");
//
//        verify(actRepository).existsByMatricule(actRequest.getMatricule());
//        verifyNoMoreInteractions(actRepository);
//        verifyNoInteractions(actMapper);
//    }
//
//    @Test
//    void updateACT_ShouldUpdateAndReturnACT_WhenACTExistsAndMatriculeIsUnchanged() {
//        // Given
//        Long id = 1L;
//        when(actRepository.findById(id)).thenReturn(Optional.of(act1));
//        when(actRepository.save(act1)).thenReturn(act1);
//        when(actMapper.toDto(act1)).thenReturn(actResponse1);
//
//        // When
//        ACTResponse result = actService.updateACT(id, actRequest);
//
//        // Then
//        assertThat(result).isEqualTo(actResponse1);
//        verify(actRepository).findById(id);
//        verify(actMapper).updateEntityFromDto(actRequest, act1);
//        verify(actRepository).save(act1);
//        verify(actMapper).toDto(act1);
//        verify(actRepository, never()).existsByMatricule(any());
//    }
//
//    @Test
//    void updateACT_ShouldUpdateAndReturnACT_WhenACTExistsAndNewMatriculeIsUnique() {
//        // Given
//        Long id = 1L;
//        ACTRequest newRequest = new ACTRequest();
//        newRequest.setMatricule("ACT003");
//        newRequest.setNomPrenom("Test User Updated");
//
//        when(actRepository.findById(id)).thenReturn(Optional.of(act1));
//        when(actRepository.existsByMatricule(newRequest.getMatricule())).thenReturn(false);
//        when(actRepository.save(act1)).thenReturn(act1);
//        when(actMapper.toDto(act1)).thenReturn(actResponse1);
//
//        // When
//        ACTResponse result = actService.updateACT(id, newRequest);
//
//        // Then
//        assertThat(result).isEqualTo(actResponse1);
//        verify(actRepository).findById(id);
//        verify(actRepository).existsByMatricule(newRequest.getMatricule());
//        verify(actMapper).updateEntityFromDto(newRequest, act1);
//        verify(actRepository).save(act1);
//        verify(actMapper).toDto(act1);
//    }
//
//    @Test
//    void updateACT_ShouldThrowResourceNotFoundException_WhenACTNotFound() {
//        // Given
//        Long id = 999L;
//        when(actRepository.findById(id)).thenReturn(Optional.empty());
//
//        // When & Then
//        assertThatThrownBy(() -> actService.updateACT(id, actRequest))
//                .isInstanceOf(ResourceNotFoundException.class)
//                .hasMessage("ACT non trouvé avec l'id: " + id);
//
//        verify(actRepository).findById(id);
//        verifyNoMoreInteractions(actRepository);
//        verifyNoInteractions(actMapper);
//    }
//
//    @Test
//    void updateACT_ShouldThrowUniqueConstraintViolationException_WhenNewMatriculeAlreadyExists() {
//        // Given
//        Long id = 1L;
//        ACTRequest newRequest = new ACTRequest();
//        newRequest.setMatricule("ACT002");
//        newRequest.setNomPrenom("Test User Updated");
//
//        when(actRepository.findById(id)).thenReturn(Optional.of(act1));
//        when(actRepository.existsByMatricule(newRequest.getMatricule())).thenReturn(true);
//
//        // When & Then
//        assertThatThrownBy(() -> actService.updateACT(id, newRequest))
//                .isInstanceOf(UniqueConstraintViolationException.class)
//                .hasMessage("Un ACT avec le matricule " + newRequest.getMatricule() + " existe déjà");
//
//        verify(actRepository).findById(id);
//        verify(actRepository).existsByMatricule(newRequest.getMatricule());
//        verifyNoMoreInteractions(actRepository);
//        verifyNoInteractions(actMapper);
//    }
//
//    @Test
//    void deleteACT_ShouldDeleteACT_WhenACTExists() {
//        // Given
//        Long id = 1L;
//        when(actRepository.existsById(id)).thenReturn(true);
//
//        // When
//        actService.deleteACT(id);
//
//        // Then
//        verify(actRepository).existsById(id);
//        verify(actRepository).deleteById(id);
//    }
//
//    @Test
//    void deleteACT_ShouldThrowResourceNotFoundException_WhenACTNotFound() {
//        // Given
//        Long id = 999L;
//        when(actRepository.existsById(id)).thenReturn(false);
//
//        // When & Then
//        assertThatThrownBy(() -> actService.deleteACT(id))
//                .isInstanceOf(ResourceNotFoundException.class)
//                .hasMessage("ACT non trouvé avec l'id: " + id);
//
//        verify(actRepository).existsById(id);
//        verify(actRepository, never()).deleteById(any());
//    }
//}