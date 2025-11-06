package com.jmvstv_v.mapper;

import com.jmvstv_v.dto.EmployeeDto;
import com.jmvstv_v.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    @Mapping(source = "team.name", target = "team")
    @Mapping(source = "team.teamLead.name", target = "teamLead")
    EmployeeDto toDto(Employee employee);

    List<EmployeeDto> toDtoList(Iterable<Employee> employees);
}
