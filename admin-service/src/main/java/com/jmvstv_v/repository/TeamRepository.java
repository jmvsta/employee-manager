package com.jmvstv_v.repository;

import com.jmvstv_v.entity.Team;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends CrudRepository<Team, Long> {

    @EntityGraph(attributePaths = {"teamLead"})
    Optional<Team> findWithTeamLeadById(Long id);

    @EntityGraph(attributePaths = {"teamLead"})
    Iterable<Team> findAll();

    @EntityGraph(attributePaths = {"teamLead"})
    Optional<Team> findTeamByName(String name);

    @Modifying
    @Query(value = "UPDATE team SET name = COALESCE(:name, name), teamlead_id  = :teamLeadId WHERE id = :id", nativeQuery = true)
    int updateTeam(@Param("id") Long id, @Param("name") String name, @Param("teamLeadId") Long teamLeadId);

}
