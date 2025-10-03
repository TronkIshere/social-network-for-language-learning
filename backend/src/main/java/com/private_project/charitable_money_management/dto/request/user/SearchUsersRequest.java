package com.private_project.charitable_money_management.dto.request.user;

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

