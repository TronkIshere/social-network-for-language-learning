package com.private_project.social_network_for_language_learning.dto.request.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchUsersRequest {
    String keyword;
    Integer pageNo;
    Integer pageSize;
}

