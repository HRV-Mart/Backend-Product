package com.hrv.mart.product.service

import com.hrv.mart.product.model.Product
import com.hrv.mart.product.repository.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ProductService (
    @Autowired
    private val productRepository: ProductRepository
)
