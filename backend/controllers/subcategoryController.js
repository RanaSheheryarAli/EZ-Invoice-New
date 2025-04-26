const Subcategory = require("../models/Subcategory");
const Category = require("../models/Category");

// Create a new Subcategory and link it to a Category
const createSubcategory = async (req, res) => {
    try {
        const { categoryId, name } = req.body;

        // Validate if categoryId exists
        const category = await Category.findById(categoryId);
        if (!category) {
            return res.status(404).json({ error: "Category not found" });
        }

        // Create new subcategory
        const newSubcategory = new Subcategory({ categoryId, name });
        await newSubcategory.save();

        // Update the category to include the subcategory
        category.subcategories.push(newSubcategory._id);
        await category.save();

        res.status(201).json({ message: "Subcategory created successfully", newSubcategory });
    } catch (error) {
        res.status(500).json({ error: "Server error" });
    }
};

// Get all subcategories for a specific category
const getSubcategoriesByCategory = async (req, res) => {
    try {
        const { categoryId } = req.params;

        const subcategories = await Subcategory.find({ categoryId }).populate("products");
        if (!subcategories.length) {
            return res.status(404).json({ error: "No subcategories found" });
        }

        res.status(200).json(subcategories);
    } catch (error) {
        res.status(500).json({ error: "Server error" });
    }
};

// Get a single subcategory by ID
const getSubcategoryById = async (req, res) => {
    try {
        const { subcategoryId } = req.params;

        const subcategory = await Subcategory.findById(subcategoryId).populate("products");
        if (!subcategory) {
            return res.status(404).json({ error: "Subcategory not found" });
        }

        res.status(200).json(subcategory);
    } 
    catch (error) {
        res.status(500).json({ error: "Server error ha " });
    }
};

// Update a subcategory
const updateSubcategory = async (req, res) => {
    try {
        const { subcategoryId } = req.params;
        const { name } = req.body;

        const updatedSubcategory = await Subcategory.findByIdAndUpdate(
            subcategoryId,
            { name },
            { new: true }
        );

        if (!updatedSubcategory) {
            return res.status(404).json({ error: "Subcategory not found" });
        }

        res.status(200).json({ message: "Subcategory updated successfully", updatedSubcategory });
    } catch (error) {
        res.status(500).json({ error: "Server error" });
    }
};

// Delete a subcategory
const deleteSubcategory = async (req, res) => {
    try {
        const { subcategoryId } = req.params;

        const deletedSubcategory = await Subcategory.findByIdAndDelete(subcategoryId);
        if (!deletedSubcategory) {
            return res.status(404).json({ error: "Subcategory not found" });
        }

        res.status(200).json({ message: "Subcategory deleted successfully" });
    } catch (error) {
        res.status(500).json({ error: "Server error" });
    }
};

module.exports = {
    createSubcategory,
    getSubcategoriesByCategory,
    getSubcategoryById,
    updateSubcategory,
    deleteSubcategory
};
