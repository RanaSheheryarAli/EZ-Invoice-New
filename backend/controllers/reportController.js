const Invoice = require('../models/Invoice');
const Product = require('../models/Product');
const mongoose = require('mongoose');

// Summary report including total invoices, sales, expenses, profit/loss, and graph

const ObjectId = mongoose.Types.ObjectId;

const getReportSummary = async (req, res) => {
    try {
        const { businessId, startDate, endDate } = req.query;

        if (!businessId || !startDate || !endDate) {
            return res.status(400).json({ error: "businessId, startDate, and endDate are required" });
        }

        const start = new Date(startDate);
        const end = new Date(endDate);
        end.setHours(23, 59, 59, 999);

        // Just use as string directly
        const invoices = await Invoice.find({
            businessId,
            date: { $gte: start, $lte: end }
        });

        const totalInvoices = invoices.length;
        const totalSales = invoices.reduce((sum, inv) => sum + inv.totalAmount, 0);

        let totalExpense = 0;
        for (const invoice of invoices) {
            for (const item of invoice.products) {
                const product = await Product.findById(item.productId);
                if (product && product.purchaceprice) {
                    totalExpense += product.purchaceprice * item.quantity;
                }
            }
        }

        const profitOrLoss = totalSales - totalExpense;

        const salesByDay = {};
        invoices.forEach(inv => {
            const day = inv.date.toISOString().split('T')[0];
            salesByDay[day] = (salesByDay[day] || 0) + inv.totalAmount;
        });

        const salesGraph = Object.keys(salesByDay).map(date => ({
            date,
            amount: salesByDay[date]
        }));

        res.json({
            totalInvoices,
            totalSales,
            totalExpense,
            profitOrLoss,
            salesGraph
        });

    } catch (err) {
        console.error("Error in report summary:", err);
        res.status(500).json({ error: "Internal server error" });
    }
};


module.exports = {
    getReportSummary
};
