const express = require("express");
const router = express.Router();

const {
  login,
  loginCustomer,
  registerCustomer,
} = require("../controllers/authController");

router.post("/", login);
router.post("/customer", loginCustomer);
router.post("/register", registerCustomer);
router.post("/register-customer", registerCustomer);

module.exports = router;
