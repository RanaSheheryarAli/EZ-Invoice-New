const express = require("express");
const router = express.Router();
const { createProduct, getProductsBySubcategory, getProductById, updateProduct, deleteProduct } = require("../controllers/productController");

// ✅ Create a new product
router.post("/create-product", createProduct);

// ✅ Get all products by subcategory ID
router.get("/get-products/:subcategoryId", getProductsBySubcategory);

// ✅ Get a single product by ID
router.get("/get-product/:productId", getProductById);

// ✅ Update a product by ID
router.put("/update-product/:productId", updateProduct);

// ✅ Delete a product by ID
router.delete("/delete-product/:productId", deleteProduct);

module.exports = router;
