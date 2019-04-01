package org.cognizant.projectmanagement.controller;

import org.cognizant.projectmanagement.api.Users;
import org.cognizant.projectmanagement.repo.UsersRepository;
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
@WebMvcTest(UsersController.class)
public class UsersControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UsersRepository usersRepository;

    @Test
    public void testGetUser() throws Exception {
        Optional<Users> users = Optional.of(createMockUsers());
        when(usersRepository.findById(1l)).thenReturn(users);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/users/1").accept(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
        JSONAssert.assertEquals("{userId:1,firstName:TestF,lastName:TestL,employeeId:1,projectId:1,taskId:1}",
                result.getResponse().getContentAsString(), false);

        try {
            requestBuilder = MockMvcRequestBuilders.get("/users/2").accept(MediaType.APPLICATION_JSON);
            mvc.perform(requestBuilder).andReturn();
        } catch (Exception ex) {
            assertEquals("Not found: id-2", ex.getCause().getMessage());
        }
    }

    @Test
    public void testAddUser() throws Exception {
        String request = "{\"userId\":\"1\",\"firstName\":\"TestF\",\"lastName\":\"TestL\",\"employeeId\":\"1\",\"projectId\":\"1\",\"taskId\":\"1\"}";
        when(usersRepository.save(any(Users.class))).thenReturn(createMockUsers());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users").accept(MediaType.APPLICATION_JSON)
                .content(request).contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.CREATED.value(), result.getResponse().getStatus());
        assertEquals("http://localhost/users/1",
                result.getResponse().getHeader(HttpHeaders.LOCATION));

        requestBuilder = MockMvcRequestBuilders.get("/users").accept(MediaType.APPLICATION_JSON)
                .content(request).contentType(MediaType.APPLICATION_JSON);
        try {
            result = mvc.perform(requestBuilder).andReturn();
        } catch (Exception ex) {
            assertEquals("Not found", ex.getCause().getMessage());
        }

        when(usersRepository.save(any(Users.class))).thenReturn(null);
        requestBuilder = MockMvcRequestBuilders.post("/users").accept(MediaType.APPLICATION_JSON)
                .content(request).contentType(MediaType.APPLICATION_JSON);
        result = mvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.NO_CONTENT.value(), result.getResponse().getStatus());
    }

    @Test
    public void testEditUser() throws Exception {
        String request = "{\"userId\":\"1\",\"firstName\":\"First\",\"lastName\":\"Last\",\"employeeId\":\"1\",\"projectId\":\"1\",\"taskId\":\"1\"}";
        when(usersRepository.findById(anyLong())).thenReturn(Optional.of(createMockUsers()));
        Users users = createMockUsers();
        users.setFirstName("First");
        users.setLastName("Last");
        when(usersRepository.save(any(Users.class))).thenReturn(createMockUsers());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.put("/users/1").accept(MediaType.APPLICATION_JSON)
                .content(request).contentType(MediaType.APPLICATION_JSON);
        MvcResult result = mvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.NO_CONTENT.value(), result.getResponse().getStatus());

        when(usersRepository.findById(anyLong())).thenReturn(Optional.empty());
        requestBuilder = MockMvcRequestBuilders.put("/users/1").accept(MediaType.APPLICATION_JSON)
                .content(request).contentType(MediaType.APPLICATION_JSON);
        result = mvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.NOT_FOUND.value(), result.getResponse().getStatus());
    }

    @Test
    public void testDeleteUser() throws Exception {
        doNothing().when(usersRepository).deleteById(anyLong());

        RequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/users/1").accept(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }

    private Users createMockUsers() {
        Users users = new Users();
        users.setEmployeeId(1l);
        users.setUserId(1l);
        users.setFirstName("TestF");
        users.setLastName("TestL");
        users.setProjectId(1l);
        users.setTaskId(1l);
        return users;
    }
}
