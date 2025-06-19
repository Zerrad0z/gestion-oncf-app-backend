//package com.oncf.gare_app.service.impl;
//
//import com.oncf.gare_app.dto.AntenneRequestDto;
//import com.oncf.gare_app.dto.AntenneResponseDto;
//import com.oncf.gare_app.entity.Antenne;
//import com.oncf.gare_app.entity.Section;
//import com.oncf.gare_app.exception.AntenneAlreadyExistsException;
//import com.oncf.gare_app.exception.AntenneNotFoundException;
//import com.oncf.gare_app.exception.SectionNotFoundException;
//import com.oncf.gare_app.mapper.AntenneMapper;
//import com.oncf.gare_app.repository.AntenneRepository;
//import com.oncf.gare_app.repository.SectionRepository;
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
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class AntenneServiceImplTest {
//
//    @Mock
//    private AntenneRepository antenneRepository;
//
//    @Mock
//    private SectionRepository sectionRepository;
//
//    @Mock
//    private AntenneMapper antenneMapper;
//
//    @InjectMocks
//    private AntenneServiceImpl antenneService;
//
//    private Antenne antenne1;
//    private Antenne antenne2;
//    private Section section;
//    private AntenneRequestDto antenneRequestDto;
//    private AntenneResponseDto antenneResponseDto1;
//    private AntenneResponseDto antenneResponseDto2;
//
//    @BeforeEach
//    void setUp() {
//        // Setup Section
//        section = new Section();
//        section.setId(100L);
//        section.setNom("Section Test");
//
//        // Setup Antennes
//        antenne1 = new Antenne();
//        antenne1.setId(1L);
//        antenne1.setNom("Antenne Test 1");
//        antenne1.setSection(section);
//
//        antenne2 = new Antenne();
//        antenne2.setId(2L);
//        antenne2.setNom("Antenne Test 2");
//        antenne2.setSection(section);
//
//        // Setup DTOs
//        antenneRequestDto = new AntenneRequestDto();
//        antenneRequestDto.setNom("Antenne Test 1");
//        antenneRequestDto.setSectionId(100L);
//
//        antenneResponseDto1 = new AntenneResponseDto();
//        antenneResponseDto1.setId(1L);
//        antenneResponseDto1.setNom("Antenne Test 1");
//
//        antenneResponseDto2 = new AntenneResponseDto();
//        antenneResponseDto2.setId(2L);
//        antenneResponseDto2.setNom("Antenne Test 2");
//    }
//
//    @Test
//    void createAntenne_ShouldCreateAndReturnAntenne_WhenValidRequest() {
//        // Given
//        when(sectionRepository.existsById(antenneRequestDto.getSectionId())).thenReturn(true);
//        when(antenneRepository.existsByNomAndSectionId(antenneRequestDto.getNom(), antenneRequestDto.getSectionId())).thenReturn(false);
//        when(antenneMapper.toEntity(antenneRequestDto)).thenReturn(antenne1);
//        when(antenneRepository.save(antenne1)).thenReturn(antenne1);
//        when(antenneMapper.toDto(antenne1)).thenReturn(antenneResponseDto1);
//
//        // When
//        AntenneResponseDto result = antenneService.createAntenne(antenneRequestDto);
//
//        // Then
//        assertThat(result).isEqualTo(antenneResponseDto1);
//        verify(sectionRepository).existsById(antenneRequestDto.getSectionId());
//        verify(antenneRepository).existsByNomAndSectionId(antenneRequestDto.getNom(), antenneRequestDto.getSectionId());
//        verify(antenneMapper).toEntity(antenneRequestDto);
//        verify(antenneRepository).save(antenne1);
//        verify(antenneMapper).toDto(antenne1);
//    }
//
//    @Test
//    void createAntenne_ShouldThrowSectionNotFoundException_WhenSectionDoesNotExist() {
//        // Given
//        when(sectionRepository.existsById(antenneRequestDto.getSectionId())).thenReturn(false);
//
//        // When & Then
//        assertThatThrownBy(() -> antenneService.createAntenne(antenneRequestDto))
//                .isInstanceOf(SectionNotFoundException.class);
//
//        verify(sectionRepository).existsById(antenneRequestDto.getSectionId());
//        verifyNoMoreInteractions(antenneRepository, antenneMapper);
//    }
//
//    @Test
//    void createAntenne_ShouldThrowAntenneAlreadyExistsException_WhenAntenneWithSameNameExists() {
//        // Given
//        when(sectionRepository.existsById(antenneRequestDto.getSectionId())).thenReturn(true);
//        when(antenneRepository.existsByNomAndSectionId(antenneRequestDto.getNom(), antenneRequestDto.getSectionId())).thenReturn(true);
//
//        // When & Then
//        assertThatThrownBy(() -> antenneService.createAntenne(antenneRequestDto))
//                .isInstanceOf(AntenneAlreadyExistsException.class);
//
//        verify(sectionRepository).existsById(antenneRequestDto.getSectionId());
//        verify(antenneRepository).existsByNomAndSectionId(antenneRequestDto.getNom(), antenneRequestDto.getSectionId());
//        verify(antenneRepository, never()).save(any());
//        verifyNoInteractions(antenneMapper);
//    }
//
//    @Test
//    void getAllAntennes_ShouldReturnListOfAntennes_WhenAntennesExist() {
//        // Given
//        List<Antenne> antennes = Arrays.asList(antenne1, antenne2);
//        List<AntenneResponseDto> expectedResponse = Arrays.asList(antenneResponseDto1, antenneResponseDto2);
//        when(antenneRepository.findAll()).thenReturn(antennes);
//        when(antenneMapper.toDtoList(antennes)).thenReturn(expectedResponse);
//
//        // When
//        List<AntenneResponseDto> result = antenneService.getAllAntennes();
//
//        // Then
//        assertThat(result).hasSize(2);
//        assertThat(result).containsExactly(antenneResponseDto1, antenneResponseDto2);
//        verify(antenneRepository).findAll();
//        verify(antenneMapper).toDtoList(antennes);
//    }
//
//    @Test
//    void getAllAntennes_ShouldReturnEmptyList_WhenNoAntennesExist() {
//        // Given
//        when(antenneRepository.findAll()).thenReturn(Collections.emptyList());
//        when(antenneMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());
//
//        // When
//        List<AntenneResponseDto> result = antenneService.getAllAntennes();
//
//        // Then
//        assertThat(result).isEmpty();
//        verify(antenneRepository).findAll();
//        verify(antenneMapper).toDtoList(Collections.emptyList());
//    }
//
//    @Test
//    void getAntenneById_ShouldReturnAntenne_WhenAntenneExists() {
//        // Given
//        Long id = 1L;
//        when(antenneRepository.findById(id)).thenReturn(Optional.of(antenne1));
//        when(antenneMapper.toDto(antenne1)).thenReturn(antenneResponseDto1);
//
//        // When
//        AntenneResponseDto result = antenneService.getAntenneById(id);
//
//        // Then
//        assertThat(result).isEqualTo(antenneResponseDto1);
//        verify(antenneRepository).findById(id);
//        verify(antenneMapper).toDto(antenne1);
//    }
//
//    @Test
//    void getAntenneById_ShouldThrowAntenneNotFoundException_WhenAntenneDoesNotExist() {
//        // Given
//        Long id = 999L;
//        when(antenneRepository.findById(id)).thenReturn(Optional.empty());
//
//        // When & Then
//        assertThatThrownBy(() -> antenneService.getAntenneById(id))
//                .isInstanceOf(AntenneNotFoundException.class);
//
//        verify(antenneRepository).findById(id);
//        verifyNoInteractions(antenneMapper);
//    }
//
//    @Test
//    void getAntennesBySection_ShouldReturnAntennesForSection_WhenSectionExists() {
//        // Given
//        Long sectionId = 100L;
//        List<Antenne> antennes = Arrays.asList(antenne1, antenne2);
//        List<AntenneResponseDto> expectedResponse = Arrays.asList(antenneResponseDto1, antenneResponseDto2);
//        when(sectionRepository.existsById(sectionId)).thenReturn(true);
//        when(antenneRepository.findBySectionId(sectionId)).thenReturn(antennes);
//        when(antenneMapper.toDtoList(antennes)).thenReturn(expectedResponse);
//
//        // When
//        List<AntenneResponseDto> result = antenneService.getAntennesBySection(sectionId);
//
//        // Then
//        assertThat(result).hasSize(2);
//        assertThat(result).containsExactly(antenneResponseDto1, antenneResponseDto2);
//        verify(sectionRepository).existsById(sectionId);
//        verify(antenneRepository).findBySectionId(sectionId);
//        verify(antenneMapper).toDtoList(antennes);
//    }
//
//    @Test
//    void getAntennesBySection_ShouldThrowSectionNotFoundException_WhenSectionDoesNotExist() {
//        // Given
//        Long sectionId = 999L;
//        when(sectionRepository.existsById(sectionId)).thenReturn(false);
//
//        // When & Then
//        assertThatThrownBy(() -> antenneService.getAntennesBySection(sectionId))
//                .isInstanceOf(SectionNotFoundException.class);
//
//        verify(sectionRepository).existsById(sectionId);
//        verifyNoMoreInteractions(antenneRepository, antenneMapper);
//    }
//
//    @Test
//    void getAntennesBySection_ShouldReturnEmptyList_WhenNoAntennesInSection() {
//        // Given
//        Long sectionId = 100L;
//        when(sectionRepository.existsById(sectionId)).thenReturn(true);
//        when(antenneRepository.findBySectionId(sectionId)).thenReturn(Collections.emptyList());
//        when(antenneMapper.toDtoList(Collections.emptyList())).thenReturn(Collections.emptyList());
//
//        // When
//        List<AntenneResponseDto> result = antenneService.getAntennesBySection(sectionId);
//
//        // Then
//        assertThat(result).isEmpty();
//        verify(sectionRepository).existsById(sectionId);
//        verify(antenneRepository).findBySectionId(sectionId);
//        verify(antenneMapper).toDtoList(Collections.emptyList());
//    }
//
//    @Test
//    void updateAntenne_ShouldUpdateAndReturnAntenne_WhenValidRequest() {
//        // Given
//        Long id = 1L;
//        when(antenneRepository.findById(id)).thenReturn(Optional.of(antenne1));
//        when(sectionRepository.existsById(antenneRequestDto.getSectionId())).thenReturn(true);
//        when(antenneRepository.save(antenne1)).thenReturn(antenne1);
//        when(antenneMapper.toDto(antenne1)).thenReturn(antenneResponseDto1);
//
//        // When
//        AntenneResponseDto result = antenneService.updateAntenne(id, antenneRequestDto);
//
//        // Then
//        assertThat(result).isEqualTo(antenneResponseDto1);
//        verify(antenneRepository).findById(id);
//        verify(sectionRepository).existsById(antenneRequestDto.getSectionId());
//        verify(antenneMapper).updateEntityFromDto(antenneRequestDto, antenne1);
//        verify(antenneRepository).save(antenne1);
//        verify(antenneMapper).toDto(antenne1);
//    }
//
//    @Test
//    void updateAntenne_ShouldThrowAntenneNotFoundException_WhenAntenneDoesNotExist() {
//        // Given
//        Long id = 999L;
//        when(antenneRepository.findById(id)).thenReturn(Optional.empty());
//
//        // When & Then
//        assertThatThrownBy(() -> antenneService.updateAntenne(id, antenneRequestDto))
//                .isInstanceOf(AntenneNotFoundException.class);
//
//        verify(antenneRepository).findById(id);
//        verifyNoMoreInteractions(sectionRepository, antenneRepository, antenneMapper);
//    }
//
//    @Test
//    void updateAntenne_ShouldThrowSectionNotFoundException_WhenSectionDoesNotExist() {
//        // Given
//        Long id = 1L;
//        when(antenneRepository.findById(id)).thenReturn(Optional.of(antenne1));
//        when(sectionRepository.existsById(antenneRequestDto.getSectionId())).thenReturn(false);
//
//        // When & Then
//        assertThatThrownBy(() -> antenneService.updateAntenne(id, antenneRequestDto))
//                .isInstanceOf(SectionNotFoundException.class);
//
//        verify(antenneRepository).findById(id);
//        verify(sectionRepository).existsById(antenneRequestDto.getSectionId());
//        verify(antenneRepository, never()).save(any());
//        verifyNoMoreInteractions(antenneMapper);
//    }
//
//    @Test
//    void updateAntenne_ShouldThrowAntenneAlreadyExistsException_WhenNameChangedAndExists() {
//        // Given
//        Long id = 1L;
//        AntenneRequestDto updatedRequest = new AntenneRequestDto();
//        updatedRequest.setNom("New Antenne Name");
//        updatedRequest.setSectionId(100L);
//
//        when(antenneRepository.findById(id)).thenReturn(Optional.of(antenne1));
//        when(sectionRepository.existsById(updatedRequest.getSectionId())).thenReturn(true);
//        when(antenneRepository.existsByNomAndSectionId(updatedRequest.getNom(), updatedRequest.getSectionId())).thenReturn(true);
//
//        // When & Then
//        assertThatThrownBy(() -> antenneService.updateAntenne(id, updatedRequest))
//                .isInstanceOf(AntenneAlreadyExistsException.class);
//
//        verify(antenneRepository).findById(id);
//        verify(sectionRepository).existsById(updatedRequest.getSectionId());
//        verify(antenneRepository).existsByNomAndSectionId(updatedRequest.getNom(), updatedRequest.getSectionId());
//        verify(antenneRepository, never()).save(any());
//        verifyNoMoreInteractions(antenneMapper);
//    }
//
//    @Test
//    void updateAntenne_ShouldThrowAntenneAlreadyExistsException_WhenSectionChangedAndNameExists() {
//        // Given
//        Long id = 1L;
//        AntenneRequestDto updatedRequest = new AntenneRequestDto();
//        updatedRequest.setNom("Antenne Test 1"); // Same name
//        updatedRequest.setSectionId(200L); // Different section
//
//        when(antenneRepository.findById(id)).thenReturn(Optional.of(antenne1));
//        when(sectionRepository.existsById(updatedRequest.getSectionId())).thenReturn(true);
//        when(antenneRepository.existsByNomAndSectionId(updatedRequest.getNom(), updatedRequest.getSectionId())).thenReturn(true);
//
//        // When & Then
//        assertThatThrownBy(() -> antenneService.updateAntenne(id, updatedRequest))
//                .isInstanceOf(AntenneAlreadyExistsException.class);
//
//        verify(antenneRepository).findById(id);
//        verify(sectionRepository).existsById(updatedRequest.getSectionId());
//        verify(antenneRepository).existsByNomAndSectionId(updatedRequest.getNom(), updatedRequest.getSectionId());
//        verify(antenneRepository, never()).save(any());
//        verifyNoMoreInteractions(antenneMapper);
//    }
//
//    @Test
//    void deleteAntenne_ShouldDeleteAntenne_WhenAntenneExists() {
//        // Given
//        Long id = 1L;
//        when(antenneRepository.existsById(id)).thenReturn(true);
//
//        // When
//        antenneService.deleteAntenne(id);
//
//        // Then
//        verify(antenneRepository).existsById(id);
//        verify(antenneRepository).deleteById(id);
//    }
//
//    @Test
//    void deleteAntenne_ShouldThrowAntenneNotFoundException_WhenAntenneDoesNotExist() {
//        // Given
//        Long id = 999L;
//        when(antenneRepository.existsById(id)).thenReturn(false);
//
//        // When & Then
//        assertThatThrownBy(() -> antenneService.deleteAntenne(id))
//                .isInstanceOf(AntenneNotFoundException.class);
//
//        verify(antenneRepository).existsById(id);
//        verify(antenneRepository, never()).deleteById(any());
//    }
//}