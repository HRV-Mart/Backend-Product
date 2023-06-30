package com.hrv.mart.product.repository

import com.hrv.mart.product.model.Product
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface MongoProductRepository : ReactiveMongoRepository<Product, String> {
    fun findProductsByNameNotNull(pageRequest: PageRequest): Flux<Product>
    fun countProductByNameNotNull(): Mono<Long>
}
