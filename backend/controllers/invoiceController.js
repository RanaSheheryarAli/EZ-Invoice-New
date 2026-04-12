// controllers/invoiceController.js
const Invoice = require('../models/Invoice');
const Customer = require('../models/Customer');
const Product = require('../models/Product');
const { findOrCreateCustomer } = require('./customerController');

const createInvoice = async (req, res) => {
    try {
        const { businessId, customerName, customerPhone, products, totalAmount } = req.body;

        const customer = await findOrCreateCustomer(businessId, customerName, customerPhone);

        // Update stock for each product (no stock check)
        for (const item of products) {
            const product = await Product.findById(item.productId);
            if (!product) {
                return res.status(404).json({ error: `Product not found: ${item.productId}` });
            }
            product.stock -= item.quantity;
            await product.save();
        }

        const invoice = new Invoice({
            businessId,
            customerId: customer._id,
            products,
            totalAmount
        });
        await invoice.save();

        customer.invoices.push(invoice._id);
        await customer.save();

        res.status(201).json({ message: 'Invoice created', invoice });
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Internal server error' });
    }
};

const getTodayInvoiceCount = async (req, res) => {
    try {
        const { businessId } = req.params;
        const today = new Date();
        today.setHours(0, 0, 0, 0);

        const count = await Invoice.countDocuments({
            businessId,
            date: { $gte: today }
        });

        res.status(200).json({ count });
    } catch (err) {
        res.status(500).json({ error: "Failed to get invoice count" });
    }
};


const getTotalSalesStats = async (req, res) => {
    try {
        const { businessId } = req.params;
        const invoices = await Invoice.find({ businessId });

        const totalInvoices = invoices.length;
        const totalSales = invoices.reduce((sum, inv) => sum + inv.totalAmount, 0);

        res.status(200).json({ totalInvoices, totalSales });
    } catch (err) {
        res.status(500).json({ error: 'Internal server error' });
    }
};


const getTopSellingProducts = async (req, res) => {
    try {
        const { businessId } = req.params;
        const invoices = await Invoice.find({ businessId });

        const productStats = {};

        for (const invoice of invoices) {
            for (const item of invoice.products) {
                const { productId, quantity, price } = item;

                if (!productStats[productId]) {
                    productStats[productId] = { quantity: 0, totalSales: 0 };
                }

                productStats[productId].quantity += quantity;
                productStats[productId].totalSales += price * quantity;
            }
        }

        // Fetch product names
        const detailedStats = await Promise.all(Object.entries(productStats).map(async ([productId, data]) => {
            const product = await Product.findById(productId);
            return {
                productName: product ? product.name : 'Unknown',
                productId,
                quantitySold: data.quantity,
                totalSales: data.totalSales
            };
        }));

        // Sort by quantitySold descending
        detailedStats.sort((a, b) => b.quantitySold - a.quantitySold);

        res.status(200).json(detailedStats);
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Internal server error' });
    }
};


const getSalesGraphData = async (req, res) => {
    try {
        const { businessId } = req.params;
        const invoices = await Invoice.find({ businessId });

        const dailySales = {};
        invoices.forEach(inv => {
            const date = inv.date.toISOString().split('T')[0];
            if (!dailySales[date]) dailySales[date] = 0;
            dailySales[date] += inv.totalAmount;
        });

        res.status(200).json(dailySales);
    } catch (err) {
        res.status(500).json({ error: 'Internal server error' });
    }
};


const getInvoicesByBusinessId = async (req, res) => {
    try {
        const { businessId } = req.params;
        const invoices = await Invoice.find({ businessId })
            .sort({ date: -1 }) // 🔁 Sort by date descending (most recent first)
            .populate('customerId', 'name phone') // Optional: populate customer info
            .populate('products.productId', 'name'); // Optional: populate product info

        res.status(200).json(invoices);
    } catch (err) {
        console.error(err);
        res.status(500).json({ error: 'Failed to fetch invoices' });
    }
};



module.exports = { createInvoice, getTotalSalesStats, getTopSellingProducts, getSalesGraphData,getTodayInvoiceCount,getInvoicesByBusinessId };
