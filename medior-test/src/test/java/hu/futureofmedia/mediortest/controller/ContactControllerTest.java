package hu.futureofmedia.mediortest.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import hu.futureofmedia.mediortest.config.UserDetailsServiceImpl;
import hu.futureofmedia.mediortest.dao.dto.Contact;
import hu.futureofmedia.mediortest.dao.entities.CompanyEntity;
import hu.futureofmedia.mediortest.dao.entities.Status;
import hu.futureofmedia.mediortest.dao.repositories.CompanyRepository;
import hu.futureofmedia.mediortest.services.ContactService;

@RunWith(SpringRunner.class)
@WebMvcTest(value = ContactController.class)
@WithMockUser
public class ContactControllerTest {
    ObjectMapper mapper;
    Contact contactDto;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ContactService contactService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private CompanyRepository companyRepository;



    @Before
    public void init() {
        mapper = new ObjectMapper();
        contactDto = Contact.builder()
                .id(1L)
                .firstName("john")
                .lastName("doo")
                .email("example@gmail.com")
                .phoneNumber("+36305309290")
                .company("Company #3")
                .status(Status.ACTIVE)
                .build();

        Mockito.when(
                companyRepository.findByname("Company #3")).thenReturn(java.util.Optional.of(new CompanyEntity()));
    }

    @Test
    public void succesWhenAllFieldIsValid() throws Exception {
        String contactJson = mapper.writeValueAsString(contactDto);
        Mockito.when(
                contactService.create(Mockito.any(Contact.class))).thenReturn(contactDto);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/contact/create")
                .accept(MediaType.APPLICATION_JSON).content(contactJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        var contentAsString = response.getContentAsString();
        Contact contact = mapper.readValue(contentAsString, Contact.class);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(Status.ACTIVE, contact.getStatus());
    }

    @Test
    public void failsWhenCompanyNotFound() throws Exception {
        Contact wrongCompany = copy(contactDto);
        wrongCompany.setCompany("INVALID");
        String contactJson = mapper.writeValueAsString(wrongCompany);
        Mockito.when(
                contactService.create(Mockito.any(Contact.class))).thenReturn(contactDto);

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/contact/create")
                .accept(MediaType.APPLICATION_JSON).content(contactJson)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        Exception resolvedException = result.getResolvedException();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
        assertNotNull(resolvedException);
        assertTrue(resolvedException.getMessage().contains("Couldn't find company name"));

    }


    public Contact copy(Contact original) {
        return Contact.builder()
                .id(original.getId())
                .firstName(original.getFirstName())
                .lastName(original.getLastName())
                .email(original.getEmail())
                .phoneNumber(original.getPhoneNumber())
                .company(original.getCompany())
                .status(original.getStatus())
                .build();
    }
}
