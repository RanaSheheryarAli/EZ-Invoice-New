
// routes/categoryRoutes.js

const express = require('express');
const { createCategory, getallCategory ,getCategory ,  updateCategory,deleteCategory} = require('../controllers/categoryController');

const router = express.Router();

router.post('/add-catagory', createCategory );
router.get('/getall-catagory/:businessId', getallCategory );
router.get('/get-catagory/:categoryId', getCategory );
router.put('/update-catagory/:categoryId', updateCategory );
router.delete('/delete-catagory/:categoryId', deleteCategory );

module.exports = router;
