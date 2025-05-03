// routes/invoiceRoutes.js
const express = require('express');
const router = express.Router();
const { createInvoice, getTotalSalesStats, getTopSellingProducts, getSalesGraphData,getTodayInvoiceCount, getInvoicesByBusinessId } = require('../controllers/invoiceController');

router.post('/create-invoice', createInvoice);
router.get('/stats/:businessId', getTotalSalesStats);
router.get('/top-products/:businessId', getTopSellingProducts);
router.get('/sales-graph/:businessId', getSalesGraphData);

router.get('/count-today/:businessId', getTodayInvoiceCount);

router.get('/all/:businessId', getInvoicesByBusinessId);


module.exports = router;
