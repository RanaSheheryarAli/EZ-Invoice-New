const express = require("express");
const router = express.Router();
const { createProduct, getProductsBySubcategory, getProductById, updateProduct, deleteProduct, getProductsByBusiness } = require("../controllers/productController");

router.post("/create-product", createProduct);
router.get("/get-products/:subcategoryId", getProductsBySubcategory);
router.get("/get-product/:productId", getProductById);
router.put("/update-product/:productId", updateProduct);
router.delete("/delete-product/:productId", deleteProduct);

// ✅ New route for fetching all products by business
router.get("/get-products-by-business/:businessId", getProductsByBusiness);

module.exports = router;
