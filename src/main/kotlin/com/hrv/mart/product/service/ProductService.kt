package com.hrv.mart.product.service

import com.hrv.mart.product.model.Product
import com.hrv.mart.product.repository.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class ProductService (
    @Autowired
    private val productRepository: ProductRepository
) {
    fun createProduct(product: Product) =
        productRepository.insert(product.setIdToDefault())
    fun updateProduct(product: Product, response: ServerHttpResponse) =
        isProductExist(productId = product.id)
            .flatMap {isExist ->
                if (isExist)
                    productRepository.save(product)
                        .map {
                            setHTTPOkCode(response)
                        }
                        .then(Mono.just("Product Updated Successfully"))
                else
                    setHTTPNotfoundCode(response)
                        .then(Mono.just("Product Not Found"))

            }
    fun getProductFromId(productId: String, response: ServerHttpResponse) =
        isProductExist(productId)
            .flatMap {isExist ->
                if (isExist)
                    setHTTPOkCode(response)
                        .then(
                            productRepository
                                .findById(productId)
                        )
                else
                    setHTTPNotfoundCode(response)
                        .then(Mono.empty())
            }
    fun deleteProduct(productId: String, response: ServerHttpResponse) =
        isProductExist(productId)
            .flatMap { isExist ->
                if (isExist) {
                    productRepository.deleteById(productId)
                        .then(setHTTPOkCode(response))
                        .then(Mono.just("Product Deleted Successfully"))
                }
                else {
                    setHTTPNotfoundCode(response)
                        .then(Mono.just("Product Not Found"))
                }
            }
    private fun isProductExist(productId: String) =
        productRepository.existsById(productId)
    private fun setHTTPNotfoundCode(response: ServerHttpResponse) =
        generateDummyMono()
            .map {
                response.statusCode = HttpStatus.NOT_FOUND
            }

    private fun setHTTPOkCode(response: ServerHttpResponse) =
        generateDummyMono()
            .map {
                response.statusCode = HttpStatus.OK
            }
    private fun generateDummyMono() =
        Mono.just("Dummy Mono")
}