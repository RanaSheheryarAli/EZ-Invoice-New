const Product = require("../models/Product");
const Subcategory = require("../models/Subcategory");
const mongoose = require("mongoose");

// ✅ Create a new product and add it to the subcategory
const createProduct = async (req, res) => {
    try {
        const { subcategoryId, name, barcode, saleprice, salepriceDiscount, purchaceprice, Texes, stock, date, minstock, itemlocation } = req.body;

        // Validate subcategoryId format
        if (!mongoose.Types.ObjectId.isValid(subcategoryId)) {
            return res.status(400).json({ error: "Invalid subcategory ID format" });
        }

        // Check if subcategory exists
        const subcategory = await Subcategory.findById(subcategoryId);
        if (!subcategory) {
            return res.status(404).json({ error: "Subcategory not found" });
        }

        // Create new product
        const newProduct = new Product({
            subcategoryId,
            name,
            barcode,
            saleprice,
            salepriceDiscount,
            purchaceprice,
            Texes,
            stock,
            date,
            minstock,
            itemlocation
        });

        // Save product to database
        await newProduct.save();

        // Add product reference to subcategory
        subcategory.products.push(newProduct._id);
        await subcategory.save();

        res.status(201).json({ message: "Product created successfully", product: newProduct });

    } catch (error) {
        console.error("Error creating product:", error);
        res.status(500).json({ error: "Internal Server Error" });
    }
};

// ✅ Get all products by subcategory ID
const getProductsBySubcategory = async (req, res) => {
    try {
        const { subcategoryId } = req.params;

        // Validate subcategoryId
        if (!mongoose.Types.ObjectId.isValid(subcategoryId)) {
            return res.status(400).json({ error: "Invalid subcategory ID format" });
        }

        // Find products by subcategory
        const products = await Product.find({ subcategoryId });

        if (!products.length) {
            return res.status(404).json({ error: "No products found for this subcategory" });
        }

        res.status(200).json(products);
    } catch (error) {
        console.error("Error fetching products:", error);
        res.status(500).json({ error: "Internal Server Error" });
    }
};

// ✅ Get a single product by ID
const getProductById = async (req, res) => {
    try {
        const { productId } = req.params;

        if (!mongoose.Types.ObjectId.isValid(productId)) {
            return res.status(400).json({ error: "Invalid product ID format" });
        }

        const product = await Product.findById(productId);
        if (!product) {
            return res.status(404).json({ error: "Product not found" });
        }

        res.status(200).json(product);
    } catch (error) {
        console.error("Error fetching product:", error);
        res.status(500).json({ error: "Internal Server Error" });
    }
};

// ✅ Update a product by ID
const updateProduct = async (req, res) => {
    try {
        const { productId } = req.params;

        if (!mongoose.Types.ObjectId.isValid(productId)) {
            return res.status(400).json({ error: "Invalid product ID format" });
        }

        const updatedProduct = await Product.findByIdAndUpdate(productId, req.body, { new: true });
        if (!updatedProduct) {
            return res.status(404).json({ error: "Product not found" });
        }

        res.status(200).json({ message: "Product updated successfully", updatedProduct });
    } catch (error) {
        console.error("Error updating product:", error);
        res.status(500).json({ error: "Internal Server Error" });
    }
};

// ✅ Delete a product by ID and remove it from subcategory
const deleteProduct = async (req, res) => {
    try {
        const { productId } = req.params;

        if (!mongoose.Types.ObjectId.isValid(productId)) {
            return res.status(400).json({ error: "Invalid product ID format" });
        }

        const product = await Product.findByIdAndDelete(productId);
        if (!product) {
            return res.status(404).json({ error: "Product not found" });
        }

        // Remove product reference from subcategory
        await Subcategory.findByIdAndUpdate(product.subcategoryId, { 
            $pull: { products: product._id } 
        });

        res.status(200).json({ message: "Product deleted successfully" });
    } catch (error) {
        console.error("Error deleting product:", error);
        res.status(500).json({ error: "Internal Server Error" });
    }
};

module.exports = { createProduct, getProductsBySubcategory, getProductById, updateProduct, deleteProduct };
