package com.docencia.tutorial.controllers;

import com.docencia.tutorial.models.Product;
import com.docencia.tutorial.repositories.ProductRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final ProductRepository productRepository;

    public CartController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping
    public String index(HttpSession session, Model model) {
        // Obtener productos desde la base de datos
        List<Product> productList = productRepository.findAll();
        Map<Long, Product> products = new HashMap<>();
        for (Product product : productList) {
            products.put(product.getId(), product);
        }

        // Obtener los productos del carrito desde la sesión
        Map<Long, Long> cartProductData = (Map<Long, Long>) session.getAttribute("cart_product_data");
        Map<Long, Product> cartProducts = new HashMap<>();

        if (cartProductData != null) {
            // Agregar productos del carrito desde la base de datos
            for (Long id : cartProductData.keySet()) {
                Optional<Product> product = productRepository.findById(id);
                product.ifPresent(p -> cartProducts.put(id, p));
            }
        }

        model.addAttribute("title", "Cart - Online Store");
        model.addAttribute("subtitle", "Shopping Cart");
        model.addAttribute("products", products);
        model.addAttribute("cartProducts", cartProducts);
        return "cart/index";
    }

    @GetMapping("/add/{id}")
    public String add(@PathVariable Long id, HttpSession session) {
        // Recuperar el carrito desde la sesión o inicializar uno nuevo
        Map<Long, Long> cartProductData = (Map<Long, Long>) session.getAttribute("cart_product_data");
        if (cartProductData == null) {
            cartProductData = new HashMap<>();
        }

        // Agregar el producto al carrito
        cartProductData.put(id, id);
        session.setAttribute("cart_product_data", cartProductData);
        return "redirect:/cart";
    }

    @GetMapping("/removeAll")
    public String removeAll(HttpSession session) {
        // Eliminar todos los productos del carrito
        session.removeAttribute("cart_product_data");
        return "redirect:/cart";
    }
}
