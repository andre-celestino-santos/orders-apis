package com.andre.orders_apis.integration;

import com.andre.orders_apis.dto.OrderItemRequestDto;
import com.andre.orders_apis.dto.OrderItemResponseDto;
import com.andre.orders_apis.dto.OrderRequestDto;
import com.andre.orders_apis.dto.OrderResponseDto;
import com.andre.orders_apis.dto.ProductRequestDto;
import com.andre.orders_apis.dto.ProductResponseDto;
import com.andre.orders_apis.entity.Category;
import com.andre.orders_apis.entity.Order;
import com.andre.orders_apis.entity.OrderItem;
import com.andre.orders_apis.entity.OrderStatus;
import com.andre.orders_apis.entity.Product;
import com.andre.orders_apis.enums.OrderApiError;
import com.andre.orders_apis.repository.OrderItemRepository;
import com.andre.orders_apis.repository.OrderRepository;
import com.andre.orders_apis.repository.ProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class OrderIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    public void beforeEach() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    public void shouldCreateOrderSuccessfully() throws Exception {
        ProductResponseDto createdProduct = createProduct();

        OrderRequestDto request = new OrderRequestDto();
        request.setCustomerId("abc123");

        List<OrderItemRequestDto> requestItems = new ArrayList<>();
        OrderItemRequestDto itemRequest = new OrderItemRequestDto();
        itemRequest.setQuantity(2);
        itemRequest.setId(createdProduct.getId());
        requestItems.add(itemRequest);

        request.setItems(requestItems);

        String content = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();
        OrderResponseDto response = objectMapper.readValue(responseAsString, OrderResponseDto.class);

        String headerLocationExpected = "/v1/orders/%s".formatted(response.getId());
        String headerLocationResponse = result.getResponse().getHeader(HttpHeaders.LOCATION);

        Assertions.assertThat(headerLocationResponse).endsWith(headerLocationExpected);

        Assertions.assertThat(response.getCustomerId()).isEqualTo(request.getCustomerId());
        Assertions.assertThat(response.getStatus()).isEqualTo(OrderStatus.CREATED);
        Assertions.assertThat(response.getCreatedAt()).isNotNull();
        Assertions.assertThat(response.getUpdatedAt()).isNotNull();

        List<OrderItemResponseDto> responseItems = response.getItems();
        Assertions.assertThat(responseItems).hasSize(1);

        OrderItemResponseDto responseItem = responseItems.get(0);
        Assertions.assertThat(responseItem.getId()).isEqualTo(createdProduct.getId());
        Assertions.assertThat(responseItem.getQuantity()).isEqualTo(itemRequest.getQuantity());
        Assertions.assertThat(responseItem.getCreatedAt()).isNotNull();

        Optional<Product> optProduct = productRepository.findById(createdProduct.getId());
        Assertions.assertThat(optProduct).isPresent();
        Product product = optProduct.get();
        Assertions.assertThat(product.getStockQuantity()).isEqualTo(3);
    }

    @Test
    public void shouldCreateOrderInParallelSuccessfully() throws Exception {
        ProductResponseDto createdProduct = createProduct(100);

        OrderRequestDto request = new OrderRequestDto();
        request.setCustomerId("abc123");

        List<OrderItemRequestDto> requestItems = new ArrayList<>();
        OrderItemRequestDto itemRequest = new OrderItemRequestDto();
        itemRequest.setQuantity(1);
        itemRequest.setId(createdProduct.getId());
        requestItems.add(itemRequest);

        request.setItems(requestItems);

        String content = objectMapper.writeValueAsString(request);

        AtomicInteger createdOrderCount = new AtomicInteger();

        Runnable task = () -> {
            try {
                MvcResult result = mockMvc.perform(post("/v1/orders")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(content))
                        .andReturn();

                if (HttpStatus.CREATED.value() == result.getResponse().getStatus()) {
                    createdOrderCount.incrementAndGet();
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        ExecutorService threadPool = Executors.newFixedThreadPool(10);

        for (int i = 0; i < createdProduct.getStockQuantity(); i++) {
            threadPool.submit(task);
        }

        threadPool.shutdown();

        boolean isTerminated = threadPool.awaitTermination(120, TimeUnit.SECONDS);
        Assertions.assertThat(isTerminated).isTrue();

        Assertions.assertThat(createdOrderCount.get()).isEqualTo(createdProduct.getStockQuantity());

        Optional<Product> optProduct = productRepository.findById(createdProduct.getId());
        Assertions.assertThat(optProduct).isPresent();
        Product product = optProduct.get();
        Assertions.assertThat(product.getStockQuantity()).isEqualTo(0);
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
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.items").value("must not be empty"));
    }

    @Test
    public void shouldReturnNotFoundWhenCreateOrderAndProductNotFound() throws Exception {
        OrderRequestDto request = new OrderRequestDto();
        request.setCustomerId("abc123");

        List<OrderItemRequestDto> requestItems = new ArrayList<>();
        OrderItemRequestDto itemRequest = new OrderItemRequestDto();
        itemRequest.setQuantity(2);
        itemRequest.setId(999L);
        requestItems.add(itemRequest);

        request.setItems(requestItems);

        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(OrderApiError.PRODUCT_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(OrderApiError.PRODUCT_NOT_FOUND.getMessage().formatted(itemRequest.getId())));
    }

    @Test
    public void shouldReturnBadRequestWhenCreateOrderWithProductInsufficientStockQuantity() throws Exception {
        ProductResponseDto createdProduct = createProduct();

        OrderRequestDto request = new OrderRequestDto();
        request.setCustomerId("abc123");

        List<OrderItemRequestDto> requestItems = new ArrayList<>();
        OrderItemRequestDto itemRequest = new OrderItemRequestDto();
        itemRequest.setQuantity(6);
        itemRequest.setId(createdProduct.getId());
        requestItems.add(itemRequest);

        request.setItems(requestItems);

        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(OrderApiError.PRODUCT_INSUFFICIENT_STOCK_QUANTITY.getCode()))
                .andExpect(jsonPath("$.message").value(OrderApiError.PRODUCT_INSUFFICIENT_STOCK_QUANTITY.getMessage()
                        .formatted(itemRequest.getQuantity(), itemRequest.getId(), createdProduct.getStockQuantity())));
    }

    @Test
    public void shouldCancelOrderSuccessfully() throws Exception {
        ProductResponseDto createdProduct = createProduct();

        OrderResponseDto createdOrder = createOrder(createdProduct);

        cancelOrder(createdOrder.getId());

        Optional<Order> optOrder = orderRepository.findById(createdOrder.getId());
        Assertions.assertThat(optOrder).isPresent();

        Order order = optOrder.get();
        Assertions.assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);

        List<OrderItem> items = orderItemRepository.findAllByOrder(order);
        Assertions.assertThat(items).hasSize(1);

        Product product = items.get(0).getProduct();
        Assertions.assertThat(product.getId()).isEqualTo(createdProduct.getId());
        Assertions.assertThat(product.getStockQuantity()).isEqualTo(createdProduct.getStockQuantity());
    }

    @Test
    public void shouldCancelOrderAlreadyCancelledSuccessfully() throws Exception {
        ProductResponseDto createdProduct = createProduct();

        OrderResponseDto createdOrder = createOrder(createdProduct);

        cancelOrder(createdOrder.getId());

        cancelOrder(createdOrder.getId());

        Optional<Order> optOrder = orderRepository.findById(createdOrder.getId());
        Assertions.assertThat(optOrder).isPresent();

        Order order = optOrder.get();
        Assertions.assertThat(order.getStatus()).isEqualTo(OrderStatus.CANCELLED);

        List<OrderItem> items = orderItemRepository.findAllByOrder(order);
        Assertions.assertThat(items).hasSize(1);

        Product product = items.get(0).getProduct();
        Assertions.assertThat(product.getId()).isEqualTo(createdProduct.getId());
        Assertions.assertThat(product.getStockQuantity()).isEqualTo(createdProduct.getStockQuantity());
    }

    @Test
    public void shouldReturnNotFoundWhenCancelOrderNotFound() throws Exception {
        Long orderId = 999L;

        mockMvc.perform(post("/v1/orders/%s/cancel".formatted(orderId)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(OrderApiError.ORDER_NOT_FOUND.getCode()))
                .andExpect(jsonPath("$.message").value(OrderApiError.ORDER_NOT_FOUND.getMessage().formatted(orderId)));
    }

    private ProductResponseDto createProduct(Integer stockQuantity) throws Exception {
        ProductRequestDto request = new ProductRequestDto();
        request.setBrand("Samsung");
        request.setModel("A07");
        request.setPrice(new BigDecimal("594.00"));
        request.setCategory(Category.SMARTPHONE);
        request.setStockQuantity(stockQuantity);
        request.setDescription("Samsung Galaxy A07 128gb, 4gb");

        String content = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readValue(result.getResponse().getContentAsString(), ProductResponseDto.class);
    }

    private ProductResponseDto createProduct() throws Exception {
        return createProduct(5);
    }

    private OrderResponseDto createOrder(ProductResponseDto createdProduct) throws Exception {
        OrderRequestDto request = new OrderRequestDto();
        request.setCustomerId("abc123");

        List<OrderItemRequestDto> requestItems = new ArrayList<>();
        OrderItemRequestDto itemRequest = new OrderItemRequestDto();
        itemRequest.setQuantity(2);
        itemRequest.setId(createdProduct.getId());
        requestItems.add(itemRequest);

        request.setItems(requestItems);

        String content = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        String responseAsString = result.getResponse().getContentAsString();

        return objectMapper.readValue(responseAsString, OrderResponseDto.class);
    }

    private void cancelOrder(Long id) throws Exception {
        mockMvc.perform(post("/v1/orders/%s/cancel".formatted(id)))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

}