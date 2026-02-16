package com.andre.orders_apis.controller;

import com.andre.orders_apis.dto.ProductRequestDto;
import com.andre.orders_apis.dto.ProductResponseDto;
import com.andre.orders_apis.entity.Category;
import com.andre.orders_apis.entity.Product;
import com.andre.orders_apis.mapper.ProductMapper;
import com.andre.orders_apis.service.ProductService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private ProductMapper productMapper;

    @Test
    public void shouldCreateProductSuccessfully() throws Exception {
        ProductRequestDto request = new ProductRequestDto();
        request.setBrand("Samsung");
        request.setModel("A07");
        request.setPrice(new BigDecimal("594.00"));
        request.setCategory(Category.SMARTPHONE);
        request.setStockQuantity(5);
        request.setDescription("Samsung Galaxy A07 128gb, 4gb");

        Mockito.when(productMapper.toEntity(Mockito.any())).thenReturn(new Product());

        Product product = new Product();
        product.setId(1);

        Mockito.when(productService.create(Mockito.any())).thenReturn(product);

        ProductResponseDto response = new ProductResponseDto();
        response.setId(1);

        Mockito.when(productMapper.toResponse(Mockito.isA(Product.class))).thenReturn(response);

        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(header().string(HttpHeaders.LOCATION, Matchers.endsWith("/v1/products/%s".formatted(response.getId()))));
    }

    @Test
    public void shouldReturnBadRequestWithNullFields() throws Exception {
        ProductRequestDto request = new ProductRequestDto();
        request.setBrand(null);
        request.setModel(null);
        request.setPrice(null);
        request.setCategory(null);
        request.setStockQuantity(null);
        request.setDescription(null);

        Mockito.when(productMapper.toEntity(Mockito.any())).thenReturn(new Product());

        Product product = new Product();
        product.setId(1);

        Mockito.when(productService.create(Mockito.any())).thenReturn(product);

        ProductResponseDto response = new ProductResponseDto();
        response.setId(1);

        Mockito.when(productMapper.toResponse(Mockito.isA(Product.class))).thenReturn(response);

        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.brand").value("must not be blank"))
                .andExpect(jsonPath("$.model").value("must not be blank"))
                .andExpect(jsonPath("$.description").value("must not be blank"))
                .andExpect(jsonPath("$.price").value("must not be null"))
                .andExpect(jsonPath("$.category").value("must not be null"))
                .andExpect(jsonPath("$.stockQuantity").value("must not be null"));
    }

    @Test
    public void shouldReturnBadRequestWithSizeGreaterThanRequired() throws Exception {
        ProductRequestDto request = new ProductRequestDto();
        request.setBrand("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901");
        request.setModel("12345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901");
        request.setPrice(new BigDecimal("594.00"));
        request.setCategory(Category.SMARTPHONE);
        request.setStockQuantity(5);
        request.setDescription("1234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234" +
                "56789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901");

        Mockito.when(productMapper.toEntity(Mockito.any())).thenReturn(new Product());

        Product product = new Product();
        product.setId(1);

        Mockito.when(productService.create(Mockito.any())).thenReturn(product);

        ProductResponseDto response = new ProductResponseDto();
        response.setId(1);

        Mockito.when(productMapper.toResponse(Mockito.isA(Product.class))).thenReturn(response);

        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.brand").value("size must be between 1 and 100"))
                .andExpect(jsonPath("$.model").value("size must be between 1 and 100"))
                .andExpect(jsonPath("$.description").value("size must be between 1 and 200"));
    }

    @Test
    public void shouldUpdateDescriptionSuccessfully() throws Exception {
        ProductRequestDto request = new ProductRequestDto();
        request.setDescription("new description");

        Mockito.when(productMapper.toEntity(Mockito.any(), Mockito.any())).thenReturn(new Product());

        Product product = new Product();
        product.setId(1);
        product.setDescription("new description");

        Mockito.when(productService.update(Mockito.any())).thenReturn(product);

        ProductResponseDto response = new ProductResponseDto();
        response.setId(1);
        response.setDescription("new description");

        Mockito.when(productMapper.toResponse(Mockito.isA(Product.class))).thenReturn(response);

        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(patch("/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.description").value(response.getDescription()));
    }

    @Test
    public void shouldDeleteProductSuccessfully() throws Exception {
        Mockito.doNothing().when(productService).delete(Mockito.any());

        mockMvc.perform(delete("/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    public void shouldReturnAllActiveProductByCategoryWithPagination() throws Exception {
        Product product = new Product();
        product.setId(1);
        product.setBrand("Apple");
        product.setModel("Iphone 16");
        product.setCategory(Category.SMARTPHONE);

        Page<Product> pageSmartphoneProductMock = new PageImpl<>(List.of(product));

        Mockito.when(productService.getAllByCategory(Mockito.any(), Mockito.any())).thenReturn(pageSmartphoneProductMock);

        ProductResponseDto response = new ProductResponseDto();
        response.setId(1);
        response.setBrand("Apple");
        response.setModel("Iphone 16");
        response.setCategory(Category.SMARTPHONE);

        Page<ProductResponseDto> pageResponseMock = new PageImpl<>(List.of(response));

        Mockito.when(productMapper.toResponse(Mockito.isA(Page.class))).thenReturn(pageResponseMock);

        mockMvc.perform(get("/v1/products?category=SMARTPHONE")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value(0))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].brand").value("Apple"))
                .andExpect(jsonPath("$.content[0].model").value("Iphone 16"))
                .andExpect(jsonPath("$.content[0].category").value("SMARTPHONE"));
    }

}