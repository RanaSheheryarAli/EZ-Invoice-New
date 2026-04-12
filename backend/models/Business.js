const mongoose = require('mongoose');

const BusinessSchema = new mongoose.Schema({
    userId: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
    name: { type: String, required: true },
    ownername: { type: String, required: true },
    gstin: { type: String, required: true },
    address: { type: String, required: true },
    email: { type: String, required: true },
    contact: { type: String, required: true },
    website: { type: String, required: true },
    country: { type: String, required: true },
    currency: { type: String, required: true },
    numberformate: { type: String, required: true },
    dateformate: { type: String, required: true },
    signature: { type: String, required: true },
    logoUrl: { type: String }, // ✅ NEW FIELD
    categoryIds: [{ type: mongoose.Schema.Types.ObjectId, ref: 'Category' }],
    createdAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model('Business', BusinessSchema);
