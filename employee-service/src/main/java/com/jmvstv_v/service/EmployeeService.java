package com.jmvstv_v.service;

import com.jmvstv_v.dto.EmployeeDto;
import com.jmvstv_v.dto.FilterDto;
import com.jmvstv_v.entity.Employee;
import com.jmvstv_v.entity.Team;
import com.jmvstv_v.mapper.EmployeeMapper;
import com.jmvstv_v.repository.EmployeeRepository;
import com.jmvstv_v.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class EmployeeService {

    private final EmployeeMapper employeeMapper;

    private final EmployeeRepository employeeRepository;

    private final TeamRepository teamRepository;

    public EmployeeService(
            EmployeeMapper employeeMapper,
            EmployeeRepository employeeRepository,
            TeamRepository teamRepository
    ) {
        this.employeeMapper = employeeMapper;
        this.employeeRepository = employeeRepository;
        this.teamRepository = teamRepository;
    }

    @Transactional(readOnly = true)
    public EmployeeDto getEmployee(Long id) throws NoSuchElementException {
        var employee = employeeRepository.findWithTeamAndLeadById(id).orElseThrow();
        return employeeMapper.toDto(employee);
    }

    @Transactional(readOnly = true)
    public List<EmployeeDto> getAllEmployees() {
        var employees = employeeRepository.findAll();
        return employeeMapper.toDtoList(employees);
    }

    @Transactional
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {
        var teamName = employeeDto.getTeam();
        var employee = new Employee();
        if (teamName != null) {
            var team = teamRepository.findTeamByName(teamName)
                    .orElseThrow(() -> new EntityNotFoundException("Team not found: " + teamName));
            employee.setTeam(team);
        }
        employee.setName(employeeDto.getName());
        var savedEmployee = employeeRepository.save(employee);

        return employeeMapper.toDto(savedEmployee);
    }

    @Transactional
    public void updateEmployee(Long id, EmployeeDto employeeDto) {
        Team team = null;
        var teamName = employeeDto.getTeam();
        if (!Strings.isEmpty(teamName)) {
            team = teamRepository.findTeamByName(teamName)
                    .orElseThrow(() -> new EntityNotFoundException("TeamId not found: " + teamName));
        }
        int updated = employeeRepository.updateEmployee(id, employeeDto.getName(), team);
        if (updated != 1) {
            throw new EntityNotFoundException("Employee not found: " + id);
        }
    }

    @Transactional
    public void removeEmployee(Long id) {
        employeeRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<EmployeeDto> findEmployees(FilterDto filterDto) {
        if (filterDto.getTeamLeadsOnly() != null && filterDto.getTeamLeadsOnly()) {
            var teamLeads = employeeRepository.findAllTeamLeads(filterDto.getTeamName(), filterDto.getName());
            return employeeMapper.toDtoList(teamLeads);
        } else {
            var employees = employeeRepository.findAllWithFilters(
                    filterDto.getTeamName(),
                    filterDto.getName(),
                    filterDto.getTeamLeadName(),
                    filterDto.getTeamLeadId());
            return employeeMapper.toDtoList(employees);
        }
    }

}
