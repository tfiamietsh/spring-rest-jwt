package core.controller;

import core.dto.user.UserInfoDto;
import core.enums.Role;
import core.service.UserService;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoInteractions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AdminControllerTest {
    private final static Long TEST_ID = 0L;
    private final static String TEST_USERNAME = "test_username";
    private final static Role TEST_ROLE = Role.STAFF;
    private final static OffsetDateTime TEST_REGISTERED_AT = OffsetDateTime.now();
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser(roles = "STAFF")
    void shouldReturnForbiddenWhenGetUsersForStaffRole() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isForbidden());

        verifyNoInteractions(userService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNoContentIfUsersIsEmptyWhenGetUsersForAdminRole() throws Exception {
        when(userService.getAllUsers()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnOkWithUsersIfUsersIsNonemptyWhenGetUsersForAdminRole() throws Exception {
        UserInfoDto userInfoDto = new UserInfoDto(TEST_ID, TEST_USERNAME, TEST_ROLE.getDisplayName(),
                TEST_REGISTERED_AT);

        when(userService.getAllUsers()).thenReturn(List.of(userInfoDto));

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void shouldReturnForbiddenWhenPatchPromoteForStaffRole() throws Exception {
        mockMvc.perform(patch("/admin/promote/{id}", TEST_ID))
                .andExpect(status().isForbidden());

        verifyNoInteractions(userService);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnOkWhenPatchPublishForAdminRole() throws Exception {
        mockMvc.perform(patch("/admin/promote/{id}", TEST_ID))
                .andExpect(status().isOk());

        verify(userService, times(1)).promote(TEST_ID, TEST_ROLE);
    }
}
