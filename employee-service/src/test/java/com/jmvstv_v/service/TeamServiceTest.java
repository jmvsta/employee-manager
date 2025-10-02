package com.jmvstv_v.service;

import com.jmvstv_v.config.MockConfig;
import com.jmvstv_v.dto.TeamDto;
import com.jmvstv_v.entity.Employee;
import com.jmvstv_v.entity.Team;
import com.jmvstv_v.mapper.TeamMapper;
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
import java.util.NoSuchElementException;
import java.util.Optional;

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
class TeamServiceTest {

    @Mock
    private TeamMapper teamMapper;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private TeamService service;

    @BeforeAll
    static void init() {
    }

    @Test
    @DisplayName("should return team dto")
    void getTeam_found() {
        var team = new Team();
        team.setId(1L);
        team.setName("TestTeam");
        team.setTeamLead(new Employee(1L, "TestTeamLead", team));
        var dto = new TeamDto(team.getId(), team.getName(), team.getTeamLead().getId());
        Mockito.when(teamRepository.findWithTeamLeadById(anyLong())).thenReturn(Optional.of(team));
        Mockito.when(teamMapper.toDto(any(Team.class))).thenReturn(dto);

        var resultDto = service.getTeam(2L);

        assertEquals(dto, resultDto);
        verify(teamRepository, times(1)).findWithTeamLeadById(2L);
        verify(teamMapper, times(1)).toDto(team);
    }

    @Test
    @DisplayName("should not find team and throw error")
    void getTeam_notFound() {
        Mockito.when(teamRepository.findWithTeamLeadById(anyLong())).thenReturn(Optional.empty());
        assertThrows(
                NoSuchElementException.class,
                () -> service.getTeam(1L)
        );
        verify(teamRepository, times(1)).findWithTeamLeadById(1L);
    }

    @Test
    @DisplayName("should return empty list")
    void getAllTeams_empty() {
        Mockito.when(teamRepository.findAll()).thenReturn(new ArrayList<>());
        Mockito.when(teamMapper.toDtoList(any(Iterable.class))).thenReturn(new ArrayList<>());

        var resultDto = service.getAllTeams();

        assertTrue(resultDto.isEmpty());
        verify(teamRepository, times(1)).findAll();
        verify(teamMapper, times(1)).toDtoList(new ArrayList<>());
    }

    @Test
    @DisplayName("should find a team and create team")
    void createTeam_created() {
        var teamLead = new Employee(1L, "TestTeamLead", null);
        var team = new Team();
        team.setId(1L);
        team.setName("TestTeam");
        team.setTeamLead(teamLead);
        teamLead.setTeam(team);
        var requestDto = new TeamDto(team.getName(), team.getTeamLead().getId());
        var dto = new TeamDto(team.getId(), team.getName(), team.getTeamLead().getId());
        Mockito.when(employeeRepository.findWithTeamAndLeadById(anyLong())).thenReturn(Optional.of(teamLead));
        Mockito.when(teamRepository.save(any(Team.class))).thenReturn(team);
        Mockito.when(teamMapper.toDto(any(Team.class))).thenReturn(dto);

        var resultDto = service.createTeam(requestDto);

        assertEquals(dto, resultDto);
        verify(employeeRepository, times(1)).findWithTeamAndLeadById(anyLong());
        verify(teamRepository, times(1)).save(any(Team.class));
        verify(teamMapper, times(1)).toDto(team);
    }

    @Test
    @DisplayName("shouldn't find a team and should throw an exception")
    void createTeam_teamNotFound() {
        var requestDto = new TeamDto("TestTeam", 1L);
        Mockito.when(employeeRepository.findWithTeamAndLeadById(anyLong())).thenThrow(new EntityNotFoundException());

        assertThrows(EntityNotFoundException.class, () -> service.createTeam(requestDto));
        verify(employeeRepository, times(1)).findWithTeamAndLeadById(anyLong());
        verify(teamRepository, times(0)).save(any(Team.class));
        verify(teamMapper, times(0)).toDto(any());
    }

    @Test
    @DisplayName("should update an team successfully")
    void updateTeam_updated() {
        var requestDto = new TeamDto("TestTeam", 1L);
        Mockito.when(teamRepository.updateTeam(anyLong(), anyString(), anyLong())).thenReturn(1);

        assertDoesNotThrow(() -> service.updateTeam(2L, requestDto));
        verify(teamRepository, times(1)).updateTeam(anyLong(), anyString(), anyLong());
    }

    @Test
    @DisplayName("cannot update team and throws an exception")
    void updateTeam_cannotUpdate() {
        var requestDto = new TeamDto("TestTeam", 1L);
        Mockito.when(teamRepository.updateTeam(anyLong(), anyString(), anyLong())).thenThrow(new EntityNotFoundException());

        assertThrows(EntityNotFoundException.class, () -> service.updateTeam(1L, requestDto));
        verify(teamRepository, times(1)).updateTeam(anyLong(), anyString(), anyLong());
        verify(teamRepository, times(0)).save(any(Team.class));
        verify(teamMapper, times(0)).toDto(any());
    }

    @Test
    @DisplayName("should delete an team successfully")
    void deleteTeam_deleted() {
        Mockito.doNothing().when(teamRepository).deleteById(anyLong());

        assertDoesNotThrow(() -> service.removeTeam(1L));
        verify(teamRepository, times(1)).deleteById(anyLong());
    }

}
