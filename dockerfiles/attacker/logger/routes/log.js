var express = require('express');
const fs = require("fs");
var router = express.Router();

const path = '/';

/* GET users listing. */
router.get('/', function (req, res, next) {
    const content = req.query.msg;
    res.status(200).send("Enregistrement de " + content);
    console.log(content);
    fs.appendFile('logs/log.log', content + "\n", err => {
        if (err) {
            console.error(err)
            return
        }
        console.log("c'est enregistré");
    })
});

module.exports = router;