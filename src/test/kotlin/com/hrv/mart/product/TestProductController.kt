package com.hrv.mart.product

import com.hrv.mart.product.controller.ProductController
import com.hrv.mart.product.model.Product
import com.hrv.mart.product.repository.ProductRepository
import com.hrv.mart.product.service.ProductService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.springframework.http.server.reactive.ServerHttpResponse
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class TestProductController {
    private val productRepository = mock(ProductRepository::class.java)
    private val productService = ProductService(productRepository)
    private val productController = ProductController(productService)
    private val response = mock(ServerHttpResponse::class.java)
    private val product = Product(
        name = "Test Product",
        description = "Created for test",
        images = listOf("https://test.image.com/1")
    )
    @Test
    fun `should create product and return that product`() {
        doReturn(Mono.just(product))
            .`when`(productRepository)
            .insert(product)
        StepVerifier.create(productController.createProduct(product))
            .expectNext(product)
            .verifyComplete()
    }
    @Test
    fun `should update product and return success message if product exist in database`() {
        doReturn(Mono.just(true))
            .`when`(productRepository)
            .existsById(product.id)
        doReturn(Mono.just(product))
            .`when`(productRepository)
            .save(product)
        StepVerifier.create(productController.updateProduct(product, response))
            .expectNext("Product Updated Successfully")
            .verifyComplete()
    }
    @Test
    fun `should not update product and return error message`() {
        doReturn(Mono.just(false))
            .`when`(productRepository)
            .existsById(product.id)
        StepVerifier.create(productController.updateProduct(product, response))
            .expectNext("Product Not Found")
            .verifyComplete()
    }
    @Test
    fun `should return product if product exist`() {
        doReturn(Mono.just(true))
            .`when`(productRepository)
            .existsById(product.id)
        doReturn(Mono.just(product))
            .`when`(productRepository)
            .findById(product.id)
        StepVerifier.create(productController.getProductFromId(productId = product.id, response = response))
            .expectNext(product)
            .verifyComplete()
    }
    @Test
    fun `should return empty mono if product does not exist`() {
        doReturn(Mono.just(false))
            .`when`(productRepository)
            .existsById(product.id)
        StepVerifier.create(productController.getProductFromId(product.id, response))
            .expectComplete()
            .verify()
    }
    @Test
    fun `should delete product and return success message when product exist`() {
        doReturn(Mono.just(true))
            .`when`(productRepository)
            .existsById(product.id)
        doReturn(Mono.empty<Void>())
            .`when`(productRepository)
            .deleteById(product.id)
        StepVerifier.create(productController.deleteProductFromId(product.id, response))
            .expectNext("Product Deleted Successfully")
            .verifyComplete()
    }
    @Test
    fun `should not delete product and return error message when product do not exist`() {
        doReturn(Mono.just(false))
            .`when`(productRepository)
            .existsById(product.id)
        StepVerifier.create(productController.deleteProductFromId(product.id, response))
            .expectNext("Product Not Found")
            .verifyComplete()
    }
}