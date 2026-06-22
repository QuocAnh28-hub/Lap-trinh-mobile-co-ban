const express = require("express");
const router = express.Router();

const {
  getRooms,
  getRoomCalendar,
  getAvailableRoomsAdvanced,
  addRoom,
  updateRoom,
  cleanRoom,
} = require("../controllers/roomController");

router.get("/calendar", getRoomCalendar);
router.get("/available", getAvailableRoomsAdvanced);
router.get("/", getRooms);
router.post("/", addRoom);
router.post("/:id/clean", cleanRoom);
router.put("/:id", updateRoom);

module.exports = router;
