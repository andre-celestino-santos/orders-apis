package com.andre.orders_apis.controller;

import com.andre.orders_apis.dto.OrderItemRequestDto;
import com.andre.orders_apis.dto.OrderItemResponseDto;
import com.andre.orders_apis.dto.OrderRequestDto;
import com.andre.orders_apis.dto.OrderResponseDto;
import com.andre.orders_apis.entity.Order;
import com.andre.orders_apis.entity.OrderStatus;
import com.andre.orders_apis.enums.OrderApiError;
import com.andre.orders_apis.exception.ResourceNotFoundException;
import com.andre.orders_apis.filter.JwtAuthenticationFilter;
import com.andre.orders_apis.mapper.OrderMapper;
import com.andre.orders_apis.service.OrderService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@AutoConfigureMockMvc(addFilters = false)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private OrderMapper orderMapper;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    public void shouldCreateOrderSuccessfully() throws Exception {
        OrderRequestDto request = new OrderRequestDto();
        request.setCustomerId("abc123");

        List<OrderItemRequestDto> requestItems = new ArrayList<>();
        OrderItemRequestDto itemRequest = new OrderItemRequestDto();
        itemRequest.setQuantity(2);
        itemRequest.setId(3L);
        requestItems.add(itemRequest);

        request.setItems(requestItems);

        Mockito.when(orderMapper.toEntity(Mockito.any())).thenReturn(new Order());

        Order order = new Order();
        order.setId(27L);

        Mockito.when(orderService.create(Mockito.any())).thenReturn(order);

        OrderResponseDto response = new OrderResponseDto();
        response.setId(order.getId());
        response.setCustomerId(request.getCustomerId());
        response.setStatus(OrderStatus.CREATED);
        response.setCreatedAt(LocalDateTime.now());
        response.setUpdatedAt(LocalDateTime.now());

        List<OrderItemResponseDto> responseItems = new ArrayList<>();
        OrderItemResponseDto itemResponse = new OrderItemResponseDto();
        itemResponse.setId(itemRequest.getId());
        itemResponse.setQuantity(itemRequest.getQuantity());
        itemResponse.setCreatedAt(LocalDateTime.now());
        responseItems.add(itemResponse);

        response.setItems(responseItems);

        Mockito.when(orderMapper.toResponse(Mockito.any())).thenReturn(response);

        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content))
                .andExpect(status().isCreated())
                .andExpect(header().string(HttpHeaders.LOCATION, Matchers.endsWith("/v1/orders/%s".formatted(response.getId()))))
                .andExpect(jsonPath("$.id").value(response.getId()))
                .andExpect(jsonPath("$.customerId").value(response.getCustomerId()))
                .andExpect(jsonPath("$.status").value(response.getStatus().name()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.updatedAt").isNotEmpty())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].id").value(itemResponse.getId()))
                .andExpect(jsonPath("$.items[0].quantity").value(itemResponse.getQuantity()))
                .andExpect(jsonPath("$.items[0].createdAt").isNotEmpty());

        Mockito.verify(orderMapper, Mockito.atMostOnce()).toEntity(Mockito.any());

        Mockito.verify(orderService, Mockito.atMostOnce()).create(Mockito.any());

        Mockito.verify(orderMapper, Mockito.atMostOnce()).toEntity(Mockito.any());
    }

    @Test
    public void shouldReturnBadRequestWhenCreateOrderWithCustomerIdNull() throws Exception {
        OrderRequestDto request = new OrderRequestDto();
        request.setCustomerId(null);

        List<OrderItemRequestDto> requestItems = new ArrayList<>();
        OrderItemRequestDto itemRequest = new OrderItemRequestDto();
        itemRequest.setQuantity(2);
        itemRequest.setId(3L);
        requestItems.add(itemRequest);

        request.setItems(requestItems);

        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.customerId").value("must not be blank"));

        Mockito.verify(orderMapper, Mockito.never()).toEntity(Mockito.any());

        Mockito.verify(orderService, Mockito.never()).create(Mockito.any());

        Mockito.verify(orderMapper, Mockito.never()).toEntity(Mockito.any());
    }

    @Test
    public void shouldReturnBadRequestWhenCreateOrderWithCustomerIdEmpty() throws Exception {
        OrderRequestDto request = new OrderRequestDto();
        request.setCustomerId("");

        List<OrderItemRequestDto> requestItems = new ArrayList<>();
        OrderItemRequestDto itemRequest = new OrderItemRequestDto();
        itemRequest.setQuantity(2);
        itemRequest.setId(3L);
        requestItems.add(itemRequest);

        request.setItems(requestItems);

        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.customerId").value("must not be blank"));

        Mockito.verify(orderMapper, Mockito.never()).toEntity(Mockito.any());

        Mockito.verify(orderService, Mockito.never()).create(Mockito.any());

        Mockito.verify(orderMapper, Mockito.never()).toEntity(Mockito.any());
    }

    @Test
    public void shouldReturnBadRequestWhenCreateOrderWithCustomerIdWhiteSpace() throws Exception {
        OrderRequestDto request = new OrderRequestDto();
        request.setCustomerId(" ");

        List<OrderItemRequestDto> requestItems = new ArrayList<>();
        OrderItemRequestDto itemRequest = new OrderItemRequestDto();
        itemRequest.setQuantity(2);
        itemRequest.setId(3L);
        requestItems.add(itemRequest);

        request.setItems(requestItems);

        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.customerId").value("must not be blank"));

        Mockito.verify(orderMapper, Mockito.never()).toEntity(Mockito.any());

        Mockito.verify(orderService, Mockito.never()).create(Mockito.any());

        Mockito.verify(orderMapper, Mockito.never()).toEntity(Mockito.any());
    }

    @Test
    public void shouldReturnBadRequestWhenCreateOrderWithItemsNull() throws Exception {
        OrderRequestDto request = new OrderRequestDto();
        request.setCustomerId("abc123");

        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.items").value("must not be empty"));

        Mockito.verify(orderMapper, Mockito.never()).toEntity(Mockito.any());

        Mockito.verify(orderService, Mockito.never()).create(Mockito.any());

        Mockito.verify(orderMapper, Mockito.never()).toEntity(Mockito.any());
    }

    @Test
    public void shouldReturnBadRequestWhenCreateOrderWithItemsEmpty() throws Exception {
        OrderRequestDto request = new OrderRequestDto();
        request.setCustomerId("abc123");

        List<OrderItemRequestDto> requestItems = new ArrayList<>();
        request.setItems(requestItems);

        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.items").value("must not be empty"));

        Mockito.verify(orderMapper, Mockito.never()).toEntity(Mockito.any());

        Mockito.verify(orderService, Mockito.never()).create(Mockito.any());

        Mockito.verify(orderMapper, Mockito.never()).toEntity(Mockito.any());
    }

    @Test
    public void shouldReturnBadRequestWhenCreateOrderWithItemIdNull() throws Exception {
        OrderRequestDto request = new OrderRequestDto();
        request.setCustomerId("abc123");

        List<OrderItemRequestDto> requestItems = new ArrayList<>();
        OrderItemRequestDto itemRequest = new OrderItemRequestDto();
        itemRequest.setQuantity(2);
        itemRequest.setId(null);
        requestItems.add(itemRequest);

        request.setItems(requestItems);

        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['items[0].id']").value("must not be null"));

        Mockito.verify(orderMapper, Mockito.never()).toEntity(Mockito.any());

        Mockito.verify(orderService, Mockito.never()).create(Mockito.any());

        Mockito.verify(orderMapper, Mockito.never()).toEntity(Mockito.any());
    }

    @Test
    public void shouldReturnBadRequestWhenCreateOrderWithItemIdZero() throws Exception {
        OrderRequestDto request = new OrderRequestDto();
        request.setCustomerId("abc123");

        List<OrderItemRequestDto> requestItems = new ArrayList<>();
        OrderItemRequestDto itemRequest = new OrderItemRequestDto();
        itemRequest.setQuantity(2);
        itemRequest.setId(0L);
        requestItems.add(itemRequest);

        request.setItems(requestItems);

        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['items[0].id']").value("must be greater than or equal to 1"));

        Mockito.verify(orderMapper, Mockito.never()).toEntity(Mockito.any());

        Mockito.verify(orderService, Mockito.never()).create(Mockito.any());

        Mockito.verify(orderMapper, Mockito.never()).toEntity(Mockito.any());
    }

    @Test
    public void shouldReturnBadRequestWhenCreateOrderWithItemIdNegative() throws Exception {
        OrderRequestDto request = new OrderRequestDto();
        request.setCustomerId("abc123");

        List<OrderItemRequestDto> requestItems = new ArrayList<>();
        OrderItemRequestDto itemRequest = new OrderItemRequestDto();
        itemRequest.setQuantity(2);
        itemRequest.setId(-1L);
        requestItems.add(itemRequest);

        request.setItems(requestItems);

        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['items[0].id']").value("must be greater than or equal to 1"));

        Mockito.verify(orderMapper, Mockito.never()).toEntity(Mockito.any());

        Mockito.verify(orderService, Mockito.never()).create(Mockito.any());

        Mockito.verify(orderMapper, Mockito.never()).toEntity(Mockito.any());
    }

    @Test
    public void shouldReturnBadRequestWhenCreateOrderWithItemQuantityNull() throws Exception {
        OrderRequestDto request = new OrderRequestDto();
        request.setCustomerId("abc123");

        List<OrderItemRequestDto> requestItems = new ArrayList<>();
        OrderItemRequestDto itemRequest = new OrderItemRequestDto();
        itemRequest.setQuantity(null);
        itemRequest.setId(32L);
        requestItems.add(itemRequest);

        request.setItems(requestItems);

        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['items[0].quantity']").value("must not be null"));

        Mockito.verify(orderMapper, Mockito.never()).toEntity(Mockito.any());

        Mockito.verify(orderService, Mockito.never()).create(Mockito.any());

        Mockito.verify(orderMapper, Mockito.never()).toEntity(Mockito.any());
    }

    @Test
    public void shouldReturnBadRequestWhenCreateOrderWithItemQuantityZero() throws Exception {
        OrderRequestDto request = new OrderRequestDto();
        request.setCustomerId("abc123");

        List<OrderItemRequestDto> requestItems = new ArrayList<>();
        OrderItemRequestDto itemRequest = new OrderItemRequestDto();
        itemRequest.setQuantity(0);
        itemRequest.setId(32L);
        requestItems.add(itemRequest);

        request.setItems(requestItems);

        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['items[0].quantity']").value("must be greater than or equal to 1"));

        Mockito.verify(orderMapper, Mockito.never()).toEntity(Mockito.any());

        Mockito.verify(orderService, Mockito.never()).create(Mockito.any());

        Mockito.verify(orderMapper, Mockito.never()).toEntity(Mockito.any());
    }

    @Test
    public void shouldReturnBadRequestWhenCreateOrderWithItemQuantityNegative() throws Exception {
        OrderRequestDto request = new OrderRequestDto();
        request.setCustomerId("abc123");

        List<OrderItemRequestDto> requestItems = new ArrayList<>();
        OrderItemRequestDto itemRequest = new OrderItemRequestDto();
        itemRequest.setQuantity(-2);
        itemRequest.setId(32L);
        requestItems.add(itemRequest);

        request.setItems(requestItems);

        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.['items[0].quantity']").value("must be greater than or equal to 1"));

        Mockito.verify(orderMapper, Mockito.never()).toEntity(Mockito.any());

        Mockito.verify(orderService, Mockito.never()).create(Mockito.any());

        Mockito.verify(orderMapper, Mockito.never()).toEntity(Mockito.any());
    }

    @Test
    public void shouldCancelOrderSuccessfully() throws Exception {
        Mockito.doNothing().when(orderService).cancel(Mockito.any());

        mockMvc.perform(post("/v1/orders/76/cancel"))
                        .andExpect(status().isNoContent());

        Mockito.verify(orderService, Mockito.atMostOnce()).cancel(Mockito.any());
    }

    @Test
    public void shouldReturnNotFoundWhenCancelOrderNotFound() throws Exception {
        Long orderId = 999L;

        ResourceNotFoundException resourceNotFoundException = new ResourceNotFoundException(OrderApiError.ORDER_NOT_FOUND, orderId);

        Mockito.doThrow(resourceNotFoundException).when(orderService).cancel(Mockito.any());

        mockMvc.perform(post("/v1/orders/76/cancel"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(OrderApiError.ORDER_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(OrderApiError.ORDER_NOT_FOUND.getMessage().formatted(orderId)));

        Mockito.verify(orderService, Mockito.atMostOnce()).cancel(Mockito.any());
    }

}