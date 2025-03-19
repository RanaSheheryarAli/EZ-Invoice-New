const mongoose = require('mongoose');

const CategorySchema = new mongoose.Schema({
    businessId: { type: mongoose.Schema.Types.ObjectId, ref: 'Business', required: true },
    name: { type: String, required: true },
    subcategories: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Subcategory' }] // Array of subcategory IDs
});

module.exports = mongoose.model('Category', CategorySchema);
