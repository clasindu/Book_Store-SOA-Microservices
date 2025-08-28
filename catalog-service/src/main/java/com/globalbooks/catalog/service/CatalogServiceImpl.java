package com.globalbooks.catalog.service;

import com.globalbooks.catalog.dao.ProductDAO;
import com.globalbooks.catalog.dao.ProductDAOImpl;
import com.globalbooks.catalog.exception.CatalogException;
import com.globalbooks.catalog.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jws.WebService;
import java.util.List;

@WebService(
    endpointInterface = "com.globalbooks.catalog.service.CatalogService",
    targetNamespace = "http://globalbooks.com/services/catalog/v1",
    serviceName = "CatalogService",
    portName = "CatalogServicePort"
)
public class CatalogServiceImpl implements CatalogService {

    private static final Logger logger = LoggerFactory.getLogger(CatalogServiceImpl.class);
    private final ProductDAO productDAO;

    public CatalogServiceImpl() {
        this.productDAO = new ProductDAOImpl();
        logger.info("CatalogService initialized successfully");
    }

    @Override
    public Product getProductById(String productId) throws CatalogException {
        logger.info("Getting product by ID: {}", productId);
        
        if (productId == null || productId.trim().isEmpty()) {
            throw new CatalogException("INVALID_INPUT", "Product ID cannot be null or empty");
        }
        
        try {
            Product product = productDAO.findById(productId);
            if (product == null) {
                throw new CatalogException("PRODUCT_NOT_FOUND", 
                    "Product with ID " + productId + " not found");
            }
            logger.info("Found product: {}", product.getTitle());
            return product;
        } catch (CatalogException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error getting product by ID: {}", productId, e);
            throw new CatalogException("DATABASE_ERROR", "Failed to retrieve product", e);
        }
    }

    @Override
    public List<Product> searchProducts(SearchCriteria criteria) throws CatalogException {
        logger.info("Searching products with criteria");
        
        if (criteria == null) {
            throw new CatalogException("INVALID_INPUT", "Search criteria cannot be null");
        }
        
        try {
            List<Product> products = productDAO.search(criteria);
            logger.info("Found {} products", products.size());
            return products;
        } catch (Exception e) {
            logger.error("Error searching products", e);
            throw new CatalogException("DATABASE_ERROR", "Failed to search products", e);
        }
    }

    @Override
    public PriceQuote getProductPrice(String productId, int quantity) throws CatalogException {
        logger.info("Getting price quote for product: {}, quantity: {}", productId, quantity);
        
        if (productId == null || productId.trim().isEmpty()) {
            throw new CatalogException("INVALID_INPUT", "Product ID cannot be null or empty");
        }
        
        if (quantity <= 0) {
            throw new CatalogException("INVALID_INPUT", "Quantity must be greater than zero");
        }
        
        try {
            Product product = productDAO.findById(productId);
            if (product == null) {
                throw new CatalogException("PRODUCT_NOT_FOUND", 
                    "Product with ID " + productId + " not found");
            }
            
            PriceQuote quote = new PriceQuote(productId, product.getPrice(), quantity);
            logger.info("Generated price quote: total={}", quote.getTotal());
            return quote;
        } catch (CatalogException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error generating price quote", e);
            throw new CatalogException("CALCULATION_ERROR", "Failed to calculate price", e);
        }
    }

    @Override
    public InventoryStatus checkInventory(String productId) throws CatalogException {
        logger.info("Checking inventory for product: {}", productId);
        
        if (productId == null || productId.trim().isEmpty()) {
            throw new CatalogException("INVALID_INPUT", "Product ID cannot be null or empty");
        }
        
        try {
            InventoryStatus status = productDAO.getInventoryStatus(productId);
            if (status == null) {
                throw new CatalogException("PRODUCT_NOT_FOUND", 
                    "Product with ID " + productId + " not found");
            }
            return status;
        } catch (CatalogException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error checking inventory", e);
            throw new CatalogException("DATABASE_ERROR", "Failed to check inventory", e);
        }
    }

    @Override
    public boolean updateInventory(String productId, int quantity, String operation) 
            throws CatalogException {
        logger.info("Updating inventory for product: {}, quantity: {}, operation: {}", 
            productId, quantity, operation);
        
        if (productId == null || productId.trim().isEmpty()) {
            throw new CatalogException("INVALID_INPUT", "Product ID cannot be null or empty");
        }
        
        if (quantity <= 0) {
            throw new CatalogException("INVALID_INPUT", "Quantity must be greater than zero");
        }
        
        if (operation == null || 
            (!operation.equals("RESERVE") && !operation.equals("RELEASE") && !operation.equals("DEDUCT"))) {
            throw new CatalogException("INVALID_INPUT", 
                "Operation must be RESERVE, RELEASE, or DEDUCT");
        }
        
        try {
            boolean result = productDAO.updateInventory(productId, quantity, operation);
            if (!result) {
                throw new CatalogException("UPDATE_FAILED", 
                    "Failed to update inventory for product " + productId);
            }
            logger.info("Inventory updated successfully");
            return true;
        } catch (CatalogException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error updating inventory", e);
            throw new CatalogException("DATABASE_ERROR", "Failed to update inventory", e);
        }
    }
}