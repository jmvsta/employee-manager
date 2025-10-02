package com.jmvstv_v.dto;

import java.util.Objects;

public class FilterDto {
    
    private String name;

    private String teamName;

    private String teamLeadName;

    private Long teamLeadId;

    private Boolean teamLeadsOnly;

    public FilterDto() {
    }

    public FilterDto(String name, String teamName, String teamLeadName, Long teamLeadId, Boolean teamLeadsOnly) {
        this.name = name;
        this.teamName = teamName;
        this.teamLeadName = teamLeadName;
        this.teamLeadId = teamLeadId;
        this.teamLeadsOnly = teamLeadsOnly;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamLeadName() {
        return teamLeadName;
    }

    public void setTeamLeadName(String teamLeadName) {
        this.teamLeadName = teamLeadName;
    }

    public Long getTeamLeadId() {
        return teamLeadId;
    }

    public void setTeamLeadId(Long teamLeadId) {
        this.teamLeadId = teamLeadId;
    }

    public Boolean getTeamLeadsOnly() {
        return teamLeadsOnly;
    }

    public void setTeamLeadsOnly(Boolean teamLeadsOnly) {
        this.teamLeadsOnly = teamLeadsOnly;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FilterDto filterDto)) return false;
        return Objects.equals(name, filterDto.name) && Objects.equals(teamName, filterDto.teamName) && Objects.equals(teamLeadName, filterDto.teamLeadName) && Objects.equals(teamLeadId, filterDto.teamLeadId) && Objects.equals(teamLeadsOnly, filterDto.teamLeadsOnly);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, teamName, teamLeadName, teamLeadId, teamLeadsOnly);
    }
}
