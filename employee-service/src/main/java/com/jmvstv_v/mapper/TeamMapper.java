package com.jmvstv_v.mapper;

import com.jmvstv_v.dto.TeamDto;
import com.jmvstv_v.entity.Team;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TeamMapper {

    @Mapping(source = "teamLead.id", target = "teamLeadId")
    TeamDto toDto(Team team);

    List<TeamDto> toDtoList(Iterable<Team> employees);
}
