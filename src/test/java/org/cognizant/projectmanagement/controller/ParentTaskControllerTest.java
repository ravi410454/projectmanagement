package org.cognizant.projectmanagement.controller;

import org.cognizant.projectmanagement.api.ParentTask;
import org.cognizant.projectmanagement.repo.ParentTaskRepository;
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

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@WebMvcTest(ParentTaskController.class)
public class ParentTaskControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ParentTaskRepository parentTaskRepository;


    @Test
    public void testGetParentTask() throws Exception {
        Optional<ParentTask> parenttask = Optional.of(createMockParentTask());
        when(parentTaskRepository.findById(1l)).thenReturn(parenttask);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/parenttask/1").accept(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        JSONAssert.assertEquals("{parentId:1,parentTask:Test}",
                result.getResponse().getContentAsString(), false);

        try {
            requestBuilder = MockMvcRequestBuilders.get("/parenttask/2").accept(MediaType.APPLICATION_JSON);
            mvc.perform(requestBuilder).andReturn();
        } catch (Exception ex) {
            assertEquals("Not found: id-2", ex.getCause().getMessage());
        }
    }

    @Test
    public void testAddParentTask() throws Exception {
        String request = "{\"parentId\":\"1\",\"parentTask\":\"Test\"}";
        when(parentTaskRepository.save(any(ParentTask.class))).thenReturn(createMockParentTask());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/parenttask").accept(MediaType.APPLICATION_JSON)
                .content(request).contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
        assertEquals("http://localhost/parenttask/1",
                result.getResponse().getHeader(HttpHeaders.LOCATION));

        requestBuilder = MockMvcRequestBuilders.get("/parenttask").accept(MediaType.APPLICATION_JSON)
                .content(request).contentType(MediaType.APPLICATION_JSON);
        result = mvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.METHOD_NOT_ALLOWED.value(), result.getResponse().getStatus());

        when(parentTaskRepository.save(any(ParentTask.class))).thenReturn(null);
        requestBuilder = MockMvcRequestBuilders.post("/parenttask").accept(MediaType.APPLICATION_JSON)
                .content(request).contentType(MediaType.APPLICATION_JSON);
        result = mvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.NO_CONTENT.value(), result.getResponse().getStatus());
    }

    @Test
    public void testEditParentTask() throws Exception {
        String request = "{\"parentId\":\"1\",\"parentTask\":\"Test\"}";
        when(parentTaskRepository.findById(anyLong())).thenReturn(Optional.of(createMockParentTask()));
        ParentTask parenttask = createMockParentTask();
        parenttask.setParentTask("ParentTask");
        when(parentTaskRepository.save(any(ParentTask.class))).thenReturn(createMockParentTask());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/parenttask/1").accept(MediaType.APPLICATION_JSON)
                .content(request).contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.NO_CONTENT.value(), result.getResponse().getStatus());

        when(parentTaskRepository.findById(anyLong())).thenReturn(Optional.empty());
        requestBuilder = MockMvcRequestBuilders.put("/parenttask/1").accept(MediaType.APPLICATION_JSON)
                .content(request).contentType(MediaType.APPLICATION_JSON);
        result = mvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
    }

    @Test
    public void testDeleteParentTask() throws Exception {
        doNothing().when(parentTaskRepository).deleteById(anyLong());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/parenttask/1").accept(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    private ParentTask createMockParentTask() {
        ParentTask parenttask = new ParentTask();
        parenttask.setParentId(1);
        parenttask.setParentTask("Test");
        return parenttask;
    }
}
