package com.tmb.oneapp.productsexpservice.service.productexperience.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tmb.oneapp.productsexpservice.mapper.customer.CustomerInformationMapper;
import com.tmb.oneapp.productsexpservice.model.productexperience.customer.search.response.CustomerSearchResponse;
import com.tmb.oneapp.productsexpservice.model.productexperience.portfolio.response.CustomerInformation;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class CustomerInformationMapperTest {

    @Test
    public void should_return_customer_information_when_call_map_with_customer_search_response() throws IOException {
        CustomerInformationMapper customerInformationMapper = new CustomerInformationMapper();
        ObjectMapper mapper = new ObjectMapper();
        //given
        CustomerSearchResponse response = mapper.readValue(
                Paths.get("src/test/resources/investment/customer/customer_search_response.json").toFile(), CustomerSearchResponse.class);
        CustomerInformation expected = mapper.readValue(
                Paths.get("src/test/resources/investment/customer/customer_information.json").toFile(), CustomerInformation.class);

        //then
        CustomerInformation actual = customerInformationMapper.map(response);
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("lastDateSync")
                .isEqualTo(expected);

    }

}
