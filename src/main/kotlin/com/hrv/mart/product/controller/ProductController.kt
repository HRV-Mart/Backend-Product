package com.hrv.mart.product.controller

import com.hrv.mart.product.model.Product
import com.hrv.mart.product.service.ProductService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/product")
class ProductController(
    @Autowired
    private val productService: ProductService
) {
    @PostMapping
    fun createProduct(@RequestBody product: Product) =
        productService.createProduct(product)
    @PutMapping
    fun updateProduct(@RequestBody product: Product, response: ServerHttpResponse) =
        productService.updateProduct(product, response)
    @GetMapping ("/{productId}")
    fun getProductFromId(@PathVariable productId: String, response: ServerHttpResponse) =
        productService.getProductFromId(productId = productId, response = response)
    @DeleteMapping( "/{productId}" )
    fun deleteProductFromId(@PathVariable productId: String, response: ServerHttpResponse) =
        productService.deleteProduct(productId = productId, response = response)
}
