const mongoose = require('mongoose');

const ProductSchema = new mongoose.Schema({
    businessId: { type: mongoose.Schema.Types.ObjectId, ref: 'Business', required: true }, // ✅ Add this line
    categoryId: { type: mongoose.Schema.Types.ObjectId, ref: 'Category', required: true },
    subcategoryId: { type: mongoose.Schema.Types.ObjectId, ref: 'Subcategory', required: true },
    name: { type: String, required: true },
    barcode: { type: String, required: true },
    saleprice: { type: Number, required: true },
    salepriceDiscount: { type: Number, required: true },
    purchaceprice: { type: Number, required: true },
    Texes: { type: Number, required: true },
    stock: { type: Number, required: true },
    date: { type: Date, required: true },
    minstock: { type: Number, required: true },
    itemlocation: { type: String, required: true }
});

module.exports = mongoose.model('Product', ProductSchema);
