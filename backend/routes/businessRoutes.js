// routes/businessRoutes.js
const express = require('express');
const BusinessController = require('../controllers/businessController');
const router = express.Router();

router.post('/add-business', BusinessController.createBusiness);
router.get('/get-business/:userId', BusinessController.getBusiness);
router.delete('/delete-business/:userId', BusinessController.deleteBusiness);
router.put('/update-business/:userId', BusinessController.updateBusiness);

module.exports = router;