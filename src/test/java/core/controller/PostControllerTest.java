package core.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import core.dto.post.PostPendingDto;
import core.dto.post.PostPublishedDto;
import core.dto.post.PostSuggestionDto;
import core.service.PostService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.times;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {
    private static final Long TEST_ID = 0L;
    private static final String TEST_TITLE = "test_title", TEST_TEXT = "test_text";
    private static final String TEST_AUTHOR_USERNAME = "test_author_username";
    private static final String TEST_REVIEWER_USERNAME = "test_reviewer_username";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PostService postService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldReturnNoContentIfPublishedPostsIsEmptyWhenGetPosts() throws Exception {
        when(postService.getAllPublished()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/posts"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnOkWithPublishedPostsIfPublishedPostsIsNonemptyWhenGetPosts() throws Exception {
        PostPublishedDto publishedDto = new PostPublishedDto(TEST_TITLE, TEST_TEXT, TEST_AUTHOR_USERNAME,
                TEST_REVIEWER_USERNAME);

        when(postService.getAllPublished()).thenReturn(List.of(publishedDto));

        mockMvc.perform(get("/posts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void shouldReturnForbiddenWhenPostSuggestWithoutAuth() throws Exception {
        PostSuggestionDto suggestionDto = new PostSuggestionDto(TEST_TITLE, TEST_TEXT);

        mockMvc.perform(post("/posts/suggest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(suggestionDto)))
                .andExpect(status().isForbidden());

        verifyNoInteractions(postService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturnCreatedWhenPostSuggestForUserRole() throws Exception {
        PostSuggestionDto suggestionDto = new PostSuggestionDto(TEST_TITLE, TEST_TEXT);

        mockMvc.perform(post("/posts/suggest")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(suggestionDto)))
                .andExpect(status().isCreated());

        verify(postService, times(1)).suggest(suggestionDto);
    }

    @Test
    void shouldReturnForbiddenWhenGetPendingWithoutAuth() throws Exception {
        mockMvc.perform(get("/posts/pending"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(postService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturnForbiddenWhenGetPendingForUserRole() throws Exception {
        mockMvc.perform(get("/posts/pending"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(postService);
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void shouldReturnNoContentIfPendingPostsIsEmptyWhenGetPendingForStaffRole() throws Exception {
        when(postService.getAllPending()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/posts/pending"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void shouldReturnOkWithPendingPostsIfPendingPostsIsNonemptyWhenGetPendingForStaffRole() throws Exception {
        PostPendingDto pendingDto = new PostPendingDto(TEST_ID, TEST_TITLE, TEST_TEXT, TEST_AUTHOR_USERNAME);

        when(postService.getAllPending()).thenReturn(List.of(pendingDto));

        mockMvc.perform(get("/posts/pending"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void shouldReturnForbiddenWhenPatchPublishWithoutAuth() throws Exception {
        mockMvc.perform(patch("/posts/publish/{id}", TEST_ID))
                .andExpect(status().isForbidden());

        verifyNoInteractions(postService);
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturnForbiddenWhenPatchPublishForUserRole() throws Exception {
        mockMvc.perform(patch("/posts/publish/{id}", TEST_ID))
                .andExpect(status().isForbidden());

        verifyNoInteractions(postService);
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void shouldReturnOkWhenPatchPublishForStaffRole() throws Exception {
        mockMvc.perform(patch("/posts/publish/{id}", TEST_ID))
                .andExpect(status().isOk());

        verify(postService, times(1)).publish(TEST_ID);
    }
}
