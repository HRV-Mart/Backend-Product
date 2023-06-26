package com.hrv.mart.product.service

import com.hrv.mart.custompageable.Pageable
import com.hrv.mart.product.model.Product
import com.hrv.mart.product.repository.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.switchIfEmpty

@Service
class ProductService(
    @Autowired
    private val productRepository: ProductRepository
) {
    fun createProduct(product: Product) =
        productRepository.insert(product.setIdToDefault())
    fun updateProduct(product: Product, response: ServerHttpResponse) =
        isProductExist(productId = product.id)
            .flatMap { isExist ->
                if (isExist) {
                    productRepository.save(product)
                        .map {
                            setHTTPOkCode(response)
                        }
                        .then(Mono.just("Product Updated Successfully"))
                } else {
                    setHTTPNotfoundCode(response)
                        .then(Mono.just("Product Not Found"))
                }
            }
    fun getProductFromId(productId: String, response: ServerHttpResponse) =
        productRepository.findById(productId)
            .flatMap {product ->
                setHTTPOkCode(response)
                    .then(Mono.just(product))
            }
            .switchIfEmpty {
                setHTTPNotfoundCode(response)
                    .then(Mono.empty())
            }
    fun getAllProduct(pageRequest: PageRequest) =
        productRepository.findProductsByNameNotNull(pageRequest)
            .collectList()
            .flatMap {products ->
                val count = productRepository.countProductByNameNotNull()
                count.map {totalSize ->
                    Pageable<Product>(
                        data = products,
                        nextPage = Pageable.getNextPage(
                            pageSize = pageRequest.pageSize.toLong(),
                            page = pageRequest.pageNumber.toLong(),
                            totalSize = totalSize
                        ),
                        size = pageRequest.pageSize.toLong()
                    )
                }
            }
    fun deleteProduct(productId: String, response: ServerHttpResponse) =
        isProductExist(productId)
            .flatMap { isExist ->
                if (isExist) {
                    productRepository.deleteById(productId)
                        .then(setHTTPOkCode(response))
                        .then(Mono.just("Product Deleted Successfully"))
                } else {
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
