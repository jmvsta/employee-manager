package com.jmvstv_v.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Objects;

public class EmployeeDto {

    private Long id;

    @NotBlank(message = "Employee name must not be empty")
    private String name;

    private String team;

    private String teamLead;

    public EmployeeDto() {
    }

    public EmployeeDto(Long id, String name, String team, String teamLead) {
        this.id = id;
        this.name = name;
        this.team = team;
        this.teamLead = teamLead;
    }

    public EmployeeDto(String name, String team, String teamLead) {
        this.name = name;
        this.team = team;
        this.teamLead = teamLead;
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

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getTeamLead() {
        return teamLead;
    }

    public void setTeamLead(String teamLead) {
        this.teamLead = teamLead;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EmployeeDto that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(team, that.team) && Objects.equals(teamLead, that.teamLead);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, team, teamLead);
    }
}
