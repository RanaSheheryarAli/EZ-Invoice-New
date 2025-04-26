const express = require("express");
const router = express.Router();
const {
    createSubcategory,
    getSubcategoriesByCategory,
    getSubcategoryById,
    updateSubcategory,
    deleteSubcategory
} = require("../controllers/subcategoryController");

// Create a new subcategory
router.post("/add-subcategory", createSubcategory);

// Get all subcategories for a specific category
router.get("/category/:categoryId", getSubcategoriesByCategory);

// Get a single subcategory by ID
router.get("/get-subcategory/:subcategoryId", getSubcategoryById);

// Update a subcategory
router.put("/:subcategoryId", updateSubcategory);

// Delete a subcategory
router.delete("/:subcategoryId", deleteSubcategory);

module.exports = router;
