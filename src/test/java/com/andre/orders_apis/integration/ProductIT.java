package com.andre.orders_apis.integration;

import com.andre.orders_apis.dto.ProductRequestDto;
import com.andre.orders_apis.dto.ProductResponseDto;
import com.andre.orders_apis.entity.Category;
import com.andre.orders_apis.entity.Product;
import com.andre.orders_apis.enums.OrderApiError;
import com.andre.orders_apis.repository.ProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class ProductIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Test
    public void shouldCreateProductSuccessfully() throws Exception {
        ProductRequestDto request = new ProductRequestDto();
        request.setBrand("Samsung");
        request.setModel("A07");
        request.setPrice(new BigDecimal("594.00"));
        request.setCategory(Category.SMARTPHONE);
        request.setStockQuantity(5);
        request.setDescription("Samsung Galaxy A07 128gb, 4gb");

        String content = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isCreated())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        ProductResponseDto response = objectMapper.readValue(responseAsString, ProductResponseDto.class);

        Assertions.assertThat(response.getId()).isGreaterThan(0);
        Assertions.assertThat(response.getBrand()).isEqualTo(request.getBrand());
        Assertions.assertThat(response.getModel()).isEqualTo(request.getModel());
        Assertions.assertThat(response.getPrice()).isEqualByComparingTo(request.getPrice());
        Assertions.assertThat(response.getCategory()).isEqualTo(request.getCategory());
        Assertions.assertThat(response.getStockQuantity()).isEqualTo(request.getStockQuantity());
        Assertions.assertThat(response.getDescription()).isEqualTo(request.getDescription());
        Assertions.assertThat(response.getCreatedAt()).isNotNull();
        Assertions.assertThat(response.getUpdatedAt()).isNotNull();

        String headerLocationExpected = "/v1/products/%s".formatted(response.getId());
        String headerLocationResponse = result.getResponse().getHeader(HttpHeaders.LOCATION);

        Assertions.assertThat(headerLocationResponse).endsWith(headerLocationExpected);

        Optional<Product> optProduct = productRepository.findById(response.getId());
        Assertions.assertThat(optProduct).isPresent();
        Product product = optProduct.get();
        Assertions.assertThat(product.getActive()).isTrue();
    }

    @Test
    public void shouldUpdateDescriptionSuccessfully() throws Exception {
        ProductRequestDto createRequest = new ProductRequestDto();
        createRequest.setBrand("Samsung");
        createRequest.setModel("A07");
        createRequest.setPrice(new BigDecimal("594.00"));
        createRequest.setCategory(Category.SMARTPHONE);
        createRequest.setStockQuantity(5);
        createRequest.setDescription("Samsung Galaxy A07 128gb, 4gb");

        String createContent = objectMapper.writeValueAsString(createRequest);

        MvcResult result = mockMvc.perform(post("/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createContent))
                .andExpect(status().isCreated())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        ProductResponseDto createResponse = objectMapper.readValue(responseAsString, ProductResponseDto.class);
        Assertions.assertThat(createResponse.getDescription()).isEqualTo(createRequest.getDescription());

        ProductRequestDto updateRequest = new ProductRequestDto();
        updateRequest.setDescription("new description");

        String updateContent = objectMapper.writeValueAsString(updateRequest);

        mockMvc.perform(patch("/v1/products/%s".formatted(createResponse.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateContent))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createResponse.getId()))
                .andExpect(jsonPath("$.description").value(updateRequest.getDescription()));
    }

    @Test
    public void shouldReturnNotFoundWhenProductIsNotFound() throws Exception {
        Integer id = 999;
        mockMvc.perform(patch("/v1/products/%s".formatted(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"description": "new description"}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(OrderApiError.PRODUCT_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(OrderApiError.PRODUCT_NOT_FOUND.getMessage().formatted(id)));
    }

}