package com.jmvstv_v.service;

import com.jmvstv_v.config.MockConfig;
import com.jmvstv_v.dto.EmployeeDto;
import com.jmvstv_v.dto.FilterDto;
import com.jmvstv_v.entity.Employee;
import com.jmvstv_v.entity.Team;
import com.jmvstv_v.mapper.EmployeeMapper;
import com.jmvstv_v.repository.EmployeeRepository;
import com.jmvstv_v.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ActiveProfiles("test")
@Import({MockConfig.class})
@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private EmployeeMapper employeeMapper;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private TeamRepository teamRepository;

    @InjectMocks
    private EmployeeService service;

    @BeforeAll
    static void init() {
    }

    @Test
    @DisplayName("should return employee dto")
    void getEmployee_found() {
        var team = new Team();
        team.setId(1L);
        team.setName("TestTeam");
        team.setTeamLead(new Employee(1L, "TestTeamLead", team));
        Employee employee = new Employee(2L, "TestName", team);
        var dto = new EmployeeDto(employee.getId(), employee.getName(), team.getName(), team.getTeamLead().getName());
        Mockito.when(employeeRepository.findWithTeamAndLeadById(anyLong())).thenReturn(Optional.of(employee));
        Mockito.when(employeeMapper.toDto(any(Employee.class))).thenReturn(dto);

        var resultDto = service.getEmployee(2L);

        assertEquals(dto, resultDto);
        verify(employeeRepository, times(1)).findWithTeamAndLeadById(2L);
        verify(employeeMapper, times(1)).toDto(employee);
    }

    @Test
    @DisplayName("should not find employee and throw error")
    void getEmployee_notFound() {
        Mockito.when(employeeRepository.findWithTeamAndLeadById(anyLong())).thenReturn(Optional.empty());
        assertThrows(
                NoSuchElementException.class,
                () -> service.getEmployee(1L)
        );
        verify(employeeRepository, times(1)).findWithTeamAndLeadById(1L);
    }

    @Test
    @DisplayName("should return empty list")
    void getAllEmployees_empty() {
        Mockito.when(employeeRepository.findAll()).thenReturn(new ArrayList<>());
        Mockito.when(employeeMapper.toDtoList(any(Iterable.class))).thenReturn(new ArrayList<>());

        var resultDto = service.getAllEmployees();

        assertTrue(resultDto.isEmpty());
        verify(employeeRepository, times(1)).findAll();
        verify(employeeMapper, times(1)).toDtoList(new ArrayList<>());
    }

    @Test
    @DisplayName("should find all teamLeads by filter")
    void findAllEmployees_foundTeamLead() {
        var team1 = new Team();
        team1.setId(1L);
        team1.setName("TestTeam1");
        var lead1 = new Employee(2L, "TestName", team1);
        team1.setTeamLead(lead1);
        var team2 = new Team();
        team2.setId(1L);
        team2.setName("TestTeam2");
        var lead2 = new Employee(2L, "TestName", team2);
        team2.setTeamLead(lead2);
        List<Employee> leads = List.of(lead1, lead2);
        List<EmployeeDto> dtos = List.of(new EmployeeDto(lead1.getId(), lead1.getName(), lead1.getTeam().getName(), lead1.getTeam().getTeamLead().getName()),
                new EmployeeDto(lead2.getId(), lead2.getName(), lead2.getTeam().getName(), lead2.getTeam().getTeamLead().getName()));
        Mockito.when(employeeRepository.findAllTeamLeads(any(), any())).thenReturn(leads);
        Mockito.when(employeeMapper.toDtoList(any(Iterable.class))).thenReturn(dtos);

        var filter = new FilterDto();
        filter.setTeamLeadsOnly(true);
        var resultDtos = service.findEmployees(filter);

        assertThat(resultDtos, containsInAnyOrder(dtos.toArray(new EmployeeDto[]{})));
        verify(employeeRepository, times(1)).findAllTeamLeads(any(), any());
        verify(employeeMapper, times(1)).toDtoList(any(Iterable.class));
    }

    @Test
    @DisplayName("should find a team and create employee")
    void createEmployee_created() {
        var team = new Team();
        team.setId(1L);
        team.setName("TestTeam");
        team.setTeamLead(new Employee(1L, "TestTeamLead", team));
        Employee requestEmployee = new Employee(2L, "TestName", team);
        requestEmployee.setId(null);
        Employee employee = new Employee(2L, "TestName", team);
        var requestDto = new EmployeeDto(employee.getName(), team.getName(), team.getTeamLead().getName());
        var dto = new EmployeeDto(employee.getId(), employee.getName(), team.getName(), team.getTeamLead().getName());
        Mockito.when(teamRepository.findTeamByName(anyString())).thenReturn(Optional.of(team));
        Mockito.when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        Mockito.when(employeeMapper.toDto(any(Employee.class))).thenReturn(dto);

        var resultDto = service.createEmployee(requestDto);

        assertEquals(dto, resultDto);
        verify(teamRepository, times(1)).findTeamByName("TestTeam");
        verify(employeeRepository, times(1)).save(any(Employee.class));
        verify(employeeMapper, times(1)).toDto(employee);
    }

    @Test
    @DisplayName("shouldn't find a team and should throw an exception")
    void createEmployee_teamNotFound() {
        var requestDto = new EmployeeDto("TestName", "TestTeam", "TestTeamLead");
        Mockito.when(teamRepository.findTeamByName(anyString())).thenThrow(new EntityNotFoundException());

        assertThrows(EntityNotFoundException.class, () -> service.createEmployee(requestDto));
        verify(teamRepository, times(1)).findTeamByName("TestTeam");
        verify(employeeRepository, times(0)).save(any(Employee.class));
        verify(employeeMapper, times(0)).toDto(any());
    }

    @Test
    @DisplayName("should update an employee successfully")
    void updateEmployee_updated() {
        var team = new Team();
        team.setId(1L);
        team.setName("TestTeam");
        team.setTeamLead(new Employee(1L, "TestTeamLead", team));
        Employee employee = new Employee(2L, "TestNameUpd", team);
        var requestDto = new EmployeeDto(employee.getName(), team.getName(), team.getTeamLead().getName());
        Mockito.when(teamRepository.findTeamByName(anyString())).thenReturn(Optional.of(team));
        Mockito.when(employeeRepository.updateEmployee(anyLong(), anyString(), any(Team.class))).thenReturn(1);

        assertDoesNotThrow(() -> service.updateEmployee(2L, requestDto));
        verify(employeeRepository, times(1)).updateEmployee(anyLong(), anyString(), any(Team.class));
        verify(teamRepository, times(1)).findTeamByName("TestTeam");
    }

    @Test
    @DisplayName("cannot update employee and throws an exception")
    void updateEmployee_cannotUpdate() {
        var requestDto = new EmployeeDto("TestName", "TestTeam", "TestTeamLead");
        Mockito.when(teamRepository.findTeamByName(anyString())).thenThrow(new EntityNotFoundException());

        assertThrows(EntityNotFoundException.class, () -> service.updateEmployee(1L, requestDto));
        verify(teamRepository, times(1)).findTeamByName("TestTeam");
        verify(employeeRepository, times(0)).save(any(Employee.class));
        verify(employeeMapper, times(0)).toDto(any());
    }

    @Test
    @DisplayName("should delete an employee successfully")
    void deleteEmployee_deleted() {
        Mockito.doNothing().when(employeeRepository).deleteById(anyLong());

        assertDoesNotThrow(() -> service.removeEmployee(1L));
        verify(employeeRepository, times(1)).deleteById(anyLong());
    }

}
