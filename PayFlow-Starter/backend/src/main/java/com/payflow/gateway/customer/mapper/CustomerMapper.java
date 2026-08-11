package com.payflow.gateway.customer.mapper;

import com.payflow.gateway.auth.entity.User;
import com.payflow.gateway.customer.dto.response.CustomerResponse;
import com.payflow.gateway.customer.dto.response.CustomerSummaryResponse;
import com.payflow.gateway.customer.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    /*
     * Convert Customer entity → CustomerResponse.
     *
     * Some fields come from the associated User entity.
     */
    @Mapping(source = "id", target = "id")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.firstName", target = "firstName")
    @Mapping(source = "user.lastName", target = "lastName")
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.phoneNumber", target = "phoneNumber")
    @Mapping(source = "user.role", target = "role")
    CustomerResponse toResponse(Customer customer);

    /*
     * Convert Customer entity → CustomerSummaryResponse.
     */
    @Mapping(
            target = "fullName",
            expression = "java(customer.getUser().getFirstName() + \" \" + customer.getUser().getLastName())"
    )
    @Mapping(source = "user.email", target = "email")
    @Mapping(source = "user.role", target = "role")
    CustomerSummaryResponse toSummaryResponse(Customer customer);
}