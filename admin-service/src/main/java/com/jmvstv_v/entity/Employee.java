package com.jmvstv_v.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Employee {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "employee_seq"
    )
    @SequenceGenerator(
            name = "employee_seq",
            sequenceName = "employee_seq",
            allocationSize = 1
    )
    private Long id;

    @NotBlank
    private String name;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    public Employee() {
    }

    public Employee(Long id, String name, Team team) {
        this.id = id;
        this.name = name;
        this.team = team;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

}
