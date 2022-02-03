var express = require('express');
const fs = require("fs");
var router = express.Router();

const path = '/';

/* GET users listing. */
router.post('/', function (req, res, next) {
    // console.log(req.body)

    const content = Object.keys(req.body)[0].replace("\\n", "\n");


    res.status(200).send("Enregistrement de " + content);
    console.log(content);
    fs.appendFile('logs/log.log', content + "\n\n\n", err => {
        if (err) {
            console.error(err)
            return
        }
        console.log("c'est enregistré");
    })
});

module.exports = router;