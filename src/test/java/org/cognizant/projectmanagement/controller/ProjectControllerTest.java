package org.cognizant.projectmanagement.controller;

import org.cognizant.projectmanagement.api.Project;
import org.cognizant.projectmanagement.repo.ProjectRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(ProjectController.class)
public class ProjectControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ProjectRepository projectRepository;


    @Test
    public void testGetProject() throws Exception {
        Optional<Project> project = Optional.of(createMockProject());
        when(projectRepository.findById(1l)).thenReturn(project);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/project/1").accept(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        JSONAssert.assertEquals("{projectId:1,project:Test,priority:2,startDate:2019-02-01,endDate:2019-12-31}",
                result.getResponse().getContentAsString(), false);

        try {
            requestBuilder = MockMvcRequestBuilders.get("/project/2").accept(MediaType.APPLICATION_JSON);
            mvc.perform(requestBuilder).andReturn();
        } catch (Exception ex) {
            assertEquals("Not found: id-2", ex.getCause().getMessage());
        }
    }

    @Test
    public void testAddProject() throws Exception {
        String request = "{\"projectId\":\"1\",\"project\":\"Test\",\"priority\":\"2\",\"startDate\":\"2019-02-01\",\"endDate\":\"2019-12-31\"}";
        when(projectRepository.save(any(Project.class))).thenReturn(createMockProject());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/project").accept(MediaType.APPLICATION_JSON)
                .content(request).contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
        assertEquals("http://localhost/project/1",
                result.getResponse().getHeader(HttpHeaders.LOCATION));

        requestBuilder = MockMvcRequestBuilders.get("/project").accept(MediaType.APPLICATION_JSON)
                .content(request).contentType(MediaType.APPLICATION_JSON);
        result = mvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), result.getResponse().getStatus());

        when(projectRepository.save(any(Project.class))).thenReturn(null);
        requestBuilder = MockMvcRequestBuilders.post("/project").accept(MediaType.APPLICATION_JSON)
                .content(request).contentType(MediaType.APPLICATION_JSON);
        result = mvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.NO_CONTENT.value(), result.getResponse().getStatus());
    }

    @Test
    public void testEditProject() throws Exception {
        String request = "{\"projectId\":\"1\",\"project\":\"Test\",\"priority\":\"2\",\"startDate\":\"2019-02-01\",\"endDate\":\"2019-12-31\"}";
        when(projectRepository.findById(anyLong())).thenReturn(Optional.of(createMockProject()));
        Project project = createMockProject();
        project.setProject("Test Project");
        project.setPriority(3);
        when(projectRepository.save(any(Project.class))).thenReturn(createMockProject());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/project/1").accept(MediaType.APPLICATION_JSON)
                .content(request).contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.NO_CONTENT.value(), result.getResponse().getStatus());

        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());
        requestBuilder = MockMvcRequestBuilders.put("/project/1").accept(MediaType.APPLICATION_JSON)
                .content(request).contentType(MediaType.APPLICATION_JSON);
        result = mvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
    }

    @Test
    public void testDeleteProject() throws Exception {
        doNothing().when(projectRepository).deleteById(anyLong());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/project/1").accept(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    private Project createMockProject() {
        Project project = new Project();
        project.setProjectId(1);
        project.setProject("Test");
        project.setPriority(2);
        project.setStartDate(LocalDate.of(2019, 02, 01));
        project.setEndDate(LocalDate.of(2019, 12, 31));
        return project;
    }

}
