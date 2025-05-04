// routes/customerRoutes.js
const express = require('express');
const router = express.Router();
const { getTopCustomers,findOrCreateCustomerAPI, getCustomersByBusiness } = require('../controllers/customerController');

router.get('/top-customers/:businessId', getTopCustomers);
router.post('/find-or-create', findOrCreateCustomerAPI);

router.get('/all-customer-by-business/:businessId', getCustomersByBusiness);

module.exports = router;
