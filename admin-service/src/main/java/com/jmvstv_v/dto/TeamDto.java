package com.jmvstv_v.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

public class TeamDto {

    private Long id;

    @NotBlank(message = "Team name must not be empty")
    private String name;

    private Long teamLeadId;

    public TeamDto() {
    }

    public TeamDto(Long id, String name, Long teamLeadId) {
        this.id = id;
        this.name = name;
        this.teamLeadId = teamLeadId;
    }

    public TeamDto(String name, Long teamLeadId) {
        this.name = name;
        this.teamLeadId = teamLeadId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTeamLeadId() {
        return teamLeadId;
    }

    public void setTeamLeadId(Long teamLeadId) {
        this.teamLeadId = teamLeadId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TeamDto teamDto)) return false;
        return Objects.equals(id, teamDto.id) && Objects.equals(name, teamDto.name) && Objects.equals(teamLeadId, teamDto.teamLeadId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, teamLeadId);
    }
}
