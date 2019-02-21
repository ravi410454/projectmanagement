package org.cognizant.projectmanagement.controller;

import org.cognizant.projectmanagement.api.Task;
import org.cognizant.projectmanagement.repo.TaskRepository;
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
@WebMvcTest(TaskController.class)
public class TaskControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TaskRepository taskRepository;


    @Test
    public void testGetTask() throws Exception {
        when(taskRepository.findById(1l)).thenReturn(Optional.of(createMockTask()));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/task/1").accept(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        JSONAssert.assertEquals("{taskId:1, parentId:2, projectId:3, task:Test, startDate:2019-02-01, endDate:2019-12-31, priority:1,status:Status}",
                result.getResponse().getContentAsString(), false);

        try {
            requestBuilder = MockMvcRequestBuilders.get("/task/2").accept(MediaType.APPLICATION_JSON);
            mvc.perform(requestBuilder).andReturn();
        } catch (Exception ex) {
            assertEquals("Not found: id-2", ex.getCause().getMessage());
        }
    }

    @Test
    public void testAddTask() throws Exception {
        String request = "{\"taskId\":\"1\", \"parentId\":\"2\", \"projectId\":\"3\", \"task\":\"Test\", \"startDate\":\"2019-02-01\", " +
                "\"endDate\":\"2019-12-31\", \"priority\":\"1\", \"status\":\"Status\"}";
        when(taskRepository.save(any(Task.class))).thenReturn(createMockTask());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/task").accept(MediaType.APPLICATION_JSON)
                .content(request).contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
        assertEquals("http://localhost/task/1",
                result.getResponse().getHeader(HttpHeaders.LOCATION));

        requestBuilder = MockMvcRequestBuilders.get("/task").accept(MediaType.APPLICATION_JSON)
                .content(request).contentType(MediaType.APPLICATION_JSON);
        result = mvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), result.getResponse().getStatus());

        when(taskRepository.save(any(Task.class))).thenReturn(null);
        requestBuilder = MockMvcRequestBuilders.post("/task").accept(MediaType.APPLICATION_JSON)
                .content(request).contentType(MediaType.APPLICATION_JSON);
        result = mvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.NO_CONTENT.value(), result.getResponse().getStatus());
    }

    @Test
    public void testEditTask() throws Exception {
        String request = "{\"taskId\":\"1\", \"parentId\":\"2\", \"projectId\":\"3\", \"task\":\"Test\", \"startDate\":\"2019-02-01\", " +
                "\"endDate\":\"2019-12-31\", \"priority\":\"1\", \"status\":\"Status\"}";
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(createMockTask()));
        Task task = createMockTask();
        task.setPriority(5);
        task.setStatus("Done");
        when(taskRepository.save(any(Task.class))).thenReturn(createMockTask());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/task/1").accept(MediaType.APPLICATION_JSON)
                .content(request).contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.NO_CONTENT.value(), result.getResponse().getStatus());

        when(taskRepository.findById(anyLong())).thenReturn(Optional.empty());
        requestBuilder = MockMvcRequestBuilders.put("/task/1").accept(MediaType.APPLICATION_JSON)
                .content(request).contentType(MediaType.APPLICATION_JSON);
        result = mvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
    }

    @Test
    public void testDeleteTask() throws Exception {
        doNothing().when(taskRepository).deleteById(anyLong());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/task/1").accept(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    private Task createMockTask() {
        Task task = new Task();
        task.setTask("Test");
        task.setStatus("Status");
        task.setPriority(1);
        task.setParentId(2);
        task.setProjectId(3);
        task.setTaskId(1);
        task.setStartDate(LocalDate.of(2019, 02, 01));
        task.setEndDate(LocalDate.of(2019, 12, 31));
        return task;
    }
}
