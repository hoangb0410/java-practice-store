package com.store.store.modules.reward.dto;

import org.springdoc.core.annotations.ParameterObject;

import com.store.store.common.pagination.PaginationRequest;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ParameterObject
public class GetRewardsRequest extends PaginationRequest {
}
