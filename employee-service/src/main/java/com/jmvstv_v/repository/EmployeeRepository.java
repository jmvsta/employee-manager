package com.jmvstv_v.repository;

import com.jmvstv_v.entity.Employee;
import com.jmvstv_v.entity.Team;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeRepository extends CrudRepository<Employee, Long> {

    @EntityGraph(attributePaths = {"team", "team.teamLead"})
    Optional<Employee> findWithTeamAndLeadById(Long id);

    @EntityGraph(attributePaths = {"team", "team.teamLead"})
    Iterable<Employee> findAll();

    @Modifying
    @Query(value = "UPDATE Employee SET name = COALESCE(:name, name), team = :team WHERE id = :id")
    int updateEmployee(@Param("id") Long id, @Param("name") String name, @Param("team") Team team);

    @Query("""
              SELECT teamLead FROM Team team 
                JOIN team.teamLead teamLead 
                JOIN FETCH teamLead.team
                WHERE (:teamName IS NULL OR team.name = :teamName)
                AND (:name IS NULL OR teamLead.name = :name)
            """)
    Iterable<Employee> findAllTeamLeads(@Param("teamName") String teamName, @Param("name") String name);


    @Query("""
              SELECT employee FROM Employee employee
                LEFT JOIN FETCH employee.team team 
                LEFT JOIN FETCH team.teamLead teamLead
                WHERE (:teamName IS NULL OR team IS NOT NULL AND team.name = :teamName)
                AND (:teamLeadName IS NULL OR teamLead IS NOT NULL AND teamLead.name = :teamLeadName)
                AND (:teamLeadId IS NULL OR teamLead IS NOT NULL AND teamLead.id = :teamLeadId)
                AND (:name IS NULL OR employee.name = :name)
            """)
    Iterable<Employee> findAllWithFilters(
            @Param("teamName") String teamName,
            @Param("name") String name,
            @Param("teamLeadName") String teamLeadName,
            @Param("teamLeadId") Long teamLeadId
    );

    boolean existsEmployeeByTeam_Id(Long teamId);
}
