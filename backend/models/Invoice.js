// models/Invoice.js
const mongoose = require('mongoose');

const InvoiceSchema = new mongoose.Schema({
    businessId: { type: mongoose.Schema.Types.ObjectId, ref: 'Business', required: true },
    customerId: { type: mongoose.Schema.Types.ObjectId, ref: 'Customer', required: true },
    products: [
        {
            productId: { type: mongoose.Schema.Types.ObjectId, ref: 'Product', required: true },
            quantity: { type: Number, required: true },
            price: { type: Number, required: true }
        }
    ],
    totalAmount: { type: Number, required: true },
    date: { type: Date, default: Date.now }
});

module.exports = mongoose.model('Invoice', InvoiceSchema);
