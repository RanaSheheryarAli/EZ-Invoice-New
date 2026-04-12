const express = require('express');
const BusinessController = require('../controllers/businessController');
const upload = require('../middlewares/uploadLogo');
const router = express.Router();

router.post('/upload-logo', upload.single('logo'), BusinessController.uploadLogo); // ✅ new route

router.post('/add-business', BusinessController.createBusiness);
router.get('/get-business/:userId', BusinessController.getBusiness);
router.delete('/delete-business/:userId', BusinessController.deleteBusiness);
router.put('/update-business/:userId', BusinessController.updateBusiness);

module.exports = router;
