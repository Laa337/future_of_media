package hu.futureofmedia.mediortest.controller;

import static org.junit.Assert.*;

import java.util.List;

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
import com.fasterxml.jackson.databind.type.CollectionType;

import hu.futureofmedia.mediortest.config.UserDetailsServiceImpl;
import hu.futureofmedia.mediortest.dao.dto.Contact;
import hu.futureofmedia.mediortest.dao.dto.ContactListItem;
import hu.futureofmedia.mediortest.dao.entities.CompanyEntity;
import hu.futureofmedia.mediortest.dao.entities.Status;
import hu.futureofmedia.mediortest.dao.repositories.CompanyRepository;
import hu.futureofmedia.mediortest.services.ContactService;
import hu.futureofmedia.mediortest.services.EmailService;
import hu.futureofmedia.mediortest.services.page.PagedRequest;
import lombok.SneakyThrows;

@RunWith(SpringRunner.class)
@WebMvcTest(value = ContactController.class)
@WithMockUser
public class ContactControllerTest {
    private ObjectMapper mapper;
    private Contact contactDto;
    private PagedRequest simpleRequest;
    private PagedRequest sortedRequest;
    private PagedRequest filteredAndSortedRequest;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ContactService contactService;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @MockBean
    private EmailService emailService;

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

        simpleRequest = PagedRequest.builder()
                .pageNumber(0)
                .build();

        sortedRequest = PagedRequest.builder()
                .pageNumber(0)
                .sortField("email")
                .build();

        filteredAndSortedRequest = PagedRequest.builder()
                .pageNumber(0)
                .sortField("email")
                .filter("f")
                .build();

        Mockito.when(
                companyRepository.findByname("Company #3")).thenReturn(java.util.Optional.of(new CompanyEntity()));

        Mockito.when(
                contactService.create(Mockito.any(Contact.class))).thenReturn(contactDto);

        Mockito.when(
                contactService.findAllActive(simpleRequest)).thenReturn(
                List.of(
                        ContactListItem.builder().fullName("first").build(),
                        ContactListItem.builder().fullName("second").build()
                )
        );

        Mockito.when(
                contactService.findAllActive(sortedRequest)).thenReturn(
                List.of(
                        ContactListItem.builder().fullName("second").build(),
                        ContactListItem.builder().fullName("first").build()
                )
        );

        Mockito.when(
                contactService.findAllActive(filteredAndSortedRequest)).thenReturn(
                List.of(ContactListItem.builder().fullName("first").build())
        );
    }

    @Test
    public void succesWhenAllFieldIsValid() throws Exception {
        String contactJson = mapper.writeValueAsString(contactDto);


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

    @SneakyThrows
    @Test
    public void getListWithSimplePAgedRequest() {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/contact/all-active")
                .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(simpleRequest))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        var contentAsString = response.getContentAsString();

        CollectionType listType = mapper.getTypeFactory()
                .constructCollectionType(List.class, ContactListItem.class);
        List<ContactListItem> dtos = mapper.readValue(contentAsString, listType);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(2, dtos.size());
        assertEquals("first", dtos.get(0).getFullName());
    }

    @SneakyThrows
    @Test
    public void getListWithSortedPagedRequest() {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/contact/all-active")
                .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(sortedRequest))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        var contentAsString = response.getContentAsString();

        CollectionType listType = mapper.getTypeFactory()
                .constructCollectionType(List.class, ContactListItem.class);
        List<ContactListItem> dtos = mapper.readValue(contentAsString, listType);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(2, dtos.size());
        assertEquals("second", dtos.get(0).getFullName());
    }

    @SneakyThrows
    @Test
    public void getListWithFilteredAndSortedPagedRequest() {
        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/contact/all-active")
                .accept(MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(filteredAndSortedRequest))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(requestBuilder).andReturn();
        MockHttpServletResponse response = result.getResponse();
        var contentAsString = response.getContentAsString();

        CollectionType listType = mapper.getTypeFactory()
                .constructCollectionType(List.class, ContactListItem.class);
        List<ContactListItem> dtos = mapper.readValue(contentAsString, listType);

        assertEquals(HttpStatus.OK.value(), response.getStatus());
        assertEquals(1, dtos.size());
        assertEquals("first", dtos.get(0).getFullName());
    }

    public Contact copy( Contact original ) {
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
