// models/Customer.js
const mongoose = require('mongoose');

const CustomerSchema = new mongoose.Schema({
    businessId: { type: mongoose.Schema.Types.ObjectId, ref: 'Business', required: true },
    name: { type: String, required: true },
    phone: { type: String, required: true },
    invoices: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Invoice' }],
}, { timestamps: true });

module.exports = mongoose.model('Customer', CustomerSchema);