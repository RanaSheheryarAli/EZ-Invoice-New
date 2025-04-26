
// controllers/categoryController.js
const Category = require('../models/Category');
const Business =require('../models/Business');
const mongoose = require('mongoose');

const createCategory = async (req, res) => {
    try {
        const { businessId, name } = req.body;

        // Validate input
        if (!businessId || !name) {
            return res.status(400).json({ error: "Business ID and category name are required" });
        }

        // Check if the business exists
        const business = await Business.findById(businessId);
        if (!business) {
            return res.status(404).json({ error: "Business not found" });
        }

        // Create a new category
        const newCategory = new Category({ businessId, name });
        await newCategory.save();

        // Update the Business document to include this new category
        await Business.findByIdAndUpdate(
            businessId,
            { $push: { categoryIds: newCategory._id } },  // Add category ID to Business
            { new: true }
        );

        res.status(201).json({ message: "Category created and linked to business", category: newCategory });
    } catch (error) {
        console.error(error);
        res.status(500).json({ error: "Server error" });
    }
};


const getallCategory = async (req, res) => {
    try {
        const { businessId } = req.params; // Extract businessId from request params

        // Validate if businessId is provided
        if (!businessId) {
            return res.status(400).json({ error: "Business ID is required" });
        }

        // Fetch categories associated with the given businessId, including subcategories
        const categories = await Category.find({ businessId }).populate("subcategories");

        // Check if categories exist
        if (!categories || categories.length === 0) {
            return res.status(404).json({ error: "No categories found for this business" });
        }

        res.status(200).json(categories);
    } catch (error) {
        console.error(error);
        res.status(500).json({ error: "Server error" });
    }
};

const getCategory = async (req, res) => {

        try {
            const { categoryId } = req.params;
    
            if (!categoryId) {
                return res.status(400).json({ error: "Category ID is required" });
            }
    
            const category = await Category.findById(categoryId).populate("subcategories");
    
            if (!category) {
                return res.status(404).json({ error: "Category not found" });
            }
    
            res.status(200).json(category);
        }  catch (error) {
        console.error("Error fetching subcategory:", error); // Detailed error logging
        res.status(500).json({ error: "Internal Server Error" });
    }


};

const updateCategory = async (req, res) => {
    try {
        const { categoryId } = req.params;
        const { name } = req.body;

        if (!mongoose.Types.ObjectId.isValid(categoryId)) {
            return res.status(400).json({ error: "Invalid category ID format" });
        }

        const updatedCategory = await Category.findByIdAndUpdate(
            categoryId,
            { name },
            { new: true }
        );

        if (!updatedCategory) {
            return res.status(404).json({ error: "Category not found" });
        }

        res.status(200).json({ message: "Category updated successfully", updatedCategory });
    } catch (error) {
        console.error("Error updating category:", error);
        res.status(500).json({ error: "Internal Server Error" });
    }
};

// ✅ Delete a category by ID
const deleteCategory = async (req, res) => {
    try {
        const { categoryId } = req.params;

        if (!mongoose.Types.ObjectId.isValid(categoryId)) {
            return res.status(400).json({ error: "Invalid category ID format" });
        }

        const category = await Category.findByIdAndDelete(categoryId);
        if (!category) {
            return res.status(404).json({ error: "Category not found" });
        }

        // Remove category reference from Business
        await Business.findByIdAndUpdate(category.businessId, {
            $pull: { categoryIds: categoryId }
        });

        res.status(200).json({ message: "Category deleted successfully" });
    } catch (error) {
        console.error("Error deleting category:", error);
        res.status(500).json({ error: "Internal Server Error" });
    }
};

module.exports = {
    createCategory,
    getallCategory,
    getCategory,
    updateCategory,
    deleteCategory
};