package com.product.service.controller;

import com.product.service.dto.ProductRequest;
import com.product.service.dto.ProductResponse;
import com.product.service.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/api/products")
@Tag(name = "Products", description = "API para gestionar productos")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Obtiene los detalles de un producto específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<ProductResponse> getProductById(
            @Parameter(description = "ID del producto") @PathVariable @Positive(message = "El id debe ser mayor a 0") Long id) {
        var response = productService.getProductById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping()
    @Operation(summary = "Obtener todos los productos", description = "Obtiene la lista de todos los productos registrados")
    @ApiResponse(responseCode = "200", description = "Lista de productos")
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        var response = productService.getAllProducts();
        return ResponseEntity.ok(response);
    }

    @PostMapping()
    @Operation(summary = "Crear nuevo producto", description = "Crea un nuevo producto con los datos proporcionados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos")
    })
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        var response = productService.createProduct(productRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Editar producto", description = "Edita un producto existente con los datos proporcionados")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto editado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<ProductResponse> updateProduct(@Parameter(description = "ID del producto a editar")
                                                         @PathVariable @Positive(message = "El id debe ser mayor a 0") Long id,
                                                         @Valid @RequestBody ProductRequest productRequest) {
        var response = productService.updateProduct(id, productRequest);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar producto", description = "Elimina un producto por su ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<Void> deleteProductById(
            @Parameter(description = "ID del producto a eliminar")
            @PathVariable @Positive(message = "El id debe ser mayor a 0") Long id){
        productService.deleteProductById(id);
        return ResponseEntity.noContent().build();
    }

}