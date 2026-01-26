package com.yigit.ecommerce.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yigit.ecommerce.dto.request.order.CheckoutRequest;
import com.yigit.ecommerce.dto.request.payment.PaymentRequest;
import com.yigit.ecommerce.model.address.Address;
import com.yigit.ecommerce.model.cart.Cart;
import com.yigit.ecommerce.model.cart.CartItem;
import com.yigit.ecommerce.model.category.Category;
import com.yigit.ecommerce.model.product.Product;
import com.yigit.ecommerce.model.user.User;
import com.yigit.ecommerce.repository.AddressRepository;
import com.yigit.ecommerce.repository.CartRepository;
import com.yigit.ecommerce.repository.CategoryRepository;
import com.yigit.ecommerce.repository.ProductRepository;
import com.yigit.ecommerce.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestDataFactory testDataFactory;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private AddressRepository addressRepository;

    private Address address;

    @BeforeEach
    void setUp() {
        // USER
        User user = new User();
        user.setEmail("user@test.com");
        user.setPassword("password");
        user = userRepository.save(user);

        // CATEGORY + PRODUCT (FACTORY)
        Category category = testDataFactory.createCategory("Test Category");
        category = categoryRepository.save(category);

        Product product = testDataFactory.createDefaultProduct(category);
        product = productRepository.save(product);

        // CART
        Cart cart = new Cart();
        cart.setUser(user);

        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(1);

        cart.setItems(List.of(item));
        cartRepository.save(cart);

        // ADDRESS
        address = new Address();
        address.setUser(user);
        address.setTitle("Home");
        address.setAddressLine("Test Street");
        address.setCity("Istanbul");
        address.setDistrict("Kadikoy");
        address.setZipCode("34000");
        address.setCountry("TR");
        addressRepository.save(address);
    }

    // -------------------------------------------------
    // SECURITY – AUTH REQUIRED
    // -------------------------------------------------

    @Test
    void checkout_shouldReturn403_whenNotAuthenticated() throws Exception {
        CheckoutRequest request = new CheckoutRequest(
                address.getId(),
                validPaymentRequest()
        );

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void getMyOrders_shouldReturn403_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isForbidden());
    }


    // -------------------------------------------------
    // SECURITY – AUTHORIZED BUT NO REAL JWT CONTEXT
    // -------------------------------------------------

    @Test
    @WithMockUser(roles = "USER")
    void checkout_shouldReturn401_evenIfMockUserPresent() throws Exception {
        CheckoutRequest request = new CheckoutRequest(
                address.getId(),
                validPaymentRequest()
        );

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getMyOrders_shouldReturn401_evenIfMockUserPresent() throws Exception {
        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isUnauthorized());
    }

    // -------------------------------------------------
    // HELPER
    // -------------------------------------------------

    private PaymentRequest validPaymentRequest() {
        return new PaymentRequest(
                new BigDecimal("100.00"), // amount
                "4111111111111111",       // cardNumber
                "Yigit Gulbeyaz",         // cardHolder
                "12/30",                  // expireDate
                "123"                     // cvv
        );
    }
}

