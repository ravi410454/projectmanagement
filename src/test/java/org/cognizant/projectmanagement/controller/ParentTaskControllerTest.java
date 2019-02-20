package org.cognizant.projectmanagement.controller;

import org.cognizant.projectmanagement.repo.ParentTaskRepository;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(ParentTaskController.class)
public class ParentTaskControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ParentTaskRepository parentTaskRepository;
}
