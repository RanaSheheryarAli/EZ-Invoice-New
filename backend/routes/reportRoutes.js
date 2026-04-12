const express = require('express');
const { getReportSummary } = require('../controllers/reportController');
const router = express.Router();

// GET /api/reports/summary?businessId=...&startDate=YYYY-MM-DD&endDate=YYYY-MM-DD
router.get('/summary', getReportSummary);

module.exports = router;
