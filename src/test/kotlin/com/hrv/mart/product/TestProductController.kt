package com.hrv.mart.product

import com.hrv.mart.custompageable.model.Pageable
import com.hrv.mart.product.controller.ProductController
import com.hrv.mart.product.model.Product
import com.hrv.mart.product.repository.MongoProductRepository
import com.hrv.mart.product.service.ProductService
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.mock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest
import org.springframework.data.domain.PageRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.testcontainers.containers.MongoDBContainer
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.util.*

@DataMongoTest
class TestProductController (
    @Autowired
    private val mongoProductRepository: MongoProductRepository
){
    private val productService = ProductService(mongoProductRepository)
    private val productController = ProductController(productService)
    private val response = mock(ServerHttpResponse::class.java)
    private val product = Product(
        name = "Test Product",
        description = "Created for test",
        images = listOf("https://test.image.com/1"),
        price = 100L
    )
    @BeforeEach
    fun cleanDataBase() {
        mongoProductRepository
            .deleteAll()
            .subscribe()
    }

    @Test
    fun `should create product and return that product`() {
        StepVerifier.create(productController.createProduct(product))
            .expectNext(product)
            .verifyComplete()
    }
    @Test
    fun `should update product and return success message if product exist in database`() {
        mongoProductRepository
            .insert(product)
            .subscribe()
        StepVerifier.create(productController.updateProduct(product, response))
            .expectNext("Product Updated Successfully")
            .verifyComplete()
    }
    @Test
    fun `should not update product and return error message`() {
        StepVerifier.create(productController.updateProduct(product, response))
            .expectNext("Product Not Found")
            .verifyComplete()
    }
    @Test
    fun `should return product if product exist`() {
        mongoProductRepository
            .insert(product)
            .subscribe()
        StepVerifier.create(productController.getProductFromId(productId = product.id, response = response))
            .expectNext(product)
            .verifyComplete()
    }
    @Test
    fun `should return empty mono if product does not exist`() {
        StepVerifier.create(productController.getProductFromId(product.id, response))
            .expectComplete()
            .verify()
    }
    @Test
    fun `should delete product and return success message when product exist`() {
        mongoProductRepository
            .insert(product)
            .subscribe()
        StepVerifier.create(productController.deleteProductFromId(product.id, response))
            .expectNext("Product Deleted Successfully")
            .verifyComplete()
    }
    @Test
    fun `should not delete product and return error message when product do not exist`() {
        StepVerifier.create(productController.deleteProductFromId(product.id, response))
            .expectNext("Product Not Found")
            .verifyComplete()
    }
    @Test
    fun `should return list of products`() {
        val page = 0
        val size = 1
        mongoProductRepository
            .insert(product)
            .subscribe()
        StepVerifier.create(productController.getAllProducts(
            page = Optional.of(page),
            size = Optional.of(size)
        ))
            .expectNext(Pageable(size.toLong(), null, listOf(product)))
            .verifyComplete()
    }
    @Test
    fun `should return empty list of products`() {
        val page = 1
        val size = 1
        mongoProductRepository
            .insert(product)
            .subscribe()
        StepVerifier.create(productController.getAllProducts(
            page = Optional.of(page),
            size = Optional.of(size)
        ))
            .expectNext(Pageable(size.toLong(), null, listOf()))
            .verifyComplete()
    }
    companion object {
        private lateinit var mongoDBContainer: MongoDBContainer

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            mongoDBContainer = MongoDBContainer("mongo:latest")
                .apply { withExposedPorts(27_017) }
                .apply { start() }
            mongoDBContainer
                .withReuse(true)
                .withAccessToHost(true)
            System.setProperty("spring.data.mongodb.uri", "${mongoDBContainer.connectionString}/test")
            System.setProperty("spring.data.mongodb.auto-index-creation", "true")
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            mongoDBContainer.stop()
        }
    }

}