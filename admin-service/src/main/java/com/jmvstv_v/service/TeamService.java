package com.jmvstv_v.service;

import com.jmvstv_v.dto.TeamDto;
import com.jmvstv_v.entity.Team;
import com.jmvstv_v.mapper.TeamMapper;
import com.jmvstv_v.repository.EmployeeRepository;
import com.jmvstv_v.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class TeamService {

    private final TeamMapper teamMapper;

    private final EmployeeRepository employeeRepository;

    private final TeamRepository teamRepository;

    public TeamService(
            TeamMapper teamMapper,
            EmployeeRepository employeeRepository,
            TeamRepository teamRepository
        ) {
        this.teamMapper = teamMapper;
        this.employeeRepository = employeeRepository;
        this.teamRepository = teamRepository;
    }

    @Transactional(readOnly = true)
    public TeamDto getTeam(Long id) throws NoSuchElementException {
        var employee = teamRepository.findWithTeamLeadById(id).orElseThrow();
        return teamMapper.toDto(employee);
    }

    @Transactional(readOnly = true)
    public List<TeamDto> getAllTeams() {
        var teams = teamRepository.findAll();
        return teamMapper.toDtoList(teams);
    }

    @Transactional
    public TeamDto createTeam(TeamDto teamDto) {
        var teamLeadId = teamDto.getTeamLeadId();
        var team = new Team();
        if (teamLeadId != null) {
            var teamLead = employeeRepository.findWithTeamAndLeadById(teamLeadId)
                    .orElseThrow(() -> new EntityNotFoundException("Teamlead not found: " + teamLeadId));
            team.setTeamLead(teamLead);
        }

        team.setName(teamDto.getName());
        var savedTeam = teamRepository.save(team);

        return teamMapper.toDto(savedTeam);
    }

    @Transactional
    public void updateTeam(Long id, TeamDto teamDto) {
        int updated = teamRepository.updateTeam(id, teamDto.getName(), teamDto.getTeamLeadId());
        if (updated != 1) {
            throw new EntityNotFoundException("Team not found: " + id);
        }
    }

    @Transactional
    public void removeTeam(Long id) {
        if (employeeRepository.existsEmployeeByTeam_Id(id)) {
            throw new IllegalStateException("Cannot delete team with assigned employees");
        }
        teamRepository.deleteById(id);
    }
}
