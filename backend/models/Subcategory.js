const mongoose = require('mongoose');

const SubcategorySchema = new mongoose.Schema({
    categoryId: { type: mongoose.Schema.Types.ObjectId, ref: 'Category', required: true },
    name: { type: String, required: true },
    products: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Product' }] // Reference to Product model
});

module.exports = mongoose.model('Subcategory', SubcategorySchema);
