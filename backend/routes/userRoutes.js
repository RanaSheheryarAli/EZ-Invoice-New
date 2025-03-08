const express = require('express');
const router = express.Router();
const UserController = require('../controllers/userController');

// Signup Route
router.post('/signup', UserController.signup);

// Signin (Login) Route
router.post('/signin', UserController.signin);

module.exports = router;
