package com.yigit.ecommerce.config;

import com.yigit.ecommerce.model.category.Category;
import com.yigit.ecommerce.model.product.Product;
import com.yigit.ecommerce.model.user.Role;
import com.yigit.ecommerce.model.user.User;
import com.yigit.ecommerce.repository.CartItemRepository;
import com.yigit.ecommerce.repository.CategoryRepository;
import com.yigit.ecommerce.repository.OrderItemRepository;
import com.yigit.ecommerce.repository.ProductRepository;
import com.yigit.ecommerce.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

/**
 * Sample data initializer for development.
 * Only runs when 'dev' profile is active.
 * To activate: add spring.profiles.active=dev to application.yaml
 */
@Configuration
@Profile("dev")
public class DataInitializer {

        @Bean
        CommandLineRunner initSampleData(
                        CategoryRepository categoryRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository,
                        PasswordEncoder passwordEncoder,
                        CartItemRepository cartItemRepository,
                        OrderItemRepository orderItemRepository) {
                return args -> {
                        System.out.println("🚀 DataInitializer running with dev profile...");

                        // Create test users if they don't exist
                        initializeUsers(userRepository, passwordEncoder);

                        // Clear dependent tables first to avoid FK constraint violations
                        cartItemRepository.deleteAll();
                        orderItemRepository.deleteAll();

                        // Clear existing products and categories for fresh seed
                        long existingProducts = productRepository.count();
                        if (existingProducts > 0) {
                                System.out.println("🗑️ Clearing " + existingProducts
                                                + " existing products for re-seed...");
                                productRepository.deleteAll();
                        }
                        long existingCategories = categoryRepository.count();
                        if (existingCategories > 0) {
                                System.out.println("🗑️ Clearing " + existingCategories
                                                + " existing categories for re-seed...");
                                categoryRepository.deleteAll();
                        }

                        System.out.println("📦 Initializing sample products and categories...");

                        // Create Categories if they don't exist, otherwise use existing
                        Category electronics = categoryRepository.save(new Category(null, "Electronics"));
                        Category clothing = categoryRepository.save(new Category(null, "Clothing"));
                        Category home = categoryRepository.save(new Category(null, "Home & Garden"));
                        Category sports = categoryRepository.save(new Category(null, "Sports & Outdoors"));
                        Category books = categoryRepository.save(new Category(null, "Books"));
                        Category beauty = categoryRepository.save(new Category(null, "Beauty & Personal Care"));
                        Category toys = categoryRepository.save(new Category(null, "Toys & Games"));
                        Category food = categoryRepository.save(new Category(null, "Food & Beverages"));

                        // Create Products
                        List<Product> products = List.of(
                                        // Electronics
                                        createProduct("Wireless Bluetooth Headphones",
                                                        "Premium noise-cancelling over-ear headphones with 30-hour battery life",
                                                        new BigDecimal("149.99"), electronics),
                                        createProduct("Smart Watch Pro",
                                                        "Fitness tracker with heart rate monitor, GPS, and 7-day battery",
                                                        new BigDecimal("299.99"), electronics),
                                        createProduct("4K Ultra HD Monitor",
                                                        "27-inch IPS display with HDR support and 144Hz refresh rate",
                                                        new BigDecimal("449.99"), electronics),
                                        createProduct("Portable Power Bank",
                                                        "20000mAh fast charging power bank with USB-C",
                                                        new BigDecimal("59.99"), electronics),
                                        createProduct("Wireless Mechanical Keyboard",
                                                        "RGB backlit mechanical keyboard with hot-swappable switches",
                                                        new BigDecimal("129.99"),
                                                        electronics),

                                        // Clothing
                                        createProduct("Classic Denim Jacket",
                                                        "Vintage-style blue denim jacket with brass buttons",
                                                        new BigDecimal("89.99"), clothing),
                                        createProduct("Cotton Crew Neck T-Shirt",
                                                        "Soft organic cotton t-shirt, available in multiple colors",
                                                        new BigDecimal("24.99"),
                                                        clothing),
                                        createProduct("Running Sneakers",
                                                        "Lightweight breathable running shoes with cushioned sole",
                                                        new BigDecimal("119.99"), clothing),
                                        createProduct("Wool Winter Scarf", "Cozy merino wool scarf, handcrafted design",
                                                        new BigDecimal("45.99"), clothing),
                                        createProduct("Leather Belt", "Genuine leather belt with antique brass buckle",
                                                        new BigDecimal("39.99"), clothing),

                                        // Home & Garden
                                        createProduct("Smart LED Light Bulbs (4-pack)",
                                                        "WiFi-enabled color-changing bulbs compatible with Alexa",
                                                        new BigDecimal("49.99"), home),
                                        createProduct("Indoor Plant Pot Set",
                                                        "Set of 3 ceramic pots with drainage holes",
                                                        new BigDecimal("34.99"), home),
                                        createProduct("Memory Foam Pillow", "Ergonomic cooling pillow for better sleep",
                                                        new BigDecimal("69.99"), home),
                                        createProduct("Stainless Steel Cookware Set",
                                                        "10-piece professional kitchen cookware",
                                                        new BigDecimal("199.99"), home),
                                        createProduct("Robot Vacuum Cleaner",
                                                        "Smart navigation with app control and auto-charging",
                                                        new BigDecimal("349.99"), home),

                                        // Sports
                                        createProduct("Yoga Mat Premium",
                                                        "Extra thick non-slip yoga mat with carrying strap",
                                                        new BigDecimal("39.99"), sports),
                                        createProduct("Adjustable Dumbbells",
                                                        "5-50 lb adjustable weight set for home gym",
                                                        new BigDecimal("299.99"), sports),
                                        createProduct("Hiking Backpack 40L",
                                                        "Waterproof backpack with multiple compartments",
                                                        new BigDecimal("89.99"), sports),
                                        createProduct("Resistance Bands Set",
                                                        "5 different resistance levels with handles",
                                                        new BigDecimal("29.99"), sports),
                                        createProduct("Tennis Racket Pro", "Graphite frame with vibration dampening",
                                                        new BigDecimal("159.99"), sports),

                                        // Books
                                        createProduct("The Art of Programming",
                                                        "Comprehensive guide to software development best practices",
                                                        new BigDecimal("49.99"),
                                                        books),
                                        createProduct("Mindfulness for Beginners",
                                                        "Introduction to meditation and mindful living",
                                                        new BigDecimal("19.99"), books),
                                        createProduct("World History Encyclopedia",
                                                        "Illustrated 1000-page comprehensive history book",
                                                        new BigDecimal("79.99"), books),
                                        createProduct("Cookbook: Global Flavors", "200+ recipes from around the world",
                                                        new BigDecimal("34.99"), books),

                                        // Beauty
                                        createProduct("Vitamin C Serum", "Anti-aging serum with hyaluronic acid",
                                                        new BigDecimal("29.99"),
                                                        beauty),
                                        createProduct("Natural Shampoo Set", "Organic shampoo and conditioner duo",
                                                        new BigDecimal("24.99"),
                                                        beauty),
                                        createProduct("Electric Toothbrush", "Sonic cleaning with 5 brushing modes",
                                                        new BigDecimal("79.99"), beauty),
                                        createProduct("Moisturizing Face Cream", "24-hour hydration with SPF 30",
                                                        new BigDecimal("44.99"),
                                                        beauty),

                                        // Toys
                                        createProduct("Building Blocks Set (500 pcs)",
                                                        "Compatible brick set for creative building",
                                                        new BigDecimal("39.99"), toys),
                                        createProduct("Remote Control Car",
                                                        "High-speed RC car with rechargeable battery",
                                                        new BigDecimal("59.99"), toys),
                                        createProduct("Board Game Collection", "Classic family board games bundle",
                                                        new BigDecimal("49.99"),
                                                        toys),
                                        createProduct("Educational Puzzle Set", "STEM learning puzzles for ages 6+",
                                                        new BigDecimal("29.99"), toys),

                                        // Food
                                        createProduct("Organic Coffee Beans 1kg",
                                                        "Single-origin Arabica beans, medium roast",
                                                        new BigDecimal("24.99"), food),
                                        createProduct("Gourmet Chocolate Box", "Assorted premium chocolates, 24 pieces",
                                                        new BigDecimal("39.99"), food),
                                        createProduct("Herbal Tea Collection", "12 different flavors, 60 tea bags",
                                                        new BigDecimal("19.99"),
                                                        food),
                                        createProduct("Extra Virgin Olive Oil", "Cold-pressed Italian olive oil, 750ml",
                                                        new BigDecimal("29.99"), food));

                        productRepository.saveAll(products);

                        System.out.println(
                                        "✅ Sample data initialized: " + products.size() + " products in 8 categories");
                };
        }

        private void initializeUsers(UserRepository userRepository, PasswordEncoder passwordEncoder) {
                // Create admin user if not exists
                if (userRepository.findByEmail("admin@shophub.com").isEmpty()) {
                        User admin = new User();
                        admin.setName("Admin");
                        admin.setEmail("admin@shophub.com");
                        admin.setPassword(passwordEncoder.encode("admin123"));
                        admin.setRole(Role.ADMIN);
                        userRepository.save(admin);
                        System.out.println("👤 Created admin user: admin@shophub.com / admin123");
                }

                // Create regular user if not exists
                if (userRepository.findByEmail("user@shophub.com").isEmpty()) {
                        User user = new User();
                        user.setName("Test User");
                        user.setEmail("user@shophub.com");
                        user.setPassword(passwordEncoder.encode("user123"));
                        user.setRole(Role.USER);
                        userRepository.save(user);
                        System.out.println("👤 Created test user: user@shophub.com / user123");
                }
        }

        private Product createProduct(String name, String description, BigDecimal price, Category category) {
                Product product = new Product();
                product.setName(name);
                product.setDescription(description);
                product.setPrice(price);
                product.setCategory(category);
                return product;
        }
}
