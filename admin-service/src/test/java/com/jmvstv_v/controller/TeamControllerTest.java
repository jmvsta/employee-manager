package com.jmvstv_v.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jmvstv_v.config.MockConfig;
import com.jmvstv_v.configuration.GlobalExceptionHandler;
import com.jmvstv_v.dto.TeamDto;
import com.jmvstv_v.dto.ErrorDto;
import com.jmvstv_v.service.TeamService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TeamController.class)
@Import({MockConfig.class, GlobalExceptionHandler.class})
@ActiveProfiles("test")
class TeamControllerTest {

    @Autowired
    MockMvc mvc;

    private static final ObjectMapper mapper = new ObjectMapper();

    @MockitoBean
    TeamService service;

    @Value("${spring.security.user.name}")
    private String username;

    @Value("${spring.security.user.password}")
    private String password;

    @Test
    @DisplayName("should return team on getTeam by id")
    void getTeam_ok() throws Exception {
        var expected = new TeamDto(1L, "TestTeam", 1L);
        Mockito.when(service.getTeam(anyLong())).thenReturn(expected);
        var response = mvc.perform(get("/teams/1")
                        .with(httpBasic(username, password)))
                .andExpect(status().isOk()).andReturn();
        var actual = mapper.readValue(response.getResponse().getContentAsByteArray(), TeamDto.class);

        verify(service, times(1)).getTeam(anyLong());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("should return team on getAllTeams")
    void getTeams_ok() throws Exception {
        var expected = List.of(
                new TeamDto(1L, "TestTeam", 1L),
                new TeamDto(2L, "TestTeam", 2L));
        Mockito.when(service.getAllTeams()).thenReturn(expected);
        var response = mvc.perform(get("/teams")
                        .with(httpBasic(username, password)))
                .andExpect(status().isOk()).andReturn();
        var actual = mapper.readValue(response.getResponse().getContentAsByteArray(), new TypeReference<List<TeamDto>>() {
        });

        verify(service, times(1)).getAllTeams();
        assertThat(actual, containsInAnyOrder(expected.toArray(new TeamDto[]{})));
    }

    @Test
    @DisplayName("should return created team on createTeam")
    void createTeam_ok() throws Exception {
        var request = new TeamDto("TestTeam", 1L);
        var expected = new TeamDto(1L, "TestTeam", 1L);
        Mockito.when(service.createTeam(any(TeamDto.class))).thenReturn(expected);
        var response = mvc.perform(post("/teams")
                        .with(httpBasic(username, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated()).andReturn();
        var actual = mapper.readValue(response.getResponse().getContentAsByteArray(), TeamDto.class);

        verify(service, times(1)).createTeam(any(TeamDto.class));
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("should return validation error")
    void createTeam_validationError() throws Exception {
        var request = new TeamDto(1L, "", 1L);
        var response = mvc.perform(post("/teams")
                        .with(httpBasic(username, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()).andReturn();
        var actual = mapper.readValue(response.getResponse().getContentAsByteArray(), ErrorDto.class);

        verify(service, times(0)).createTeam(any(TeamDto.class));
        assertEquals("http://localhost/docs#errors/bad-request", actual.getType());
        assertEquals(400, actual.getCode());
        assertEquals("Bad Request", actual.getTitle());
        assertEquals("/teams", actual.getInstance());
        assertTrue(actual.getDetail().contains("'name'"));
    }

    @Test
    @DisplayName("should return internal error")
    void createTeam_internalError() throws Exception {
        var request = new TeamDto(1L, "TestName", 1L);
        Mockito.doThrow(new EntityNotFoundException("Team not found: TestTeam")).when(service)
                .createTeam(any(TeamDto.class));
        var response = mvc.perform(post("/teams")
                        .with(httpBasic(username, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError()).andReturn();
        var actual = mapper.readValue(response.getResponse().getContentAsByteArray(), ErrorDto.class);

        verify(service, times(1)).createTeam(any(TeamDto.class));
        assertEquals("http://localhost/docs#errors/internal-server-error", actual.getType());
        assertEquals(500, actual.getCode());
        assertEquals("Internal Server Error", actual.getTitle());
        assertEquals("/teams", actual.getInstance());
        assertEquals("Team not found: TestTeam", actual.getDetail());
    }

    @Test
    @DisplayName("should return nothing on updateTeam")
    void updateTeam_ok() {
        var request = new TeamDto(1L, "TestTeam1", 1L);
        var expected = new TeamDto(1L, "TestTeam1",1L);
        Mockito.when(service.createTeam(any(TeamDto.class))).thenReturn(expected);
        assertDoesNotThrow(() -> mvc.perform(patch("/teams/1")
                        .with(httpBasic(username, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNoContent()));
        verify(service, times(1)).updateTeam(anyLong(), any(TeamDto.class));
    }

    @Test
    @DisplayName("should return validation error")
    void updateTeam_validationError() throws Exception {
        var request = new TeamDto(1L, "", 1L);
        var response = mvc.perform(patch("/teams/1")
                        .with(httpBasic(username, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()).andReturn();
        var actual = mapper.readValue(response.getResponse().getContentAsByteArray(), ErrorDto.class);

        verify(service, times(0)).updateTeam(anyLong(), any(TeamDto.class));
        assertEquals("http://localhost/docs#errors/bad-request", actual.getType());
        assertEquals(400, actual.getCode());
        assertEquals("Bad Request", actual.getTitle());
        assertEquals("/teams/1", actual.getInstance());
        assertTrue(actual.getDetail().contains("'name'"));
    }

    @Test
    @DisplayName("should return nothing on removeTeam")
    void removeTeam_ok() {
        Mockito.doNothing().when(service).removeTeam(anyLong());
        assertDoesNotThrow(() -> mvc.perform(delete("/teams/1")
                        .with(httpBasic(username, password)))
                .andExpect(status().isNoContent()));
        verify(service, times(1)).removeTeam(anyLong());
    }

}
