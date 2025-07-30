package com.openclassrooms.starterjwt.services;

import com.openclassrooms.starterjwt.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class TeacherServiceTest {

      @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private TeacherService teacherService;

  @Test
  void testFindAll_ShouldReturnListOfTeachers() {
      // Given - Préparation des données
      Teacher teacher1 = new Teacher();
      teacher1.setId(1L);
      teacher1.setFirstName("John");
      teacher1.setLastName("Doe");

      Teacher teacher2 = new Teacher();
      teacher2.setId(2L);
      teacher2.setFirstName("Sylvie");
      teacher2.setLastName("Yogi");

      List<Teacher> expectedTeachers = Arrays.asList(teacher1, teacher2);

      System.out.println("=== TEST FIND ALL ===");
      System.out.println("Nombre de teachers attendus: " + expectedTeachers.size());

      // Configuration du mock
      when(teacherRepository.findAll()).thenReturn(expectedTeachers);

      // When - Exécution
      List<Teacher> actualTeachers = teacherService.findAll();

      // Then - Vérifications
      assertNotNull(actualTeachers);
      assertEquals(2, actualTeachers.size());
      assertEquals(expectedTeachers.get(0).getId(), actualTeachers.get(0).getId());
      assertEquals(expectedTeachers.get(1).getId(), actualTeachers.get(1).getId());

      System.out.println("Nombre de teachers retournés: " + actualTeachers.size());

      // Vérification de l'appel au repository
      verify(teacherRepository, times(1)).findAll();
  }


  @Test
  void testFindById_WhenTeacherExists_ShouldReturnTeacher() {
      // Given - Préparation des données
      Long teacherId = 1L;
      Teacher expectedTeacher = new Teacher();
      expectedTeacher.setId(teacherId);
      expectedTeacher.setFirstName("John");
      expectedTeacher.setLastName("Doe");

      System.out.println("=== TEST FIND BY ID - SUCCESS ===");
      System.out.println("Teacher ID recherché: " + teacherId);
      System.out.println("Teacher attendu: " + expectedTeacher.getFirstName() + " " + expectedTeacher.getLastName());

      // Configuration du mock
      when(teacherRepository.findById(teacherId)).thenReturn(Optional.of(expectedTeacher));

      // When - Exécution
      Teacher actualTeacher = teacherService.findById(teacherId);

      // Then - Vérifications
      assertNotNull(actualTeacher);
      assertEquals(expectedTeacher.getId(), actualTeacher.getId());
      assertEquals(expectedTeacher.getFirstName(), actualTeacher.getFirstName());
      assertEquals(expectedTeacher.getLastName(), actualTeacher.getLastName());

      System.out.println("Teacher trouvé: " + actualTeacher.getFirstName() + " " + actualTeacher.getLastName());

      // Vérification de l'appel au repository
      verify(teacherRepository, times(1)).findById(teacherId);
  }

@Test
void testFindById_WhenTeacherNotExists_ShouldReturnNull() {
    // Given - Préparation des données
    Long teacherId = 999L;
    
    System.out.println("=== TEST FIND BY ID - NOT FOUND ===");
    System.out.println("Teacher ID recherché (inexistant): " + teacherId);
    
    // Configuration du mock - retourne Optional.empty(), Optional.empty() : simule un teacher non trouvé
    when(teacherRepository.findById(teacherId)).thenReturn(Optional.empty());
    
    // When - Exécution
    Teacher actualTeacher = teacherService.findById(teacherId);
    
    // Then - Vérifications, assertNull() vérifie que la méthode retourne bien null
    assertNull(actualTeacher);
    
    System.out.println("Résultat: null comme attendu ");
    
    // Vérification de l'appel au repository
    verify(teacherRepository, times(1)).findById(teacherId);
}

}
