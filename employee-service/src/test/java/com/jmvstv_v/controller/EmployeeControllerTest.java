package com.jmvstv_v.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jmvstv_v.config.MockConfig;
import com.jmvstv_v.configuration.GlobalExceptionHandler;
import com.jmvstv_v.dto.EmployeeDto;
import com.jmvstv_v.dto.ErrorDto;
import com.jmvstv_v.dto.FilterDto;
import com.jmvstv_v.service.EmployeeService;
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

@WebMvcTest(EmployeeController.class)
@Import({MockConfig.class, GlobalExceptionHandler.class})
@ActiveProfiles("test")
class EmployeeControllerTest {

    @Autowired
    MockMvc mvc;

    private static final ObjectMapper mapper = new ObjectMapper();

    @MockitoBean
    EmployeeService service;

    @Value("${spring.security.user.name}")
    private String username;

    @Value("${spring.security.user.password}")
    private String password;

    @Test
    @DisplayName("should return employee on getEmployee by id")
    void getEmployee_ok() throws Exception {
        var expected = new EmployeeDto(1L, "TestUser", "TestTeam", "TestTeamLead");
        Mockito.when(service.getEmployee(anyLong())).thenReturn(expected);
        var response = mvc.perform(get("/employees/1")
                        .with(httpBasic(username, password)))
                .andExpect(status().isOk()).andReturn();
        var actual = mapper.readValue(response.getResponse().getContentAsByteArray(), EmployeeDto.class);

        verify(service, times(1)).getEmployee(anyLong());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("should return employee on getAllEmployees")
    void getEmployees_ok() throws Exception {
        var expected = List.of(
                new EmployeeDto(1L, "TestUser", "TestTeam", "TestTeamLead"),
                new EmployeeDto(2L, "TestUser", "TestTeam", "TestTeamLead"));
        Mockito.when(service.getAllEmployees()).thenReturn(expected);
        var response = mvc.perform(get("/employees")
                        .with(httpBasic(username, password)))
                .andExpect(status().isOk()).andReturn();
        var actual = mapper.readValue(response.getResponse().getContentAsByteArray(), new TypeReference<List<EmployeeDto>>() {
        });

        verify(service, times(1)).getAllEmployees();
        assertThat(actual, containsInAnyOrder(expected.toArray(new EmployeeDto[]{})));
    }

    @Test
    @DisplayName("should return employees on findEmployees")
    void findEmployees_ok() throws Exception {
        var filter = new FilterDto("TestName", "TestTeam", "TestTeamLead", 1L, true);
        var expected = List.of(
                new EmployeeDto(1L, "TestUser", "TestTeam", "TestTeamLead"),
                new EmployeeDto(2L, "TestUser", "TestTeam", "TestTeamLead"));
        Mockito.when(service.findEmployees(any(FilterDto.class))).thenReturn(expected);
        var response = mvc.perform(post("/employees/find")
                        .with(httpBasic(username, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(filter)))
                .andExpect(status().isOk()).andReturn();
        var actual = mapper.readValue(response.getResponse().getContentAsByteArray(),
                new TypeReference<List<EmployeeDto>>() {
                });

        verify(service, times(1)).findEmployees(any(FilterDto.class));
        assertThat(actual, containsInAnyOrder(expected.toArray(new EmployeeDto[]{})));
    }

    @Test
    @DisplayName("should return created employee on createEmployee")
    void createEmployee_ok() throws Exception {
        var request = new EmployeeDto("TestName", "TestTeam", "TestTeamLead");
        var expected = new EmployeeDto(1L, "TestName", "TestTeam", "TestTeamLead");
        Mockito.when(service.createEmployee(any(EmployeeDto.class))).thenReturn(expected);
        var response = mvc.perform(post("/employees")
                        .with(httpBasic(username, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated()).andReturn();
        var actual = mapper.readValue(response.getResponse().getContentAsByteArray(), EmployeeDto.class);

        verify(service, times(1)).createEmployee(any(EmployeeDto.class));
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("should return validation error")
    void createEmployee_validationError() throws Exception {
        var request = new EmployeeDto(1L, "", "TestTeam", "TestTeamLead");
        var response = mvc.perform(post("/employees")
                        .with(httpBasic(username, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()).andReturn();
        var actual = mapper.readValue(response.getResponse().getContentAsByteArray(), ErrorDto.class);

        verify(service, times(0)).createEmployee(any(EmployeeDto.class));
        assertEquals("http://localhost/docs#errors/bad-request", actual.getType());
        assertEquals(400, actual.getCode());
        assertEquals("Bad Request", actual.getTitle());
        assertEquals("/employees", actual.getInstance());
        assertTrue(actual.getDetail().contains("'name'"));
    }

    @Test
    @DisplayName("should return internal error")
    void createEmployee_internalError() throws Exception {
        var request = new EmployeeDto(1L, "TestName", "TestTeam", "TestTeamLead");
        Mockito.doThrow(new EntityNotFoundException("Team not found: TestTeam")).when(service)
                .createEmployee(any(EmployeeDto.class));
        var response = mvc.perform(post("/employees")
                        .with(httpBasic(username, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError()).andReturn();
        var actual = mapper.readValue(response.getResponse().getContentAsByteArray(), ErrorDto.class);

        verify(service, times(1)).createEmployee(any(EmployeeDto.class));
        assertEquals("http://localhost/docs#errors/internal-server-error", actual.getType());
        assertEquals(500, actual.getCode());
        assertEquals("Internal Server Error", actual.getTitle());
        assertEquals("/employees", actual.getInstance());
        assertEquals("Team not found: TestTeam", actual.getDetail());
    }

    @Test
    @DisplayName("should return nothing on updateEmployee")
    void updateEmployee_ok() {
        var request = new EmployeeDto(1L, "TestName1", "TestTeam", "TestTeamLead");
        var expected = new EmployeeDto(1L, "TestName1", "TestTeam", "TestTeamLead");
        Mockito.when(service.createEmployee(any(EmployeeDto.class))).thenReturn(expected);
        assertDoesNotThrow(() -> mvc.perform(patch("/employees/1")
                        .with(httpBasic(username, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isNoContent()));
        verify(service, times(1)).updateEmployee(anyLong(), any(EmployeeDto.class));
    }

    @Test
    @DisplayName("should return validation error")
    void updateEmployee_validationError() throws Exception {
        var request = new EmployeeDto(1L, "", "TestTeam", "TestTeamLead");
        var response = mvc.perform(patch("/employees/1")
                        .with(httpBasic(username, password))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest()).andReturn();
        var actual = mapper.readValue(response.getResponse().getContentAsByteArray(), ErrorDto.class);

        verify(service, times(0)).updateEmployee(anyLong(), any(EmployeeDto.class));
        assertEquals("http://localhost/docs#errors/bad-request", actual.getType());
        assertEquals(400, actual.getCode());
        assertEquals("Bad Request", actual.getTitle());
        assertEquals("/employees/1", actual.getInstance());
        assertTrue(actual.getDetail().contains("'name'"));
    }

    @Test
    @DisplayName("should return nothing on removeEmployee")
    void removeEmployee_ok() {
        Mockito.doNothing().when(service).removeEmployee(anyLong());
        assertDoesNotThrow(() -> mvc.perform(delete("/employees/1")
                        .with(httpBasic(username, password)))
                .andExpect(status().isNoContent()));
        verify(service, times(1)).removeEmployee(anyLong());
    }

}
